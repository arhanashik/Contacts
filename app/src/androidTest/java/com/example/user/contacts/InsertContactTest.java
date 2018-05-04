package com.example.user.contacts;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.user.contacts.activities.CreateEditContactActivity;
import com.example.user.contacts.others.AppConstants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by USER on 3/14/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InsertContactTest {

    @Rule
    public ActivityTestRule<CreateEditContactActivity> mActivityRule = new ActivityTestRule<>(
                CreateEditContactActivity.class);

    @Before
    public void initCreation(){
        onView(withId(R.id.first_name)).perform(typeText("First Name"), closeSoftKeyboard());
        onView(withId(R.id.last_name)).perform(typeText("Last Name"), closeSoftKeyboard());
        onView(withId(R.id.company)).perform(typeText("Company"), closeSoftKeyboard());
        onView(withId(R.id.phone_home)).perform(typeText("01235"), closeSoftKeyboard());
        onView(withId(R.id.phone_work)).perform(typeText("5678"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("email@com"), closeSoftKeyboard());
        onView(withId(R.id.address)).perform(typeText("Address"), closeSoftKeyboard());
        onView(withId(R.id.website)).perform(typeText("Website"), closeSoftKeyboard());
    }

    @Test
    public void successfulInsertion(){
        onView(withId(R.id.add_contact)).perform(click());

        onView(withText(AppConstants.CONTACT_CREATED)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void failedInsertion(){
        onView(withId(R.id.phone_home)).perform(clearText());
        onView(withId(R.id.phone_work)).perform(clearText());
        onView(withId(R.id.add_contact)).perform(click());

        onView(withText(AppConstants.EMPTY_FIELD)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
