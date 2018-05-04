package com.example.user.contacts;

import android.content.Context;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by USER on 2/20/2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class InsertContactUnitTest {

    Context context = mock(Context.class);

    @Mock
    DBHelper databaseMock = new DBHelper(context);

    boolean isNew = true;

    @Mock
    Contact fakeContact = new Contact();

    @Test
    public void testInsertion()  {
        String phoneNum = "017641212";
        fakeContact = new Contact(0, "First name", "last name",
                "company", phoneNum, "1231244", "a@gmail.com",
                "khulna", "12/02/2016", "www.db.org", isNew,
                0, null);
        String result = fakeContact.insertContact(fakeContact, databaseMock, isNew);
        assertThat(result, is(AppConstants.CONTACT_CREATED));
    }

    @Test
    public void testInsertionFailed()  {
        String phoneNum = "";
        fakeContact = new Contact(0, "First name", "last name",
                "company", phoneNum, phoneNum, "a@gmail.com",
                "khulna", "12/02/2016", "www.db.org", isNew,
                0, null);
        //fakeContact.insertContact(fakeContact, databaseMock, isNew);
        String result = fakeContact.insertContact(fakeContact, databaseMock, isNew);
        assertThat(result, is(AppConstants.EMPTY_FIELD));
    }

    @Test
    public void testUpdate()  {
        //condition 1 for insertion: at least one phone number is not null
        //condition 2 for insertion: phone number is not in database
        String phoneNum = "017641212";
        fakeContact = new Contact(0, "First name", "last name",
                "company", phoneNum, "123123", "a@gmail.com",
                "khulna", "12/02/2016", "www.db.org", isNew,
                0, null);
        fakeContact.insertContact(fakeContact, databaseMock, isNew);
        fakeContact.setFavourite(false);
        isNew = false;
        String result = fakeContact.insertContact(fakeContact, databaseMock, isNew);
        assertThat(result, is(AppConstants.CONTACT_UPDATED));
    }
}
