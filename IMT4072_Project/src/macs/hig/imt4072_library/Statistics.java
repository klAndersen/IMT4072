package macs.hig.imt4072_library;

import java.util.ArrayList;

/**
	Class basically for statistics calculations. 
	Calculates statistics like: 
	- Calculate mean <br />
	- Calculate variance <br />
	- Calculate standard deviation <br />
	- Calculate co-variance <br />
	- Calculate correlation co-efficient <br />
	- Calculate linear regression
	@Author  Knut Lucas Andersen
*/
public class Statistics {
	//constant used in error messages when failing at variance calculations
	final static String FAILED_AT_VARIANCE_CALCULATION = "\nCan't calculate the co-variance.";

	/**
	Default constructor
	*/
	public Statistics() {

	} //constructor

	/**
	Calculate the mean based on the values in the passed list.
	Requires a list filled with integers.
	@param valueList - ArrayList<Integer> list of values for calculating the mean
	@return double: mean || 0 (if mean couldn't be calculated)
	*/
	double calculateMeanInteger(ArrayList<Integer> valueList) {
		double mean = 0;
		int n = valueList.size();
		mean = sumOfListInteger(valueList);
		//check if mean is greater then zero 
		//(to avoid divide-by-zero error)
		if(mean > 0) {
			mean = mean / n;
		} //if
		return mean;
	} //calculateMean

	/**
	Calculate the mean based on the values in the passed list.
	Requires a list filled with double.
	@param valueList - ArrayList<Double>: list of values for calculating the mean
	@return double: mean || 0 (if mean couldn't be calculated)
	*/
	double calculateMeanDouble(ArrayList<Double> valueList) {
		double mean = 0;
		int n = 0;
		n = valueList.size();
		mean = sumOfListDouble(valueList);
		//check if mean is greater then zero 
		//(to avoid divide-by-zero error)
		if(mean > 0) {
			mean = mean / n;
		} //if
		return mean;
	} //calculateMean

	/**
	Calculates the variance for the values passed in the list valueList.
	The function checks that the passed mean is greater then zero, and then
	calculates the variance, which then gets returned.
	@param valueList - ArrayList<Double>: The list of values to be used to calculate the variance
	@param mean - double: the mean based on the values in valueList
	@return double: calculated variance || 0 (if variance couldn't be calculated)
	*/
	double calculateVariance(ArrayList<Double> valueList, double mean) {
		double variance = 0,
			sumOfValues = 0;
		int n = 0;
		if(mean > 0) {
			n = valueList.size();
			//calculate the sum of the values
			for(int i = 0; i < n; i++) {
				sumOfValues = valueList.get(i) - mean;
				variance += Math.pow(sumOfValues, 2);
			} //for
			//check that the sum of the values is greater then zero
			//and then calculate the variance
			if(variance > 0) {
				variance = variance / (n - 1);
			} //if
		} //if
		return variance;
	} //calculateVariance

	/**
	Calculates the standard deviance. It checks that the passed variance is
	greater then zero and then uses Math.sqrt() to calculate the stdDev.
	@param variance - double: the variance for the the value to get stdDev
	@return double: calculated standard deviance || 0 (if stdDev couldn't be calculated)
	*/
	double calculateStdDev(double variance) {
		double stdDev = 0;
		if(variance > 0) {
			stdDev = Math.sqrt(variance);
		} //if
		return stdDev;
	} //calculateStdDev

