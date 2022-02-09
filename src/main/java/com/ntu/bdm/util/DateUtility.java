package com.ntu.bdm.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtility {

  private DateUtility() {
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
}
