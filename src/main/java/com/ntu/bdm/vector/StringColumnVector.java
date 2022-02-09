package com.ntu.bdm.vector;

import java.util.HashSet;

public class StringColumnVector extends AbstractColumnVector<String> {

  private HashSet<String> uniqueData;

  public StringColumnVector() {
    super();
    uniqueData = new HashSet<>();
  }

  public HashSet<String> getUniqueData() {
    return uniqueData;
  }

  @Override
  public void add(String value) {
    if (value != null) {
      uniqueData.add(value);
    }
    super.add(value);
  }
}
