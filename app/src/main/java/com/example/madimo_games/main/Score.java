package com.example.madimo_games.main;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class Score {

    /*
    public void nuevoRecord(String puntajeNuevo, String puntajeRecord, DatabaseReference database, String id, String key){
        Map<String, Object> mapUpdate= new HashMap<>();
        if(Integer.parseInt(puntajeRecord) < Integer.parseInt(puntajeNuevo)){ //revisar los tipos de datos, dejar score con integer
            mapUpdate.put(key, puntajeNuevo);
            database.child("Users").child(id).updateChildren(mapUpdate); //update score
        }
    }
     */
    public void nuevoRecord(int puntajeNuevo, int puntajeRecord, DatabaseReference database, String id){
        Map<String, Object> mapUpdate= new HashMap<>();
        if(puntajeRecord < puntajeNuevo){ //revisar los tipos de datos, dejar score con integer
            mapUpdate.put("score1", 2000);
            mapUpdate.put("score2", 1);
            mapUpdate.put("score3", puntajeNuevo);
            database.child("Users").child(id).updateChildren(mapUpdate); //update score
        }
    }

}
