package com.driveembetter.proevolutionsoftware.driveembetter.chartBuild;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;



import java.util.ArrayList;

/* Chart Async Task Class */
public class ChartAsyncTask extends AsyncTask<String, Double, ScatterData> {
    RetainedFragment fragment;


    public ChartAsyncTask(RetainedFragment fragment) {
        this.fragment = fragment;
        setGraphProperties();


    }

    @Override
    /* This method perform a computation on a background thread */
    protected ScatterData doInBackground(String... params) {

        /* Make computation */
        return this.calculate();
    }

    @Override
    /* Called on the UI thread after cancel(boolean) is invoked */
    protected void onCancelled() {
        /* Dismiss progress dialog, removing it from the screen */
        fragment.getProgressDialog().dismiss();
        /* Mark task as cancelled */
        fragment.setTask(null);
        /* Display a toast notification */
        Toast.makeText(fragment.getActivity().getApplicationContext(), fragment.getString(R.string.strAborted), Toast.LENGTH_LONG).show();
    }

    @Override
    /* Invoked on the UI thread after the background computation finishes */
    protected void onPostExecute(ScatterData data) {
        /* Dismiss progress dialog, removing it from the screen */
        fragment.getProgressDialog().dismiss();
        /* Get scatter data*/
        fragment.setData(data);
        /* Mark task as cancelled */
        fragment.setTask(null);
        if (data != null) {
            /* If data is valid, plot function */
            this.draw(data);
        } else {
            /* Display a toast notification */
            Toast.makeText(fragment.getActivity().getApplicationContext(), fragment.getString(R.string.strFunctionNotValid), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    /* Invoked on the UI thread before the task is executed */
    protected void onPreExecute() {
        super.onPreExecute();
        /* Set progress dialog to 0 */
        fragment.getProgressDialog().setProgress(0);
        /* Show progress dialog */
        fragment.getProgressDialog().show();
    }

    @Override
    /* Update progress value */
    protected void onProgressUpdate(Double... values) {
        fragment.getProgressDialog().setProgress(values[0].intValue());
    }

    /* Make computation */
    public ScatterData calculate() {

        SingletonScatterData sessionData = SingletonScatterData.getInstance();
        ScatterDataSet scatterDataSet;
        /* list of values */
        ArrayList<Entry> vals = new ArrayList<>();
        /* x axis */
        ArrayList<String> xVals;

        if (!sessionData.isValid()) {
            xVals = new ArrayList<>();
            for (int i = 1; i <= 24; i++) {

                xVals.add(String.valueOf(i));

                    /* If expression is infinity, skip */
                Entry entry = new Entry((float) i + 10, i);

                vals.add(entry);

            }
        }
        else {
            xVals = sessionData.getxVals();
            for (int i = 25; i <= 40; i++) {

                xVals.add(String.valueOf(i));
                Entry entry = new Entry((float) i + 10, i);
                sessionData.getData().addEntry(entry);

            }
        }

        /* Create a new scatter data set and set properties */
        if(!sessionData.isValid()) {
            scatterDataSet = new ScatterDataSet(vals, "func");
            scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            scatterDataSet.setColor(Color.BLUE);
            scatterDataSet.setScatterShapeSize(4f);
            sessionData.setValid(true); //validate data
            sessionData.setData(scatterDataSet);
            sessionData.setxVals(xVals);
        }   else {
            scatterDataSet = sessionData.getData();
            sessionData.setValid(false); //invalidate data
        }
        /* List of scatter data set*/
        ArrayList<IScatterDataSet> dataSets= new ArrayList<>();
        dataSets.add(scatterDataSet);

        return new ScatterData(xVals, dataSets);
    }

    /* Plot function */
    public void draw(ScatterData data) {
        /* Check if data is valid */
        if (data == null) {
            return;
        }

        data.setDrawValues(false);
        fragment.getChart().setData(data);

        /* Redraw fragment.getChart() */
        //fragment.getChart().getAxisLeft().setValueFormatter(new LargeValueFormatter());
        fragment.getChart().notifyDataSetChanged();
        fragment.getChart().invalidate();
    }

    /* Set graph properties */
    private void setGraphProperties() {

        fragment.getChart().getLegend().setEnabled(false);

        // Sets the background color that will cover the whole fragment.getChart()-view
        fragment.getChart().setBackgroundColor(Color.WHITE);

        // Set a description text that appears in the bottom right corner of the fragment.getChart()
        fragment.getChart().setDescription("");

        // Enables / disables drawing the fragment.getChart() borders (lines surrounding the fragment.getChart())
        fragment.getChart().setDrawBorders(false);

        // Set the text that should appear if the fragment.getChart() is empty
        fragment.getChart().setNoDataTextDescription(String.valueOf(R.string.strNoGraph));


        // Flag that indicates if auto scaling on the y axis is enabled. If enabled the y axis automatically adjusts to the min and max y values of the current x axis range whenever the viewport changes. This is especially interesting for fragment.getChart()s displaying financial data. Default: false
        fragment.getChart().setAutoScaleMinMaxEnabled(true);

        // Sets the number of maximum visible drawn value-labels on the fragment.getChart(). This only takes affect when setDrawValues() is enabled.
        fragment.getChart().setMaxVisibleValueCount(10000);


        // Get Left Axis
        YAxis yAxis = fragment.getChart().getAxisLeft();


        // Enables / disables drawing the zero-line
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineColor(Color.YELLOW);

        // Set the axis enabled
        yAxis.setEnabled(true);

        // Enable drawing the labels of the axis
        yAxis.setDrawLabels(true);

        // The line alongside the axis (axis-line) should be drawn
        yAxis.setDrawAxisLine(true);

        // Enable drawing the grid lines for the axis
        yAxis.setDrawGridLines(true);

        // Get Right Axis
        yAxis = fragment.getChart().getAxisRight();

        // Set the axis enabled
        yAxis.setEnabled(false);


        // Get x Axis
        XAxis xAxis = fragment.getChart().getXAxis();

        // Set the axis enabled
        xAxis.setEnabled(true);

        // Enable drawing the labels of the axis
        xAxis.setDrawLabels(true);

        // The line alongside the axis (axis-line) should be drawn
        xAxis.setDrawAxisLine(true);

        // Enable drawing the grid lines for the axis
        xAxis.setDrawGridLines(true);

        // Sets the position where the XAxis should appear
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
    }

}

