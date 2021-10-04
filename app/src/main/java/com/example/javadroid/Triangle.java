package com.example.javadroid;

public class Triangle extends Shape{
    private int base;
    private int height;

    public Triangle(int base, int height){
        this.base = base;
        this.height = height;
    }

    @Override
    public int area(int base, int height) {
        return (base * height) / 2;
    }
}
