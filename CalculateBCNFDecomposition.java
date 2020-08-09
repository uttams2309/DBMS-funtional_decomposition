package myproject;
import java.util.ArrayList;
import java.util.List;
public class CalculateBCNFDecomposition extends CalculateDecomposition {
	public final List<Relation> resultWithPossibleDuplicates;
	public Calculate3NFDecomposition threenfDecomposition;
	public List<Relation> bcnfDecomposedWithDuplicates;
	public List<Relation> threeNFDecomposedWithDuplicates;
	public List<Relation> pureBCNFDecomposedRs;
	public List<Relation> threeNFDecomposedRs;
	public List<Functional_Dependency> pureBCNFLostFDs;
	public List<Functional_Dependency> threeNFLostFDs;
	
	public CalculateBCNFDecomposition(final Calculate3NFDecomposition threenfDecomposition) {
		super(threenfDecomposition.getInputRelation());
		resultWithPossibleDuplicates = new ArrayList<>();
		this.threenfDecomposition = threenfDecomposition;
	}
    public CalculateBCNFDecomposition(final Relation relation)
    { 
    	super(relation);
    	resultWithPossibleDuplicates = new ArrayList<>();
    }
	@Override
	protected void decompose() {
		if (getInputRelation().getInputFDs().isEmpty()) {
			setOutputMsgFlag(true);
			setOutputMsg("No functional dependencies provided in input relation, therefore input relation is already in BCNF.");
			return;
		}
		if (getInputRelation().getNormalFormsResults().isInBCNF()) {
			setOutputMsgFlag(true);
			setOutputMsg("Input relation is already in BCNF. No decomposition necessary. ");
			return;
		}
		
		BCNFDecomposeMethodWithout3NF();
		if (threenfDecomposition.getOutputRelations().isEmpty()) {
			threenfDecomposition.force3NFDecomposition();
		}
		if (!threenfDecomposition.getOutputRelations().isEmpty()) {
			decomposeFrom3NF();
		}

		return;
	}
	
	public List<Relation> getPureBCNFDecomposedRs() {
		return pureBCNFDecomposedRs;
	}
	
	public List<Relation> getBcnfDecomposedWithDuplicates() {
		return bcnfDecomposedWithDuplicates;
	}

	public List<Functional_Dependency> getPureBCNFLostFDs() {
		return pureBCNFLostFDs;
	}

	public List<Relation> getThreeNFDecomposedWithDuplicates() {
		return threeNFDecomposedWithDuplicates;
	}

	public List<Relation> getThreeNFDecomposedRs() {
		return threeNFDecomposedRs;
	}

	public List<Functional_Dependency> getThreeNFLostFDs() {
		return threeNFLostFDs;
	}

	public void BCNFDecomposeMethodWithout3NF() {
		List<Relation> workingOutputRelations = decomposeBCNFHelper(getInputRelation());
		bcnfDecomposedWithDuplicates = workingOutputRelations;
		List<Relation> eliminatedDuplicatesAndSubsets = eliminateDuplicateSubsetRelations(workingOutputRelations);
		List<Functional_Dependency> missingFDs = findEliminatedFunctionalDependencies(eliminatedDuplicatesAndSubsets, getInputRelation().getInputFDs());
		pureBCNFDecomposedRs = eliminatedDuplicatesAndSubsets;
		pureBCNFLostFDs = missingFDs;
	}
	
