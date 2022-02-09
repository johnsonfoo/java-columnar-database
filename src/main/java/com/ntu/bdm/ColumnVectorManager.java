package com.ntu.bdm;

import com.ntu.bdm.vector.CategoricalColumnVector;
import com.ntu.bdm.vector.ColumnVector;
import java.util.HashMap;

public class ColumnVectorManager {

  private HashMap<String, CategoricalColumnVector<String>> categoricalColumnVectors;
  private HashMap<String, ColumnVector<Double>> doubleColumnVectors;
  private HashMap<String, ColumnVector<String>> stringColumnVectors;

  public ColumnVectorManager() {
    categoricalColumnVectors = new HashMap<>();
    doubleColumnVectors = new HashMap<>();
    stringColumnVectors = new HashMap<>();
  }

  public void createCategoricalColumnVector(String name) {
    categoricalColumnVectors.put(name, new CategoricalColumnVector<String>());
  }

  public void createColumnVector(Class<?> cls, String fieldName) {
    if (cls == Double.class) {
      doubleColumnVectors.put(fieldName, new ColumnVector<Double>());
    }
    if (cls == String.class) {
      stringColumnVectors.put(fieldName, new ColumnVector<String>());
    }
  }

  public void addToCategoricalColumnVector(String fieldName, String value) {
    categoricalColumnVectors.get(fieldName).add(value);
  }

  public <T> void addToColumnVector(String fieldName, T value) {
    if (doubleColumnVectors.containsKey(fieldName)) {
      doubleColumnVectors.get(fieldName).add((Double) value);
    }
    if (stringColumnVectors.containsKey(fieldName)) {
      stringColumnVectors.get(fieldName).add((String) value);
    }
  }

  public HashMap<String, CategoricalColumnVector<String>> getCategoricalColumnVectors() {
    return categoricalColumnVectors;
  }

  public HashMap<String, ColumnVector<Double>> getDoubleColumnVectors() {
    return doubleColumnVectors;
  }

  public HashMap<String, ColumnVector<String>> getStringColumnVectors() {
    return stringColumnVectors;
  }
}
