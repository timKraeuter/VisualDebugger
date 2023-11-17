package no.hvl.tk.visual.debugger.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class ODAttributeValueTest {

  @Test
  void testToString() {
    ODAttributeValue odAttributeValue = new ODAttributeValue("attributeName", "type", "value");
    assertThat(
        odAttributeValue.toString(),
        is("Attribute:[name='attributeName', type='type', value='value']"));
  }

  @Test
  void testEqualsAndHashCode() {
    ODAttributeValue odAttributeValue = new ODAttributeValue("attributeName", "type", "value");

    ODAttributeValue differentName = new ODAttributeValue("different", "type", "value");
    ODAttributeValue differentType = new ODAttributeValue("attributeName", "different", "value");
    ODAttributeValue differentValue = new ODAttributeValue("attributeName", "type", "different");

    assertThat(odAttributeValue, is(odAttributeValue));
    assertThat(odAttributeValue.hashCode(), is(odAttributeValue.hashCode()));

    assertThat(odAttributeValue, is(not("123")));
    assertThat(odAttributeValue, is(not(differentName)));
    assertThat(odAttributeValue, is(not(differentType)));
    assertThat(odAttributeValue, is(not(differentValue)));
    assertThat(odAttributeValue.hashCode(), is(not(differentName.hashCode())));
    assertThat(odAttributeValue.hashCode(), is(not(differentType.hashCode())));
    assertThat(odAttributeValue.hashCode(), is(not(differentValue.hashCode())));
  }
}
