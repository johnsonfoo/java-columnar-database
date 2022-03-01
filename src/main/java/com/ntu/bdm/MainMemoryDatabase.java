package com.ntu.bdm;

import com.ntu.bdm.manager.ColumnIndexManager;
import com.ntu.bdm.manager.ColumnVectorManager;
import com.ntu.bdm.util.TimestampUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/********************************************************
 * MainMemoryDatabase is an application class. It manages
 * the data in the main memory in a column-oriented manner,
 * including data storage and processing.
 *
 ********************************************************/
public class MainMemoryDatabase {

  private static final String EMPTY_DATA_SYMBOL = "M";

  private ColumnVectorManager columnVectorManager;
  private ColumnIndexManager columnIndexManager;

  /**
   * Instantiates a new MainMemoryDatabase.
   */
  public MainMemoryDatabase() {
    columnVectorManager = new ColumnVectorManager();
    columnIndexManager = new ColumnIndexManager();
  }

  /**
   * Initialise ColumnVectors and CategoricalColumnVectors.
   */
  public void initialiseColumnVectors() {
    columnVectorManager.createStringColumnVector("Timestamp");
    columnVectorManager.createCategoricalColumnVector("Station");
    columnVectorManager.createDoubleColumnVector("Temperature");
    columnVectorManager.createDoubleColumnVector("Humidity");
    columnVectorManager.createCategoricalColumnVector("Year");
    columnVectorManager.createCategoricalColumnVector("Month");
  }

  /**
   * Populate ColumnVectors and CategoricalColumnVectors with data from input CSV rows.
   *
   * @param csvRows the csv rows
   */
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

  /**
   * Create CategoricalColumnIndexes.
   */
  public void createCategoricalColumnIndexes() {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }

  /**
   * Gets list of string array. Each element in list represents an output CSV row. The list of
   * string array contain minimum and maximum values of column with the fieldName. The minimum and
   * maximum values are searched from input CSV rows that satisfy the year, month and station
   * conditions inside queryParams.
   *
   * @param fieldName   the field name
   * @param queryParams the query params
   * @return the min max rows with distinct date for field matching query params
   */
  public List<String[]> getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams(String fieldName,
      Map<String, String> queryParams) {
    List<String[]> minMaxRows = new ArrayList<>();

    List<List<Integer>> minMaxPositionList = getMinMaxPositionListForFieldMatchingQueryParams(
        fieldName, queryParams);
    List<Integer> minPositionList = minMaxPositionList.get(0);
    List<Integer> maxPositionList = minMaxPositionList.get(1);

    for (Integer position : minPositionList) {
      String category = "Min " + fieldName;
      String[] newRow = constructNewRow(position, queryParams.get("Station"), category, fieldName);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    for (Integer position : maxPositionList) {
      String category = "Max " + fieldName;
      String[] newRow = constructNewRow(position, queryParams.get("Station"), category, fieldName);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    return minMaxRows;
  }

  /*
   * Gets a list of minimum and maximum indexes for column with the fieldName. The indexes belong to
   * the rows that satisfy the year, month and station conditions inside query parameters.
   */
  private List<List<Integer>> getMinMaxPositionListForFieldMatchingQueryParams(String fieldName,
      Map<String, String> queryParams) {
    List<Integer> positionList = getPositionListMatchingQueryParams(queryParams);

    return columnVectorManager.getMinMaxPositionListForFieldFromPositionList(fieldName,
        positionList);
  }

  /*
   * Gets list of indexes of rows that satisfy the year, month and station conditions inside query
   * parameters.
   */
  private List<Integer> getPositionListMatchingQueryParams(Map<String, String> queryParams) {
    return columnIndexManager.getPositionListMatchingQueryParams(queryParams);
  }

  /*
   * Gets a string array which represents an output CSV row.
   */
  private String[] constructNewRow(Integer position, String station, String category,
      String fieldName) {
    String date = TimestampUtil.parseAndGetDate(
        columnVectorManager.getStringForFieldWithPosition("Timestamp", position));
    String fieldValue = String.valueOf(
        columnVectorManager.getDoubleForFieldWithPosition(fieldName, position));
    return new String[]{date, station, category, fieldValue};
  }

  /*
   * Checks if the string array which represents an output CSV row is not present in minMaxRows.
   * Returns true if it is not present, false if present.
   */
  private boolean checkNewRowIsDifferent(List<String[]> minMaxRows, String[] newRow) {
    int currentSize = minMaxRows.size();
    return currentSize <= 0 || !Arrays.equals(newRow, minMaxRows.get(currentSize - 1));
  }
}
