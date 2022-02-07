package com.ntu.bdm.vector;

import java.util.BitSet;
import java.util.List;

public interface ColumnVector<T> {

  /**
   * Gets the number of values.
   *
   * @return number of values in the vector
   */
  int getValueCount();

  /**
   * Returns number of null elements in the vector.
   *
   * @return number of null elements
   */
  int getNullCount();

  /**
   * Get the element at the given index from the data vector.
   *
   * @param index position of element
   * @return element at given index
   */
  T get(int index);

  /**
   * Check whether an element in the vector is null.
   *
   * @param index index to check for null
   * @return true if element is null
   */
  boolean isNull(int index);

  /**
   * Gets the validity vector.
   *
   * @return vector of bits tracking which elements in the vector are null
   */
  BitSet getValidityVector();

  /**
   * Gets the data vector.
   *
   * @return vector of data
   */
  List<T> getDataVector();
  
}
