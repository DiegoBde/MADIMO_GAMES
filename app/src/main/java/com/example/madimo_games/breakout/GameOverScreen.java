package com.example.madimo_games.breakout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madimo_games.R;
import com.example.madimo_games.main.AltosPuntajes;
import com.example.madimo_games.main.Constants;
import com.example.madimo_games.main.MainScreen;
import com.example.madimo_games.main.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GameOverScreen extends AppCompatActivity {
    private String gano;
    private int puntaje;
    private String puntajeRecord, id, nomJuego;
    private Bundle recibido;
    private Score puntuacion = new Score();
    private FirebaseAuth auth;
    private DatabaseReference dataBase;
    private TextView txtBestScore, txtNewScore;
    private ImageView ivGameOver;
    private ImageButton btnHome, btnRetry, btnScores;
    private MediaPlayer gameOverMusic;
    private Intent inRetry, inMain, inScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        inRetry = new Intent(this, MainBreakOut.class); // CAMBIAR dependiendo del juego
        inMain = new Intent(this, MainScreen.class);
        inScores = new Intent(this, AltosPuntajes.class);

        recibido = this.getIntent().getExtras();
        puntaje = recibido.getInt("puntaje");
        nomJuego = recibido.getString("nomJuego");
        try {
            gano = recibido.getString("gano");
        }catch (Exception e){

        }
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        id = auth.getCurrentUser().getUid();

        txtBestScore = findViewById(R.id.txt_mejorPuntuacion);
        txtNewScore = findViewById(R.id.txt_nuevaPuntuacion);
        btnHome = findViewById(R.id.ib_homeGameOver);
        btnRetry = findViewById(R.id.ib_retryGameOver);
        btnScores = findViewById(R.id.ib_scoreGameOver);
        ivGameOver = findViewById(R.id.iv_GameOver);
        try{
            if(gano != null){
                ivGameOver.setImageResource(R.drawable.win);
                gameOverMusic = MediaPlayer.create(this,R.raw.victory);
            }else{
                gameOverMusic = MediaPlayer.create(this,R.raw.gameover);
            }
        }catch (Exception e){

        }

        updateScore(); //verifica si el puntaje actual es mayor al record personal
        setButtons(); //settea los botones
        try {
            getUserInfo(); //obtiene ambos puntajes del usuario [nuevo y record]
        }catch (Exception e){

        }

    }
    private void setButtons(){
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(inMain);
                finish();
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(inRetry);
                finish();
            }
        });
        btnScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle;
                bundle = new Bundle();
                bundle.putString("numJuego", nomJuego);
                inScores.putExtra("numJuego", nomJuego);
                startActivity(inScores);
                finish();
            }
        });
    }

    private void updateScore(){
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                puntajeRecord = snapshot.child(nomJuego).getValue().toString();
                puntuacion.nuevoRecord(puntaje,Integer.parseInt(puntajeRecord),dataBase,id, nomJuego);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo(){
        String id= auth.getCurrentUser().getUid();
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtBestScore.setText(dataSnapshot.child(nomJuego).getValue().toString());
                    txtNewScore.setText(puntaje+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            gameOverMusic.start();
        }
    }




}