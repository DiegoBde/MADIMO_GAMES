package com.example.madimo_games.gato;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.madimo_games.R;
import com.example.madimo_games.main.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainGato extends AppCompatActivity {
    private boolean isOn=false;
    TextView crono;
    Thread cronos;
    MediaPlayer mp;
    MediaPlayer fatality;
    private int mili=0,seg=0,minutos=0;
    Handler h = new Handler();

    private final List<int[]> combList = new ArrayList<>();
    private int [] boxPos = {0,0,0,0,0,0,0,0,0};
    private int turno = 1;
    private int totalSelectedBoxes = 1;
    private LinearLayout lyj1,lyj2;
    private TextView nombre1,nombre2;
    private ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gato);


        //======= PANTALLA COMPLETA========================
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        //============================================================


        mp = MediaPlayer.create(this,R.raw.botonesgato);
        fatality = MediaPlayer.create(this,R.raw.fatality);
        crono = (TextView) findViewById(R.id.crono);

        nombre1 = findViewById(R.id.nombre1);
        nombre2 = findViewById(R.id.nombre2);

        lyj1 = findViewById(R.id.lyj1);
        lyj2 = findViewById(R.id.lyj2);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);

        combList.add(new int[]{0,1,2});
        combList.add(new int[]{3,4,5});
        combList.add(new int[]{6,7,8});
        combList.add(new int[]{0,3,6});
        combList.add(new int[]{1,4,7});
        combList.add(new int[]{2,5,8});
        combList.add(new int[]{2,4,6});
        combList.add(new int[]{0,4,8});

        final String getNombre1 = getIntent().getStringExtra("jugador1");
        final String getNombre2 = getIntent().getStringExtra("jugador2");
        nombre1.setText(getNombre1);
        nombre2.setText(getNombre2);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(0)){
                    realizarAccion((ImageView)view, 0);
                    mp.start();
                    crono.setText("00:00:000");
                    isOn = true;
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(1)){
                    realizarAccion((ImageView)view, 1);
                    mp.start();
                }
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(2)){
                    realizarAccion((ImageView)view, 2);
                    mp.start();
                }
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(3)){
                    realizarAccion((ImageView)view, 3);
                    mp.start();
                }
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(4)){
                    realizarAccion((ImageView)view, 4);
                    mp.start();
                }
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(5)){
                    realizarAccion((ImageView)view, 5);
                    mp.start();
                }
            }
        });
        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(6)){
                    realizarAccion((ImageView)view, 6);
                    mp.start();
                }
            }
        });
        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(7)){
                    realizarAccion((ImageView)view, 7);
                    mp.start();
                }
            }
        });
        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (seleccionable(8)){
                    realizarAccion((ImageView)view, 8);
                    mp.start();
                }
            }
        });
        cronos = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isOn) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mili++;
                        if (mili == 999) {
                            seg++;
                            mili = 0;
                        }
                        if (seg == 59) {
                            minutos++;
                            seg = 0;
                        }
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                String m = "", s = "", mi = "";
                                if (mili < 10) {
                                    m = "00" + mili;
                                } else if (mili < 100) {
                                    m = "0" + mili;
                                } else {
                                    m = "" + mili;
                                }
                                if (seg < 10) {
                                    s = "0" + seg;
                                } else {
                                    s = "" + seg;
                                }
                                if (minutos < 10) {
                                    mi = "0" + minutos;
                                } else {
                                    mi = "" + minutos;
                                }
                                crono.setText(mi + ":" + s+":" + m);
                            }
                        });
                    }
                }
            }

        });
        cronos.start();
    }
    private void realizarAccion (ImageView imageView, int cajaSelec){
        boxPos[cajaSelec] = turno;
        if (turno == 1){
            imageView.setImageResource(R.drawable.cross_icon);
            if(revisarGanador()){
                dialogoGanador dialogoGanador = new dialogoGanador(MainGato.this, nombre1.getText().toString() + " Ha ganado", MainGato.this);
                dialogoGanador.setCancelable(false);
                dialogoGanador.show();
                fatality.start();
                isOn = false;
            }
            else if(totalSelectedBoxes == 9){
                dialogoGanador dialogoGanador = new dialogoGanador(MainGato.this, " es un empate ", MainGato.this);
                dialogoGanador.setCancelable(false);
                dialogoGanador.show();
                fatality.start();
                isOn = false;

            }
            else{
                cambiarTurno(2);
                totalSelectedBoxes++;

            }
        }
        else{
            imageView.setImageResource(R.drawable.zero_icon);
            if (revisarGanador()){
                dialogoGanador dialogoGanador = new dialogoGanador(MainGato.this, nombre2.getText().toString() + " Ha ganado", MainGato.this);
                dialogoGanador.setCancelable(false);
                dialogoGanador.show();
                fatality.start();
                isOn = false;

            }
            else if(cajaSelec == 9){
                dialogoGanador dialogoGanador = new dialogoGanador(MainGato.this, " es un empate ", MainGato.this);
                dialogoGanador.setCancelable(false);
                dialogoGanador.show();
            }
            else cambiarTurno(1);
            totalSelectedBoxes++;

        }
    }

    private void cambiarTurno (int turnoActual){
        turno = turnoActual;
        if (turno == 1){
            lyj1.setBackgroundResource(R.drawable.round_back_blue_border);
            lyj2.setBackgroundResource(R.drawable.round_back_dark_blue);
        }
        else{
            lyj2.setBackgroundResource(R.drawable.round_back_blue_border);
            lyj1.setBackgroundResource(R.drawable.round_back_dark_blue);
        }
    }
    private boolean revisarGanador(){
        boolean respuesta = false;
        for(int i=0;i<combList.size();i++){
            final int [] combinacion = combList.get(i);
            if(boxPos[combinacion[0]] == turno && boxPos[combinacion[1]] == turno && boxPos[combinacion[2]] == turno){
                respuesta = true;
            }

        }
        return respuesta;
    }

    private boolean seleccionable (int boxPoss) {

        boolean respuesta = false;
        if (boxPos[boxPoss] == 0){
            respuesta = true;
        }
        return respuesta;
    }
    public void restartMatch(){
        boxPos = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        turno = 1;
        totalSelectedBoxes = 1;
        image1.setImageResource(R.drawable.transparent);
        image2.setImageResource(R.drawable.transparent);
        image3.setImageResource(R.drawable.transparent);
        image4.setImageResource(R.drawable.transparent);
        image5.setImageResource(R.drawable.transparent);
        image6.setImageResource(R.drawable.transparent);
        image7.setImageResource(R.drawable.transparent);
        image8.setImageResource(R.drawable.transparent);
        image9.setImageResource(R.drawable.transparent);
        crono.setText("00:00:000");
        mili=0;
        seg=0;
        minutos=0;

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();

        }
    } //Esconder UI cuando se cambia de foco

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    } //Esconder UI
}