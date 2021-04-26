package sample;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MaterialList {
	Map<Material, Integer> data;
	
	private MaterialList() {
		this.data = new LinkedHashMap<>();
	}
	
	public static MaterialList create() {
		return new MaterialList();
	}
	
	public void add(final Material material, final Integer i) {
		final Integer amount = this.data.containsKey(material) ? this.data.get(material) + i : i;
		this.data.put(material, amount);
	}
	
	public void add(final MaterialList materialList) {
		for (final Material current : materialList.data.keySet()) {
			this.add(current, materialList.data.get(current));
		}
	}
	
	public Vector<QuantifiedComponent> toLoeweList() {
		final Set<Material> keySet = this.data.keySet();
		final Vector<QuantifiedComponent> ret = new Vector<>();
		for (final Material current : keySet) {
			ret.add(QuantifiedComponent.createQuantifiedComponent(this.data.get(current), current));
		}
		return ret;
	}
}
