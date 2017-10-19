package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.EMPTY_FIELD;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE_LIST;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VEHICLE_EXISTS_YET;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.VAN;

public class AddVehicleActivity extends AppCompatActivity {

    private Vehicle vehicle;
    private Button confirm;
    private EditText plateNumber;
    private EditText model;
    private EditText owner;
    private RadioGroup radioGroup;
    private RadioButton car;
    private RadioButton moto;
    private RadioButton van;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String type;
    private ArrayList<String> plates_list;
    private static boolean fromInsurance;
    private static int try_number;
    private static Button ok;
    private static RelativeLayout alert;

    private static EditText insurance_date;
    private static EditText revision_date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_vehicle);
        init_resources();
        Intent mIntent = getIntent();
        this.plates_list = mIntent.getStringArrayListExtra(PLATE_LIST);


        insurance_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fromInsurance = true;
                showTruitonDatePickerDialog(v);
            }


        });

        revision_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fromInsurance = false;
                showTruitonDatePickerDialog(v);
            }


        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                      switch (checkedId) {
                                                          case R.id.car:
                                                              setType(car.getText().toString());
                                                              System.out.println("TEEEEEEEEEEEEEEEEEEEEEEEXXXTTTT " + car.getText().toString());

                                                              break;
                                                          case R.id.moto:
                                                              setType(moto.getText().toString());
                                                              break;
                                                          case R.id.van:
                                                              setType(van.getText().toString());
                                                              break;
                                                      }
                                                  }

        });




        confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                if( radioGroup.getCheckedRadioButtonId() == -1 || isEmpty(plateNumber) || isEmpty(owner) || isEmpty(model) || isEmpty(insurance_date) || isEmpty(revision_date) ) {

                    Toast.makeText(getApplicationContext(), EMPTY_FIELD , Toast.LENGTH_SHORT).show();

                }else if (exists_yet(plateNumber.getText().toString())) {

                    Toast.makeText(getApplicationContext(), VEHICLE_EXISTS_YET, Toast.LENGTH_SHORT).show();

                }else{

                    vehicle = new Vehicle(getType(),
                            model.getText().toString(),
                            plateNumber.getText().toString(),
                            owner.getText().toString(),
                            insurance_date.getText().toString(),
                            revision_date.getText().toString());

                    addNewVehicle();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });



    }

    private void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private boolean exists_yet(String plate) {

        if (plates_list == null){
            return false;
        }else {
            int i;
            for (i = 0; i < plates_list.size(); i++) {
                if (plates_list.get(i).equals(plate))
                    return true;
            }
            return false;
        }
    }




    private void init_resources() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        confirm = (Button)findViewById(R.id.confirm);
        plateNumber = (EditText)findViewById(R.id.newplatenumber);
        owner = (EditText)findViewById(R.id.newowner);
        model = (EditText)findViewById(R.id.newmodel);
        radioGroup = (RadioGroup)findViewById(R.id.radiotype);
        car = (RadioButton)findViewById(R.id.car);
        moto = (RadioButton)findViewById(R.id.moto);
        van = (RadioButton)findViewById(R.id.van);
        insurance_date = (EditText)findViewById(R.id.insurance_date_plain);
        revision_date = (EditText)findViewById(R.id.rev);
        this.plates_list = new ArrayList<String>();
        this.fromInsurance = false;
        try_number = 1;
        this.alert = (RelativeLayout)findViewById(R.id.alert);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private void addNewVehicle(){

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");

        if( getType().equals(CAR) || getType().equals("Auto")){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+ ";" +vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }

        if( getType().equals(MOTO)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }

        if( getType().equals(VAN) || getType().equals("Furgone")){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner()+";"+vehicle.getInsurance_date()+";"+vehicle.getRevision_date());
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

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
                                show_alert_message();
                                insurance_date.setTextColor(Color.RED);
                                insurance_date.setText(day + "/" + (month + 1) + "/" + year);
                                insurance_date.startAnimation(shakeError());
                            }
                        }

                    }else{
                        try_number = 1;
                        insurance_date.setText(day + "/" + (month + 1) + "/" + year);
                    }
                } else
                    revision_date.setText(day + "/" + (month + 1) + "/" + year);
            }

        private TranslateAnimation shakeError() {
            TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
            shake.setDuration(700);
            shake.setInterpolator(new CycleInterpolator(7));
            return shake;
        }

        private void show_alert_message() {
            final View popupView = getActivity().getLayoutInflater().inflate(R.layout.insurance_alert, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            popupWindow.setFocusable(true);
            ok = (Button) popupView.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });

            popupWindow.showAtLocation(alert, Gravity.CENTER,0,0);
        }
    }
}



