package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.fragment.RetainedFragment;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonScatterData;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;
import java.util.Random;

/* Chart Async Task Class */
public class ChartAsyncTask extends AsyncTask<String, Double, ScatterData> {

    private final static String TAG = ChartAsyncTask.class.getSimpleName();
    private final static int HOURS_IN_DAY = 24;
    private final static int DAYS_IN_WEEK = 7;

    RetainedFragment fragment;

    public ChartAsyncTask(RetainedFragment fragment) {
        this.fragment = fragment;
        setGraphProperties();
    }



    @Override
    /* This method perform a computation on a background thread */
    protected ScatterData doInBackground(String... params) {
        String value = params[0];
        String valueTime = params[1];
        /* Make computation */
        return this.calculate(value, valueTime);
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
    public ScatterData calculate(String value, String valueTime) {

        SingletonScatterData sessionData = SingletonScatterData.getInstance();
        ScatterDataSet scatterDataSet;
        /* list of values */
        ArrayList<Entry> vals = new ArrayList<>();
        /* x axis */
        ArrayList<String> xVals = new ArrayList<>();
        float sampleSum, sampleSize, mean;

       /* for (int i = 0; i <= Constants.HOURS; i++) {
            if(MeanDay.getInstance().getMap().get(i) != null) {
                xVals.add(String.valueOf(i));

                sampleSum = MeanDay.getInstance().getMap().get(i).getSampleSumVelocity();
                sampleSize = (float) MeanDay.getInstance().getMap().get(i).getSampleSizeVelocity();
                mean = sampleSum / sampleSize;
                Entry entry = new Entry(mean, i);
                vals.add(entry);
            }else{
                xVals.add(String.valueOf(i));
                vals.add(new Entry(0,i));
            }
        }*/

        if (value.equals(Constants.VELOCITY)) {

            if  (valueTime.equals(Constants.STR_WEEK)){

                for (int i = 0; i < DAYS_IN_WEEK; i++) {
                    if (SingletonUser.getInstance().getMeanWeek().getMap().get(i) != null) {

                        xVals.add(String.valueOf(i));
                        sampleSum = SingletonUser.getInstance().getMeanWeek().getMap().get(i).getSampleSumAcceleration();
                        sampleSize = SingletonUser.getInstance().getMeanWeek().getMap().get(i).getSampleSizeAcceleration();
                        mean = Math.abs(sampleSum / sampleSize);
                        Entry entry = new Entry(mean, i);
                        vals.add(entry);
                    } else {
                        xVals.add(String.valueOf(i));
                        vals.add(new Entry(0, i));
                    }


                }
            } else{

                for (int i = 0; i < HOURS_IN_DAY; i++) {
                    if (SingletonUser.getInstance().getMeanDay().getMap().get(i) != null) {

                        xVals.add(String.valueOf(i));
                        sampleSum = SingletonUser.getInstance().getMeanDay().getMap().get(i).getSampleSumAcceleration();
                        sampleSize = SingletonUser.getInstance().getMeanDay().getMap().get(i).getSampleSizeAcceleration();
                        mean = sampleSum / sampleSize;
                        Entry entry = new Entry(mean + 1, i);
                        vals.add(entry);
                    } else {
                        xVals.add(String.valueOf(i));
                        vals.add(new Entry(0, i));
                    }


                }
            }
            scatterDataSet = new ScatterDataSet(vals, "km/h");

        } else{

            if  (valueTime.equals(Constants.STR_WEEK)){

                for (int i = 0; i < DAYS_IN_WEEK; i++) {

                    if (SingletonUser.getInstance().getMeanWeek().getMap().get(i) != null) {

                        xVals.add(String.valueOf(i));
                        sampleSum = SingletonUser.getInstance().getMeanWeek().getMap().get(i).getSampleSumAcceleration();
                        sampleSize =  SingletonUser.getInstance().getMeanWeek().getMap().get(i).getSampleSizeAcceleration();
                        mean = sampleSum / sampleSize;
                        Entry entry = new Entry(mean, i);
                        vals.add(entry);

                    } else {

                        xVals.add(String.valueOf(i));
                        vals.add(new Entry(0, i));
                    }


                }
            } else{

                for (int i = 0; i < HOURS_IN_DAY; i++) {

                    if (SingletonUser.getInstance().getMeanDay().getMap().get(i) != null) {

                        xVals.add(String.valueOf(i));
                        sampleSum = SingletonUser.getInstance().getMeanDay().getMap().get(i).getSampleSumAcceleration();
                        sampleSize = SingletonUser.getInstance().getMeanDay().getMap().get(i).getSampleSizeAcceleration();
                        mean = Math.abs(sampleSum / sampleSize);
                        Entry entry = new Entry(mean, i);
                        vals.add(entry);

                    } else {

                        xVals.add(String.valueOf(i));
                        vals.add(new Entry(0, i));
                    }


                }
            }
            scatterDataSet = new ScatterDataSet(vals, "10^-3 km/h^2");
        }

        /* Create a new scatter data set and set properties */
        //scatterDataSet = new ScatterDataSet(vals, "statistics");
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scatterDataSet.setColor(Color.BLUE);
        scatterDataSet.setScatterShapeSize(10);
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        sessionData.setValid(true); //validate data
        sessionData.setData(scatterDataSet);
        sessionData.setxVals(xVals);

        /* List of scatter data set*/
        ArrayList<IScatterDataSet> dataSets= new ArrayList<>();
        dataSets.add(scatterDataSet);

        return new ScatterData(xVals, dataSets);
    }

    public static void fillMeanWeekDay(int ore, int giorniSettimana, int type) {
        MeanDay mean2 = SingletonUser.getInstance().getMeanDay();
        MeanWeek mean3 = SingletonUser.getInstance().getMeanWeek();

        switch (type) {
            case 0:
                for (int i = 0; i < ore; ++i) {
                    Mean meanDay = new Mean();
                    meanDay.setSampleSumAcceleration(new Random().nextInt(40)); // modificare con il valore della velocità
                    meanDay.setSampleSumVelocity(new Random().nextInt(50));
                    meanDay.setSampleSizeAcceleration();
                    meanDay.setSampleSizeVelocity();
                    mean2.getMap().put(i, meanDay);
                }

                for (int i = 0; i < giorniSettimana; ++i) {
                    Mean meanWeek = new Mean();
                    meanWeek.setSampleSumAcceleration(new Random().nextInt(40)); // modificare con il valore della velocità
                    meanWeek.setSampleSumVelocity(new Random().nextInt(50));
                    meanWeek.setSampleSizeAcceleration();
                    meanWeek.setSampleSizeVelocity();
                    mean3.getMap().put(i, meanWeek);
                }
                break;

            case 1:
                for (int i = 0; i < ore; i+=3) {
                    Mean meanDay = new Mean();
                    meanDay.setSampleSumAcceleration(new Random().nextInt(40)); // modificare con il valore della velocità
                    meanDay.setSampleSumVelocity(new Random().nextInt(50));
                    meanDay.setSampleSizeAcceleration();
                    meanDay.setSampleSizeVelocity();
                    mean2.getMap().put(i, meanDay);
                }

                for (int i = 0; i < giorniSettimana; i+=2) {
                    Mean meanWeek = new Mean();
                    meanWeek.setSampleSumAcceleration(new Random().nextInt(40)); // modificare con il valore della velocità
                    meanWeek.setSampleSumVelocity(new Random().nextInt(50));
                    meanWeek.setSampleSizeAcceleration();
                    meanWeek.setSampleSizeVelocity();
                    mean3.getMap().put(i, meanWeek);
                }
                break;
        }
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

        fragment.getChart().getLegend().setEnabled(true);

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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Only positive value
        fragment.getChart().getAxisLeft().setAxisMinValue(0);
        fragment.getChart().getAxisRight().setAxisMinValue(0);
    }
}

