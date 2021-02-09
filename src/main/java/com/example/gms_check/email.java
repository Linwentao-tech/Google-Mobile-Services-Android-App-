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

public class email extends Activity {
    public static boolean DEFAULT,PRELOAD,CALENDAR_READ,CALENDAR_WRITE,CONTACTS_READ,CONTACTS_WRITE,RESULT;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.email);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.email);
        myLayout.setBackgroundColor( Color.GRAY);
        Button test = (Button)findViewById( R.id.test_gmail );
        Button reset = (Button)findViewById( R.id.reset_gmail );
        Button back = (Button)findViewById( R.id.back_gmail );
        //output
        final TextView defaultOutput = (TextView) findViewById( R.id.defaultOutputGmail );
        final TextView preloadOutput = (TextView) findViewById( R.id.preloadOutputGmail );
        final TextView calendarROutput = (TextView) findViewById( R.id.calendarReadableOutput );
        final TextView calendarWOutput = (TextView) findViewById( R.id.calendarWritableOutput );
        final TextView contactsROutput = (TextView) findViewById( R.id.contactsReadableOutput );
        final TextView contactsWOutput = (TextView) findViewById( R.id.contactsWritableOutput );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(8, i);
                finish();
            }
        } );
        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultOutput.setText( null );
                preloadOutput.setText( null );
                calendarROutput.setText( null );
                calendarWOutput.setText( null );
                contactsROutput.setText( null );
                contactsWOutput.setText( null );
            }
        } );
        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String preload  = "pm list packages -i | grep -c com.google.android.gm";
                    //preload
                    int preloadDigit = Integer.parseInt( MainActivity.exeCmd( preload ).toString().replaceAll( "\n","" ) );
                    if (preloadDigit==1){
                        preloadOutput.setText( "Pass" );PRELOAD=true;preloadOutput.setTextColor( Color.GREEN );
                    }else{
                        preloadOutput.setText( "Fail" );PRELOAD=false;preloadOutput.setTextColor( Color.RED );
                    }
                    //default
                    String defaultStr = "am start -W -a android.intent.action.SENDTO -d mailto:someone@gmail.com | grep -c com.google.android.gm";
                    int defaultDigit = Integer.parseInt( MainActivity.exeCmd( defaultStr ).toString().replaceAll( "\n","" ) );
                    if(defaultDigit==1){
                        defaultOutput.setText( "Pass" );DEFAULT=true;defaultOutput.setTextColor( Color.GREEN );
                    }else{
                        defaultOutput.setText( "Fail" );DEFAULT=false;defaultOutput.setTextColor( Color.RED );
                    }
                    MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
                    //read calendar
                    String readCalendar = "dumpsys package com.google.android.gm | grep -c \"android.permission.READ_CALENDAR: granted=true\"";
                    int readCalendarDigit = Integer.parseInt( MainActivity.exeCmd( readCalendar ).toString().replaceAll( "\n","" ) );
                    if(readCalendarDigit>=1){
                        calendarROutput.setText( "Pass" );CALENDAR_READ=true;calendarROutput.setTextColor( Color.GREEN );
                    }else{
                        calendarROutput.setText( "Fail" );CALENDAR_READ=false;calendarROutput.setTextColor( Color.RED );
                    }
                    //write calendar
                    String writeCalendar = "dumpsys package com.google.android.gm | grep -c \"android.permission.WRITE_CALENDAR: granted=true\"";
                    int writeCalendarDigit = Integer.parseInt( MainActivity.exeCmd( writeCalendar ).toString().replaceAll( "\n","" ) );
                    if(writeCalendarDigit>=1){
                        calendarWOutput.setText( "Pass" );CALENDAR_WRITE=true;calendarWOutput.setTextColor( Color.GREEN );
                    }else{
                        calendarWOutput.setText( "Fail" );CALENDAR_WRITE=false;calendarWOutput.setTextColor( Color.RED );
                    }
                    //read contacts
                    String readContacts = "dumpsys package com.google.android.gm | grep -c \"android.permission.READ_CONTACTS: granted=true\"";
                    int readContactsDigit = Integer.parseInt( MainActivity.exeCmd( readContacts ).toString().replaceAll( "\n","" ));
                    if(readContactsDigit>=1){
                        contactsROutput.setText( "Pass" );CONTACTS_READ=true;contactsROutput.setTextColor( Color.GREEN );
                    }else{
                        contactsROutput.setText( "Fail" );CONTACTS_READ=false;contactsROutput.setTextColor( Color.RED );
                    }
                    //write contacts
                    String writeContacts = "dumpsys package com.google.android.gm | grep -c \"android.permission.WRITE_CONTACTS: granted=true\"";
                    int writeContactsDigit = Integer.parseInt( MainActivity.exeCmd( writeContacts ).toString().replaceAll( "\n","" ) );
                    if(writeContactsDigit>=1){
                        contactsWOutput.setText( "Pass" );CONTACTS_WRITE=true;contactsWOutput.setTextColor( Color.GREEN );
                    }else{
                        contactsWOutput.setText( "Fail" );CONTACTS_WRITE=false;contactsWOutput.setTextColor( Color.RED );
                    }
                    //force stop the process
                    MainActivity.exeCmd( "m force-stop com.google.android.gm" );

                } catch (IOException e) {
                    e.printStackTrace();
                }

                RESULT = DEFAULT&&PRELOAD&&CALENDAR_WRITE&&CALENDAR_READ&&CONTACTS_WRITE&&CONTACTS_READ;
            }
        } );





    }
}
