package myproject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
public class MinimalFDCover {
	public static void determineMinimalCover(final Relation relation) {
		List<Functional_Dependency> fMin = new ArrayList<>();
		List<String> minimalCoverOutput = new ArrayList<>();

		if (relation.getInputFDs().isEmpty()) {
			// Input FDs is empty, so minimal cover is also empty.
			return;
		}
		if (relation.getClosures().isEmpty()) {
			CalculateClosure.improvedCalculateClosures(relation);
		}

		// Split FDs that have more than one attribute on right-side.
		for (Functional_Dependency f : relation.getInputFDs()) {
			if (f.getIsProperDependency()) {
				if (f.getRightHandAttributes().size() == 1 && !RDTUtils.isFunctionalDependencyAlreadyInFDList(f, fMin)) {
					fMin.add(f);
				} else {
					minimalCoverOutput.add("Input functional dependency " + f.getFDName()
							+ " has more than one attribute on its right-hand side. Splitting input functional dependency: ");
					for (Attribute a : f.getRightHandAttributes()) {
						List<Attribute> rightSplitted = new ArrayList<>();
						rightSplitted.add(a);
						Functional_Dependency splitted = new Functional_Dependency(f.getLeftHandAttributes(), rightSplitted,
								relation);
						if (!RDTUtils.isFunctionalDependencyAlreadyInFDList(splitted, fMin)) {
							fMin.add(splitted);
							minimalCoverOutput.add(splitted.getFDName());
						}
					}
					minimalCoverOutput.add("Finished splitting input functional dependency " + f.getFDName() + ".");
				}
			}
		}
		// Minimize left-hand side
		List<Functional_Dependency> minimizedLHS = new ArrayList<>();
		for (Functional_Dependency f : fMin) {
			// Only need to consider FDs whose LHS has 2 or more attributes
			if (f.getLeftHandAttributes().size() > 1) {
				minimalCoverOutput
						.add("Functional dependency "
								+ f.getFDName()
								+ " has more than one attribute on its left-hand side. Checking if each left-hand side attribute is a necessary attribute to compute the right-hand side attribute(s): ");
				List<Attribute> minimizedLeftAttributes = new ArrayList<>();
				HashSet<Attribute> nonEssentialAttributes = new HashSet<>();
				boolean essentialAttribute = false;
				for (Attribute a : f.getLeftHandAttributes()) {
					// Check if attribute is necessary by taking it out and
					// computing closure.
					Attribute rightAttribute = f.getRightHandAttributes().get(0);
					essentialAttribute = false;
					List<Attribute> newLeftSide = new ArrayList<>();
					for (Attribute b : f.getLeftHandAttributes()) {
						if (!b.equals(a) && !nonEssentialAttributes.contains(b)) {
							newLeftSide.add(b);
						}
					}
					// Find closure with new left-hand side
					Closure closure = RDTUtils.findClosureWithLeftHandAttributes(newLeftSide, relation.getClosures());

					// Now check if the right-hand attribute is still in the
					// closure.
					if (closure == null || !RDTUtils.attributeListContainsAttribute(closure.getClosure(), rightAttribute)) {
						// Removed attribute is necessary. Add to list.
						minimizedLeftAttributes.add(a);
						essentialAttribute = true;
						minimalCoverOutput.add("Attribute " + a.getName()
								+ " is necessary in order to maintain coverage on right-hand side attribute " + rightAttribute
								+ ".");
					}
					if (!essentialAttribute) {
						minimalCoverOutput
								.add("Attribute "
										+ a.getName()
										+ " is not necessary since the remaining left-hand side attribute(s) can still determine the right-hand side attribute " + rightAttribute
										+ ".");
						nonEssentialAttributes.add(a);
					}
				}
				if (minimizedLeftAttributes.size() < f.getLeftHandAttributes().size()) {
					if (!minimizedLeftAttributes.isEmpty()) {
						Functional_Dependency reducedFD = new Functional_Dependency(minimizedLeftAttributes,
								f.getRightHandAttributes(), relation);
						// Verify that new FD is legitimate (i.e., the closure
						// of left side includes the attribute on right side
						Closure verifyClosure = RDTUtils.findClosureWithLeftHandAttributes(reducedFD.getLeftHandAttributes(),
								relation.getClosures());
						if (RDTUtils.attributeListContainsAttribute(verifyClosure.getClosure(), reducedFD
								.getRightHandAttributes().get(0))) {
							if (!RDTUtils.isFunctionalDependencyAlreadyInFDList(reducedFD, minimizedLHS)) {
								minimizedLHS.add(reducedFD);
							}
						}
					}
				} else {
					if (!RDTUtils.isFunctionalDependencyAlreadyInFDList(f, minimizedLHS)) {
						minimizedLHS.add(f);
					}
				}
			} else {
				if (!RDTUtils.isFunctionalDependencyAlreadyInFDList(f, minimizedLHS)) {
					minimizedLHS.add(f);
				}
			}
		}
		fMin.clear();
		for (Functional_Dependency funcDe : minimizedLHS) {
			if (funcDe.getIsProperDependency() && !RDTUtils.isFunctionalDependencyAlreadyInFDList(funcDe, fMin)) {
				fMin.add(funcDe);
			}
		}
		// Now minimize the set of functional dependencies
		minimalCoverOutput
				.add("Now minimizing the set of functional dependencies. For each functional dependency, create a temporary subset of functional dependencies without the given functional dependency. The given functional dependency is necessary if the new closure does not contain the right-hand side attribute of the removed functional dependency.");
		int[] blocked = new int[fMin.size()];
		for (int y = 0; y < blocked.length; y++) {
			blocked[y] = 0;
		}
		List<Functional_Dependency> minimizedSetFDs = new ArrayList<>();
		for (int i = 0; i < fMin.size(); i++) {
			if (blocked[i] != 0) {
				continue;
			}
			// Create temporary subset of FDs with the given FD removed
			List<Functional_Dependency> checkRemoved = new ArrayList<>();
			for (int j = 0; j < fMin.size(); j++) {
				if (j != i && blocked[j] == 0) {
					checkRemoved.add(fMin.get(j));
				}
			}
			Closure checkClosure = CalculateClosure.calculateClosureOf(fMin.get(i).getLeftHandAttributes(), checkRemoved);
			if (!RDTUtils.attributeListContainsAttribute(checkClosure.getClosure(), fMin.get(i).getRightHandAttributes().get(0))) {
				// The FD is necessary since the new closure does not contain
				// the right-hand side attribute of the removed FD.
				minimizedSetFDs.add(fMin.get(i));
				minimalCoverOutput.add("Functional dependency " + fMin.get(i).getFDName() + " is necessary.");
			} else {
				// The FD is not necessary and we can strike it out of the list.
				blocked[i] = 1;
				minimalCoverOutput.add("Functional dependency " + fMin.get(i).getFDName() + " is not necessary.");
			}
		}
		fMin.clear();
		fMin.addAll(minimizedSetFDs);
		// Now consolidate FDs that have common left-hand side
		minimalCoverOutput.add("Consolidating functional dependencies that have the same left-hand side attribute(s).");
		List<Functional_Dependency> consolidatedFDs = new ArrayList<>();
		int[] checkedIndices = new int[fMin.size()];
		for (int z = 0; z < checkedIndices.length; z++) {
			checkedIndices[z] = 0;
		}
		for (int i = 0; i < fMin.size(); i++) {
			if (checkedIndices[i] != 0) {
				continue;
			}
			Functional_Dependency f = fMin.get(i);
			checkedIndices[i] = 1;
			List<Attribute> leftHandSide = new ArrayList<>();
			List<Attribute> rightHandSide = new ArrayList<>();
			for (Attribute leftAttr : f.getLeftHandAttributes()) {
				leftHandSide.add(leftAttr);
			}
			for (Attribute rightAttr : f.getRightHandAttributes()) {
				rightHandSide.add(rightAttr);
			}
			for (int j = 0; j < fMin.size(); j++) {
				if (j != i && checkedIndices[j] == 0) {
					Functional_Dependency g = fMin.get(j);
					if (g.getLeftHandAttributes().size() == leftHandSide.size()) {
						boolean containsAll = true;
						for (Attribute leftAttr : leftHandSide) {
							if (!RDTUtils.attributeListContainsAttribute(g.getLeftHandAttributes(), leftAttr)) {
								containsAll = false;
								break;
							}
						}
						if (containsAll) {
							for (Attribute dupRightAttr : g.getRightHandAttributes()) {
								rightHandSide.add(dupRightAttr);
							}
							checkedIndices[j] = 1;
						}
					}
				}
			}
			Functional_Dependency consolidated = new Functional_Dependency(leftHandSide, rightHandSide, relation);
			consolidatedFDs.add(consolidated);
		}
		Collections.sort(consolidatedFDs);
		for (Functional_Dependency consolidatedFD : consolidatedFDs) {
			relation.addMinimalCoverFD(consolidatedFD);
		}
		minimalCoverOutput.add("Finished calculating a minimal cover set of functional dependencies on the given relation.");
		relation.setMinimalCoverOutput(minimalCoverOutput);
	}
}
