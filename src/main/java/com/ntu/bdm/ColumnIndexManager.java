package com.ntu.bdm;

import com.ntu.bdm.index.CategoricalColumnIndex;
import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.HashMap;

public class ColumnIndexManager {

  private HashMap<String, CategoricalColumnIndex<String>> categoricalColumnIndexes;

  public ColumnIndexManager() {
    categoricalColumnIndexes = new HashMap<>();
  }

  public void constructCategoricalColumnIndex(String fieldName,
      CategoricalColumnVector<String> categoricalColumnVector) {
    categoricalColumnIndexes.put(fieldName, new CategoricalColumnIndex<String>());
    categoricalColumnIndexes.get(fieldName).constructBitmapIndex(categoricalColumnVector);
  }

  public HashMap<String, CategoricalColumnIndex<String>> getCategoricalColumnIndexes() {
    return categoricalColumnIndexes;
  }
}
