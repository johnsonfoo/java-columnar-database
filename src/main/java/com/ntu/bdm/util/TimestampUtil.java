package com.ntu.bdm.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/********************************************************
 * TimestampUtil is utility class which contains just
 * static methods and cannot be instantiated. It provides
 * methods to parse year, month and date strings from a
 * timestamp string having pattern "yyyy-MM-dd HH:mm".
 *
 ********************************************************/
public class TimestampUtil {

  private TimestampUtil() {
  }

  /**
   * The constant formatTimeStamp holds pattern for timestamp string.
   */
  public static final DateTimeFormatter formatTimeStamp = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm");

  /**
   * Parse and get year string.
   *
   * @param timestamp the timestamp
   * @return the string
   */
  public static String parseAndGetYear(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return String.valueOf(localDate.getYear());
  }

  /**
   * Parse and get month string.
   *
   * @param timestamp the timestamp
   * @return the string
   */
  public static String parseAndGetMonth(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return String.valueOf(localDate.getMonth());
  }

  /**
   * Parse and get date string.
   *
   * @param timestamp the timestamp
   * @return the string
   */
  public static String parseAndGetDate(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return localDate.toString();
  }
}
