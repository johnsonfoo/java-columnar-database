package com.ntu.bdm.manager;

import com.ntu.bdm.index.CategoricalColumnIndex;
import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/********************************************************
 * ColumnIndexManager is a controller class that is
 * responsible for managing {@link CategoricalColumnIndex}.
 * It exposes public methods that clients use to interact
 * with the CategoricalColumnIndex class.
 *
 * It has a map that contain CategoricalColumnIndex
 * objects. The map uses a key fieldName to uniquely
 * identify an object.
 *
 ********************************************************/
public class ColumnIndexManager {

  private Map<String, CategoricalColumnIndex<String>> categoricalColumnIndexes;

  /**
   * Instantiates a new ColumnIndexManager.
   */
  public ColumnIndexManager() {
    categoricalColumnIndexes = new HashMap<>();
  }

  /**
   * Create a new CategoricalColumnIndex with the fieldName.
   *
   * @param fieldName               the field name
   * @param categoricalColumnVector the categorical column vector
   */
  public void constructCategoricalColumnIndex(String fieldName,
      CategoricalColumnVector<String> categoricalColumnVector) {
    categoricalColumnIndexes.put(fieldName, new CategoricalColumnIndex<>());
    categoricalColumnIndexes.get(fieldName).constructBitmapIndex(categoricalColumnVector);
  }

  /**
   * Create multiple new CategoricalColumnIndexes with each having different fieldNames.
   *
   * @param categoricalColumnVectors the categorical column vectors
   */
  public void constructCategoricalColumnIndexes(
      Map<String, CategoricalColumnVector<String>> categoricalColumnVectors) {
    for (Map.Entry<String, CategoricalColumnVector<String>> entry : categoricalColumnVectors.entrySet()) {
      String fieldName = entry.getKey();
      CategoricalColumnVector<String> categoricalColumnVector = entry.getValue();
      constructCategoricalColumnIndex(fieldName, categoricalColumnVector);
    }
  }

  /**
   * Gets the CategoricalColumnIndex map.
   *
   * @return the categorical column indexes
   */
  public Map<String, CategoricalColumnIndex<String>> getCategoricalColumnIndexes() {
    return categoricalColumnIndexes;
  }

  /**
   * Gets bitmap corresponding to category from CategoricalColumnIndex with the fieldName.
   *
   * @param fieldName the field name
   * @param category  the category
   * @return the bitmap for field with category
   */
  public BitSet getBitmapForFieldWithCategory(String fieldName, String category) {
    return categoricalColumnIndexes.get(fieldName).getBitmapIndex().get(category);
  }

  /**
   * Gets positionList which contains valid indexes. Valid indexes refer to indexes of rows that
   * satisfy all query parameters.
   *
   * @param queryParams the query params
   * @return the position list matching query params
   */
  public List<Integer> getPositionListMatchingQueryParams(Map<String, String> queryParams) {
    BitSet resultBitmap = null;
    List<Integer> positionList = new ArrayList<>();

    for (Map.Entry<String, String> entry : queryParams.entrySet()) {
      String fieldName = entry.getKey();
      String category = entry.getValue();
      BitSet bitmap = getBitmapForFieldWithCategory(fieldName, category);

      // Check if there exists a bitmap corresponding to category from CategoricalColumnIndex with
      // the fieldName
      if (bitmap == null) {
        return positionList;
      }

      // The following computes the bitwise AND between the bitmaps retrieved to obtain the bitmap
      // representing rows satisfying all query parameters
      if (resultBitmap == null) {
        resultBitmap = (BitSet) bitmap.clone();
      } else {
        resultBitmap.and(bitmap);
      }
    }

    // To iterate over the true bits in a bitmap, use the following loop
    for (int i = resultBitmap.nextSetBit(0); i >= 0; i = resultBitmap.nextSetBit(i + 1)) {
      positionList.add(i);
    }

    return positionList;
  }

  /**
   * Serialise CategoricalColumnIndex into bytes.
   *
   * @param fieldName the field name
   * @return the map
   */
  public Map<String, byte[]> serialiseCategoricalColumnIndex(String fieldName) {
    Map<String, byte[]> serialisedCategoricalColumnIndex = new HashMap<>();

    CategoricalColumnIndex<String> categoricalColumnIndex = categoricalColumnIndexes.get(fieldName);
    Map<String, BitSet> bitmapIndex = categoricalColumnIndex.getBitmapIndex();

    for (Map.Entry<String, BitSet> entry : bitmapIndex.entrySet()) {
      String category = entry.getKey();
      BitSet bitmap = entry.getValue();
      serialisedCategoricalColumnIndex.put(category, bitmap.toByteArray());
    }

    return serialisedCategoricalColumnIndex;
  }
}
