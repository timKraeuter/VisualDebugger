package no.hvl.tk.visual.debugger.manueltests;

import com.google.common.collect.Lists;
import no.hvl.tk.visual.debugger.manueltests.holders.CollectionHolder;
import no.hvl.tk.visual.debugger.manueltests.holders.PrimitiveArrayHolder;
import no.hvl.tk.visual.debugger.manueltests.partsList.domain.Material;
import no.hvl.tk.visual.debugger.manueltests.partsList.domain.Product;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * Manuel testing suite with different debugging scenarios.
 */
class DebuggingScenariosTest {

    /**
     * Equivalent Unit-Test using a mocked stack frame in primitiveLocalVariablesTest().
     */
    @Test
    void testPrimitiveVariables() {
        final byte aByte1 = 0;
        final Byte aByte2 = 0;
        final short aShort1 = 1;
        final Short aShort2 = 2;
        final int anInt1 = 2;
        final Integer anInt2 = 2;
        final long aLong1 = 3L;
        final Long aLong2 = 3L;
        final float aFloat1 = 4.1F;
        final Float aFloat2 = 4.1F;
        final double aDouble1 = 5.1;
        final Double aDouble2 = 5.1;
        final char aChar = '6';
        final Character aCharacter = '2';
        final boolean aBoolean1 = true;
        final Boolean aBoolean2 = true;
        final String aString = "8";
        final String nullString = null;
        final String[] stringArray = {"1", "2", "3"};
        final int[] intArray = {1, 2, 3};
        System.out.println("Put your breakpoint here");
    }

    /**
     * Equivalent Unit-Test using a mocked stack frame in objectWithAttributesTest().
     */
    @Test
    void testWithOneLayerObjectVariables() {
        final Material aMaterial = Material.create("stringValue", 42);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
    }

    /**
     * Equivalent Unit-Test using a mocked stack frame in multiLayerObjectTest().
     */
    @Test
    void testMultiLayerObjectVariables() {
        final String productName = "productName";
        final Product product = Product.create(productName, 3);
        product.addPart(Material.create("mat1Name", 1), 1);
        product.addPart(Material.create("mat2Name", 2), 1);
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
    }

    /**
     * Equivalent Unit-Test using a mocked stack frame in primitiveSubArrayTest().
     */
    @Test
    void primitiveArrayAsFieldTest() {
        final PrimitiveArrayHolder primitiveArrayHolder = new PrimitiveArrayHolder(new int[]{1, 2, 3});
        System.out.println(primitiveArrayHolder);
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
    }

    @Test
    void primitiveCollectionTest() {
        // Each list value ends up as an attribute at the moment.
        final CollectionHolder<String> stringHolder = new CollectionHolder<>(Lists.newArrayList("1", "2", "3"));
        final CollectionHolder<Integer> intHolder = new CollectionHolder<>(Lists.newArrayList(1, 2, 3));
        System.out.println(stringHolder);
        System.out.println(stringHolder);
        System.out.println(stringHolder);
        System.out.println(intHolder);
        System.out.println(intHolder);
    }

    @Test
    void nonPrimitiveCollectionTest() {
        // Each list value ends up as an attribute at the moment.
        final CollectionHolder<Material> materialHolder = new CollectionHolder<>(Lists.newArrayList(
                Material.create("1", 10),
                Material.create("2", 20),
                Material.create("3", 30)));
        System.out.println(materialHolder);
        System.out.println(materialHolder);
        System.out.println(materialHolder);
    }

    @Test
    void primitiveCollectionAtRootTest() {
        // Lists work even with objects
        final List<String> stringList = Lists.newArrayList("1", "2", "3");
        final List<Integer> intList = Lists.newArrayList(1, 2, 3);
        System.out.println(stringList);
        System.out.println(stringList);
        System.out.println(stringList);
        System.out.println(intList);
        System.out.println(intList);
        System.out.println(intList);
    }

