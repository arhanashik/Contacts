package com.example.user.contacts.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.contacts.R;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;
import com.example.user.contacts.utils.ImageUtility;

import java.util.Calendar;

public class CreateEditContactActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etFirstName, etLastName, etCompany, etPhoneHome, etPhoneWork,
            etEmail, etAddress, etBirthday, etWebsite, etSelectImage;
    private ImageView imgSelectPhoto, imgSelectBirthday, imgFavorite;
    private Button btnAddContact;

    private DBHelper dbHelper;
    private Contact contact;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;

    private boolean isNew = true;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intViews();
    }

    private void intViews() {
        dbHelper = new DBHelper(this);

        etFirstName = findViewById(R.id.first_name);
        etLastName = findViewById(R.id.last_name);
        etCompany = findViewById(R.id.company);
        etPhoneHome = findViewById(R.id.phone_home);
        etPhoneWork = findViewById(R.id.phone_work);
        etEmail = findViewById(R.id.email);
        etAddress = findViewById(R.id.address);
        etBirthday = findViewById(R.id.birthday);
        etWebsite = findViewById(R.id.website);
        etSelectImage = findViewById(R.id.image);

        imgSelectPhoto = findViewById(R.id.img_select);
        imgSelectBirthday = findViewById(R.id.img_birthday);
        imgFavorite = findViewById(R.id.img_favorite);

        btnAddContact = findViewById(R.id.add_contact);

        imgSelectPhoto.setOnClickListener(this);
        imgSelectBirthday.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        btnAddContact.setOnClickListener(this);

        contact = new Contact();
        contact = (Contact) getIntent().getSerializableExtra("CONTACT");
        if(contact!=null) {
            setTitle("Edit Contact");
            btnAddContact.setText("Update Contact");
            isNew = false;
            etFirstName.setText(contact.getFirstName());
            etLastName.setText(contact.getLastName());
            etCompany.setText(contact.getCompany());
            etPhoneHome.setText(contact.getPhoneHome());
            etPhoneWork.setText(contact.getPhoneWork());
            etEmail.setText(contact.getEmail());
            etBirthday.setText(contact.getBirthday());
            etWebsite.setText(contact.getWebsite());
            etSelectImage.setText("Change image");
            isFavorite = contact.isFavourite();
            updateFavoriteButton();
        }

    }

    @Override
    public void onClick(View v) {
        if(v==imgSelectPhoto){
            selectImg();

        }else if(v==imgSelectBirthday){
            selectBirthday();

        }else if(v==imgFavorite){
            isFavorite = !isFavorite;
            if(isFavorite)
                Toast.makeText(this, "Marked as favorite!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Removed from favorite!", Toast.LENGTH_SHORT).show();
            updateFavoriteButton();

        }else if(v==btnAddContact){
            addEditContact();

        }
    }

    private void selectImg(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, AppConstants.CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case AppConstants.CAPTURE_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    if(bitmap != null){
                        etSelectImage.setText("Image captured!");
                        contact.setThumbnail(ImageUtility.getBytes(bitmap));
                    }
                    etSelectImage.setText("Image not captured!");
                }
                else etSelectImage.setText("Image not captured!");

                break;

            default:
                break;
        }
    }

    private void selectBirthday(){
        calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        etBirthday.setText("Birthday: "+date);
                        if(contact == null) contact = new Contact();
                        contact.setBirthday(date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void updateFavoriteButton(){
        if(isFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_favorite);
        }
        else {
            imgFavorite.setImageResource(R.drawable.ic_not_favorite);
        }
    }

    private void addEditContact() {
        String firstName = etFirstName.getText().toString();
        if(firstName == null || firstName.isEmpty()) firstName = "Unknown";
        if(contact == null) contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(etLastName.getText().toString());
        contact.setCompany(etCompany.getText().toString());
        contact.setPhoneHome(etPhoneHome.getText().toString());
        contact.setPhoneWork(etPhoneWork.getText().toString());
        contact.setEmail(etEmail.getText().toString());
        contact.setAddress(etAddress.getText().toString());
        contact.setWebsite(etWebsite.getText().toString());
        contact.setFavourite(isFavorite);
        contact.setTotalCalled(0);

        String result = contact.insertContact(contact, dbHelper, isNew);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
