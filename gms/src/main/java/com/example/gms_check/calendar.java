package com.example.gms_check;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class calendar extends Activity {
    public static boolean PRELOAD,DEFAULT,RESULT;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.calendar);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.calendar);
        myLayout.setBackgroundColor( Color.GRAY);

        Button test = (Button)findViewById( R.id.test_calendar );
        Button reset = (Button)findViewById( R.id.reset_calendar );
        Button back = (Button)findViewById( R.id.back_calendar );
        final TextView defaultOutput = (TextView) findViewById( R.id.defaultOutputCalendar );
        final TextView preloadOutput = (TextView) findViewById( R.id.preloadOutputCalendar );

        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultOutput.setText( null );preloadOutput.setText( null );
            }
        } );

        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String preloadStr = "pm list packages -i | grep -c com.google.android.calendar";
                    int preloadRes = Integer.parseInt(MainActivity.exeCmd( preloadStr ).toString().replaceAll( "\n","" ));
                    if(preloadRes==1){
                        preloadOutput.setText( "Pass" );PRELOAD=true;
                    }else{
                        preloadOutput.setText( "Fail" );PRELOAD=false;
                    }
                    String defaultStr = "am start -W -a android.intent.action.VIEW -d content://com.android.calendar/time/1410665898789 | grep -c com.google.android.calendar";
                    int defaultRes = Integer.parseInt( MainActivity.exeCmd( defaultStr ).toString().replaceAll( "\n","" ) );
                    if(defaultRes==1){
                        defaultOutput.setText( "Pass" );DEFAULT=true;
                    }else{
                        defaultOutput.setText( "Fail" );DEFAULT=false;
                    }
                    if(preloadOutput.getText().equals( "Pass" ))
                        preloadOutput.setTextColor( Color.GREEN );
                    else
                        preloadOutput.setTextColor( Color.RED );
                    if(defaultOutput.getText().equals( "Pass" ))
                        defaultOutput.setTextColor( Color.GREEN );
                    else
                        defaultOutput.setTextColor( Color.RED );
                    MainActivity.exeCmd( "am force-stop com.google.android.calendar" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RESULT=PRELOAD&&DEFAULT;
            }
        } );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(7, i);
                finish();
            }
        } );


}


}
