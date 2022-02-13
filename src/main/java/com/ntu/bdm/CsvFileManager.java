package com.ntu.bdm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFileManager {

  final List<List<String>> csvRows;

  public CsvFileManager() {
    this.csvRows = new ArrayList<>();
  }

  public List<List<String>> getCsvRows() {
    return csvRows;
  }

  public void readAll(String filePath) throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String csvRow;
      while ((csvRow = br.readLine()) != null) {
        String[] values = csvRow.split(",");
        csvRows.add(Arrays.asList(values));
      }
    }
    System.out.println("CSV file read to memory");
  }

  public void clear() {
    csvRows.clear();
  }
}
