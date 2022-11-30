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
import android.widget.ImageButton;
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.example.madimo_games.gato.MainGato;

public class GatoFragment extends Fragment  {
    private VideoView gameplayGato;
    MediaPlayer mPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_gato_fragment, container, false);

        gameplayGato = (VideoView) v.findViewById(R.id.vv_gameplayGato);
        gameplayGato.setVideoURI(Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.gameplaygato));


        ImageButton btnJugar = v.findViewById(R.id.btn_jugarGato);
        ImageButton btnScore = v.findViewById(R.id.btn_scoreGato);

        btnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in2 = new Intent(getActivity(), AltosPuntajes.class);
                Bundle bundle;
                bundle = new Bundle();
                String numJuego = "score2";
                bundle.putString("numJuego", numJuego);
                in2.putExtra("numJuego", numJuego);
                startActivity(in2);
            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(getActivity(), MainGato.class);
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
        gameplayGato.start(); //inicia Video
        gameplayGato.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);
            }
        });
    }
}