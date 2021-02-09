package com.example.gms_check;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



public class GTS_Activity extends Activity {
    private static final int DEFAULT_SLEEP_MS = 0x5dc;
    private static final String FRONT_CAMERA = "front";
    private static final String REAR_CAMERA = "back";
    private int mImgCountBeforeTesting = 0x0;
;
    private Map<String, String> mCameraIdMapping = new HashMap();
    List<Item> itemList;

    private Map<String, String> getCameraIdMapping() throws IOException {
        Matcher m = Pattern.compile("device@[\\d.]+/[\\w]+/(\\d+) [v\\d.()]* static.*?Facing: (Front|Back)").matcher(MainActivity.exeCmd( "dumpsys media.camera" ).toString().replaceAll("\n", ""));
        this.mCameraIdMapping.clear();
        while (m.find()) {
            this.mCameraIdMapping.put(m.group(1), m.group(2));
        }
        return this.mCameraIdMapping;
    }

    private String getActiveCameraFace() throws IOException {
        Matcher m = Pattern.compile("Camera ID: (\\d)").matcher(MainActivity.exeCmd( "dumpsys media.camera" ).toString().replaceAll( "\n","" ));
        String cameraId = "";
        while (m.find()) {
            cameraId = m.group(1);
        }
        return (this.mCameraIdMapping.get(cameraId) == null || this.mCameraIdMapping.get(cameraId).isEmpty()) ? "" : this.mCameraIdMapping.get(cameraId).toLowerCase();
    }


    private int getCameraDirectoryJpegAmount()  {
        try {
            return Integer.parseInt( MainActivity.exeCmd( "find $EXTERNAL_STORAGE/ -size +100k | grep -i '\\.jpg' | wc -l" ).toString().trim() );
        } catch (NumberFormatException | IOException e) {
            return 0;
        }
    }



