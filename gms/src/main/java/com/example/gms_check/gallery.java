package com.example.gms_check;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class gallery extends Activity {
    public static boolean pickImgResult,reviewImgResult,RESULT;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.gallery);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.gallery);
        myLayout.setBackgroundColor( Color.GRAY);
        Button back = (Button)findViewById( R.id.back_gallery );
        Button test = (Button)findViewById( R.id.test_gallery );
        Button reset = (Button)findViewById( R.id.reset_gallery );
        final TextView defaultPickImg = (TextView)findViewById( R.id.defaultPickImgOutput );
        final TextView defaultReviewImg = (TextView)findViewById( R.id.defaultReviewImgOutput);


        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String pick = "am start -W -a android.intent.action.PICK -t image/* | grep -c com.google.android.apps.photos";
                    int pickResult = Integer.parseInt( MainActivity.exeCmd( pick ).toString().replaceAll( "\n","" ) );
                    if (pickResult==1){
                        defaultPickImg.setText( "Pass" );defaultPickImg.setTextColor( Color.GREEN );
                        pickImgResult = true;
                    }else {
                        defaultPickImg.setText( "Fail" );
                        pickImgResult=false;defaultPickImg.setTextColor( Color.RED );
                    }
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

                    String review ="am start -W -a com.android.camera.action.REVIEW -t image/* | grep -c com.google.android.apps.photos";
                    int reviewResult = Integer.parseInt( MainActivity.exeCmd( review ).toString().replaceAll( "\n","") );
                    if(reviewResult==1){
                        defaultReviewImg.setText( "Pass" );defaultReviewImg.setTextColor( Color.GREEN );
                        reviewImgResult = true;
                    }else{
                        defaultReviewImg.setText( "Fail" );defaultReviewImg.setTextColor( Color.RED );
                        reviewImgResult = false;
                    }
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
                    MainActivity.exeCmd( "am start -W -a android.media.action.STILL_IMAGE_CAMERA" );
                    Thread.sleep( 3000 );
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
                    MainActivity.exeCmd( "am force-stop com.google.android.apps.photos" );
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                RESULT = pickImgResult&&reviewImgResult;

            }
        } );


        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(5, i);
                finish();
            }
        } );

        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultPickImg.setText( null );
                defaultReviewImg.setText( null );
            }
        } );

    }


}
