package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

public class AddFriendsActivity extends AppCompatActivity {

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

        this.firstFriendName = (TextView) findViewById(R.id.first_user_name_text);
        this.secondFriendName = (TextView) findViewById(R.id.second_user_name_text);
        this.firstNumber = (TextView) findViewById(R.id.first_phone_number_text);
        this.secondNumber = (TextView) findViewById(R.id.second_phone_number_text);
        this.firstButton = (Button) findViewById(R.id.first_button);
        this.secondButton = (Button) findViewById(R.id.second_button);
        this.firstFriendCard = (CardView) findViewById(R.id.firstFriendCard);
        this.secondFriendCard = (CardView) findViewById(R.id.secondFriendCard);

        SharedPreferences prefs = getSharedPreferences("friends_preference", MODE_PRIVATE);
        this.firstFriendName.setText(prefs.getString("name1", getResources().getString(R.string.empty_friends)));
        this.firstNumber.setText(prefs.getString("phone_no1", ""));

        this.secondFriendName.setText(prefs.getString("name2", getResources().getString(R.string.empty_friends)));
        this.secondNumber.setText(prefs.getString("phone_no2", ""));

        //check permissions
        if (!checkPermission())
            requestPermission();

        if (!checkPermission())
            this.finish();


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
        getFriends();
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


    //TODO SPOSTARLA

    private void getFriends() {
        SingletonUser user = SingletonUser.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                .child(NODE_USERS)
                .child(user.getUid());
        //CASE: FRIEND 1
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //CASE: FRIEND 2
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
