package macs.hig.imt4072_library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
Class that handles operations related to clusters
and cluster analysis.
@Author  Knut Lucas Andersen
*/
public class Clustering {
	//number of selected centroids
	private int _noCentroids;
	//number of unlabeled samples
	private int _noSamples;
	//number of attributes belonging to the samples
	private int _noAttributes;
	//matrix containing the unlabeled samples
	private Matrix _sampleMatrix;
	//matrix containing the centroid samples
	private Matrix _centroidMatrix;
	//list containing the clustering results from round
	//index1: iteration round, index2: cluster group
	ArrayList<ArrayList<Integer>> _clusterGroupList;

	/**
	Default constructor
	*/
	public Clustering() {
		_noCentroids = 0;
		_noSamples = 0;
		_noAttributes = 0;
		_centroidMatrix = new Matrix();
		_sampleMatrix = new Matrix();
		_clusterGroupList = new ArrayList<ArrayList<Integer>>();
	} //constructor

	/**

	
	@param noClusterGroups - int: The number of cluster groups to analyze
	@param noAttributes - int: The number of attributes
	@throws ExceptionClass 
	*/
	public Clustering(int noClusterGroups, int noAttributes) throws ExceptionClass {
		try {
			_clusterGroupList = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Double>> sampleList = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>> centroidList = new ArrayList<ArrayList<Double>>();
			
			/** 
			 * TODO:
			 * Fill clusters here and set value: noAttributes
			 */
			
			setNoSamples(sampleList.size());
			setNoCentroids(centroidList.size());
			setNoAttributes(noAttributes);
			//fill matrices with data 
			fillMatrixWithSamples(sampleList, _sampleMatrix);
			//is the number of selected cluster groups less then 
			//the number of centroids
			if(noClusterGroups < getNoCentroids()) {
				//less then total, select random values
				fillCentroidMatrixWithSamples(centroidList, _centroidMatrix, noClusterGroups);
			} else {
				//number of groups is equal to total
				fillMatrixWithSamples(centroidList, _centroidMatrix);
			} //if					 
			//check that the number of attributes in both samples are equal
			if(noAttributes != _centroidMatrix.getNumColumns()) {
				String  errorMsg = "";
				errorMsg = "The number of attributes in the test sample (count: ";
				errorMsg += (noAttributes);
				errorMsg += ") is not equal to the number of attributes in the centroid (count: ";
				errorMsg += (_centroidMatrix.getNumColumns()) + ").";
				throw new ExceptionClass(errorMsg);
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
	} //constructor

	/**
	Fills a matrix with the values in the list.
	
	@param sampleList: ArrayList<ArrayList<Double>> - List containing the
	samples to fill matrix with
	@param sampleMatrix - Matrix: The matrix to fill with values
	@return Matrix: Matrix filled with the values from the sampleList
	@throws ExceptionClass 
	*/
	void fillMatrixWithSamples(ArrayList<ArrayList<Double>> sampleList, Matrix sampleMatrix) throws ExceptionClass {
		ArrayList<Double> tempList = new ArrayList<Double>();
		sampleMatrix = new Matrix(sampleList.size(), getNoAttributes());
		//loop through the attributes and samples
		for(int i = 0; i < getNoAttributes(); i++) {
			for (ArrayList<Double> attribute : sampleList) {
				//fill list with values
				tempList.add(attribute.get(i));
			} //for
			//add the retrieved values to the matrix and clear the temp List
			sampleMatrix.fillMatrixWithValues(i, tempList);
			tempList = new ArrayList<Double>();
		} //for
		//delete the list
		tempList = new ArrayList<Double>();
	} //fillMatrixWithSamples	

	/**
	Fills the centroid matrix with values from the centroid list.
	The indexes of the samples to use are selected randomly and
	distinct, to avoid duplicates.
	
	@param centroidList: ArrayList<ArrayList<Double>> - List containing the
	all the centroid samples
	@param centroidMatrix - Matrix: The matrix to fill with centroids
	@param noClusterGroups - int: the number of selected cluster groups
	@return Matrix: Matrix filled with (random) centroid samples
	@throws ExceptionClass 
	*/
	void fillCentroidMatrixWithSamples(ArrayList<ArrayList<Double>> centroidList, Matrix centroidMatrix, int noClusterGroups) throws ExceptionClass {
		int randomCluster = 0;
		Random rand = new Random();
		ArrayList<Integer> randomIndex = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> tempList = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < noClusterGroups; i++) {
			randomCluster = rand.nextInt(noClusterGroups);
			//loop until a new, distinct random index is selected
			while(getClusterCount(randomIndex, randomCluster) > 0) {
				randomCluster = rand.nextInt(noClusterGroups);
			} //while	 
			randomIndex.add(randomCluster);
		} //for
		//sort the indexes
		Collections.sort(randomIndex);
		for (int index : randomIndex) {
			tempList.add(centroidList.get(index));
		} //for		
		//clean up and update the number of centroids
		randomIndex = new ArrayList<Integer>();
		setNoCentroids(noClusterGroups);
		fillMatrixWithSamples(tempList, centroidMatrix);
	} //fillCentroidMatrixWithSamples

	/**
	Calculates the distance between the centroid and the samples
	by use of the k-means method.
	@param maxIterations: float - The total number of iterations to run
	@param stopParameter - int: The number of equal results before stopping 
	the cluster analysis
	@return ArrayList&lt;ArrayList&lt;Integer&gt;&gt;:
	@throws ExceptionClass 
	*/
	ArrayList<ArrayList<Integer>> calculateDistanceFromCentroids(float maxIterations, int stopParameter) throws ExceptionClass {
		int counter = 0,
			round = 0,
			currentIndex = 0;
		double mean = 0,
			distance = 0,
			currentValue = 0;
		int noSamples = getNoSamples();
		boolean exitLoop = false;
		Statistics stats = new Statistics();
		ArrayList<Integer> clusterIndex = new ArrayList<Integer>();
		ArrayList<Double> clusterDistances = new ArrayList<Double>();
		while(round < maxIterations && !exitLoop) {
			//loop through the test samples and centroids attribute values
			for(int i = 0; i < noSamples; i++) {
				for(int j = 0; j < getNoCentroids(); j++) {
					distance = 0;
					for(int k = 0; k < getNoAttributes(); k++) {
						//get the value for the test sample, centroid mean and calculate distance
						currentValue = getSampleMatrix().getCellValueAt(i, k);
						mean = getCentroidMatrix().getCellValueAt(j, k);
						distance += calculateDistance(currentValue, mean);
					} //for
					//get the squareroot of the distance, and add it to the List
					distance = Math.sqrt(distance);
					clusterDistances.add(distance);
				} //for
				currentIndex = counter = 1;
				distance = clusterDistances.get(0);
				//loop through the distances and find the shortest for this sample
				for (double currentDistance : clusterDistances) {
					if(currentDistance < distance) {
						distance = currentDistance;
						currentIndex = counter;
					} //if
					counter++;
				} //for
				//clear distance values and add index to list
				clusterDistances = new ArrayList<Double>();
				clusterIndex.add(currentIndex);
			} //for
			//add the index of cluster grouping from this round
			_clusterGroupList.add(clusterIndex);
			//re-calculate centroids and update the matrix
			updateCentroids(clusterIndex);
			clusterIndex = new ArrayList<Integer>();
			round++;
			exitLoop = isResultEqualToLastRound(round, stopParameter, _clusterGroupList, stats);
		} //while
		clusterDistances = new ArrayList<Double>();
		clusterIndex = new ArrayList<Integer>();
		return _clusterGroupList;
	} //calculateDistanceFromCenteroids

	/**
	Calculates the distance between the unlabeled sample attribute
	and the centroid (substraction) and then raises the answer to
	the power of 2 and returns it.
	
	@param value - double: The current unlabeled sample attribute value
	@param value - double: The current centroid value
	@return double: Result of (value - mean)2
	*/
	double calculateDistance(double value, double mean) {
		double distance = 0;
		distance = value - mean;
		distance = Math.pow(distance, 2);
		return distance;
	} //calculateDistance

	/**
	Updates the centroid matrix, filling in the new averages where
	changes have occurred.
	Excepting the format where the list position is the sample
	(unlabeled) and the actual value in the list is the centroid index.
	@param clusterIndex - ArrayList<Integer> - List of cluster indexes
	@param centroidMatrix - Matrix: Matrix containing centroids
	from previous round
	@param sampleMatrix - Matrix:
	@return Matrix: Updated centroid matrix containing the
	average from this round
	@throws ExceptionClass 
	*/
	Matrix updateCentroids(ArrayList<Integer> clusterIndex) throws ExceptionClass {
		int rowNo = 0,
			indexCounter = 0;
		double currentValue = 0;
		_centroidMatrix = getCentroidMatrix();
		_sampleMatrix = getSampleMatrix();
		Matrix tempMatrix;
		//loop through the centroids and update the mean values
		for(int centroid = 0; centroid < getNoCentroids(); centroid++) {
			if(clusterIndex.contains(centroid + 1)) {
				rowNo = 0;
				indexCounter = 0;
				//create a matrix and loop through the indexes to retrieve the sample values
				tempMatrix = new Matrix(getClusterCount(clusterIndex, centroid + 1), getNoAttributes());
				for (int index : clusterIndex) {
					if(index == (centroid + 1)) {
						for(int columnNo = 0; columnNo < getNoAttributes(); columnNo++) {
							currentValue = _sampleMatrix.getCellValueAt(indexCounter, columnNo);
							tempMatrix.changeCellValueAt(rowNo, columnNo, currentValue);
						} //for
						rowNo++;
					} //if
					indexCounter++;
				} //for
				//create matrix containing mean and update the centroid values
				tempMatrix = tempMatrix.createMeanMatrix();
				for(int columnNo = 0; columnNo < tempMatrix.getNumRows(); columnNo++) {
					currentValue = tempMatrix.getCellValueAt(columnNo, 0);
					_centroidMatrix.changeCellValueAt(centroid, columnNo, currentValue);
				} //for
			} //if
		} //for
		return _centroidMatrix;
	} //updateCentroids

	/**
	A function made to check for equal results, mainly to stop
	the iterations before time if cluster analysis is "good enough".
	@param round - int: the current iteration round
	@param stopParameter - int: number of equals telling when
	enough equal results have been found
	@param clusterGroupList - ArrayList<ArrayList<Integer>>:
	@param stats - Statistics:
	@return boolean: True - end iteration, false - continue iteration, 
	not enough equal results found
	*/
	boolean isResultEqualToLastRound(int round, int stopParameter, ArrayList<ArrayList<Integer>> clusterGroupList, Statistics stats) {
		boolean equalRounds = false;
		if(round > 2) {
			int counter = 0,
				countEquals = 0;
			double currentMean = 0,
				previousMean = 0;
			ArrayList<Integer> previous = new ArrayList<Integer>();
			//set it to the first element to initialize
			previous = clusterGroupList.get(0);
			//loop through all the index lists and compare them
			for (ArrayList<Integer> sample : clusterGroupList) {
				//using the average of the indexes to see if result is equal
				previousMean = stats.calculateMeanInteger(previous);
				currentMean = stats.calculateMeanInteger(sample);
				if(previousMean == currentMean) {
					countEquals++;
				} //if
				previous = clusterGroupList.get(counter);
				counter++;
			} //for
			//check if number of equals exceed set parameter
			if(countEquals > stopParameter) {
				equalRounds = true;
			} //if
		} //if
		return equalRounds;
	} //isResultEqualToLastRound

	/**********************GET & SET METHODS *****************************/

	/**
	Returns the cluster count from the passed index list.
	Excepting the format where the list position is the sample
	(unlabeled) and the actual value in the list is the centroid index.
	
	@param ArrayList<Integer> - List of cluster indexes
	@param clusterValue - int: The value find and count
	@return int: The count of the given cluster value in the list
	*/
	int getClusterCount(ArrayList<Integer> clusterIndex, int clusterValue) {
		int counter = 0;
		for (int index : clusterIndex) {
			if(index == clusterValue) {
				counter++;
			} //if
		} //for
		return counter;
	} //getClusterCount

	/**
	Returns a list containing the rescaled values of the attribute index.
	The rescaling is done in the following way: xNew=(x-Min)/(Max-Min)
	
	@param noAttributes - int: The total number of attributes for this sample
	@return ArrayList<Double>: List containing the rescaled values (for the x-axis)
	*/
	ArrayList<Double> getRescaledList(int noAttributes) {
		double minValue = 1,
			rescaledValue = 0;
		ArrayList<Double> rescaledList = new ArrayList<Double>();
		for(int x = 1; x <= noAttributes; x++) {
			rescaledValue = (x - minValue) / (noAttributes - minValue);
			rescaledList.add(rescaledValue);
		} //for
		return rescaledList;
	} //getRescaledList

	/**
	Returns a list containing the indexes of the cluster groups
	(index1: iteration round, index2: the cluster group the sample was added to)
	*/
	ArrayList<ArrayList<Integer>> getClusterGroupList() {
		return _clusterGroupList;
	} //getClusterGroupList

	/**
	Returns the current centroid matrix
	(values are from the last iteration round)
	*/
	Matrix getCentroidMatrix() {
		return _centroidMatrix;
	} //getNoAttributes

	/**
	Returns the test/sample matrix
	(matrix containing the original unlabeled samples)
	*/
	Matrix getSampleMatrix() {
		return _sampleMatrix;
	} //getNoSamples

	/**
	Returns the number of attributes
	*/
	int getNoAttributes() {
		return _noAttributes;
	} //getNoAttributes

	/**
	Returns the number of test (unlabeled) samples
	*/
	int getNoSamples() {
		return _noSamples;
	} //getNoSamples

	/**
	Returns the number of centroid samples
	*/
	int getNoCentroids() {
		return _noCentroids;
	} //getNoCentroids

	/**
	Set the number of attributes belonging to the samples
	*/
	void setNoAttributes(int noAttributes) {
		_noAttributes = noAttributes;
	} //setNoAttributes

	/**
	Set the number of unlabeled samples
	*/
	void setNoSamples(int noSamples) {
		_noSamples = noSamples;
	} //setNoSamples

	/**
	Set the number of centroid samples
	*/
	void setNoCentroids(int noCentroids) {
		_noCentroids = noCentroids;
	} //setNoCentroids
}
