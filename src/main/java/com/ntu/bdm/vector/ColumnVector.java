package com.ntu.bdm.vector;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/********************************************************
 * ColumnVector is a container class which represents a
 * column of type T. T can be Integer, String, Double,
 * Boolean etc.
 *
 * ColumnVector stores data and metadata for a column.
 *
 * Data includes actual values from column. Data is
 * stored in a data vector which is an array list that
 * stores elements.
 *
 * Metadata includes number of elements, number of null
 * elements as well a validity vector. Validity vector is
 * a bitmap and tracks index of null elements.
 *
 ********************************************************/
public class ColumnVector<T> {

  private int valueCount;
  private int nullCount;
  private BitSet validityVector;
  private List<T> dataVector;

  /**
   * Instantiates a new ColumnVector.
   */
  public ColumnVector() {
    valueCount = 0;
    nullCount = 0;
    validityVector = new BitSet();
    dataVector = new ArrayList<>();
  }

  /**
   * Gets the number of elements in the data vector.
   *
   * @return number of elements
   */
  public int getValueCount() {
    return valueCount;
  }

  /**
   * Returns number of null elements in the data vector.
   *
   * @return number of null elements
   */
  public int getNullCount() {
    return nullCount;
  }

  /**
   * Get the element at the given index from the data vector.
   *
   * @param index position of element
   * @return element at given index
   */
  public T get(int index) {
    return dataVector.get(index);
  }

  /**
   * Check whether an element in the data vector is null.
   *
   * @param index index to check for null
   * @return true if element is null
   */
  public boolean isNull(int index) {
    return !validityVector.get(index);
  }

  /**
   * Gets the validity vector.
   *
   * @return bitmap tracking which elements in the data vector are null
   */
  public BitSet getValidityVector() {
    return validityVector;
  }

  /**
   * Gets the data vector.
   *
   * @return vector of data
   */
  public List<T> getDataVector() {
    return dataVector;
  }

  /**
   * Insert value into the data vector and increment number of elements in data vector.
   *
   * If value is not null, flip bit representing index of value in data vector to 1 in validity
   * vector. Else, increment number of null elements in data vector.
   *
   * @param value the value
   */
  public void add(T value) {
    if (value != null) {
      validityVector.set(valueCount);
    } else {
      nullCount++;
    }
    dataVector.add(value);
    valueCount++;
  }
}
