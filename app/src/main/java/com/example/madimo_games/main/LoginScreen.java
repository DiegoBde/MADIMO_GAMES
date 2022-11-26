package com.example.madimo_games.main;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    private VideoView vvGalactic;
    public TextView txtRegister;
    EditText emailLogin, passwordLogin;
    Button btnLogin;
    String correo = "", pass = "";
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Iniciar Sesión");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        emailLogin = findViewById(R.id.tb_email);
        passwordLogin = findViewById(R.id.tb_pass);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = emailLogin.getText().toString();
                pass = passwordLogin.getText().toString();
                if (emailLogin.getText().toString().isEmpty()){
                    emailLogin.setError("Campo Obligatorio");
                    emailLogin.requestFocus();
                }
                else if(passwordLogin.getText().toString().isEmpty()){
                    passwordLogin.setError("Campo Obligatorio");
                    passwordLogin.requestFocus();
                }
                if((emailLogin.getText()+"")!="" && (passwordLogin.getText()+"")!=""){
                    loginUser();
                }
            }
        });

        Intent inRegister = new Intent(this, RegisterScreen.class);
        txtRegister=(TextView) findViewById(R.id.txt_register);
        clickTxtRegister(inRegister);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        vvGalactic = findViewById(R.id.vv_fondoLogin); //obtenemos el objeto VideoView
        vvGalactic.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.fondogalactic)); //le asignamos el video
        reproducirVideo();

    }

    public void loginUser(){
        auth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginScreen.this, ProfileScreen.class));
                    Toast.makeText(LoginScreen.this, "Bien", Toast.LENGTH_SHORT).show();
                    //finish();
                }else{
                    Toast.makeText(LoginScreen.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void reproducirVideo(){
        vvGalactic.start(); //inicia Video
        vvGalactic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);
            }
        });
    }
    public void clickTxtRegister(Intent in){
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
            reproducirVideo();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                       // | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}