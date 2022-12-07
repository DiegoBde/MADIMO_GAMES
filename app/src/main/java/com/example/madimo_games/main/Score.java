package com.example.madimo_games.main;


import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class Score {

    /*
     public void nuevoRecord(int puntajeNuevo, int puntajeRecord, DatabaseReference database, String id){
        Map<String, Object> mapUpdate= new HashMap<>();
        if(puntajeRecord < puntajeNuevo){ //revisar los tipos de datos, dejar score con integer
            mapUpdate.put("score1", 0);
            mapUpdate.put("score2", 0);
            mapUpdate.put("score3", puntajeNuevo);
            database.child("Users").child(id).updateChildren(mapUpdate); //update score
        }
    }
     */
      public void sendScore(Bundle bundle, int puntaje, String nomJuego, Intent intent){
          bundle.putInt("puntaje", puntaje);
          bundle.putString("nomJuego", nomJuego);
          intent.putExtras(bundle);
      }
      public void nuevoRecord(int puntajeNuevo, int puntajeRecord, DatabaseReference database, String id, String juego){
        Map<String, Object> mapUpdate= new HashMap<>();
        if(puntajeRecord < puntajeNuevo){ //revisar los tipos de datos, dejar score con integer
            mapUpdate.put(juego, puntajeNuevo);
            database.child("Users").child(id).updateChildren(mapUpdate); //update score
        }
    }



}
