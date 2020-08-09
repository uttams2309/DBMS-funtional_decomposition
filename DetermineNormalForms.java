package myproject;

import java.util.ArrayList;
import java.util.List;

public class DetermineNormalForms {
	private final Relation relation;
	public boolean hasDeterminedNormalForms;
	public boolean isFirstNormalForm;
	private String firstNormalFormMsg;
	public boolean isSecondNormalForm;
	private String secondNormalFormMsg;
    public boolean isThirdNormalForm;
	private String thirdNormalFormMsg;
	public boolean isBCNF;
	private String BCNFMsg;
	private List<Functional_Dependency> bcnfViolatingFDs;

	public DetermineNormalForms(final Relation relation) {
		this.relation = relation;
		hasDeterminedNormalForms = false;
		bcnfViolatingFDs = null;
	}

	public void calculateNormalForms() {
		calculateFirstNormalForm();
		calculateSecondNormalForm();
		calculateThirdNormalForm();
		calculateBCNF();
		hasDeterminedNormalForms = true;
	}

	private void calculateFirstNormalForm() {
		isFirstNormalForm = true;
		firstNormalFormMsg = "Input relation is assumed to be in 1NF: each attribute is assumed to contain only one value per row.";
	}

	private void calculateSecondNormalForm() {
		if (!isFirstNormalForm) {
			isSecondNormalForm = false;
			secondNormalFormMsg = "Input relation is not in 2NF because it is not in 1NF.";
			return;
		}
		// Check if all minimum keys are single-attributes
		boolean singleAttribute = true;
		for (Closure c : relation.getMinimumKeyClosures()) {
			if (c.getClosureOf().size() > 1) {
				singleAttribute = false;
				break;
			}
		}
		if (singleAttribute) {
			isSecondNormalForm = true;
			secondNormalFormMsg = "Input relation is in 2NF: "
					+ "It is in 1NF and there are no composite minimum keys (minimum keys composed of more than one attribute).";
			return;
		}
		// Check if there is at least one non-prime attribute that does not
		// depend on all minimum key attributes

		List<Attribute> failedAttrs = new ArrayList<>();
		List<Closure> failedClosures = new ArrayList<>();
		List<Closure> failedProperClosure = new ArrayList<>();

		List<Attribute> primeAttributes = relation.getPrimeAttributes();
		for (Closure minClosure : relation.getMinimumKeyClosures()) {
			if (minClosure.getClosureOf().size() > 1) {
				List<Attribute> nonPrimes = new ArrayList<>();
				for (Attribute ab : minClosure.getClosure()) {
					if (!RDTUtils.attributeListContainsAttribute(primeAttributes, ab)) {
						nonPrimes.add(ab);
					}
				}
				for (Attribute ac : nonPrimes) {
					for (Closure c : relation.getClosures()) {
						if (c.getClosureOf().size() >= minClosure.getClosureOf().size()) {
							break;
						}
						if (RDTUtils.isClosureProperSubsetOfOtherClosure(minClosure, c)) {
							if (RDTUtils.attributeListContainsAttribute(c.getClosure(), ac)
									&& !RDTUtils.attributeListContainsAttribute(c.getClosureOf(), ac)) {
								if (!RDTUtils.attributeListContainsAttribute(failedAttrs, ac)) {
									failedAttrs.add(ac);
									failedClosures.add(c);
									failedProperClosure.add(minClosure);
								}
								break;
							}
						}
					}
				}
			}
		}

		if (failedAttrs.isEmpty()) {
			isSecondNormalForm = true;
			secondNormalFormMsg = "Input relation is in 2NF: "
					+ "It is in 1NF and there are no partial dependencies on a composite minimum key "
					+ "(a minimum key composed of more than one attribute).";
		} else {
			isSecondNormalForm = false;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < failedAttrs.size(); i++) {
				sb.append("The minimum set of attributes that attribute " + failedAttrs.get(i).getName()
						+ " is functionally determined by is attribute(s) {");
				for (int j = 0; j < failedClosures.get(i).getClosureOf().size(); j++) {
					sb.append(failedClosures.get(i).getClosureOf().get(j).getName());
					if (j < failedClosures.get(i).getClosureOf().size() - 1) {
						sb.append(", ");
					}
				}
				sb.append("}; whereas it should only be functionally determined by the full set of attributes of the composite minimum key {");
				for (int k = 0; k < failedProperClosure.get(i).getClosureOf().size(); k++) {
					sb.append(failedProperClosure.get(i).getClosureOf().get(k).getName());
					if (k < failedProperClosure.get(i).getClosureOf().size() - 1) {
						sb.append(", ");
					}
				}
				sb.append("}. ");
				String attribute;
				if (failedAttrs.size() == 1) {
					attribute = "attribute violates";
				} else {
					attribute = "attributes violate";
				}
				secondNormalFormMsg = "Input relation is not in 2NF: There is at least one partial dependency on a composite minimum key. "
						+ "To satisfy 2NF, there should not be any non-prime attribute "
						+ " that can be functionally determined by a proper subset of a composite minimum key. "
						+ "See above closure list for the composite minimum key(s). "
						+ "The following non-prime "
						+ attribute + " the condition: " + sb.toString();
			}
		}
	}

	/**
	 * 
	 * @param functionalDependency
	 * @return True if input functional dependency is trivial: all of its
	 *         right-hand side attributes are also in its left-hand side.
	 */
	private boolean isTrivialFD(final Functional_Dependency f) {
		for (Attribute rightAttr : f.getRightHandAttributes()) {
			if (!RDTUtils.attributeListContainsAttribute(f.getLeftHandAttributes(), rightAttr)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param dependency
	 * @return True if input dependency A->B of relation R, A is a
	 *         superkey or key of R.
	 */
	@SuppressWarnings("rawtypes")
	private boolean isAKeyOrSuperKey(final Dependency dependency) {
		List<Closure> allKeys = new ArrayList<>();
		allKeys.addAll(relation.getSuperKeyClosures());
		allKeys.addAll(relation.getMinimumKeyClosures());
		for (Closure c : allKeys) {
			if (c.getClosureOf().size() == dependency.getLeftHandAttributes().size()) {
				boolean isFullMatch = true;
				List rawAttrs = dependency.getLeftHandAttributes();
				List<Attribute> castAttrs = new ArrayList<>();
				for (Object o : rawAttrs) {
					Attribute a = (Attribute) o;
					castAttrs.add(a);
				}
				for (Attribute a : castAttrs) {
					if (!RDTUtils.attributeListContainsAttribute(c.getClosureOf(), a)) {
						isFullMatch = false;
						break;
					}
				}
				if (isFullMatch) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isTrivialMultivaluedDependency(final MultivaluedDependency m) {
		// First check if all attributes are found in the MVD (regardless of side). If so, then the MVD is trivial.
		List<Attribute> mvdAttrs = new ArrayList<>();
		for (Attribute a : m.getLeftHandAttributes()) {
			if (!RDTUtils.attributeListContainsAttribute(mvdAttrs, a)) {
				mvdAttrs.add(a);
			}
		}
		for (Attribute a : m.getRightHandAttributes()) {
			if (!RDTUtils.attributeListContainsAttribute(mvdAttrs, a)) {
				mvdAttrs.add(a);
			}
		}
		if (mvdAttrs.size() == relation.getAttributes().size()) {
			return true;
		}
		// Next check if all attributes on right side are also found in left side
		for (Attribute rightAttr : m.getRightHandAttributes()) {
			if (!RDTUtils.attributeListContainsAttribute(m.getLeftHandAttributes(), rightAttr)) {
				return false;
			}
		}
		return true;
	}

	private void calculateThirdNormalForm() {
		List<Functional_Dependency> failedFDs = new ArrayList<>();
		// For each FD A ->B
		for (Functional_Dependency f : RDTUtils.getSingleAttributeMinimalCoverList(relation.getFDs(), relation)) {
			// Check if B is a subset of A (A->B is trivial)
			if (isTrivialFD(f)) {
				continue;
			}

			// Next check if A is a super key or key of the relation R
			if (isAKeyOrSuperKey(f)) {
				continue;
			} 

			// Check if B is or is part of some candidate key of the relation R
			boolean isPartofKey = false;
			for (Closure c : relation.getMinimumKeyClosures()) {
				for (Attribute a : f.getRightHandAttributes()) {
					if (RDTUtils.attributeListContainsAttribute(c.getClosureOf(), a)) {
						isPartofKey = true;
						break;
					}
				}
				if (isPartofKey) {
					break;
				}
			}
			if (isPartofKey) {
				continue;
			}
			// Having not satisfied at least one of the above conditions, the
			// functional dependency is added to failed list
			failedFDs.add(f);
		}
		// Check if all FDs fit at least one of the conditions
		if (failedFDs.isEmpty() && isSecondNormalForm) {
			isThirdNormalForm = true;
			thirdNormalFormMsg = "Input relation is in 3NF: It is in 2NF and for each functional dependency: "
					+ "(1) The right-hand side is a subset of the left hand side, "
					+ "(2) the left-hand side is a superkey (or minimum key) of the relation, or "
					+ "(3) the right-hand side is (or is a part of) some minimum key of the relation.";
		} else {
			isThirdNormalForm = false;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < failedFDs.size(); i++) {
				sb.append(failedFDs.get(i).getFDName());
				if (i < failedFDs.size() - 1) {
					sb.append("; ");
				}
			}
			sb.append(".");
			String failure;
			if (failedFDs.size() == 1) {
				failure = "dependency that failed is: ";
			} else {
				failure = "dependencies that failed are: ";
			}
			String twoNFStatus;
			if (isSecondNormalForm) {
				twoNFStatus = "it is in 2NF";
			} else {
				twoNFStatus = "it is not in 2NF";
			}
			String twoNFStatusEnding;
			String threeNFStatus;
			String threeNFRequirements = "(1) The right-hand side is a subset of the left hand side, "
					+ "(2) the left-hand side is a superkey (or minimum key) of the relation, or "
					+ "(3) the right-hand side is (or is a part of) some minimum key of the relation.";
			if (failedFDs.isEmpty()) {
				twoNFStatusEnding = ".";
				threeNFStatus = " Otherwise it would be in 3NF as all of the relation's functional dependencies satisfy at least one of the following conditions: " + threeNFRequirements;
			} else {
				twoNFStatusEnding = isSecondNormalForm ? " but " : " and ";
				threeNFStatus = "not all functional dependencies satisfy at least one of the following conditions: "
						+ threeNFRequirements
						+ " The functional " + failure + sb.toString();
			}
			
			thirdNormalFormMsg = "Input relation is not in 3NF: " + twoNFStatus + twoNFStatusEnding + threeNFStatus;
		}
	}

	private void calculateBCNF() {
		List<Functional_Dependency> failedFDs = new ArrayList<>();
		// For each FD A ->B
		for (Functional_Dependency f : RDTUtils.getSingleAttributeMinimalCoverList(relation.getFDs(), relation)) {
			// Check if B is a subset of A (A->B is trivial)
			if (isTrivialFD(f)) {
				continue;
			}
			// Check if A is a superkey of input relation
			if (isAKeyOrSuperKey(f)) {
				continue;
			}
			// Having not satisfied at least one of the previous conditions, the
			// FD violates BCNF
			failedFDs.add(f);
		}

		if (failedFDs.isEmpty() && isThirdNormalForm) {
			isBCNF = true;
			BCNFMsg = "Input relation is in BCNF: it is in 3NF and for each functional dependency: "
					+ "(1) The right-hand side is a subset of the left hand side, or "
					+ "(2) the left-hand side is a superkey (or minimum key) of the relation.";
		} else {
			isBCNF = false;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < failedFDs.size(); i++) {
				sb.append(failedFDs.get(i).getFDName());
				if (i < failedFDs.size() - 1) {
					sb.append("; ");
				}
			}
			sb.append(".");
			String failure;
			if (failedFDs.size() == 1) {
				failure = "dependency that failed is: ";
			} else {
				failure = "dependencies that failed are: ";
			}
			String threeNFStatus;
			if (isThirdNormalForm) {
				threeNFStatus = "it is in 3NF but ";
			} else {
				threeNFStatus = "it is not in 3NF and ";
			}
			BCNFMsg = "Input relation is not in BCNF: " + threeNFStatus
					+ "not all functional dependencies satisfy at least one of the following conditions: "
					+ "(1) The right-hand side is a subset of the left hand side, or "
					+ "(2) the left-hand side is a superkey (or minimum key) of the relation. " + "The functional "
					+ failure + sb.toString();
		}
	}

	
	protected String getFirstNormalFormMsg() {
		return firstNormalFormMsg;
	}

	protected String getSecondNormalFormMsg() {
		return secondNormalFormMsg;
	}

	protected String getThirdNormalFormMsg() {
		return thirdNormalFormMsg;
	}

	protected String getBCNFMsg() {
		return BCNFMsg;
	}
	
	protected boolean isIn3NF() {
		return isThirdNormalForm;
	}
	
	protected boolean isInBCNF() {
		return isBCNF;
	}
	
	protected List<Functional_Dependency> getBCNFViolatingFDs() {
		if (bcnfViolatingFDs == null) {
			bcnfViolatingFDs = new ArrayList<>();
			for (Functional_Dependency f : relation.getFDs()) {
				// Check if B is a subset of A (A->B is trivial)
				if (isTrivialFD(f)) {
					continue;
				}
				// Check if A is a superkey of input relation
				if (isAKeyOrSuperKey(f)) {
					continue;
				}
				// Having not satisfied at least one of the previous conditions, the
				// FD violates BCNF
				bcnfViolatingFDs.add(f);
			}
		}
		return bcnfViolatingFDs;
	}
}

