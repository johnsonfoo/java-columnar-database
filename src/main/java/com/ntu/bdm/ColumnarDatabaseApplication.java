package com.ntu.bdm;

import com.ntu.bdm.util.DateUtility;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnarDatabaseApplication {

  public static void main(String[] args) {
    CsvFileManager csvFileManager = new CsvFileManager();
    csvFileManager.readDataAtOnce("SingaporeWeather.csv");

    ColumnVectorManager columnVectorManager = createColumnVectorsFromCsv();
    populateColumnVectorsFromCsv(csvFileManager, columnVectorManager);
    ColumnIndexManager columnIndexManager = new ColumnIndexManager();
    createCategoricalColumnIndexes(columnVectorManager, columnIndexManager);

    csvFileManager.writeHeader("ScanResult.csv",
        new String[]{"Date", "Station", "Category", "Value"});

    for (String year : new String[]{"2003", "2013"}) {
      for (Month month : Month.values()) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("Station", "Paya Lebar");
        queryParameters.put("Year", year);
        queryParameters.put("Month", String.valueOf(month));

        List<Integer> positionList = columnIndexManager.findByFieldNamesAndCategories(
            queryParameters);

        List<List<Integer>> minimumMaximumTemperatureList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Temperature", positionList);

        List<List<Integer>> minimumMaximumHumidityList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Humidity", positionList);

        System.out.println("Year " + year + " Month " + month);
        System.out.println("Min Temperature Index: " + minimumMaximumTemperatureList.get(0));
        System.out.println("Max Temperature Index: " + minimumMaximumTemperatureList.get(1));
        System.out.println("Min Humidity Index: " + minimumMaximumHumidityList.get(0));
        System.out.println("Max Humidity Index: " + minimumMaximumHumidityList.get(1));
        System.out.println();
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

  public static void populateColumnVectorsFromCsv(CsvFileManager csvFileManager,
      ColumnVectorManager columnVectorManager) {
    List<List<String>> csvRows = csvFileManager.getCsvRows();

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
      Double temperature = temperatureString.equals("M") ? null : Double.valueOf(temperatureString);
      columnVectorManager.addToDoubleColumnVector("Temperature", temperature);

      String humidityString = csvRow.get(4);
      Double humidity = humidityString.equals("M") ? null : Double.valueOf(humidityString);
      columnVectorManager.addToDoubleColumnVector("Humidity", humidity);
    }
  }

  private static void createCategoricalColumnIndexes(ColumnVectorManager columnVectorManager,
      ColumnIndexManager columnIndexManager) {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }

  public static List<String[]> getMinimumMaximumRows(ColumnVectorManager columnVectorManager,
      String fieldName, List<List<Integer>> minimumMaximumPositionList) {
    String station = "Paya Lebar";
    List<Integer> minimumPositionList = minimumMaximumPositionList.get(0);
    List<Integer> maximumPositionList = minimumMaximumPositionList.get(1);

    List<String[]> minimumMaximumRows = new ArrayList<>();

    for (Integer position : minimumPositionList) {
      String date = DateUtility.parseAndGetDay(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Min " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      minimumMaximumRows.add(new String[]{date, station, category, fieldValue});
    }

    for (Integer position : maximumPositionList) {
      String date = DateUtility.parseAndGetDay(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Max " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      minimumMaximumRows.add(new String[]{date, station, category, fieldValue});
    }

    return minimumMaximumRows;
  }
}
