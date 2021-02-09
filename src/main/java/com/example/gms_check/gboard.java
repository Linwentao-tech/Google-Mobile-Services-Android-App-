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

public class gboard extends Activity {
    public static boolean PRELOAD,DEFAULT,RESULT;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.gboard);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.gboard);
        myLayout.setBackgroundColor( Color.GRAY);
        final TextView defaultGb = (TextView)findViewById( R.id.defaultOutputGboard );
        final TextView preloadGb = (TextView)findViewById( R.id.preloadOutputGboard );
        Button test = (Button)findViewById( R.id.test_gb );
        Button reset = (Button)findViewById( R.id.reset_gb );
        Button back = (Button)findViewById( R.id.back_gb );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(9, i);
                finish();
            }
        } );
        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preloadGb.setText( null );defaultGb.setText( null );
            }
        } );

        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String pre_gb = "settings get secure default_input_method | grep -c com.google.android.inputmethod";
                    int preDig = Integer.parseInt( MainActivity.exeCmd( pre_gb ).toString().replaceAll( "\n","" ));
                    if(preDig==1){
                        preloadGb.setText( "Pass" );PRELOAD=true;preloadGb.setTextColor( Color.GREEN );
                    }else{
                        preloadGb.setText( "Fail" );PRELOAD=false;preloadGb.setTextColor( Color.RED );
                    }
                    String def_gb = "ime list -a | grep mId | grep -v -c mId=com.google.android";
                    int defDig = Integer.parseInt( MainActivity.exeCmd( pre_gb ).toString().replaceAll( "\n","" ));
                    if(preDig==0){
                        defaultGb.setText( "Pass" );DEFAULT=true;defaultGb.setTextColor( Color.GREEN );
                    }else{
                        defaultGb.setText( "Fail" );DEFAULT=false;defaultGb.setTextColor( Color.RED );
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                RESULT = PRELOAD&&DEFAULT;
            }
        } );









    }
}
