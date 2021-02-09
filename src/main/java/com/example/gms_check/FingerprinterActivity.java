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
import java.util.ArrayList;
import java.util.List;

public class FingerprinterActivity extends Activity {
    public static boolean RESULT;
    String fingerPrinter ;


    public static StringBuilder exeCmd(String commandStr) throws IOException {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprinter);
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.fingerprinter);
        myLayout.setBackgroundColor( Color.GRAY);
        Button test = (Button) findViewById(R.id.test_finger);
        try {
            fingerPrinter = exeCmd("getprop ro.build.fingerprint").toString().replaceAll("\n","");
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView fingerPrinterOutput = (TextView) findViewById(R.id.fingerPrinterOutput);
        final TextView result = (TextView) findViewById(R.id.resultOutput_finger);
        Button back = (Button) findViewById(R.id.back_finger);
        fingerPrinterOutput.setText(fingerPrinter);
        fingerPrinterOutput.setTextColor( Color.WHITE );
        test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String brand = null,productName=null,productDevice=null,buildVersionRelease=null,buildId=null,
                        buildVersionIncrementalValue=null,buildType=null,buildTags=null;
                try {
                    brand = exeCmd("getprop ro.product.brand").toString().replaceAll("\n","");
                    productName = exeCmd("getprop ro.product.name").toString().replaceAll("\n","");;
                    productDevice = exeCmd("getprop ro.product.device").toString().replaceAll("\n","");
                    buildVersionRelease = exeCmd("getprop ro.build.version.release").toString().replaceAll("\n","");
                    buildId = exeCmd("getprop ro.build.id").toString().replaceAll("\n","");
                    buildVersionIncrementalValue = exeCmd("getprop ro.build.version.incremental").toString().replaceAll("\n","");
                    buildType = exeCmd("getprop ro.build.type").toString().replaceAll("\n","");
                    buildTags = exeCmd("getprop ro.build.tags").toString().replaceAll("\n","");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(brand).append("/").append(productName).append("/").append(productDevice).append(":").append(buildVersionRelease)
                        .append("/").append(buildId).append("/").append(buildVersionIncrementalValue).append(":").append(buildType).append("/")
                        .append(buildTags);
                if (sb.toString().equals(fingerPrinter)){
                    result.setText("PASS");
                    result.setTextColor( Color.GREEN );
                }else{
                    result.setText("FAIL");
                    result.setTextColor( Color.RED );
                }
                RESULT = sb.toString().equals(fingerPrinter);
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.putExtra("result", RESULT);
                setResult(4, i);
                finish();
            }
        });
    }
}
