package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_NAME;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_PHONE_NO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;

/**
 * Created by matti on 17/10/2017.
 */

public class AddFriendsActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final static String TAG = AddFriendsActivity.class.getSimpleName();

    private final static int PICK_FIRST_CONTACT = 120;
    private final static int PICK_SECOND_CONTACT = 121;

    TextView firstFriendName, secondFriendName, firstNumber, secondNumber;
    Button firstButton, secondButton;
    PopupWindow contactList;
    CardView firstFriendCard, secondFriendCard;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_friends);
        initResources();
    }


    private void initResources() {

        this.firstFriendName = findViewById(R.id.first_user_name_text);
        this.secondFriendName = findViewById(R.id.second_user_name_text);
        this.firstNumber = findViewById(R.id.first_phone_number_text);
        this.secondNumber = findViewById(R.id.second_phone_number_text);
        this.firstButton = findViewById(R.id.first_button);
        this.secondButton = findViewById(R.id.second_button);
        this.firstFriendCard = findViewById(R.id.firstFriendCard);
        this.secondFriendCard = findViewById(R.id.secondFriendCard);

        SharedPreferences prefs = getSharedPreferences("friends_preference", MODE_PRIVATE);
        this.firstFriendName.setText(prefs.getString("name1", getResources().getString(R.string.empty_friends)));
        this.firstNumber.setText(prefs.getString("phone_no1", ""));

        this.secondFriendName.setText(prefs.getString("name2", getResources().getString(R.string.empty_friends)));
        this.secondNumber.setText(prefs.getString("phone_no2", ""));

        /*
        //check permissions
        if (!checkPermission())
            requestPermission();

        if (!checkPermission())
            this.finish();
            */


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS}, 1);
        } else {
            Log.d("DB", "PERMISSION GRANTED");
        }

        /*
        this.firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactListIntent = new Intent(AddFriendsActivity.this, ContactListActivity.class);
                contactListIntent.putExtra("number", 1);
                AddFriendsActivity.this.startActivity(contactListIntent);
                Toast.makeText(getApplicationContext(), "Sto cercando i tuoi contatti..", Toast.LENGTH_SHORT).show();
            }
        });

        this.secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactListIntent = new Intent(AddFriendsActivity.this, ContactListActivity.class);
                contactListIntent.putExtra("number", 2);
                AddFriendsActivity.this.startActivity(contactListIntent);
                Toast.makeText(getApplicationContext(), "Sto cercando i tuoi contatti..", Toast.LENGTH_SHORT).show();
            }
        });
        */
        this.firstButton.setOnClickListener(this);
        this.secondButton.setOnClickListener(this);
        getFriends();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        switch (id) {
            case R.id.first_button:
                startActivityForResult(intent, AddFriendsActivity.PICK_FIRST_CONTACT);
                break;

            case R.id.second_button:
                startActivityForResult(intent, AddFriendsActivity.PICK_SECOND_CONTACT);
                break;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_OK:
                Uri contactData = data.getData();
                Cursor c =  this.getContentResolver().query(contactData, null, null, null, null);
                if (c != null && c.moveToFirst()) {
                    SharedPreferences.Editor editor = getSharedPreferences("friends_preference", MODE_PRIVATE).edit();
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = null;

                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null,
                                null
                        );
                        if (phones == null) {
                            return;
                        }
                        phones.moveToFirst();
                        number = phones.getString(phones.getColumnIndex("data1"));
                        phones.close();
                    }

                    switch (reqCode) {
                        case (AddFriendsActivity.PICK_FIRST_CONTACT):
                            this.firstFriendName.setText(name);
                            if (number != null) {
                                this.firstNumber.setText(number);
                                editor.putString("phone_no" + number, number);
                            }
                            FirebaseDatabaseManager.updateFriend(PICK_FIRST_CONTACT, name, number == null ? "null" : number);
                            editor.putString("name_" + PICK_FIRST_CONTACT, number);
                            break;

                        case PICK_SECOND_CONTACT:
                            this.secondFriendName.setText(name);
                            if (number != null) {
                                this.secondNumber.setText(number);
                                editor.putString("phone_no" + number, number);
                            }
                            FirebaseDatabaseManager.updateFriend(PICK_SECOND_CONTACT, name, number == null ? "null" : number);
                            editor.putString("name_" + PICK_SECOND_CONTACT, number);
                            break;
                    }
                    editor.apply();
                }

                if (c != null) {
                    c.close();
                }
                break;

            case RESULT_CANCELED:
                Log.d(TAG, "Azione cancellata: lista contatti chiusa");
                break;
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
            Toast.makeText(this,"Please, accept read contact permission.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }
    }


    // TODO SPOSTARLA
    // TODO: 25/10/17 MA CHE TE DICE SA CAPOCCIA?
    private void getFriends() {
        SingletonUser user = SingletonUser.getInstance();
        if (user == null) {
            return;
        }

        final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                .child(NODE_USERS)
                .child(user.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(CHILD_FIRST_FRIEND)) {
                    String name = dataSnapshot.child(CHILD_FIRST_FRIEND).child(CHILD_NAME).getValue().toString();
                    String phoneNo = dataSnapshot.child(CHILD_FIRST_FRIEND).child(CHILD_PHONE_NO).getValue().toString();
                    firstFriendName.setText(name);
                    firstNumber.setText(phoneNo);
                } else {
                    firstFriendName.setText(R.string.empty_friends);
                    firstNumber.setText("");
                }

                if (dataSnapshot.hasChild(CHILD_SECOND_FRIEND)) {
                    String name = dataSnapshot.child(CHILD_SECOND_FRIEND).child(CHILD_NAME).getValue().toString();
                    String phoneNo = dataSnapshot.child(CHILD_SECOND_FRIEND).child(CHILD_PHONE_NO).getValue().toString();
                    secondFriendName.setText(name);
                    secondNumber.setText(phoneNo);
                } else {
                    secondFriendName.setText(R.string.empty_friends);
                    secondNumber.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
