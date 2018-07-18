
package com.example.kalyan.tutorial;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.VideoView;

import com.example.kalyan.tutorial.Adapter.NotesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {

    RecyclerView notesView;
    NotesAdapter notesAdapter;
    ArrayList<Notes> notesList = new ArrayList<Notes>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

         notesView = (RecyclerView) findViewById(R.id.notes_recycler_view);

         notesAdapter = new NotesAdapter(this,notesList);
         RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
         notesView.setLayoutManager(mLayoutManager);
         notesView.setItemAnimator(new DefaultItemAnimator());
         notesView.setAdapter(notesAdapter);

         prepareNotesData();
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

    private void prepareNotesData() {
        if(notesList != null)
            notesList.clear();
        String selectQuery = "SELECT  * FROM " + Constant.DATABASE_TABLE_NAME+";";
        Cursor mCursor = null;
        try {
            SQLiteDatabase myNotesDatabase = openOrCreateDatabase(Constant.NOTES_DATABASE_NAME, MODE_PRIVATE, null);
            mCursor = myNotesDatabase.rawQuery(selectQuery, null);
            Log.e("cursor",mCursor.toString());
        } catch (SQLiteException e) {

        }

        if(mCursor != null && mCursor.moveToFirst()){
            String name = mCursor.getString(mCursor.getColumnIndex(Constant.NOTES_COLUMN_NAME));
            String subject = mCursor.getString(mCursor.getColumnIndex(Constant.NOTES_COLUMN_SUBJECT));
            notesList.add(new Notes(name,subject));

            while (mCursor.moveToNext()){
                name = mCursor.getString(mCursor.getColumnIndex(Constant.NOTES_COLUMN_NAME));
                subject = mCursor.getString(mCursor.getColumnIndex(Constant.NOTES_COLUMN_SUBJECT));

                notesList.add(new Notes(name,subject));
            }
        }

        notesAdapter.notifyDataSetChanged();
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
}
