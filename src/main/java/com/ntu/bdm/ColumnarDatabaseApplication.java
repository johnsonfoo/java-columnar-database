package com.ntu.bdm;

import com.ntu.bdm.util.CSVFileUtil;
import com.ntu.bdm.util.DateUtility;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnarDatabaseApplication {

  public static final String INPUT_FILE_PATH = "SingaporeWeather.csv";
  public static final String EMPTY_DATA_SYMBOL = "M";
  public static final String STATION = "Paya Lebar";
  public static final String[] YEARS = new String[]{"2003", "2013"};
  public static final String OUTPUT_FILE_PATH = "ScanResult.csv";
  public static final String[] OUTPUT_FILE_HEADER = new String[]{"Date", "Station", "Category",
      "Value"};

  public static void main(String[] args) {
    ColumnVectorManager columnVectorManager = createColumnVectorsFromCsv();
    populateColumnVectorsFromCsv(columnVectorManager,
        CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
    ColumnIndexManager columnIndexManager = new ColumnIndexManager();
    createCategoricalColumnIndexes(columnVectorManager, columnIndexManager);

    CSVFileUtil.writeHeader(OUTPUT_FILE_PATH, OUTPUT_FILE_HEADER);

    for (String year : YEARS) {
      for (Month month : Month.values()) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("Station", STATION);
        queryParameters.put("Year", year);
        queryParameters.put("Month", String.valueOf(month));

        List<Integer> positionList = columnIndexManager.findByFieldNamesAndCategories(
            queryParameters);

        List<List<Integer>> minimumMaximumTemperaturePositionList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Temperature", positionList);

        List<List<Integer>> minimumMaximumHumidityPositionList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Humidity", positionList);

        System.out.println("Year " + year + " Month " + month);
        System.out.println(
            "Min Temperature Index: " + minimumMaximumTemperaturePositionList.get(0));
        System.out.println(
            "Max Temperature Index: " + minimumMaximumTemperaturePositionList.get(1));
        System.out.println("Min Humidity Index: " + minimumMaximumHumidityPositionList.get(0));
        System.out.println("Max Humidity Index: " + minimumMaximumHumidityPositionList.get(1));
        System.out.println();

        CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
            getMinimumMaximumRowsWithDistinctDates(columnVectorManager, "Temperature",
                minimumMaximumTemperaturePositionList));
        CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
            getMinimumMaximumRowsWithDistinctDates(columnVectorManager, "Humidity",
                minimumMaximumHumidityPositionList));
      }
    }
  }

  public static ColumnVectorManager createColumnVectorsFromCsv() {
    ColumnVectorManager columnVectorManager = new ColumnVectorManager();

    columnVectorManager.createCategoricalColumnVector("Year");
    columnVectorManager.createCategoricalColumnVector("Month");
    columnVectorManager.createStringColumnVector("Timestamp");
    columnVectorManager.createCategoricalColumnVector("Station");
    columnVectorManager.createDoubleColumnVector("Temperature");
    columnVectorManager.createDoubleColumnVector("Humidity");

    return columnVectorManager;
  }

  public static void populateColumnVectorsFromCsv(ColumnVectorManager columnVectorManager,
      List<List<String>> csvRows
  ) {
    for (List<String> csvRow : csvRows) {
      String timestamp = csvRow.get(1);
      columnVectorManager.addToStringColumnVector("Timestamp", timestamp);

      String year = DateUtility.parseAndGetYear(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Year", year);

      String month = DateUtility.parseAndGetMonth(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Month", month);

      String station = csvRow.get(2);
      columnVectorManager.addToCategoricalColumnVector("Station", station);

      String temperatureString = csvRow.get(3);
      Double temperature =
          temperatureString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(temperatureString);
      columnVectorManager.addToDoubleColumnVector("Temperature", temperature);

      String humidityString = csvRow.get(4);
      Double humidity =
          humidityString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(humidityString);
      columnVectorManager.addToDoubleColumnVector("Humidity", humidity);
    }
  }

  private static void createCategoricalColumnIndexes(ColumnVectorManager columnVectorManager,
      ColumnIndexManager columnIndexManager) {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }

  public static List<String[]> getMinimumMaximumRowsWithDistinctDates(
      ColumnVectorManager columnVectorManager,
      String fieldName, List<List<Integer>> minimumMaximumPositionList) {
    String station = STATION;
    List<Integer> minimumPositionList = minimumMaximumPositionList.get(0);
    List<Integer> maximumPositionList = minimumMaximumPositionList.get(1);

    List<String[]> minimumMaximumRows = new ArrayList<>();

    for (Integer position : minimumPositionList) {
      String date = DateUtility.parseAndGetDay(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Min " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      int currentSize = minimumMaximumRows.size();
      String[] newRow = {date, station, category, fieldValue};

      if (currentSize > 0 && Arrays.equals(newRow, minimumMaximumRows.get(currentSize - 1))) {
        continue;
      }

      minimumMaximumRows.add(newRow);
    }

    for (Integer position : maximumPositionList) {
      String date = DateUtility.parseAndGetDay(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Max " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      int currentSize = minimumMaximumRows.size();
      String[] newRow = {date, station, category, fieldValue};

      if (currentSize > 0 && Arrays.equals(newRow, minimumMaximumRows.get(currentSize - 1))) {
        continue;
      }

      minimumMaximumRows.add(newRow);
    }

    return minimumMaximumRows;
  }
}
