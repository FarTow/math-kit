package mathkit;

import java.util.Arrays;

public class Matrix {
    /**
     * Container for all values in this matrix
     */
    private final double[][] con;

    // Constructors

    public static Matrix identityMatrix(int size) {
        Matrix result = new Matrix(size, size);

        for (int i = 0; i < size; i++) {
            result.con[i][i] = 1.0;
        }

        return result;
    }

    /**
     * Initialize this matrix to be an empty matrix with row x col dimensions
     * @param numRows amount of rows in this matrix, must be greater than 0
     * @param numCols amount of columns in this matrix, must be greater than 0
     */
    public Matrix(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Number of rows and columns in a matrix must be greater than 0");
        }

        con = new double[numRows][numCols];
    }

    /**
     * Initialize this matrix to be a row x col matrix with all values set to val
     * @param numRows amount of rows in this matrix, must be greater than 0
     * @param numCols amount of columns in this matrix, must be greater than 0
     * @param val default value to set for all values in this matrix
     */
    public Matrix(int numRows, int numCols, double val) {
        this(numRows, numCols);

        for (double[] row : con) {
            Arrays.fill(row, val);
        }
    }

    /**
     * Initialize this matrix with a 2D array
     * @param other 2D array of values for this matrix, cannot be null and must be rectangular
     */
    public Matrix(double[][] other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot create a matrix from a null 2D array");
        } else if (!arrayIsRectangular(other)) {
            throw new IllegalArgumentException("Cannot create a matrix from a non-rectangular 2D array");
        }

        con = new double[other.length][other[0].length];

        for (int r = 0; r < other.length; r++) {
            System.arraycopy(other[r], 0, con[r], 0, other[r].length);
        }
    }

    /**
     * Initialize this matrix to be a copy of another matrix
     * @param other matrix to be copied, cannot be null
     */
    public Matrix(Matrix other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot create a matrix from a null matrix");
        }

        con = new double[other.con.length][other.con[0].length];

        for (int r = 0; r < other.con.length; r++) {
            System.arraycopy(other.con[r], 0, con[r], 0, other.con[r].length);
        }
    }

    // Calculations

    /**
     * @param other matrix to add, must be the same size as this matrix
     * @return a new matrix representing the sum of the two matrices
     */
    public Matrix add(Matrix other) {
        if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot add two matrices of different sizes");
        }

        Matrix result = new Matrix(this);

        for (int r = 0; r < result.con.length; r++) {
            for (int c = 0; c < result.con[0].length; c++) {
                result.con[r][c] += other.con[r][c];
            }
        }

        return result;
    }

    /**
     * @param other matrix to subtract, must be the same size as this matrix
     * @return a new matrix representing the difference of the two matrices
     */
    public Matrix subtract(Matrix other) {
        if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot subtract two matrices of different sizes");
        }

        Matrix result = new Matrix(this);

        for (int r = 0; r < result.con.length; r++) {
            for (int c = 0; c < result.con[0].length; c++) {
                result.con[r][c] -= other.con[r][c];
            }
        }

        return result;
    }

    /**
     * @param scalar factor to multiply all values in this matrix by
     * @return a new matrix representing this one multiplied by the scalar
     */
    public Matrix multiplyByScalar(double scalar) {
        Matrix result = new Matrix(this);

        for (double[] row : result.con) {
            for (int c = 0; c < row.length; c++) {
                row[c] *= scalar;
            }
        }

        return result;
    }

    /**
     * @param scalar factor to divide all values in this matrix by
     * @return a new matrix representing this one divided by the scalar
     */
    public Matrix divideByScalar(double scalar) {
        Matrix result = new Matrix(this);

        for (double[] row : result.con) {
            for (int c = 0; c < row.length; c++) {
                row[c] /= scalar;
            }
        }

        return result;
    }

    /**
     * Treat this matrix as the left hand matrix and other as the right hand matrix
     * @param other matrix to multiply by
     * @return a new matrix representing this matrix multiplied by another matrix
     */
    public Matrix multiplyMatrix(Matrix other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot multiply this matrix by a null matrix");
        }

        if (con[0].length != other.con.length) {
            throw new IllegalArgumentException("This matrix's number of columns must" +
                    "equal the other matrix's number of rows");
        }

        Matrix result = new Matrix(con.length, other.con[0].length);

        for (int r = 0; r < con.length; r++) {
            for (int c = 0; c < other.con[0].length; c++) {
                // calculate the dot product between this matrix's current row and the other matrix's current column
                double dotProduct = 0.0;

                for (int i = 0; i < con[0].length; i++) {
                    dotProduct += con[r][i] * other.con[i][c];
                }

                result.con[r][c] = dotProduct;
            }
        }

        return result;
    }

    /**
     * Treat the matrix being multiplied as an m by n matrix being multiplied on the left side
     * <br>Treat the vector being multiplied as a 1 by n matrix being multiplied on the right side
     * @param other vector to multiply by,
     *              cannot be null and size must be equal to the columns in this matrix
     * @return a new vector representing this matrix multiplied by a vector
     */
    public Vector multiplyVector(Vector other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot multiply this matrix by a null vector");
        } else if (con[0].length != other.size()) {
            throw new IllegalArgumentException("This matrix's number of columns must equal the vector's size");
        }

        Vector result = new Vector(con.length);

        for (int matRow = 0; matRow < result.size(); matRow++) {
            double coordVal = 0.0;

            for (int vecIndex = 0; vecIndex < con[0].length; vecIndex++) {
                coordVal += con[matRow][vecIndex] * other.get(vecIndex);
            }

            result.set(matRow, coordVal);
        }

        return result;
    }

    // Setters

    /**
     * Set the value at (row, col) to a desired value
     * @param row row of value to set, must be within the dimensions of this matrix
     * @param col column of value to set, must be within the dimensions of this matrix
     * @param val to set at (row, col)
     */
    public void set(int row, int col, double val) {
        if (!validRowCol(row, col)) {
            throw new IllegalArgumentException("Row " + row + " and col " + col +
                    " are not within the dimensions of this matrix");
        }

        con[row][col] = val;
    }

    // Getters

    /**
     * @return this matrix transposed
     */
    public Matrix getTransposed() {
        Matrix result = new Matrix(con[0].length, con.length);

        for (int r = 0; r < con.length; r++) {
            for (int c = 0; c < con[0].length; c++) {
                result.con[c][r] = con[r][c];
            }
        }

        return result;
    }

    /**
     * @return the determinant of this matrix
     */
    public double getDeterminant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Cannot get determinant of non-square matrix");
        }

        if (con.length == 1) {
            return con[0][0];
        } else if (isUpperTriangular() || isLowerTriangular()) {
            return triangularDeterminant();
        } else {
            int[] cols = new int[con[0].length];

            for (int i = 0; i < cols.length; i++) {
                cols[i] = i;
            }

            return determinantHelper(0, cols);
        }
    }

    // Calculates the determinant of a triangular matrix by calculating the product of the diagonal
    private double triangularDeterminant() {
        double determinant = con[0][0];

        for (int i = 1; i < con.length; i++) {
            determinant *= con[i][i];
        }

        return determinant;
    }

    // Calculate the determinant recursively through the Laplace expansion
    private double determinantHelper(int row, int[] cols) {
        // base case: the sub-matrix is 2x2, calculate default determinant
        if (cols.length == 2) {
            return (con[row][cols[0]] * con[row + 1][cols[1]]) - (con[row][cols[1]] * con[row + 1][cols[0]]);
        }

        // otherwise: iterate through top row and appropriate columns
        double determinant = 0.0;
        for (int i = 0; i < cols.length; i++) {
            int col = cols[i];
            double val = con[row][col];

            if (!Constants.doubleEqualsZero(val)) {
                double multiplier = (i & 1) == 0 ? 1.0 : -1.0;
                determinant += multiplier * con[row][col] * determinantHelper(row + 1, removeCol(cols, i));
            }
        }

        return determinant;
    }

    // Helper method for determinantHelper to remove a column from an array of columns
    private int[] removeCol(int[] cols, int colIndex) {
        int[] newVals = new int[cols.length - 1];

        System.arraycopy(cols, 0, newVals, 0, colIndex);
        System.arraycopy(cols, colIndex + 1, newVals, colIndex, cols.length - 1 - colIndex);

        return newVals;
    }

    /**
     * @return whether or not this matrix is upper triangular
     */
    public boolean isUpperTriangular() {
        // a matrix cannot be upper triangular if it isn't square
        if (!isSquare()) {
            return false;
        }

        for (int r = con.length - 1; r > 0; r--) {
            for (int c = r - 1; c >= 0; c--) {
                if (!Constants.doubleEqualsZero(con[r][c])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return whether or not this matrix is lower triangular
     */
    public boolean isLowerTriangular() {
        // a matrix cannot be lower triangular if it isn't square
        if (!isSquare()) {
            return false;
        }

        for (int r = 0; r < con.length - 1; r++) {
            for (int c = con.length - 1; c > r; c--) {
                if (!Constants.doubleEqualsZero(con[r][c])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param row row of coordinate to be retrieved, must be within the dimensions of this matrix
     * @param col column of coordinate to be retrieved, must be within the dimensions of this matrix
     * @return a value at (row, col) in this matrix
     */
    public double get(int row, int col) {
        if (!validRowCol(row, col)) {
            throw new IllegalArgumentException("Row " + row + " and col " + col +
                    " are not within the dimensions of this matrix");
        }

        return con[row][col];
    }

    /**
     * @param row row of matrix to be retrieved, must be within the dimensions of this matrix
     * @return a row in this matrix
     */
    public double[] getRow(int row) {
        if (row < 0 || row >= con.length) {
            throw new IllegalArgumentException("Row " + row + " is not within the dimensions of this matrix");
        }

        return Arrays.copyOf(con[row], con[0].length);
    }

    /**
     * @param col col of matrix to be retrieved, must be within the dimensions of this matrix
     * @return a column in this matrix
     */
    public double[] getCol(int col) {
        if (col < 0 || col >= con[0].length) {
            throw new IllegalArgumentException("Col " + col + " is not within the dimensions of this matrix");
        }

        double[] returnCol = new double[con.length];

        for (int r = 0; r < con.length; r++) {
            returnCol[r] = con[r][col];
        }

        return returnCol;
    }

    /**
     * @param row row of matrix to be retrieved, must be within the dimensions of this matrix
     * @return a row in this matrix as a vector
     */
    public Vector getRowAsVector(int row) {
        return new Vector(getRow(row));
    }

    /**
     * @param col col of matrix to be retrieved, must be within the dimensions of this matrix
     * @return a col in this matrix as a vector
     */
    public Vector getColAsVector(int col) {
        return new Vector(getCol(col));
    }

    /**
     * @return the number of number of rows in this matrix
     */
    public int getNumRows() {
        return con.length;
    }

    /**
     * @return the number of columns in this matrix
     */
    public int getNumCols() {
        return con[0].length;
    }

    /**
     * @param other matrix to compare size with
     * @return whether or not this matrix is the same size as another matrix
     */
    public boolean isSameSize(Matrix other) {
        return con.length == other.con.length && con[0].length == other.con[0].length;
    }

    /**
     * @return whether or not this matrix is square
     */
    public boolean isSquare() {
        return con.length == con[0].length;
    }

    // Override

    /**
     * @return The values of this matrix in a table format
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();

        for (double[] row : con) {
            returnString.append("[");

            for (int c = 0; c < con[0].length - 1; c++) {
                returnString.append(String.format("%.2f", row[c]));
                returnString.append(", ");
            }

            returnString.append(String.format("%.2f", row[row.length - 1]));
            returnString.append("]");
            returnString.append("\n");
        }

        returnString.deleteCharAt(returnString.length() - 1);

        return returnString.toString();
    }

    /**
     * Two matrices are considered equal if they are the same size and have the same values
     * @param obj object to compare to
     * @return if an object is equal to this matrix
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Matrix)) {
            return false;
        }

        Matrix objAsMatrix = (Matrix) obj;

        if (!isSameSize(objAsMatrix)) {
            return false;
        }

        for (int r = 0; r < con.length; r++) {
            for (int c = 0; r < con[0].length; c++) {
                if (!Constants.doublesAreEqual(con[r][c], objAsMatrix.con[r][c])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return the hash code of this matrix
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(con);
    }

    // Helpers

    // Helper method for the 2D array initializer
    // Check if a given array is rectangular
    // array should never be null
    private boolean arrayIsRectangular(double[][] array) {
        for (int r = 1; r < array.length; r++) {
            if (array[r].length != array[0].length) {
                return false;
            }
        }

        return true;
    }

    // Check if the given row and column are within the dimensions of this matrix
    private boolean validRowCol(int row, int col) {
        return row >= 0 && col >= 0 && row < con.length && col < con[0].length;
    }

}
