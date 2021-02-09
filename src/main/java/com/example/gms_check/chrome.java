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

public class chrome extends Activity {


    public static boolean preloadResult,defaultResult,hotseatResult,RESULT;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.chrome);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.chrome);
        myLayout.setBackgroundColor( Color.GRAY);


        Button test = (Button) findViewById( R.id.test_chrome );
        Button back = (Button) findViewById( R.id.back_chrome );
        Button reset = (Button)findViewById( R.id.reset_chrome );


        final TextView preloadOutput = (TextView) findViewById( R.id.preloadOutputChrome );
        final TextView defaultOutput = (TextView) findViewById( R.id.defaultOutputChrome );
        final TextView hotseatOutput = (TextView) findViewById( R.id.hotseatOutputChrome );


        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int preload = Integer.parseInt( MainActivity.exeCmd( "pm list packages -i | grep -c com.android.chrome" ).toString().replaceAll( "\n","" ));
                    if (preload==1){
                        preloadOutput.setText("Pass");
                        preloadResult =true;
                    }else {
                        preloadOutput.setText("Fail");
                        preloadResult = false;
                    }

                    int default1 = Integer.parseInt( MainActivity.exeCmd( "am start -W -a android.intent.action.VIEW -d \"http://www.baidu.com/\" | grep -c com.android.chrome" ).toString().replaceAll( "\n","" ) );
                    if(default1==1){
                        defaultOutput.setText( "Pass" );
                        defaultResult = true;
                    }else {
                        defaultOutput.setText( "Fail" );
                        defaultResult = false;
                    }

                    Thread.sleep( 0x7d0 );
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );


                    String out1 = MainActivity.exeCmd( "xmllint /tmp/view.xml --xpath \"//node[contains(@resource-id,\"hotseat\")]/node/nod" +
                            "e/@text\" | grep -c Chrome" ).toString().replaceAll( "\n","" );
                    String out2 = MainActivity.exeCmd( "xmllint /tmp/view.xml --xpath \"//node[contains(@resource-id,\"hotseat\")]/node/nod" +
                            "e/@text\" | grep -c Chrome" ).toString().replaceAll( "\n","" ) ;
                    if(Integer.parseInt( out1 )==1||Integer.parseInt( out2 )==2){
                        hotseatOutput.setText( "Pass" );
                        hotseatResult = true;
                    }else{
                        hotseatOutput.setText( "Fail" );
                        hotseatResult = false;
                    }
                    MainActivity.exeCmd( "am force-stop com.android.chrome" );

                    RESULT = preloadResult&&hotseatResult&&defaultResult;

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(preloadOutput.getText().equals( "Pass" ))
                    preloadOutput.setTextColor( Color.GREEN );
                else
                    preloadOutput.setTextColor( Color.RED );

                if(defaultOutput.getText().equals( "Pass" ))
                    defaultOutput.setTextColor( Color.GREEN );
                else
                    defaultOutput.setTextColor( Color.RED );
                if(hotseatOutput.getText().equals( "Pass" ))
                    hotseatOutput.setTextColor( Color.GREEN );
                else
                    hotseatOutput.setTextColor( Color.RED );

            }
        } );

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(4, i);
                finish();
            }
        } );
        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preloadOutput.setText( null );
                defaultOutput.setText( null );
                hotseatOutput.setText( null );
            }
        } );

    }
}
