package com.example.habitshare;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class HabitTest {
    @Test
    public void testSelectDayOfWeek(){
        Habit habit = new Habit("Habit name", "2021-02-02");
        // set a date from empty
        habit.selectDayOfWeek(0);
        assertEquals("Sunday", habit.getSelectDayOfWeek());

        // insert friday
        habit.selectDayOfWeek(5);
        assertEquals("Sunday, Friday", habit.getSelectDayOfWeek());

        // insert wednesday between sunday and friday
        habit.selectDayOfWeek(3);
        assertEquals("Sunday, Wednesday, Friday", habit.getSelectDayOfWeek());
    }

    @Test
    public void testGetSelectDayOfWeekList(){
        Habit habit = new Habit("Habit name", "2021-02-02", "No reason", "Monday");
        for(int i = 0; i < 6; i++){
            if(i != 1){
                assertFalse(habit.getSelectDayOfWeekList()[i]);
            }
            else{
                assertTrue(habit.getSelectDayOfWeekList()[i]);
            }
        }

        habit = new Habit("Habit name", "2021-02-02", "No reason", "Monday, Wednesday");
        for(int i = 0; i < 6; i++){
            if(i != 1 & i != 3){
                assertFalse(habit.getSelectDayOfWeekList()[i]);
            }
            else{
                assertTrue(habit.getSelectDayOfWeekList()[i]);
            }
        }

        habit = new Habit("Habit name", "2021-02-02", "No reason", "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday");
        for(int i = 0; i < 6; i++){
            assertTrue(habit.getSelectDayOfWeekList()[i]);
        }
    }

    @Test
    public void testGetAndSetStatus(){
        Habit habit = new Habit("Habit name", "2021-02-02");
        // the default status should be false
        assertFalse(habit.getStatus());

        // set to true
        habit.setStatus(true);
        assertTrue(habit.getStatus());

        // set to false again
        habit.setStatus(false);
        assertFalse(habit.getStatus());
    }

    @Test
    public void testGetAndSetDate(){
        Habit habit = new Habit("Habit name", "2021-02-02");

        // the date is set to be 2021-02-01 by the constructor
        assertEquals("2021-02-02", habit.getDate());

        // set date to an empty string
        habit.setDate("");
        assertEquals("", habit.getDate());

        // set date to another date
        habit.setDate("1989-06-04");
        assertEquals("1989-06-04", habit.getDate());
    }

    @Test
    public void testGetAndSetTitle(){
        Habit habit = new Habit("Habit name", "2021-02-02");

        // the title is set to be "Habit Name" by the constructor
        assertEquals("Habit name", habit.getTitle());

        // set the title to an empty string
        habit.setTitle("");
        assertEquals("", habit.getTitle());

        // set the title to be "Study"
        habit.setTitle("Study");
        assertEquals("Study", habit.getTitle());
    }

    @Test
    public void testGetAndSetIsDisclosed(){
        Habit habit = new Habit("Habit name", "2021-02-02");

        // set private
        habit.setPrivate();
        assertFalse(habit.getIsDisclosed());

        // set public
        habit.setPublic();
        assertTrue(habit.getIsDisclosed());

        // set to private again
        habit.setPrivate();
        assertFalse(habit.getIsDisclosed());

        // set to public again
        habit.setPublic();
        assertTrue(habit.getIsDisclosed());
    }

    @Test
    public void testGetAndSetNumberOfTimesActuallyDenoted(){
        Habit habit = new Habit("Habit name", "2021-02-02");

        // default is 0
        assertEquals(0, habit.getNumberOfTimesActuallyDenoted());

        habit.setNumberOfTimesActuallyDenoted(1);
        assertEquals(1, habit.getNumberOfTimesActuallyDenoted());

        habit.setNumberOfTimesActuallyDenoted(2);
        assertEquals(2, habit.getNumberOfTimesActuallyDenoted());

        habit.setNumberOfTimesActuallyDenoted(100);
        assertEquals(100, habit.getNumberOfTimesActuallyDenoted());

        habit.setNumberOfTimesActuallyDenoted(1000000);
        assertEquals(1000000, habit.getNumberOfTimesActuallyDenoted());
    }
}
