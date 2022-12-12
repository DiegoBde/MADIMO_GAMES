package com.example.madimo_games.gato;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.madimo_games.R;

public class PlayerName extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        final EditText playerNameEt = findViewById(R.id.player_name_et);
        final AppCompatButton btnComenzar = findViewById(R.id.btnComenzar);

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getPlayerName = playerNameEt.getText().toString();

                if(getPlayerName.isEmpty()){
                    Toast.makeText(PlayerName.this, "Ingrese un nombre de jugador", Toast.LENGTH_SHORT).show();
                }
                else{

                    Intent intent = new Intent(PlayerName.this, MainGato.class);
                    intent.putExtra("playerName", getPlayerName);
                    startActivity(intent);
                    finish();

                }


            }
        });
    }

}