package com.example.user.contacts.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.contacts.R;
import com.example.user.contacts.activities.ContactDetailsActivity;
import com.example.user.contacts.activities.CreateEditContactActivity;
import com.example.user.contacts.adapters.RecyclerViewAdapter;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.layout.FastScroller;
import com.example.user.contacts.listeners.OnDataChangedListener;
import com.example.user.contacts.listeners.OnSearchListener;
import com.example.user.contacts.listeners.RecyclerViewOnItemClickListener;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;
import com.example.user.contacts.others.StickyIndex;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.contacts.activities.MainActivity.mSearchListener;
import static com.example.user.contacts.activities.MainActivity.nowInTab;

public class ContactsFragment extends Fragment implements OnSearchListener, OnDataChangedListener{
    private static final String TAG = "ContactsFragment";

    private Activity mActivity;
    private RecyclerView recyclerView;
    private StickyIndex stickyIndex;
    private FastScroller fastScroller;
    private FloatingActionButton fab;
    private View rootView;

    private DBHelper dbHelper;
    private List<Contact> myContacts;
    private RecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getContext());
        myContacts = dbHelper.getContacts(AppConstants.TABLE_CONTACTS, AppConstants.QUERY_GET_ALL_CONTACT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        //if(nowInTab==0) mSearchListener = this;
    }

    public OnSearchListener getSearchListener(){
        return this;
    }

    public OnDataChangedListener getDataChangedListener(){return this;}

    @Override
    public void onDetach() {
        mSearchListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        fastScroller = rootView.findViewById(R.id.fast_scroller);
        stickyIndex = rootView.findViewById(R.id.sticky_index_container);
        fab = rootView.findViewById(R.id.fab);

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

        implementFabListener();

        return rootView;
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

    private void implementFabListener () {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateEditContactActivity.class));
            }
        });
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

    public void updateRecyclerViewFromSearchSelection (String contactName) {
        Contact contact = ((RecyclerViewAdapter) recyclerView.getAdapter()).getContactByName(contactName);
        int contactIdx = ((RecyclerViewAdapter) recyclerView.getAdapter()).getDataSet().indexOf(contact);

        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, contactIdx);
    }

    @Override
    public void OnSearch(String searchKey) {
        if(nowInTab==0) {
            mAdapter.getFilter().filter(searchKey);
            stickyIndex.setDataSet(getIndexList(mAdapter.getDataSet()));
        }
    }

    @Override
    public void onDataChanged() {
        myContacts.clear();
        myContacts = dbHelper.getContacts(AppConstants.TABLE_CONTACTS, AppConstants.QUERY_GET_ALL_CONTACT);
        mAdapter.setDataSet(myContacts);
        if(myContacts.size()>0) stickyIndex.setDataSet(getIndexList(mAdapter.getDataSet()));

    }
}
