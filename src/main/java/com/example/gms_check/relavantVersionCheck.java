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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class relavantVersionCheck extends Activity {
    public static boolean GMS, SECURITY_PATCH, FIRST_LEVEL_API,RESULT;
    public static <BufferReader> StringBuilder exeCmd(String commandStr) throws IOException {
        Process p = Runtime.getRuntime().exec(commandStr);
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relevant_version_check);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.rvc);
        myLayout.setBackgroundColor( Color.GRAY);
        final TextView gmsOUtput = (TextView) findViewById(R.id.gmsVersionOutput);
        final TextView securityPatchOutput = (TextView) findViewById(R.id.securityPatchOutput);
        final TextView firstAPILevelOutput = (TextView) findViewById(R.id.firstAPILevelOutput);
        Button back = (Button) findViewById(R.id.back_re);
        Button test = (Button) findViewById(R.id.test_re);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(exeCmd("getprop ro.com.google.gmsversion").toString().equals("\n")){
                        gmsOUtput.setText("None");gmsOUtput.setTextColor( Color.RED );
                        GMS = false;
                    }else {
                        gmsOUtput.setText(relavantVersionCheck.exeCmd("getprop ro.com.google.gmsversion"));
                        GMS = true;
                        gmsOUtput.setTextColor( Color.GREEN );
                    }

                    if(exeCmd("getprop ro.build.version.security_patch").toString().equals("\n")){
                        securityPatchOutput.setText("None");securityPatchOutput.setTextColor( Color.RED );
                        SECURITY_PATCH = false;
                    }else {
                        securityPatchOutput.setText(relavantVersionCheck.exeCmd("getprop ro.build.version.security_patch"));
                        SECURITY_PATCH = true;securityPatchOutput.setTextColor( Color.GREEN );
                    }
                    if(exeCmd("getprop ro.product.first_api_level").toString().equals("\n")){
                        firstAPILevelOutput.setText("None");firstAPILevelOutput.setTextColor( Color.RED );
                        FIRST_LEVEL_API = false;
                    }else {
                        firstAPILevelOutput.setText(relavantVersionCheck.exeCmd("getprop ro.product.first_api_level"));
                        FIRST_LEVEL_API = true;firstAPILevelOutput.setTextColor( Color.GREEN );
                    }
                    RESULT = GMS&&SECURITY_PATCH&&FIRST_LEVEL_API;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(3, i);
                finish();
            }
        });
    }
}
