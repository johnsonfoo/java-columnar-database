package com.ntu.bdm.vector;

import java.util.HashSet;

public class CategoricalColumnVector<T> extends ColumnVector<T> {

  private HashSet<T> categories;

  public CategoricalColumnVector() {
    super();
    categories = new HashSet<>();
  }

  public HashSet<T> getCategories() {
    return categories;
  }

  /**
   * Insert non-null values into the categories hashset before calling parent's add method.
   */
  @Override
  public void add(T value) {
    if (value != null) {
      categories.add(value);
    }
    super.add(value);
  }
}
