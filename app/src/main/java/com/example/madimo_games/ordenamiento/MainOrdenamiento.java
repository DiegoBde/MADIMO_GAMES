package com.example.madimo_games.ordenamiento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.madimo_games.R;

import java.util.ArrayList;
import java.util.Collections;

public class MainOrdenamiento extends AppCompatActivity{
    private Bundle b;
    private Intent in;
    private boolean isOn;
    private Button btnValidar, btnAuto, btnMostrar;
    private TextView txtCrono, txtNumOrdenados, txtPuntaje, texto;
    private Thread cronos;
    private Handler h = new Handler();
    private final ArrayList numeros = new ArrayList();
    private MediaPlayer musica, sonidomoneda, sonidomoneda2;
    private int mili=0, seg=0, minutos=0, puntaje = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ordenamiento);

        b = new Bundle();
        in = new Intent(this,SegundoNivel.class);
        isOn = true;

        btnMostrar = findViewById(R.id.btn_mostrar1);
        btnValidar = findViewById(R.id.btn_Validar1);
        btnAuto = findViewById(R.id.btn_auto1);
        txtPuntaje = findViewById(R.id.txt_Puntaje1);
        txtNumOrdenados = findViewById(R.id.numerosordenados);
        txtCrono =  findViewById(R.id.txt_crono1);
        texto  = findViewById(R.id.txt_orden1);

        musica = MediaPlayer.create(this,R.raw.menuordenamiento);
        sonidomoneda = MediaPlayer.create(this,R.raw.coin);
        sonidomoneda2 = MediaPlayer.create(this,R.raw.coin);
        //musica.start();

        ArrayList<Button> listado = new ArrayList<Button>();

        listado.add((Button) findViewById(R.id.btn1));
        listado.add((Button) findViewById(R.id.btn2));
        listado.add((Button) findViewById(R.id.btn3));
        listado.add((Button) findViewById(R.id.btn4));
        listado.add((Button) findViewById(R.id.btn5));
        listado.add((Button) findViewById(R.id.btn6));
        listado.add((Button) findViewById(R.id.btn7));
        listado.add((Button) findViewById(R.id.btn8));
        listado.add((Button) findViewById(R.id.btn9));
        listado.add((Button) findViewById(R.id.btn10));
        listado.add((Button) findViewById(R.id.btn11));
        listado.add((Button) findViewById(R.id.btn12));

        for (final Button btn : listado) {
            int num = (int) (Math.random() * 12) + 1;
            numeros.add(num);
            btn.setText(num + "");
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    if(sonidomoneda.isPlaying()){
                        sonidomoneda2.start();
                    }else{
                        sonidomoneda.start();}

                    texto.setText(texto.getText() + " " + btn.getText());
                    btn.setVisibility(View.INVISIBLE);
                    puntaje += 100;
                    txtPuntaje.setText("Puntaje acumulado: "+ puntaje);

                }

            });

        }

        btnAuto.setVisibility(View.INVISIBLE);

        setButtons();

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
                            //puntaje++;
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
                                txtCrono.setText(mi + ":" + s+":" + m);
                            }
                        });
                    }
                }
            }

        }); //cronometro
        cronos.start();
    }

    private void validarContenido(TextView texto, ArrayList numeros){
        Collections.sort(numeros);
        String cadena="";
        for (Object num: numeros){
            cadena+=(int)num+"";
        }
        String cadena2 = texto.getText().toString().replaceAll(" ","");

        if(cadena.equals(cadena2)) {
            int score = puntaje - ((minutos/59)+(seg));
            enviarPuntaje(b, score, minutos, seg, mili, in);
        }else{
            musica.stop();
            finish();
            startActivity(getIntent());

        }

    }

    private void enviarPuntaje(Bundle b, int score, int min, int seg, int mili, Intent in){
        b.putInt("score", score);
        b.putInt("mili",mili);
        b.putInt("seg",seg);
        b.putInt("min",min);
        in.putExtras(b);
        musica.stop();
        startActivity(in);
    }

    private void setButtons(){
        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(numeros);
                if(numeros.equals(numeros)) {
                    Intent intent = new Intent (v.getContext(), SegundoNivel.class);
                    startActivityForResult(intent, 0);
                    startActivity(intent);
                    finish();
                }else{

                    finish();
                    startActivity(getIntent());

                }

                Intent intent = new Intent (v.getContext(), SegundoNivel.class);
                startActivityForResult(intent, 0);
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(numeros);
                for (Object num: numeros){
                    btnAuto.setVisibility(View.VISIBLE);
                    txtNumOrdenados.setText(txtNumOrdenados.getText().toString()+(int)num+" - ");
                }

            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarContenido(texto, numeros);}

        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            musica.start();
            musica.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) { //Musica en loop infinito
                    mp.setLooping(true);

                }
            });
        }
    }
}