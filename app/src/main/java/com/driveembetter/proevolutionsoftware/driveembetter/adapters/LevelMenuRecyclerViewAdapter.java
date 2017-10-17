package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;

/**
 * Created by sonu on 19/09/16.
 */

public class LevelMenuRecyclerViewAdapter
        extends RecyclerView
        .Adapter<LevelMenuRecyclerViewAdapter
        .RecyclerViewHolder>
        implements View.OnClickListener {

    private final static String TAG = LevelMenuRecyclerViewAdapter.class.getSimpleName();

    static class RecyclerViewHolder
            extends RecyclerView.ViewHolder {

        private TextView label;
        private RadioButton radioButton;

        RecyclerViewHolder(View view) {
            super(view);
            this.label = view.findViewById(R.id.label);
            this.radioButton = view.findViewById(R.id.radio_button);
        }

    }

    private SparseIntArray sparseIntArray;
    private Context context;
    private int selectedPosition;
    private int setPosition;



    public LevelMenuRecyclerViewAdapter(Context context, SparseIntArray sparseIntArray) {
        this.selectedPosition = Integer.MIN_VALUE;
        this.sparseIntArray = sparseIntArray;
        this.context = context;
        this.setPosition = Integer.MIN_VALUE;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_level_menu, viewGroup, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int i) {
        int key = this.sparseIntArray.keyAt(i);
        int stringID = this.sparseIntArray.get(key);
        holder.label.setText(this.context.getString(stringID));

        if (this.setPosition == key) {
            this.setPosition = -1;
            this.selectedPosition = holder.getAdapterPosition();
        }
        // Check the radio button if both position and selectedPosition matches
        holder.radioButton.setChecked(this.selectedPosition == i);

        // Set the position tag to both radio button and label
        holder.radioButton.setTag(i);
        holder.label.setTag(i);

        holder.radioButton.setOnClickListener(this);
        holder.label.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.itemCheckChanged(view);
    }

    // On selecting any view set the current position to selectedPositon and notify adapter
    private void itemCheckChanged(View v) {
        this.selectedPosition = (Integer) v.getTag();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (this.sparseIntArray != null ? this.sparseIntArray.size() : 0);
    }

    // Return the selectedPosition item
    public int getSelectedItem() {
        return this.sparseIntArray.keyAt(this.selectedPosition);
    }

    public void setRadioButtonValue(int key) {
        this.setPosition = key;
    }
}