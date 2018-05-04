package com.example.user.contacts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.contacts.R;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;

public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgCall, imgMessage, imgEdit, imgFavorite, imgDelete, imgCallHome, imgCallWork, imgEmail;
    private TextView tvFirstName, tvLastName, tvCompany, tvPhoneHome, tvPhoneWork, tvEmail,
            tvBirthday, tvWebsite, tvAddress;

    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    private Contact contact;
    private DBHelper dbHelper;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contact = (Contact) getIntent().getSerializableExtra("CONTACT");
        dbHelper = new DBHelper(this);

        initView();
    }

    private void initView() {
        imgCall = findViewById(R.id.img_call);
        imgMessage = findViewById(R.id.img_message);
        imgEdit = findViewById(R.id.img_edit);
        imgFavorite = findViewById(R.id.img_favorite);
        imgDelete = findViewById(R.id.img_delete);
        imgCallHome = findViewById(R.id.img_call_phone_home);
        imgCallWork = findViewById(R.id.img_call_phone_work);
        imgEmail = findViewById(R.id.img_email);

        tvFirstName = findViewById(R.id.first_name);
        tvLastName = findViewById(R.id.last_name);
        tvCompany = findViewById(R.id.company);
        tvPhoneHome = findViewById(R.id.phone_home);
        tvPhoneWork = findViewById(R.id.phone_work);
        tvEmail = findViewById(R.id.email);
        tvBirthday = findViewById(R.id.birthday);
        tvWebsite = findViewById(R.id.website);
        tvAddress = findViewById(R.id.address);

        if (contact != null) {
            setTitle(contact.getFirstName() + " " + contact.getLastName());
            isFavorite = contact.isFavourite();
            updateFavoriteButton();
            tvFirstName.setText(contact.getFirstName());
            tvLastName.setText(contact.getLastName());
            tvCompany.setText(contact.getCompany());
            tvPhoneHome.setText(contact.getPhoneHome());
            tvPhoneWork.setText(contact.getPhoneWork());
            tvEmail.setText(contact.getEmail());
            tvBirthday.setText(contact.getBirthday());
            tvWebsite.setText(contact.getWebsite());
            tvAddress.setText(contact.getAddress());
        }

        imgCall.setOnClickListener(this);
        imgMessage.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        imgCallHome.setOnClickListener(this);
        imgCallWork.setOnClickListener(this);
        imgEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgCall) {
            dbHelper.incrementTotalCalled(AppConstants.TABLE_CONTACTS, contact.getId(), contact.getTotalCalled());
            dialContactPhone(contact.getPhoneHome());

        } else if (v == imgMessage) {
            dbHelper.incrementTotalCalled(AppConstants.TABLE_CONTACTS, contact.getId(), contact.getTotalCalled());
            sendSMS(contact.getPhoneHome());

        } else if (v == imgEdit) {
            Intent intent = new Intent(ContactDetailsActivity.this, CreateEditContactActivity.class);
            if (contact != null && !contact.getPhoneHome().isEmpty())
                intent.putExtra("CONTACT", contact);
            startActivity(intent);

        } else if (v == imgFavorite) {
            isFavorite = !isFavorite;
            if (isFavorite)
                Toast.makeText(this, "Marked as favorite!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Removed from favorite!", Toast.LENGTH_SHORT).show();
            updateFavoriteButton();
            if (contact!=null) {
                contact.setFavourite(isFavorite);
                dbHelper.toggleFavorite(AppConstants.TABLE_CONTACTS, contact.getId(), !isFavorite);
            }

        } else if (v == imgDelete) {
            deleteContact();

        }else if(v==imgCallHome){
            dbHelper.incrementTotalCalled(AppConstants.TABLE_CONTACTS, contact.getId(), contact.getTotalCalled());
            dialContactPhone(contact.getPhoneHome());

        }else if(v==imgCallWork){
            if(!contact.getPhoneWork().isEmpty()) {
                dbHelper.incrementTotalCalled(AppConstants.TABLE_CONTACTS, contact.getId(), contact.getTotalCalled());
                dialContactPhone(contact.getPhoneWork());
            }
            else Toast.makeText(this, "Phone (Work) is not added!", Toast.LENGTH_SHORT).show();

        }else if(v==imgEmail){
            if(!contact.getEmail().isEmpty()) {
                dbHelper.incrementTotalCalled(AppConstants.TABLE_CONTACTS, contact.getId(), contact.getTotalCalled());
                sendEmail(contact.getEmail());
            }
            else Toast.makeText(this, "Email is not added!", Toast.LENGTH_SHORT).show();


        }
    }

    private void dialContactPhone(final String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    AppConstants.CALL_PHONE_REQUEST_CODE);
        }
        else startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void sendSMS(final String phoneNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);

            Uri sms_uri = Uri.parse("smsto:"+phoneNumber);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);

            if (defaultSmsPackageName != null)
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);

        } else {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.setData(Uri.parse("sms:" +  phoneNumber));
            smsIntent.putExtra("sms_body","");
            startActivity(smsIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==AppConstants.CALL_PHONE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            dialContactPhone(contact.getPhoneHome());
        else Toast.makeText(this, "Permission required!", Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteButton(){
        if(isFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_favorite);
        }
        else {
            imgFavorite.setImageResource(R.drawable.ic_not_favorite);
        }
    }

    private void deleteContact() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(contact.getFirstName()+" "+contact.getLastName())
                .setMessage("Delete this contact?")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbHelper.deleteContact(AppConstants.TABLE_CONTACTS, contact.getId())){
                            Toast.makeText(ContactDetailsActivity.this, "Contact deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ContactDetailsActivity.this, "Failed to deleted!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        dialog = builder.create();
        dialog.show();

    }

    private void sendEmail(String email) {
        String subject = "";
        String body = "";
        String chooserTitle = "Select app";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);

        startActivity(Intent.createChooser(emailIntent, chooserTitle));
    }
}
