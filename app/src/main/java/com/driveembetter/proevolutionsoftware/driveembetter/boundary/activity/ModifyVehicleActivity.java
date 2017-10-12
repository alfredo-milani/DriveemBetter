package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CURRENT;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.EMPTY_FIELD;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.INS_DATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MODEL;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.OWNER;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE_LIST;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.REV_DATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.TYPE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VAN;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VEHICLE_EXISTS_YET;

public class ModifyVehicleActivity extends FragmentActivity {


    private EditText newPlateNumber;
    private EditText newModel;
    private EditText newOwner;
    private static EditText revision_date_plain;
    private static EditText insurance_date_plain;
    private Button confirm;
    private String plateNumber;
    private String owner;
    private String model;
    private String type;
    private static String insurance_date;
    private String revision_date;
    private ArrayList<String> plates_list;
    private static Vehicle vehicle;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String current_plate;
    private static boolean fromInsurance;
    private DatabaseReference current_ref;
    private static Button ok;
    private static RelativeLayout alert;
    private static int try_number;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_vehicle);
        init_resources();
        Intent myIntent = getIntent();
        Bundle extras = myIntent.getExtras();
        getView(extras);

        insurance_date_plain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fromInsurance = true;
                showTruitonDatePickerDialog(v);
            }


        });

        revision_date_plain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fromInsurance = false;
                showTruitonDatePickerDialog(v);
            }


        });

        confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (isEmpty(newPlateNumber) || isEmpty(newOwner) || isEmpty(newModel) || isEmpty(insurance_date_plain) || isEmpty(revision_date_plain)) {
                    Toast.makeText(getApplicationContext(), EMPTY_FIELD, Toast.LENGTH_SHORT).show();
                } else if (exists_yet(newPlateNumber.getText().toString())) {
                    Toast.makeText(getApplicationContext(), VEHICLE_EXISTS_YET, Toast.LENGTH_SHORT).show();
                } else {
                    vehicle = new Vehicle(type,
                            newModel.getText().toString(),
                            newPlateNumber.getText().toString(),
                            newOwner.getText().toString(),
                            insurance_date_plain.getText().toString(),
                            revision_date_plain.getText().toString());
                    removeOldVehicleFromDb();
                    addModifiedVehicle();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void addModifiedVehicle() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");
        if( type.equals(CAR)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+ ";" +vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }
        if( type.equals(MOTO)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }
        if( type.equals(VAN)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }

        if(current_plate.equals(plateNumber))
            add_modified_current_vehicle();
    }

    private void add_modified_current_vehicle() {

        if(current_ref==null|| current_plate==null)
            return;

        if(current_ref.child(current_plate) == null) {
            return;

        } else {

            current_ref.child(current_plate).removeValue();

            if( type.equals(CAR)){

                current_ref.child(vehicle.getNumberPlate())
                        .setValue(vehicle.getType()+ ";" +vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
            }
            if( type.equals(MOTO)){
                current_ref.child(vehicle.getNumberPlate())
                        .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
            }
            if( type.equals(VAN)){
                current_ref.child(vehicle.getNumberPlate())
                        .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
            }
        }
    }

    private void removeOldVehicleFromDb() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");
        ref.child(plateNumber).removeValue();
        this.current_ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_vehicle");
        if(current_ref.getKey() == null){
            return;
        } else if (current_ref.getKey().equals(current_plate)){
            this.database = FirebaseDatabase.getInstance();
            current_ref.child(current_plate).removeValue();
        }
    }

    private void getView(Bundle extras) {

        this.plateNumber = extras.getString(PLATE);
        this.owner = extras.getString(OWNER);
        this.model = extras.getString(MODEL);
        this.plates_list = extras.getStringArrayList(PLATE_LIST);
        this.type = extras.getString(TYPE);
        this.insurance_date = extras.getString(INS_DATE);
        this.revision_date = extras.getString(REV_DATE);
        this.current_plate = extras.getString(CURRENT);
        this.newPlateNumber.setText(plateNumber);
        this.newOwner.setText(owner);
        this.newModel.setText(model);
        if (insurance_is_expired()){
            insurance_date_plain.setTextColor(Color.RED);
            insurance_date_plain.setText(insurance_date);
            insurance_date_plain.startAnimation(shakeError());
        }else{
            this.insurance_date_plain.setText(insurance_date);
            this.insurance_date_plain.setTextColor(Color.BLACK);

        }
        this.revision_date_plain.setText(revision_date);
    }

    private static boolean insurance_is_expired() {

        String current_date = insurance_date;
        Date today = new Date();
        String myFormatString = "dd/MM/yy";
        SimpleDateFormat df = new SimpleDateFormat(myFormatString);
        Date givenDate = null;
        try {
            givenDate = df.parse(current_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(givenDate.before(today))
            return true;
        return false;
    }

    public static TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(700);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    private void init_resources() {

        this.newPlateNumber = (EditText) findViewById(R.id.newplatenumber);
        this.newModel = (EditText) findViewById(R.id.newmodel);
        this.newOwner = (EditText) findViewById(R.id.newowner);
        this.insurance_date_plain = (EditText)findViewById(R.id.insurance_date_plain);
        this.revision_date_plain = (EditText) findViewById(R.id.revision_date_plain);
        this.confirm = (Button) findViewById(R.id.confirm);
        try_number = 1;
        this.alert = (RelativeLayout)findViewById(R.id.modify_rel);
    }

    private boolean exists_yet(String plate) {

        if (plates_list == null){
            return false;
        }else {
            int i;
            for (i = 0; i < plates_list.size(); i++) {
                if (plates_list.get(i).equals(plate)){
                    if (plates_list.get(i).equals(plateNumber)){
                        return false;
                    }else
                        return true;
                }
            }
            return false;
        }
    }

    private boolean isEmpty(EditText etText) {

        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    private void showTruitonDatePickerDialog(View v) {

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Date current = new Date();
            String myFormatString = "dd/MM/yy";
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date givenDate = null;
            try {
                givenDate = df.parse(day + "/" + (month + 1) + "/" + year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long l = givenDate.getTime();
            Date next = new Date(l);
            if (fromInsurance) {
                if (next.before(current)) {
                    if (try_number <= 1) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please control insurance expiration date", Toast.LENGTH_LONG).show();
                        try_number +=1;
                        System.out.println(" TRY NUMBER <1");
                    }else{
                        if (try_number >1) {
                            show_alert_message(true);
                            insurance_date_plain.setTextColor(Color.RED);
                            insurance_date_plain.setText(day + "/" + (month + 1) + "/" + year);
                        }
                    }

                }else{
                    try_number = 1;
                    insurance_date_plain.setTextColor(Color.BLACK);
                    insurance_date_plain.setText(day + "/" + (month + 1) + "/" + year);
                }
            } else
                revision_date_plain.setText(day + "/" + (month + 1) + "/" + year);
        }

        private void show_alert_message(final Boolean expiration) {
            final View popupView = getActivity().getLayoutInflater().inflate(R.layout.insurance_alert, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            popupWindow.setFocusable(true);
            ok = (Button) popupView.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    if (expiration)
                        insurance_date_plain.startAnimation(shakeError());
                }
            });

            popupWindow.showAtLocation(alert, Gravity.CENTER,0,0);
        }
    }
}
