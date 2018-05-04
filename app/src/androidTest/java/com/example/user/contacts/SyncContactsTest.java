package com.example.user.contacts;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.user.contacts.activities.MainActivity;
import com.example.user.contacts.fragments.ContactsFragment;
import com.example.user.contacts.models.Contact;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by USER on 3/14/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SyncContactsTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initValidString(){
        onView(withId(R.id.action_import_contacts)).perform(click());
    }

    @Test
    public void syncContacts(){
        onView(withText("Sync Contacts")).perform(click());

        onData(allOf(is(instanceOf(Contact.class))));
    }

    @After
    public void whatToDo(){

    }
}
