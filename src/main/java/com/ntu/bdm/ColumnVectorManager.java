package com.ntu.bdm;

import com.ntu.bdm.vector.CategoricalColumnVector;
import com.ntu.bdm.vector.ColumnVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnVectorManager {

  private final Map<String, CategoricalColumnVector<String>> categoricalColumnVectors;
  private final Map<String, ColumnVector<Double>> doubleColumnVectors;
  private final Map<String, ColumnVector<String>> stringColumnVectors;

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

  public List<List<Integer>> getMinMaxPositionListByFieldName(String fieldName,
      List<Integer> positionList) {
    List<Integer> minPositionList = new ArrayList<>();
    List<Integer> maxPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return List.of(minPositionList, maxPositionList);
    }

    ColumnVector<Double> doubleColumnVector = doubleColumnVectors.get(fieldName);
    List<Double> dataVector = doubleColumnVector.getDataVector();

    Double minimum = dataVector.get(positionList.get(0));
    Double maximum = dataVector.get(positionList.get(0));

    for (Integer position : positionList) {

      if (doubleColumnVector.isNull(position)) {
        continue;
      }

      Double current = dataVector.get(position);

      if (current < minimum) {
        minimum = current;
        minPositionList.clear();
        minPositionList.add(position);
      } else if (current.equals(minimum)) {
        minPositionList.add(position);
      }

      if (current > maximum) {
        maximum = current;
        maxPositionList.clear();
        maxPositionList.add(position);
      } else if (current.equals(maximum)) {
        maxPositionList.add(position);
      }
    }

    return List.of(minPositionList, maxPositionList);
  }

  public Double getDoubleByFieldNameAndPosition(String fieldName, Integer position) {
    return doubleColumnVectors.get(fieldName).get(position);
  }

  public String getStringByFieldNameAndPosition(String fieldName, Integer position) {
    return stringColumnVectors.get(fieldName).get(position);
  }

  public List<String[]> serialiseDoubleColumnVectorByFieldName(String fieldName,
      String emptyDataSymbol) {
    List<String[]> serialisedDoubleColumnVector = new ArrayList<>();

    ColumnVector<Double> doubleColumnVector = doubleColumnVectors.get(fieldName);
    List<Double> dataVector = doubleColumnVector.getDataVector();

    for (int i = 0; i < doubleColumnVector.getValueCount(); i++) {
      String id = String.valueOf(i);
      if (doubleColumnVector.isNull(i)) {
        serialisedDoubleColumnVector.add(new String[]{id, emptyDataSymbol});
      } else {
        serialisedDoubleColumnVector.add(new String[]{id, String.valueOf(dataVector.get(i))});
      }
    }

    return serialisedDoubleColumnVector;
  }

  public List<String[]> serialiseStringColumnVectorByFieldName(String fieldName) {
    List<String[]> serialisedStringColumnVector = new ArrayList<>();

    ColumnVector<String> stringColumnVector = stringColumnVectors.get(fieldName);
    List<String> dataVector = stringColumnVector.getDataVector();

    for (int i = 0; i < stringColumnVector.getValueCount(); i++) {
      String id = String.valueOf(i);
      serialisedStringColumnVector.add(new String[]{id, String.valueOf(dataVector.get(i))});
    }

    return serialisedStringColumnVector;
  }
}
