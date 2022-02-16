package com.ntu.bdm.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVFileUtil {

  private CSVFileUtil() {
  }

  public static List<String[]> readDataAtOnce(String filePath) {

    // first create file object for file placed at location
    // specified by filepath
    File file = new File(filePath);

    List<String[]> data = null;
    try {
      // create FileWriter object with file as parameter
      FileReader inputFile = new FileReader(file);

      // create csvReader object and skip first Line
      CSVReader reader = new CSVReaderBuilder(inputFile)
          .withSkipLines(1)
          .build();

      // read data from csv
      data = reader.readAll();

      // closing reader connection
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  public static void writeDataLineByLine(String filePath, String[] data) {

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

  public static void writeDataAtOnce(String filePath, List<String[]> data) {

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
      writer.writeAll(data);

      // closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeHeader(String filePath, String[] data) {
    // first create file object for file placed at location
    // specified by filepath
    File file = new File(filePath);

    try {
      // create FileWriter object with file as parameter
      FileWriter outputFile = new FileWriter(file);

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
}
