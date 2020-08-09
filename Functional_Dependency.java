package myproject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Functional_Dependency extends Dependency<Functional_Dependency> {

	
	public Functional_Dependency(final String input, final Relation relation) {
		super(input, RDTUtils.functionalDependencyArrow, relation);
	}
	
	public Functional_Dependency(final List<Attribute> leftHandSide, final List<Attribute> rightHandSide, final Relation relation) {
		super(leftHandSide, rightHandSide, RDTUtils.functionalDependencyArrow, relation);
	}
	
	public Functional_Dependency(Relation relation,
			ArrayList<Attribute> leftHandAttributes,
			ArrayList<Attribute> rightHandAttributes, int normalForm) {
		super(relation ,	leftHandAttributes, rightHandAttributes, normalForm);
	}

	public String getFDName() {
		return getName();
	}

	public int compareTo(Functional_Dependency otherDependency) {
		if (this.getLeftHandAttributes().size() != otherDependency.getLeftHandAttributes().size()) {
			return this.getLeftHandAttributes().size() - otherDependency.getLeftHandAttributes().size();
		}
		return this.getFDName().compareTo(otherDependency.getFDName());
	}
	

}
