package com.example.madimo_games.main;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.example.madimo_games.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.madimo_games.breakout.MainBreakOut;

public class BreakOutFragment extends Fragment{
    private VideoView gameplay;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

@Override
    public View onCreateView (LayoutInflater inflater, ViewGroup viewGroup,Bundle savedInstanceState){

    View v = inflater.inflate(R.layout.activity_break_out_fragment, viewGroup, false);

    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();
    gameplay = (VideoView) v.findViewById(R.id.vv_gameplayBO);
    gameplay.setVideoURI(Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.gameplaybreakout));

    ImageButton btnJugar = v.findViewById(R.id.btn_jugarBreakOut);
    ImageButton btnScore = v.findViewById(R.id.btn_puntajesBreakOut);

    btnScore.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent in2 = new Intent(getActivity(), AltosPuntajes.class);
            Bundle bundle;
            bundle = new Bundle();
            String numJuego = "score3";
            bundle.putString("numJuego", numJuego);
            in2.putExtra("numJuego", numJuego);
            startActivity(in2);
        }
    });


    btnJugar.setOnClickListener(new View.OnClickListener() {
            Intent in = new Intent(getActivity(), MainBreakOut.class);
            @Override
            public void onClick(View view) {

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
        gameplay.start(); //inicia Video
        gameplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //Video en loop infinito
                mp.setLooping(true);
            }
        });
    }



}
