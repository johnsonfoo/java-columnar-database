package com.ntu.bdm;

import com.ntu.bdm.index.CategoricalColumnIndex;
import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ColumnIndexManager {

  private Map<String, CategoricalColumnIndex<String>> categoricalColumnIndexes;

  public ColumnIndexManager() {
    categoricalColumnIndexes = new HashMap<>();
  }

  public void constructCategoricalColumnIndex(String fieldName,
      CategoricalColumnVector<String> categoricalColumnVector) {
    categoricalColumnIndexes.put(fieldName, new CategoricalColumnIndex<String>());
    categoricalColumnIndexes.get(fieldName).constructBitmapIndex(categoricalColumnVector);
  }

  public void constructCategoricalColumnIndexes(
      Map<String, CategoricalColumnVector<String>> categoricalColumnVectors) {
    for (Map.Entry<String, CategoricalColumnVector<String>> entry : categoricalColumnVectors.entrySet()) {
      String fieldName = entry.getKey();
      CategoricalColumnVector<String> categoricalColumnVector = entry.getValue();
      constructCategoricalColumnIndex(fieldName, categoricalColumnVector);
    }
  }

  public Map<String, CategoricalColumnIndex<String>> getCategoricalColumnIndexes() {
    return categoricalColumnIndexes;
  }

  public BitSet findBitmapByFieldNameAndCategory(String fieldName, String category) {
    return categoricalColumnIndexes.get(fieldName).getBitmapIndex().get(category);
  }
}
