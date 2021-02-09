package com.example.gms_check;

public class Item {
    private String name;
    private int position;
    private String state;

    public String getName(){
        return name;
    }

    public int getPosition(){
        return position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public String getState(){ return state;}

    public void setState(String state) {
        this.state = state;
    }


}
