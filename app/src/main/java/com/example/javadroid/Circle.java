package com.example.javadroid;

public class Circle extends Shape{
    private int radius;

    public Circle(int radius){
        this.radius = radius;
    }

    @Override
    public int area(int radius) {
        return (radius * 3.1415 * radius)
    }
}