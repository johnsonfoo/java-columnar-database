package com.ntu.bdm.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/********************************************************
 * FileUtil is utility class which contains just static
 * methods and cannot be instantiated. It provides
 * methods to read bytes from text files and write bytes
 * to text files.
 *
 ********************************************************/
public class FileUtil {

  private FileUtil() {
  }

  /**
   * Read bytes from text file.
   *
   * @param filePath the file path
   * @return the byte [ ]
   */
  public static byte[] readBytesFromFile(String filePath) {
    byte[] bytes = null;
    try {
      bytes = Files.readAllBytes(Path.of(filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
  }

  /**
   * Write bytes to text file.
   *
   * @param filePath the file path
   * @param bytes    the bytes
   */
  public static void writeBytesToFile(String filePath, byte[] bytes) {
    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
