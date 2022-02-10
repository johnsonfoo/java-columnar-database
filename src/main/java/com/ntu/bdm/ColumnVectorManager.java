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

  public void createCategoricalColumnVector(String fieldName) {
    categoricalColumnVectors.put(fieldName, new CategoricalColumnVector<String>());
  }

  public void createDoubleColumnVector(String fieldName) {
    doubleColumnVectors.put(fieldName, new ColumnVector<Double>());
  }

  public void createStringColumnVector(String fieldName) {
    stringColumnVectors.put(fieldName, new ColumnVector<String>());
  }

  public void addToCategoricalColumnVector(String fieldName, String value) {
    categoricalColumnVectors.get(fieldName).add(value);
  }

  public void addToDoubleColumnVector(String fieldName, Double value) {
    doubleColumnVectors.get(fieldName).add(value);
  }

  public void addToStringColumnVector(String fieldName, String value) {
    stringColumnVectors.get(fieldName).add(value);
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
