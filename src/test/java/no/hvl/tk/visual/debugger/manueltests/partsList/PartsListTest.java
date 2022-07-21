package no.hvl.tk.visual.debugger.manueltests.partsList;

import no.hvl.tk.visual.debugger.manueltests.partsList.domain.Material;
import no.hvl.tk.visual.debugger.manueltests.partsList.domain.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartsListTest {

    @Test
    void overallCostForFoldingWallTableTest() {
        // Given
        final Product folding_wall_table = Product.create("Folding wall table", 5);
        folding_wall_table.addPart(Material.create("Main support", 10), 1);
        folding_wall_table.addPart(Material.create("Hinge", 5), 4);
        folding_wall_table.addPart(Material.create("Wood screw D3,5 x 20mm", 1), 26);
        folding_wall_table.addPart(Material.create("Wood screw D4 x 45mm", 1), 10);

        // When
        final int cost = folding_wall_table.getOverallCost();

        // Then
        assertEquals(71, cost);
    }
}
