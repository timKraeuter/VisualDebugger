package no.hvl.tk.visual.debugger.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ODPrimitiveRootValueTest {

  @Test
  void testEqualsAndCompareTo() {
    final ODPrimitiveRootValue value1 = new ODPrimitiveRootValue("name", "type", "value");
    assertThat(value1, is(value1));
    assertThat(value1, is(not("123")));

    final ODPrimitiveRootValue valueWithDifferentName =
        new ODPrimitiveRootValue("name1", value1.getType(), value1.getValue());
    final ODPrimitiveRootValue valueWithDifferentType =
        new ODPrimitiveRootValue(value1.getVariableName(), "type1", value1.getValue());
    final ODPrimitiveRootValue valueWithDifferentValue =
        new ODPrimitiveRootValue(value1.getVariableName(), value1.getType(), "value1");

    assertThat(value1, is(not(valueWithDifferentName)));
    assertThat(value1, is(not(valueWithDifferentType)));
    assertThat(value1, is(not(valueWithDifferentValue)));

    // Test compareTo() by sorting.
    final Set<ODPrimitiveRootValue> values =
        Sets.newHashSet(
            value1, valueWithDifferentName, valueWithDifferentType, valueWithDifferentValue);
    final List<ODPrimitiveRootValue> sortedValues =
        values.stream().sorted().collect(Collectors.toList());

    assertThat(
        sortedValues,
        is(
            Lists.newArrayList(
                value1, valueWithDifferentType, valueWithDifferentValue, valueWithDifferentName)));
  }
}
