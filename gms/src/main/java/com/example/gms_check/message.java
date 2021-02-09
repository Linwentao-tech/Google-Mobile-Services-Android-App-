package com.example.gms_check;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class message extends Activity {
    public static boolean preloadResultb,defaultResultb,hotseatResultb,flagResultb,RESULT;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.messsage);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.messageMain);
        myLayout.setBackgroundColor( Color.GRAY);

        Button test = (Button) findViewById( R.id.test_me );
        Button reset = (Button) findViewById( R.id.reset_me );
        Button back = (Button) findViewById( R.id.back_me );
        final TextView preloadOutput = (TextView) findViewById( R.id.preloadOutputMessage );
        final TextView defaultOutput = (TextView) findViewById( R.id.defaultOutputMessage );
        final TextView hotseatOutput = (TextView) findViewById( R.id.hotseatOutputMessage);
        final TextView flagOutput = (TextView) findViewById( R.id.flagOutputMessage );

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(6, i);
                finish();

            }
        } );


        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preloadOutput.setText( null );
                defaultOutput.setText( null );
                hotseatOutput.setText( null );
                flagOutput.setText( null );
            }
        } );
        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder ab=new AlertDialog.Builder(message.this);
                    String check = "pm list features | grep -c android.hardware.telephony";
                    int checkResult = Integer.parseInt( MainActivity.exeCmd( check ).toString().replaceAll( "\n","" ) );
                    if (checkResult ==0){
                        String errMessage = "Tablet does not requires to preload Android Message";
                        ab.setTitle("Error");
                        ab.setMessage( errMessage);
                        ab.setNegativeButton("exit", null );
                        ab.show();
                        return;
                    }
                    //preload
                    String preload = "pm list packages -i | grep -c com.google.android.apps.messaging";
                    int preloadResult = Integer.parseInt( MainActivity.exeCmd( preload ).toString().replaceAll( "\n","" ) );
                    if (preloadResult==1){
                        preloadOutput.setText( "Pass" );preloadResultb=true;preloadOutput.setTextColor( Color.GREEN );
                    }else {
                        preloadOutput.setText( "Fail" );preloadResultb=false;preloadOutput.setTextColor( Color.RED );
                    }

                    //default
                    String defaultOutputstr = "am start -W -a android.intent.action.SENDTO -d sms:CCXXXXXXXXXX | grep -c com.google.android.apps.messaging";
                    int defaultResult = Integer.parseInt( MainActivity.exeCmd( defaultOutputstr ).toString().replaceAll( "\n","" ) );
                    if(defaultResult==1){
                        defaultOutput.setText( "Pass" );defaultResultb=true;defaultOutput.setTextColor( Color.GREEN );
                    }else{
                        defaultOutput.setText( "Fail" );defaultResultb=false;defaultOutput.setTextColor( Color.RED );
                    }
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

                    //hotseat
                    String test1 = "xmllint /tmp/view.xml --xpath \"//node[contains(@resource-id,\"hotseat\")]/node/node/@text\" | grep -c Messages";
                    String test2 = "xmllint /tmp/view.xml --xpath \"//node[contains(@resource-id,\"hotseat\")]/node/node/node/@text\" | grep -c Messages ";
                    int test1Result = Integer.parseInt( MainActivity.exeCmd( test1 ).toString().replaceAll( "\n","" ) );
                    int test2Result = Integer.parseInt( MainActivity.exeCmd( test2 ).toString().replaceAll( "\n","" ) );
                    if(test1Result==1 || test2Result==1){
                        hotseatOutput.setText( "Pass" );hotseatResultb=true;hotseatOutput.setTextColor( Color.GREEN );
                    }else{
                        hotseatOutput.setText( "Fail" );hotseatResultb=false;hotseatOutput.setTextColor( Color.RED );
                    }

                    //flag
                    String flag = "getprop | grep ro.com.google.acsa | grep -c true";
                    int flagResult = Integer.parseInt( MainActivity.exeCmd( flag ).toString().replaceAll( "\n","" ) );
                    if (flagResult==1){
                        flagOutput.setText( "Pass" );flagResultb=true;flagOutput.setTextColor( Color.GREEN );
                    }else {
                        flagOutput.setText( "Fail" );flagResultb=false;flagOutput.setTextColor( Color.RED );
                    }

                    MainActivity.exeCmd( "am force-stop com.google.android.apps.messaging" );


                } catch (IOException e) {
                    e.printStackTrace();
                }
                RESULT= flagResultb&&hotseatResultb&&defaultResultb&&flagResultb;


            }
        } );
}




}
