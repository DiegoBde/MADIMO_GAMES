package com.example.madimo_games.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madimo_games.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class AltosPuntajes extends AppCompatActivity {
    String numJuego;
    String rival, rivalNick;
    String currentUserToken, currentUserNick;
    int currentUserPosition;
    Bundle recibido;
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerViewUsuarios;
    AdaptadorUsuario adaptadorUsuario;
    ArrayList<Usuario> usuarioList;
    FirebaseAuth auth;
    DatabaseReference dataBase;
    TextView txtNombreJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altos_puntajes);

        recibido = this.getIntent().getExtras();
        numJuego = recibido.getString("numJuego");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Puntajes");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        txtNombreJuego = findViewById(R.id.txt_juegoTabla);

        mLayoutManager.setReverseLayout(true); //ordenar al reves
        mLayoutManager.setStackFromEnd(true);
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(mLayoutManager);

        usuarioList = new ArrayList<>();

        dataBase.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    currentUserToken = dataSnapshot.child("token").getValue().toString();
                    currentUserNick = dataSnapshot.child("nick").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        obtenerTodosLosUsuarios(String.valueOf(numJuego));
    }

    private void obtenerTodosLosUsuarios(String juego) {
        switch (juego){
            case "score1":
                txtNombreJuego.setText("Ordenamix");
                break;
            case "score2":
                txtNombreJuego.setText("Gato");
                break;
            case "score3":
                txtNombreJuego.setText("BreakOut");
                break;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(juego).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Usuario usuario = ds.getValue(Usuario.class);

                    usuarioList.add(usuario);
                    switch (juego){
                        case "score1":
                            adaptadorUsuario = new AdaptadorUsuario(AltosPuntajes.this, usuarioList, 1);
                            break;
                        case "score2":
                            adaptadorUsuario = new AdaptadorUsuario(AltosPuntajes.this, usuarioList, 2);
                            break;
                        case "score3":
                            adaptadorUsuario = new AdaptadorUsuario(AltosPuntajes.this, usuarioList, 3);
                            break;
                    }
                        recyclerViewUsuarios.setAdapter(adaptadorUsuario);

                }
                for(int i=0; i<usuarioList.size()-1;i++){

                    if(usuarioList.get(i).getToken() == currentUserToken){
                        currentUserPosition = i-usuarioList.size();
                        if(currentUserPosition < 0){
                            currentUserPosition *= (-1);
                        }
                        if(i > 0){
                            rival = usuarioList.get(i-1).getToken();
                            rivalNick = "por encima de "+usuarioList.get(i-1).getNick();
                            String title = "Puntuacion Actualizada", message = currentUserNick+ "Te ha superado en el Ranking";
                            PushNotificationSend.pushNotification(
                                    AltosPuntajes.this,
                                    rival,
                                    title,
                                    message
                            );
                        }else{
                            rivalNick = "";
                        }

                        Toast.makeText(AltosPuntajes.this, "jugador "+currentUserNick+" usted está en la posicion: "+currentUserPosition+" de la tabla "+ rivalNick, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}