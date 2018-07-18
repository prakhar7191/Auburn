package com.example.kalyan.tutorial.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kalyan.tutorial.News;
import com.example.kalyan.tutorial.Notes;
import com.example.kalyan.tutorial.R;
import com.example.kalyan.tutorial.WebNews;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by KALYAN on 27-01-2018.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<News> newsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView desc_tv,title_tv;
        ImageView imgView;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            imgView = (ImageView) view.findViewById(R.id.news_image);
            desc_tv = (TextView) view.findViewById(R.id.news_desc);
            title_tv = (TextView)view.findViewById(R.id.news_title);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context,WebNews.class);
            intent.putExtra("url",newsList.get(position).getUrl());
            context.startActivity(intent);
        }
    }


    public NewsAdapter(Context context, List<News> newsList)
    {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        News news = newsList.get(position);
        holder.desc_tv.setText(news.getDesc());
        holder.title_tv.setText(news.getTitle());
        Picasso.with(context)
                .load(news.getUrlImg()).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}


