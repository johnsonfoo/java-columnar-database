package com.ntu.bdm.vector;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class AbstractColumnVector<T>{

  private int valueCount;
  private int nullCount;
  private BitSet validityVector;
  private List<T> dataVector;

  public AbstractColumnVector() {
    valueCount = 0;
    nullCount = 0;
    validityVector = new BitSet();
    dataVector = new ArrayList<>();
  }

  /**
   * Gets the number of values.
   *
   * @return number of values in the vector
   */
  public int getValueCount() {
    return valueCount;
  }

  /**
   * Returns number of null elements in the vector.
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
   * Check whether an element in the vector is null.
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
   * @return vector of bits tracking which elements in the vector are null
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
   * Insert value into the data vector and increment number of values in vector.
   * <p>
   * If value is not null, flip bit representing index of value in data vector to 1 in validity
   * vector. Else, increment number of null elements in vector.
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
