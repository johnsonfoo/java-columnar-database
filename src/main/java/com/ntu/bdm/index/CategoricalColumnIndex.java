package com.ntu.bdm.index;

import com.ntu.bdm.vector.CategoricalColumnVector;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class CategoricalColumnIndex<T> {

  private Map<T, BitSet> bitmapIndex;

  public CategoricalColumnIndex() {
    bitmapIndex = new HashMap<>();
  }

  public void constructBitmapIndex(CategoricalColumnVector<T> categoricalColumnVector) {
    for (T categories : categoricalColumnVector.getCategories()) {
      bitmapIndex.put(categories, new BitSet());
    }

    for (int i = 0; i < categoricalColumnVector.getValueCount(); i++) {
      if (categoricalColumnVector.isNull(i)) {
        continue;
      }
      bitmapIndex.get(categoricalColumnVector.get(i)).set(i);
    }
  }

  public Map<T, BitSet> getBitmapIndex() {
    return bitmapIndex;
  }
}
