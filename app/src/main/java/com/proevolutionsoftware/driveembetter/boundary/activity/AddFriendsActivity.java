package com.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.proevolutionsoftware.driveembetter.R;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.proevolutionsoftware.driveembetter.utils.PermissionManager;

import static com.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.proevolutionsoftware.driveembetter.constants.Constants.CHILD_NAME;
import static com.proevolutionsoftware.driveembetter.constants.Constants.CHILD_PHONE_NO;
import static com.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;
import static com.proevolutionsoftware.driveembetter.constants.Constants.PICK_FIRST_CONTACT;
import static com.proevolutionsoftware.driveembetter.constants.Constants.PICK_SECOND_CONTACT;

/**
 * Created by matti on 17/10/2017.
 */

public class AddFriendsActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final static String TAG = AddFriendsActivity.class.getSimpleName();

    TextView firstFriendName, secondFriendName, firstNumber, secondNumber;
    Button firstButton, secondButton;
    PopupWindow contactList;
    CardView firstFriendCard, secondFriendCard;
    RelativeLayout relativeLayout;
    ImageButton deleteFirst, deleteSecond;

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
        this.deleteFirst = findViewById(R.id.delete_first_friend);
        this.deleteSecond = findViewById(R.id.delete_second_friend);

        SharedPreferences prefs = getSharedPreferences("friends_preference", MODE_PRIVATE);
        this.firstFriendName.setText(prefs.getString("name1", getResources().getString(R.string.empty_friends)));
        this.firstNumber.setText(prefs.getString("phone_no1", ""));

        this.secondFriendName.setText(prefs.getString("name2", getResources().getString(R.string.empty_friends)));
        this.secondNumber.setText(prefs.getString("phone_no2", ""));

        this.firstButton.setOnClickListener(this);
        this.secondButton.setOnClickListener(this);
        this.deleteFirst.setOnClickListener(this);
        this.deleteSecond.setOnClickListener(this);

        getFriends();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog;
        int id = view.getId();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        switch (id) {
            case R.id.first_button:
                if (PermissionManager.isAllowed(this, PermissionManager.READ_CONTACTS_MANIFEST) &&
                        PermissionManager.isAllowed(this, PermissionManager.SEND_SMS_MANIFEST)) {
                    startActivityForResult(intent, PICK_FIRST_CONTACT);
                } else {
                    PermissionManager.checkAndAskPermission(
                            this,
                            new int[] {
                            PermissionManager.READ_CONTACTS,
                            PermissionManager.SEND_SMS
                            },
                            PermissionManager.ASK_FOR_ACCIDENT
                    );
                }
                break;

            case R.id.second_button:
                if (PermissionManager.isAllowed(this, PermissionManager.READ_CONTACTS_MANIFEST) &&
                        PermissionManager.isAllowed(this, PermissionManager.SEND_SMS_MANIFEST)) {
                    startActivityForResult(intent, PICK_SECOND_CONTACT);
                } else {
                    PermissionManager.checkAndAskPermission(
                            this,
                            new int[] {
                                    PermissionManager.READ_CONTACTS,
                                    PermissionManager.SEND_SMS
                            },
                            PermissionManager.ASK_FOR_ACCIDENT
                    );
                }
                break;

            case R.id.delete_first_friend:
                alertDialogBuilder = new AlertDialog.Builder(this);

                // set title
                alertDialogBuilder.setTitle("Warning!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Do you want to delete this friend?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabaseManager.deleteFriend(1);
                                firstFriendName.setText(getResources().getString(R.string.empty_friends));
                                firstNumber.setText("");
                                deleteFirst.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;

            case R.id.delete_second_friend:
                alertDialogBuilder = new AlertDialog.Builder(this);

                // set title
                alertDialogBuilder.setTitle("Warning!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Do you want to delete this friend?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabaseManager.deleteFriend(2);
                                secondFriendName.setText(getResources().getString(R.string.empty_friends));
                                secondNumber.setText("");
                                deleteSecond.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
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
                        case (PICK_FIRST_CONTACT):
                            if (secondNumber.getText().toString().equals(number)) {
                                alertDialog();
                            } else {
                                this.firstFriendName.setText(name);
                                if (number != null) {
                                    this.firstNumber.setText(number);
                                    editor.putString("phone_no" + number, number);
                                }
                                FirebaseDatabaseManager.updateFriend(PICK_FIRST_CONTACT, name, number == null ? "null" : number);
                                editor.putString("name_" + PICK_FIRST_CONTACT, number);
                            }
                            break;

                        case PICK_SECOND_CONTACT:
                            if (firstNumber.getText().toString().equals(number)) {
                                alertDialog();
                            } else {
                                this.secondFriendName.setText(name);
                                if (number != null) {
                                    this.secondNumber.setText(number);
                                    editor.putString("phone_no" + number, number);
                                }
                                FirebaseDatabaseManager.updateFriend(PICK_SECOND_CONTACT, name, number == null ? "null" : number);
                                editor.putString("name_" + PICK_SECOND_CONTACT, number);
                            }
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
        if (!firstFriendName.getText().equals(getResources().getString(R.string.empty_friends)))
            deleteFirst.setVisibility(View.VISIBLE);
        if (!secondFriendName.getText().equals(getResources().getString(R.string.empty_friends)))
            deleteSecond.setVisibility(View.VISIBLE);
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


                if (!firstFriendName.getText().equals(getResources().getString(R.string.empty_friends)))
                    deleteFirst.setVisibility(View.VISIBLE);
                if (!secondFriendName.getText().equals(getResources().getString(R.string.empty_friends)))
                    deleteSecond.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void alertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Warning")
                .setMessage("You have already selected this number as trusted friend!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
