package com.ntu.bdm.vector;

import java.util.HashSet;

public class CategoricalStringColumnVector extends StringColumnVector {

  private HashSet<String> categories;

  public CategoricalStringColumnVector() {
    super();
    categories = new HashSet<>();
  }

  public HashSet<String> getCategories() {
    return categories;
  }

  @Override
  public void add(String value) {
    if (value != null) {
      categories.add(value);
    }
    super.add(value);
  }
}
