package com.driveembetter.proevolutionsoftware.driveembetter.chartBuild;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.ArrayList;

/**
 * Created by alfredo on 28/08/17.
 */

/* Chart Async Task Class */
public class ChartAsyncTask extends AsyncTask<String, Double, ScatterData> {
    RetainedFragment fragment;
    private final ExtendedDoubleEvaluator evaluator;

    public ChartAsyncTask(RetainedFragment fragment) {
        this.fragment = fragment;
        setGraphProperties();

        /* Create a new extended evaluator */
        evaluator = new ExtendedDoubleEvaluator();
    }

    @Override
    /* This method perform a computation on a background thread */
    protected ScatterData doInBackground(String... params) {
        /* Retrieve parameters (function, startIndex and endIndex) from arguments */
        String function = params[0];
        String startIndex = params[1];
        String endIndex = params[2];

        /* Make computation */
        return this.calculate(function, Double.valueOf(startIndex), Double.valueOf(endIndex));
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
    public ScatterData calculate(String function, double startIndex, double endIndex) {

        /* Create a new empty variable set */
        final StaticVariableSet<Double> variables = new StaticVariableSet<>();
        boolean check;
        int k=0;
        double step = (endIndex - startIndex) / 10000;
        while(step>0.01){
            k++;
            step=(endIndex - startIndex) / (10000*Math.pow(10,k));
        }

        /* list of values */
        ArrayList<Entry> vals = new ArrayList<>();
        /* x axis */
        ArrayList<String> xVals = new ArrayList<>();

        for (double i = startIndex; i <= endIndex; i = i + step) {
            try {
                publishProgress((Math.abs(startIndex) + i) * 100 / (endIndex - startIndex));

                if (isCancelled()) {
                    return null;
                }
                check=true;
                /* Set the value of x */
                variables.set("x", (i));

                /* Add value to x axis */
                xVals.add(String.valueOf(Math.floor(i * 100) / 100));

                /* Evaluate the expression */
                Double result = evaluator.evaluate(function, variables);
                variables.set("x",(i+step));
                Double result2=evaluator.evaluate(function,variables);

                if(Math.abs(result-result2)>=0.1){
                    check=false;
                }
                if (!result.equals(Double.NEGATIVE_INFINITY) && !result.equals(Double.POSITIVE_INFINITY) && check) {
                    /* If expression is infinity, skip */
                    Entry entry = new Entry(result.floatValue(), (int) ((i-startIndex) / step));
                    vals.add(entry);
                }
            } catch(IllegalArgumentException w) {
                if (w.getMessage() == null || !w.getMessage().contains("Invalid argument passed to")) {
                    /* Syntax error */
                    return null;
                }
                /* ... else value is out of domain of the function */
            }
        }

        /* Create a new scatter data set and set properties */
        ScatterDataSet scatterDataSet = new ScatterDataSet(vals, "func");
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scatterDataSet.setColor(Color.RED);
        scatterDataSet.setScatterShapeSize(2f);

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