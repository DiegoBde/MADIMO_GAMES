package com.example.madimo_games.main;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.madimo_games.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AltosPuntajes extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
   // LinearLayoutManager mLayoutManager2;
    RecyclerView recyclerViewUsuarios;
    //RecyclerView recyclerViewUsuarios2;
    AdaptadorUsuario adaptadorUsuario;
    ArrayList<Usuario> usuarioList;
    FirebaseAuth auth;
    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altos_puntajes);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Puntajes");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager2 = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        //recyclerViewUsuarios2 = findViewById(R.id.recyclerViewUsuarios2);

        mLayoutManager.setReverseLayout(true); //ordenar al reves
        mLayoutManager.setStackFromEnd(true);
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(mLayoutManager);
       // recyclerViewUsuarios2.setHasFixedSize(true);
       // recyclerViewUsuarios2.setLayoutManager(mLayoutManager2);

        usuarioList = new ArrayList<>();

        obtenerTodosLosUsuarios("score3");

    }

    private void obtenerTodosLosUsuarios(String juego) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(juego).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Usuario usuario = ds.getValue(Usuario.class);

                    //  if(!usuario.getUid().equals(fUser).getUid()){
                    //      usuarioList.add(usuario)
                    // }
                    usuarioList.add(usuario);
                    adaptadorUsuario = new AdaptadorUsuario(AltosPuntajes.this, usuarioList);
                    recyclerViewUsuarios.setAdapter(adaptadorUsuario);
                    //recyclerViewUsuarios2.setAdapter(adaptadorUsuario);
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