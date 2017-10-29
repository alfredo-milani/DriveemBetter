package com.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.entity.Friend;

import java.util.ArrayList;

/**
 * Created by matti on 17/10/2017.
 */

public class ContactAdapter extends ArrayAdapter<Friend> implements Filterable {

    private Context context;
    private ArrayList<Friend> friends;

    public ContactAdapter(Context context, ArrayList<Friend> friends) {
        super(context, 0, friends);
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        int listViewItemType = getItemViewType(position);

        View row = convertView;

        ViewHolderContact holder = null;
        holder = new ViewHolderContact();

        Friend friend = friends.get(position);

        if (row == null) {
               LayoutInflater inflater = ((Activity) context).getLayoutInflater();

                if (listViewItemType == 0) {
                    row = inflater.from(getContext()).inflate(R.layout.item_contact, null);
                }

                holder.textView1 = row.findViewById(R.id.name);
                holder.textView2 = row.findViewById(R.id.phone_no);
                row.setTag(holder);
        } else {
                holder = (ViewHolderContact) row.getTag();
        }
        holder.textView1.setText(friend.getName());
        holder.textView2.setText(friend.getPhoneNo());


        return row;
    }

}
