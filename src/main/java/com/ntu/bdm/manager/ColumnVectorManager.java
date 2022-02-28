package com.ntu.bdm.manager;

import com.ntu.bdm.vector.CategoricalColumnVector;
import com.ntu.bdm.vector.ColumnVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/********************************************************
 * ColumnVectorManager is a controller class that is
 * responsible for managing {@link ColumnVector} and
 * {@link CategoricalColumnVector}. It exposes public
 * methods that clients use to interact with the
 * ColumnVector and CategoricalColumnVector class.
 *
 * It has multiple maps that each contain ColumnVector
 * and CategoricalColumnVector objects storing data of
 * different types. Each map uses a key fieldName to
 * uniquely identify an object.
 *
 ********************************************************/
public class ColumnVectorManager {

  private final Map<String, CategoricalColumnVector<String>> categoricalColumnVectors;
  private final Map<String, ColumnVector<Double>> doubleColumnVectors;
  private final Map<String, ColumnVector<String>> stringColumnVectors;

  /**
   * Instantiates a new ColumnVectorManager.
   */
  public ColumnVectorManager() {
    categoricalColumnVectors = new HashMap<>();
    doubleColumnVectors = new HashMap<>();
    stringColumnVectors = new HashMap<>();
  }

  /**
   * Create a new CategoricalColumnVector with the fieldName.
   *
   * @param fieldName the field name
   */
  public void createCategoricalColumnVector(String fieldName) {
    categoricalColumnVectors.put(fieldName, new CategoricalColumnVector<>());
  }

  /**
   * Create a new DoubleColumnVector with the fieldName.
   *
   * @param fieldName the field name
   */
  public void createDoubleColumnVector(String fieldName) {
    doubleColumnVectors.put(fieldName, new ColumnVector<>());
  }

  /**
   * Create a new StringColumnVector with the fieldName.
   *
   * @param fieldName the field name
   */
  public void createStringColumnVector(String fieldName) {
    stringColumnVectors.put(fieldName, new ColumnVector<>());
  }

  /**
   * Add a value to CategoricalColumnVector with the fieldName.
   *
   * @param fieldName the field name
   * @param value     the value
   */
  public void addToCategoricalColumnVector(String fieldName, String value) {
    categoricalColumnVectors.get(fieldName).add(value);
  }

  /**
   * Add a value to DoubleColumnVector with the fieldName.
   *
   * @param fieldName the field name
   * @param value     the value
   */
  public void addToDoubleColumnVector(String fieldName, Double value) {
    doubleColumnVectors.get(fieldName).add(value);
  }

  /**
   * Add a value to StringColumnVector with the fieldName.
   *
   * @param fieldName the field name
   * @param value     the value
   */
  public void addToStringColumnVector(String fieldName, String value) {
    stringColumnVectors.get(fieldName).add(value);
  }

  /**
   * Gets the CategoricalColumnVector map.
   *
   * @return the categorical column vectors
   */
  public Map<String, CategoricalColumnVector<String>> getCategoricalColumnVectors() {
    return categoricalColumnVectors;
  }

  /**
   * Gets a list of minimum and maximum indexes for DoubleColumnVector with the fieldName from a
   * positionList which contains valid indexes.
   *
   * @param fieldName    the field name
   * @param positionList the position list
   * @return the min max position list for field from position list
   */
  public List<List<Integer>> getMinMaxPositionListForFieldFromPositionList(String fieldName,
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

      // Check if current value is null
      if (doubleColumnVector.isNull(position)) {
        continue;
      }

      Double current = dataVector.get(position);

      /*
       * If current value if less than minimum value encountered so far, set minimum value to
       * current value, clear list of minimum indexes before adding current value index to list of
       * minimum indexes.
       *
       * Else if current value is equal to minimum value encountered so far, add current value
       * index to list of minimum indexes.
       */
      if (current < minimum) {
        minimum = current;
        minPositionList.clear();
        minPositionList.add(position);
      } else if (current.equals(minimum)) {
        minPositionList.add(position);
      }

      /*
       * If current value if more than maximum value encountered so far, set maximum value to
       * current value, clear list of maximum indexes before adding current value index to list of
       * maximum indexes.
       *
       * Else if current value is equal to maximum value encountered so far, add current value
       * index to list of maximum indexes.
       */
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

  /**
   * Gets element from DoubleColumnVector with the fieldName at index position.
   *
   * @param fieldName the field name
   * @param position  the position
   * @return the double for field with position
   */
  public Double getDoubleForFieldWithPosition(String fieldName, Integer position) {
    return doubleColumnVectors.get(fieldName).get(position);
  }

  /**
   * Gets element from StringColumnVector with the fieldName at index position.
   *
   * @param fieldName the field name
   * @param position  the position
   * @return the string for field with position
   */
  public String getStringForFieldWithPosition(String fieldName, Integer position) {
    return stringColumnVectors.get(fieldName).get(position);
  }

  /**
   * Serialise DoubleColumnVector into list of strings.
   *
   * @param fieldName       the field name
   * @param emptyDataSymbol the empty data symbol
   * @return the list
   */
  public List<String[]> serialiseDoubleColumnVector(String fieldName, String emptyDataSymbol) {
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

  /**
   * Serialise StringColumnVector into list of strings.
   *
   * @param fieldName the field name
   * @return the list
   */
  public List<String[]> serialiseStringColumnVector(String fieldName) {
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
