package com.ntu.bdm;

import com.ntu.bdm.util.CSVFileUtil;
import com.ntu.bdm.util.FileUtil;
import com.ntu.bdm.util.TimestampUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class DiskDatabase {

  public static final String EMPTY_DATA_SYMBOL = "M";
  public static final String DISK_COLUMN_STORAGE_PATH = "disk/column/";
  public static final String DISK_INDEX_STORAGE_PATH = "disk/index/";

  private ColumnVectorManager columnVectorManager;
  private ColumnIndexManager columnIndexManager;

  public DiskDatabase() {
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

  public void writeColumnVectorsToDisk() {
    String timestampColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Timestamp.csv";
    String[] timestampColumnFileHeader = new String[]{"id", "Timestamp"};

    CSVFileUtil.writeHeader(timestampColumnFilePath, timestampColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(timestampColumnFilePath,
        columnVectorManager.serialiseStringColumnVectorByFieldName("Timestamp"));

    String temperatureColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Temperature.csv";
    String[] temperatureColumnFileHeader = new String[]{"id", "Temperature"};

    CSVFileUtil.writeHeader(temperatureColumnFilePath, temperatureColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(temperatureColumnFilePath,
        columnVectorManager.serialiseDoubleColumnVectorByFieldName("Temperature",
            EMPTY_DATA_SYMBOL));

    String humidityColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Humidity.csv";
    String[] humidityColumnFileHeader = new String[]{"id", "Humidity"};

    CSVFileUtil.writeHeader(humidityColumnFilePath, humidityColumnFileHeader);
    CSVFileUtil.writeDataAtOnce(humidityColumnFilePath,
        columnVectorManager.serialiseDoubleColumnVectorByFieldName("Humidity",
            EMPTY_DATA_SYMBOL));
  }

  public void writeCategoricalColumnIndexesToDisk() {
    Map<String, byte[]> serialisedYear = columnIndexManager.serialiseCategoricalColumnIndexByFieldName(
        "Year");
    for (Map.Entry<String, byte[]> entry : serialisedYear.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/year/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }

    Map<String, byte[]> serialisedMonth = columnIndexManager.serialiseCategoricalColumnIndexByFieldName(
        "Month");
    for (Map.Entry<String, byte[]> entry : serialisedMonth.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/month/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }

    Map<String, byte[]> serialisedStation = columnIndexManager.serialiseCategoricalColumnIndexByFieldName(
        "Station");
    for (Map.Entry<String, byte[]> entry : serialisedStation.entrySet()) {
      String category = entry.getKey();
      byte[] bytes = entry.getValue();
      String indexFilePath = DISK_INDEX_STORAGE_PATH + "/station/" + category + ".txt";
      FileUtil.writeBytesToFile(indexFilePath, bytes);
    }
  }

  public void clearColumnVectorManagerContents() {
    columnVectorManager = null;
  }

  public void clearColumnIndexManagerContents() {
    columnIndexManager = null;
  }

  public List<String[]> getMinMaxRowsWithDistinctDate(String fieldName,
      String station, String year, String month) {
    List<String[]> minMaxRows = new ArrayList<>();

    List<List<Integer>> minMaxPositionList = getMinMaxPositionList(fieldName, station, year, month);
    List<Integer> minPositionList = minMaxPositionList.get(0);
    List<Integer> maxPositionList = minMaxPositionList.get(1);

    String timestampColumnFilePath = DISK_COLUMN_STORAGE_PATH + "Timestamp.csv";
    List<String[]> timestampColumnRows = CSVFileUtil.readDataAtOnce(timestampColumnFilePath);

    String columnFilePath = DISK_COLUMN_STORAGE_PATH + fieldName + ".csv";
    List<String[]> columnRows = CSVFileUtil.readDataAtOnce(columnFilePath);

    for (Integer position : minPositionList) {
      String category = "Min " + fieldName;
      String[] newRow = constructNewRow(position, station, category, timestampColumnRows,
          columnRows);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    for (Integer position : maxPositionList) {
      String category = "Max " + fieldName;
      String[] newRow = constructNewRow(position, station, category, timestampColumnRows,
          columnRows);

      if (checkNewRowIsDifferent(minMaxRows, newRow)) {
        minMaxRows.add(newRow);
      }
    }

    return minMaxRows;
  }

  private List<List<Integer>> getMinMaxPositionList(String fieldName, String station, String year,
      String month) {
    List<Integer> positionList = getPositionList(station, year, month);

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

      if (csvRow[1].equals(EMPTY_DATA_SYMBOL)) {
        continue;
      }

      Double current = Double.valueOf(csvRow[1]);

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

  private List<Integer> getPositionList(String station, String year, String month) {
    List<Integer> positionList = new ArrayList<>();

    String stationIndexFilePath = DISK_INDEX_STORAGE_PATH + "/station/" + station + ".txt";
    String yearIndexFilePath = DISK_INDEX_STORAGE_PATH + "/year/" + year + ".txt";
    String monthIndexFilePath = DISK_INDEX_STORAGE_PATH + "/month/" + month + ".txt";

    BitSet stationBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(stationIndexFilePath));
    BitSet yearBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(yearIndexFilePath));
    BitSet monthBitmap = BitSet.valueOf(FileUtil.readBytesFromFile(monthIndexFilePath));

    BitSet resultBitmap = (BitSet) stationBitmap.clone();
    resultBitmap.and(yearBitmap);
    resultBitmap.and(monthBitmap);

    for (int i = resultBitmap.nextSetBit(0); i >= 0; i = resultBitmap.nextSetBit(i + 1)) {
      positionList.add(i);
    }

    return positionList;
  }

  private String[] constructNewRow(Integer position, String station, String category,
      List<String[]> timestampColumnRows, List<String[]> columnRows) {
    String date = TimestampUtil.parseAndGetDate(timestampColumnRows.get(position)[1]);
    String fieldValue = String.valueOf(columnRows.get(position)[1]);
    String[] newRow = {date, station, category, fieldValue};
    return newRow;
  }

  private boolean checkNewRowIsDifferent(List<String[]> minMaxRows, String[] newRow) {
    int currentSize = minMaxRows.size();
    if (currentSize > 0 && Arrays.equals(newRow, minMaxRows.get(currentSize - 1))) {
      return false;
    }
    return true;
  }
}