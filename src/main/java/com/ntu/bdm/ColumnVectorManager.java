package com.ntu.bdm;

import com.ntu.bdm.vector.CategoricalColumnVector;
import com.ntu.bdm.vector.ColumnVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

  public List<List<Integer>> getMinimumMaximumPositionListByFieldName(String fieldName,
      List<Integer> positionList) {
    List<Double> dataVector = getDoubleColumnVectors().get(fieldName).getDataVector();

    List<Integer> minimumPositionList = new ArrayList<>();
    List<Integer> maximumPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return List.of(minimumPositionList, maximumPositionList);
    }

    Double minimum = dataVector.get(positionList.get(0));
    Double maximum = dataVector.get(positionList.get(0));

    for (Integer position : positionList) {
      Double current = dataVector.get(position);

      if (current < minimum) {
        minimum = current;
        minimumPositionList.clear();
        minimumPositionList.add(position);
      } else if (current.equals(minimum)) {
        minimumPositionList.add(position);
      }

      if (current > maximum) {
        maximum = current;
        maximumPositionList.clear();
        maximumPositionList.add(position);
      } else if (current.equals(maximum)) {
        maximumPositionList.add(position);
      }
    }

    return List.of(minimumPositionList, maximumPositionList);
  }
}
