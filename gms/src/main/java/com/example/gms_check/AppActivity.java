package com.example.gms_check;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AppActivity extends Activity {
    List<Item> itemList;
    private String[] readxmlAPP() throws ParserConfigurationException, IOException, SAXException {
        Queue<String> queue = new LinkedList<String>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(getAssets().open("config.xml"));
        NodeList list = doc.getElementsByTagName("App");
        for (int i = 0; i < list.getLength(); ++i)
        {
            Element element = (Element) list.item(i);

            if (element.getElementsByTagName("Chrome").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Chrome");
            }
            if (element.getElementsByTagName("Gallery").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Gallery");
            }
            if (element.getElementsByTagName("Messaging").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Messaging");
            }
            if (element.getElementsByTagName("Calendar").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Calendar");
            }
            if (element.getElementsByTagName("Email").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Email");
            }
            if (element.getElementsByTagName("Keyboard").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Keyboard");
            }
            if (element.getElementsByTagName("Voice_assistant").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Voice_assistant");
            }
            if (element.getElementsByTagName("Search").item(0)
                    .getFirstChild().getNodeValue().equals("on")){
                queue.add("Search");
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
    protected void onCreate(Bundle savedInstanceState) {
        final ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.main_app);
        myLayout.setBackgroundColor( Color.GRAY);
        String[] app = new String[0];
        try {
            app = readxmlAPP();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        listView = findViewById(R.id.appView);
        itemList = MainActivity.initItem(app);
        myListAdapter mMylistAdapter = new myListAdapter(itemList,this);
        listView.setAdapter(mMylistAdapter);
        final Button findBotton = (Button) findViewById(R.id.back_app);
        findBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i =0;i < itemList.size();i++){
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Chrome")){
                        Intent intent=new Intent(AppActivity.this,chrome.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Gallery")){
                        Intent intent=new Intent(AppActivity.this,gallery.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Messaging")){
                        Intent intent=new Intent(AppActivity.this,message.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Calendar")){
                        Intent intent=new Intent(AppActivity.this,calendar.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Email")){
                        Intent intent=new Intent(AppActivity.this,email.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Keyboard")){
                        Intent intent=new Intent(AppActivity.this,gboard.class);
                        startActivityForResult( intent,2 );
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Voice_assistant")){
                        try {
                            String uniqueness="am start -W -a android.intent.action.VOICE_COMMAND | grep -c com.google.android.googlequicksearchbox";
                            int uniquenessResult = Integer.parseInt( MainActivity.exeCmd( uniqueness ).toString().replaceAll( "\n","" ) );
                            if (uniquenessResult==1){
                                itemList.get( i ).setState( "Pass" );
                            }else{
                                itemList.get( i ).setState( "Fail" );
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (position == itemList.get(i).getPosition() && itemList.get(i).getName().equals("Search")){
                        Intent intent=new Intent(AppActivity.this,search.class);
                        startActivityForResult( intent,2 );
                    }
                }myListAdapter mMylistAdapter = new myListAdapter(itemList,AppActivity.this);
                listView.setAdapter(mMylistAdapter);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ListView listView = findViewById(R.id.appView);
        if (requestCode == 2 && resultCode == 4) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Chrome")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }

                }
            }
        }
        if (requestCode == 2 && resultCode == 5) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Gallery")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        if (requestCode == 2 && resultCode == 6) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Messaging")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        if (requestCode == 2 && resultCode == 7) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Calendar")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        if (requestCode == 2 && resultCode == 8) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Email")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        if (requestCode == 2 && resultCode == 9) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Keyboard")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }if (requestCode == 2 && resultCode == 10) {
            boolean result = data.getBooleanExtra("result",true);
            for(int i =0;i<itemList.size();i++){
                if(itemList.get(i).getName().equals("Search")){
                    if(result){
                        itemList.get(i).setState("Pass");
                    }else {
                        itemList.get(i).setState("Fail");
                    }
                }
            }
        }
        myListAdapter mMylistAdapter = new myListAdapter(itemList,AppActivity.this);
        listView.setAdapter(mMylistAdapter);
    }




}
