package com.example.uscdrinkdoor;


import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccountUI {

    @Rule
    public ActivityScenarioRule<CreateAccount> activityRule =
            new ActivityScenarioRule<>(CreateAccount.class);

    //bug uncovered -> users could sign up with empty info -> crash
    @Test
    public void registerAccountButtonWithEmptyFields() {

        onView(withId(R.id.saveInfo))
                .perform(click());
        //make sure error shows up
        onView(withText("Make sure you have filled out all fields"))
                .check(matches(isDisplayed()));


    }
}