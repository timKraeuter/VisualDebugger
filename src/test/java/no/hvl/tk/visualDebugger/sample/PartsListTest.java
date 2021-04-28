package no.hvl.tk.visualDebugger.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PartsListTest {

	private PartsList pl;

	@BeforeEach
	public void setUp() throws Exception {
		this.pl = PartsList.create();

		this.pl.createMaterial("Nudel", 5);
		this.pl.createMaterial("Gemuese", 5);
		this.pl.createMaterial("Fleisch", 5);
		this.pl.createMaterial("Ei", 5);
		this.pl.createMaterial("Wasser", 0);
		this.pl.createMaterial("Soya", 1);

		this.pl.createProduct("Sosse", 5);
		this.pl.createProduct("Ramen", 1);

		this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Nudel"), 5);
		this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Sosse"), 2);
		this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Gemuese"), 3);
		this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Fleisch"), 4);
		this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Ei"), 1);

		this.pl.addPart(this.pl.getComponent("Sosse"), this.pl.getComponent("Wasser"), 3);
		this.pl.addPart(this.pl.getComponent("Sosse"), this.pl.getComponent("Soya"), 2);
		this.pl.addPart(this.pl.getComponent("Sosse"), this.pl.getComponent("Gemuese"), 1);

	}

	@Test
	public void testitest() {
		System.out.println(this.pl.getMaterialList(this.pl.getComponent("Ramen")));
	}

	@Test
	public void manual2() throws Exception {
		final Product test1 = this.pl.createProduct("test1", 1);
		final Material mat1 = this.pl.createMaterial("mat1", 1);
		final Material mat2 = this.pl.createMaterial("mat2", 4);

		this.pl.addPart(test1, mat1, 1);
		this.pl.addPart(test1, mat2, 3);

		assertEquals(14, this.pl.getOverallPrice(test1));
	}

	@Test
	public void observerTest() {
		assertEquals(90, this.pl.getOverallPrice(this.pl.getComponent("Ramen")));
		this.pl.changePrice(this.pl.getComponent("Soya"), 3);
		assertEquals(98, this.pl.getOverallPrice(this.pl.getComponent("Ramen")));
	}

	@Test
	public void testCycleException() {
		try {
			this.pl.addPart(this.pl.getComponent("Ramen"), this.pl.getComponent("Ramen"), 1);
			fail();
		} catch (final CycleException ignored) {
		}
	}

	@Test
	public void testCycleException_2() {
		try {
			final Product cycledings = this.pl.createProduct("cycledings", 2);
			final Component ramen = this.pl.getComponent("Ramen");
			this.pl.addPart(cycledings, ramen, 2);
			this.pl.addPart(ramen, cycledings, 1);

			fail();
		} catch (final CycleException ignored) {
		}
	}

	@Test
	public void testCycleException_3() {
		try {
			final Component ei = this.pl.getComponent("Ei");
			this.pl.addPart(ei, this.pl.getComponent("Gemuese"), 1);
			fail();
		} catch (final Exception ignored) {
		}
	}

}
