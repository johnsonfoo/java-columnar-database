package com.ntu.bdm;

import com.ntu.bdm.util.DateUtility;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnarDatabaseApplication {

  public static void main(String[] args) throws Exception {
    CsvFileManager csvFileManager = new CsvFileManager();
    csvFileManager.readAll("SingaporeWeather.csv");

    ColumnVectorManager columnVectorManager = createColumnVectorsFromCsv();
    populateColumnVectorsFromCsv(csvFileManager, columnVectorManager);
    ColumnIndexManager columnIndexManager = new ColumnIndexManager();
    createCategoricalColumnIndexes(columnVectorManager, columnIndexManager);

    for (String year : new String[]{"2003", "2013"}) {
      for (Month month : Month.values()) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("Station", "Paya Lebar");
        queryParameters.put("Year", year);
        queryParameters.put("Month", String.valueOf(month));

        List<Integer> positionList = columnIndexManager.findByFieldNamesAndCategories(
            queryParameters);

        List[] minimumMaximumTemperatureList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Temperature", positionList);

        List[] minimumMaximumHumidityList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Humidity", positionList);

        System.out.println("Year " + year + " Month " + month);
        System.out.println("Min Temperature Index: " + minimumMaximumTemperatureList[0]);
        System.out.println("Max Temperature Index: " + minimumMaximumTemperatureList[1]);
        System.out.println("Min Humidity Index: " + minimumMaximumHumidityList[0]);
        System.out.println("Max Humidity Index: " + minimumMaximumHumidityList[1]);
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

    for (int i = 1; i < csvRows.size(); i++) {
      String timestamp = csvRows.get(i).get(1);
      columnVectorManager.addToStringColumnVector("Timestamp", timestamp);

      String year = DateUtility.parseAndGetYear(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Year", year);

      String month = DateUtility.parseAndGetMonth(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Month", month);

      String station = csvRows.get(i).get(2);
      columnVectorManager.addToCategoricalColumnVector("Station", station);

      String temperatureString = csvRows.get(i).get(3);
      Double temperature = temperatureString.equals("M") ? null : Double.valueOf(temperatureString);
      columnVectorManager.addToDoubleColumnVector("Temperature", temperature);

      String humidityString = csvRows.get(i).get(4);
      Double humidity = humidityString.equals("M") ? null : Double.valueOf(humidityString);
      columnVectorManager.addToDoubleColumnVector("Humidity", humidity);
    }
  }

  private static void createCategoricalColumnIndexes(ColumnVectorManager columnVectorManager,
      ColumnIndexManager columnIndexManager) {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }
}
