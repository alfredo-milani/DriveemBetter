package com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.AddVehicleActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.Modify_vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.VehiclesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CURRENT;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.INS_DATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MODEL;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.OWNER;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.PLATE_LIST;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.REV_DATE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.TYPE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VAN;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.YES;


public class GarageFragment extends Fragment
    implements SingletonUser.UserDataCallback {

    private SingletonUser singletonUser;
    private SingletonFirebaseProvider singletonFirebaseProvider;
    private ListView listview;
    private TextView label;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<String> vehiclesName;
    private ImageButton delete;
    private ImageButton modify;
    private ImageButton select;
    private Button ok;
    private int selected_item;
    private FloatingActionButton fab;
    private ArrayList<String> plates_list;
    private TextView type;
    private TextView model;
    private TextView plate;
    private TextView owner;
    private TextView rev_date;
    private TextView ins_date;
    private RelativeLayout mRelativeLayout;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference current_ref;
    private Boolean clik_event;
    private String current_vehicle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return init_view(inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v ) {
                Intent intent = new Intent(getActivity(), AddVehicleActivity.class);
                startActivityForResult(intent, 1);
            }



        });

        this.singletonUser.getVehicles(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        this.singletonUser.getVehicles(this);
    }

    @Override
    public void onVehiclesReceive() {



        this.vehicles = this.singletonUser.getVehicleArrayList();

        vehicles_exist();
        getCurrentVhicle();

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {


                clik_event = true;
                selected_item = index;
                check_selected_item();
                v.setBackgroundColor(0xFFedf5ff);
                showOptions();

                modify.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), Modify_vehicle.class);
                        Bundle extras = new Bundle();

                        extras.putString(PLATE, vehicles.get(selected_item).getNumberPlate());
                        extras.putString(MODEL, vehicles.get(selected_item).getModel());
                        extras.putString(OWNER, vehicles.get(selected_item).getOwner());
                        extras.putStringArrayList(PLATE_LIST, plates_list);
                        extras.putString(TYPE,vehicles.get(selected_item).getType());
                        extras.putString(INS_DATE, vehicles.get(selected_item).getInsurance_date());
                        extras.putString(REV_DATE, vehicles.get(selected_item).getRevision_date());
                        extras.putString(CURRENT, current_vehicle);
                        intent.putExtras(extras);
                        startActivity(intent);
                        hideOptions();
                        onStart();

                    }
                });


                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        remove_old_current_vehicle();
                        add_new_current_vehicle(selected_item);
                        hideOptions();
                        clik_event = false;
                        onStart();

                    }
                });


                delete.setOnClickListener( new View.OnClickListener(){


                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are tou sure to delete :\n" + vehiclesName.get(selected_item));
                        builder.setCancelable(true);

                        builder.setPositiveButton(

                                YES,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        removeVehicleFromDb();
                                        vehicles.remove(selected_item);
                                        vehiclesName.remove(selected_item);
                                        plates_list.remove(selected_item);
                                        hideOptions();
                                        dialog.cancel();
                                        onStart();
                                    }
                                });

                        builder.setNegativeButton(

                                NO,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });

                return true;
            }
        });



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (clik_event == true){

                    if (position==selected_item){
                        view.setBackgroundColor(Color.TRANSPARENT);
                        hideOptions();
                    }else{

                        listview.getChildAt(selected_item).setBackgroundColor(Color.TRANSPARENT);
                        hideOptions();
                    }
                    clik_event =false;

                }else if (clik_event == false) {

                    show_details(position);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v ) {
                Intent intent = new Intent(getActivity(), AddVehicleActivity.class);
                int i;
                for(i=0;i<plates_list.size();i++){
                    System.out.println("LIIIIIISSSTTTTTT  " + i +" " + plates_list.get(i));
                }
                intent.putExtra(PLATE_LIST, plates_list);
                startActivity(intent);
            }


        });
    }

    private void vehicles_exist() {

        if (vehicles == null){
            this.label.setText("Hello! Add a new vehicle");
            listview.setVisibility(View.INVISIBLE);
        }else {
            listview.setVisibility(View.VISIBLE);
        }

    }

    private void remove_old_current_vehicle() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_vehicle");
        ref.removeValue();
    }

    private void add_new_current_vehicle(int selected_item) {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_vehicle");

        if( vehicles.get(selected_item).getType().equals(CAR)){

            ref.child(vehicles.get(selected_item).getNumberPlate())
                    .setValue(vehicles.get(selected_item).getType()+ ";" +vehicles.get(selected_item).getModel()+";"+vehicles.get(selected_item).getNumberPlate()+
                            ";"+vehicles.get(selected_item).getOwner()+";"+vehicles.get(selected_item).getInsurance_date()+";"+vehicles.get(selected_item).getRevision_date());
        }

        if( vehicles.get(selected_item).getType().equals(MOTO)){

            ref.child(vehicles.get(selected_item).getNumberPlate())
                    .setValue(vehicles.get(selected_item).getType()+";"+vehicles.get(selected_item).getModel()+";"+vehicles.get(selected_item).getNumberPlate()+";"+vehicles.get(selected_item).getOwner()+";"+vehicles.get(selected_item).getInsurance_date()+";"+vehicles.get(selected_item).getRevision_date());
        }

        if( vehicles.get(selected_item).getType().equals(VAN)){

            ref.child(vehicles.get(selected_item).getNumberPlate())
                    .setValue(vehicles.get(selected_item).getType()+";"+vehicles.get(selected_item).getModel()+";"+vehicles.get(selected_item).getNumberPlate()+";"+vehicles.get(selected_item).getOwner()+";"+vehicles.get(selected_item).getInsurance_date()+";"+vehicles.get(selected_item).getRevision_date());
        }

        singletonUser.setCurrentVehicle(vehicles.get(selected_item));
    }

    private View init_view(LayoutInflater inflater, ViewGroup container) {

        final View view = inflater.inflate(R.layout.fragment_garage, container, false);
        this.listview = (ListView) view.findViewById(R.id.listView);
        this.delete = (ImageButton) view.findViewById(R.id.delete);
        delete.setVisibility(View.GONE);
        this.modify = (ImageButton) view.findViewById(R.id.modify);
        modify.setVisibility(View.GONE);
        this.select = (ImageButton) view.findViewById(R.id.select);
        select.setVisibility(View.GONE);
        this.fab = (FloatingActionButton) view.findViewById(R.id.fab);
        this.mRelativeLayout = (RelativeLayout) view.findViewById(R.id.layout_garage);
        // Set action bar title
        this.getActivity().setTitle(R.string.garage);
        return view;

    }

    private void check_selected_item() {

        int current_pos;

        for (current_pos=0 ; current_pos<vehicles.size(); current_pos++) {
            if (current_pos != selected_item) {
                View vw = listview.getChildAt(current_pos);
                vw.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void removeVehicleFromDb() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");
        ref.child(vehicles.get(selected_item).getNumberPlate()).removeValue();

        if (vehicles.get(selected_item).getNumberPlate().equals(current_vehicle)){
            this.database = FirebaseDatabase.getInstance();
            this.current_ref = database.getReference("users/" + SingletonFirebaseProvider
                    .getInstance()
                    .getFirebaseUser()
                    .getUid() + "/current_vehicle");
            current_ref.child(current_vehicle).removeValue();
        }
    }

    private void show_details(int position) {

        final View popupView = getActivity().getLayoutInflater().inflate(R.layout.vehicle_informations, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);

        this.model = (TextView) popupView.findViewById(R.id.modelview);
        this.type = (TextView) popupView.findViewById(R.id.typeview);
        this.owner = (TextView) popupView.findViewById(R.id.ownerview);
        this.plate = (TextView) popupView.findViewById(R.id.plateview);
        this.ins_date = (TextView) popupView.findViewById(R.id.ins_date);
        this.rev_date = (TextView)popupView.findViewById(R.id.rev_date);
        this.ok = (Button) popupView.findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


        model.setText(vehicles.get(position).getModel());
        type.setText(TYPE + vehicles.get(position).getType());
        owner.setText(OWNER + vehicles.get(position).getOwner());
        plate.setText(PLATE + vehicles.get(position).getNumberPlate());
        ins_date.setText(vehicles.get(position).getInsurance_date());
        rev_date.setText(vehicles.get(position).getRevision_date());

        popupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }

    private void showOptions() {
        this.modify.setVisibility(View.VISIBLE);
        this.delete.setVisibility(View.VISIBLE);
        this.fab.setVisibility(View.INVISIBLE);
        this.select.setVisibility(View.VISIBLE);

    }

    private void hideOptions() {
        this.modify.setVisibility(View.INVISIBLE);
        this.delete.setVisibility(View.INVISIBLE);
        this.fab.setVisibility(View.VISIBLE);
        this.select.setVisibility(View.INVISIBLE);
    }

    private void populateListView() {

        this.plates_list.clear();
        this.vehicles = this.singletonUser.getVehicleArrayList();
        if (this.vehicles == null /* || this.vehicles.isEmpty() */) {
            return;
        }

        int i;
        for(i=0; i<vehicles.size();i++){
            this.vehiclesName.add(i, vehicles.get(i).getModel());
            this.plates_list.add(i, vehicles.get(i).getNumberPlate());
        }

        listview.setAdapter(new VehiclesAdapter(getContext(), vehicles , current_vehicle));
    }

    private void initResources() {

        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();
        this.singletonUser = this.singletonFirebaseProvider.getUserInformations();
        this.vehiclesName = new ArrayList<String>();
        this.plates_list = new ArrayList<String>();
        this.clik_event = false;
    }

    private void getCurrentVhicle() {

        this.database = FirebaseDatabase.getInstance();
        this.ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/current_vehicle");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    populateListView();

                }else {

                    String[] temp1 = dataSnapshot.getValue().toString().split("=");
                    String[] temp2 = temp1[1].split(";");
                    current_vehicle = temp2[2];
                    populateListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentState.setFragmentState(FragmentState.GARAGE_FRAGMENT, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        FragmentState.setFragmentState(FragmentState.GARAGE_FRAGMENT, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        FragmentState.setFragmentState(FragmentState.GARAGE_FRAGMENT, false);
    }
}
