package com.ntu.bdm;

import com.ntu.bdm.index.CategoricalColumnIndex;
import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
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

  public List<Integer> findByFieldNamesAndCategories(Map<String, String> queryParameters) {
    BitSet resultBitmap = null;

    for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
      String fieldName = entry.getKey();
      String category = entry.getValue();
      BitSet bitmap = findBitmapByFieldNameAndCategory(fieldName, category);

      // The following computes the bitwise AND between the bitmaps retrieved to obtain bitmap
      // representing rows satisfying all query parameters
      if (resultBitmap == null) {
        resultBitmap = (BitSet) bitmap.clone();
      } else {
        resultBitmap.and(bitmap);
      }
    }

    List<Integer> positionList = new ArrayList<>();

    // To iterate over the true bits in a BitSet, use the following loop
    for (int i = resultBitmap.nextSetBit(0); i >= 0; i = resultBitmap.nextSetBit(i + 1)) {
      positionList.add(i);
    }

    return positionList;
  }
}
