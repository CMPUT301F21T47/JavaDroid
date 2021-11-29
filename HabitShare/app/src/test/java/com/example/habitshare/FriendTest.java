package com.example.habitshare;

import static org.junit.Assert.*;

import org.junit.*;

public class FriendTest {
    @Test
    public void testGetUserName(){
        Friend friend = new Friend("123", "123@123.com");
        assertEquals("123", friend.getUserName());

        friend = new Friend("Tianxiang", "123@123.com");
        assertEquals("Tianxiang", friend.getUserName());

        friend = new Friend("Sky Walker", "123@123.com");
        assertEquals("Sky Walker", friend.getUserName());
    }

    @Test
    public void testGetEmail(){
        Friend friend = new Friend("123", "123@123.com");
        assertEquals("123@123.com", friend.getEmail());

        friend = new Friend("123", "tianxia3@ualberta.ca");
        assertEquals("tianxia3@ualberta.ca", friend.getEmail());

        friend = new Friend("123", "644120622@qq.com");
        assertEquals("644120622@qq.com", friend.getEmail());
    }
}
