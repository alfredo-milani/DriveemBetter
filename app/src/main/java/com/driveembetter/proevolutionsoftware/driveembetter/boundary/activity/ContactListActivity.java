package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Friend;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.ContactAdapter;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mattia on 17/10/2017.
 */

public class ContactListActivity extends AppCompatActivity {

    private final static String TAG = ContactListActivity.class.getSimpleName();

    ListView contactList;
    EditText inputSearch;
    ContactAdapter contactAdapter;
    ArrayList<Friend> friends;
    Context context;
    private int number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "DIOCANEEE");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.contact_list);
        Bundle b = getIntent().getExtras();
        this.number = b.getInt("number");
        initResources();
    }

    private void initResources() {
        this.contactList = findViewById(R.id.contacts);
        this.inputSearch = findViewById(R.id.input_search);
        context = this;
        getContactList();
        /**
         * Enabling Search Filter
         **/
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                int textLength = cs.length();
                ArrayList<Friend> tempArrayList = new ArrayList<>();
                for(Friend c: friends){
                    if (textLength <= c.getName().length()) {
                        if (c.getName().toLowerCase().contains(cs.toString().toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                }
                contactAdapter = new ContactAdapter(context, tempArrayList);
                contactList.setAdapter(contactAdapter);
            }


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Friend friend = (Friend) contactList.getItemAtPosition(i);
                SharedPreferences.Editor editor = getSharedPreferences("friends_preference", MODE_PRIVATE).edit();
                if (friend.getName().equals("No User")) {
                    //delete
                    FirebaseDatabaseManager.deleteFriend(number);
                    Intent addFriendActivity = new Intent(ContactListActivity.this, AddFriendsActivity.class);
                    ContactListActivity.this.startActivity(addFriendActivity);
                    editor.putString("name"+number, getResources().getString(R.string.empty_friends));
                    editor.putString("phone_no"+number, "");
                    editor.apply();
                    finish();
                } else {
                    FirebaseDatabaseManager.updateFriend(number, friend.getName(), friend.getPhoneNo());
                    Intent addFriendActivity = new Intent(ContactListActivity.this, AddFriendsActivity.class);
                    ContactListActivity.this.startActivity(addFriendActivity);
                    editor.putString("name"+number, friend.getName());
                    editor.putString("phone_no"+number, friend.getPhoneNo());
                    editor.apply();
                    finish();
                }
            }
        });
    }

    private void getContactList() {
        friends = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Friend friend = new Friend(name, phoneNo);
                        friends.add(friend);
                    }
                    pCur.close();
                }
            }
        }

        Collections.sort(friends, new CustomComparator());
        friends = removeDuplicates(friends);
        friends.add(0, new Friend("No User", ""));
        contactAdapter = new ContactAdapter(this, friends);
        //contactList.setAdapter(new ContactAdapter(this, friends));
        contactList.setAdapter(contactAdapter);

        if(cur!=null){
            cur.close();
        }
    }

    private ArrayList<Friend> removeDuplicates(ArrayList<Friend> friends) {

        for (int i = 0; i < friends.size(); i++) {
            Friend testFriend = friends.get(i);
            for (int j = i; j < friends.size(); j++) {
                if (testFriend.getName().equals(friends.get(j).getName()))
                    friends.remove(j);
            }
        }
        return friends;
    }

    public class CustomComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getName().compareTo(o2.getName());
        }

    }

}
