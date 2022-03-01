package com.ntu.bdm;

import com.ntu.bdm.manager.ColumnIndexManager;
import com.ntu.bdm.manager.ColumnVectorManager;
import com.ntu.bdm.util.CSVFileUtil;
import com.ntu.bdm.util.FileUtil;
import com.ntu.bdm.util.TimestampUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/********************************************************
 * DiskDatabase is an application class. It manages the
 * data in the disk in a column-oriented manner.
 *
 * It first processes the input CSV file into multiple
 * column store CSV files and index txt files in the disk.
 * It then uses the index files to perform search queries.
 *
 ********************************************************/
public class DiskDatabase {

  public static final String EMPTY_DATA_SYMBOL = "M";
  public static final String DISK_COLUMN_STORAGE_PATH = "disk/column/";
  public static final String DISK_INDEX_STORAGE_PATH = "disk/index/";

  private ColumnVectorManager columnVectorManager;
  private ColumnIndexManager columnIndexManager;

  /**
   * Instantiates a new DiskDatabase.
   */
  public DiskDatabase() {
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
   * Write ColumnVectors to disk as CSV files.
   */
  public void writeColumnVectorsToDisk() {
    String timestampColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Timestamp.csv";
    String[] timestampColumnFileHeader = new String[]{"id", "Timestamp"};

    CSVFileUtil.writeHeader(timestampColumnFilePath, timestampColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(timestampColumnFilePath,
        columnVectorManager.serialiseStringColumnVector("Timestamp"));

    String temperatureColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Temperature.csv";
    String[] temperatureColumnFileHeader = new String[]{"id", "Temperature"};

    CSVFileUtil.writeHeader(temperatureColumnFilePath, temperatureColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(temperatureColumnFilePath,
        columnVectorManager.serialiseDoubleColumnVector("Temperature",
            EMPTY_DATA_SYMBOL));

    String humidityColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Humidity.csv";
    String[] humidityColumnFileHeader = new String[]{"id", "Humidity"};

    CSVFileUtil.writeHeader(humidityColumnFilePath, humidityColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(humidityColumnFilePath,
        columnVectorManager.serialiseDoubleColumnVector("Humidity",
            EMPTY_DATA_SYMBOL));
  }

  /**
   * Write CategoricalColumnIndexes to disk as txt files.
   */
  public void writeCategoricalColumnIndexesToDisk() {
    Map<String, byte[]> serialisedYear = columnIndexManager.serialiseCategoricalColumnIndex(
        "Year");
    for (Map.Entry<String, byte[]> entry : serialisedYear.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/year/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }

    Map<String, byte[]> serialisedMonth = columnIndexManager.serialiseCategoricalColumnIndex(
        "Month");
    for (Map.Entry<String, byte[]> entry : serialisedMonth.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/month/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }

    Map<String, byte[]> serialisedStation = columnIndexManager.serialiseCategoricalColumnIndex(
        "Station");
    for (Map.Entry<String, byte[]> entry : serialisedStation.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/station/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }
  }

  /**
   * Close connection to ColumnVectorManager.
   */
  public void closeColumnVectorManager() {
    columnVectorManager = null;
  }

  /**
   * Close connection to ColumnIndexManager.
   */
  public void closeColumnIndexManager() {
    columnIndexManager = null;
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

    String timestampColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Timestamp.csv";
    List<String[]> timestampColumnRows = CSVFileUtil.readDataAtOnce(timestampColumnFilePath);

    String columnFilePath = DISK_COLUMN_STORAGE_PATH + fieldName + ".csv";
    List<String[]> columnRows = CSVFileUtil.readDataAtOnce(columnFilePath);

    for (Integer position : minPositionList) {
      String category = "Min " + fieldName;
      String[] newRow = constructNewRow(position, queryParams.get("Station"), category,
          timestampColumnRows, columnRows);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    for (Integer position : maxPositionList) {
      String category = "Max " + fieldName;
      String[] newRow = constructNewRow(position, queryParams.get("Station"), category,
          timestampColumnRows, columnRows);

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

    List<Integer> minPositionList = new ArrayList<>();
    List<Integer> maxPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return List.of(minPositionList, maxPositionList);
    }

    String columnFilePath = DISK_COLUMN_STORAGE_PATH + fieldName + ".csv";
    List<String[]> csvRows = CSVFileUtil.readDataAtOnce(columnFilePath);

    String[] csvRow = csvRows.get(positionList.get(0));

    Double minimum = Double.valueOf(csvRow[1]);
    Double maximum = Double.valueOf(csvRow[1]);

    for (Integer position : positionList) {

      csvRow = csvRows.get(position);

      // Check if current value is null
      if (csvRow[1].equals(EMPTY_DATA_SYMBOL)) {
        continue;
      }

      Double current = Double.valueOf(csvRow[1]);

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

  /*
   * Gets list of indexes of rows that satisfy the year, month and station conditions inside query
   * parameters.
   */
  private List<Integer> getPositionListMatchingQueryParams(Map<String, String> queryParams) {
    List<Integer> positionList = new ArrayList<>();

    String stationIndexFilePath =
        DISK_INDEX_STORAGE_PATH + "/station/" + queryParams.get("Station") + ".txt";
    String yearIndexFilePath =
        DISK_INDEX_STORAGE_PATH + "/year/" + queryParams.get("Year") + ".txt";
    String monthIndexFilePath =
        DISK_INDEX_STORAGE_PATH + "/month/" + queryParams.get("Month") + ".txt";

    BitSet stationBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(stationIndexFilePath));
    BitSet yearBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(yearIndexFilePath));
    BitSet monthBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(monthIndexFilePath));

    /*
     * The following computes the bitwise AND between the bitmaps retrieved to obtain the bitmap
     * representing rows satisfying all query parameters
     */
    BitSet resultBitmap = (BitSet) stationBitmap.clone();
    resultBitmap.and(yearBitmap);
    resultBitmap.and(monthBitmap);

    // To iterate over the true bits in a bitmap, use the following loop
    for (int i = resultBitmap.nextSetBit(0); i >= 0; i = resultBitmap.nextSetBit(i + 1)) {
      positionList.add(i);
    }

    return positionList;
  }

  /*
   * Gets a string array which represents an output CSV row.
   */
  private String[] constructNewRow(Integer position, String station, String category,
      List<String[]> timestampColumnRows, List<String[]> columnRows) {
    String date = TimestampUtil.parseAndGetDate(timestampColumnRows.get(position)[1]);
    String fieldValue = String.valueOf(columnRows.get(position)[1]);
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
