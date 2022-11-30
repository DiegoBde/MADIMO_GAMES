package com.example.madimo_games.main;

import androidx.annotation.NonNull;
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
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.example.madimo_games.breakout.MainBreakOut;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainScreen extends AppCompatActivity {
    Boolean estaLogueado = false, mute = false;
    TextView txtLogin;
    private ImageView ivPerfil, ivMusic;
    private VideoView vvBg;
    public ImageButton btnLogin, btnPlay, btnLeaderBoard, btnPerfil;
    MediaPlayer backgroundMusic;
    FirebaseAuth auth;
    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();

        Intent inLogin = new Intent(this, LoginScreen.class);
        Intent inPerfil = new Intent(this, ProfileScreen.class);
        Intent inJugar = new Intent(this, FragmentActivity.class);
        Intent inScore = new Intent(this, AltosPuntajes.class);

        ivPerfil = findViewById(R.id.iv_perfil);
        ivMusic = findViewById(R.id.ibtn_sound);
        btnLogin = findViewById(R.id.ibtn_login);
        btnLeaderBoard = findViewById(R.id.ibtn_score);
        btnPlay = findViewById(R.id.ibtn_jugar);
        txtLogin = findViewById(R.id.txt_login);

        //ClickBotonLogin(inLogin);
        ClickBotonMusic();
        ClickBotonJugar(inJugar);
        ClickBotonPuntajes(inScore);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundMusic.stop();
                if(estaLogueado){
                    startActivity(inPerfil);
                }else{
                    startActivity(inLogin);
                }


            }
        });
        try {
            getUserInfo();
        }catch (Exception e){

        }

        //==================================================================================
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        //==================================================================================
        // Configurar fondo en movimiento

        vvBg = findViewById(R.id.vv_fondoMain); //obtenemos el objeto VideoView
        vvBg.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.backgroundgame)); //le asignamos el video
        reproducirVideo();

    }

    private void ClickBotonPuntajes(Intent in) {
        btnLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundMusic.stop();
                startActivity(in);

            }
        });
    }

    public void ClickBotonJugar(Intent in){
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundMusic.stop();
                startActivity(in);

            }
        });
    }

    public void ClickBotonMusic(){
        ivMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //backgroundMusic.stop();
                if(!mute){
                    backgroundMusic.setVolume(0,0);
                    Picasso.get().load(R.drawable.musicoffbutton).into(ivMusic);
                    mute = true;

                }else{
                    mute = false;
                    backgroundMusic.setVolume(1,1);
                    Picasso.get().load(R.drawable.musiconbutton).into(ivMusic);
                }

            }
        });
    }
    public void reproducirVideo(){
        vvBg.start(); //inicia Video
        vvBg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);

            }
        });
    }

    private void getUserInfo(){
        String id = auth.getCurrentUser().getUid();
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        txtLogin.setText("Bienvenido "+dataSnapshot.child("nick").getValue().toString());
                        String imagen = dataSnapshot.child("imagen").getValue().toString();
                        Picasso.get().load(imagen).into(ivPerfil);
                        estaLogueado = true;
                    }catch (Exception e){

                    }

                }else{
                    estaLogueado = false;
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
            reproducirVideo();
            backgroundMusic = MediaPlayer.create(this, R.raw.backgroud_music);
            if(mute){
                backgroundMusic.setVolume(0,0);
            }else{
                backgroundMusic.setVolume(1,1);
            }
            backgroundMusic.start();
            try {
                getUserInfo();
            }catch (Exception e){

            }
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