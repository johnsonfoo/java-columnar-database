package com.ntu.bdm;

public class ColumnarDatabaseApplication {

  public static void main(String[] args) throws Exception {
    CsvFileManager csvFileManager = new CsvFileManager();
    csvFileManager.readAll("SingaporeWeather.csv");
    csvFileManager.print();
    csvFileManager.clear();
    csvFileManager.print();

    ColumnVectorManager columnVectorManager = createColumnVectorsFromCsv();
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
}
