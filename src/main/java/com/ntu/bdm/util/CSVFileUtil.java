package com.ntu.bdm.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/********************************************************
 * CSVFileUtil is utility class which contains just
 * static methods and cannot be instantiated. It provides
 * methods to read data from CSV files and write data to
 * CSV files.
 *
 ********************************************************/
public class CSVFileUtil {

  private CSVFileUtil() {
  }

  /**
   * Read data from CSV at once into list. Each row in CSV is an element in list.
   *
   * @param filePath the file path
   * @return the list
   */
  public static List<String[]> readDataAtOnce(String filePath) {
    // First create file object for file placed at location specified by filepath
    File file = new File(filePath);

    List<String[]> data = null;
    try {
      // Create FileWriter object with file as parameter
      FileReader inputFile = new FileReader(file);

      // Create csvReader object and skip first Line
      CSVReader reader = new CSVReaderBuilder(inputFile)
          .withSkipLines(1)
          .build();

      // Read data from csv
      data = reader.readAll();

      // Closing reader connection
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  /**
   * Write data line by line to CSV. The data will be a new row in CSV.
   *
   * @param filePath the file path
   * @param data     the data
   */
  public static void writeDataLineByLine(String filePath, String[] data) {
    // First create file object for file placed at location specified by filepath
    File file = new File(filePath);

    try {
      // Create FileWriter object with file as parameter
      FileWriter outputFile = new FileWriter(file, true);

      // Create CSVWriter with ',' as separator
      CSVWriter writer = new CSVWriter(outputFile, ',',
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);

      // Add data to csv
      writer.writeNext(data);

      // Closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write data from list at once to CSV. Each element in list will be a new row in CSV.
   *
   * @param filePath the file path
   * @param data     the data
   */
  public static void writeDataAtOnce(String filePath, List<String[]> data) {
    // First create file object for file placed at location specified by filepath
    File file = new File(filePath);

    try {
      // Create FileWriter object with file as parameter
      FileWriter outputFile = new FileWriter(file, true);

      // Create CSVWriter with ',' as separator
      CSVWriter writer = new CSVWriter(outputFile, ',',
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);

      // Add data to csv
      writer.writeAll(data);

      // Closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write header to CSV.
   *
   * @param filePath the file path
   * @param data     the data
   */
  public static void writeHeader(String filePath, String[] data) {
    // First create file object for file placed at location specified by filepath
    File file = new File(filePath);

    try {
      // Create FileWriter object with file as parameter
      FileWriter outputFile = new FileWriter(file);

      // Create CSVWriter with ',' as separator
      CSVWriter writer = new CSVWriter(outputFile, ',',
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);

      // Add data to csv
      writer.writeNext(data);

      // Closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
