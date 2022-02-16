package com.ntu.bdm.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimestampUtil {

  private TimestampUtil() {
  }

  public static final DateTimeFormatter formatTimeStamp = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm");

  public static String parseAndGetYear(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return String.valueOf(localDate.getYear());
  }

  public static String parseAndGetMonth(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return String.valueOf(localDate.getMonth());
  }

  public static String parseAndGetDay(String timestamp) {
    LocalDate localDate = LocalDate.parse(timestamp, formatTimeStamp);
    return localDate.toString();
  }
}
