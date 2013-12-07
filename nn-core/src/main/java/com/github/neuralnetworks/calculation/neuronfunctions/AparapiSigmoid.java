package com.github.neuralnetworks.calculation.neuronfunctions;

import com.github.neuralnetworks.calculation.ConnectionCalculatorImpl;

public class AparapiSigmoid extends ConnectionCalculatorImpl {

    private static final long serialVersionUID = 5869298546838843306L;

    public AparapiSigmoid() {
	super(new AparapiSigmoidByRows(), new AparapiSigmoidByColumns());
    }

    public static class AparapiSigmoidByRows extends AparapiWeightedSumByRows {

	private static final long serialVersionUID = -3409078521599849086L;

	@Override
	protected void after(int row, int column) {
	    output[outputBaseIndex(row, column)] = 1 / (1 + exp(-output[outputBaseIndex(row, column)]));
	}
    }

    public static class AparapiSigmoidByColumns extends AparapiWeightedSumByColumns {

	private static final long serialVersionUID = -3409078521599849086L;

	@Override
	protected void after(int row, int column) {
	    output[outputBaseIndex(row, column)] = 1 / (1 + exp(-output[outputBaseIndex(row, column)]));
	}
    }
}