package com.ntu.bdm.vector;

import java.util.HashSet;
import java.util.Set;

/********************************************************
 * CategoricalColumnVector is a container class that is
 * an extension to {@link ColumnVector}. It represents a
 * column with discrete values of type T. T can be
 * Integer, String, Double, Boolean etc.
 *
 * Additional metadata stored is the discrete values
 * inside data vector. The discrete values are stored in
 * categories hashset that only stores unique values.
 *
 ********************************************************/
public class CategoricalColumnVector<T> extends ColumnVector<T> {

  private Set<T> categories;

  /**
   * Instantiates a new CategoricalColumnVector.
   */
  public CategoricalColumnVector() {
    super();
    categories = new HashSet<>();
  }

  /**
   * Gets unique elements in data vector.
   *
   * @return the categories
   */
  public Set<T> getCategories() {
    return categories;
  }

  /**
   * Insert non-null elements into the categories hashset before calling {@link
   * ColumnVector#add(Object)}.
   */
  @Override
  public void add(T value) {
    if (value != null) {
      categories.add(value);
    }
    super.add(value);
  }
}
