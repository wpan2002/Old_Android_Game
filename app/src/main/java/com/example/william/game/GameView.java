package com.example.william.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    static MediaPlayer gameOnsound;
    static MediaPlayer gameOnSound2;
    //music 2 starts at 120
    final MediaPlayer killedEnemysound;
    final MediaPlayer bos;
    final MediaPlayer bos2;
    final MediaPlayer bos3;
    final MediaPlayer gameOversound;
    final MediaPlayer death;
    final MediaPlayer run;
    final MediaPlayer ror;

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;

    private Paint paint;
    private boss boss;
    private boss2 boss2;
    private boss3 boss3;
    private finalboss finalboss;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Enemy[] enemies;

    private Shield shield;

    private Missile[] missiles;

    private Blade[] blades;

    private LowBlade lowblade;

    private illum[] illums;

    private Escape escape;

    private Escape2 escape2;

    private Escape3 escape3;

    private Superplayer superp;

    private int enemyCount = 3;

    int score;

    //created a reference of the class Friend
    private Friend friend;


    private ArrayList<Star> stars = new
            ArrayList<Star>();

    //defining a boom object to display blast
    private Boom boom;
    private Explode explode;

    int highScore[] = new int[4];

    //Shared Prefernces to store the High Scores
    SharedPreferences sharedPreferences;

    //a screenX holder
    int screenX;

    //to count the number of Misses
    int countMisses;

    //indicator that the enemy has just entered the game screen
    boolean flag ;
    Context context;


    //an indicator if the game is Over
    private boolean isGameOver ;

    int c;

    int p;

    private boolean bossTime;

    private boolean cutscene;

    private boolean shielded = false;

    private Charge charge;

    private Shield2 shield2;

    int bosnum;

    private boolean gameon2;

    private boolean finalbosstime;

    int finbosc;

    int bossscore = 10;




    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        shield2 = new Shield2(context);

        shield = new Shield(context, screenX, screenY);
        shield.setX(-800);
        //single enemy initialization
        enemies = new Enemy[enemyCount];
        for (int i = 0; i < enemyCount; i++) {
            enemies[i] = new Enemy(context, screenX, screenY);
        }

        missiles = new Missile[2];
        for (int i = 0; i < 2; i++) {
            missiles[i] = new Missile(context, screenX, screenY);
        }

        blades = new Blade[2];
        for (int i = 0; i < 2; i++) {
            blades[i] = new Blade(context, screenX, screenY);
        }

        lowblade = new LowBlade(context, screenX, screenY);

        illums = new illum[3];
        for (int i = 0; i < 3; i++) {
            illums[i] = new illum(context, screenX, screenY);
        }

        escape = new Escape(context,screenX,screenY);
        escape2 = new Escape2(context,screenX,screenY);
        escape3 = new Escape3(context,screenX,screenY);
        superp = new Superplayer(context);

        //initializing boom object
        boom = new Boom(context);
        explode = new Explode(context);
        charge = new Charge(context);

        //initializing the Friend class object
        friend = new Friend(context, screenX, screenY);
        boss = new boss(context, screenX, screenY);
        boss2 = new boss2(context, screenX, screenY);
        boss3 = new boss3(context, screenX, screenY);
        finalboss = new finalboss(context, screenX, screenY);
        this.screenX = screenX;

        countMisses = 0;

        isGameOver = false;
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        gameOnSound2 = MediaPlayer.create(context,R.raw.gameon2);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);
        bos = MediaPlayer.create(context,R.raw.boss);
        death = MediaPlayer.create(context,R.raw.death);
        run = MediaPlayer.create(context,R.raw.run);
        ror = MediaPlayer.create(context,R.raw.roar);
        bos2 = MediaPlayer.create(context,R.raw.boss2);
        bos3 = MediaPlayer.create(context,R.raw.boss3);
//starting the game music as the game starts
        gameOnsound.start();
        this.context = context;

        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);

