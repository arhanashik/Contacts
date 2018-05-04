package com.example.user.contacts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.user.contacts.R;
import com.example.user.contacts.listeners.TextGetter;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.utils.ImageUtility;
import com.pkmmte.view.CircularImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TextGetter, Filterable {

    private Context context;
    private List<Contact> dataSet;
    private List<Contact> mFilteredList;

    public RecyclerViewAdapter (List<Contact> contacts, Context c) {
        this.dataSet = contacts;
        this.context = c;
        this.mFilteredList = contacts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_details, parent, false);

        return new ContactsViewHolder (view);
    }

    private Contact contact;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactsViewHolder contactHolder = (ContactsViewHolder) holder;

        contact = mFilteredList.get(position);
        contactHolder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());
        contactHolder.firstLetter.setText(String.valueOf(contact.getFirstName().charAt(0)).toUpperCase());

        //if (contact.getThumbnail() != null || contact.getThumbnail().length>0) {
        //contactHolder.firstLetter.setVisibility(TextView.INVISIBLE);

        //contactHolder.thumbnail.setImageResource(R.drawable.ic_account_circle_black_24dp);
        //} else {
        contactHolder.firstLetter.setVisibility(TextView.VISIBLE);
        contactHolder.thumbnail.setImageResource(R.drawable.circle_icon);

        GradientDrawable drawable = (GradientDrawable) contactHolder.thumbnail.getDrawable();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        drawable.setColor(color);
        //}

        setRegularLineLayout(contactHolder);
    }

    private void setRegularLineLayout(ContactsViewHolder vh) {
        vh.firstLetter.setTextColor(Color.parseColor("#ffffff"));
        vh.firstLetter.setTextSize(26);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public Contact getContact (int pos) {
        return mFilteredList.get(pos);
    }

    public Contact getContactByName (String name) {
        for (Contact c : mFilteredList) {
            String data = c.getFirstName().toLowerCase();
            if (data.contains(name.toLowerCase())) {
                return c;
            }
        }

        return null;
    }

    public List<Contact> getDataSet() {
        return mFilteredList;
    }

    public void setDataSet(List<Contact> contacts) {
        mFilteredList = contacts;
        notifyDataSetChanged();
    }

    @Override
    public String getTextFromAdapter(int pos) {
        return String.valueOf(mFilteredList.get(pos).getFirstName().charAt(0)).toUpperCase();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {

                    mFilteredList = dataSet;
                } else {
                    List<Contact> filteredList = new ArrayList<>();

                    for (Contact contact : dataSet) {

                        if (contact.getFirstName().toLowerCase().contains(charString)
                                || contact.getLastName().toLowerCase().contains(charString)
                                || String.valueOf(contact.getPhoneHome()).contains(charString)
                                || String.valueOf(contact.getPhoneWork()).contains(charString)
                                || contact.getEmail().toLowerCase().contains(charString)) {

                            filteredList.add(contact);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void removeItem(int position) {
        mFilteredList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Contact contact, int position) {
        mFilteredList.add(position, contact);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView firstLetter;
        TextView contactName;
        CircularImageView thumbnail;

        public ContactsViewHolder (View v) {
            super (v);
            firstLetter = v.findViewById(R.id.contact_first_letter);
            contactName = v.findViewById(R.id.contact_name);
            thumbnail = v.findViewById(R.id.contact_thumbnail);
        }
    }
}
