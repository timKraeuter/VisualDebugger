package no.hvl.tk.visual.debugger.partsList;

import no.hvl.tk.visual.debugger.partsList.domain.Material;
import no.hvl.tk.visual.debugger.partsList.domain.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartsListTest {

    @Test
    void overallCostForFoldingWallTableTest() {
        final Product folding_wall_table = Product.create("Folding wall table", 5);
        folding_wall_table.addPart(Material.create("Main support", 10), 1);
        folding_wall_table.addPart(Material.create("Hinge", 5), 4);
        folding_wall_table.addPart(Material.create("Wood screw D3,5 x 20mm", 1), 26);
        folding_wall_table.addPart(Material.create("Wood screw D4 x 45mm", 1), 10);

        assertEquals(71, folding_wall_table.getOverallCost());

        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
        System.out.println("123");
    }
}
