package com.example.habitshare;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HabitTest {
    @Test
    public void testGetSelectedDayOfWeek(){
        Habit habit = new Habit("Habit name", "2021-02-02");
        habit.selectDayOfWeek(0);
        assertEquals("Monday", habit.getSelectDayOfWeek());

        habit.selectDayOfWeek(5);
        assertEquals("Monday, Saturday", habit.getSelectDayOfWeek());

        habit.selectDayOfWeek(3);
        assertEquals("Monday, Thursday, Saturday", habit.getSelectDayOfWeek());
    }

    @Test
    public void testConstructor2(){
        Habit habit = new Habit("Habit name", "2021-02-02"," ", "Monday, Thursday, Saturday");
        boolean[] daysOfWeekList = habit.getSelectDayOfWeekList();
        assertTrue(daysOfWeekList[0]);
        assertTrue(daysOfWeekList[3]);
        assertTrue(daysOfWeekList[5]);
    }

}
