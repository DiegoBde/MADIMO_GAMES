package com.example.madimo_games.breakout;

import static java.lang.String.valueOf;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.madimo_games.R;
import com.example.madimo_games.main.Constants;
import com.example.madimo_games.main.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import androidx.annotation.MainThread;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainBreakOut mainBreakOut;
    private Score clasePuntaje = new Score();
    private Intent inGameOver = new Intent(getContext(), GameOverScreen.class);
    private Bundle b = new Bundle();

    FirebaseAuth auth;
    DatabaseReference dataBase;

    private int milli=0, seg=0, mins=0, score=0;
    private int puntaje;
    private String id, nomJuego = "score3";

    private Resources res = getResources();
    private Bitmap bitmapBG = BitmapFactory.decodeResource(res, R.drawable.space);
    private Bitmap bitmapPaddle = BitmapFactory.decodeResource(res, R.drawable.paddle);
    private Bitmap bitmapBall = BitmapFactory.decodeResource(res, R.drawable.ballspace);

    private MediaPlayer backgroundMusic = MediaPlayer.create(getContext(),R.raw.backgroud_music);
    private MediaPlayer jumpBall = MediaPlayer.create(getContext(),R.raw.pelotarebota);
    private MediaPlayer jumpBall2 = MediaPlayer.create(getContext(),R.raw.pelotarebota);
    private MediaPlayer breakBricks = MediaPlayer.create(getContext(),R.raw.rompebloques);

    private MainThread thread;
    private Rect r = new Rect();

    private RectPlayer player;
    private Point playerPoint;
    private Point ballPoint;
    private BricksManager bricksManager;
    private Ball ball;

    private boolean movingPlayer = false;
    private boolean gameOver = false;
    private long gameOverTime;
    private boolean signoY = true;
    private boolean signoX = true;
    private String gano;

    private boolean isOn;

    public GamePanel(Context context) {
        super(context);
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        id = auth.getCurrentUser().getUid();
        backgroundMusic.setVolume(0.5f,0.5f);
        isOn = true;
        timer();

        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);

        player = new RectPlayer(new Rect(50, 100, 300 ,150), Color.TRANSPARENT);

        playerPoint = new Point(Constants.SCREEN_WIDTH/2, 4*Constants.SCREEN_HEIGHT/4);
        player.update(playerPoint);

        ball = new Ball(new Rect(0, 0, 30 ,30), Color.TRANSPARENT);
        backgroundMusic.start();

        ballPoint = new Point(playerPoint.x+50,playerPoint.y-5); //first point
        ball.update(ballPoint);  //update first point


        bricksManager = new BricksManager(getLeft(), getRight(), getTop(), getBottom());

        setFocusable(true);

    }

    public String timer(){
        if(isOn) {
            milli++;
            if (milli == 34) {
                seg++;
                //ball.getRectangle().right += 10;
                //ball.getRectangle().bottom += 10;
                milli = 0;
            }
            if (seg == 59) {
                mins++;
                vel+=10;
                ball.getRectangle().right += 50;
                ball.getRectangle().bottom += 50;
                seg = 0;
            }
        }

        return "╰═ Tiempo: "+mins+":"+seg+" ═╯"+"\nPuntaje: "+ score;
    }

    public void reset(){

    }

    public void onWin(){
        if(bricksManager.bricks.size() == 0){
            gameOver = true;
            gano = "si";
            b.putString("gano", gano);
            puntaje = score - seg;
            clasePuntaje.sendScore(b,puntaje,nomJuego,inGameOver);
            getContext().startActivity(inGameOver);
            mainBreakOut.finish();
            //pasar String q indique si ganó o no
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder){

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(true){
            try{
                thread.setRunning(false);
                thread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!gameOver && player.getRectangle().contains((int)event.getX(), 4*Constants.SCREEN_HEIGHT/4))
                    movingPlayer = true;
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 2000){
                    //reset();
                    gameOver = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver && movingPlayer)
                    playerPoint.set((int)event.getX(), 4*Constants.SCREEN_HEIGHT/4);  //Objeto sigue al dedo precionado
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;

        }
        return true;
    }

    int vel = 15;
    int direccion=1;

    public void movimiento(){

        switch (direccion){
            case 0:
                ballPoint.set(ballPoint.x + vel, ballPoint.y + vel);  //  \.
                signoX = false;
                signoY = false;
                break;
            case 1:
                ballPoint.set(ballPoint.x + vel, ballPoint.y - vel);  //   /'
                signoX = false;
                signoY = true;
                break;
            case 2:
                ballPoint.set(ballPoint.x - vel, ballPoint.y + vel);  //   ./
                signoX = true;
                signoY = false;
                break;
            case 3:
                ballPoint.set(ballPoint.x - vel, ballPoint.y - vel);  //   '\
                signoX = true;
                signoY = true;
                break;
        }

    }
    public void movimientoColision(){
        if (ballPoint.y > Constants.SCREEN_HEIGHT && signoX) {  //  ./
            gameOver = true;
            puntaje = score - seg;
            clasePuntaje.sendScore(b,puntaje, nomJuego, inGameOver);

            direccion = 3;
            backgroundMusic.stop();

            getContext().startActivity(inGameOver);
            mainBreakOut.finish();
        }
        if (ballPoint.y > Constants.SCREEN_HEIGHT && !signoX) {
            gameOver = true;
            puntaje = score - seg;
            clasePuntaje.sendScore(b,puntaje, nomJuego, inGameOver);

            direccion = 1;
            backgroundMusic.stop();

            getContext().startActivity(inGameOver);
            mainBreakOut.finish();
        }
        if (ballPoint.x < 0 && signoY) {
            jumpBall2.start();
            direccion = 1;
        }
        if (ballPoint.x < 0 && !signoY) {
            jumpBall2.start();
            direccion = 0;
        }
        if (ballPoint.y < 0 && signoX) {
            jumpBall.start();
            direccion = 2;
        }
        if (ballPoint.y < 0 && !signoX) {
            jumpBall.start();
            direccion = 0;
        }
        if (ballPoint.x > Constants.SCREEN_WIDTH && signoY) {
            jumpBall2.start();
            direccion = 3;
        }
        if (ballPoint.x > Constants.SCREEN_WIDTH && !signoY) {
            jumpBall2.start();
            direccion = 2;
        }
        //gameOverTime = System.currentTimeMillis();
    }

    public void update(){
        onWin();
        if(!gameOver) {
            player.update(playerPoint); //actualiza player
            bricksManager.update();     //actualiza bricks
            movimientoColision();
            if(bricksManager.pelotaCollide(ball)){
                score +=100;
                breakBricks.start();
                if(signoX || signoY){
                    direccion = 3;
                }if(!signoX || signoY){
                    direccion = 1;
                }if(!signoX || !signoY){
                    direccion = 0;
                }if(signoX || !signoY) {
                    direccion = 2;
                }
            }else if(player.playerCollide(ball)){
                jumpBall.start();
                if(signoX ||signoY){
                    direccion = 3;
                }if(!signoX || signoY){
                    direccion = 1;
                }
            }

            movimiento();
            bricksManager.borrarBricks(ball);
            ball.update(ballPoint);
        }
    }


    @Override
    public void draw(Canvas canvas){
        Paint paint = new Paint();
        super.draw(canvas);

        Bitmap reBitmapBG = Bitmap.createScaledBitmap(bitmapBG, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT+200, true);
        Bitmap reBitmapPaddle = Bitmap.createScaledBitmap(bitmapPaddle, player.getRectangle().width(), player.getRectangle().height(), true);
        Bitmap reBitmapBall = Bitmap.createScaledBitmap(bitmapBall, ball.getRectangle().width(), ball.getRectangle().height(), true);

        canvas.drawBitmap(reBitmapBG, 0, 0, paint);

        player.draw(canvas);
        canvas.drawBitmap(reBitmapPaddle, player.getRectangle().left, player.getRectangle().top, paint);

        ball.draw(canvas);
        canvas.drawBitmap(reBitmapBall, ball.getRectangle().left, ball.getRectangle().top, paint);

        bricksManager.draw(canvas);

        paint.setTextSize(50);
        paint.setColor(Color.rgb(251,250,218));
        paint.setTypeface(getResources().getFont(R.font.audiowide));
        cronometro(canvas, paint, (timer()));

        if(gameOver){
            isOn=false;

        }

    }


    public void cronometro(Canvas canvas, Paint paint, String text){
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = (cHeight / 5f + r.height() / 2f - r.bottom)+80;
        canvas.drawText(text,x,y,paint);
    }
}