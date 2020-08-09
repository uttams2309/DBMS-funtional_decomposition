package myproject;

import java.util.List;

public class MultivaluedDependency extends Dependency<MultivaluedDependency> {

	public MultivaluedDependency(String input, Relation relation) {
		super(input, RDTUtils.multivaliedDependencyArrow, relation);
	}
	
	public MultivaluedDependency(final List<Attribute> leftHandSide, final List<Attribute> rightHandSide, final Relation relation) {
		super(leftHandSide, rightHandSide, RDTUtils.multivaliedDependencyArrow, relation);
	}

	
	public int compareTo(MultivaluedDependency otherDependency) {
		if (this.getLeftHandAttributes().size() != otherDependency.getLeftHandAttributes().size()) {
			return this.getLeftHandAttributes().size() - otherDependency.getLeftHandAttributes().size();
		}
		return this.getName().compareTo(otherDependency.getName());
	}

}
