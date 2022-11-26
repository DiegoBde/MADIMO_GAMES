package com.example.madimo_games.main;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madimo_games.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.myHolder>{
    private Context context;
    private List<Usuario> usuarioList;

    public AdaptadorUsuario(Context context, List<Usuario> usuarioList) {
        this.context = context;
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_leader_board_screen,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int i) {
        String imagen = usuarioList.get(i).getImagen();
        String nombre = usuarioList.get(i).getName();
        int puntaje = usuarioList.get(i).getScore3();
        String puntajeString = String.valueOf(puntaje);

        holder.nombreJugador.setText(nombre);
        holder.puntajeJugador.setText(puntajeString);

        try{
            Picasso.get().load(imagen).into(holder.imagenJugador);
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }


    public class myHolder extends RecyclerView.ViewHolder{

        ImageView imagenJugador;
        TextView nombreJugador, puntajeJugador;

        public myHolder(@NonNull View itemView) {
            super(itemView);

            imagenJugador = itemView.findViewById(R.id.iv_fotoPerfilJugador1);
            nombreJugador = itemView.findViewById(R.id.txt_nombreJugador1);
            puntajeJugador = itemView.findViewById(R.id.txt_puntajeJugador1);
        }
    }
}
