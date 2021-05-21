package no.hvl.tk.visualDebugger.debugging;

import com.google.common.collect.Lists;
import no.hvl.tk.visualDebugger.partsList.Material;
import no.hvl.tk.visualDebugger.partsList.Product;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DebuggingScenariosTest {
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
    void mapTest() {
        final Map<String, String> someMap = new HashMap<>();
        someMap.put("key1", "value1");
        someMap.put("key2", "value2");
        someMap.put("key3", "value3");
        System.out.println(someMap);
        System.out.println(someMap);
        System.out.println(someMap);
        System.out.println(someMap);
        System.out.println(someMap);
        System.out.println(someMap);
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
}
