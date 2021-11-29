package com.example.habitshare;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HabitEventTest {
    @Test
    public void testGetAndSetComment(){
        HabitEvent habitEvent = new HabitEvent("Habit name", "2021-11-29");

        assertNull(habitEvent.getComment());

        habitEvent.setComment("123456");
        assertEquals("123456", habitEvent.getComment());

        habitEvent.setComment("");
        assertEquals("", habitEvent.getComment());

        habitEvent.setComment("No Comment");
        assertEquals("No Comment", habitEvent.getComment());
    }

    @Test
    public void testGetDenoteDate(){
        HabitEvent habitEvent = new HabitEvent("Habit name", "2021-11-29");
        assertEquals("2021-11-29", habitEvent.getDenoteDate());

        habitEvent = new HabitEvent("Habit name", "1899-11-29");
        assertEquals("1899-11-29", habitEvent.getDenoteDate());

        habitEvent = new HabitEvent("Habit name", "1899-10-29");
        assertEquals("1899-10-29", habitEvent.getDenoteDate());
    }

    @Test
    public void testGetAndSetEventTitle(){
        HabitEvent habitEvent = new HabitEvent("Habit name", "2021-11-29");

        habitEvent.setEventTitle("event title");
        assertEquals("event title", habitEvent.getEventTitle());

        habitEvent.setEventTitle("adasdd");
        assertEquals("adasdd", habitEvent.getEventTitle());

        habitEvent.setEventTitle("123123");
        assertEquals("123123", habitEvent.getEventTitle());
    }

    @Test
    public void testGetAndSetHasImage(){
        HabitEvent habitEvent = new HabitEvent("Habit name", "2021-11-29");

        assertFalse(habitEvent.isHasImage());

        habitEvent.setHasImage(true);
        assertTrue(habitEvent.isHasImage());

        habitEvent.setHasImage(false);
        assertFalse(habitEvent.isHasImage());

        habitEvent.setHasImage(true);
        assertTrue(habitEvent.isHasImage());

        habitEvent.setHasImage(false);
        assertFalse(habitEvent.isHasImage());
    }

    @Test
    public void testGetAndSetLocation(){
        HabitEvent habitEvent = new HabitEvent("Habit name", "2021-11-29");

        habitEvent.setLocation("11225 89 Ave NW #11203, Edmonton, AB");
        assertEquals("11225 89 Ave NW #11203, Edmonton, AB", habitEvent.getLocation());

        habitEvent.setLocation("8225 112 St NW, Edmonton, AB T6G 2R8");
        assertEquals("8225 112 St NW, Edmonton, AB T6G 2R8", habitEvent.getLocation());

        habitEvent.setLocation("10025 102A Ave NW, Edmonton, AB T5J 2Z2");
        assertEquals("10025 102A Ave NW, Edmonton, AB T5J 2Z2", habitEvent.getLocation());
    }
}
