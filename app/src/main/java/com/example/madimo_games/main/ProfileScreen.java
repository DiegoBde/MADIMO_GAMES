package com.example.madimo_games.main;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.madimo_games.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileScreen extends AppCompatActivity {
    TextView nombreUser, score1, score2, score3;
    Button btnLogOut;
    ImageButton btnEditarFoto;
    ImageView ivFotoPerfil, ivPaisJugador;
    FirebaseAuth auth;
    DatabaseReference dataBase;
    StorageReference storage;
    String rutaAlmacenamiento = "FotosDePerfil/*";
    private static final int CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO = 200;
    private static final int CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN = 300;

    private String [] permisosAlmacenamiento;
    private Uri imagen_Uri;
    private String perfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        permisosAlmacenamiento = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        nombreUser=findViewById(R.id.txt_nickProfile);
        score1 = findViewById(R.id.txt_scoreOrdenamiento);
        score2 = findViewById(R.id.txt_scoreGato);
        score3 = findViewById(R.id.txt_scoreBreakOut);
        btnEditarFoto = findViewById(R.id.btn_editarFoto);
        btnLogOut = findViewById(R.id.btn_volver);
        ivFotoPerfil = findViewById(R.id.iv_fotoProfile);
        ivPaisJugador = findViewById(R.id.iv_bandera);
        getUserInfo();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ProfileScreen.this, MainScreen.class));
                finish();
            }
        });

        btnEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarDatos();
            }
        });

    }
    private void editarDatos(){
        String[] opciones = {"Foto de Perfil"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(opciones, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    perfil="imagen";
                    actualizarFotoPerfil();
                }
            }
        });
        builder.create().show();
    }

    private void actualizarFotoPerfil() {
        String[] opciones = {"Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(!comprobarPermisoAlmacenamiento()){
                        solicitarPermisoAlmacenamiento();
                    }else{
                        elegirImagenDeGaleria();
                    }
                }
            }
        });
        builder.create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisoAlmacenamiento() {
        requestPermissions(permisosAlmacenamiento,CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO);
    }

    private boolean comprobarPermisoAlmacenamiento() {
        boolean resultado = ContextCompat.checkSelfPermission(ProfileScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return resultado;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO:{
                if(grantResults.length>0){
                    boolean escrituraDeAlamcenamientoAceptado = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(escrituraDeAlamcenamientoAceptado){
                        elegirImagenDeGaleria();
                    }else{
                        Toast.makeText(this, "HABILITE EL PERMISO DE LA GALERIA", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void elegirImagenDeGaleria() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria,CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imagen_Uri = data.getData();
        subirFoto(imagen_Uri);
        /*if(requestCode==RESULT_OK){
            if(requestCode==CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN){
              imagen_Uri = data.getData();
               subirFoto(imagen_Uri);
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirFoto(Uri imagen_uri) {

        String rutaDeArchivoYNombre = rutaAlmacenamiento + "" + perfil + "_" + auth.getCurrentUser().getUid();
        StorageReference storageReference = storage.child(rutaDeArchivoYNombre);
        storageReference.putFile(imagen_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if(uriTask.isSuccessful()){
                            HashMap<String,Object>resultado = new HashMap<>();
                            resultado.put(perfil,downloadUri.toString());
                            dataBase.child("Users").child(auth.getCurrentUser().getUid()).updateChildren(resultado)
                                    //dataBase.child(auth.getCurrentUser().getUid()).updateChildren(resultado)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProfileScreen.this, "LA IMAGEN HA SIDO CAMBIADA CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileScreen.this, "HA OCURRIDO UN ERRROR", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(ProfileScreen.this, "ALGO HA SALIDO MAL", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileScreen.this, "ALGO HA SALIDO MAL 2", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getUserInfo(){
        String id= auth.getCurrentUser().getUid();
        dataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String name =  dataSnapshot.child("nick").getValue().toString();
                    String scoreOrdenamiento = dataSnapshot.child("score1").getValue().toString();
                    String scoreGato = dataSnapshot.child("score2").getValue().toString();
                    String scoreBreakOut = dataSnapshot.child("score3").getValue().toString();
                    String imagen = dataSnapshot.child("imagen").getValue().toString();
                    String pais = dataSnapshot.child("country").getValue().toString();

                    nombreUser.setText(name);
                    score1.setText(scoreOrdenamiento);
                    score2.setText(scoreGato);
                    score3.setText(scoreBreakOut);

                    try {
                        Picasso.get().load(imagen).into(ivFotoPerfil);
                        switch (pais){

                            case "Argentina":
                                Picasso.get().load(R.drawable.argentina).into(ivPaisJugador);
                                break;
                            case "Chile":
                                Picasso.get().load(R.drawable.chile).into(ivPaisJugador);
                                break;
                            case "Peru":
                                Picasso.get().load(R.drawable.peru).into(ivPaisJugador);
                                break;
                            case "Colombia":
                                Picasso.get().load(R.drawable.colombia).into(ivPaisJugador);
                                break;
                        }

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.spawnicon).into(ivFotoPerfil);
                        Picasso.get().load(R.drawable.argentina).into(ivPaisJugador);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


}