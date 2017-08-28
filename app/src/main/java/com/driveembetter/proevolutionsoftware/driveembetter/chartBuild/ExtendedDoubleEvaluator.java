package com.driveembetter.proevolutionsoftware.driveembetter.chartBuild;

/**
 * Created by alfredo on 28/08/17.
 */

import com.fathzer.soft.javaluator.DoubleEvaluator;

import java.security.Policy;
import java.util.Iterator;
import java.util.function.Function;

/** A subclass of DoubleEvaluator that supports SQRT function.
 */
public class ExtendedDoubleEvaluator extends DoubleEvaluator {
    /** Defines the new function (square root).*/
    private static final Function SQRT = new Function("sqrt", 1);
    private static final Policy.Parameters PARAMS;

    static {
        // Gets the default DoubleEvaluator's parameters
        PARAMS = DoubleEvaluator.getDefaultParameters();
        // add the new sqrt function to these parameters
        PARAMS.add(SQRT);
    }

    public ExtendedDoubleEvaluator() {
        super(PARAMS);
    }

    @Override
    protected Double evaluate(Function function, Iterator<Double> arguments, Object evaluationContext) {
        if (function == SQRT) {
            // Implements the new function
            return Math.sqrt(arguments.next());
        } else {
            // If it's another function, pass it to DoubleEvaluator
            return super.evaluate(function, arguments, evaluationContext);
        }
    }

}