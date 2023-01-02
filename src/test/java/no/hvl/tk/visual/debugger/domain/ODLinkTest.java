package no.hvl.tk.visual.debugger.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

class ODLinkTest {

    @Test
    void testToString() {
        ODObject odObject1 = new ODObject(1, "String", "person");
        ODObject odObject2 = new ODObject(2, "String", "address");
        ODLink odLink = new ODLink(odObject1, odObject2, "address");
        assertThat(odLink.toString(), is("Link[type='address', from=person, to=address]"));
    }

    @Test
    void testEqualsAndHashCode() {
        ODObject odObject1 = new ODObject(1, "String", "person");
        ODObject odObject2 = new ODObject(2, "String", "address");
        ODLink odLink = new ODLink(odObject1, odObject2, "address");

        ODLink differentObjects = new ODLink(odObject2, odObject1, "address");
        ODLink differentType = new ODLink(odObject1, odObject2, "type");

        assertThat(odLink, is(odLink));
        assertThat(odLink.hashCode(), is(odLink.hashCode()));

        assertThat(odLink, is(not(differentObjects)));
        assertThat(odLink, is(not("123")));
        assertThat(odLink, is(not(differentType)));
        assertThat(odLink.hashCode(), is(not(differentObjects.hashCode())));
        assertThat(odLink.hashCode(), is(not(differentType.hashCode())));

    }
}