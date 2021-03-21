package com.example.findmypastime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class GameList extends ArrayAdapter<Game> {
    private final Activity context;
    private final List<Game> gameList;

    public GameList(Activity context, List<Game> gameList) {
        super(context,R.layout.list_layout, gameList);
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        Game game = gameList.get(position);
        textViewName.setText(game.getGameName());

        return listViewItem;
    }
}
