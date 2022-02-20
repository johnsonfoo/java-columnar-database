package com.ntu.bdm.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

  private FileUtil() {
  }

  public static byte[] readBytesFromFile(String filePath) {
    byte[] bytes = null;
    try {
      bytes = Files.readAllBytes(Path.of(filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
  }

  public static void writeBytesToFile(String filePath, byte[] bytes) {
    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
