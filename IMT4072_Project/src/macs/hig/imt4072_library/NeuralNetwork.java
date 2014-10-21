package macs.hig.imt4072_library;

import java.util.ArrayList;
import java.util.Random;

/**
Abstract class for Neural networks, to ease creation
of different neural network types and classes.
@Author  Knut Lucas Andersen
*/
public abstract class NeuralNetwork {
	//the sum of the weights
	private double _weightedSum;
	//matrix containing all the weights
	private Matrix _weightMatrix;
	//matrix containing the calculated output
	private Matrix _outputMatrix;
	//learning rate for the perception
	private double _learningRate;
	//list containing input values 
	private ArrayList<ArrayList<Double>> _inputList;
	//list containing desired output values
	ArrayList<Double> _desiredOutputList;
	
	/**
	Default constructor
	*/
	public NeuralNetwork() {
		_weightedSum = 0;
		_learningRate = 0;
		_weightMatrix = new Matrix();
		_outputMatrix = new Matrix();
		_inputList = new ArrayList<ArrayList<Double>>();
		_desiredOutputList = new ArrayList<Double>();
	} //constructor

	/***
	Constructor creating a Neural network object.
	
	@param threshold - double: The threshold for this network
	@param inputList: ArrayList<ArrayList<Double>> - List containing the input
	@throws ExceptionClass 
	*/
	public NeuralNetwork(ArrayList<ArrayList<Double>> inputList, ArrayList<Double> desiredOutputList) throws ExceptionClass {
		if(!inputList.isEmpty() && !desiredOutputList.isEmpty()) {
			_inputList = inputList;
			setLearningRate();
			_desiredOutputList = new ArrayList<Double>(desiredOutputList);
			//fill weight matrix with (random) values
			_outputMatrix = new Matrix(inputList.size(), 1);
			_weightMatrix = new Matrix(inputList.get(0).size(), 1);
			ArrayList<Double> weightList = fillWeightList(inputList.get(0).size());
			_weightMatrix.fillMatrixWithValues(0, weightList);
			/****CHEAT/DEBUG TEST***
			_weightMatrix.changeCellValueAt(0, 0, 1);
			_weightMatrix.changeCellValueAt(1, 0, -1);
			_weightMatrix.changeCellValueAt(2, 0, 0);
			_learningRate = 0.1;
			/****END CHEAT/DEBUG TEST***/
			_weightMatrix.transposeMatrix();
		} //if
	} //constructor

	/**
	Fills the passed matrix with the values from passed list.
	
	@param matrix - Matrix: Matrix to fill with values
	@param valueList: ArrayList<ArrayList<Double>> - List with values to add to matrix
	@throws ExceptionClass 
	*/
	void fillMatrixWithValues(Matrix matrix, ArrayList<ArrayList<Double>> valueList) throws ExceptionClass {
		int counter = 0;
		for (ArrayList<Double> list : valueList) {
			matrix.fillMatrixWithValues(counter, list);
			counter++;
		} //for
	} //fillMatrixWithValues

	/**
	Fills a list with random values between 0.1 - 1.0, to use as weights
	*/
	ArrayList<Double> fillWeightList(int listCount) {
		int counter = 0;
		double randomNo = 0;
		Random rand = new Random();
		ArrayList<Double> weightList = new ArrayList<Double>();
		while(counter < listCount) {
			randomNo = 1 + rand.nextInt(100);
			randomNo = randomNo / 100;
			weightList.add(randomNo);
			counter++;
		} //while
		return weightList;
	} //fillWeightList

	/**
	train the perceptron until weights give results matching criteria for desired output
	@throws ExceptionClass 
	*/
	void trainSingularPerceptron() throws ExceptionClass {
		int counter = 0;
		double desOutput = 0,
			output = 0,
			runningError = 0;
		Matrix inputMatrix = new Matrix();
		//loop through the input values
		for (ArrayList<Double> list : _inputList) {
			//get values for this round
			desOutput = _desiredOutputList.get(counter);
			inputMatrix = new Matrix(list.size(), 1);
			inputMatrix.fillMatrixWithValues(0, list);
			//get the values for the multiplied matrices
			output = inputMatrix.matrixMultiplication(_weightMatrix, inputMatrix, false).getCellValueAt(0, 0);
			_outputMatrix.changeCellValueAt(counter, 0, output);
			//is the resulting output equal to the desired output?
			if(desOutput != checkCalculatedPerceptrpn(output)) {
				adjustWeights(inputMatrix, desOutput, output);
				runningError += calculateCurrentError(desOutput, output);
				//if a numerical format error occured, just set error to 1/2
				if(Double.isNaN(runningError)) {
					runningError = 0.5;
				} //if
			} //if
			counter++;
		} //foreach
		//the training cycle is completed, but did any errors occur?
		if(runningError > 0) {
			trainSingularPerceptron();
		} //if
	} //trainPerceptron

