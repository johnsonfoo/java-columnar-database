package com.ntu.bdm;

import com.ntu.bdm.util.CSVFileUtil;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/********************************************************
 * DatabaseApplication is the main class. It will start
 * either the MainMemoryDatabase or DiskDatabase depending
 * on the user's input.
 *
 * It will query for two years and one location from the
 * database. The last digit of the required years equals
 * to the last digit of the provided matriculation number.
 * The location depends on the second last digit of the
 * provided matriculation number, with even number for
 * Changi and odd number for Paya Lebar.
 *
 * The database will output the query results to an output
 * CSV file.
 *
 ********************************************************/
public class DatabaseApplication {

  private static Boolean DISK_STORAGE = false;
  private static String MATRICULATION_NUMBER;
  private static String STATION;
  private static String[] YEARS;
  private static String[] MONTHS;
  private static final String INPUT_FILE_PATH = "SingaporeWeather.csv";
  private static final String MAIN_MEMORY_DATABASE_OUTPUT_FILE_PATH = "ScanResultMainMemory.csv";
  private static final String DISK_DATABASE_OUTPUT_FILE_PATH = "ScanResultDisk.csv";
  private static final String[] OUTPUT_FILE_HEADER = new String[]{"Date", "Station", "Category",
      "Value"};

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    readCommandLineParameters(args);
    initialiseStationAndYearsAndMonths();

    if (!DISK_STORAGE) {
      System.out.println("Using main memory storage for application");
      MainMemoryDatabase mainMemoryDatabase = new MainMemoryDatabase();
      mainMemoryDatabase.initialiseColumnVectors();
      mainMemoryDatabase.populateColumnVectors(CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
      mainMemoryDatabase.createCategoricalColumnIndexes();

      CSVFileUtil.writeHeader(MAIN_MEMORY_DATABASE_OUTPUT_FILE_PATH, OUTPUT_FILE_HEADER);

      for (String year : YEARS) {
        System.out.println(
            "Started scanning Station " + STATION + " for Year " + year);

        for (String month : MONTHS) {
          Map<String, String> queryParams = new HashMap<>();
          queryParams.put("Station", STATION);
          queryParams.put("Year", year);
          queryParams.put("Month", month);

          /*
           * Write minimum maximum temperature result rows to output CSV file. The result rows
           * satisfy the station, year and month conditions inside queryParams.
           */
          CSVFileUtil.writeDataAtOnce(MAIN_MEMORY_DATABASE_OUTPUT_FILE_PATH,
              mainMemoryDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams(
                  "Temperature", queryParams));

          /*
           * Write minimum maximum humidity result rows to output CSV file. The result rows
           * satisfy the station, year and month conditions inside queryParams.
           */
          CSVFileUtil.writeDataAtOnce(MAIN_MEMORY_DATABASE_OUTPUT_FILE_PATH,
              mainMemoryDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams(
                  "Humidity", queryParams));
        }
        System.out.println("Finished");
      }
    } else {
      System.out.println("Using disk storage for application");
      DiskDatabase diskDatabase = new DiskDatabase();
      diskDatabase.initialiseColumnVectors();
      diskDatabase.populateColumnVectors(CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
      diskDatabase.createCategoricalColumnIndexes();
      diskDatabase.writeColumnVectorsToDisk();
      diskDatabase.writeCategoricalColumnIndexesToDisk();
      diskDatabase.closeColumnVectorManager();
      diskDatabase.closeColumnIndexManager();

      CSVFileUtil.writeHeader(DISK_DATABASE_OUTPUT_FILE_PATH, OUTPUT_FILE_HEADER);

      for (String year : YEARS) {
        System.out.println(
            "Started scanning Station " + STATION + " for Year " + year);

        for (String month : MONTHS) {
          Map<String, String> queryParams = new HashMap<>();
          queryParams.put("Station", STATION);
          queryParams.put("Year", year);
          queryParams.put("Month", month);

          /*
           * Write minimum maximum temperature result rows to output CSV file. The result rows
           * satisfy the station, year and month conditions inside queryParams.
           */
          CSVFileUtil.writeDataAtOnce(DISK_DATABASE_OUTPUT_FILE_PATH,
              diskDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams("Temperature",
                  queryParams));

          /*
           * Write minimum maximum humidity result rows to output CSV file. The result rows
           * satisfy the station, year and month conditions inside queryParams.
           */
          CSVFileUtil.writeDataAtOnce(DISK_DATABASE_OUTPUT_FILE_PATH,
              diskDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams("Humidity",
                  queryParams));
        }
        System.out.println("Finished");
      }
    }
    System.out.println("Scanning completed and scan results successfully written to disk");
  }

  /**
   * Read command line parameters.
   *
   * @param args the args
   */
  public static void readCommandLineParameters(String[] args) {
    // Define options
    Options options = new Options();

    // Option for using DiskDatabase
    options.addOption("d", "disk", false, "Uses disk storage");

    // Option for matriculation number
    Option config = Option.builder("m")
        .longOpt("matric")
        .hasArg()
        .required(true)
        .desc("Sets the matriculation number").build();
    options.addOption(config);

    // Define parser
    CommandLine cmd;
    CommandLineParser parser = new DefaultParser();
    HelpFormatter helper = new HelpFormatter();

    try {
      cmd = parser.parse(options, args);
      if (cmd.hasOption("d")) {
        System.out.println("Disk storage set to true");
        DISK_STORAGE = true;
      } else {
        System.out.println("Main memory storage set to true");
      }
      if (cmd.hasOption("m")) {
        String opt_config = cmd.getOptionValue("m");
        System.out.println("Matriculation number set to " + opt_config);
        MATRICULATION_NUMBER = opt_config;
      }
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      helper.printHelp("Usage:", options);
      System.exit(0);
    }
  }

  /**
   * Initialise STATION, YEARS and MONTHS constants.
   */
  public static void initialiseStationAndYearsAndMonths() {
    String matriculationNumber = MATRICULATION_NUMBER;
    int length = matriculationNumber.length();

    int secondLastDigit = Integer.parseInt(matriculationNumber.substring(length - 3, length - 2));
    int lastDigit = Integer.parseInt(matriculationNumber.substring(length - 2, length - 1));

    STATION = secondLastDigit % 2 == 0 ? "Changi" : "Paya Lebar";

    YEARS = IntStream.range(2002, 2022).filter(i -> i % 10 == lastDigit)
        .mapToObj(String::valueOf).toArray(String[]::new);

    MONTHS = Arrays.stream(Month.values()).map(String::valueOf).toArray(String[]::new);
  }
}
