package com.example.gms_check;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

    List<Item> itemList;

    public void fingerPrinter() throws IOException {
        String brand = null,productName=null,productDevice=null,buildVersionRelease=null,buildId=null,
                buildVersionIncrementalValue=null,buildType=null,buildTags=null;
        String fingerPrinter = exeCmd("getprop ro.build.fingerprint").toString().replaceAll("\n","");
        brand = exeCmd("getprop ro.product.brand").toString().replaceAll("\n","");
        productName = exeCmd("getprop ro.product.name").toString().replaceAll("\n","");
        productDevice = exeCmd("getprop ro.product.device").toString().replaceAll("\n","");
        buildVersionRelease = exeCmd("getprop ro.build.version.release").toString().replaceAll("\n","");
        buildId = exeCmd("getprop ro.build.id").toString().replaceAll("\n","");
        buildVersionIncrementalValue = exeCmd("getprop ro.build.version.incremental").toString().replaceAll("\n","");
        buildType = exeCmd("getprop ro.build.type").toString().replaceAll("\n","");
        buildTags = exeCmd("getprop ro.build.tags").toString().replaceAll("\n","");
        StringBuilder sb = new StringBuilder();
        sb.append(brand).append("/").append(productName).append("/").append(productDevice).append(":").append(buildVersionRelease)
                .append("/").append(buildId).append("/").append(buildVersionIncrementalValue).append(":").append(buildType).append("/")
                .append(buildTags);

        for(int i =0;i<itemList.size();i++){
            if(itemList.get(i).getName().equals("Fingerprinter")){
                if(sb.toString().equals(fingerPrinter)){
                    itemList.get(i).setState("Pass");
                }else {
                    itemList.get(i).setState("Fail");
                }
            }
        }
    }

    public void notchCheck() throws IOException {

        String cmd = "pm list features | grep camera_notch";

        String notch = exeCmd(cmd).toString().replaceAll("\n","");
        for(int i =0;i<itemList.size();i++){
            if(itemList.get(i).getName().equals("Notch_check")){
                if (!notch.isEmpty()){
                    itemList.get(i).setState("Pass");
                }else{
                    itemList.get(i).setState("Notch is not set");
                }
            }
        }
    }

    public void relevantVersionCheck() throws IOException {
        boolean gms,securityPatch,firstAPILevel,result;
        gms = !exeCmd( "getprop ro.com.google.gmsversion" ).toString().equals( "\n" );
        securityPatch= !exeCmd( "getprop ro.build.version.security_patch" ).toString().equals( "\n" );
        firstAPILevel= !exeCmd( "getprop ro.product.first_api_level" ).toString().equals( "\n" );
        result=gms&&securityPatch&&firstAPILevel;
        for(int i =0;i<itemList.size();i++){
            if(itemList.get(i).getName().equals("Relevant_version_check")){
                if (!result){
                    itemList.get(i).setState("Fail");
                }else{
                    itemList.get(i).setState("Pass");
                }
            }
        }
    }

    public void sdkVersionCheck() throws IOException {
        Queue<Integer> queue = new LinkedList<>();
        String str = "dumpsys package | grep -n -E \"^Package|targetSdk\"";
        String targetSdk = exeCmd(str).toString();
        Matcher m = Pattern.compile("targetSdk=(\\d*)").matcher(targetSdk);
        while(m.find()){
            queue.add(Integer.parseInt(m.group(1)));
        }
        for(int i =0;i<itemList.size();i++){
            if(itemList.get(i).getName().equals("SDK_version_check")){
                for (Integer x : queue){
                    if (x<28){
                        itemList.get(i).setState("Fail");
                        return;
                    }
                }
                itemList.get(i).setState("Pass");
            }
        }



    }


    public static StringBuilder exeCmd(String commandStr) throws IOException {
        String[] command = { "/bin/sh", "-c", commandStr };
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb;
    }


    private String[] readxmlGMS() throws ParserConfigurationException, IOException, SAXException {
        Queue<String> queue = new LinkedList<String>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(getAssets().open("config.xml"));
        NodeList list = doc.getElementsByTagName("App");
        queue.add("App");
        NodeList listOfOtherItems = doc.getElementsByTagName("GMS");
        for (int i = 0; i < list.getLength(); ++i){
            Element element = (Element) listOfOtherItems.item(i);
            if (element.getElementsByTagName("Fingerprinter").item(0)
                .getFirstChild().getNodeValue().equals("on")){
                queue.add("Fingerprinter");
            }
            if (element.getElementsByTagName("Relevant_version_check").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Relevant_version_check");
            }
            if (element.getElementsByTagName("Notch_check").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Notch_check");
            }
            if (element.getElementsByTagName("SDK_version_check").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("SDK_version_check");
            }
        }
        queue.add("GTS_AssistantTest");
        String[] item = new String[queue.size()];
        int i =0;
        while(!queue.isEmpty()){
            item[i] = queue.peek();
            queue.remove();
            i++;

        }
    return item;}

    public static List<Item> initItem(String[] itemlist){
        List<Item> myitemList = new ArrayList<>();
        for ( int i =0; i < itemlist.length;i++){
            Item newItem  = new Item();
            newItem.setName(itemlist[i]);
            newItem.setPosition(i);
            newItem.setState(null);
            myitemList.add(newItem);
        }
        return myitemList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] item = new String[0];
        try {
            item = readxmlGMS();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        listView = findViewById(R.id.listView);
        itemList= initItem(item);
        LinearLayout myLayout = findViewById(R.id.appmain);
        myLayout.setBackgroundColor( Color.GRAY);
        myListAdapter mMylistAdapter = new myListAdapter(itemList,this);
        listView.setAdapter(mMylistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i =0;i < itemList.size();i++){
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("App")){
                        Intent intent=new Intent(MainActivity.this,AppActivity.class);
                        startActivity(intent);
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("GTS_AssistantTest")){
                        Intent intent=new Intent(MainActivity.this,GTS_Activity.class);
                        startActivity(intent);
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Fingerprinter")){
                        Intent intent=new Intent(MainActivity.this,FingerprinterActivity.class);
                        startActivityForResult(intent,1);
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Relevant_version_check")){
                        Intent intent=new Intent(MainActivity.this,relavantVersionCheck.class);
                        startActivityForResult(intent,1);
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Notch_check")){
                        try {
                            String cmd = "pm list features | grep camera_notch";
                            String notch = exeCmd(cmd).toString().replaceAll("\n","");
                        if (!notch.isEmpty()){
                            itemList.get(i).setState("Pass");
                        }else{
                            itemList.get(i).setState("Notch is not set");
                        }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (itemList.get(i).getPosition()==position && itemList.get(i).getName().equals("SDK_version_check")){
                        Queue<Integer> queue = new LinkedList<>();
                        String str = "dumpsys package | grep -n -E \"^Package|targetSdk\"";
                        try {
                            String targetSdk = exeCmd(str).toString();
                            Matcher m = Pattern.compile("targetSdk=(\\d*)").matcher(targetSdk);
                            while(m.find()){
                                queue.add(Integer.parseInt(m.group(1)));
                            }
                            for (Integer x : queue){
                                 if (x<28){
                                     itemList.get(i).setState("Fail");
                                     myListAdapter mMylistAdapter = new myListAdapter(itemList,MainActivity.this);
                                     listView.setAdapter(mMylistAdapter);
                                     return;}
                            }
                            itemList.get(i).setState("Pass");

                        } catch (IOException e) {
                            e.printStackTrace();
                            e.getMessage();
                        }
                    }
                } myListAdapter mMylistAdapter = new myListAdapter(itemList,MainActivity.this);
                listView.setAdapter(mMylistAdapter);
            }
            });
        Button clear = findViewById(R.id.clear);
        Button reboot = findViewById(R.id.reboot);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0;i<itemList.size();i++){
                    itemList.get(i).setState(null);
                }
                myListAdapter mMylistAdapter = new myListAdapter(itemList,MainActivity.this);
                listView.setAdapter(mMylistAdapter);
            }
        });

        reboot.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reboot = new Intent(Intent.ACTION_REBOOT);
                reboot.putExtra("nowait", 1);
                reboot.putExtra("interval", 1);
                reboot.putExtra("window", 0);
                sendBroadcast(reboot);
            }
        } );
        Button autoTest = findViewById( R.id.autoTestMain);
        autoTest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fingerPrinter();
                    //2
                    notchCheck();
                    //3
                    relevantVersionCheck();
                    //4
                    sdkVersionCheck();
                    myListAdapter mMylistAdapter = new myListAdapter(itemList,MainActivity.this);
                    listView.setAdapter(mMylistAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ListView listView = findViewById(R.id.listView);
        if (requestCode == 1 && resultCode == 3) {
            boolean result = data.getBooleanExtra("result",true);
                for(int i =0;i<itemList.size();i++){
                    if(itemList.get(i).getName().equals("Relevant_version_check")){
                        if(result){
                            itemList.get(i).setState("Pass");
                        }else {
                            itemList.get(i).setState("Fail");
                        }
                    }
            }
        }
        if (requestCode == 1 && resultCode == 4) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Fingerprinter")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        myListAdapter mMylistAdapter = new myListAdapter(itemList,this);
        listView.setAdapter(mMylistAdapter);
    }











}



