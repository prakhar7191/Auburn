package com.example.kalyan.tutorial;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Calendar;

public class EventActivity extends AppCompatActivity implements View.OnClickListener {

    Long startDt,endDt;
    Calendar startCal;
    EditText title_et;
    TextView time_tv,date_tv;
    Activity thisActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        time_tv = (TextView) findViewById(R.id.event_time);
        date_tv = (TextView) findViewById(R.id.event_date);
        title_et = (EditText) findViewById(R.id.event_title);

        startCal = Calendar.getInstance();
        startDt = System.currentTimeMillis();
        endDt = System.currentTimeMillis()+3600*1000;

        thisActivity = this;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                                                Manifest.permission.WRITE_CALENDAR}, 101);
            }
            return;
        }

        time_tv.setOnClickListener(this);
        date_tv.setOnClickListener(this);
    }

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

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                addEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEvent() {
        startDt = System.currentTimeMillis();
        endDt = System.currentTimeMillis()+3600*1000;
        startCal = Calendar.getInstance();

        String title = title_et.getText().toString().trim();
        String time = time_tv.getText().toString().trim();
        String date = date_tv.getText().toString().trim();

        if(!TextUtils.isEmpty(title)&& !time.equals("Time") && !date.equals("Date")){
            startDt = startCal.getTimeInMillis();
            endDt = startDt+3600*1000;
            addToCalendar(EventActivity.this,title,startDt,endDt);
        }else {
            Toast.makeText(getApplicationContext(),"Fill the Fields properly",Toast.LENGTH_SHORT).show();
        }
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
                startCal.set(Calendar.HOUR,hour);
                startCal.set(Calendar.MINUTE,min);
                time_tv.setText(hour+"hrs "+min+"mins ");
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showDateDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogdate, null);
        dialogBuilder.setView(dialogView);

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();

        dialogBuilder.setTitle("Select Date");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                int date = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                month = month + 1;
                startCal.set(Calendar.DATE,date);
                startCal.set(Calendar.MONTH,month);
                startCal.set(Calendar.YEAR,year);
                date_tv.setText(date+"/"+month+"/"+year);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.event_date:
                showDateDialog();
                break;
            case R.id.event_time:
                showTimeDialog();
                break;
        }
    }

    private void addToCalendar(final Context ctx, final String title, final long dtstart, final long dtend) {
        final ContentResolver cr = ctx.getContentResolver();
        Cursor cursor ;
        if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
            cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"),
                    new String[]{  CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME }, null, null, null);
        else
            cursor = cr.query(Uri.parse("content://calendar/calendars"),
                    new String[]{  CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME }, null, null, null);
        if ( cursor.moveToFirst() ) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                Log.e("name",calNames[i]);
                cursor.moveToNext();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues cv = new ContentValues();
                    cv.put("calendar_id", calIds[which]);
                    cv.put("title", title);
                    cv.put("dtstart", System.currentTimeMillis() );
                    cv.put("hasAlarm", 1);
                    cv.put("dtend", System.currentTimeMillis() + 1800*1000);
                    cv.put(CalendarContract.Events.EVENT_TIMEZONE,1);

                    Uri newEvent ;
                    if ((Build.VERSION.SDK_INT) >= 8 )
                        newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
                    else
                        newEvent = cr.insert(Uri.parse("content://calendar/events"), cv);

                    if (newEvent != null) {
                        long id = Long.parseLong( newEvent.getLastPathSegment() );
                        ContentValues values = new ContentValues();
                        values.put( "event_id", id );
                        values.put( "method", 1 );
                        values.put( "minutes", 15 ); // 15 minutes
                        if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                            cr.insert( Uri.parse( "content://com.android.calendar/reminders" ), values );
                        else
                            cr.insert( Uri.parse( "content://calendar/reminders" ), values );
                    }
                    dialog.cancel();
                    thisActivity.finish();
                }

            });

            builder.create().show();
        }
        cursor.close();
    }
}

