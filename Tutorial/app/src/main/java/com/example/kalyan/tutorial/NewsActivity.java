package com.example.kalyan.tutorial;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kalyan.tutorial.Adapter.NewsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private ImageView imgView;
    RecyclerView newsView;
    NewsAdapter newsAdapter;
    ArrayList<News> newsList = new ArrayList<News>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsView = (RecyclerView) findViewById(R.id.news_recycler_view);
        newsAdapter = new NewsAdapter(this,newsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        newsView.setLayoutManager(mLayoutManager);
        newsView.setItemAnimator(new DefaultItemAnimator());
        prepareNotesData();
        newsView.setAdapter(newsAdapter);
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
        newsTask newsTask = new newsTask();
        newsTask.execute();
    }

    private class newsTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = Constant.NEWS_URL;
            String jsonNews = Utility.getResponceForUrl(url);
            return jsonNews;
        }

        @Override
        protected void onPostExecute(String rawNews) {
            if (rawNews != null) {
                getMainContentNews(rawNews);
                super.onPostExecute(rawNews);
            }else {
                Toast.makeText(getApplicationContext(),"There is some problem",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getMainContentNews(String jsonNews){
        try {
            JSONObject root = new JSONObject(jsonNews);
            JSONArray newsArray = root.getJSONArray("articles");
            for(int i= 0;i<newsArray.length();i++){
                JSONObject currentObject = newsArray.getJSONObject(i);
                String title = currentObject.getString("title");
                String desc = currentObject.getString("description");
                String ImgUrl = currentObject.getString("urlToImage");
                String url = currentObject.getString("url");

                News currentNews = new News(title,desc,url,ImgUrl);
                Log.e("CurrentNews",currentNews.getTitle()+" : "+currentNews.getDesc());
                newsList.add(currentNews);
            }
            newsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
