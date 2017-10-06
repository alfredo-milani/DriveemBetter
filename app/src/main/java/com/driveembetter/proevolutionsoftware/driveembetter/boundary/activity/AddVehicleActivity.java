package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.EMPTY_FIELD;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE_LIST;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VAN;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VEHICLE_EXISTS_YET;

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
    private int vehicleNumber;
    private String type;
    private String plate;
    private String mod;
    private String own;
    private ArrayList<String> plates_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        init_resources();

        Intent mIntent = getIntent();
        this.plates_list = mIntent.getStringArrayListExtra(PLATE_LIST);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                      switch (checkedId) {
                                                          case R.id.car:
                                                              setType(car.getText().toString());
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


                if( radioGroup.getCheckedRadioButtonId() == -1 || isEmpty(plateNumber) || isEmpty(owner) || isEmpty(model)) {

                    Toast.makeText(getApplicationContext(), EMPTY_FIELD , Toast.LENGTH_SHORT).show();

                }else if (exists_yet(plateNumber.getText().toString())) {

                    Toast.makeText(getApplicationContext(), VEHICLE_EXISTS_YET, Toast.LENGTH_SHORT).show();

                }else{

                    vehicle = new Vehicle(getType(),
                            model.getText().toString(),
                            plateNumber.getText().toString(),
                            owner.getText().toString());

                    addNewVehicle();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

            }

        });



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

        confirm = (Button)findViewById(R.id.confirm);
        plateNumber = (EditText)findViewById(R.id.newplatenumber);
        owner = (EditText)findViewById(R.id.newowner);
        model = (EditText)findViewById(R.id.newmodel);
        radioGroup = (RadioGroup)findViewById(R.id.radiotype);
        car = (RadioButton)findViewById(R.id.car);
        moto = (RadioButton)findViewById(R.id.moto);
        van = (RadioButton)findViewById(R.id.van);
        this.plates_list = new ArrayList<String>();

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

        if( getType().equals(CAR)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+ ";" +vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner());
        }

        if( getType().equals(MOTO)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner());
        }

        if( getType().equals(VAN)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner());
        }

    }





    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;

    }

}
