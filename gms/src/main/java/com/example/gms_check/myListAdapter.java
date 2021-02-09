package com.example.gms_check;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class myListAdapter extends BaseAdapter {
    private List<Item> item_list;
    private LayoutInflater inflater;

    public  myListAdapter (List<Item> item_list, Context context) {
        this.item_list = item_list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return item_list==null?0:item_list.size();
    }

    @Override
    public Object getItem(int position) {
        return item_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.listview_item, null);
        Item itemData = (Item) getItem(position);
        TextView itemName = (TextView) view.findViewById(R.id.itemName);
        TextView itemState = (TextView) view.findViewById(R.id.stateName);
        itemName.setText(itemData.getName());
        itemState.setText(itemData.getState());
        itemName.setTextColor( Color.parseColor("#F5F5DC"));
        if(itemState.getText().equals( "Pass" )){
            itemState.setTextColor( Color.GREEN );
        }
        else{
            itemState.setTextColor( Color.RED );
        }
        return view;


    }
}
