package com.example.madimo_games.main;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.example.madimo_games.ordenamiento.MainOrdenamiento;

public class OrdenFragment extends Fragment {
    private VideoView gameplayOrden;

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            View v = inflater.inflate(R.layout.activity_orden_fragment, container, false);
            gameplayOrden = (VideoView) v.findViewById(R.id.vv_gameplayOrden);
            gameplayOrden.setVideoURI(Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.gameplayordenamix));

            Button btnJugar = (Button) v.findViewById(R.id.btnJugar);
            btnJugar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(getActivity(), MainOrdenamiento.class);
                    //in.putExtra("algo", "Cosas");
                    startActivity(in);
                }
            });

            return v;
        }
    @Override
    public void onResume() {
        super.onResume();
        reproducirVideo();
    }
    public void reproducirVideo(){
        gameplayOrden.start(); //inicia Video
        gameplayOrden.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);
            }
        });
    }
}