	/**
	Calculate the co-variance by the use of the two passed lists.
	The first list should contain the x-values and the second list the
	y-values. The function first calculates the mean for the x - and y-values,
	and if mean was successfully calculated, the co-variance is then calculated.
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: the co-variance || 0 (if co-variance couldn't be calculated)
	*/
	double calculateCoVariance(ArrayList<Double> listOfX, ArrayList<Double> listOfY) throws ExceptionClass {
		double coVariance = 0;
		try {
			double currentX = 0,
				currentY = 0,
				meanX = 0,
				meanY = 0;
			int n = 0;
			//check that the lists is of equal size
			ExceptionClass.throwErrorIflistSizeIsNotEqual(listOfX, listOfY, FAILED_AT_VARIANCE_CALCULATION);
			n = listOfX.size();
			//calculate mean
			meanX = calculateMeanDouble(listOfX);
			meanY = calculateMeanDouble(listOfY);
			//loop through the values and calculate the sum 
			for(int i = 0; i < n; i++) {
				currentX = listOfX.get(i) - meanX;
				currentY = listOfY.get(i) - meanY;
				coVariance += currentX * currentY;
			} //for
			//if the sum was created, calculate the covariance
			if(coVariance > 0) {
				coVariance = coVariance / (n - 1);
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return coVariance;
	} //calculateCoVariance

	/**
	Calculates the k1. It checks that the passed variance and stdDev is
	greater then zero and then calculates and returns k1.
	@param totVariance - double: the total variance for the x - and y-values
	@param totStdDev - double: the total stdDev for the x - and y-values
	@return double: calculated k1 || 0 (if k1 couldn't be calculated)
	*/
	double calculateK1(double totVariance, double totStdDev) {
		double k1 = 0;
		if(totVariance > 0 && totStdDev > 0) {
			k1 = totStdDev / totVariance;
		} //if
		return k1;
	} //calculateK1

	/**
	Calculates the correlation co-efficient.
	The function first checks that the lists passed is of equal size, and then
	calculates (in following order):
	- the mean for x and y
	- the variance for x and y
	- the value for stdDevX * stdDevY (multiStdDev)
	- the co-variance for x and y
	- checks that co-variance and multiStdDev was calculated,
	then calculates and returns the correlation co-efficient
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: calculated correlation co-efficient || 0 (if corr. co-effic. couldn't be calculated)
	*/
	double calculateCorrelationCoefficient(ArrayList<Double> listOfX, ArrayList<Double> listOfY) throws ExceptionClass {
		double r = 0;
		try {
			double coVariance = 0,
				meanX = 0,
				meanY = 0,
				varianceX = 0,
				varianceY = 0,
				multiStdDev = 0;
			//check that the lists is of equal size
			ExceptionClass.throwErrorIflistSizeIsNotEqual(listOfX, listOfY, FAILED_AT_VARIANCE_CALCULATION);
			//calculate the mean of the x - and y-values
			meanX = calculateMeanDouble(listOfX);
			meanY = calculateMeanDouble(listOfY);
			//calculate the variance for the x - and y-values
			varianceX = calculateVariance(listOfX, meanX);
			varianceY = calculateVariance(listOfY, meanY);
			//calculate the value for Sx * Sy
			multiStdDev = calculateStdDev(varianceX) * calculateStdDev(varianceY);
			//calculate the co-variance
			coVariance = calculateCoVariance(listOfX, listOfY);
			//check that the co-variance and the multiplication of multiStdDev was calculated
			if(coVariance > 0 && multiStdDev > 0) {
				//calculate the correlation co-efficient
				r = coVariance / multiStdDev;
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return r;
	} //calculateCorrelationCoefficient


	/********************** CALCULATE LINEAR REGRESSION ***************************/

	/**
	Calculate the linear regression linReg = a + bx.
	The a is calculated from the function calculateRegressionA(),
	and the b is calculated from the function calculateRegressionB()
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@param x - double: the value for x to be calculated
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: linear regression || 0 (if can't calculate the linear regression)
	*/
	double calculateLinearRegression(ArrayList<Double> listOfX, ArrayList<Double> listOfY, double x, double a, double b) throws ExceptionClass {
		double linReg = 0;
		//calculate b and a
		b = calculateRegressionB(listOfX, listOfY);
		a = calculateRegressionA(listOfX, listOfY, b);
		//calculate the linear regression
		linReg = a + (b * x);
		return linReg;
	} //calculateLinearRegression

	/**
	Calculates the value a for the linear regression linReg = a + bx.
	The formula for a = (sum(listOfY) - (b * sum(listOfX)) / n;
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: the value a || 0 (if can't calculate a)
	*/
	double calculateRegressionA(ArrayList<Double> listOfX, ArrayList<Double> listOfY, double b) throws ExceptionClass {
		int n = 0;
		double a = 0,
			sumListX = 0,
			sumListY = 0;
		try {
			//check that the lists is of equal size
			ExceptionClass.throwErrorIflistSizeIsNotEqual(listOfX, listOfY, FAILED_AT_VARIANCE_CALCULATION);
			n = listOfX.size();
			//calculate the sum of x and y
			sumListX = calculateMeanDouble(listOfX);
			sumListY = calculateMeanDouble(listOfY);
			//check that the sums were calculated
			if(sumListX > 0 && sumListY > 0) {
				a = (sumListY - (b * sumListX)) / n;
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return a;
	} //calculateRegressionA

	/**
	Calculates the value b for the linear regression linReg = a + bx.
	The formula for b is:
	1) numerator = ((n * sum(x*y) - (sum(listOfX) * sum(listOfY))
	2) denominator = ((n * sum(x*x) - sum(listOfX)2)
	3) b = numerator / denominator
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: the value b || 0 (if can't calculate b)
	*/
	double calculateRegressionB(ArrayList<Double> listOfX, ArrayList<Double> listOfY) throws ExceptionClass {
		int n = 0;
		double b = 0;
		try {
			double numerator = 0,
				denominator = 0,
				sumListX = 0,
				sumListY = 0,
				sumXY = 0,
				sumX_2;
			//check that the lists is of equal size
			ExceptionClass.throwErrorIflistSizeIsNotEqual(listOfX, listOfY, FAILED_AT_VARIANCE_CALCULATION);
			n = listOfX.size();
			//calculate the sum, sum(x*y), sum(x2)
			sumListX = calculateMeanDouble(listOfX);
			sumListY = calculateMeanDouble(listOfY);
			sumXY = sumOfXtimesY(listOfX, listOfY);
			sumX_2 = sumOfVariableToPowerOf2(listOfX);
			//check that the sums were calculated
			if(sumListX > 0 && sumListY > 0 && sumXY > 0 && sumX_2 > 0) {
				//get the value of the numerator
				numerator = ((n * sumXY) - (sumListX * sumListY));
				//get the value of the denominator
				denominator = ((n * sumX_2) - Math.pow(sumListX, 2));
				//calculate b
				b = numerator / denominator;
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return b;
	} //calculateRegressionB

	/************************* PRIVATE FUNCTIONS *********************************/

	/**
	Calculates the sum for all the values in passed valueList.
	Requires a list filled with integers.
	@param valueList - ArrayList<Integer>: list of values to calculate the sum of
	@return int: the sum of the passed values in the list valueList
	*/
	double sumOfListInteger(ArrayList<Integer> valueList) {
		int max = 0;
		double sum = 0;
		max = valueList.size();
		for(int i = 0; i < max; i++) {
			sum += valueList.get(i);
		} //for
		return sum;
	} //sumOfListInteger

	/**
	Calculates the sum for all the values in passed valueList.
	Requires a list filled with double.
	@param valueList - ArrayList<Double>: list of values to calculate the sum of
	@return double: the sum of the passed values in the list valueList
	*/
	double sumOfListDouble(ArrayList<Double> valueList) {
		int max = 0;
		double sum = 0;
		max = valueList.size();
		for(int i = 0; i < max; i++) {
			sum += valueList.get(i);
		} //for
		return sum;
	} //sumOfListDouble

	/**
	Calculates the sum the values in the two lists (x * y).
	@param listOfX - ArrayList<Integer>: list of values for X
	@param listOfY - ArrayList<Integer>: list of values for Y
	@throws new ExceptionClass: Error if the listOfX.size() != listOfY.size()
	@return double: Sum(x * y) for the values in listOfX and listOfY
	*/
	double sumOfXtimesY(ArrayList<Double> listOfX, ArrayList<Double> listOfY) throws ExceptionClass {
		int max = 0;
		double x = 0,
			y = 0,
			sumOfXY = 0;
		try {
			//check that the lists is of equal size
			ExceptionClass.throwErrorIflistSizeIsNotEqual(listOfX, listOfY, FAILED_AT_VARIANCE_CALCULATION);
			max = listOfX.size();
			for(int i = 0; i < max; i++) {
				x = listOfX.get(i);
				y = listOfY.get(i);
				sumOfXY += x * y;
			} //for
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return sumOfXY;
	} //sumOfXtimesY

	/**
	Raises each value to the power of two (x2), and adds all the values together.
	@param valueList - ArrayList<Integer>: the values to raise to power of two
	@return double: Sum of all values raised to the power of 2 (Sum(x2) )
	*/
	double sumOfVariableToPowerOf2(ArrayList<Double> valueList) {
		double sumToPower = 0,
			value = 0;
		int max = 0;
		max = valueList.size();
		for(int i = 0; i < max; i++) {
			value = valueList.get(i);
			sumToPower += Math.pow(value, 2);
		} //for
		return sumToPower;
	} //sumOfVariableToPower
}
