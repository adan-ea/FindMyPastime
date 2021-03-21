package com.example.findmypastime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameListActivity extends AppCompatActivity {

    public final String TAG = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";

    private DatabaseReference dbGames;
    private ListView listViewGames;
    private List<Game> gameList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        TextView txtGames = findViewById(R.id.jeux_textView);
        //Récupération du nombre de visages détéctés
        String inputData = this.getIntent().getExtras().getString("message");
        txtGames.setText("Jeux conseillés pour " + inputData + " personnes :");
        dbGames = FirebaseDatabase.getInstance().getReference("jeux/"+ inputData +"players");
        listViewGames = (ListView) findViewById(R.id.listViewGames);
        gameList = new ArrayList<>();



        //Bouton de retour
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent anotherActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(anotherActivity);
                finish();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        dbGames.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameList.clear();
                for(DataSnapshot gameSnapshot : dataSnapshot.getChildren()){
                    Game game = gameSnapshot.getValue(Game.class);

                    gameList.add(game);

                }
                GameList adapter = new GameList(GameListActivity.this, gameList);
                listViewGames.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}
