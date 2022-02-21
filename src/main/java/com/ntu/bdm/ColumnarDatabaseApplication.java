package com.ntu.bdm;

import com.ntu.bdm.util.CSVFileUtil;
import com.ntu.bdm.util.TimestampUtil;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

  public static final String INPUT_FILE_PATH = "SingaporeWeather.csv";
  public static final String EMPTY_DATA_SYMBOL = "M";
  public static Boolean DISK_STORAGE = false;
  public static String MATRICULATION_NUMBER;
  public static String STATION;
  public static String[] YEARS;
  public static final String OUTPUT_FILE_PATH = "ScanResult.csv";
  public static final String[] OUTPUT_FILE_HEADER = new String[]{"Date", "Station", "Category",
      "Value"};
  public static final String DISK_COLUMN_STORAGE_PATH = "disk/column/";

  public static void main(String[] args) {
    readCommandLineParameters(args);
    initialiseStationAndYears();

    if (!DISK_STORAGE) {
      mainMemoryStorage();
    }
  }

  public static void mainMemoryStorage() {
    ColumnVectorManager columnVectorManager = createColumnVectorsFromCsv();
    populateColumnVectorsFromCsv(columnVectorManager, CSVFileUtil.readDataAtOnce(INPUT_FILE_PATH));
    ColumnIndexManager columnIndexManager = new ColumnIndexManager();
    createCategoricalColumnIndexes(columnVectorManager, columnIndexManager);

    CSVFileUtil.writeHeader(OUTPUT_FILE_PATH, OUTPUT_FILE_HEADER);

    for (String year : YEARS) {
      for (Month month : Month.values()) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("Station", STATION);
        queryParameters.put("Year", year);
        queryParameters.put("Month", String.valueOf(month));

        List<Integer> positionList = columnIndexManager.findByFieldNamesAndCategories(
            queryParameters);

        List<List<Integer>> minimumMaximumTemperaturePositionList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Temperature", positionList);

        List<List<Integer>> minimumMaximumHumidityPositionList = columnVectorManager.getMinimumMaximumPositionListByFieldName(
            "Humidity", positionList);

        System.out.println("Year " + year + " Month " + month);
        System.out.println(
            "Min Temperature Index: " + minimumMaximumTemperaturePositionList.get(0));
        System.out.println(
            "Max Temperature Index: " + minimumMaximumTemperaturePositionList.get(1));
        System.out.println("Min Humidity Index: " + minimumMaximumHumidityPositionList.get(0));
        System.out.println("Max Humidity Index: " + minimumMaximumHumidityPositionList.get(1));
        System.out.println();

        CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
            getMinimumMaximumRowsWithDistinctDates(columnVectorManager, "Temperature",
                minimumMaximumTemperaturePositionList));
        CSVFileUtil.writeDataAtOnce(OUTPUT_FILE_PATH,
            getMinimumMaximumRowsWithDistinctDates(columnVectorManager, "Humidity",
                minimumMaximumHumidityPositionList));
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

  public static void populateColumnVectorsFromCsv(ColumnVectorManager columnVectorManager,
      List<String[]> csvRows) {
    for (String[] csvRow : csvRows) {
      String timestamp = csvRow[1];
      columnVectorManager.addToStringColumnVector("Timestamp", timestamp);

      String year = TimestampUtil.parseAndGetYear(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Year", year);

      String month = TimestampUtil.parseAndGetMonth(timestamp);
      columnVectorManager.addToCategoricalColumnVector("Month", month);

      String station = csvRow[2];
      columnVectorManager.addToCategoricalColumnVector("Station", station);

      String temperatureString = csvRow[3];
      Double temperature =
          temperatureString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(temperatureString);
      columnVectorManager.addToDoubleColumnVector("Temperature", temperature);

      String humidityString = csvRow[4];
      Double humidity =
          humidityString.equals(EMPTY_DATA_SYMBOL) ? null : Double.valueOf(humidityString);
      columnVectorManager.addToDoubleColumnVector("Humidity", humidity);
    }
  }

  public static void createCategoricalColumnIndexes(ColumnVectorManager columnVectorManager,
      ColumnIndexManager columnIndexManager) {
    columnIndexManager.constructCategoricalColumnIndexes(
        columnVectorManager.getCategoricalColumnVectors());
  }

  public static void outputColumnVectorsToCsv(ColumnVectorManager columnVectorManager) {
    String timestampFilePath = DISK_COLUMN_STORAGE_PATH + "Timestamp.csv";
    String[] timestampFileHeader = new String[]{"id", "Timestamp"};
    String temperatureFilePath = DISK_COLUMN_STORAGE_PATH + "Temperature.csv";
    String[] temperatureFileHeader = new String[]{"id", "Temperature"};
    String humidityFilePath = DISK_COLUMN_STORAGE_PATH + "Humidity.csv";
    String[] humidityFileHeader = new String[]{"id", "Humidity"};

    CSVFileUtil.writeHeader(timestampFilePath, timestampFileHeader);
    CSVFileUtil.writeDataAtOnce(timestampFilePath,
        columnVectorManager.serialiseStringColumnVectorByFieldName("Timestamp"));

    CSVFileUtil.writeHeader(temperatureFilePath, temperatureFileHeader);
    CSVFileUtil.writeDataAtOnce(temperatureFilePath,
        columnVectorManager.serialiseDoubleColumnVectorByFieldName("Temperature",
            EMPTY_DATA_SYMBOL));

    CSVFileUtil.writeHeader(humidityFilePath, humidityFileHeader);
    CSVFileUtil.writeDataAtOnce(humidityFilePath,
        columnVectorManager.serialiseDoubleColumnVectorByFieldName("Humidity",
            EMPTY_DATA_SYMBOL));
  }

  public static List<String[]> getMinimumMaximumRowsWithDistinctDates(
      ColumnVectorManager columnVectorManager,
      String fieldName, List<List<Integer>> minimumMaximumPositionList) {
    String station = STATION;
    List<Integer> minimumPositionList = minimumMaximumPositionList.get(0);
    List<Integer> maximumPositionList = minimumMaximumPositionList.get(1);

    List<String[]> minimumMaximumRows = new ArrayList<>();

    for (Integer position : minimumPositionList) {
      String date = TimestampUtil.parseAndGetDate(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Min " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      int currentSize = minimumMaximumRows.size();
      String[] newRow = {date, station, category, fieldValue};

      if (currentSize > 0 && Arrays.equals(newRow, minimumMaximumRows.get(currentSize - 1))) {
        continue;
      }

      minimumMaximumRows.add(newRow);
    }

    for (Integer position : maximumPositionList) {
      String date = TimestampUtil.parseAndGetDate(
          columnVectorManager.getStringByFieldNameAndPosition("Timestamp", position));
      String category = "Max " + fieldName;
      String fieldValue = String.valueOf(
          columnVectorManager.getDoubleByFieldNameAndPosition(fieldName, position));

      int currentSize = minimumMaximumRows.size();
      String[] newRow = {date, station, category, fieldValue};

      if (currentSize > 0 && Arrays.equals(newRow, minimumMaximumRows.get(currentSize - 1))) {
        continue;
      }

      minimumMaximumRows.add(newRow);
    }

    return minimumMaximumRows;
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

  public static void initialiseStationAndYears() {
    String matriculationNumber = MATRICULATION_NUMBER;
    int length = matriculationNumber.length();

    int secondLastDigit = Integer.parseInt(matriculationNumber.substring(length - 3, length - 2));
    int lastDigit = Integer.parseInt(matriculationNumber.substring(length - 2, length - 1));

    STATION = secondLastDigit % 2 == 0 ? "Changi" : "Paya Lebar";

    YEARS = IntStream.range(2002, 2022).filter(i -> i % 10 == lastDigit)
        .mapToObj(String::valueOf).toArray(String[]::new);
  }
}
