package com.example.debtdomino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

@RunWith(AndroidJUnit4.class)
public class AuthInstrumentedTest {
    private FirebaseUser currentUser;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void initializeApp() {
        scenario = ActivityScenario.launch(MainActivity.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Test
    public void basicAuth() throws InterruptedException {
        Espresso.onView(ViewMatchers.withId(R.id.username_edit_text))
                .perform(ViewActions.typeText("quyendl.ptnk@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password_edit_text)).perform(ViewActions.typeText("123456789"));
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        while (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Thread.sleep(100);
        }
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Espresso.onView(ViewMatchers.withId(R.id.logout_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());

        while (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Thread.sleep(100);
        }
    }

    @After
    public void closeApp() {
        scenario.close();
    }
}