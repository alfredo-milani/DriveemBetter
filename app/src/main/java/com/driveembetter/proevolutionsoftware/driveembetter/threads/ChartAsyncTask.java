package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.StatisticsFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.statistics.SingletonScatterData;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/* Chart Async Task Class */
public class ChartAsyncTask extends AsyncTask<String, Double, ScatterData> {

    // Resources
    private StatisticsFragment fragment;

    public ChartAsyncTask(StatisticsFragment fragment) {
        this.fragment = fragment;
        this.setGraphProperties();
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.fragment.showProgress();
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
        /* Display a toast notification */
        Toast.makeText(fragment.getActivity().getApplicationContext(), fragment.getString(R.string.strAborted), Toast.LENGTH_LONG).show();
    }

    @Override
    /* Invoked on the UI thread after the background computation finishes */
    protected void onPostExecute(ScatterData data) {
        this.fragment.hideProgress();

        /* Get scatter data*/
        fragment.setData(data);
        if (data != null) {
            /* If data is valid, plot function */
            this.draw(data);
        } else {
            /* Display a toast notification */
            Toast.makeText(fragment.getActivity().getApplicationContext(), fragment.getString(R.string.strFunctionNotValid), Toast.LENGTH_LONG).show();
        }
    }

    /* Make computation */
    private ScatterData calculate() {

        SingletonScatterData sessionData = SingletonScatterData.getInstance();
        ScatterDataSet scatterDataSet;
        /* list of values */
        ArrayList<Entry> vals = new ArrayList<>();
        /* x axis */
        ArrayList<String> xVals = new ArrayList<>();
        float sampleSum, sampleSize, mean;

        MeanDay mean2 = MeanDay.getInstance();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        //TEST!!!
       /*int hour = date.getHours();
        for (int i = 0; i <= 24; i ++) {


            if (date.equals(mean2.getLocalDate())) { //stesso giorno


                if (mean2.getMap().get(hour) != null){
                    Mean meanDay = mean2.getMap().get(hour);
                    meanDay.setSampleSum(10);  // modificare con il valore della velocità
                    meanDay.setSampleSize();
                    mean2.getMap().put(hour, meanDay);
                } else{
                    Mean meanDay = new Mean();
                    meanDay.setSampleSum(50); //modificare con il valore della velocità
                    meanDay.setSampleSize();
                    mean2.getMap().put(hour, meanDay);
                }
            }else {
                mean2.getMap().clear();
                mean2.setLocalDate(date);
                Mean meanDay = new Mean();
                meanDay.setSampleSum(50); // modificare con il valore della velocità
                meanDay.setSampleSize();
                mean2.getMap().put(hour, meanDay);
            }
            System.out.println("Programma per " + i + " eseguito in ora " + hour + " giorno" );
            Log.e("c","Programma per " + i + " eseguito in ora " + hour + " giorno");

        }*/


        for (int i = 0; i <= Constants.HOURS; i++) {
            if(MeanDay.getInstance().getMap().get(i) != null) {

                xVals.add(String.valueOf(i));
                 /* Add entry with the mean of velocity */
                sampleSum = MeanDay.getInstance().getMap().get(i).getSampleSum();
                sampleSize = (float) MeanDay.getInstance().getMap().get(i).getSampleSize();
                mean = sampleSum / sampleSize;
                Entry entry = new Entry(mean, i);
                vals.add(entry);
            }else{
                xVals.add(String.valueOf(i));
                vals.add(new Entry(0,i));
            }

        }

        /* Create a new scatter data set and set properties */

        scatterDataSet = new ScatterDataSet(vals, "func");
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scatterDataSet.setColor(Color.BLUE);
        scatterDataSet.setScatterShapeSize(6f);
        sessionData.setValid(true); //validate data
        sessionData.setData(scatterDataSet);
        sessionData.setxVals(xVals);

        /* List of scatter data set*/
        ArrayList<IScatterDataSet> dataSets= new ArrayList<>();
        dataSets.add(scatterDataSet);

        return new ScatterData(xVals, dataSets);
    }

    /* Plot function */
    private void draw(ScatterData data) {
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

