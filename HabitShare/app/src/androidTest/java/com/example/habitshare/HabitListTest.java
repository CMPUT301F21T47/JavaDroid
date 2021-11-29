package com.example.habitshare;

import static org.junit.Assert.assertTrue;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitListTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> ruleMainActivity = new ActivityTestRule<>(MainActivity.class, true, true);
    public ActivityTestRule<LoginActivity> ruleLoginActivity = new ActivityTestRule<>(LoginActivity.class, true, true);
    public ActivityTestRule<AddHabitActivity> ruleAddHabitActivity = new ActivityTestRule<>(AddHabitActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),ruleLoginActivity.getActivity());
        solo.enterText((EditText) solo.getView(R.id.login_enter_email), "tianxia3@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.login_enter_password), "Aa@123456");
        solo.clickOnView((Button) solo.getView(R.id.login_button));
        wait(5000);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),ruleMainActivity.getActivity());
    }

    @Test
    public void checkAddHabit() throws Exception{
        // opens add habit activity
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.button_add_habit));
        assertTrue(solo.waitForActivity(AddHabitActivity.class, 2000));

        // switch solo
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),ruleAddHabitActivity.getActivity());

        // enter information of a habit
        solo.enterText((EditText) solo.getView(R.id.enter_habit_title), "Study");
        solo.enterText((EditText) solo.getView(R.id.enter_reason), "I want to obtain a good grade");
        solo.clickOnCheckBox(R.id.checkBox_monday);
        solo.clickOnCheckBox(R.id.checkBox_friday);
        solo.clickOnButton(R.id.button_select_date);
        solo.clickOnScreen(1200, 1536);
        solo.scrollDown();
        solo.clickOnButton(R.id.button_confirm_add_habit);
        wait(2000);

        // see if the habit is in the list
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),ruleMainActivity.getActivity());
        assertTrue(solo.waitForText("Study", 1, 3000));
    }
}