	/**
	Based on the formula runningError = E; 
	E = E + 1/2 * (y - o)2
	*/
	double calculateCurrentError(double desOutput, double output) {
		double half = 0.5,
			error = 0,
			outputPow2 = 0;
		//	outputPow2 = || y - o ||2 
		outputPow2 = desOutput - output;
		outputPow2 = Math.pow(outputPow2, 2);
		error = (half*outputPow2);
		return error;
	} //calculateCurrentError

	/**
	Adjusts the weight matrix by use of the following formula:
	_weightMatrix = _weightMatrix + (desOutput - output) * inputMatrix; i = 1, ..., m
	------------------
	@throws ExceptionClass 
	*/
	void adjustWeights(Matrix inputMatrix, double desOutput, double output) throws ExceptionClass {
		double cellValue = 0,
			nTimesOutput = 0;
		//multiply learning rate with the desired and result output
		nTimesOutput = _learningRate * (desOutput - output);
		//create a new matrix with the same values as the passed input matrix
		Matrix tempMatrix = new Matrix(inputMatrix.getNumRows(), inputMatrix.getNumColumns());
		//fill tempMatrix with inputMatrix values
		for(int rowNo = 0; rowNo < inputMatrix.getNumRows(); rowNo++) {
			for(int columnNo = 0; columnNo < inputMatrix.getNumColumns(); columnNo++) {
				cellValue = inputMatrix.getCellValueAt(rowNo, columnNo);
				tempMatrix.changeCellValueAt(rowNo, columnNo, cellValue);
			} //for
		} //for
		//multiply the cells in the tempmatrix with the nTimesOutput result, 
		//transpose tempMatrix to match _weightMatrix and then and update the 
		//_weightMatrix by adding this with the tempMatrix
		tempMatrix.multiplyCellsWithValue(nTimesOutput);
		tempMatrix.transposeMatrix();
		_weightMatrix = tempMatrix.matrixAddition(_weightMatrix, tempMatrix);
	} //adjustWeights

	/**
	Returns a value for the mathematical function for the calculated 
	output. 
	f = { 1 IF output > 0, -1 otherwise
	
	@param output - double: the currently calculated output
	@return double:  (output > 0) ? 1 : -1;
	*/
	double checkCalculatedPerceptrpn(double output) {
		return (output > 0) ? 1 : -1;
	} //calculatePerceptron

	/**
	Calculates the weighted sum based on the content in the matrix inputMatrix
	and _weightMatrix.
	@throws ExceptionClass 
	*/
	void calculateWeightedSum(Matrix inputMatrix) throws ExceptionClass {
		Matrix multpliedMatrix = new Matrix();
		_weightMatrix.transposeMatrix();
		//multiply the matrices to get the weighted sum
		multpliedMatrix.matrixMultiplication(inputMatrix, _weightMatrix, false);
		//loop through values to get the weighted sum
		for(int rowNo = 0; rowNo < multpliedMatrix.getNumRows(); rowNo++) {
			for(int columnNo = 0; columnNo < multpliedMatrix.getNumColumns(); columnNo++) {
				_weightedSum += multpliedMatrix.getCellValueAt(rowNo, columnNo);
			} //for
		} //for
	} //calculateWeightedSum

	void setLearningRate() {
		double randomNo = 0;
		Random rand = new Random();
		//set learning rate
		do {
			randomNo = 1 + rand.nextInt(7); 
			randomNo = randomNo / 10;
			_learningRate = randomNo;
		} while(_learningRate <= 0);
	} //setLearningRate

	Matrix getWeightedMatrix() {
		return _weightMatrix;
	} //getWeightedMatrix

	Matrix getResultingOutputMatrix() {
		return _outputMatrix;
	} //getOutputMatrix

	double getLearningRate() {
		return _learningRate;
	} //getLearningRate

	/**
	Returns the weighted sum
	*/
	double getWeightedSum() {
		return _weightedSum;
	} //getWeightedSum

}
