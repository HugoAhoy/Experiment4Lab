package FeElectric.FeElectric.Clean;

import java.util.List;

public class Relation {
	private static List<String> properties;
	private static String predicate;
	
	public Relation(List<String> prop, String pred) {
		setProperties(prop);
		setPredicate(pred);
	}
	
	
	public String getPredicate() {
		return predicate;
	}
	public static void setPredicate(String predicate) {
		Relation.predicate = predicate;
	}


	public List<String> getProperties() {
		return properties;
	}


	public static void setProperties(List<String> properties) {
		Relation.properties = properties;
	}
	
}
