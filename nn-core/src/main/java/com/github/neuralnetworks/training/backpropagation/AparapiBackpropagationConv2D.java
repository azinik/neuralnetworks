package com.github.neuralnetworks.training.backpropagation;

import java.util.List;

import com.github.neuralnetworks.architecture.Connections;
import com.github.neuralnetworks.architecture.Conv2DConnection;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.Matrix;
import com.github.neuralnetworks.calculation.ValuesProvider;
import com.github.neuralnetworks.calculation.neuronfunctions.AparapiConv2D;
import com.github.neuralnetworks.util.Util;

/**
 * BackPropagation base function for convolutional layers
 */
public class AparapiBackpropagationConv2D extends AparapiConv2D implements BackpropagationConnectionCalculator {

    private static final long serialVersionUID = -345286029645674230L;

    /**
     * Activation of the output layer from the feedforward phase
     */
    protected float[] ffActivation;

    /**
     * weight updates and momentum
     */
    protected float[] weightUpdates;
    protected float[] weightUpdatesMomentum;

    /**
     * BP parameters
     */
    protected float learningRate;
    protected float momentum;
    protected float weightDecay;

    /**
     * activations from the feedforward phase
     */
    protected ValuesProvider activations;

    public AparapiBackpropagationConv2D(Conv2DConnection c, int miniBatchSize) {
	super(c, miniBatchSize);
    }

    @Override
    public void calculate(List<Connections> connections, ValuesProvider valuesProvider, Layer targetLayer) {
	Conv2DConnection c = null;

	for (Connections con : connections) {
	    if (con instanceof Conv2DConnection) {
		c = (Conv2DConnection) con;
	    }
	}

	if (c != null) {
	    // currently works only as a feedforward (including bp)
	    if (targetLayer == c.getOutputLayer()) {
		super.calculate(c, valuesProvider.getValues(Util.getOppositeLayer(c, targetLayer), c), valuesProvider.getValues(targetLayer, c));
	    } else {
		super.calculate(c, valuesProvider.getValues(targetLayer, c), valuesProvider.getValues(Util.getOppositeLayer(c, targetLayer), c));
	    }

	    updateWeights();
	}
    }

    @Override
    protected void init(Conv2DConnection c, Matrix input, Matrix output) {
	super.init(c, input, output);

	int weightUpdatesLength = c.getWeights().length * output.getColumns();
	if (weightUpdates == null || weightUpdates.length != weightUpdatesLength) {
	    weightUpdates = new float[weightUpdatesLength];
	    weightUpdatesMomentum = new float[weightUpdatesLength];
	} else {
	    Util.fillArray(weightUpdates, 0);
	}

	ffActivation = activations.getValues(c.getInputLayer(), c).getElements();
    }

    @Override
    protected void conv(int weightsStartId, int inputStartId) {
	int id = getGlobalId();

	int miniBatch = miniBatchSize;
	int fmw = featureMapWeights;
	float activationDerivative = 0;
	int inputId = 0;

	for (int p = 0; p < miniBatch; p++) {
	    activationDerivative = activationFunctionDerivative(output[id * miniBatch + p]);
	    output[id * miniBatch + p] = activationDerivative;

	    for (int i = 0; i < fmw; i++) {
		inputId = (inputStartId + featureMapOffsets[i]) * miniBatch + p;
		weightUpdates[weightsStartId + i] += activationDerivative * ffActivation[inputId];
		input[inputId] += activationDerivative * weights[weightsStartId + i];
	    }
	}
    }

    /**
     * Weight updates after the backpropagation
     */
    protected void updateWeights() {
	float weightUpdate = 0;
	for (int i = 0; i < weights.length; i++) {
	    weightUpdate = learningRate * weightUpdates[i] + momentum * weightUpdatesMomentum[i] - weightDecay * weights[i];
	    weights[i] += weightUpdate;
	    weightUpdatesMomentum[i] = weightUpdates[i];
	    weightUpdates[i] = weightUpdate;
	}
    }

    /**
     * Derivative of the FF activation function
     * 
     * @param value
     * @return
     */
    protected float activationFunctionDerivative(float value) {
	return value;
    }

    @Override
    public float getLearningRate() {
        return learningRate;
    }

    @Override
    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public float getMomentum() {
        return momentum;
    }

    @Override
    public void setMomentum(float momentum) {
        this.momentum = momentum;
    }

    @Override
    public float getWeightDecay() {
        return weightDecay;
    }

    @Override
    public void setWeightDecay(float weightDecay) {
        this.weightDecay = weightDecay;
    }

    @Override
    public ValuesProvider getActivations() {
        return activations;
    }

    @Override
    public void setActivations(ValuesProvider activations) {
        this.activations = activations;
    }
}
