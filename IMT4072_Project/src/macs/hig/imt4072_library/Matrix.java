package macs.hig.imt4072_library;

import java.util.ArrayList;

/**
Class functioning as a matrix by use of a 2D array.
The class has the ability to:
- Create a mean matrix <br />
- Calculate corrected mean <br />
- Calculate the variance <br />
- Create a co-variance matrix <br />
- Addition, subtraction and multiplication of matrices <br />
- Create a scatter matrix and between class scatter matrix <br />
- Create a matrix containing the optimal line (w) <br />
- Transpose the matrix (or transpose a temporary matrix) <br />
- Inverse a 2D matrix <br />
- Create an identity matrix (not implemented)
@Author  Knut Lucas Andersen
*/

public class Matrix {
	//the number of rows in the matrix
	private int _numRows;
	//the number of columns in the matrix
	private int _numColumns;
	//array containing the matrix values
	private double _matrixArray[][];
	
	public Matrix() {
		//default initialisation
		_numRows = 0;
		_numColumns = 0;
		_matrixArray = new double[0][0];
	} //constructor

	/**
	Constructor creating an empty matrix with the size based
	on the based numRows and numColumns.
	@param numRows - int: The number of rows in the matrix
	@param numColumns - int: The number of columns in the matrix
	*/
	public Matrix(int numRows, int numColumns) {
		//set the values for the matrix
		setNewRowCount(numRows);
		setNewColumnCount(numColumns);
		//create the matrix array
		_matrixArray = createMatrixArray(numRows, numColumns);
	} //constructor

	/***************** CALCULATION FUNCTIONS *******************/

