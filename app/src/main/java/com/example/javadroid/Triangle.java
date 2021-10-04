package com.example.javadroid;

public class Triangle extends Shape{
    // This is a triangle
    private int base;
    private int height;

    public Triangle(int base, int height){
        this.base = base;
        this.height = height;
    }

    public int area(int base, int height) {
        return (base * height) / 2;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
