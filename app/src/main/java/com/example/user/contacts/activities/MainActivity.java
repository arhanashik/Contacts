package com.example.user.contacts.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.example.user.contacts.R;
import com.example.user.contacts.adapters.FragmentsAdapter;
import com.example.user.contacts.dao.ContactsFromPhone;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.fragments.ContactsFragment;
import com.example.user.contacts.fragments.FavoritesFragments;
import com.example.user.contacts.fragments.FrequentContactsFragments;
import com.example.user.contacts.listeners.OnDataChangedListener;
import com.example.user.contacts.listeners.OnSearchListener;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = "MainActivity";

    private TabLayout tabs;
    private ViewPager viewPager;
    private FragmentsAdapter viewPagerAdapter;

    private int[] tabIcons = {
            R.drawable.ic_all_contacts,
            R.drawable.ic_favorite,
            R.drawable.ic_frequent
    };

    private List<Contact> phoneContacts;
    private DBHelper dbHelper;

    private ContactsFragment contactsFragment = new ContactsFragment();
    private FavoritesFragments favoritesFragments = new FavoritesFragments();
    private FrequentContactsFragments frequentContactsFragments = new FrequentContactsFragments();

    public static OnSearchListener mSearchListener;
    public static int nowInTab = 0;
    public static boolean updateOnResume = false;
    public static OnDataChangedListener mDataChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getSupportActionBar().setElevation(0);
        }catch (NullPointerException e){
            Log.d(TAG, "Action Bar Elevation is not working");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementExitTransition(new Slide());
            getWindow().setSharedElementEnterTransition(new Slide());
        }

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        setupTabIcons();

        handleIntent(getIntent());

        dbHelper = new DBHelper(this);
        if(dbHelper.getRowCount(AppConstants.TABLE_CONTACTS)==0) importContactFromPhone();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        mSearchListener = contactsFragment.getSearchListener();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import_contacts) {
            importContactFromPhone();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new FragmentsAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(contactsFragment, "All Contacts");
        viewPagerAdapter.addFragment(favoritesFragments, "Favorites");
        viewPagerAdapter.addFragment(frequentContactsFragments, "Frequent");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
        tabs.getTabAt(2).setIcon(tabIcons[2]);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0) mSearchListener = contactsFragment.getSearchListener();
                else if(tab.getPosition()==1) mSearchListener = favoritesFragments.getSearchListener();
                else if(tab.getPosition()==2) mSearchListener = frequentContactsFragments.getSearchListener();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void checkReadContactPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                    AppConstants.READ_CONTACT_REQUEST_CODE);
        }
        else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Reading. Please wait...");
            pd.setCancelable(false);
            pd.show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    phoneContacts = new ContactsFromPhone(getApplicationContext()).listMappedContacts();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(nowInTab==0){
                                mDataChanged = contactsFragment.getDataChangedListener();
                                mDataChanged.onDataChanged();
                            }
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Total: " + phoneContacts.size(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.READ_CONTACT_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setMessage("Reading. Please wait...");
                    pd.setCancelable(false);
                    pd.show();
                    // Since reading contacts takes more time, let's run it on a separate thread.
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            phoneContacts = new ContactsFromPhone(getApplicationContext()).listMappedContacts();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(nowInTab==0){
                                        mDataChanged = contactsFragment.getDataChangedListener();
                                        mDataChanged.onDataChanged();
                                    }
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Total: " + phoneContacts.size(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void importContactFromPhone(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Import Contacts from phone?")
                .setCancelable(false)
                .setPositiveButton("Sync Contacts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkReadContactPermission();
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(mSearchListener != null) mSearchListener.OnSearch(query);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mSearchListener != null) mSearchListener.OnSearch(newText);
        return false;
    }
}

