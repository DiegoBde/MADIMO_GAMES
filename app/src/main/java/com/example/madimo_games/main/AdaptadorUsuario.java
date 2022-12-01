package com.example.madimo_games.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madimo_games.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.myHolder>{

    private Context context;
    private List<Usuario> usuarioList;
    private int juego, puntaje;

    public AdaptadorUsuario(Context context, List<Usuario> usuarioList, int juego) {
        this.context = context;
        this.usuarioList = usuarioList;
        this.juego = juego;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.score_layout,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int i) {

        String imagen = usuarioList.get(i).getImagen();
        String nombre = usuarioList.get(i).getName();
        String pais = usuarioList.get(i).getCountry();

        switch (juego){
            case 1:
                 puntaje = usuarioList.get(i).getScore1();
                break;
            case 2:
                 puntaje = usuarioList.get(i).getScore2();
                break;
            case 3:
                 puntaje = usuarioList.get(i).getScore3();
                break;
        }

        String puntajeString = String.valueOf(puntaje);

        holder.nombreJugador.setText(nombre);
        holder.puntajeJugador.setText(puntajeString);

        try{
            Picasso.get().load(imagen).into(holder.imagenJugador);

            switch (pais){

                case "Argentina":
                    Picasso.get().load(R.drawable.argentina).into(holder.paisJugador);
                    break;
                case "Chile":
                    Picasso.get().load(R.drawable.chile).into(holder.paisJugador);
                    break;
                case "Peru":
                    Picasso.get().load(R.drawable.peru).into(holder.paisJugador);
                    break;
                case "Colombia":
                    Picasso.get().load(R.drawable.colombia).into(holder.paisJugador);
                    break;
            }
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder{

        ImageView imagenJugador, paisJugador;
        TextView nombreJugador, puntajeJugador;

        public myHolder(@NonNull View itemView) {
            super(itemView);

            imagenJugador = itemView.findViewById(R.id.iv_fotoPerfilJugador1);
            nombreJugador = itemView.findViewById(R.id.txt_nombreJugador1);
            puntajeJugador = itemView.findViewById(R.id.txt_puntajeJugador1);
            paisJugador = itemView.findViewById(R.id.iv_banderaPais);
        }
    }
}
