package com.example.habitshare;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import static org.junit.Assert.*;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.*;

public class LoginTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> ruleMainActivity = new ActivityTestRule<>(MainActivity.class, true, true);

    @Rule
    public ActivityTestRule<LoginActivity> ruleLoginActivity = new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),ruleLoginActivity.getActivity());
    }

    @Test
    public void loginShowUp() throws Exception{
        assertTrue(solo.waitForActivity(LoginActivity.class, 2000));
    }

    @Test
    public void invalidAccount() throws Exception{
        // any invalid input will prevent the app from existing the login activity
        // empty input
        solo.clickOnView((Button) solo.getView(R.id.login_button));
        assertTrue(solo.waitForActivity(LoginActivity.class, 5000));

        // wrong password
        solo.enterText((EditText) solo.getView(R.id.login_enter_email), "tianxia3@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.login_enter_password), "adfadfasfasdf");
        assertTrue(solo.waitForActivity(LoginActivity.class, 5000));
    }

    @Test
    public void checkLoginSuccess(){
        // a successful login will exists login activity and enters the main activity
        solo.enterText((EditText) solo.getView(R.id.login_enter_email), "tianxia3@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.login_enter_password), "Aa@123456");
        solo.clickOnView((Button) solo.getView(R.id.login_button));
        assertTrue(solo.waitForActivity(MainActivity.class, 5000));
    }
}



