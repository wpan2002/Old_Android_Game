package com.example.william.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Manish on 10/24/2016.
 */

public class Blade{

    private Bitmap bitmap;
    private int x;
    private int y;
    private int z;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;
    private boolean b;

    Context c;

    //creating a rect object for a friendly ship
    private Rect detectCollision;


    public Blade(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blade1);
        z = 10 + (bitmap.getHeight())/2;
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        Random generator = new Random();
        speed = 2;
        c = context;
        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();
        if(y > z) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blade1);
            b = true;
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blade2);
            b = false;
        }
        //initializing rect object
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(int playerSpeed) {
        x -= 3;
        x -= speed;
        if(b) {
            y -= 2;

        }else {
            y += 2;

        }
        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
            if(y > z) {
                bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.blade1);
                b = true;
            }else {
                bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.blade2);
                b = false;
            }
        }

        //Adding the top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }



    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    //getters
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}