package com.example.madimo_games.breakout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.madimo_games.R;
import com.example.madimo_games.main.Constants;
import com.example.madimo_games.main.LeaderBoardScreen;
import com.example.madimo_games.main.MainScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOverScreen extends AppCompatActivity {
    int puntaje;
    Bundle recibido;
    FirebaseAuth auth;
    DatabaseReference dataBase;
    TextView score, scoreNuevo;
    Button btnHome, btnRetry, btnScores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);
        Intent inRetry = new Intent(this, MainBreakOut.class);
        Intent inMain = new Intent(this, MainScreen.class);
        Intent inScores = new Intent(this, LeaderBoardScreen.class);


        recibido = this.getIntent().getExtras();
        puntaje = recibido.getInt("puntaje");

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        score = findViewById(R.id.txt_mejorPuntuacion);
        scoreNuevo = findViewById(R.id.txt_nuevaPuntuacion);
        btnHome = findViewById(R.id.btn_home);
        btnRetry = findViewById(R.id.btn_retry);
        btnScores = findViewById(R.id.btn_scores);


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
                startActivity(inScores);
                finish();
            }
        });
        getUserInfo();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;




    }

    private void getUserInfo(){
        String id= auth.getCurrentUser().getUid();
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    score.setText(dataSnapshot.child("score3").getValue().toString());
                    scoreNuevo.setText(puntaje+"");

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
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


}