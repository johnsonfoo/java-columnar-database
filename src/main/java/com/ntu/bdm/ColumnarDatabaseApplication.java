package com.ntu.bdm;

public class ColumnarDatabaseApplication {

  public static void main(String[] args) throws Exception {
    CsvFileManager csvFileManager = new CsvFileManager();
    csvFileManager.readAll("SingaporeWeather.csv");
    csvFileManager.print();
    csvFileManager.clear();
    csvFileManager.print();
  }
}
