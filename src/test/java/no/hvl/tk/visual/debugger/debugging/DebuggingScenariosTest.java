package no.hvl.tk.visual.debugger.debugging;

import com.google.common.collect.Lists;
import no.hvl.tk.visual.debugger.partsList.domain.Material;
import no.hvl.tk.visual.debugger.partsList.domain.Product;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DebuggingScenariosTest {
    @Test
    void testPrimitiveVariables() {
        byte aByte1 = 0;
        Byte aByte2 = 0;
        short aShort1 = 1;
        Short aShort2 = 2;
        int anInt1 = 2;
        Integer anInt2 = 2;
        long aLong1 = 3L;
        Long aLong2 = 3L;
        float aFloat1 = 4.1F;
        Float aFloat2 = 4.1F;
        double aDouble1 = 5.1;
        Double aDouble2 = 5.1;
        char aChar = '6';
        Character aCharacter = '2';
        boolean aBoolean1 = true;
        Boolean aBoolean2 = true;
        String aString = "8";
        System.out.println("Put your breakpoint here");
    }

    @Test
    void testWithOneLayerObjectVariables() {
        Material aMaterial = Material.create("stringValue", 42);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
    }

    @Test
    void testMultiLayerObjectVariables() {
        String productName = "productName";
        final Product product = Product.create(productName, 3);
        product.addPart(Material.create("mat1Name", 1), 1);
        product.addPart(Material.create("mat2Name", 2), 1);
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
    }

    @Test
    void primitiveCollectionTest() {
        // Each list value ends up as an attribute at the moment.
        CollectionHolder<String> stringHolder = new CollectionHolder<>(Lists.newArrayList("1", "2", "3"));
        CollectionHolder<Integer> intHolder = new CollectionHolder<>(Lists.newArrayList(1, 2, 3));
        System.out.println(stringHolder);
        System.out.println(stringHolder);
        System.out.println(stringHolder);
        System.out.println(intHolder);
        System.out.println(intHolder);
    }

    @Test
    void nonPrimitiveCollectionTest() {
        // Each list value ends up as an attribute at the moment.
        CollectionHolder<Material> materialHolder = new CollectionHolder<>(Lists.newArrayList(
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
        List<String> stringList = Lists.newArrayList("1", "2", "3");
        List<Integer> intList = Lists.newArrayList(1, 2, 3);
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
        List<Material> materialsList = Lists.newArrayList(
                Material.create("1", 10),
                Material.create("2", 20),
                Material.create("3", 30));
        System.out.println(materialsList);
        System.out.println(materialsList);
        System.out.println(materialsList);
        System.out.println(materialsList);
        // Lists work even with objects
        Set<Material> materialsSet = Sets.newHashSet(
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
        final Map<String, Integer> postcalCodes = new HashMap<>();
        postcalCodes.put("Oslo", 1295);
        postcalCodes.put("Bergen", 5052);
        postcalCodes.put("Berlin", 13585);
        postcalCodes.put("Hanover", 30161);
        final Map<Double, Boolean> doubleBooleanMap = new HashMap<>();
        doubleBooleanMap.put(1.1, true);
        doubleBooleanMap.put(1.2, false);
        doubleBooleanMap.put(1.3, true);
        System.out.println(postcalCodes);
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
        final Product prod1 = makeProductWith2Mats();
        final Product prod2 = makeProductWith2Mats();
        final Product prod3 = makeProductWith2Mats();
        final Product prod4 = makeProductWith2Mats();
        final Product prod5 = makeProductWith2Mats();
        final Product prod6 = makeProductWith2Mats();
        final Product prod7 = makeProductWith2Mats();
        final Product prod8 = makeProductWith2Mats();
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
        System.out.println(prod8);
    }

    @NotNull
    private Product makeProductWith2Mats() {
        final Material mat1 = Material.create("123", 2);
        final Material mat2 = Material.create("123", 2);
        Product product = Product.create("123", 1);
        product.addPart(mat1, 1);
        product.addPart(mat2, 2);
        return product;
    }


    @Test
    void testObjectCycle() {
        // Does not crash but will probably call 10 times until the depth is reached.
        class Cycle {
            public Cycle next;
        }

        Cycle c1 = new Cycle();
        Cycle c2 = new Cycle();
        c1.next = c2;
        c2.next = c1;

        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
        System.out.println(c1);
    }
}
