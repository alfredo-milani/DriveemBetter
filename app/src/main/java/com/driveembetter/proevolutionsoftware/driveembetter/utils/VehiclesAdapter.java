package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;

import java.util.ArrayList;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.VAN;

/**
 * Created by matteo on 03/10/17.
 */

public class VehiclesAdapter extends ArrayAdapter<Vehicle> {

    private final Context context;
    private final ArrayList<Vehicle> vehicles;
    private String current_plate;



    public VehiclesAdapter(Context context, ArrayList<Vehicle> data, String current_vehicle) {

        super(context,0, data);
        this.context = context;
        this.vehicles = data;
        this.current_plate = current_vehicle;
    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {

        if(vehicles.get(position).getType().equals(CAR)){
            return 0;
        }else if (vehicles.get(position).getType().equals(MOTO)){
            return 1;
        }else if (vehicles.get(position).getType().equals(VAN)){
            return 2;
        }
        return -1;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;

        int listViewItemType = getItemViewType(position);


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            if (listViewItemType == 0) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.car_item, null);
            }else if (listViewItemType == 1) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.moto_item, null);
            }else if (listViewItemType == 2){
                row = LayoutInflater.from(getContext()).inflate(R.layout.van_item, null);
            }

            holder = new ViewHolder();
            holder.textView1 = (TextView)row.findViewById(R.id.textView23);
            holder.textView2 = (TextView)row.findViewById(R.id.textView24);
            holder.imageview = (ImageView)row.findViewById(R.id.current);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Vehicle vehicle = vehicles.get(position);

        if (current_plate == null){
            holder.textView1.setText(vehicle.getModel());
            holder.textView2.setText(vehicle.getNumberPlate());
            holder.imageview.setVisibility(View.INVISIBLE);


        } else{

            holder.textView1.setText(vehicle.getModel());
            holder.textView2.setText(vehicle.getNumberPlate());

            if (current_plate.equals(vehicle.getNumberPlate())){
                System.out.println("current + " + current_plate + " " +vehicle.getNumberPlate());
                holder.imageview.setVisibility(View.VISIBLE);
            }else if (!current_plate.equals(vehicle.getNumberPlate())){
                System.out.println("current + " + current_plate + " " +vehicle.getNumberPlate());
                holder.imageview.setVisibility(View.INVISIBLE);
            }
        }
        return row;
    }

}