//initializing the array high scores with the previous values
        highScore[0] = sharedPreferences.getInt("score1",0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);

    }


    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        player.update();
        if(score == 109 && !gameon2) {
            gameOnsound.stop();
            gameOnSound2.start();
            gameon2 = true;
            bossscore = bossscore + 5;
            //finalbosstime = true;
        }

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);

        explode.setX(-2050);
        explode.setY(-2050);

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        //setting the flag true when the enemy just enters the screen
        /*if(enemies.getX()==screenX){
            flag = true;
        }*/


        //if collision occurs with player
        for (int i = 0; i < enemyCount; i++) {
            if(!bossTime) {
                enemies[i].update(player.getSpeed());
            }else {
                enemies[i].setX(-800);
            }
            //if collision occurrs with player
            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {

                //displaying boom at that location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());
                if(!bossTime) {
                    killedEnemysound.start();
                    enemies[i].setX(-200);
                    score++;
                    c++;
                }

            }
        }// the condition where player misses the enemy
        /*else{
            //if the enemy has just entered
            if(flag){
                //if player's x coordinate is more than the enemies's x coordinate.i.e. enemy has just passed across the player
                if(player.getDetectCollision().exactCenterX() >= enemies.getDetectCollision().exactCenterX()){
                    //increment countMisses
                    countMisses++;

                    //setting the flag false so that the else part is executed only when new enemy enters the screen
                    flag = false;
                    //if no of Misses is equal to 3, then game is over.
                    if(countMisses==3){
                        //setting playing false to stop the game.
                        playing = false;
                        isGameOver = true;
                    }
                }
            }
        }*/

        if(!bossTime) {
            friend.update(player.getSpeed());
        }else {
            friend.setX(-800);
        }
        //updating the friend ships coordinates
        //friend.update(player.getSpeed());
        //checking for a collision between player and a friend
        if(Rect.intersects(player.getDetectCollision(),friend.getDetectCollision())){
            if(!shielded) {

                //displaying the boom at the collision
                boom.setX(friend.getX());
                boom.setY(friend.getY());
                //setting playing false to stop the game
                if (!bossTime) {
                    playing = false;
                    //setting the isGameOver true as the game is over
                    isGameOver = true;
                    gameOnsound.stop();
                    //play the game over sound

                    run.start();
                    for (int i = 0; i < 4; i++) {
                        if (highScore[i] < score) {

                            final int finalI = i;
                            highScore[i] = score;
                            break;
                        }
                    }

                    //storing the scores through shared Preferences
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    for (int i = 0; i < 4; i++) {
                        int j = i + 1;
                        e.putInt("score" + j, highScore[i]);
                    }
                    e.apply();
                }
            }else {
                if (!bossTime) {
                    shielded = false;
                    friend.setX(-800);
                }
            }
        }

        if(c >= 10) {
            bossTime = true;
            if(bosnum == 0) {
                boss.setX(1000);
                bos.start();
            }else if(bosnum == 1){
                boss2.setX(1000);
                bos2.start();
            }else {
                boss3.setX(1000);
                bos3.start();
            }

        }
        if(bossTime && finalbosstime) {
            finalboss.setX(900);

        }

        if(c == 5) {
            shield.respawn();
        }

        if (Rect.intersects(player.getDetectCollision(), shield.getDetectCollision())) {
            shielded = true;
            shield.setX(-10000);
        }

        shield.update(player.getSpeed());

        if(shielded) {
            shield2.setX(player.getX());
            shield2.setY(player.getY());
        }else {
            shield2.setX(10000);
            shield2.setY(10000);
        }

        if(p >= 9) {
            if(bosnum == 0) {
                bos.stop();
                death.start();
            }
        }
        if(p >= 50) {
            charge.setX(player.getX());
            charge.setY(player.getY());
        }else {
            charge.setX(10000);
            charge.setY(10000);
        }
        if(p>= 100) {
            /*bossTime = false;
            boss.setX(-10000);
            c = 0;
            p = 0;*/
            cutscene = true;
        }
        if(cutscene) {
            ror.start();
            escape.update(player.getSpeed());
            escape2.update(player.getSpeed());
            escape3.update(player.getSpeed());
            if(escape.getX() > 800 && escape.getX() < 1000) {
                if(bosnum == 0) {
                    explode.setX(boss.getX() - 300);
                    explode.setY(boss.getY());
                }else if(bosnum == 1){
                    explode.setX(boss2.getX() - 300);
                    explode.setY(boss2.getY());
                }else {
                    explode.setX(boss3.getX() - 300);
                    explode.setY(boss3.getY());
                }
            }
            if(escape.getX() > 1000) {
                bossTime = false;
                boss.setX(-10000);
                boss2.setX(-10000);
                boss3.setX(-10000);
                finalboss.setX(-10000);
                c = 0;
                p = 0;
                cutscene = false;
                score = score + bossscore;
                bosnum++;
                if(bosnum > 2) {
                    bosnum = 0;
                }
                if(finalbosstime) {
                    finalbosstime = false;
                }
            }

        }else {
            escape.setX(-800);
            escape2.setX(-800);
            escape3.setX(-800);
        }
        if(score > 109) {
            superp.setX(player.getX());
            superp.setY(player.getY());
        }else {
            superp.setX(10000);
        }

        if(!bossTime) {
            lowblade.setX(-800);
        }else if(bosnum == 1 && !finalbosstime) {
            lowblade.update(player.getSpeed());
        }
        if (Rect.intersects(player.getDetectCollision(), lowblade.getDetectCollision())) {
            if(!shielded && !cutscene) {
                //displaying boom at that location
                boom.setX(lowblade.getX());
                boom.setY(lowblade.getY());


                //setting the isGameOver true as the game is over
                if (bossTime && bosnum  == 1) {
                    playing = false;
                    isGameOver = true;
                    gameOnsound.stop();
                    //play the game over sound
                    run.start();
                    for (int l = 0; l < 4; l++) {
                        if (highScore[l] < score) {

                            final int finalI = l;
                            highScore[l] = score;
                            break;
                        }
                    }

                    //storing the scores through shared Preferences
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    for (int k = 0; k < 4; k++) {
                        int j = k + 1;
                        e.putInt("score" + j, highScore[k]);
                    }
                    e.apply();
                }
            }else {
                if (bossTime && bosnum == 1 && !cutscene) {
                    shielded = false;
                    lowblade.setX(-800);
                    for (int k = 0; k < 2; k++) {
                        blades[k].setX(-800);
                    }

                }
            }

        }

        for (int i = 0; i < 2; i++) {
            if(!bossTime) {
                missiles[i].setX(-800);
                blades[i].setX(-800);
            }else if(bosnum == 0 && !finalbosstime) {
                missiles[i].update(player.getSpeed());
                if(missiles[i].getX() <= -200) {
                    p++;
                }
            }else if(bosnum == 1 && !finalbosstime){
                blades[i].update(player.getSpeed());
                if(blades[i].getX() <= -200) {
                    p++;
                }
            }

            //if collision occurrs with player
            if (Rect.intersects(player.getDetectCollision(), missiles[i].getDetectCollision())) {
                if(!shielded && !cutscene) {
                    //displaying boom at that location
                    boom.setX(missiles[i].getX());
                    boom.setY(missiles[i].getY());


                    //setting the isGameOver true as the game is over
                    if (bossTime && bosnum == 0) {
                        playing = false;
                        isGameOver = true;
                        gameOnsound.stop();
                        //play the game over sound
                        run.start();
                        for (int l = 0; l < 4; l++) {
                            if (highScore[l] < score) {

                                final int finalI = l;
                                highScore[l] = score;
                                break;
                            }
                        }

                        //storing the scores through shared Preferences
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        for (int k = 0; k < 4; k++) {
                            int j = k + 1;
                            e.putInt("score" + j, highScore[k]);
                        }
                        e.apply();
                    }
                }else {
                    if (bossTime && bosnum == 0 && !cutscene) {
                        shielded = false;
                        for (int k = 0; k < 2; k++) {
                            missiles[k].setX(-800);
                        }
                    }
                }

            }
            if (Rect.intersects(player.getDetectCollision(), blades[i].getDetectCollision())) {
                if(!shielded && !cutscene) {

                    //displaying boom at that location
                    boom.setX(blades[i].getX());
                    boom.setY(blades[i].getY());


                    //setting the isGameOver true as the game is over
                    if (bossTime && bosnum % 2 == 1) {
                        playing = false;
                        isGameOver = true;
                        gameOnsound.stop();
                        //play the game over sound
                        run.start();
                        for (int j = 0; j < 4; j++) {
                            if (highScore[i] < score) {

                                final int finalI = j;
                                highScore[j] = score;
                                break;
                            }
                        }

                        //storing the scores through shared Preferences
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        for (int k = 0; k < 4; k++) {
                            int j = k + 1;
                            e.putInt("score" + j, highScore[k]);
                        }
                        e.apply();
                    }
                }else {
                    if (bossTime && bosnum % 2 == 1 && !cutscene) {
                        shielded = false;
                        for (int k = 0; k < 2; k++) {
                            blades[k].setX(-800);
                        }
                        lowblade.setX(-800);
                    }
                }

            }
        }
        for(int i = 0; i< 3; i++) {
            if(!bossTime) {
                illums[i].setX(-800);
            }else if(bosnum == 2 && !finalbosstime) {
                illums[i].update(player.getSpeed());
                if (illums[i].getX() <= 0 && (i == 0 || i == 1)) {
                    p++;
                }
            }
            if (Rect.intersects(player.getDetectCollision(), illums[i].getDetectCollision())) {
                if(!shielded && !cutscene) {
                    //displaying boom at that location
                    boom.setX(illums[i].getX());
                    boom.setY(illums[i].getY());


                    //setting the isGameOver true as the game is over
                    if (bossTime && bosnum ==2) {
                        playing = false;
                        isGameOver = true;
                        gameOnsound.stop();
                        //play the game over sound
                        run.start();
                        for (int l = 0; l < 4; l++) {
                            if (highScore[l] < score) {

                                final int finalI = l;
                                highScore[l] = score;
                                break;
                            }
                        }

                        //storing the scores through shared Preferences
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        for (int k = 0; k < 4; k++) {
                            int j = k + 1;
                            e.putInt("score" + j, highScore[k]);
                        }
                        e.apply();
                    }
                }else {
                    if (bossTime && bosnum == 2 && !cutscene ) {
                        shielded = false;
                        for (int k = 0; k < 3; k++) {
                            illums[k].setX(-800);
                        }
                    }
                }

            }
        }
        if(bossTime && finalbosstime) {
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if(bosnum == 2 && bossTime ) {
                canvas.drawColor(Color.WHITE);
                paint.setColor(Color.BLACK);
            }else {
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.WHITE);
            }
            if(score > 109) {
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.RED);
            }


            paint.setTextSize(20);

            for (Star s : stars) {
                if(bosnum == 2 && bossTime) {
                    paint.setStrokeWidth(s.getStarWidth() + 10);
                }else {
                    paint.setStrokeWidth(s.getStarWidth());
                }
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }


            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    boss.getBitmap(),
                    boss.getX(),
                    boss.getY(),
                    paint);

            canvas.drawBitmap(
                    boss2.getBitmap(),
                    boss2.getX(),
                    boss2.getY(),
                    paint);
            canvas.drawBitmap(
                    boss3.getBitmap(),
                    boss3.getX(),
                    boss3.getY(),
                    paint);
            canvas.drawBitmap(
                    finalboss.getBitmap(),
                    finalboss.getX(),
                    finalboss.getY(),
                    paint);
            if(bosnum == 0) {
                canvas.drawBitmap(
                        escape.getBitmap(),
                        escape.getX(),
                        escape.getY(),
                        paint);
            }
            if(bosnum== 1) {
                canvas.drawBitmap(
                        escape2.getBitmap(),
                        escape2.getX(),
                        escape2.getY(),
                        paint);
            }
            if(bosnum == 2) {
                canvas.drawBitmap(
                        escape3.getBitmap(),
                        escape3.getX(),
                        escape3.getY(),
                        paint);
            }

            canvas.drawBitmap(
                    shield2.getBitmap(),
                    shield2.getX(),
                    shield2.getY(),
                    paint);
            canvas.drawBitmap(
                    lowblade.getBitmap(),
                    lowblade.getX(),
                    lowblade.getY(),
                    paint);

            canvas.drawBitmap(
                    charge.getBitmap(),
                    charge.getX(),
                    charge.getY(),
                    paint);

            canvas.drawBitmap(
                    shield.getBitmap(),
                    shield.getX(),
                    shield.getY(),
                    paint);



            paint.setTextSize(30);
            canvas.drawText("Score: "+score,100,50,paint);

            for (int i = 0; i < enemyCount; i++) {
                canvas.drawBitmap(
                        enemies[i].getBitmap(),
                        enemies[i].getX(),
                        enemies[i].getY(),
                        paint
                );
            }

            for (int i = 0; i < 2; i++) {
                canvas.drawBitmap(
                        missiles[i].getBitmap(),
                        missiles[i].getX(),
                        missiles[i].getY(),
                        paint
                );
            }

            for (int i = 0; i < 2; i++) {
                canvas.drawBitmap(
                        blades[i].getBitmap(),
                        blades[i].getX(),
                        blades[i].getY(),
                        paint
                );
            }

            for (int i = 0; i < 3; i++) {
                canvas.drawBitmap(
                        illums[i].getBitmap(),
                        illums[i].getX(),
                        illums[i].getY(),
                        paint
                );
            }


            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );

            canvas.drawBitmap(
                    explode.getBitmap(),
                    explode.getX(),
                    explode.getY(),
                    paint
            );

            //drawing friends image
            canvas.drawBitmap(

                    friend.getBitmap(),
                    friend.getX(),
                    friend.getY(),
                    paint
            );
            canvas.drawBitmap(
                    superp.getBitmap(),
                    superp.getX(),
                    superp.getY(),
                    paint);

            if(shielded) {
                paint.setTextSize(100);
                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("SHIELD: ON",canvas.getWidth()/2+350,yPos+ 500,paint);
            }
            if(score > 109) {
                paint.setTextSize(100);
                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("OVERDRIVE",canvas.getWidth()/2 - 150,yPos+ 500,paint);
            }

            if(bossTime && bosnum == 0) {
                paint.setTextSize(80);
                canvas.drawText("BOSS: SINISTAR (+" + bossscore + " points)",450,80,paint);
            }else if(bossTime && bosnum == 1) {
                paint.setTextSize(80);
                canvas.drawText("BOSS: KEFKA (+" + bossscore + " points)",450,80,paint);
            }else if(bossTime) {
                paint.setTextSize(80);
                canvas.drawText("BOSS: GOD HIMSELF??? (+" + bossscore + " points)",450,80,paint);
            }

            if(cutscene) {
                paint.setTextSize(150);
                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("ATTACK",canvas.getWidth()/2 - 900,yPos+500,paint);
            }


            if(p >= 50 && !cutscene) {
                paint.setTextSize(100);
                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("CHARGE: " + (p - 50) + "/50",canvas.getWidth()/2 - 900,yPos+ 500,paint);
            }

            //draw game Over when the game is over
            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
                paint.setTextSize(120);
                canvas.drawText("Final Score:"+score,canvas.getWidth()/2,yPos+200,paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                stopMusic();
                context.startActivity(new Intent(context,MainActivity.class));

            }
        }
        return true;
    }
    public static void stopMusic(){
        gameOnsound.stop();
    }

}