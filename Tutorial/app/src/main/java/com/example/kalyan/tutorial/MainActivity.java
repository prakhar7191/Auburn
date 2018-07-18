package com.example.kalyan.tutorial;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kalyan.tutorial.Adapter.NotesAdapter;
import com.squareup.picasso.Picasso;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    final String TAG = "tag";

    EditText searchEdit;
    TextToSpeech textToSpeech;
    ArrayList<Contact> contactList = new ArrayList<Contact>();

    ImageView microphone_iv;
    AudioManager audioManager;

    Socket socket;
    String server_ip = "192.168.43.142";
    DataOutputStream dos;
    DataInputStream dis;
    boolean isSocketConnected=false;
    String currQuery="";

    @Override
    protected void onStart() {
        VideoView videoView = (VideoView) findViewById(R.id.backgroundVideo);
        //videoView.setRotation(90f);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.back);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        super.onStart();

    }

    public void initSocket() {
        Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(server_ip,5077);
                    dos = new DataOutputStream(socket.getOutputStream());
                    dis = new DataInputStream(socket.getInputStream());
                    isSocketConnected=true;
                    Log.e("initSocket","Connected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        socketThread.start();
    }

    public void sendSocketQuery(final String query) {
        Log.e("startSendQuery",query);
        if (!isSocketConnected) {
            Log.e("notConnSendSocket",query);
            return;
        }
        Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currQuery=query;
                    dos.writeUTF(query);
                    dos.flush();
                    Log.e("queryInSendSocketQuery",query);
                    getSocketQuery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        socketThread.start();
    }

    public void getSocketQuery() {
        Log.e("getSocketQuery","atfirst");
        Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = dis.readUTF();
                    Log.e("dataInGetSocketQuery",data);
                    if ( data == null || data.equals("failed to load results")) {
                        // wikipedia search
                        wikiOutputString(currQuery);
                    } else {
                        speakIt(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        socketThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        searchEdit = (EditText) findViewById(R.id.search);
        microphone_iv = (ImageView) findViewById(R.id.microphone_image);
        microphone_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchStr = searchEdit.getText().toString().trim();
                if(!TextUtils.isEmpty(searchStr)) {
                    doSomethingUsingResult(searchStr);
                }else{
                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak buddy");
                    startActivityForResult(i, 100);
                }
                searchEdit.setText("");
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 101);
            }
            return;
        }
        updateContacts();

        Log.e("contactList", contactList.toString());
    }


    private void updateContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        Log.e("contactList in show", contactList.toString());
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new Contact(name, phNumber));
        }
        cursor.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = results.get(0);

            doSomethingUsingResult(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doSomethingUsingResult(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        result = result.toLowerCase();

        if(result.contains("note")&& !result.contains("show")){
            showNoteDialog();
        }
        if(result.contains("note")&&result.contains("show")){
            Intent intent = new Intent(MainActivity.this,NotesActivity.class);
            startActivity(intent);
        }
        else if (result.contains("search for")) {
            wikiOutputString(result.substring(result.indexOf("search for") + "search for".length()));
        } else if (result.contains("call")) {
            callNumber(result.substring(result.indexOf("call") + "call".length()));
        }else if(result.contains("weather")){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
        }else if(result.contains("mail")){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }else if(result.contains("open")){
            openApp(result.substring(result.indexOf("open") + "open".length()).trim().toLowerCase());
        }else if(result.contains("event")){
            Intent intent = new Intent(getApplicationContext(),EventActivity.class);
            startActivity(intent);
        }else if(result.contains("news")){
            Intent intent = new Intent(MainActivity.this,NewsActivity.class);
            startActivity(intent);
        }else if(result.contains("increase") && result.contains("volume")){
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }else if(result.contains("decrease") && result.contains("volume")){
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }else if(result.contains("alarm")){
            showTimeDialog();
        }else if(result.contains("play") && result.contains("music")){
            openApp("music");
        }else if(result.contains("play") && result.contains("video")){
            openApp("video");
        }else if(result.contains("set ip")){
            showIpDialog();
        }
        else{
            sendSocketQuery(result);
        }
    }

    private void showIpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.notes_popout, null);
        dialogBuilder.setView(dialogView);

        final EditText dialog_note= (EditText) dialogView.findViewById(R.id.dialog_note);

        dialogBuilder.setTitle("Write here");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String ip = dialog_note.getText().toString().trim();
                if(!TextUtils.isEmpty(ip)){
                   server_ip = ip;
                   initSocket();
                    Toast.makeText(getApplicationContext(),"saved" +ip ,Toast.LENGTH_SHORT).show();
                }else {
                    speakIt("some error occured");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showNoteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.notes_popout, null);
        dialogBuilder.setView(dialogView);

       final EditText dialog_note= (EditText) dialogView.findViewById(R.id.dialog_note);

        dialogBuilder.setTitle("Write here");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String note = dialog_note.getText().toString().trim();
                if(!TextUtils.isEmpty(note)){
                    makeNote(note);
                    Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_SHORT).show();
                }else {
                    speakIt("some error occured");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private void callNumber(String callingName) {
        Toast.makeText(getApplicationContext(),callingName.toLowerCase(),Toast.LENGTH_SHORT).show();
        for (int i = 0; i < contactList.size(); i++) {
            callingName=callingName.trim();//replaceAll(" ","");

            Log.e(contactList.get(i).getName().toLowerCase(),"name");
            if (contactList.get(i).getName().toLowerCase().contains(callingName.toLowerCase())) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contactList.get(i).getNumber()));
                startActivity(callIntent);break;
            }
        }
    }

    private void wikiOutputString(String serarchFor) {
        wikiTask wikiTask = new wikiTask();
        wikiTask.execute(serarchFor);
    }


    private class wikiTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String url = Constant.WIKI_API_URL+strings[0]+ Constant.WIKI_FORMAT;
            String jsonWiki = Utility.getResponceForUrl(url);
            String toSpeak = Utility.getMainContentFromWiki(jsonWiki);
            return toSpeak;
        }

        @Override
        protected void onPostExecute(String toSpeak) {
            Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
            speakIt(toSpeak);
            super.onPostExecute(toSpeak);
        }
    }

    private void speakIt(String toSpeak){
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void makeNote(String subject) {

        SQLiteDatabase myNotesDatabase = openOrCreateDatabase(Constant.NOTES_DATABASE_NAME, MODE_PRIVATE, null);
        myNotesDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "Notes(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,subject TEXT NOT NULL);");

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        ContentValues values = new ContentValues();

        values.put(Constant.NOTES_COLUMN_NAME,timeStamp);
        values.put(Constant.NOTES_COLUMN_SUBJECT,subject);

        myNotesDatabase.insert(Constant.DATABASE_TABLE_NAME,null,values);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    private class socketClass extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            String gotData = readData();
            return gotData;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.e("socket",s);
            super.onPostExecute(s);
        }
    }
    private String readData() {
        try {
            Socket s = new Socket("192.168.1.217",5077);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str=dis.readUTF();
            return str;
        }
        catch (Exception e) {

        }
        return null;
    }

    private void showTimeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogtime, null);
        dialogBuilder.setView(dialogView);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);

        dialogBuilder.setTitle("Select Time");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                setAlarm(hour,min);
                speakIt("Alarm is set for "+ hour +" O'clock and "+min+"minutes");
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

        private void setAlarm(int hours, int min) {
            Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
            openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openClockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openClockIntent.putExtra(AlarmClock.EXTRA_HOUR, hours);
            openClockIntent.putExtra(AlarmClock.EXTRA_MINUTES, min);
            startActivity(openClockIntent);
        }

        private void openApp(String requiredApp) {
            String[] requiredPackage = null;
            int count = 0;
            ArrayList<String> allRequiredPackages = new ArrayList<String>();
            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo packageInfo : packages) {
                String currentPackage = packageInfo.packageName;
                Log.e(TAG, "Installed package :" + currentPackage);
                if (currentPackage.contains(requiredApp.toLowerCase())) {
                    count++;
                    allRequiredPackages.add(currentPackage);
                    break;
                }
            }

            if(allRequiredPackages.size() > 0){
                launchApp(allRequiredPackages.get(0));
                speakIt("here you go");
            }else{
                Toast.makeText(getApplicationContext(),"Sorry,I can not find such application",Toast.LENGTH_SHORT).show();
                speakIt("Sorry,I can not find such application");
            }
        }

        private void launchApp(String packageName) {
            Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (mIntent != null) {
                try {
                    startActivity(mIntent);
                } catch (ActivityNotFoundException err) {
                    Toast t = Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }

    }