	public List<Relation> decomposeBCNFHelper(final Relation r) {
		List<Relation> result = new ArrayList<>();
		int counter = 0;
		if (r.getClosures().isEmpty()) {
			CalculateClosure.improvedCalculateClosures(r);
		}
		if (r.getMinimalCover().isEmpty()) {
			MinimalFDCover.determineMinimalCover(r);
		}
		if (r.getMinimumKeyClosures().isEmpty()) {
			CalculateKeys.calculateKeys(r);
		}
		if (!r.getNormalFormsResults().hasDeterminedNormalForms) {
			r.determineNormalForms();
		}
		if (r.getNormalFormsResults().isInBCNF()) {
			result.add(r);
			return result;
		}
		for (Functional_Dependency f : r.getNormalFormsResults().getBCNFViolatingFDs()) {
			Closure leftSideClosure = RDTUtils.findClosureWithLeftHandAttributes(f.getLeftHandAttributes(), r.getClosures());
			List<Functional_Dependency> r1FDs = RDTUtils.fetchFDsOfDecomposedR(RDTUtils.getSingleAttributeMinimalCoverList(r.getInputFDs(), r), leftSideClosure.getClosure());
			Relation r1 = new Relation(r.getName() + "_" + counter++, leftSideClosure.getClosure(), r1FDs);
			List<Attribute> r2Attributes = new ArrayList<>();
			for (Attribute a : f.getLeftHandAttributes()) {
				if (!RDTUtils.attributeListContainsAttribute(r2Attributes, a)) {
					r2Attributes.add(a);
				}
			}
			for (Attribute a : r.getAttributes()) {
				if (!RDTUtils.attributeListContainsAttribute(leftSideClosure.getClosure(), a)) {
					if (!RDTUtils.attributeListContainsAttribute(r2Attributes, a)) {
						r2Attributes.add(a);
					}
				}
			}
			List<Functional_Dependency> r2FDs = RDTUtils.fetchFDsOfDecomposedR(RDTUtils.getSingleAttributeMinimalCoverList(r.getInputFDs(), r), r2Attributes);
			Relation r2 = new Relation(r.getName() + "_" + counter++, r2Attributes, r2FDs);
			result.addAll(decomposeBCNFHelper(r1));
			result.addAll(decomposeBCNFHelper(r2));
		}
		return result;
	}
	
	public void decomposeFrom3NF() {
		if (this.threenfDecomposition == null) {
			return;
		}
		List<Relation> workingBCNFRelations = new ArrayList<>();
		for (Relation threeNF : threenfDecomposition.getOutputRelations()) {
			workingBCNFRelations.addAll(decomposeBCNFHelper(threeNF));
		}
		threeNFDecomposedWithDuplicates = workingBCNFRelations;
		List<Relation> purgeDuplicatesAndSubsets = eliminateDuplicateSubsetRelations(workingBCNFRelations);
		List<Functional_Dependency> lostFDs = findEliminatedFunctionalDependencies(purgeDuplicatesAndSubsets, RDTUtils.getSingleAttributeMinimalCoverList(getInputRelation().getMinimalCover(), getInputRelation()));
		threeNFDecomposedRs = purgeDuplicatesAndSubsets;
		threeNFLostFDs = lostFDs;
	}
	
	public List<Relation> eliminateDuplicateSubsetRelations(final List<Relation> workingOutputRelations) {
		List<Relation> output = new ArrayList<>();
		boolean[] removeIndices = new boolean[workingOutputRelations.size()];
		for (int i = 0; i < removeIndices.length; i++) {
			removeIndices[i] = false;
		}
		for (int i = 0; i < workingOutputRelations.size(); i++) {
			if (!removeIndices[i]) {
				Relation currentRelation = workingOutputRelations.get(i);
				for (int j = 0; j < workingOutputRelations.size(); j++) {
					if (i != j && !removeIndices[j]) {
						Relation otherRelation = workingOutputRelations.get(j);
						if (RDTUtils.isAttributeListSubsetOfOtherAttributeList(currentRelation.getAttributes(),
								otherRelation.getAttributes())) {
							removeIndices[j] = true;
						}
					}
				}
			}
		}
		for (int i = 0; i < workingOutputRelations.size(); i++) {
			if (!removeIndices[i]) {
				output.add(workingOutputRelations.get(i));
			}
		}
		return output;
	}
	
	public List<Functional_Dependency> findEliminatedFunctionalDependencies(final List<Relation> outputRelations, final List<Functional_Dependency> inputFDs) {
		if (outputRelations == null || inputFDs == null) {
			throw new IllegalArgumentException("Input list of relations or input list of functional dependencies is null.");
		}
		List<Functional_Dependency> missingFDs = new ArrayList<>();
		for (Functional_Dependency originalFD : inputFDs) {
			boolean found = false;
			for (Relation bcnfR : outputRelations) {
				if (RDTUtils.isFunctionalDependencyAlreadyInFDList(originalFD, bcnfR.getInputFDs())) {
					found = true;
					break;
				}
			}
			if (!found) {
				missingFDs.add(originalFD);
			}
		}
		return missingFDs;
	}

	protected List<Relation> getResultWithDuplicates() {
		return resultWithPossibleDuplicates;
	}
	
	@Override
	protected List<Relation> getOutputRelations() {
		return new ArrayList<Relation>();
	}
}

