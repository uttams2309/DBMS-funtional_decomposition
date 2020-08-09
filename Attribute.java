package myproject;
public class Attribute implements Comparable<Attribute>{
	private final String name;
	public Attribute(final String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(Attribute otherAttribute) {
		if (this.name.length() != otherAttribute.getName().length()) {
			return this.name.length() - otherAttribute.getName().length();
		}
		return this.getName().compareTo(otherAttribute.getName());
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Attribute)) {
			return false;
		}
		Attribute otherAttribute = (Attribute) o;
		return this.name.compareTo(otherAttribute.name) == 0;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
