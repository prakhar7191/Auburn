package com.example.kalyan.tutorial;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    TextView overView_tv, maxTemp_tv, minTemp_tv, dayDesc_tv, nightDesc_tv;
    ImageView dayImage_iv, nightImage_iv;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        String url = Constant.CITYCODE_URL + "allahabad";
        overView_tv = (TextView) findViewById(R.id.over_view_text);
        maxTemp_tv = (TextView) findViewById(R.id.max_temp);
        minTemp_tv = (TextView) findViewById(R.id.min_temp);
        dayDesc_tv = (TextView) findViewById(R.id.day_desc);
        nightDesc_tv = (TextView) findViewById(R.id.night_desc);

        dayImage_iv = (ImageView) findViewById(R.id.day_image);
        nightImage_iv = (ImageView) findViewById(R.id.night_image);

        getLocationCodeTask myTask = new getLocationCodeTask();
        myTask.execute(url);
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

    private class getLocationCodeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return Utility.getResponceForUrl(urls[0]);
        }

        @Override
        protected void onPostExecute(String jsonResponce) {
            String locationCode = Utility.getLocationCode(jsonResponce);

            String mainWeatherContentUrl = Constant.CONTENT_URL + locationCode + Constant.API_KEY;
            getMainContentTask mainContentTask = new getMainContentTask();
            mainContentTask.execute(mainWeatherContentUrl);
        }
    }

    private class getMainContentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return Utility.getResponceForUrl(urls[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            String mainContent = Utility.getMainContentDataForWeather(s);
            String[] mainContentParts = mainContent.split("/");
            overView_tv.setText(mainContentParts[0]);

            minTemp_tv.setText(String.format("%.2f", Utility.convertFToC(Float.parseFloat(mainContentParts[1]))) + "*C");
            maxTemp_tv.setText(String.format("%.2f", Utility.convertFToC(Float.parseFloat(mainContentParts[2]))) + "*C");
            dayDesc_tv.setText(mainContentParts[4]);
            nightDesc_tv.setText(mainContentParts[6]);
            if (mainContentParts[3].length() <= 1)
                mainContentParts[3] = "0" + mainContentParts[3];
            if (mainContentParts[5].length() <= 1)
                mainContentParts[5] = "0" + mainContentParts[3];
            String dayImageUrl = Constant.ICON_URI + mainContentParts[3] + Constant.ICON_NAME_IN_URI;
            Picasso.with(getApplicationContext())
                    .load(dayImageUrl).into(dayImage_iv);
            String nightImageUrl = Constant.ICON_URI + mainContentParts[5] + Constant.ICON_NAME_IN_URI;
            Picasso.with(getApplicationContext())
                    .load(nightImageUrl).into(nightImage_iv);
            speakIt(mainContentParts[0]);
        }

        private void speakIt(String toSpeak){
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