	/**
	Calculates the mean of the passed columnNo and returns the value.
	If passed columnNo value isn't within range, a ExceptionClass is thrown.
	@param columnNo - int: the columnNo to calculate mean
	@throws ExceptionClass - Throws error if the columnNo value isn't within range
	@return double: the mean
	*/
	double calculateMean(int columnNo) throws ExceptionClass {
		double mean = 0;
		try {
			//check that the matrix isn't empty and then check that the column is within range
			ExceptionClass.throwErrorIfMatrixIsEmpty(getNumRows(), getNumColumns());
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), "\nCan't calculate mean.");
			_matrixArray = getMatrix();
			for(int i = 0; i < getNumRows(); i++) {
				mean += _matrixArray[i][columnNo];
			} //for
			mean = mean / (double) getNumRows();
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return mean;
	} //calculateMean

	/**
	Calculates the mean of the matrix and returns a matrix containing
	1 row and n-columns, where n = calling matrix.getNumColumns().
	After the matrix is created, it's transposed to ensure correct order.
	Throws error if matrix is empty.
	@param columnNo - int: the columnNo to calculate mean
	@throws ExceptionClass - Throws error if the columnNo
	value isn't within range
	@return Matrix - A matrix containing the mean(s)
	*/
	Matrix createMeanMatrix() throws ExceptionClass {
		//check that the matrix isn't empty and then check that the column is within range
		ExceptionClass.throwErrorIfMatrixIsEmpty(this.getNumRows(), this.getNumColumns());
		int numRows = 1,
			numColumns = this.getNumColumns();
		Matrix meanMatrix = new Matrix(numRows, numColumns);
		for(int i = 0; i < this.getNumColumns(); i++) {
			meanMatrix.changeCellValueAt(0, i, this.calculateMean(i));
		} //for
		meanMatrix.transposeMatrix();
		return meanMatrix;
	} //createMeanMatrix

	/**
	Calculates the corrected mean. If the rowNo/columnNo value isn't
	within range, a ExceptionClass is thrown.
	@param rowNo - int: The rowNo to calculate corrected mean from
	@param columnNo - int: The columnNo to calculate corrected mean from
	@param globalMean - double: The global mean
	(mean calculated from the whole columnNo)
	@throws ExceptionClass - Throws error if the rowNo/columnNo
	value isn't within range
	@return double: the corrected mean
	*/
	double calculateCorrectedMean(int rowNo, int columnNo, double globalMean) throws ExceptionClass {
		double correctedMean = 0;
		try {
			//check that the values are within range
			ExceptionClass.throwErrorIfMatrixRowOutOfRange(rowNo, getNumRows(), "\nCan't calculate corrected mean data.");
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), "\nCan't calculate corrected mean data.");
			_matrixArray = getMatrix();
			correctedMean = _matrixArray[rowNo][columnNo] - globalMean;
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return correctedMean;
	} //calculateCorrectedMean

	/**
	Calculates the variance for the passed columnNo. If the passed
	columnNo isn't within range, a ExceptionClass is thrown.
	@param columnNo - int: The columnNo to calculate co-variance from
	@throws ExceptionClass - Throws error if the columnNo
	value isn't within range
	@return double: the variance || 0
	*/
	double calculateVariance(int columnNo) throws ExceptionClass {
		try {
			//check that the matrix isn't empty and then check that the column is within range
			ExceptionClass.throwErrorIfMatrixIsEmpty(getNumRows(), getNumColumns());
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), "\nCan't calculate variance.");
			double mean = 0,
				variance = 0,
				sumOfValues = 0;
			int n = getNumRows();
			_matrixArray = getMatrix();
			mean = calculateMean(columnNo);
			//calculate the sum of the values
			for(int i = 0; i < n; i++) {
				sumOfValues = _matrixArray[i][columnNo] - mean;
				variance += Math.pow(sumOfValues, 2);
			} //for
			//check that the sum of the values is greater then zero
			//and then calculate the variance
			if(variance > 0) {
				variance = variance / (n - 1);
				return variance;
			} //if
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return 0;
	} //calculateVariance

	/**
	Calculates the co-variance for the two passed columns.
	It first subtracts mean from the value, and then multiply
	the result. When all the columnNo values have been looped through
	the result is divided by (n-1) and returned.
	@param columnX - int: The 1. columnNo to calculate co-variance from
	@param columnY - int: The 2. columnNo to calculate co-variance from
	@return double: the coVariance || 0
	*/
	double calculateCoVariance(int columnX, int columnY) throws ExceptionClass {
		double coVariance = 0;
		try {
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnX, getNumColumns(), "\nCan't calculate Co-Variance.");
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnY, getNumColumns(), "\nCan't calculate Co-Variance.");
			double currentX = 0,
				currentY = 0,
				meanX = 0,
				meanY = 0;
			int n = getNumRows();
			_matrixArray = getMatrix();
			//calculate mean
			meanX = calculateMean(columnX);
			meanY = calculateMean(columnY);
			//loop through the values and calculate the sum 
			for(int i = 0; i < n; i++) {
				currentX = _matrixArray[i][columnX] - meanX;
				currentY = _matrixArray[i][columnY] - meanY;
				coVariance += currentX * currentY;
			} //for
			coVariance = coVariance / (n - 1);
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return coVariance;
	} //calculateCoVariance

	/**
	Creates a matrix containing the calculated co-variance values,
	based on the values from the passed matrix parent.
	If parent is empty, ExceptionClass is thrown.
	@param parent - Matrix: Matrix containing the original values
	@throws ExceptionClass - Throws error if parent is empty
	@return Matrix - a matrix filled with the co-variance calculated
	from the passed Matrix parent
	*/
	Matrix createCoVarianceMatrix(Matrix parent) throws ExceptionClass {
		int numElements = 0;
		double coVariance = 0;
		//since the matrix is a NxN matrix, use columns as counter
		numElements = parent.getNumColumns();
		//check that the parent matrix isn't empty
		String  errorMsg = "\nCan't create co-variance matrix.";
		ExceptionClass.throwErrorIfParentMatrixIsEmpty(parent.getNumRows(), parent.getNumColumns(), errorMsg);
		//create the co-variance matrix
		Matrix coVarMatrix = new Matrix(numElements, numElements);
		//fill coVarMatrix with values from parent matrix
		for(int i = 0; i < numElements; i++) {
			for(int j = 0; j < numElements; j++) {
				coVariance = parent.calculateCoVariance(i, j);
				coVarMatrix.changeCellValueAt(i, j, coVariance);
			} //for
		} //for
		return coVarMatrix;
	} //createCoVarianceMatrix

	/**
	Attempts to calculate and create a scatter matrix based on the values in
	the passed coVarMatrix. If the coVarMatrix is empty, an attempt to create
	it is done by calling the function createCoVarianceMatrix().
	The values in the co-variance matrix is then multiplied by (n-1) and
	finally the filled scatter matrix is returned.
	If both the co-variance matrix and the coVarParent matrix is empty,
	ExceptionClass is thrown.
	@param coVarParent: Matrix filled with the original values
	@param coVarMatrix: Matrix filled with the co-variance values
	@throws ExceptionClass - Throws error if both the coVarParent
	and coVarMatrix is empty
	@see createCoVarianceMatrix(Matrix parent)
	@return Matrix - a scatter matrix (filled with the co-variance multiplied with (n-1))
	*/
	Matrix createScatterMatrix(Matrix coVarParent, Matrix coVarMatrix) throws ExceptionClass {
		//since the matrix should sizes should be equal, just use the counter
		int n = 0,
			numElements = 0;
		double coVariance = 0;
		//get the columncount
		numElements = coVarMatrix.getNumColumns();
		//check that the covarmatrix isn't empty
		if(numElements == 0) {
			try {
				//the co-variance matris is empty, try to create it
				coVarMatrix = createCoVarianceMatrix(coVarParent);
			} catch(ExceptionClass ex) {
				//failed at creating co-var matrix
				//this happens if the parent-matrix is empty
				//get the error message from the createCoVarianceMatrix()
				//and add the error for this function.
				String  errorMsg = ex.ToString();
				errorMsg += "\nCan't create scatter matrix.";
				throw new ExceptionClass(errorMsg);
			} //try/catch
		} //if
		n = coVarParent.getNumRows() - 1;
		//create scatter matrix
		Matrix scatterMatrix = new Matrix(numElements, numElements);
		//fill scatterMatrix with values 
		for(int i = 0; i < numElements; i++) {
			for(int j = 0; j < numElements; j++) {
				coVariance = n * coVarMatrix.getMatrix()[i][j];
				scatterMatrix.changeCellValueAt(i, j, coVariance);
			} //for
		} //for
		return scatterMatrix;
	} //createScatterMatrix

	/**
	Create a matrix containing the between scatter.
	Based on a two-class problem.
	@param invSw - Matrix: Matrix containing the mean values
	@throws ExceptionClass - Throws error if one of the matrices is empty
	@return Matrix - matrix containing the between scatter
	*/
	Matrix getBetweenClassScatterMatrix(Matrix meanMatrix1, Matrix meanMatrix2) throws ExceptionClass {
		ExceptionClass.throwErrorIfMatrixIsEmpty(meanMatrix1.getNumRows(), meanMatrix1.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(meanMatrix2.getNumRows(), meanMatrix2.getNumColumns());
		//check that the size of the mean matrices are equal
		ExceptionClass.throwErrorIfMatrixNotOfEqualOrder(meanMatrix1, meanMatrix2);
		int numElements = meanMatrix1.getNumRows();
		Matrix sB = new Matrix(numElements, numElements);
		double subtractedMean1 = 0,
			subtractedMean2 = 0,
			value = 0;
		//calculate the subtracted mean
		subtractedMean1 = meanMatrix1.getCellValueAt(0, 0) - meanMatrix2.getCellValueAt(0, 0);
		subtractedMean2 = meanMatrix1.getCellValueAt(1, 0) - meanMatrix2.getCellValueAt(1, 0);
		//since it's only four values, it's done manually
		value = subtractedMean1 * subtractedMean1;
		sB.changeCellValueAt(0, 0, value);
		value = subtractedMean1 * subtractedMean2;
		sB.changeCellValueAt(0, 1, value);
		value = subtractedMean2 * subtractedMean1;
		sB.changeCellValueAt(1, 0, value);
		value = subtractedMean2 * subtractedMean2;
		sB.changeCellValueAt(1, 1, value);
		//return the matrix
		return sB;
	} //getBetweenClassScatterMatrix

	/**
	Create a matrix containing the optimal line v.
	@param invSw - Matrix: Matrix containing values to be additioned with values in matrix2
	@param meanC1 - Matrix: Matrix containing values to add to values in matrix1
	@param meanC2 - Matrix: Matrix containing values to add to values in matrix1
	@throws ExceptionClass
	- Throws error if one of the matrices is empty
	- Throws error if the matrices containing the mean aren't of equal size/order
	@return Matrix - matrix containing the list v that represents the optimal line
	*/
	Matrix getOptimalLineMatrix(Matrix invSw, Matrix meanC1, Matrix meanC2) throws ExceptionClass {
		//check that the matrices aren't empty
		ExceptionClass.throwErrorIfMatrixIsEmpty(invSw.getNumRows(), invSw.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(meanC1.getNumRows(), meanC1.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(meanC2.getNumRows(), meanC2.getNumColumns());
		//check that the size of the mean matrices are equal
		ExceptionClass.throwErrorIfMatrixNotOfEqualOrder(meanC1, meanC2);
		//check that the 
		String  errorMsg = "Can't calculate the optimal line.";
		ExceptionClass.throwErrorIfColumnAndRowCountDiffers(invSw.getNumColumns(), meanC1.getNumRows(), errorMsg);
		//create the matrix
		Matrix optimalLine = new Matrix(invSw.getNumRows(), meanC1.getNumColumns());
		double sumValue = 0,
			subtractedMean = 0,
			invSwValue = 0;
		//loop through the matrices
		for(int i = 0; i < invSw.getNumRows(); i++) {
			for(int j = 0; j < meanC1.getNumColumns(); j++) {
				for(int k = 0; k < invSw.getNumColumns(); k++) {
					//get the values, subtract the means and add the
					//multiplied result of the inverse and subtracted mean
					invSwValue = invSw.getCellValueAt(i, k);
					subtractedMean = meanC1.getCellValueAt(k, j) - meanC2.getCellValueAt(k, j);
					sumValue += invSwValue * subtractedMean;
				} //for
				//set value and reset summation variable
				optimalLine.changeCellValueAt(i, j, sumValue);
				sumValue = 0;
			} //for
		} //for
		return optimalLine;
	} //getOptimalLineMatrix

	/**
	Additions the two matrices that are passed if they are of equal size
	(equal row - and column count).
	Throws error if count isn't equal.
	@param matrix1 - Matrix: Matrix containing values to be additioned with values in matrix2
	@param matrix2 - Matrix: Matrix containing values to add to values in matrix1
	@throws ExceptionClass
	- Throws error if matrix is empty
	- Throws error if matrices aren't of equal size/order
	@return Matrix - matrix containing the result of the addition
	*/
	Matrix matrixAddition(Matrix matrix1, Matrix matrix2) throws ExceptionClass {
		//check that the matrices isn't empty and that the size/order is equal (rowcount and columncount)
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix1.getNumRows(), matrix1.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix2.getNumRows(), matrix2.getNumColumns());
		ExceptionClass.throwErrorIfMatrixNotOfEqualOrder(matrix1, matrix2);
		int rowCount = 0,
			columnCount = 0;
		double value = 0;
		rowCount = matrix1.getNumRows();
		columnCount = matrix1.getNumColumns();
		Matrix additionedMatrix = new Matrix(rowCount, columnCount);
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) {
				value = matrix1.getCellValueAt(i, j) + matrix2.getCellValueAt(i, j);
				additionedMatrix.changeCellValueAt(i, j, value);
			} //for
		} //for
		return additionedMatrix;
	} //matrixAddition

	/**
	Subtracts the two matrices that are passed if they are of equal size
	(equal row - and column count).
	Throws error if count isn't equal.
	@param matrix1 - Matrix: Matrix containing values to be subtracted from matrix2
	@param matrix2 - Matrix: Matrix containing values to subtract from matrix1
	@throws ExceptionClass
	- Throws error if matrix is empty
	- Throws error if matrices aren't of equal size/order
	@return Matrix - matrix containing the result of the subtraction
	*/
	Matrix matrixSubstraction(Matrix matrix1, Matrix matrix2) throws ExceptionClass {
		//check that the matrices isn't empty and that the size/order is equal (rowcount and columncount)
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix1.getNumRows(), matrix1.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix2.getNumRows(), matrix2.getNumColumns());
		ExceptionClass.throwErrorIfMatrixNotOfEqualOrder(matrix1, matrix2);
		int rowCount = 0,
			columnCount = 0;
		double value = 0;
		rowCount = matrix1.getNumRows();
		columnCount = matrix1.getNumColumns();
		Matrix subtractedMatrix = new Matrix(rowCount, columnCount);
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) {
				value = matrix1.getCellValueAt(i, j) - matrix2.getCellValueAt(i, j);
				subtractedMatrix.changeCellValueAt(i, j, value);
			} //for
		} //for
		return subtractedMatrix;
	} //matrixSubstraction

	/**
	Multiplies the cell values in this matrix with the passed value.
	Throws error if this matrix is empty.
	@param multiplyValue - double: Value to multiply with matrix cells
	@throws ExceptionClass - Throws error if matrix is empty
	*/
	void multiplyCellsWithValue(double multiplyValue) throws ExceptionClass {
		double value = 0;
		ExceptionClass.throwErrorIfMatrixIsEmpty(this.getNumRows(), this.getNumColumns());
		for(int rowNo = 0; rowNo < this.getNumRows(); rowNo++) {
			for(int columnNo = 0; columnNo < this.getNumColumns(); columnNo++) {
				value = multiplyValue * this.getCellValueAt(rowNo, columnNo);
				this.changeCellValueAt(rowNo, columnNo, value);
			} //for
		} //for
	} //multiplyCellsWithValue

	/**
	Multiplies the two matrices. If the columnCount in matrix1 isn't equal to rowCount in
	matrix2 (requirement for matrix multiplication), then a new check is performed if
	allowSwitch is set to true by checking the rowCount in matrix1 vs columnCount in matrix2.
	If allowSwitch is true, and rowCount == columnCount, then the matrices are switched
	before multiplication is executed (Note! m1*m2 isn't necessarily equal to m2*m1).
	If the size/order isn't equal an error is thrown. An error is also thrown if either
	of the matrices are empty.
	@param matrix1 - Matrix: Matrix containing values to multiply with matrix2
	@param matrix2 - Matrix: Matrix containing values to multiply from matrix1
	@param allowSwitch: boolean - boolean value for if a switch is allowed if number of rows in
	matrix1 isn't equal to number of columns in matrix2. If set to true; a check and attempt to
	switch is performed. If false; the row - and column count is compared and error thrown if
	they aren't equal
	@throws ExceptionClass
	- Throws error if matrix is empty
	- Throws error if matrices aren't of equal size/order
	@return Matrix - matrix containing the result of the multiplication
	*/
	Matrix matrixMultiplication(Matrix matrix1, Matrix matrix2, boolean allowSwitch) throws ExceptionClass {
		//check that the matrices isn't empty
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix1.getNumRows(), matrix1.getNumColumns());
		ExceptionClass.throwErrorIfMatrixIsEmpty(matrix2.getNumRows(), matrix2.getNumColumns());
		boolean isEqual = true,
			switchOrder = false;
		//check if they are equal
		if(matrix1.getNumColumns() != matrix2.getNumRows()) {
			isEqual = false;
			//is switch allowed
			if(allowSwitch) {
				//are these equal?
				if(matrix1.getNumRows() == matrix2.getNumColumns()) {
					isEqual = true;
					switchOrder = true;
				} //if
			} //if
		} //if
		//was an equality found?
		if(!isEqual) {
			throw new ExceptionClass("Can't multiply the matrices, they aren't of equal size/order.\n");
		} //if
		//is the matrices to be switched before multiplying them?
		if(switchOrder) {
			return multiplyMatrices(matrix2, matrix1);
		} //if
		return multiplyMatrices(matrix1, matrix2);
	} //matrixMultiplication

	/***
	Creates a new matrice containing the result of the multiplication of
	matrix1 and matrix2. The returned matrix's size/order contains the
	rowCount of matrix1 and the columnCount of matrix2. The multiplication
	process is to take the row value of matrix1 and multiply it with the
	column value in matrix2 and add all these multiplied values together.
	@param matrix1 - Matrix:
	@param matrix1 - Matrix:
	*/
	Matrix multiplyMatrices(Matrix matrix1, Matrix matrix2) throws ExceptionClass {
		//matrix to be filled with the multiplied values
		Matrix multipliedMatrix = new Matrix(matrix1.getNumRows(), matrix2.getNumColumns());
		double valueMatrix1 = 0,
			valueMatrix2 = 0,
			sumOfM1xM2 = 0;
		//loop through the matrices
		for(int i = 0; i < matrix1.getNumRows(); i++) {
			for(int j = 0; j < matrix2.getNumColumns(); j++) {
				for(int k = 0; k < matrix1.getNumColumns(); k++) {
					//get the values, multiply and add them together
					valueMatrix1 = matrix1.getCellValueAt(i, k);
					valueMatrix2 = matrix2.getCellValueAt(k, j);
					sumOfM1xM2 += valueMatrix1 * valueMatrix2;
				} //for
				//set the multiplied value and reset summation variable
				multipliedMatrix.changeCellValueAt(i, j, sumOfM1xM2);
				sumOfM1xM2 = 0;
			} //for
		} //for
		return multipliedMatrix;
	} //multiplyMatrices

	/****************** ALTERATION FUNCTIONS *************************/

	/***
	Deletes the array that contains the original values
	that the matrix was filled with
	*/
	void deleteMatrix() {
		deleteTempArrayObjects(_matrixArray);
	} //deleteMatrix

	/***
	Deletes a temporary array by the use of the delete operator and
	then setting the value to zero
	*/
	void deleteTempArrayObjects(double[][] tempArray) {
		tempArray = null;
	} //deleteTempArrayObjects

	/**
	Calculates a 2x2 matrix based on the original matrix values.
	First the determinants are decided and swapped, and at the end the
	determinant is calculated. If the determinant isn't zero, the inverse
	matrix is calculated and returned.
	@throws ExceptionClass
	- Throws error if the determinant is zero
	- Throws error if the calling matrix isn't a 2x2 matrix
	@return Matrix - An inverse 2x2 matrix
	*/
	Matrix inverse2DMatrix() throws ExceptionClass {
		ExceptionClass.throwErrorIfMatrixNot2x2(getNumRows(), getNumColumns());
		double[][] inverseArray = new double[0][0];
		inverseArray = createMatrixArray(getNumRows(), getNumColumns());
		int matrixSize = 2,
			rowPosition = 0,
			columnPosition = 0;
		double value = 0,
			determinantAD = 0,
			determinantBC = 0;
		//swap a and d
		rowPosition = 0;
		columnPosition = 0;
		determinantAD = _matrixArray[rowPosition][columnPosition];
		swapCellValueAt(rowPosition, columnPosition, getNumRows() - 1, getNumColumns() - 1, inverseArray);
		//swap d and a
		rowPosition = getNumRows() - 1;
		columnPosition = getNumColumns() - 1;
		determinantAD *= _matrixArray[rowPosition][columnPosition];
		//make b negative
		rowPosition = 0;
		columnPosition = getNumColumns() - 1;
		value = -_matrixArray[rowPosition][columnPosition];
		changeCellValueAt(rowPosition, columnPosition, value, inverseArray);
		determinantBC = _matrixArray[rowPosition][columnPosition];
		//make c negative
		rowPosition = getNumRows() - 1;
		columnPosition = 0;
		value = -_matrixArray[rowPosition][columnPosition];
		changeCellValueAt(rowPosition, columnPosition, value, inverseArray);
		determinantBC *= _matrixArray[rowPosition][columnPosition];
		//check that determinant isn't zero
		value = 1 / (determinantAD - determinantBC);
		if(value == 0) {
			String errorMsg = "Can't calculate the inverse, determinant is zero.";
			deleteTempArrayObjects(inverseArray);
			throw new ExceptionClass(errorMsg);
		} //if
		Matrix inverseMatrix = new Matrix(matrixSize, matrixSize);
		//calculate the inverse
		for(int i = 0; i < getNumRows(); i++) {
			for(int j = 0; j < getNumColumns(); j++) {
				inverseMatrix.changeCellValueAt(i, j, value * inverseArray[i][j]);
			} //for
		} //for
		deleteTempArrayObjects(inverseArray);
		return inverseMatrix;
	} //inverse2DMatrix

	/**
	Transposes the original matrix. First the values from the original matrix is
	copied, then the original matrix is deleted and reconstructed to fit the new
	rowNo and columnNo count. Finally it's filled with the new rowNo and columnNo values.
	*/
	void transposeMatrix() throws ExceptionClass {
		//check that the matrix has at least one rowNo and columnNo
		if(getNumRows() > 0 && getNumColumns() > 0) {
			double value = 0;
			int oldRowCount = 0,
				oldColumnCount = 0,
				newRowCount = 0,
				newColumnCount = 0;
			double[][] tempArray = new double[0][0];
			//get current count for rows and columns
			oldRowCount = getNumRows();
			oldColumnCount = getNumColumns();
			//set the new count for rows and columns (tranpose)
			newRowCount = oldColumnCount;
			newColumnCount = oldRowCount;
			//copy the content of the current matrix
			tempArray = createMatrixArray(oldRowCount, oldColumnCount);
			for(int i = 0; i < oldRowCount; i++) {
				for(int j = 0; j < oldColumnCount; j++) {
					tempArray[i][j] = _matrixArray[i][j];
				} //for
			} //for
			//delete the old matrix and recreate it
			deleteMatrix();
			_matrixArray = createMatrixArray(newRowCount, newColumnCount);
			//set new range for _matrixArray's rowNo and columnNo count
			setNewRowCount(newRowCount);
			setNewColumnCount(newColumnCount);
			//fill _matrixArray with the tranposed values
			for(int i = 0; i < oldRowCount; i++) {
				for(int j = 0; j < oldColumnCount; j++) {
					//getting tranposed position and value
					newRowCount = j;
					newColumnCount = i;
					value = tempArray[i][j];
					//add the value to _matrixArray
					changeCellValueAt(newRowCount, newColumnCount, value);
				} //for
			} //for
			deleteTempArrayObjects(tempArray);
		} //if
	} //transposeMatrix

	/**
	Transposes the passed matrix transposeArray. First the values from are
	copied, then transposeArray is deleted and reconstructed to fit the new
	rowNo and columnNo count. Finally it's filled with the new rowNo and columnNo values.
	@param numRows - int: The transposeArray's original rowNo count
	@param numColumns - int: The transposeArray's original columnNo count
	@param transposeArray - double[][]: The array to transpose
	*/
	void transposeMatrix(int numRows, int numColumns, double[][] transposeArray) {
		//check that the matrix has at least one rowNo and columnNo
		if(numRows > 0 && numColumns > 0) {
			double value = 0;
			int oldRowCount = 0,
				oldColumnCount = 0,
				newRowCount = 0,
				newColumnCount = 0;
			double[][] tempArray = new double[0][0];
			//get current count for rows and columns
			oldRowCount = numRows;
			oldColumnCount = numColumns;
			//set the new count for rows and columns (tranpose)
			newRowCount = oldColumnCount;
			newColumnCount = oldRowCount;
			//copy the content of the current matrix
			tempArray = createMatrixArray(oldRowCount, oldColumnCount);
			for(int i = 0; i < oldRowCount; i++) {
				for(int j = 0; j < oldColumnCount; j++) {
					tempArray[i][j] = transposeArray[i][j];
				} //for
			} //for
			//delete the passed matrix and recreate it
			deleteTempArrayObjects(transposeArray);
			transposeArray = createMatrixArray(newRowCount, newColumnCount);
			//fill transposeArray with the tranposed values
			for(int i = 0; i < oldRowCount; i++) {
				for(int j = 0; j < oldColumnCount; j++) {
					//getting tranposed position and value
					newRowCount = j;
					newColumnCount = i;
					value = tempArray[i][j];
					//add the value to _matrixArray
					changeCellValueAt(newRowCount, newColumnCount, value, transposeArray);
				} //for
			} //for
			deleteTempArrayObjects(tempArray);
		} //if
	} //transposeMatrix

	/**
	Fills the matrix with values. The way the matrix is filled is that it
	takes the passed columnNo and fills up that columnNo number with the values
	from the ArrayList<Double> values. Example: Say that the columnNo passed is
	column1. Then, for each value in list, the column1's row1, row2, ..., rowN
	would be filled with the given values from the list.
	Throws error if the columnNo isn't within range, the list is empty
	or the size of the list is greater then the matrix rowcount.
	@param columnNo - int: The columnNo to fill with values
	@throws ExceptionClass - Throws error if: <br />
	- The list is empty <br />
	- The columnNo value is out of range <br />
	- The number of values in the list is greater then the number of rows <br />
	(since value goes into 'columnNo' and row1, 2, ..., n)
	@param values - ArrayList<Double>: The values to fill up in this column
	*/
	void fillMatrixWithValues(int columnNo, ArrayList<Double> values) throws ExceptionClass {
		try {
			String errorMsg = "";
			int max_size = 0;
			ExceptionClass.throwErrorIfValueListIsEmpty(values);
			//check that the number of columns is within range
			errorMsg = "\nCan't fill matrix with values.";
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), errorMsg);
			//check that the list.size is equal to the number of columns
			max_size = values.size();
			int rowCount = getNumRows();
			errorMsg = "The number of row values is higher then the row count. \nCan't fill matrix with values.";
			ExceptionClass.throwErrorIfSizeGreaterThenValue(max_size, rowCount, errorMsg);
			//fill the matrix array with rowNo values from the list
			for(int i = 0; i < max_size; i++) {
				_matrixArray[i][columnNo] = values.get(i);
			} //for
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
	} //fillMatrixWithValues

	/**
	Creates a new matrix array filled with zero's. The matrix arrays
	size is based on the passed values numRows and numColumns.
	@param numRows - int: The number of rows the matrix shall have
	@param numColumns - int: The number of columns the matrix shall have
	*/
	double[][] createMatrixArray(int numRows, int numColumns) {
		//create a new array and fill it with rows and columns
		double[][] newArray = new double[numRows][numColumns];
		for(int i = 0; i < numRows; i++) {
			newArray[i] = new double[numColumns];
		} //for
		//fill matrix with default value (zero)
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numColumns; j++) {
				newArray[i][j] = 0;
			} //for
		} //for
		return newArray;
	} //createMatrixArray

	/**
	Changes the cell value at given position in the original matrix.
	Throws error if the passed rowNo/columnNo value isn't within range.
	@param rowNo - int: The rowNo position to change value at
	@param columnNo - int: The columnNo position to change value at
	@param value - double: The new value
	@throws ExceptionClass - Throws error if rowNo/columnNo isn't within range
	*/
	void changeCellValueAt(int rowNo, int columnNo, double value) throws ExceptionClass {
		try {
			String errorMsg = "";
			//check that rowNo value is legal
			errorMsg = "\nCan't change value at position ["
				+ (rowNo) + "," + (columnNo) + "]";
			ExceptionClass.throwErrorIfMatrixRowOutOfRange(rowNo, getNumRows(), errorMsg);
			//check that columnNo value is legal
			errorMsg = "\nCan't change value at position ["
				+ (rowNo) + "," + (columnNo) + "]";
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), errorMsg);
			_matrixArray[rowNo][columnNo] = value;
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
	} //changeCellValueAt

	/**
	Retrieves the value from the original matrix's [fromRow][fromColumn]
	and moves the value to position [toRow][toColumn] in the passed matrixArray.
	Then does the same, but this time moves the value from [toRow][toColumn] in the
	original matrix and puts it in [fromRow][fromColumn] (in matrixArray).
	@param fromRow - int: The original rowNo
	@param fromColumn - int: The original columnNo
	@param toRow - int: The place to put the value from fromRow
	@param toColumn - int: The place to put the value from fromColumn
	@param matrixArray - double[][]:the array that's to get it's value swapped
	*/
	void swapCellValueAt(int fromRow, int fromColumn, int toRow, int toColumn, double matrixArray[][]) {
		double currentValue = 0;
		currentValue = _matrixArray[fromRow][fromColumn];
		changeCellValueAt(toRow, toColumn, currentValue, matrixArray);
		currentValue = _matrixArray[toRow][toColumn];
		changeCellValueAt(fromRow, fromColumn, currentValue, matrixArray);
	} //swapCellValueAt

	/**
	Changes the cell value at given position in the passed array.
	@param rowNo - int: The rowNo position to change value at
	@param columnNo - int: The columnNo position to change value at
	@param value - double: The new value
	@param matrixArray - double[][]:The array to change value in
	*/
	void changeCellValueAt(int rowNo, int columnNo, double value, double matrixArray[][]) {
		matrixArray[rowNo][columnNo] = value;
	} //changeCellValueAt

	void setNewRowCount(int numRows) {
		_numRows = numRows;
	} //setNewRowCount

	void setNewColumnCount(int numColumns) {
		_numColumns = numColumns;
	} //setNewColumnCount

	/****************** GET FUNCTIONS *************************/

	int getNumRows() {
		return _numRows;
	} //getNumRows

	int getNumColumns() {
		return _numColumns;
	} //getNumColumns

	double[][] getMatrix() {
		return _matrixArray;
	} //getMatrix

	/**
	Function to create and possibly return an identity matrix.
	Currently not created.
	@throws ExceptionClass 
	*/
	void getIdentityMatrix() throws ExceptionClass {
		//TODO:
		//by use of possibly passed values, 
		//return (?) a N x M I-matrix
		throw new ExceptionClass("Function not created. Can't return Identity matrix.");
	} //getIdentityMatrix

	/**
	Retrieves the matrix's cell value at the given position
	(based on rowNo and columnNo value). Throws error if the passed rowNo/columnNo
	value isn't within range.
	@param rowNo - int: The rowNo position to retrieve the value from
	@param columnNo - int: The columnNo position to retrieve the value from
	@throws ExceptionClass - Throws error if rowNo/columnNo isn't within range
	@return double: the value at given position (_matrixArray[rowNo][columnNo])
	*/
	double getCellValueAt(int rowNo, int columnNo) throws ExceptionClass {
		double cellValue = 0;
		try {
			String errorMsg = "";
			//check that rowNo value is legal
			errorMsg = "\nCan't retrieve value at position ["
				+ (rowNo) + "," + (columnNo) + "]";
			ExceptionClass.throwErrorIfMatrixRowOutOfRange(rowNo, getNumRows(), errorMsg);
			//check that columnNo value is legal
			errorMsg = "\nCan't retrieve value at position ["
				+ (rowNo) + "," + (columnNo) + "]";
			ExceptionClass.throwErrorIfMatrixColumnOutOfRange(columnNo, getNumColumns(), errorMsg);
			cellValue = _matrixArray[rowNo][columnNo];
		} catch(ExceptionClass ex) {
			throw ex;
		} //try/catch
		return cellValue;
	} //getCellValueAt
}
