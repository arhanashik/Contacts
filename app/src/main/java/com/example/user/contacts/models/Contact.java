package com.example.user.contacts.models;

import android.widget.Toast;

import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.others.AppConstants;

import java.io.Serializable;

public class Contact implements Serializable, Comparable<Contact>{
    private int id;
    private String firstName;
    private String lastName;
    private String company;
    private String phoneHome;
    private String phoneWork;
    private String email;
    private String address;
    private String birthday;
    private String website;
    private boolean isFavourite;
    private int totalCalled;
    private byte[] thumbnail;

    public Contact () {
    }

    public Contact(int id, String firstName, String lastName, String company, String phoneHome,
                   String phoneWork, String email, String address, String birthday, String website,
                   boolean isFavourite, int totalCalled, byte[] thumbnail) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phoneHome = phoneHome;
        this.phoneWork = phoneWork;
        this.email = email;
        this.address = address;
        this.birthday = birthday;
        this.website = website;
        this.isFavourite = isFavourite;
        this.totalCalled = totalCalled;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName : "Unknown Name";
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public int getTotalCalled() {
        return totalCalled;
    }

    public void setTotalCalled(int totalCalled) {
        this.totalCalled = totalCalled;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int compareTo(Contact another) {
        return this.getFirstName().compareToIgnoreCase(another.getFirstName());
    }

    public String insertContact(Contact contact, DBHelper dbHelper, boolean isNew){
        if(contact == null || dbHelper == null) return AppConstants.NULL_VALUE;

        if(contact == null || (contact.getPhoneHome().isEmpty() && contact.getPhoneWork().isEmpty()))
            return AppConstants.EMPTY_FIELD;
        else{
            if(isNew){
                long inserted = dbHelper.insertContact(contact, AppConstants.TABLE_CONTACTS);
                if(inserted == -2)
                    return AppConstants.ANOTHER_CONTACT_EXISTS;
                else if(inserted == -3)
                    return AppConstants.EMPTY_FIELD;
                else
                    return AppConstants.CONTACT_CREATED;
            }else {
                if(dbHelper.updateContact(contact, AppConstants.TABLE_CONTACTS))
                    return AppConstants.CONTACT_UPDATED;
                else
                    return AppConstants.CONTACT_NOT_UPDATED;

            }
        }
    }
}
