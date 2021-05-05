package no.hvl.tk.visualDebugger.partsList;

import java.util.*;

public class MaterialList {
	Map<String, QuantifiedComponent> materials;
	
	private MaterialList() {
		this.materials = new HashMap<>();
	}
	
	public static MaterialList create() {
		return new MaterialList();
	}
	
	public void add(final QuantifiedComponent material) {
		final QuantifiedComponent quantifiedComponent = this.materials.get(material.getComponent().getName());
		if (quantifiedComponent != null) {
			quantifiedComponent.addQuantity(quantifiedComponent.getQuantity());
		} else {
			this.materials.put(
					material.getComponent().getName(),
					QuantifiedComponent.create(material.getQuantity(), material.getComponent()));
		}
	}
	
	public void add(final MaterialList materialList) {
		materialList.getMaterials().forEach(this::add);
	}

	public Collection<QuantifiedComponent> getMaterials() {
		return materials.values();
	}
}
