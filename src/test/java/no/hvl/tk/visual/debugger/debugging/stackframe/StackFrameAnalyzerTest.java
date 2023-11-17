package no.hvl.tk.visual.debugger.debugging.stackframe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jdi.Value;
import java.util.*;
import java.util.stream.Collectors;
import no.hvl.tk.visual.debugger.debugging.DebuggingInfoCollector;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.*;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.IntegerValueMock;
import no.hvl.tk.visual.debugger.domain.*;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StackFrameAnalyzerTest {
  @Test
  void primitiveLocalVariablesTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));

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

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    final Set<ODPrimitiveRootValue> primitiveVars = new HashSet<>();

    final ODPrimitiveRootValue booleanValue =
        new ODPrimitiveRootValue(booleanVarName, "java.lang.Boolean", "true");
    primitiveVars.add(booleanValue);
    final ODPrimitiveRootValue byteValue =
        new ODPrimitiveRootValue(byteVarName, "java.lang.Byte", "0");
    primitiveVars.add(byteValue);
    final ODPrimitiveRootValue shortValue =
        new ODPrimitiveRootValue(shortVarName, "java.lang.Short", "1");
    primitiveVars.add(shortValue);
    final ODPrimitiveRootValue integerValue =
        new ODPrimitiveRootValue(integerVarName, IntegerValueMock.TYPE_NAME, "1");
    primitiveVars.add(integerValue);
    final ODPrimitiveRootValue longValue =
        new ODPrimitiveRootValue(longVarName, "java.lang.Long", "1");
    primitiveVars.add(longValue);
    final ODPrimitiveRootValue floatValue =
        new ODPrimitiveRootValue(floatVarName, "java.lang.Float", "4.1");
    primitiveVars.add(floatValue);
    final ODPrimitiveRootValue doubleValue =
        new ODPrimitiveRootValue(doubleVarName, "java.lang.Double", "5.1");
    primitiveVars.add(doubleValue);
    final ODPrimitiveRootValue stringValue =
        new ODPrimitiveRootValue(stringVarName, "java.lang.String", "\"" + stringVarValue + "\"");
    primitiveVars.add(stringValue);
    final ODPrimitiveRootValue charValue =
        new ODPrimitiveRootValue(charVarName, "java.lang.Char", "'a'");
    primitiveVars.add(charValue);

    assertThat(
        debuggingInfoCollector.getCurrentDiagram().getPrimitiveRootValues(),
        CoreMatchers.equalTo(primitiveVars));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  void objectWithAttributesTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));

    final String variableName = "foldingWallTable";
    final ObjectReferenceMock<Value> objRefMock =
        StackFrameMockHelper.createObject(stackFrameMock, "Product", variableName);
    StackFrameMockHelper.addAttributeToObject(
        objRefMock, "name", "String", new StringReferenceMock("folding wall table"));
    StackFrameMockHelper.addAttributeToObject(
        objRefMock, "price", IntegerValueMock.TYPE_NAME, new IntegerValueMock(25));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(2));
    final ODObject productObject =
        this.findObjectWithVarNameIfExists(
            debuggingInfoCollector.getCurrentDiagram(), variableName);

    assertThat(productObject.getAttributeValues().size(), is(2));
    assertThat(
        productObject.getAttributeByName("name").get(), // must be present for the test case
        is(new ODAttributeValue("name", "String", "\"folding wall table\"")));
    assertThat(
        productObject.getAttributeByName("price").get(), // must be present for the test case
        is(new ODAttributeValue("price", IntegerValueMock.TYPE_NAME, "25")));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  void multiLayerObjectTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));

    final String productName = "foldingWallTable";
    final ObjectReferenceMock<Value> product =
        StackFrameMockHelper.createObject(stackFrameMock, "Product", productName);
    final String childFieldName = "material";
    final ObjectReferenceMock<Value> material =
        StackFrameMockHelper.createChildObject(product, childFieldName, "Material");
    StackFrameMockHelper.addAttributeToObject(
        material, "name", "String", new StringReferenceMock("Main support"));
    StackFrameMockHelper.addAttributeToObject(
        material, "price", "Integer", new IntegerValueMock(10));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(3));
    final ODObject productObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), productName);

    assertThat(productObject.getLinks().size(), is(1));

    final ODObject materialObject =
        this.findObjectWithVarNameIfExists(
            debuggingInfoCollector.getCurrentDiagram(), childFieldName);
    assertThat(productObject.getLinks().stream().findFirst().get().getTo(), is(materialObject));

    assertThat(materialObject.getAttributeValues().size(), is(2));
    assertThat(
        materialObject.getAttributeByName("name").get(), // must be present for the test case
        is(new ODAttributeValue("name", "String", "\"Main support\"")));
    assertThat(
        materialObject.getAttributeByName("price").get(), // must be present for the test case
        is(new ODAttributeValue("price", "Integer", "10")));
  }

  @Test
  void emptyCollectionAndMapTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    final String intArray = "intArray";
    StackFrameMockHelper.createArray(stackFrameMock, intArray, Lists.newArrayList());
    final String intList = "intList";
    StackFrameMockHelper.createList(stackFrameMock, intList, Lists.newArrayList());
    final String intSet = "intSet";
    StackFrameMockHelper.createSet(stackFrameMock, intSet, Sets.newHashSet());
    final String map = "map";
    StackFrameMockHelper.createMap(stackFrameMock, map, Maps.newHashMap());

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(5));
    final ODObject intArrayObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intArray);
    assertThat(intArrayObject.getAttributeValues().size(), is(0));
    assertThat(intArrayObject.getLinks().size(), is(0));

    final ODObject intSetObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intSet);
    assertThat(intSetObject.getAttributeValues().size(), is(0));
    assertThat(intSetObject.getLinks().size(), is(0));

    final ODObject intListObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intList);
    assertThat(intListObject.getAttributeValues().size(), is(0));
    assertThat(intListObject.getLinks().size(), is(0));

    final ODObject mapObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), map);
    assertThat(mapObject.getAttributeValues().size(), is(0));
    assertThat(mapObject.getLinks().size(), is(0));
  }

  @Test
  void primitiveRootMapTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    final String mapVarName = "mapVarName";
    final Map<Value, Value> mapContent = Maps.newHashMap();
    mapContent.put(new IntegerValueMock(1), new IntegerValueMock(1));
    mapContent.put(new IntegerValueMock(2), new IntegerValueMock(2));
    mapContent.put(new IntegerValueMock(3), new IntegerValueMock(3));
    mapContent.put(new IntegerValueMock(4), new IntegerValueMock(4));
    StackFrameMockHelper.createMap(stackFrameMock, mapVarName, mapContent);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(6));
    final ODObject mapObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), mapVarName);
    assertThat(mapObject.getAttributeValues().size(), is(0));
    assertThat(mapObject.getLinks().size(), is(4));
    assertThat(
        mapObject.getLinks().stream().allMatch(odLink -> this.checkEntryAttributes(odLink.getTo())),
        is(true));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private boolean checkEntryAttributes(final ODObject entry) {
    Assertions.assertTrue(
        entry.getAttributeValues().stream()
            .anyMatch(odAttributeValue -> odAttributeValue.getName().equals("key")));
    Assertions.assertTrue(
        entry.getAttributeValues().stream()
            .anyMatch(odAttributeValue -> odAttributeValue.getName().equals("value")));

    final ODAttributeValue key =
        entry.getAttributeValues().stream()
            .filter(odAttributeValue -> odAttributeValue.getName().equals("key"))
            .findFirst()
            .get();
    final ODAttributeValue value =
        entry.getAttributeValues().stream()
            .filter(odAttributeValue -> odAttributeValue.getName().equals("value"))
            .findFirst()
            .get();
    return key.getValue().equals(value.getValue());
  }

  @Test
  void primitiveRootListTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    final String list = "list";
    StackFrameMockHelper.createList(
        stackFrameMock,
        list,
        Lists.newArrayList(
            new IntegerValueMock(1), new IntegerValueMock(2), new IntegerValueMock(3)));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(2));
    final ODObject listObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), list);
    this.checkIntArrayOrList(listObject);
  }

  @Test
  void primitiveRootSetTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    final String set = "set";
    StackFrameMockHelper.createSet(
        stackFrameMock,
        set,
        Sets.newHashSet(new IntegerValueMock(5), new IntegerValueMock(6), new IntegerValueMock(7)));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(2));
    final ODObject setObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), set);
    this.checkIntegerSet(setObject);
  }

  private void checkIntegerSet(final ODObject setObject) {
    assertThat(setObject.getAttributeValues().size(), is(3));
    assertThat(
        setObject.getAttributeValues().stream()
            .map(ODAttributeValue::getName)
            .collect(Collectors.toSet()),
        is(Sets.newHashSet("0", "1", "2")));
    assertThat(
        setObject.getAttributeValues().stream()
            .map(ODAttributeValue::getValue)
            .collect(Collectors.toSet()),
        is(Sets.newHashSet("5", "6", "7")));
  }

  @Test
  void primitiveRootArrayTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    final String intArray = "intArray";
    StackFrameMockHelper.createArray(
        stackFrameMock,
        intArray,
        Lists.newArrayList(
            new IntegerValueMock(1), new IntegerValueMock(2), new IntegerValueMock(3)));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(2));
    final ODObject intArrayObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intArray);
    this.checkIntArrayOrList(intArrayObject);
  }

  private void checkIntArrayOrList(final ODObject intArrayObject) {
    assertThat(intArrayObject.getAttributeValues().size(), is(3));
    assertThat(
        intArrayObject.getAttributeValues(),
        is(
            Lists.newArrayList(
                new ODAttributeValue("0", IntegerValueMock.TYPE_NAME, "1"),
                new ODAttributeValue("1", IntegerValueMock.TYPE_NAME, "2"),
                new ODAttributeValue("2", IntegerValueMock.TYPE_NAME, "3"))));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  void nonPrimitiveRootCollectionTest() {
    // Given
    final StackFrameMock stackFrameMock = new StackFrameMock(ObjectReferenceMock.create("test"));
    // List
    final String objList = "objList";
    StackFrameMockHelper.createList(
        stackFrameMock,
        objList,
        Lists.newArrayList(
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material")));
    // Set
    final String objSet = "objSet";
    final Set<Value> content =
        Sets.newHashSet(
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"));
    StackFrameMockHelper.createSet(stackFrameMock, objSet, content);
    // Array
    final String objArray = "objArray";
    StackFrameMockHelper.createArray(
        stackFrameMock,
        objArray,
        Lists.newArrayList(
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material")));
    // Map
    final String mapVarName = "mapVarName";
    final Map<Value, Value> mapContent = Maps.newHashMap();
    mapContent.put(new IntegerValueMock(1), ObjectReferenceMock.create("Material"));
    mapContent.put(new IntegerValueMock(2), ObjectReferenceMock.create("Material"));
    mapContent.put(new IntegerValueMock(3), ObjectReferenceMock.create("Material"));
    mapContent.put(new IntegerValueMock(4), ObjectReferenceMock.create("Material"));
    StackFrameMockHelper.createMap(stackFrameMock, mapVarName, mapContent);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(22));
    assertThat(debuggingInfoCollector.getCurrentDiagram().getLinks().size(), is(17));

    this.checkCollectionObjWithName(objList, debuggingInfoCollector);
    this.checkCollectionObjWithName(objSet, debuggingInfoCollector);
    this.checkCollectionObjWithName(objArray, debuggingInfoCollector);

    // Check map
    final ODObject mapObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), mapVarName);
    // 4 links to the 4 map entries
    assertThat(mapObject.getLinks().size(), is(4));
    List<ODObject> mapEntries = mapObject.getLinks().stream().map(ODLink::getTo).toList();
    // Check one entry
    ODObject firstEntry = mapEntries.get(0);
    assertThat(firstEntry.getAttributeValues().size(), is(1));
    // Key is an attribute because it is a primitive type (Long).
    ODAttributeValue keyAttribute = firstEntry.getAttributeValues().stream().findFirst().get();
    assertThat(keyAttribute.getName(), is("key"));
    Assertions.assertTrue(
        Lists.newArrayList(1L, 2L, 3L, 4L).contains(Long.parseLong(keyAttribute.getValue())));
    // Value must be an object
    assertThat(firstEntry.getLinks().size(), is(1));
    assertThat(firstEntry.getLinks().stream().findFirst().get().getTo().getType(), is("Material"));
  }

  private void checkCollectionObjWithName(
      final String collectionName, final DebuggingInfoCollector debuggingInfoCollector) {
    final ODObject objArrayObject =
        this.findObjectWithVarNameIfExists(
            debuggingInfoCollector.getCurrentDiagram(), collectionName);
    assertThat(objArrayObject.getLinks().size(), is(3));
    // All objects have the type "Material".
    assertThat(
        objArrayObject.getLinks().stream()
            .map(ODLink::getTo)
            .allMatch(object -> object.getType().equals("Material")),
        is(true));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  void primitiveSubCollectionTest() {
    // Given
    final ObjectReferenceMock<Value> thisObj = ObjectReferenceMock.create("ThisType");
    final StackFrameMock stackFrameMock = new StackFrameMock(thisObj);

    // Array
    final Value arrayRefMock =
        new ArrayReferenceMock(
            Lists.newArrayList(
                new IntegerValueMock(1), new IntegerValueMock(2), new IntegerValueMock(3)));
    final String intArray = "intArray";
    StackFrameMockHelper.addChildObject(thisObj, intArray, arrayRefMock);

    // Set
    final String setTypeName = "java.util.Set";
    final ObjectReferenceMock<IntegerValueMock> setObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            setTypeName,
            Sets.newHashSet(
                new IntegerValueMock(5), new IntegerValueMock(6), new IntegerValueMock(7)));
    final String intSet = "intSet";
    StackFrameMockHelper.addChildObject(thisObj, intSet, setObjectReferenceMock);
    // List
    final String listTypeName = "java.util.List";
    final ObjectReferenceMock<IntegerValueMock> listObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            listTypeName,
            Lists.newArrayList(
                new IntegerValueMock(1), new IntegerValueMock(2), new IntegerValueMock(3)));
    final String intList = "intList";
    StackFrameMockHelper.addChildObject(thisObj, intList, listObjectReferenceMock);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(4));
    assertThat(debuggingInfoCollector.getCurrentDiagram().getLinks().size(), is(3));
    final ODObject thisObject =
        debuggingInfoCollector.getCurrentDiagram().getObjects().stream().findFirst().get();

    assertThat(thisObject.getLinks().size(), is(3));

    final ODObject intArrayObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intArray);
    final ODObject intSetObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intSet);
    final ODObject intListObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), intList);
    assertThat(
        thisObject.getLinks().stream().map(ODLink::getTo).collect(Collectors.toSet()),
        is(Sets.newHashSet(intArrayObject, intSetObject, intListObject)));
    this.checkIntArrayOrList(intArrayObject);
    this.checkIntegerSet(intSetObject);
    this.checkIntArrayOrList(intListObject);
  }

  @Test
  void nonPrimitiveSubCollectionTest() {
    // Given
    final ObjectReferenceMock<Value> thisObj = ObjectReferenceMock.create("ThisType");
    final StackFrameMock stackFrameMock = new StackFrameMock(thisObj);

    // Set
    final String setTypeName = "java.util.Set";
    final ObjectReferenceMock<Value> setObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            setTypeName,
            Sets.newHashSet(
                ObjectReferenceMock.create("Material"),
                ObjectReferenceMock.create("Material"),
                ObjectReferenceMock.create("Material")));
    final String setMaterials = "setMaterials";
    StackFrameMockHelper.addChildObject(thisObj, setMaterials, setObjectReferenceMock);
    // List
    final String listTypeName = "java.util.List";
    final ObjectReferenceMock<Value> listObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            listTypeName,
            Lists.newArrayList(
                ObjectReferenceMock.create("Material"),
                ObjectReferenceMock.create("Material"),
                ObjectReferenceMock.create("Material")));
    final String listMaterials = "listMaterials";
    StackFrameMockHelper.addChildObject(thisObj, listMaterials, listObjectReferenceMock);
    // Array
    final List<Value> arrayContent =
        Lists.newArrayList(
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"),
            ObjectReferenceMock.create("Material"));
    final Value arrayRefMock = new ArrayReferenceMock(arrayContent);
    final String arrayMaterials = "arrayMaterials";
    StackFrameMockHelper.addChildObject(thisObj, arrayMaterials, arrayRefMock);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(10));
    assertThat(debuggingInfoCollector.getCurrentDiagram().getLinks().size(), is(9));
    final ODObject thisObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), "this");

    assertThat(thisObject.getLinks().size(), is(9));
    assertThat(
        thisObject.getLinks().stream()
            .allMatch(odLink -> odLink.getTo().getType().equals("Material")),
        is(true));
    assertThat(
        thisObject.getLinks().stream().map(ODLink::getType).collect(Collectors.toSet()),
        is(Sets.newHashSet(arrayMaterials, listMaterials, setMaterials)));
  }

  private ODObject findObjectWithVarNameIfExists(
      final ObjectDiagram diagram, final String variableName) {
    final Optional<ODObject> foundObject =
        diagram.getObjects().stream()
            .filter(object -> object.getVariableName().equals(variableName))
            .findFirst();
    return foundObject.orElse(null);
  }

  /** This situation lead to a bug. This test is to ensure this bug stays dead. */
  @Test
  void nullInCollectionAndArrayBugTest() {
    // Given
    final ObjectReferenceMock<Value> thisObj = ObjectReferenceMock.create("ThisType");
    final StackFrameMock stackFrameMock = new StackFrameMock(thisObj);

    // Set
    final String setTypeName = "java.util.Set";
    final ObjectReferenceMock<Value> setObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            setTypeName, Sets.newHashSet(null, ObjectReferenceMock.create("Material"), null));
    final String setMaterials = "setMaterials";
    StackFrameMockHelper.addChildObject(thisObj, setMaterials, setObjectReferenceMock);
    // List
    final String listTypeName = "java.util.List";
    final ObjectReferenceMock<Value> listObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            listTypeName, Lists.newArrayList(null, ObjectReferenceMock.create("Material"), null));
    final String listMaterials = "listMaterials";
    StackFrameMockHelper.addChildObject(thisObj, listMaterials, listObjectReferenceMock);
    // Array
    final List<Value> arrayContent =
        Lists.newArrayList(null, ObjectReferenceMock.create("Material"), null);
    final Value arrayRefMock = new ArrayReferenceMock(arrayContent);
    final String arrayMaterials = "arrayMaterials";
    StackFrameMockHelper.addChildObject(thisObj, arrayMaterials, arrayRefMock);
    // Map
    final String mapVarName = "mapVarName";
    final Map<Value, Value> mapContent = Maps.newHashMap();
    mapContent.put(null, null);
    mapContent.put(null, ObjectReferenceMock.create("Material"));
    mapContent.put(new IntegerValueMock(3), null);
    mapContent.put(new IntegerValueMock(4), ObjectReferenceMock.create("Material"));
    StackFrameMockHelper.createMap(stackFrameMock, mapVarName, mapContent);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();

    final StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector);

    // When
    stackFrameAnalyzer.analyze();

    // Then
    assertThat(debuggingInfoCollector.getCurrentDiagram().getObjects().size(), is(10));
    assertThat(debuggingInfoCollector.getCurrentDiagram().getLinks().size(), is(8));
    final ODObject thisObject =
        this.findObjectWithVarNameIfExists(debuggingInfoCollector.getCurrentDiagram(), "this");

    assertThat(thisObject.getLinks().size(), is(3));
    assertThat(
        thisObject.getLinks().stream()
            .allMatch(odLink -> odLink.getTo().getType().equals("Material")),
        is(true));
    assertThat(
        thisObject.getLinks().stream().map(ODLink::getType).collect(Collectors.toSet()),
        is(Sets.newHashSet(arrayMaterials, listMaterials, setMaterials)));
  }

  @Test
  void loadingDepthTest() {
    // Given
    final ObjectReferenceMock<Value> thisObj = ObjectReferenceMock.create("ThisType");
    final StackFrameMock stackFrameMock = new StackFrameMock(thisObj);

    final String childFieldName = "child";
    // Set
    final String setTypeName = "java.util.Set";
    final ObjectReferenceMock<Value> setObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            setTypeName, Sets.newHashSet(ObjectReferenceMock.create("Material")));
    final String setMaterials = "setMaterials";
    StackFrameMockHelper.addChildObject(thisObj, setMaterials, setObjectReferenceMock);
    // List
    final String listTypeName = "java.util.List";
    final ObjectReferenceMock<Value> listObjectReferenceMock =
        ObjectReferenceMock.createCollectionObjectRefMock(
            listTypeName, Lists.newArrayList(ObjectReferenceMock.create("Material")));
    final String listMaterials = "listMaterials";
    StackFrameMockHelper.addChildObject(thisObj, listMaterials, listObjectReferenceMock);
    // Array
    final List<Value> arrayContent = Lists.newArrayList(ObjectReferenceMock.create("Material"));
    final Value arrayRefMock = new ArrayReferenceMock(arrayContent);
    final String arrayMaterials = "arrayMaterials";
    StackFrameMockHelper.addChildObject(thisObj, arrayMaterials, arrayRefMock);
    // Field
    final ObjectReferenceMock<Value> product =
        StackFrameMockHelper.createObject(stackFrameMock, "Product", "foldingWallTable");
    final ObjectReferenceMock<Value> child =
        StackFrameMockHelper.createChildObject(product, childFieldName, "Product");
    StackFrameMockHelper.createChildObject(child, "childChild", "Product");

    final DebuggingInfoCollector debuggingInfoCollector1 = new DebuggingInfoCollector();
    // Check depth 0 (only two root objects)
    final StackFrameAnalyzer stackFrameAnalyzerDepth0 =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector1, 0);
    stackFrameAnalyzerDepth0.analyze();
    assertThat(debuggingInfoCollector1.getCurrentDiagram().getObjects().size(), is(2));
    assertThat(debuggingInfoCollector1.getCurrentDiagram().getLinks().size(), is(0));

    // Check depth 1 (only two root objects + 4 at depth1.)
    final DebuggingInfoCollector debuggingInfoCollector2 = new DebuggingInfoCollector();
    final StackFrameAnalyzer stackFrameAnalyzerDepth1 =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector2, 1);
    stackFrameAnalyzerDepth1.analyze();
    assertThat(debuggingInfoCollector2.getCurrentDiagram().getObjects().size(), is(6));
    assertThat(debuggingInfoCollector2.getCurrentDiagram().getLinks().size(), is(4));

    // Check depth 2 (only two root objects + 4 at depth1 + 1 at depth2)
    final DebuggingInfoCollector debuggingInfoCollector3 = new DebuggingInfoCollector();
    final StackFrameAnalyzer stackFrameAnalyzerDepth2 =
        new StackFrameAnalyzer(stackFrameMock, null, debuggingInfoCollector3, 2);
    stackFrameAnalyzerDepth2.analyze();
    assertThat(debuggingInfoCollector3.getCurrentDiagram().getObjects().size(), is(7));
    assertThat(debuggingInfoCollector3.getCurrentDiagram().getLinks().size(), is(5));
  }
}
