package com.ntu.bdm;

import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

  public void writeDataLineByLine(String filePath, String[] data) {

    // first create file object for file placed at location
    // specified by filepath
    File file = new File(filePath);

    try {
      // create FileWriter object with file as parameter
      FileWriter outputFile = new FileWriter(file, true);

      // create CSVWriter with ',' as separator
      CSVWriter writer = new CSVWriter(outputFile, ',',
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);

      // add data to csv
      writer.writeNext(data);

      // closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void clear() {
    csvRows.clear();
  }
}
