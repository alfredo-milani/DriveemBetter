package com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.EMPTY_FIELD;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MODEL;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.OWNER;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE_LIST;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.TYPE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VEHICLE_EXISTS_YET;

public class Modify_vehicle extends AppCompatActivity {


    private EditText newPlateNumber;
    private EditText newModel;
    private EditText newOwner;
    private Button confirm;
    private String plateNumber, owner, model, type;
    private ArrayList<String> plates_list;
    private Vehicle vehicle;
    private FirebaseDatabase database;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_vehicle);

        init_resources();
        Intent myIntent = getIntent();
        Bundle extras = myIntent.getExtras();
        getView(extras);



        confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                if (isEmpty(newPlateNumber) || isEmpty(newOwner) || isEmpty(newModel)) {

                    Toast.makeText(getApplicationContext(), EMPTY_FIELD, Toast.LENGTH_SHORT).show();

                } else if (exists_yet(newPlateNumber.getText().toString())) {

                    Toast.makeText(getApplicationContext(), VEHICLE_EXISTS_YET, Toast.LENGTH_SHORT).show();

                } else {

                    vehicle = new Vehicle(type,
                            newModel.getText().toString(),
                            newPlateNumber.getText().toString(),
                            newOwner.getText().toString());

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
                    .setValue(vehicle.getType()+ ";" +vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner());
        }

        if( type.equals(MOTO)){

            ref.child(vehicle.getNumberPlate())
                    .setValue(vehicle.getType()+";"+vehicle.getModel()+";"+vehicle.getNumberPlate()+";"+vehicle.getOwner());
        }

    }

    private void removeOldVehicleFromDb() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");
        ref.child(plateNumber).removeValue();
    }

    private void getView(Bundle extras) {

        this.plateNumber = extras.getString(PLATE);
        this.owner = extras.getString(OWNER);
        this.model = extras.getString(MODEL);
        this.plates_list = extras.getStringArrayList(PLATE_LIST);
        this.type = extras.getString(TYPE);
        this.newPlateNumber.setHint(plateNumber);
        this.newOwner.setHint(owner);
        this.newModel.setHint(model);

    }


    private void init_resources() {

        this.newPlateNumber = (EditText) findViewById(R.id.newplatenumber);
        this.newModel = (EditText) findViewById(R.id.newmodel);
        this.newOwner = (EditText) findViewById(R.id.newowner);
        this.confirm = (Button) findViewById(R.id.confirm);

    }


    private boolean exists_yet(String plate) {

        if (plates_list == null){
            return false;
        }else {
            int i;
            for (i = 0; i < plates_list.size(); i++) {
                if (plates_list.get(i).equals(plate)){
                    if (plates_list.get(i).equals(plateNumber))
                        return false;
                    else
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
}
