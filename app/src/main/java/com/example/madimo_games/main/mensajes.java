package com.example.madimo_games.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.madimo_games.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class mensajes extends AppCompatActivity {
    FirebaseAuth auth;
    String token;
    DatabaseReference dataBase;
    String userToken;
    /*
        EditText etToken;
    NotificationManager mNotificationManager;
    MyFirebaseMessagingService myFirebaseMessagingService;
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();


        /*
        String title = "Hello";
        String message = "Testing";

        if(!title.equals("") && !message.equals("")){
            PushNotificationSend.pushNotification(
                    mensajes.this,
                    "cru_QAEPRiGLVoJ-3zMOxS:APA91bGP8nWKveJx5PehQeT4dy37pfV3Cujh-crKRJA5joTbisoP77lDtIFfUI0DEFYNXsqVt5pbiiHMVCtSdzVpxv4a46Zd9m6iK051bhBiUzJBiPa7FPQRwqfmULvTLezQAruv9UNj",
                    title,
                    message
            );
        }
         */
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        // Log and toast
                        System.out.println(token);
                        //Toast.makeText(mensajes.this, "Tu token es: "+token, Toast.LENGTH_SHORT).show();
                        actualizarToken();
                        sendAutoMessage();
                    }
                });
    /*

        //sendNotification("hola");
        //myFirebaseMessagingService.sendNotification("funaca","holas");

        etToken = findViewById(R.id.etToken);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(mensajes.this, "Tu token es: "+token, Toast.LENGTH_SHORT).show();

                        etToken.setText(token);
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        System.out.println(msg);
                        Toast.makeText(mensajes.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
 */
    }

/*
private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, mensajes.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.spawnicon)
                        .setContentTitle("GCM Notification") //this is the place I am looking for
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
 */

    private void obtenerToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        // Log and toast
                        System.out.println(token);
                        //Toast.makeText(mensajes.this, "Tu token es: "+token, Toast.LENGTH_SHORT).show();
                        actualizarToken();
                        sendAutoMessage();
                    }
                });

    }
    private void actualizarToken(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("token", token);
        dataBase.child("Users").child(auth.getCurrentUser().getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mensajes.this,"Info Actualizada", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(mensajes.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void sendAutoMessage(){
        String title = "Actualizacion de Datos", message = "El Jugador X Te ha superado!";


        dataBase.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userToken = dataSnapshot.child("token").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        PushNotificationSend.pushNotification(
                mensajes.this,
                token,
                title,
                message
        );
    }

    private void enviarPush(){
        String title = "w", message = "wq", userToken2 = "";
        PushNotificationSend.pushNotification(
                mensajes.this,
                userToken2,
                title,
                message
        );
    }
}