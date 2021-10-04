package com.example.javadroid;

public class Pentagon extends Shape{

    private int side;

    public Pentagon(int side){
        this.side = side;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
