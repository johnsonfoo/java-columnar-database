package com.ntu.bdm;

import com.ntu.bdm.util.TimestampUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMemoryDatabase {

  public static final String EMPTY_DATA_SYMBOL = "M";

  private ColumnVectorManager columnVectorManager;
  private ColumnIndexManager columnIndexManager;

  public MainMemoryDatabase() {
    columnVectorManager = new ColumnVectorManager();
    columnIndexManager = new ColumnIndexManager();
  }

  public void initialiseColumnVectors() {
    columnVectorManager.createStringColumnVector("Timestamp");
    columnVectorManager.createCategoricalColumnVector("Station");
    columnVectorManager.createDoubleColumnVector("Temperature");
    columnVectorManager.createDoubleColumnVector("Humidity");
    columnVectorManager.createCategoricalColumnVector("Year");
    columnVectorManager.createCategoricalColumnVector("Month");
  }

  public void populateColumnVectors(List<String[]> csvRows) {
    for (String[] csvRow : csvRows) {
      String timestamp = csvRow[1];
      columnVectorManager.addToStringColumnVector("Timestamp", timestamp);

      String station = csvRow[2];
      columnVectorManager.addToCategoricalColumnVector("Station", station);

      String temperatureString = csvRow[3];
      Double temperature =
          temperatureString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(temperatureString);
      columnVectorManager.addToDoubleColumnVector("Temperature", temperature);

      String humidityString = csvRow[4];
      Double humidity =
          humidityString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(humidityString);
      columnVectorManager.addToDoubleColumnVector("Humidity", humidity);

      String year = TimestampUtil.parseAndGetYear(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Year", year);

      String month = TimestampUtil.parseAndGetMonth(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Month", month);
    }
  }

  public void createCategoricalColumnIndexes() {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }

  public List<String[]> getMinMaxRowsWithDistinctDate(String fieldName,
      String station, String year, String month) {
    List<String[]> minMaxRows = new ArrayList<>();

    List<List<Integer>> minMaxPositionList = getMinMaxPositionList(fieldName, station, year, month);
    List<Integer> minPositionList = minMaxPositionList.get(0);
    List<Integer> maxPositionList = minMaxPositionList.get(1);

    for (Integer position : minPositionList) {
      String category = "Min " + fieldName;
      String[] newRow = constructNewRow(position, station, category, fieldName);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    for (Integer position : maxPositionList) {
      String category = "Max " + fieldName;
      String[] newRow = constructNewRow(position, station, category, fieldName);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    return minMaxRows;
  }

  private List<List<Integer>> getMinMaxPositionList(String fieldName, String station, String year,
      String month) {
    List<Integer> positionList = getPositionList(station, year, month);

    return columnVectorManager.getMinMaxPositionListByFieldName(fieldName, positionList);
  }

  private List<Integer> getPositionList(String station, String year, String month) {
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put("Station", station);
    queryParameters.put("Year", year);
    queryParameters.put("Month", month);

    return columnIndexManager.findByFieldNamesAndCategories(queryParameters);
  }

  private String[] constructNewRow(Integer position, String station, String category,
      String fieldName) {
    String date = TimestampUtil.parseAndGetDate(
        columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
    String fieldValue = String.valueOf(
        columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));
    return new String[]{date, station, category, fieldValue};
  }

  private boolean checkNewRowIsDifferent(List<String[]> minMaxRows, String[] newRow) {
    int currentSize = minMaxRows.size();
    return currentSize <= 0 || !Arrays.equals(newRow, minMaxRows.get(currentSize - 1));
  }
}
