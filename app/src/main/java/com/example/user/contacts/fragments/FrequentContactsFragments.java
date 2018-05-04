package com.example.user.contacts.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.contacts.R;
import com.example.user.contacts.activities.ContactDetailsActivity;
import com.example.user.contacts.adapters.RecyclerViewAdapter;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.layout.FastScroller;
import com.example.user.contacts.listeners.OnSearchListener;
import com.example.user.contacts.listeners.RecyclerViewOnItemClickListener;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;
import com.example.user.contacts.others.StickyIndex;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.contacts.activities.MainActivity.nowInTab;

public class FrequentContactsFragments extends Fragment implements OnSearchListener{

    private Activity mActivity;
    private RecyclerView recyclerView;
    private StickyIndex stickyIndex;
    private FastScroller fastScroller;
    private View rootView;

    private DBHelper dbHelper;
    private List<Contact> myContacts;
    private RecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getContext());
        myContacts = dbHelper.getContacts(AppConstants.TABLE_CONTACTS, AppConstants.QUERY_GET_FREQUENT_CONTACTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_frequent_contacts, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        fastScroller = (FastScroller) rootView.findViewById(R.id.fast_scroller);
        stickyIndex = (StickyIndex) rootView.findViewById(R.id.sticky_index_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new RecyclerViewAdapter (new ArrayList<>(myContacts), mActivity);
        recyclerView.setAdapter(mAdapter);
        implementsRecyclerViewOnItemClickListener();

        stickyIndex.setDataSet(getIndexList(myContacts));
        stickyIndex.setOnScrollListener(recyclerView);
        stickyIndex.subscribeForScrollListener(fastScroller);

        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setStickyIndex(stickyIndex.getStickyIndex());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public OnSearchListener getSearchListener(){
        return this;
    }

    private void implementsRecyclerViewOnItemClickListener () {
        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(mActivity,
                new RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Contact c = ((RecyclerViewAdapter) recyclerView.getAdapter()).getContact(position);
                        openUserDetails(view, c);
                    }
                }));
    }

    protected void openUserDetails (View view, Contact contact) {
        final View contactThumbnail = view.findViewById(R.id.contact_thumbnail);
        final Pair<View, String> pair1 = Pair.create(contactThumbnail, "contact_thumbnail");

        final View contactName = view.findViewById(R.id.contact_name);
        final Pair<View, String> pair2 = Pair.create(contactName, "contact_name");

        Intent intent = new Intent(mActivity, ContactDetailsActivity.class);
        intent.putExtra("CONTACT", contact);

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(mActivity, pair1, pair2);
            mActivity.startActivity(intent, options.toBundle());
        }
        else mActivity.startActivity(intent);
    }

    private char[] getIndexList (List<Contact> list) {
        char[] result = new char[list.size()];
        int i = 0;

        for (Contact c : list) {
            char ch = Character.toUpperCase(c.getFirstName().charAt(0));
            if(!Character.isLetter(ch)) ch = '#';
            result[i] = ch;
            i++;
        }

        return result;
    }

    @Override
    public void OnSearch(String searchKey) {
        if(nowInTab==0) {
            mAdapter.getFilter().filter(searchKey);
            stickyIndex.setDataSet(getIndexList(mAdapter.getDataSet()));
        }
    }
}
