package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.CAR;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.MOTO;
import static com.driveembetter.proevolutionsoftware.driveembetter.entity.Vehicle.VAN;

/**
 * Created by matteo on 03/10/17.
 */

public class VehiclesAdapter extends ArrayAdapter<Vehicle> {

    private final Context context;
    private final ArrayList<Vehicle> vehicles;
    private final ArrayList<String> revision_date_list;
    private final ArrayList<String> insurance_date_list;
    private String current_plate;



    public VehiclesAdapter(Context context, ArrayList<Vehicle> data, String current_vehicle, ArrayList<String> revision_date_list, ArrayList<String> insurance_date_list) {

        super(context,0, data);
        this.context = context;
        this.vehicles = data;
        this.current_plate = current_vehicle;
        this.insurance_date_list = insurance_date_list;
        this.revision_date_list = revision_date_list;
    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {

        if(vehicles.get(position).getType().equals(CAR) || vehicles.get(position).getType().equals("Auto")){
            return 0;
        }else if (vehicles.get(position).getType().equals(MOTO)){
            return 1;
        }else if (vehicles.get(position).getType().equals(VAN) || vehicles.get(position).getType().equals("Furgone")){
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
                row = inflater.from(getContext()).inflate(R.layout.item_car, null);
            }else if (listViewItemType == 1) {
                row = inflater.from(getContext()).inflate(R.layout.moto_item, null);
            }else if (listViewItemType == 2){
                row = inflater.from(getContext()).inflate(R.layout.van_item, null);
            }

            holder = new ViewHolder();
            holder.textView1 = row.findViewById(R.id.textView23);
            holder.textView2 = row.findViewById(R.id.textView24);
            holder.imageview = row.findViewById(R.id.current);
            holder.textView3 = row.findViewById(R.id.textView20);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Vehicle vehicle = vehicles.get(position);

        if (current_plate == null){
            if (insurance_is_expired(position)){
                holder.textView3.setVisibility(View.VISIBLE);
            }else{
                holder.textView3.setVisibility(View.INVISIBLE);
            }
            holder.textView1.setText(vehicle.getModel());
            holder.textView2.setText(vehicle.getNumberPlate());
            holder.imageview.setVisibility(View.INVISIBLE);


        } else{

            holder.textView1.setText(vehicle.getModel());
            holder.textView2.setText(vehicle.getNumberPlate());

            if (current_plate.equals(vehicle.getNumberPlate())){
                if (insurance_is_expired(position)){
                    holder.textView3.setVisibility(View.VISIBLE);
                }else{
                    holder.textView3.setVisibility(View.INVISIBLE);
                }
                holder.imageview.setVisibility(View.VISIBLE);

            }else if (!current_plate.equals(vehicle.getNumberPlate())){
                if (insurance_is_expired(position)){
                    holder.textView3.setVisibility(View.VISIBLE);
                }else{
                    holder.textView3.setVisibility(View.INVISIBLE);
                }
                holder.imageview.setVisibility(View.INVISIBLE);
            }
        }
        return row;
    }

    private boolean insurance_is_expired(int position) {

        String current_date = vehicles.get(position).getInsurance_date();
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


}
