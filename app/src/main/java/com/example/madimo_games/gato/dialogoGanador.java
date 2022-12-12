package com.example.madimo_games.gato;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.madimo_games.R;

public class dialogoGanador extends Dialog {

    private final String mensaje;
    private final MainGato mainActivity;

    public dialogoGanador(@NonNull Context context, String mensaje) {
        super(context);
        this.mensaje = mensaje;
        this.mainActivity = ((MainGato)context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogo_ganador);
        final TextView msgTxt = findViewById(R.id.msgTxt);
        final Button btnVolver = findViewById(R.id.btnVolver);

        msgTxt.setText(mensaje);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                getContext().startActivity(new Intent(getContext(), PlayerName.class));
                mainActivity.finish();

            }
        });


    }
}
