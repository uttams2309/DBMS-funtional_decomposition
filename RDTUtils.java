package myproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class RDTUtils {
	public static void sortAttributes(ArrayList<Attribute> attributes){
		Collections.sort(attributes, new SortAttr());
	}
	
	public static String stringifyAttributeList(List<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : attributes){
			s.append(a.getName());
		}
		return s.toString();
	}
	public static String stringifyAttributes(ArrayList<Attribute> leftAttributes, ArrayList<Attribute> rightAttributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : leftAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		s.append(";");
		for(Attribute a : rightAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		return s.toString();
	}
	public static void sortFunctionalDependency(ArrayList<Functional_Dependency> funcDeps){
		Collections.sort(funcDeps, new SortFDs());
	}
	
	public static final String functionalDependencyArrow = "->";
	public static final String multivaliedDependencyArrow = "->";
	public static final String LONG_LEFTWARDS_ARROW = "<---";

	/**
	 * @param attributeList
	 * @param attribute
	 * @return True if the attribute is contained in the attributeList and false
	 *         otherwise.
	 */
	protected static boolean attributeListContainsAttribute(final List<Attribute> attributeList,
			final Attribute attribute) {
		for (Attribute a : attributeList) {
			if (a.getName().equals(attribute.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param attributeList
	 * @return True if the given attribute list does not contain duplicate attributes and false otherwise.
	 */
	protected static boolean attributeListContainsUniqueAttributes(final List<Attribute> attributeList) {
		for (int i = 0; i < attributeList.size(); i++) {
			for (int j = 0; j < attributeList.size(); j++) {
				if (i != j && attributeList.get(i).getName().equals(attributeList.get(j).getName())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param firstAttributeList
	 * @param secondAttributeList
	 * @return True if second list's attributes are all found in the first list
	 *         and false otherwise.
	 */
	protected static boolean isAttributeListSubsetOfOtherAttributeList(final List<Attribute> firstAttributeList,
			final List<Attribute> secondAttributeList) {
		if (firstAttributeList.size() < secondAttributeList.size()) {
			return false;
		}
		for (Attribute a : secondAttributeList) {
			if (!attributeListContainsAttribute(firstAttributeList, a)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param closureList
	 * @param attribute
	 * @return True if the attribute is contained in a closure that is in the
	 *         closureList and false otherwise.
	 */
	protected static boolean closureListContainsAttribute(final List<Closure> closureList, final Attribute attribute) {
		for (Closure c : closureList) {
			for (Attribute a : c.getClosureOf()) {
				if (a.getName().equals(attribute.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param firstClosure
	 * @param secondClosure
	 * @return True if second closure is a proper subset of the first closure
	 *         and false otherwise.
	 */
	protected static boolean isClosureProperSubsetOfOtherClosure(final Closure firstClosure, final Closure secondClosure) {
		if (firstClosure.getClosureOf().size() <= secondClosure.getClosureOf().size()) {
			return false;
		}
		for (Attribute a : secondClosure.getClosureOf()) {
			if (!attributeListContainsAttribute(firstClosure.getClosureOf(), a)) {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isFunctionalDependencyAlreadyInFDList(final Functional_Dependency fd, final List<Functional_Dependency> fdList) {
		if (fd == null || fdList == null) {
			throw new IllegalArgumentException("Input null functional dependency or list of functional dependencies.");
		}
		if (fdList.isEmpty()) {
			return false;
		}
		HashMap<String, HashSet<String>> dependencyMap = new HashMap<>();
		for (Functional_Dependency f : fdList) {
			String fKey = f.getLeftHandNameKey();
			HashSet<String> fdAttrs = dependencyMap.get(fKey);
			if (fdAttrs == null) {
				fdAttrs = new HashSet<>();
			}
			for (Attribute a : f.getRightHandAttributes()) {
				fdAttrs.add(a.getName());
			}
			dependencyMap.put(fKey, fdAttrs);
		}
		HashSet<String> fdCompareAttrs = dependencyMap.get(fd.getLeftHandNameKey());
		if (fdCompareAttrs != null) {
			for (Attribute fdCompareAttr : fd.getRightHandAttributes()) {
				if (!fdCompareAttrs.contains(fdCompareAttr.getName())) {
					return false;
				}
			}
			return true;
		}

		return false;
	}
		
	/**
	 * 
	 * @param leftHand
	 * @param closureList
	 * @return Closure object in input closureList of which the left-hand side attributes match input list of attributes, or null
	 * if such Closure object does not exist in input closureList.
	 */
	protected static Closure findClosureWithLeftHandAttributes(final List<Attribute> leftHand, final List<Closure> closureList) {
		Closure closure = null;
		for (Closure c : closureList) {
			if (c.getClosureOf().size() == leftHand.size()) {
				boolean containsAll = true;
				for (Attribute attr : leftHand) {
					if (!attributeListContainsAttribute(c.getClosureOf(), attr)) {
						containsAll = false;
						break;
					}
				}
				if (containsAll) {
					closure = c;
					break;
				}
			}
		}
		return closure;
	}

	/**
	 * 
	 * @param fdList
	 * @param decomposedRAttrs
	 * @return List of all functional dependencies from input FD list that have
	 *         attributes that match the input list of attributes.
	 */
	protected static List<Functional_Dependency> fetchFDsOfDecomposedR(final List<Functional_Dependency> fdList,
			final List<Attribute> decomposedRAttrs) {
		List<Functional_Dependency> result = new ArrayList<>();
		if (fdList == null || decomposedRAttrs == null || fdList.isEmpty() || decomposedRAttrs.isEmpty()) {
			return result;
		}
		for (Functional_Dependency f : fdList) {
			List<Attribute> fdAttrs = new ArrayList<>();
			for (Attribute a : f.getLeftHandAttributes()) {
				if (!attributeListContainsAttribute(fdAttrs, a)) {
					fdAttrs.add(a);
				}
			}
			for (Attribute a : f.getRightHandAttributes()) {
				if (!attributeListContainsAttribute(fdAttrs, a)) {
					fdAttrs.add(a);
				}
			}
			if (isAttributeListSubsetOfOtherAttributeList(decomposedRAttrs, fdAttrs)) {
				result.add(f);
			}
		}
		return result;
	}
	protected static List<Functional_Dependency> getSingleAttributeMinimalCoverList(final List<Functional_Dependency> fdList, final Relation relation) {
		List<Functional_Dependency> result = new ArrayList<>();
		for (Functional_Dependency fd : fdList) {
			if (fd.getRightHandAttributes().size() > 1) {
				for (Attribute a : fd.getRightHandAttributes()) {
					List<Attribute> singleRightAttribute = new ArrayList<>();
					singleRightAttribute.add(a);
					Functional_Dependency split = new Functional_Dependency(fd.getLeftHandAttributes(), singleRightAttribute, relation);
					result.add(split);
				}
			} else {
				result.add(fd);
			}
		}
		return result;
	}
	public static void generateFunctionalDependencies(ArrayList<Functional_Dependency> copyFrom, ArrayList<Functional_Dependency> copyTo){
		for(Functional_Dependency f : copyFrom){
			ArrayList<Attribute> arr1 = new ArrayList<Attribute>(f.getLeftHandAttributes());
			ArrayList<Attribute> arr2 = new ArrayList<Attribute>(f.getRightHandAttributes());
			copyTo.add(new Functional_Dependency(f.getRelation(), arr1,arr2, f.getNormalForm()));
		}
		// return copyTo;
	}
	public static void generateFunctionalDependencies(ArrayList<Functional_Dependency> copyFrom, CopyOnWriteArrayList<Functional_Dependency> copyTo){
		for(Functional_Dependency f : copyFrom){
			ArrayList<Attribute> arr1 = new ArrayList<Attribute>(f.getLeftHandAttributes());
			ArrayList<Attribute> arr2 = new ArrayList<Attribute>(f.getRightHandAttributes());
			
			copyTo.add(new Functional_Dependency(f.getRelation(),arr1,arr2, f.getNormalForm()));
		}
		// return copyTo;
	}
}
class SortFDs implements Comparator<Functional_Dependency>{
	public int compare(Functional_Dependency a, Functional_Dependency b){
		return RDTUtils.stringifyAttributeList(a.getLeftHandAttributes()).compareTo(RDTUtils.stringifyAttributeList(b.getLeftHandAttributes()));
	}
}
class SortAttr implements Comparator<Attribute>{
	public int compare(Attribute a, Attribute b){
		return ((a.getName()).compareTo((b.getName())));
	}
}
