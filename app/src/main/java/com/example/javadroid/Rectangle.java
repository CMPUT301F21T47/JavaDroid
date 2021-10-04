package com.example.javadroid;

public class Rectangle {
    private int base;
    private int height;

    public Rectangle(int base, int height){
        this.base=base;
        this.height=height;
    }

    public void setBase(int base){
        this.base = base;
    }

    public int getBase(int base){
        return base;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public int getHeight(int height){
        return height;
    }

    public int area(int base, int height){
        return base*height;
    }

}
