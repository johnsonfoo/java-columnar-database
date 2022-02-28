package com.ntu.bdm.index;

import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/********************************************************
 * CategoricalColumnIndex is a container class which
 * holds bitmaps representing different categories in a
 * {@link CategoricalColumnVector}.
 *
 * It has a map that contain bitmap objects. The map
 * uses a key category to uniquely identify a bitmap.
 *
 ********************************************************/
public class CategoricalColumnIndex<T> {

  private Map<T, BitSet> bitmapIndex;

  /**
   * Instantiates a new CategoricalColumnIndex.
   */
  public CategoricalColumnIndex() {
    bitmapIndex = new HashMap<>();
  }

  /**
   * Create multiple new bitmaps with each representing different categories.
   *
   * @param categoricalColumnVector the categorical column vector
   */
  public void constructBitmapIndex(CategoricalColumnVector<T> categoricalColumnVector) {
    // Add empty bitmaps for each category into the map first.
    for (T categories : categoricalColumnVector.getCategories()) {
      bitmapIndex.put(categories, new BitSet());
    }

    // Iterate over the elements in categoricalColumnVector and flip bit representing index of
    // element in bitmap to true. The bitmap modified is the one that corresponds to category of
    // element.
    for (int i = 0; i < categoricalColumnVector.getValueCount(); i++) {
      if (categoricalColumnVector.isNull(i)) {
        continue;
      }
      bitmapIndex.get(categoricalColumnVector.get(i)).set(i);
    }
  }

  /**
   * Gets the bitmap map.
   *
   * @return the bitmap index
   */
  public Map<T, BitSet> getBitmapIndex() {
    return bitmapIndex;
  }
}
