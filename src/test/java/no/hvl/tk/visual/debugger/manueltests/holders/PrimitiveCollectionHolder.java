package no.hvl.tk.visual.debugger.manueltests.holders;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrimitiveCollectionHolder {
  private final int[] emptyArray = new int[0];
  private final int[] array = new int[] {1, 2, 3};
  private final List<Integer> emptyList = new ArrayList<>();
  private final List<Integer> list = Lists.newArrayList(1, 2, 3);
  private final Set<Integer> emptySet = new HashSet<>();
  private final Set<Integer> set = Sets.newHashSet(1, 2, 3);
}
