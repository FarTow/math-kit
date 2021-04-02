package mathkit;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Mathematical vector of any size stemming from the origin
 */
public class Vector implements Iterable<Double> {
    /**
     * Container for all coordinates stored in this vector
     */
    private final double[] con;

    // Constructors

    /**
     * Initialize this vector to be empty with size amount of coordinates
     * @param size amount of coordinates in this vector, must be greater than 0
     */
    public Vector(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size of a vector must be greater than 0");
        }

        con = new double[size];
    }


    /**
     * Initialize this matrix to have size amount of coordinates with all values set to val
     * @param size amount of coordinates in this vector, must be greater than 0
     * @param val default value of this vector
     */
    public Vector(int size, double val) {
        this(size);

        Arrays.fill(con, val);
    }

    /**
     * Initialize this vector's coordinates based on a variable amount of arguments
     * @param vals each coordinate in this vector, cannot be null
     */
    public Vector(double ... vals) {
        if (vals == null) {
            throw new IllegalArgumentException("Cannot create a vector from null values");
        }

        con = vals;
    }

    /**
     * Initialize this vector to be a copy of another vector
     * @param other vector to copy values of, cannot be null
     */
    public Vector(Vector other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot create vector from a null vector");
        }

        con = new double[other.con.length];
        System.arraycopy(other.con, 0, con, 0, other.con.length);
    }

    // Calculations

    /**
     * @param other vector to add, must be the same size as this vector
     * @return a new vector representing the sum of the two vectors
     */
    public Vector add(Vector other) {
        if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot add two vectors of different sizes");
        }

        Vector result = new Vector(this);

        for (int i = 0; i < con.length; i++) {
            result.con[i] += other.con[i];
        }

        return result;
    }

    /**
     * @param other vector to subtract, must be the same size as this vector
     * @return a new vector representing the difference of the two vectors
     */
    public Vector subtract(Vector other) {
        if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot subtract two vectors of different sizes");
        }

        Vector result = new Vector(this);

        for (int i = 0; i < con.length; i++) {
            result.con[i] -= other.con[i];
        }

        return result;
    }

    /**
     * @param other vector to calculate dot product with, must be the same size as this vector
     * @return a new vector representing the dot product of the two vectors
     */
    public double dotProduct(Vector other) {
        if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot calculate the dot product of two vectors of different sizes");
        }

        double result = 0.0;

        for (int i = 0; i < con.length; i++) {
            result += con[i] * other.con[i];
        }

        return result;
    }

    /**
     * @param other vector to calculate cross product with, must be 3D
     * @return a new vector representing the cross product of the two vectors
     */
    public Vector crossProduct(Vector other) {
        if (con.length != 3 && other.con.length != 3) {
            throw new IllegalArgumentException("Cannot calculate the cross product of non-3D vectors");
        }

        return new Vector(
                (con[1] * other.con[2]) - (con[2] * other.con[1]),
                (con[2] * other.con[0]) - (con[0] * other.con[2]),
                (con[0] * other.con[1]) - (con[1] * other.con[0])
        );
    }

    /**
     * @param scalar value to multiply all values by
     * @return a new vector representing this vector multiplied by the scalar
     */
    public Vector multiplyByScalar(double scalar) {
        Vector result = new Vector(this);

        for (int i = 0; i < con.length; i++) {
            result.con[i] *= scalar;
        }

        return result;
    }

    /**
     * @param scalar value to divide all values by
     * @return a new vector representing this vector divided by the scalar
     */
    public Vector divideByScalar(double scalar) {
        Vector result = new Vector(this);

        for (int i = 0; i < con.length; i++) {
            result.con[i] /= scalar;
        }

        return result;
    }

    /**
     * @return this vector normalized (unit vector)
     */
    public Vector normalized() {
        return divideByScalar(magnitude());
    }

    /**
     * @param other vector to calculate angle between
     * @return the angle between this vector and another in radians
     */
    public double angleRadians(Vector other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot calculate the angle between this vector and null");
        } else if (!isSameSize(other)) {
            throw new IllegalArgumentException("Cannot calculate the angle between two vectors of different sizes");
        }

        return Math.acos(dotProduct(other) / (magnitude() * other.magnitude()));
    }

    /**
     * @param other vector to calculate angle between
     * @return the angle between this vector and another in degrees
     */
    public double angleDegrees(Vector other) {
        return Math.toDegrees(angleRadians(other));
    }

    /**
     * @param b vector to find scalar triple product with
     * @param c vector to find scalar triple product with
     * @return the scalar triple product of three vectors
     */
    public double scalarTripleProduct(Vector b, Vector c) {
        if (b == null || c == null) {
            throw new IllegalArgumentException("Cannot calculate the scalar triple product between null vectors");
        }

        return crossProduct(b).dotProduct(c);
    }

    /**
     * Treat the vector being multiplied as an n by 1 matrix being multiplied on the left side
     * <br>Treat the matrix being multiplied as an n by p matrix being multiplied on the right side
     * @param other matrix to multiply vector by,
     *              cannot be null and the rows must equal the size of this vector
     * @return new vector representing this vector multiplied by a matrix
     */
    public Vector multiplyMatrix(Matrix other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot multiply this vector by a null matrix");
        } else if (con.length != other.getNumRows()) {
            throw new IllegalArgumentException("This vector's size must be the same as the rows in the matrix");
        }

        Vector result = new Vector(other.getNumCols());

        for (int vecIndex = 0; vecIndex < other.getNumRows(); vecIndex++) {
            for (int matCol = 0; matCol < other.getNumCols(); matCol++) {
                result.con[matCol] += con[vecIndex] * other.get(vecIndex, matCol);
            }
        }

        return result;
    }

    // Setters

    /**
     * @param index index of coordinate to set, must be within the range of this vector
     * @param val value to be set at index
     */
    public void set(int index, double val) {
        if (!validIndex(index)) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds");
        }

        con[index] = val;
    }

    // Getters

    /**
     * @return the magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(dotProduct(this));
    }

    /**
     * Used for quick comparisons of magnitude
     * @return the magnitude of this vector squared
     */
    public double quickMagnitude() {
        return dotProduct(this);
    }

    /**
     * @param index index of coordinate to be retrieved, must be within the range of this vector
     * @return the coordinate based on its index in this vector's container
     */
    public double get(int index) {
        if (!validIndex(index)) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds");
        }

        return con[index];
    }

    /**
     * @return the size (amount of coordinates) in this vector
     */
    public int size() {
        return con.length;
    }

    // Override

    /**
     * @return the values of this vector in a sequential list format
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("<");

        for (int i = 0; i < con.length - 1; i++) {
            returnString.append(String.format("%.2f", con[i]));
            returnString.append(", ");
        }

        returnString.append(String.format("%.2f", con[con.length - 1]));
        returnString.append(">");

        return returnString.toString();
    }

    /**
     * Two vectors are considered equal if they are the same size and have the same values
     * @param obj object to compare to
     * @return if an object is equal to this vector
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Vector)) {
            return false;
        }

        Vector objAsVector = (Vector) obj;

        if (!isSameSize(objAsVector)) {
            return false;
        }

        for (int i = 0; i < con.length; i++) {
            if (!Constants.doublesAreEqual(con[i], objAsVector.con[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return the hash code of this vector
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(con);
    }

    /**
     * @return an iterator to traverse the coordinates that compose this vector
     */
    @Override
    public Iterator<Double> iterator() {
        return Arrays.stream(con).iterator();
    }

    // Helper

    // Check if the given index is contained within this vector
    private boolean validIndex(int index) {
        return index >= 0 && index < con.length;
    }

    // Check if the given vector is the same size as this vector
    // other should never be null
    private boolean isSameSize(Vector other) {
        return con.length == other.con.length;
    }

}
