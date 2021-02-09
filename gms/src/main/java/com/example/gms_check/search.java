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

public class search extends Activity {
    public static boolean PRELOAD,UNIQUENESS,RESULT;
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate( savedInstanceState );
        setContentView(R.layout.search);

        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.search);
        myLayout.setBackgroundColor( Color.GRAY);
        final TextView preloadOutput = (TextView) findViewById( R.id.preloadOutputSearch );
        final TextView uniquenssOutput = (TextView) findViewById( R.id.uniquenessOutputSearch );
        Button reset = (Button) findViewById( R.id.reset_search );
        Button test = (Button) findViewById( R.id.test_search );
        Button back = (Button) findViewById( R.id.back_search );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(10, i);
                finish();
            }
        } );

        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preloadOutput.setText( null );uniquenssOutput.setText( null );
            }
        } );

        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String preload = "pm list packages -i | grep -c com.google.android.googlequicksearchbox";
                    int preloadResult = Integer.parseInt( MainActivity.exeCmd( preload ).toString().replaceAll( "\n","" ) );
                    if (preloadResult==1){
                        preloadOutput.setText( "Pass" );
                        PRELOAD = true;
                    }else{
                        preloadOutput.setText( "Fail" );
                        PRELOAD = false;
                    }
                    String uniqueness = "am start -W -a android.intent.action.WEB_SEARCH -e query wikipedia | grep -c com.google.android.googlequicksearchbox";
                    int uniquenessResult = Integer.parseInt( MainActivity.exeCmd( uniqueness ).toString().replaceAll( "\n","" )  );
                    if(uniquenessResult==1){
                        uniquenssOutput.setText( "Pass" );
                        UNIQUENESS = true;
                    }else{
                        uniquenssOutput.setText( "Fail" );
                        UNIQUENESS = true;
                    }
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(preloadOutput.getText().equals( "Pass" )){
                    preloadOutput.setTextColor( Color.GREEN );
                }else{
                    preloadOutput.setTextColor( Color.RED );
                }
                if(uniquenssOutput.getText().equals( "Pass" )){
                    uniquenssOutput.setTextColor( Color.GREEN );
                }else{
                    uniquenssOutput.setTextColor( Color.RED  );
                }
            }
        } );RESULT=UNIQUENESS&PRELOAD;




    }
}