    @Test
    void nonPrimitiveCollectionAtRootTest() {
        // Lists work even with objects
        final List<Material> materialsList = Lists.newArrayList(
                Material.create("1", 10),
                Material.create("2", 20),
                Material.create("3", 30));
        System.out.println(materialsList);
        System.out.println(materialsList);
        System.out.println(materialsList);
        System.out.println(materialsList);
        // Lists work even with objects
        final Set<Material> materialsSet = Sets.newHashSet(
                Material.create("1", 10),
                Material.create("2", 20),
                Material.create("3", 30));
        System.out.println(materialsSet);
        System.out.println(materialsSet);
        System.out.println(materialsSet);
        System.out.println(materialsSet);
    }

    @Test
    void primitiveMapVisualisationTest() {
        final Map<String, Integer> postalCodes = new HashMap<>();
        postalCodes.put("Oslo", 1295);
        postalCodes.put("Bergen", 5052);
        postalCodes.put("Berlin", 13585);
        postalCodes.put("Hanover", 30161);
        final Map<Double, Boolean> doubleBooleanMap = new HashMap<>();
        doubleBooleanMap.put(1.1, true);
        doubleBooleanMap.put(1.2, false);
        doubleBooleanMap.put(1.3, true);
        System.out.println(postalCodes);
        System.out.println(doubleBooleanMap);
    }

    @Test
    void nonPrimitiveMapVisualisationTest() {
        final Map<Material, Integer> inventory = new HashMap<>();
        inventory.put(Material.create("Main support", 10), 3);
        inventory.put(Material.create("Hinge", 5), 5);
        inventory.put(Material.create("Wood screw D3,5 x 20mm", 1), 8);
        inventory.put(Material.create("Wood screw D4 x 45mm", 1), 13);
        System.out.println(inventory);
        System.out.println(inventory);
        System.out.println(inventory);
    }

    @Test
    void duplicateObjectsTest() {
        final Material mat1 = Material.create("123", 2);
        final List<Material> materials = Lists.newArrayList(mat1, mat1);
        System.out.println(materials);
        System.out.println(materials);
        System.out.println(materials);
        System.out.println(materials);
        System.out.println(materials);
        System.out.println(materials);
        System.out.println(materials);
    }


    @Test
    void testScrolling() {
        final Product prod1 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod2 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod3 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod4 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod5 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod6 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod7 = DebuggingScenariosTest.makeProductWith2Mats();
        final Product prod8 = DebuggingScenariosTest.makeProductWith2Mats();
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
    }

    @NotNull
    private static Product makeProductWith2Mats() {
        final Material mat1 = Material.create("123", 2);
        final Material mat2 = Material.create("123", 2);
        final Product product = Product.create("123", 1);
        product.addPart(mat1, 1);
        product.addPart(mat2, 2);
        return product;
    }

    private static class Cycle {
        public Cycle next;
        public String name;

        public Cycle() {
            this.name = "some name";
        }

        public Cycle(final String name) {
            this.name = name;
        }
    }


    @Test
    void testObjectCycle() {
        final Cycle c1 = new Cycle();
        final Cycle c2 = new Cycle();
        c1.next = c2;
        c2.next = c1;

        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
    }


    @Test
    void testTransitiveObjectCycle() {
        final Cycle c1 = new Cycle();
        final Cycle c2 = new Cycle();
        final Cycle c3 = new Cycle();

        c1.next = c2;
        c2.next = c3;
        c3.next = c1;

        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
    }


    @Test
    void testVisualisationDepth() {
        final Cycle root = new Cycle();
        Cycle parent = root;
        for (int i = 1; i <= 100; i++) {
            final Cycle child = new Cycle(String.valueOf(i));
            parent.next = child;
            parent = child;
        }
        System.out.println("Buh");
        System.out.println("Buh");
        System.out.println("Buh");
        System.out.println("Buh");
    }

    @Test
    void testManyObjects() {
        final Set<Product> products = new HashSet<>();
        final int amountOfProducts = 100; // each product has two materials.
        // So x*3 objects and x*3 links + x links and one object for the root collection.
        for (int i = 0; i <= amountOfProducts; i++) {
            final Product prod = DebuggingScenariosTest.makeProductWith2Mats();
            products.add(prod);
        }
        System.out.println(products.size());
    }
}
