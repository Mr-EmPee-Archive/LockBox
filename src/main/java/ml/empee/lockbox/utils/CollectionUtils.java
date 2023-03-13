package ml.empee.lockbox.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

  public static <T> boolean hasAllItems(Collection<T> c1, Collection<T> c2) {
    List<T> toCompare = new ArrayList<>(c2);

    boolean hasAllItems = true;
    for (T origItem : c1) {

      boolean hasItem = false;
      Iterator<T> iterator = toCompare.iterator();
      while (iterator.hasNext()) {
        if(Objects.equals(origItem, iterator.next())) {
          hasItem = true;
          iterator.remove();
          break;
        }
      }

      if(!hasItem) {
        hasAllItems = false;
        break;
      }
    }

    return hasAllItems;
  }

}