    private String[] readxmlGTS() throws ParserConfigurationException, IOException, SAXException {
        Queue<String> queue = new LinkedList<String>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(getAssets().open("config.xml"));
        NodeList listOfGTS = doc.getElementsByTagName("GTS_AssistantTest");
        for (int i = 0; i < listOfGTS.getLength(); ++i){
            Element element = (Element) listOfGTS.item(i);
            if (element.getElementsByTagName("testAssistantOpenFrontCameraWithoutVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("FrontCameraWithoutVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantOpenRearCameraWithoutVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("RearCameraWithoutVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantTakePhotoWithoutVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("TakePhotoWithoutVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantTakeSelfieWithoutVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("TakeSelfieWithoutVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantOpenRearCameraWithVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("RearCameraWithVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantOpenFrontCameraWithVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("FrontCameraWithVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantTakePhotoWithVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("TakePhotoWithVoiceInteraction");
            }
            if (element.getElementsByTagName("testAssistantTakeSelfieWithVoiceInteraction").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("TakeSelfieWithVoiceInteraction");
            }
        }
        String[] item = new String[queue.size()];
        int i =0;
        while(!queue.isEmpty()){
            item[i] = queue.peek();
            queue.remove();
            i++;

        }
        return item;
    }
    public void install(){
        try {
            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //CASE
    private void FrontCameraWithoutVoiceInteraction(int i){
        String line = "am start -a android.media.action.STILL_IMAGE_CAMERA --ez com.google.assistant.extra.USE_FRONT_CAMERA true --ez android.intent.extra.USE_FRONT_CAMERA true --ez com.google.assistant.extra.CAMERA_OPEN_ONLY true --ez android.intent.extra.CAMERA_OPEN_ONLY true --ez isVoiceQuery true --ez NoUiQuery true --es android.intent.extra.REFERRER_NAME android-app://com.google.android.googlequicksearchbox/https/www.google.com --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

            if(getActiveCameraFace().equals(FRONT_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void RearCameraWithoutVoiceInteraction(int i){
        String line = "am start -a android.media.action.STILL_IMAGE_CAMERA --ez com.google.assistant.extra.USE_REAR_CAMERA true --ez android.intent.extra.USE_REAR_CAMERA true --ez com.google.assistant.extra.CAMERA_OPEN_ONLY true --ez android.intent.extra.CAMERA_OPEN_ONLY true --ez isVoiceQuery true --ez NoUiQuery true --es android.intent.extra.REFERRER_NAME android-app://com.google.android.googlequicksearchbox/https/www.google.com --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

            if(getActiveCameraFace().equals(REAR_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void TakePhotoWithoutVoiceInteraction(int i){
        String line = "am start -a android.media.action.STILL_IMAGE_CAMERA --ez isVoiceQuery true --ez NoUiQuery true --es android.intent.extra.REFERRER_NAME android-app://com.google.android.googlequicksearchbox/https/www.google.com --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
            if(getActiveCameraFace().equals(REAR_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void TakeSelfieWithoutVoiceInteraction(int i){
        String line = "am start -a android.media.action.STILL_IMAGE_CAMERA --ez  com.google.assistant.extra.USE_FRONT_CAMERA true --ez android.intent.extra.USE_FRONT_CAMERA true --ez  isVoiceQuery true  --ez  NoUiQuery true --es android.intent.extra.REFERRER_NAME android-app://com.google.android.googlequicksearchbox/https/www.google.com --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
            if(getActiveCameraFace().equals(FRONT_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void RearCameraWithVoiceInteraction(int i){
        install();
        String line = "am start -a android.intent.action.VOICE_INTERACTION_GTS_TEST --es  Testcase_type OPEN_REAR_CAMERA --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );
            if(getActiveCameraFace().equals(REAR_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void FrontCameraWithVoiceInteraction(int i){
        install();
        String line = "am start -a android.intent.action.VOICE_INTERACTION_GTS_TEST --es  Testcase_type OPEN_FRONT_CAMERA --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

            if(getActiveCameraFace().equals(FRONT_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void TakePhotoWithVoiceInteraction(int i){
        install();
        String line = "am start -a android.intent.action.VOICE_INTERACTION_GTS_TEST --es  Testcase_type TAKE_PHOTO --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

            if(getActiveCameraFace().equals(REAR_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void TakeSelfieWithVoiceInteraction(int i){
        install();
        String line = "am start -a android.intent.action.VOICE_INTERACTION_GTS_TEST --es  Testcase_type TAKE_SELFIE --activity-clear-task";
        try {
            MainActivity.exeCmd( line );
            Thread.sleep( DEFAULT_SLEEP_MS );
            MainActivity.exeCmd( "am start com.example.gms_check/com.example.gms_check.MainActivity" );

            if(getActiveCameraFace().equals(FRONT_CAMERA) && getCameraDirectoryJpegAmount() == mImgCountBeforeTesting){
                itemList.get( i ).setState( "Pass" );
            }else{
                itemList.get( i ).setState( "Fail" );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        try {
            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ListView listView ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gts);
        install();
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Run Apk Detector to install apk")
                .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        String[] app = new String[0];
        this.mImgCountBeforeTesting = getCameraDirectoryJpegAmount();
        listView = findViewById(R.id.gtsView);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.main_gts);
        myLayout.setBackgroundColor( Color.GRAY);
        try{
            getCameraIdMapping();
            app = readxmlGTS();
        itemList = MainActivity.initItem(app);
        myListAdapter mMylistAdapter = new myListAdapter(itemList,this);
        listView.setAdapter(mMylistAdapter);

        Button findBotton = (Button) findViewById(R.id.back_gts);
        findBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(GTS_Activity.this)
                        .setTitle("Warning")
                        .setMessage("Rerun detector to uninstall Apk")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .show();

            }
        });
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i =0;i < itemList.size();i++){
                    if(position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("FrontCameraWithoutVoiceInteraction")){
                        FrontCameraWithoutVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("RearCameraWithoutVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        RearCameraWithoutVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("TakePhotoWithoutVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        TakePhotoWithoutVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("TakeSelfieWithoutVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        TakeSelfieWithoutVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("RearCameraWithVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }RearCameraWithVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("FrontCameraWithVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }FrontCameraWithVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("TakePhotoWithVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }TakePhotoWithVoiceInteraction(i);
                    }
                    if(itemList.get( i ).getName().equals("TakeSelfieWithVoiceInteraction")&&itemList.get( i ).getPosition()==position){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }TakeSelfieWithVoiceInteraction(i);
                    }
                }myListAdapter mMylistAdapter = new myListAdapter(itemList,GTS_Activity.this);
                listView.setAdapter(mMylistAdapter);
            }

        } );
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        Button autoTest = (Button)findViewById( R.id.auto_gts );
        autoTest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0;i<itemList.size();i++){
                    if(itemList.get(i).getName().equals( "FrontCameraWithoutVoiceInteraction" )){
                        FrontCameraWithoutVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "RearCameraWithoutVoiceInteraction" )){
                        RearCameraWithoutVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "TakePhotoWithoutVoiceInteraction" )){
                        TakePhotoWithoutVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "TakeSelfieWithoutVoiceInteraction" )){
                        TakeSelfieWithoutVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "RearCameraWithVoiceInteraction" )){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }RearCameraWithVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "FrontCameraWithVoiceInteraction" )){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }FrontCameraWithVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "TakePhotoWithVoiceInteraction" )){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }TakePhotoWithVoiceInteraction(i);
                    }
                    if(itemList.get(i).getName().equals( "TakeSelfieWithVoiceInteraction" )){
                        try {
                            MainActivity.exeCmd( "settings put secure voice_interaction_service android.voicesettings.service/.MainInteractionService" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        TakeSelfieWithVoiceInteraction(i);
                    }
                }myListAdapter mMylistAdapter = new myListAdapter(itemList,GTS_Activity.this);
                listView.setAdapter(mMylistAdapter);

            }
        } );

        Button reset = (Button)findViewById( R.id.reset_gts );
        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0;i<itemList.size();i++){
                    itemList.get(i).setState(null);
                }
                myListAdapter mMylistAdapter = new myListAdapter(itemList,GTS_Activity.this);
                listView.setAdapter(mMylistAdapter);
            }
        } );

    }

}
