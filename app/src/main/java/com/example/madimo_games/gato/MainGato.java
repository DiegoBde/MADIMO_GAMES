package com.example.madimo_games.gato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.madimo_games.R;
import com.example.madimo_games.breakout.GameOverScreen;
import com.example.madimo_games.main.Score;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainGato extends AppCompatActivity {
    Intent in;
    Bundle b;
    String nomJuego = "score2";
    Score clasePuntaje = new Score();
    int score;

    private boolean isOn=false;
    TextView crono;
    Thread cronos;
    MediaPlayer mp;
    MediaPlayer fatality;
    MediaPlayer win;
    private int mili=0,seg=0,minutos=0;
    Handler h = new Handler();

    private final List<int[]> combList = new ArrayList<>();
    private final List<String> doneBoxes = new ArrayList<>();
    private int [] boxPos = {0,0,0,0,0,0,0,0,0};
    private int turno = 1;
    private int totalSelectedBoxes = 1;
    private LinearLayout lyj1,lyj2;
    private TextView nombre1,nombre2;
    private ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9;
    private String IdUnicoJugador = "0";

   // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://madimologin-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private boolean opponentFound = false;
    private String opponentUniqueId = "0";

    private String status = "matching";
    private String playerTurn = "";
    private String connectionId = "";

    ValueEventListener turnsEventListener, wonEventListener;
    private String [] boxesSelectedBy = {"","","","","","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gato);

        b = new Bundle();
        in = new Intent(this, GameOverScreen.class);

        mp = MediaPlayer.create(this,R.raw.botonesgato);
        fatality = MediaPlayer.create(this,R.raw.fatality);
        win = MediaPlayer.create(this,R.raw.win);
//        crono = (TextView) findViewById(R.id.crono);
        crono = (TextView) findViewById(R.id.crono1);

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

        final String getPlayerName = getIntent().getStringExtra("playerName");
        final String getNombre1 = getIntent().getStringExtra("jugador1");
        final String getNombre2 = getIntent().getStringExtra("jugador2");
//        nombre1.setText(getNombre1);
//        nombre2.setText(getNombre2);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Esperando al oponente");
        progressDialog.show();

        IdUnicoJugador = String.valueOf(System.currentTimeMillis());
        nombre1.setText(getPlayerName);
        databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!opponentFound){
                    if(snapshot.hasChildren()){
                        for(DataSnapshot connections : snapshot.getChildren()){
                            String conId = connections.getKey();
                            int getPlayersCount = (int)connections.getChildrenCount();
                            if(status.equals("waiting")){
                                if(getPlayersCount == 2){

                                    playerTurn = IdUnicoJugador;
                                    applyPlayerTurn(playerTurn);
                                    boolean playerFound = false;
                                    for(DataSnapshot players : connections.getChildren()){
                                        String getIdUnicoJugador = players.getKey();
                                        if(getIdUnicoJugador.equals(IdUnicoJugador)){
                                            playerFound = true;
                                        }
                                        else if(playerFound){
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            opponentUniqueId = players.getKey();
                                            nombre2.setText(getOpponentPlayerName);
                                            connectionId = conId;
                                            opponentFound = true;

                                            databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            if (progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                            databaseReference.child("connections").removeEventListener(this);

                                        }
                                    }

                                }
                            }
                            else{
                                if(getPlayersCount == 1){
                                    connections.child(IdUnicoJugador).child("player_name").getRef().setValue(getPlayerName);
                                    for(DataSnapshot players : connections.getChildren()){

                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        opponentUniqueId = players.getKey();

                                        playerTurn = opponentUniqueId;
                                        applyPlayerTurn(playerTurn);
                                        nombre2.setText(getOpponentName);
                                        connectionId = conId;
                                        opponentFound = true;

                                        databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                        databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                        if (progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        databaseReference.child("connections").removeEventListener(this);
                                        break;
                                    }
                                }
                            }
                        }

                        if(!opponentFound && !status.equals("waiting")){
                            String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                            snapshot.child(connectionUniqueId).child(IdUnicoJugador).child("player_name").getRef().setValue(getPlayerName);

                            status = "waiting";

                        }
                    }
                    else {

                        String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                        snapshot.child(connectionUniqueId).child(IdUnicoJugador).child("player_name").getRef().setValue(getPlayerName);

                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.getChildrenCount() == 2){
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));
                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);
                        if(!doneBoxes.contains(String.valueOf(getBoxPosition))){
                            doneBoxes.add(String.valueOf(getBoxPosition));
                            if(getBoxPosition == 1){
                                selectBox(image1, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 2){
                                selectBox(image2, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 3){
                                selectBox(image3, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 4){
                                selectBox(image4, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 5){
                                selectBox(image5, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 6){
                                selectBox(image6, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 7){
                                selectBox(image7, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 8){
                                selectBox(image8, getBoxPosition, getPlayerId);

                            }
                            else if(getBoxPosition == 9){
                                selectBox(image9, getBoxPosition, getPlayerId);

                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        wonEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("player_id")){
                    String getWinPlayerId = snapshot.child("player_id").getValue(String.class);
                    final dialogoGanador DialogoGanador;
                    if(getWinPlayerId.equals(IdUnicoJugador)){
                        //DialogoGanador = new dialogoGanador(MainGato.this, "Has ganado!"+"\n y tu tiempo fue "+crono.getText() + "puntaje: ");
                        isOn = false;
                        win.start();
                        minutos = minutos / 60;
                        mili = mili * 1000;
                        score = minutos + seg + mili;
                        DialogoGanador = new dialogoGanador(MainGato.this, "Has ganado!"+"\n y tu tiempo fue "+crono.getText() + "puntaje: "+ score);
                        juegoTerminado();
                    }
                    else{
                        DialogoGanador = new dialogoGanador(MainGato.this, "Tu oponente ha ganado :(");
                        fatality.start();
                        isOn = false;

                    }
                    DialogoGanador.setCancelable(false);
                    DialogoGanador.show();
                    databaseReference.child("turns").child(connectionId).removeEventListener(turnsEventListener);
                    databaseReference.child("won").child(connectionId).removeEventListener(wonEventListener);

                }
                   // juegoTerminado();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("1") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("1");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("2") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("2");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("3") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("3");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("4") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("4");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("5") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("5");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("6") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("6");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("7") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("7");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("8") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("8");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
                    mp.start();
                }
            }
        });
        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doneBoxes.contains("9") && playerTurn.equals(IdUnicoJugador)){
                    ((ImageView)v).setImageResource(R.drawable.cross_icon);
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("9");
                    databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(IdUnicoJugador);

                    playerTurn = opponentUniqueId;
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
    private void applyPlayerTurn(String IdUnicoJugador2){
        if(IdUnicoJugador2.equals(IdUnicoJugador)){
            lyj1.setBackgroundResource(R.drawable.round_back_blue_stroke);
            lyj2.setBackgroundResource(R.drawable.round_back_dark_blue);
            isOn = true;
        }else{
            lyj2.setBackgroundResource(R.drawable.round_back_blue_stroke);
            lyj1.setBackgroundResource(R.drawable.round_back_dark_blue);
            isOn = false;
        }

    }
    private void selectBox(ImageView imageView, int selectedBoxPosition, String selectedByPlayer){
        boxesSelectedBy[selectedBoxPosition - 1] = selectedByPlayer;
        if(selectedByPlayer.equals(IdUnicoJugador)){
            imageView.setImageResource(R.drawable.cross_icon);
            playerTurn = opponentUniqueId;
        }
        else{
            imageView.setImageResource(R.drawable.zero_icon);
            playerTurn = IdUnicoJugador;
        }
        applyPlayerTurn(playerTurn);

        if(checkPlayerWin(selectedByPlayer)){
            databaseReference.child("won").child(connectionId).child("player_id").setValue(selectedByPlayer);
        }
        if(doneBoxes.size() == 9){
            final  dialogoGanador DialogoGanador = new dialogoGanador(MainGato.this, "Es un empate");
            DialogoGanador.setCancelable(false);
            DialogoGanador.show();
            isOn = false;
        }
    }
    private boolean checkPlayerWin(String playerId){
        boolean isPlayerWon = false;
        for (int i = 0; i < combList.size(); i++) {
            final int [] combination = combList.get(i);
            if(boxesSelectedBy[combination[0]].equals(playerId)&&
                    boxesSelectedBy[combination[1]].equals(playerId)&&
                    boxesSelectedBy[combination[2]].equals(playerId)){
                isPlayerWon = true;
            }

        }
        return isPlayerWon;

    }
    private void juegoTerminado(){
        String gano = "si";
        b.putString("gano", gano);
        clasePuntaje.sendScore(b,score, nomJuego, in);
        startActivity(in);
    }
}