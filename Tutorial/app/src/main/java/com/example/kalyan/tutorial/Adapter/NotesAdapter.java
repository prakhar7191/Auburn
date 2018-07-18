package com.example.kalyan.tutorial.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kalyan.tutorial.Notes;
import com.example.kalyan.tutorial.R;

import java.util.List;

/**
 * Created by KALYAN on 25-01-2018.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private List<Notes> notesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name_tv,subject_tv;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name_tv = (TextView) view.findViewById(R.id.single_name);
            subject_tv = (TextView) view.findViewById(R.id.single_subject);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }


    public NotesAdapter(Context context, List<Notes> notesList)
    {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_notes_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Notes note = notesList.get(position);
        holder.name_tv.setText(note.getName());
        holder.subject_tv.setText(note.getSubject());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}

