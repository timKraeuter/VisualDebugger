package no.hvl.tk.visual.debugger.debugging.stackframe;

import no.hvl.tk.visual.debugger.debugging.DebuggingInfoCollector;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.ObjectReferenceMock;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.StackFrameMock;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.StackFrameMockHelper;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.StringReferenceMock;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.IntegerValueMock;
import no.hvl.tk.visual.debugger.domain.ODAttributeValue;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StackFrameAnalyzerTest {
    @Test
    void primitiveLocalVariablesTest() {
        // Given
        final StackFrameMock stackFrameMock = new StackFrameMock(new ObjectReferenceMock("test"));

        // Boolean variable
        final String booleanVarName = "aBoolean";
        StackFrameMockHelper.addLocalBooleanVariable(stackFrameMock, booleanVarName, true);
        // Byte value
        final String byteVarName = "aByte";
        StackFrameMockHelper.addLocalByteVariable(stackFrameMock, byteVarName, (byte) 0);
        // Short value
        final String shortVarName = "aShort";
        StackFrameMockHelper.addLocalShortVariable(stackFrameMock, shortVarName, (short) 1);
        // Int value
        final String integerVarName = "aInt";
        StackFrameMockHelper.addLocalIntegerVariable(stackFrameMock, integerVarName, 1);
        // Long value
        final String longVarName = "aLong";
        StackFrameMockHelper.addLocalLongVariable(stackFrameMock, longVarName, 1L);
        // Float value
        final String floatVarName = "aFloat";
        StackFrameMockHelper.addLocalFloatVariable(stackFrameMock, floatVarName, 4.1F);
        // Double value
        final String doubleVarName = "aDouble";
        StackFrameMockHelper.addLocalDoubleVariable(stackFrameMock, doubleVarName, 5.1);
        // String variable
        final String stringVarName = "productName";
        final String stringVarValue = "folding wall table";
        StackFrameMockHelper.addLocalStringVariable(stackFrameMock, stringVarName, stringVarValue);
        // Char variable
        final String charVarName = "aChar";
        StackFrameMockHelper.addLocalCharVariable(stackFrameMock, charVarName, 'a');

        final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

        final StackFrameAnalyzer stackFrameAnalyzer = new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

        // When
        stackFrameAnalyzer.analyze();

        // Then
        final Set<ODPrimitiveRootValue> primitiveVars = new HashSet<>();

        final ODPrimitiveRootValue booleanValue = new ODPrimitiveRootValue(booleanVarName, "java.lang.Boolean", "true");
        primitiveVars.add(booleanValue);
        final ODPrimitiveRootValue byteValue = new ODPrimitiveRootValue(byteVarName, "java.lang.Byte", "0");
        primitiveVars.add(byteValue);
        final ODPrimitiveRootValue shortValue = new ODPrimitiveRootValue(shortVarName, "java.lang.Short", "1");
        primitiveVars.add(shortValue);
        final ODPrimitiveRootValue integerValue = new ODPrimitiveRootValue(integerVarName, "java.lang.Integer", "1");
        primitiveVars.add(integerValue);
        final ODPrimitiveRootValue longValue = new ODPrimitiveRootValue(longVarName, "java.lang.Long", "1");
        primitiveVars.add(longValue);
        final ODPrimitiveRootValue floatValue = new ODPrimitiveRootValue(floatVarName, "java.lang.Float", "4.1");
        primitiveVars.add(floatValue);
        final ODPrimitiveRootValue doubleValue = new ODPrimitiveRootValue(doubleVarName, "java.lang.Double", "5.1");
        primitiveVars.add(doubleValue);
        final ODPrimitiveRootValue stringValue = new ODPrimitiveRootValue(stringVarName, "java.lang.String", "\"" + stringVarValue + "\"");
        primitiveVars.add(stringValue);
        final ODPrimitiveRootValue charValue = new ODPrimitiveRootValue(charVarName, "java.lang.Char", "'a'");
        primitiveVars.add(charValue);

        assertThat(debuggingInfoCollector.getCurrentDiagram().getPrimitiveRootValues(), CoreMatchers.equalTo(primitiveVars));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void objectWithAttributesTest() {
        // Given
        final StackFrameMock stackFrameMock = new StackFrameMock(new ObjectReferenceMock("test"));

        final String variableName = "foldingWallTable";
        final ObjectReferenceMock objRefMock = StackFrameMockHelper.createObject(stackFrameMock, "Product", variableName);
        StackFrameMockHelper.addAttributeToObject(objRefMock, "name", "String", new StringReferenceMock("folding wall table"));
        StackFrameMockHelper.addAttributeToObject(objRefMock, "price", "Integer", new IntegerValueMock(25));

        final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

        final StackFrameAnalyzer stackFrameAnalyzer = new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

        // When
        stackFrameAnalyzer.analyze();

        // Then
        assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(2));
        final ODObject productObject = this.findObjectWithVarNameIfExists(
                debuggingInfoCollector.getCurrentDiagram(),
                variableName);

        assertThat(productObject.getAttributeValues().size(), is(2));
        assertThat(productObject.getAttributeByName("name").get(), // must be present for the test case
                is(new ODAttributeValue("name", "String", "\"folding wall table\"")));
        assertThat(productObject.getAttributeByName("price").get(), // must be present for the test case
                is(new ODAttributeValue("price", "Integer", "25")));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void multiLayerObjectTest() {
        // Given
        final StackFrameMock stackFrameMock = new StackFrameMock(new ObjectReferenceMock("test"));

        final String productName = "foldingWallTable";
        final ObjectReferenceMock product = StackFrameMockHelper.createObject(stackFrameMock, "Product", productName);
        final String childFieldName = "material";
        final ObjectReferenceMock material = StackFrameMockHelper.createChildObject(stackFrameMock, product, childFieldName, "Material");
        StackFrameMockHelper.addAttributeToObject(material, "name", "String", new StringReferenceMock("Main support"));
        StackFrameMockHelper.addAttributeToObject(material, "price", "Integer", new IntegerValueMock(10));

        final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

        final StackFrameAnalyzer stackFrameAnalyzer = new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

        // When
        stackFrameAnalyzer.analyze();

        // Then
        assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(3));
        final ODObject productObject = this.findObjectWithVarNameIfExists(
                debuggingInfoCollector.getCurrentDiagram(),
                productName);

        assertThat(productObject.getLinks().size(), is(1));

        final ODObject materialObject = this.findObjectWithVarNameIfExists(
                debuggingInfoCollector.getCurrentDiagram(),
                childFieldName);
        assertThat(productObject.getLinks().stream().findFirst().get().getTo(), is(materialObject));

        assertThat(materialObject.getAttributeValues().size(), is(2));
        assertThat(materialObject.getAttributeByName("name").get(), // must be present for the test case
                is(new ODAttributeValue("name", "String", "\"Main support\"")));
        assertThat(materialObject.getAttributeByName("price").get(), // must be present for the test case
                is(new ODAttributeValue("price", "Integer", "10")));
    }

    private ODObject findObjectWithVarNameIfExists(final ObjectDiagram diagram, final String variableName) {
        final Optional<ODObject> foundObject = diagram.getObjects()
                                                      .stream()
                                                      .filter(object -> object.getVariableName().equals(variableName))
                                                      .findFirst();
        return foundObject.orElse(null);
    }
}
