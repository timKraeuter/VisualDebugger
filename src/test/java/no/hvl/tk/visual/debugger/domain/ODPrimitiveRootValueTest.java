package no.hvl.tk.visual.debugger.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ODPrimitiveRootValueTest {

  @Test
  void testEqualsAndCompareTo() {
    final ODPrimitiveRootValue value1 = new ODPrimitiveRootValue("name", "type", "value");
    assertThat(value1, is(value1));
    assertThat(value1, is(not("123")));

    final ODPrimitiveRootValue valueWithDifferentName =
        new ODPrimitiveRootValue("name1", value1.type(), value1.value());
    final ODPrimitiveRootValue valueWithDifferentType =
        new ODPrimitiveRootValue(value1.variableName(), "type1", value1.value());
    final ODPrimitiveRootValue valueWithDifferentValue =
        new ODPrimitiveRootValue(value1.variableName(), value1.type(), "value1");

    assertThat(value1, is(not(valueWithDifferentName)));
    assertThat(value1, is(not(valueWithDifferentType)));
    assertThat(value1, is(not(valueWithDifferentValue)));

    // Test compareTo() by sorting.
    final Set<ODPrimitiveRootValue> values =
        Sets.newHashSet(
            value1, valueWithDifferentName, valueWithDifferentType, valueWithDifferentValue);
    final List<ODPrimitiveRootValue> sortedValues = values.stream().sorted().toList();

    assertThat(
        sortedValues,
        is(
            Lists.newArrayList(
                value1, valueWithDifferentType, valueWithDifferentValue, valueWithDifferentName)));
  }
}
