package com.ntu.bdm;

import com.ntu.bdm.vector.CategoricalColumnVector;
import com.ntu.bdm.vector.ColumnVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnVectorManager {

  private Map<String, CategoricalColumnVector<String>> categoricalColumnVectors;
  private Map<String, ColumnVector<Double>> doubleColumnVectors;
  private Map<String, ColumnVector<String>> stringColumnVectors;

  public ColumnVectorManager() {
    categoricalColumnVectors = new HashMap<>();
    doubleColumnVectors = new HashMap<>();
    stringColumnVectors = new HashMap<>();
  }

  public void createCategoricalColumnVector(String fieldName) {
    categoricalColumnVectors.put(fieldName, new CategoricalColumnVector<>());
  }

  public void createDoubleColumnVector(String fieldName) {
    doubleColumnVectors.put(fieldName, new ColumnVector<>());
  }

  public void createStringColumnVector(String fieldName) {
    stringColumnVectors.put(fieldName, new ColumnVector<>());
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

  public Map<String, CategoricalColumnVector<String>> getCategoricalColumnVectors() {
    return categoricalColumnVectors;
  }

  public Map<String, ColumnVector<Double>> getDoubleColumnVectors() {
    return doubleColumnVectors;
  }

  public Map<String, ColumnVector<String>> getStringColumnVectors() {
    return stringColumnVectors;
  }

  public List<List<Integer>> getMinimumMaximumPositionListByFieldName(String fieldName,
      List<Integer> positionList) {
    ColumnVector<Double> doubleColumnVector = doubleColumnVectors.get(fieldName);
    List<Double> dataVector = doubleColumnVector.getDataVector();

    List<Integer> minimumPositionList = new ArrayList<>();
    List<Integer> maximumPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return List.of(minimumPositionList, maximumPositionList);
    }

    Double minimum = dataVector.get(positionList.get(0));
    Double maximum = dataVector.get(positionList.get(0));

    for (Integer position : positionList) {

      if (doubleColumnVector.isNull(position)) {
        continue;
      }

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

  public Double getDoubleByFieldNameAndPosition(String fieldName, Integer position) {
    return doubleColumnVectors.get(fieldName).get(position);
  }

  public String getStringByFieldNameAndPosition(String fieldName, Integer position) {
    return stringColumnVectors.get(fieldName).get(position);
  }
}
