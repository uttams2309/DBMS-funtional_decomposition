package myproject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Dependency<T extends Dependency<T>> implements Comparable<T> {
	private String funcdep;
	private ArrayList<Attribute> y;
	private ArrayList<Attribute> x;
	private final Relation relation;
	private final List<Attribute> leftSide;
	private final List<Attribute> rightSide;
	private String name;
	private final String dependencyArrow;
	private boolean isProperDependency;
	private int normalForm;
	
	public Dependency(final String input, final String dependencyArrow, final Relation relation) {
DetermineNormalForms normalForms = relation.getNormalFormsResults();
if(normalForms.isSecondNormalForm) normalForm =2;
 else if(normalForms.isThirdNormalForm)normalForm =3;
 else if(normalForms.isBCNF)normalForm =4;
 else if(normalForms.isFirstNormalForm)normalForm =1;
         

		this.relation = relation;
		leftSide = new ArrayList<>();
		rightSide = new ArrayList<>();
		name = null;
		this.dependencyArrow = dependencyArrow;
		isProperDependency = initialize(input);
	}
	
	private boolean isFullKey(ArrayList<Attribute> attributes, ArrayList<ArrayList<Attribute>> candidate_key){
		return candidate_key.contains(attributes);
	}
	
	private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){
		for(ArrayList<Attribute> k : candidate_key){
			// System.out.println("Key is: " + k + " and Attr: " + attributes + " and Attr E Key: " + k.containsAll(attributes) + " and are equal: " + k.equals(attributes));
			if(k.containsAll(attributes) && !k.equals(attributes)){
				return true;
			}
		}
		return false;
	}
	
	private boolean areAllKeyAttributes(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes){
		boolean isNonKey = true;
		for(Attribute a : attributes){
			if(nonKeyAttributes.contains(a)){
				return false;
			}
		}
		return true;
	}
	
	public void computeNormalForm(ArrayList<Attribute> keyAttributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){				
		// 2NF Checking
		boolean is2NF;
		if(isPartialKey(this.x, nonKeyAttributes, candidate_key) && !areAllKeyAttributes(this.y, nonKeyAttributes)){
			is2NF = false;
		} else {
			is2NF = true;
		}
		// if(this.isNonKeyAttribute(this.y, nonKeyAttributes)){
		// 	if(isPartialKey(this.x, nonKeyAttributes, candidate_key)){
		// 		is2NF = false;
		// 	} else {
		// 		is2NF = true;
		// 	}
		// } else {
		// 	is2NF = true;
		// }
		if(is2NF){
			// System.out.println("Setting 2NF");
			this.normalForm = 2;
		}

		// 3NF Checking
		if(this.normalForm == 2){
			if((isFullKey(this.x, candidate_key)) || (isFullKey(this.y, candidate_key) || (isPartialKey(this.y, nonKeyAttributes, candidate_key) && !isNonKeyAttribute(this.y, nonKeyAttributes)))){
				// System.out.println("Setting 3NF");
				this.normalForm = 3;
			}
		}
		// BC NF Checking
		if(this.normalForm == 3){
			if(isFullKey(this.x, candidate_key)){
				// System.out.println("Setting BCNF");
				this.normalForm = 4;
			}
		}
	}
	
	public Dependency(Relation relation, ArrayList<Attribute> x, ArrayList<Attribute> y, int normalForm){
		leftSide = null; rightSide=null;
		dependencyArrow="";
		this.relation = relation;
		this.x = new ArrayList<Attribute>();
		StringBuilder sbLeft = new StringBuilder();
		StringBuilder sbRight = new StringBuilder();
		for(Attribute a : x){
			this.x.add(a);
			sbLeft.append(a.getName());
			sbLeft.append(",");
		}
		sbLeft.deleteCharAt(sbLeft.toString().length() - 1);
		this.y = new ArrayList<Attribute>();
		for(Attribute a : y){
			this.y.add(a);
			sbRight.append(a.getName());
			sbRight.append(",");
		}
		sbRight.deleteCharAt(sbRight.toString().length() - 1);
		this.normalForm = normalForm;

		this.funcdep = sbLeft.toString() + "->" + sbRight.toString();
	}

	
	
	public Dependency(final List<Attribute> leftHandSide, final List<Attribute> rightHandSide, final String dependencyArrow, final Relation relation) {
		DetermineNormalForms normalForms = relation.getNormalFormsResults();
		if(normalForms.isSecondNormalForm) normalForm =2;
		 else if(normalForms.isThirdNormalForm)normalForm =3;
		 else if(normalForms.isBCNF)normalForm =4;
		 else if(normalForms.isFirstNormalForm)normalForm =1;

		this.relation = relation;
		this.leftSide = leftHandSide;
		this.rightSide = rightHandSide;
		name = null;
		this.dependencyArrow = dependencyArrow;
		isProperDependency = true;
		if (leftSide.isEmpty() || rightSide.isEmpty()) {
			isProperDependency = false;
		}
		if (isProperDependency) {
			try {
				Collections.sort(leftSide);
				Collections.sort(rightSide);
				setName(this.dependencyArrow);
			} catch (Exception e) {
				isProperDependency = false;
			}
		}
	}
	
	private boolean initialize(final String input) {
		try {
			setAttributes(input);
			if (leftSide.isEmpty() || rightSide.isEmpty()) {
				return false;
			}
			Collections.sort(leftSide);
			Collections.sort(rightSide);
			setName(dependencyArrow);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void setAttributes(final String input) {
		String[] splitted = input.split("->");
		String fullLeft = splitted[0];
		String fullRight = splitted[1];
		
		// Get left attributes
		String[] leftAttributes = fullLeft.split(",");
		for (String attribute : leftAttributes) {
			if (!attribute.isEmpty()) {
				Attribute a = relation.getAttribute(attribute);
				if (a == null) {
					relation.setIntegrityCheckErrorMsg("Attribute " + attribute + " does not exist in schema of Relation " + relation.getName());
					relation.setPassedIntegrityChecks(false);
					return;
				}
				leftSide.add(a);
			}
		}
		
		// Get right attributes
		String[] rightAttributes = fullRight.split(",");
		for (String attribute : rightAttributes) {
			if (!attribute.isEmpty()) {
				Attribute a = relation.getAttribute(attribute);
				if (a == null) {
					relation.setIntegrityCheckErrorMsg("Attribute " + attribute + " does not exist in schema of Relation " + relation.getName());
					relation.setPassedIntegrityChecks(false);
					return;
				}
				rightSide.add(a);
			}
		}
	}
	
	/**
	 * Given left hand attribute A and right hand attribute B and dependencyArrow, 
	 * sets dependency name to be "A {dependencyArrow} B".
	 * Will only set the name once in object's lifetime.
	 * @param dependencyArrow
	 */
	private void setName(final String dependencyArrow) {
		if (this.name != null) {
			return;		// Makes name immutable once set
		}
		StringBuilder sb = new StringBuilder();
		for (Attribute a : leftSide) {
			sb.append(a.getName());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(" " + dependencyArrow + " ");
		for (Attribute b : rightSide) {
			sb.append(b.getName());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		this.name = sb.toString();
	}
	
	protected String getName() {
		return this.name;
	}
	
	protected String getLeftHandNameKey() {
		StringBuilder sb = new StringBuilder();
		for (Attribute a : leftSide) {
			sb.append(a.getName());
			sb.append(":");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	protected List<Attribute> getLeftHandAttributes() {
		return leftSide;
	}
	
	protected List<Attribute> getRightHandAttributes() {
		return rightSide;
	}
	
	protected boolean getIsProperDependency() {
		return isProperDependency;
	}
	public int getNormalForm(){
		return this.normalForm;
	}
	public Relation getRelation(){
		return this.relation;
	}
	public abstract int compareTo(T otherDependency);
}
