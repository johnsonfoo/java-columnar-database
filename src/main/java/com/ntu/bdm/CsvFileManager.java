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

  // TODO: Remove this method after everything else is completed
  public void print() {
    if (csvRows.size() > 0) {
      List<String> header = csvRows.get(0);

      StringBuilder sb = new StringBuilder();

      for (String s : header) {
        sb.append(s);
        sb.append(": %s\n");
      }

      String toFormat = sb.toString();

      // Using 100 instead of csvRows.size() for debugging purposes only
      for (int i = 1; i <= 10; i++) {
        List<String> csvRow = csvRows.get(i);
        System.out.printf((toFormat) + "%n", csvRow.toArray());
      }
    } else {
      System.out.println("CSV file not found in memory");
    }
  }

  public void clear() {
    csvRows.clear();
  }
}
