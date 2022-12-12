package com.example.madimo_games.ordenamiento;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madimo_games.R;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;

public class TercerNivel extends AppCompatActivity {
    Intent in;
    Bundle recibido, b;
    int puntaje;
    boolean isOn;
    private TextView crono, txtPuntaje;
    Thread cronos;
    int mili=0, seg=0, minutos=0;
    Handler h=new Handler();
    MediaPlayer sonidomoneda;
    MediaPlayer musica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercer_nivel);

        b = new Bundle();
        in = new Intent(this,cuartonivel.class);
        isOn = true;
        TextView puntajeactual = findViewById(R.id.txtPuntajeActual);
        txtPuntaje = findViewById(R.id.txt_Puntaje3);
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
        final ArrayList numeros = new ArrayList();

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
        final Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);

        final TextView texto = (TextView)findViewById(R.id.texto);

        for (final Button bt:listado) {
            int num = (int) (Math.random() * 12) + 1;
            numeros.add(num);
            bt.setText(num + "");
            bt.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    sonidomoneda();
                    vibrator.vibrate(1000);
                    texto.setText(texto.getText() + " " + bt.getText());
                    bt.setVisibility(View.INVISIBLE);
                    puntaje += 100;
                    txtPuntaje.setText("Puntaje acumulado: "+ puntaje);
                }
            });
        }

        Button auto =(Button)findViewById(R.id.autocompletar);

        auto.setVisibility(View.INVISIBLE);


        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(numeros);
                if(numeros.equals(numeros)) {

                    Intent intent = new Intent (v.getContext(),cuartonivel.class);
                    startActivityForResult(intent, 0);
                    startActivity(intent);
                    finish();
                }else{

                    finish();
                    startActivity(getIntent());

                }


            }
        });

        TextView numOrdenados;
        numOrdenados = (TextView)findViewById(R.id.numerosordenados);
        Button mostrar=findViewById(R.id.mostrar);
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(numeros,Collections.reverseOrder());
                for (Object num: numeros){
                    auto.setVisibility(View.VISIBLE);
                    numOrdenados.setText(numOrdenados.getText().toString()+(int)num+" - ");
                }

            }
        });


        Button validar2 =(Button)findViewById(R.id.btnValidar);

        validar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarContenido(texto, numeros);}
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

    public void validarContenido(TextView texto, ArrayList numeros){
        Collections.sort(numeros,Collections.reverseOrder());
        String cadena="";
        for (Object num: numeros){
            cadena+=(int)num+"";
        }
        String cadena2 = texto.getText().toString().replaceAll(" ","");
        String mensaje;
        if(cadena.equals(cadena2)) {
            int score = puntaje - ((minutos/59)+(seg));
            enviarPuntaje(b, score, minutos, seg, mili, in);
        }else{

            mensaje="fail";
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
        musica.release();
        startActivity(in);
    }

}