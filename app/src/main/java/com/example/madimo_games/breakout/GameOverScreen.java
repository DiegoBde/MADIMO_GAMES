package com.example.madimo_games.breakout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madimo_games.R;
import com.example.madimo_games.main.AltosPuntajes;
import com.example.madimo_games.main.MainScreen;
import com.example.madimo_games.main.PushNotificationSend;
import com.example.madimo_games.main.Score;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class GameOverScreen extends AppCompatActivity {
    FirebaseAuth auth;
    String token;
    DatabaseReference dataBase;
    String userToken;

    private String gano;
    private int puntaje;
    private String puntajeRecord, id, nomJuego;
    private Bundle recibido;
    private Score puntuacion = new Score();
    private TextView txtBestScore, txtNewScore;
    private ImageView ivGameOver;
    private ImageButton btnHome, btnRetry, btnScores;
    private MediaPlayer gameOverMusic;
    private Intent inRetry, inMain, inScores;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
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
                notificacion();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo(){
        id = auth.getCurrentUser().getUid();
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //puntajeCurrentUser = dataSnapshot.child(nomJuego).getValue().toString();
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

    private void notificacion(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        token = task.getResult();
                        System.out.println(token);
                        actualizarToken();
                        sendAutoMessage();
                        vibrator.vibrate(500);
                    }
                });

    }
    private void actualizarToken(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("token", token);
        dataBase.child("Users").child(auth.getCurrentUser().getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(GameOverScreen.this,"Info Actualizada", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(GameOverScreen.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void sendAutoMessage(){
        String title = "Has superado tu record!", message = "Tu nuevo record es "+puntaje+"!";

        dataBase.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userToken = dataSnapshot.child("token").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        PushNotificationSend.pushNotification(
                this,
                token,
                title,
                message
        );
    }
}