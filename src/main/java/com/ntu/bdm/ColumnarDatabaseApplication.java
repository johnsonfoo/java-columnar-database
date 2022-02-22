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

public class ColumnarDatabaseApplication {

  public static Boolean DISK_STORAGE = false;
  public static String MATRICULATION_NUMBER;
  public static String STATION;
  public static String[] YEARS;
  public static String[] MONTHS;
  public static final String INPUT_FILE_PATH = "SingaporeWeather.csv";
  public static final String OUTPUT_FILE_PATH = "ScanResult.csv";
  public static final String[] OUTPUT_FILE_HEADER = new String[]{"Date", "Station", "Category",
      "Value"};

  public static void main(String[] args) {
    readCommandLineParameters(args);
    initialiseStationAndYearsAndMonths();

    CSVFileUtil.writeHeader(OUTPUT_FILE_PATH, OUTPUT_FILE_HEADER);

    if (!DISK_STORAGE) {
      MainMemoryDatabase mainMemoryDatabase = new MainMemoryDatabase();
      mainMemoryDatabase.initialiseColumnVectors();
      mainMemoryDatabase.populateColumnVectors(CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
      mainMemoryDatabase.createCategoricalColumnIndexes();

      for (String year : YEARS) {
        for (String month : MONTHS) {
          Map<String, String> queryParams = new HashMap<>();
          queryParams.put("Station", STATION);
          queryParams.put("Year", year);
          queryParams.put("Month", month);

          CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
              mainMemoryDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams(
                  "Temperature", queryParams));

          CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
              mainMemoryDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams(
                  "Humidity", queryParams));
        }
      }
    } else {
      DiskDatabase diskDatabase = new DiskDatabase();
      diskDatabase.initialiseColumnVectors();
      diskDatabase.populateColumnVectors(CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
      diskDatabase.createCategoricalColumnIndexes();
      diskDatabase.writeColumnVectorsToDisk();
      diskDatabase.writeCategoricalColumnIndexesToDisk();
      diskDatabase.clearColumnVectorManagerContents();
      diskDatabase.clearColumnIndexManagerContents();

      for (String year : YEARS) {
        for (String month : MONTHS) {
          Map<String, String> queryParams = new HashMap<>();
          queryParams.put("Station", STATION);
          queryParams.put("Year", year);
          queryParams.put("Month", month);

          CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
              diskDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams("Temperature",
                  queryParams));

          CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
              diskDatabase.getMinMaxRowsWithDistinctDateForFieldMatchingQueryParams("Humidity",
                  queryParams));
        }
      }
    }
  }

  public static void readCommandLineParameters(String[] args) {
    // define options
    Options options = new Options();

    options.addOption("d", "disk", false, "Uses disk storage");

    Option config = Option.builder("m")
        .longOpt("matric")
        .hasArg()
        .required(true)
        .desc("Sets the matriculation number").build();
    options.addOption(config);

    // define parser
    CommandLine cmd;
    CommandLineParser parser = new DefaultParser();
    HelpFormatter helper = new HelpFormatter();

    try {
      cmd = parser.parse(options, args);
      if (cmd.hasOption("d")) {
        System.out.println("Disk storage set to true");
        DISK_STORAGE = true;
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
