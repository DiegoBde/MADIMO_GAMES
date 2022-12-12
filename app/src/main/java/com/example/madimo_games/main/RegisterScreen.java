package com.example.madimo_games.main;


import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterScreen extends AppCompatActivity {

    private ExpandableListView expLV;
    private ExpLAdapter adapter;
    private ArrayList<String> listCategorias;
    private Map<String, ArrayList<String>> mapChild;
    private VideoView vvFondoRegister;
    String email, password, name, nick, pais, imagen = "", token = "", item;
    int score1 = 0;
    int score2 = 0;
    int score3 = 0;
    FirebaseAuth auth;
    DatabaseReference dataBase;
    Button btnRegister;
    EditText txtCorreo, txtPass, txtNombre, txtNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrarse");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        vvFondoRegister = findViewById(R.id.vv_fondoRegister); //obtenemos el objeto VideoView
        vvFondoRegister.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.fondogalactic)); //le asignamos el video
        reproducirVideo();

        expLV = findViewById(R.id.elv_lista);
        listCategorias = new ArrayList<>();
        mapChild = new HashMap<>();

        txtCorreo = (EditText)findViewById(R.id.tb_emailRegister);
        txtPass = (EditText)findViewById(R.id.tb_passRegister);
        txtNombre = (EditText)findViewById(R.id.tb_nameRegister);
        txtNick = (EditText)findViewById(R.id.tb_nickRegister);

        btnRegister = (Button)findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        ClickBotonRegister();
        cargarDatos();
        seleccionPais();
    }
    public void cargarDatos(){
        ArrayList<String> listPaises = new ArrayList<>();

        listPaises.add("Chile");
        listPaises.add("Argentina");
        listPaises.add("Peru");
        listPaises.add("Colombia");

        listCategorias.add("Pais");
        mapChild.put(listCategorias.get(0),listPaises);
        adapter = new ExpLAdapter(listCategorias, mapChild, this);
        expLV.setAdapter(adapter);
    }
    public void seleccionPais(){
        expLV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(RegisterScreen.this, (String)adapter.getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
                pais = (String)adapter.getChild(groupPosition, childPosition);
                return false;
            }
        });

        expLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                item = parent.getItemAtPosition(position).toString();

            }
        });
    }

    public void ClickBotonRegister(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtCorreo.getText().toString();
                password = txtPass.getText().toString();
                name = txtNombre.getText().toString();
                nick = txtNick.getText().toString();

                if (txtCorreo.getText().toString().isEmpty()){
                    txtCorreo.setError("Campo Obligatorio");
                    txtCorreo.requestFocus();
                }
                else if(txtPass.getText().toString().isEmpty()){
                    txtPass.setError("Campo Obligatorio");
                    txtPass.requestFocus();
                }
                if((txtCorreo.getText()+"")!="" && (txtPass.getText()+"")!=""){
                    registerUser();
                }

            }
        });{

        }
    }
    private void registerUser(){
        Intent in = new Intent(this, ProfileScreen.class);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map= new HashMap<>();
                    map.put("name", name);
                    map.put("nick", nick);
                    map.put("country", pais);
                    map.put("email", email);
                    map.put("pass", password);
                    map.put("token", token);
                    map.put("imagen", imagen);
                    map.put("score1", score1);
                    map.put("score2", score2);
                    map.put("score3", score3);
                    String id = auth.getCurrentUser().getUid();
                    dataBase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                startActivity(in);
                                finish();
                            }else{
                                Toast.makeText(RegisterScreen.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(RegisterScreen.this,"No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void reproducirVideo(){
        vvFondoRegister.start(); //inicia Video
        vvFondoRegister.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);
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