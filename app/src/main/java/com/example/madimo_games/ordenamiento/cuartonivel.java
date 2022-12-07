package com.example.madimo_games.ordenamiento;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madimo_games.R;
import com.example.madimo_games.breakout.GameOverScreen;
import com.example.madimo_games.main.Score;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;

public class cuartonivel extends AppCompatActivity {
    Intent in;
    Bundle recibido, b;
    Score clasePuntaje = new Score();
    String nomJuego = "score1";
    int puntaje;
    boolean isOn;
    TextView crono, txtPuntaje;
    Thread cronos;
    int mili=0, seg=0, minutos=0;
    Handler h=new Handler();
    MediaPlayer sonidomoneda;
    MediaPlayer musica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuartonivel);
        b = new Bundle();
        in = new Intent(this, GameOverScreen.class);
        isOn = true;
        TextView puntajeactual = findViewById(R.id.txtPuntajeActual);
        txtPuntaje = findViewById(R.id.txt_Puntaje4);
        musica = MediaPlayer.create(this,R.raw.menuordenamiento);
        musica.start();

        try{
            recibido = this.getIntent().getExtras();

            puntaje = recibido.getInt("score");
            mili = recibido.getInt("mili");
            seg = recibido.getInt("seg");
            minutos = recibido.getInt("min");

            puntajeactual.setText("Puntaje Actual: "+puntaje);

        }
        catch (Exception e)
        {}
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
        listado.add((Button) findViewById(R.id.btn13));
        listado.add((Button) findViewById(R.id.btn14));
        listado.add((Button) findViewById(R.id.btn15));
        listado.add((Button) findViewById(R.id.btn16));
        listado.add((Button) findViewById(R.id.btn17));
        listado.add((Button) findViewById(R.id.btn18));
        listado.add((Button) findViewById(R.id.btn19));
        listado.add((Button) findViewById(R.id.btn20));
        listado.add((Button) findViewById(R.id.btn21));
        listado.add((Button) findViewById(R.id.btn22));
        listado.add((Button) findViewById(R.id.btn23));
        listado.add((Button) findViewById(R.id.btn24));

        final TextView texto = (TextView)findViewById(R.id.texto);

        final ArrayList numeros = new ArrayList();

        for (final Button bt:listado) {
            int num = (int) (Math.random() * 24) + 1;
            numeros.add(num);
            bt.setText(num + "");
            bt.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    sonidomoneda();
                    texto.setText(texto.getText() + " " + bt.getText());
                    bt.setVisibility(View.INVISIBLE);
                    puntaje += 100;
                    txtPuntaje.setText("Puntaje acumulado: "+ puntaje);
                }
            });
        }

        Button validar2 =(Button)findViewById(R.id.btnValidar4);

        validar2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                validarContenido(texto, numeros);}
        });
        Button auto =(Button)findViewById(R.id.autocompletar);
        auto.setVisibility(View.INVISIBLE);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(numeros,Collections.reverseOrder());
                if(numeros.equals(numeros)) {

                    Intent intent = new Intent (v.getContext(),infoActivity.class);
                    startActivityForResult(intent, 0);
                    startActivity(intent);
                    finish();
                }else{

                    finish();
                    startActivity(getIntent());

                }


            }
        });

        crono = (TextView) findViewById(R.id.crono);

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
    public void sonidomoneda(){
        if (sonidomoneda==null){
            sonidomoneda=MediaPlayer.create(this,R.raw.coin);
        }
        sonidomoneda.start();

    }
    private void validarContenido(TextView texto, ArrayList numeros){
        Collections.sort(numeros,Collections.reverseOrder());
        String cadena="";
        for (Object num: numeros){
            cadena+=(int)num+"";
        }
        String cadena2 = texto.getText().toString().replaceAll(" ","");
        String mensaje;
        if(cadena.equals(cadena2)) {
            String gano = "si";
            int score = puntaje - ((minutos/59)+(seg));
            b.putString("gano", gano);
            clasePuntaje.sendScore(b,score, nomJuego, in);
            startActivity(in);
        }else{
            Toast.makeText(this, "Lose", Toast.LENGTH_SHORT).show();

        }

    }
}