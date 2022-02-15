package com.ntu.bdm;

import com.ntu.bdm.util.DateUtility;
import java.time.Month;
import java.util.ArrayList;
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
        queryParameters.put("Location", "Paya Lebar");
        queryParameters.put("Year", year);
        queryParameters.put("Month", String.valueOf(month));

        List<Integer> positionList = columnIndexManager.findByFieldNamesAndCategories(
            queryParameters);

        List<Integer> maximumTemperatureList = getMaximumPositionList(positionList,
            columnVectorManager.getDoubleColumnVectors().get("Temperature").getDataVector());
        List<Integer> minimumTemperatureList = getMinimumPositionList(positionList,
            columnVectorManager.getDoubleColumnVectors().get("Temperature").getDataVector());

        List<Integer> maximumHumidityList = getMaximumPositionList(positionList,
            columnVectorManager.getDoubleColumnVectors().get("Humidity").getDataVector());
        List<Integer> minimumHumidityList = getMinimumPositionList(positionList,
            columnVectorManager.getDoubleColumnVectors().get("Humidity").getDataVector());
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

  public static List<Integer> getMinimumPositionList(List<Integer> positionList,
      List<Double> dataVector) {
    List<Integer> minimumPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return minimumPositionList;
    }

    Double minimum = dataVector.get(positionList.get(0));

    for (Integer position : positionList) {
      Double current = dataVector.get(position);
      if (current < minimum) {
        minimum = current;
        minimumPositionList.clear();
        minimumPositionList.add(position);
      } else if (current.equals(minimum)) {
        minimumPositionList.add(position);
      }
    }

    return minimumPositionList;
  }

  // TODO: Combine logic with getMinimumPositionList to return minimumPositionList and maximumPositionList
  public static List<Integer> getMaximumPositionList(List<Integer> positionList,
      List<Double> dataVector) {
    List<Integer> maximumPositionList = new ArrayList<>();

    if (positionList.size() == 0) {
      return maximumPositionList;
    }

    Double maximum = dataVector.get(positionList.get(0));

    for (Integer position : positionList) {
      Double current = dataVector.get(position);
      if (current > maximum) {
        maximum = current;
        maximumPositionList.clear();
        maximumPositionList.add(position);
      } else if (current.equals(maximum)) {
        maximumPositionList.add(position);
      }
    }

    return maximumPositionList;
  }
}
