package myproject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Relation {
	private ArrayList<Functional_Dependency> fullFuncDeps;
	private ArrayList<Functional_Dependency> partialFuncDeps;
	private ArrayList<ArrayList<Attribute>> candidate_key=new ArrayList<ArrayList<Attribute>>();
	private static String EMPTY = "";
	public final String fdeep;
	final String name;
	public List<Attribute> attributes;
	private final List<Attribute> primeAttributes;
	private final List<Attribute> nonPrimeAttributes;
	private boolean passedIntegrityChecks;
	private String integrityCheckErrorMsg;
	 public List<Functional_Dependency> fds;
	 List<Functional_Dependency> derivedFDs;
	 List<Functional_Dependency> minimalCover;
	private List<String> minimalCoverOutput;
	private final List<MultivaluedDependency> mvds;
	private final List<Closure> closures;
	private final List<Closure> minimumKeys;
	private final List<Closure> superKeys;
	private DetermineNormalForms normalFormResults;
	
	public void separateFDs(){
		// System.out.println("Separating FDs:");
		// this.partialFuncDeps = new ArrayList<FunctionalDependency>();
		// this.fullFuncDeps = new ArrayList<FunctionalDependency>();
		Set<Functional_Dependency> partialFuncDeps = new HashSet<Functional_Dependency>();
		CopyOnWriteArrayList<Functional_Dependency> fullFuncDeps = new CopyOnWriteArrayList<Functional_Dependency>();
		ArrayList<Functional_Dependency> minimal = new ArrayList<Functional_Dependency>(this.minimalCover);
		RDTUtils.generateFunctionalDependencies(minimal, fullFuncDeps);

		Iterator<Functional_Dependency> itr1 = fullFuncDeps.iterator();
		while(itr1.hasNext()){
			// System.out.println("	Starting:");
			// System.out.println("		" + fullFuncDeps);
			// System.out.println("		" + fullFuncDepsSet);
			// System.out.println("		" + partialFuncDeps);
			Functional_Dependency fd = itr1.next();
			ArrayList<Attribute> lo = new ArrayList<Attribute >(this.primeAttributes);
			ArrayList<Attribute> mo =new ArrayList<Attribute>(this.nonPrimeAttributes);
			fd.computeNormalForm(lo,mo, this.candidate_key);
			// System.out.println("		fd = " + fd);
			if(fd.getNormalForm() < 2){
				partialFuncDeps.add(fd);
				fullFuncDeps.remove(fd);
				// transferBin.add(fd);
               ArrayList<Attribute> arrt = new ArrayList<Attribute>(fd.getRightHandAttributes());
				RDTUtils.sortAttributes(arrt);
				Closure c = CalculateClosure.calculateClosureOf(fd.getRightHandAttributes(), this.fds);
				Iterator<Functional_Dependency> itr2 = fullFuncDeps.iterator();
				while(itr2.hasNext()){
					Functional_Dependency f = itr2.next();
					if(!f.equals(fd) && ( c.getClosure().containsAll(f.getLeftHandAttributes()) )){
						partialFuncDeps.add(f);
						fullFuncDeps.remove(f);
						// transferBin.add(f);
					}
				}
			} 
			// else {
			// 	if(!transferBin.contains(fd)){
			// 		this.fullFuncDeps.add(fd);
			// 	}
			// }
			
			// fullFuncDeps.removeAll(transferBin);
			
			// transferBin.clear();
			// System.out.println("	Ending:");
			// System.out.println("		" + this.fullFuncDeps);
			// System.out.println("		" + this.partialFuncDeps);
		}
		this.partialFuncDeps = new ArrayList<Functional_Dependency>();
		this.fullFuncDeps = new ArrayList<Functional_Dependency>();
		this.fullFuncDeps.addAll(fullFuncDeps);
		this.partialFuncDeps.addAll(partialFuncDeps);
		RDTUtils.sortFunctionalDependency(this.fullFuncDeps);
		RDTUtils.sortFunctionalDependency(this.partialFuncDeps);
		// System.out.println("	Ending:");
		// System.out.println("		" + this.fullFuncDeps);
		// System.out.println("		" + this.partialFuncDeps);
	}
  public void candy()
  {  int i=0;
	  for(Closure c: this.getMinimumKeyClosures())
	    {
			List<Attribute> comp = new ArrayList<Attribute>();
			comp=parseAttributes(c.printLeftSideAttributes());
			//ArrayList<Attribute> ar = new ArrayList<Attribute>(comp);
			candidate_key.add(new ArrayList<Attribute>(comp));
			//candidate_key.get(i).addAll(ar);
			//i++;
		}
	  //System.out.println(candidate_key);
	  //System.out.println(this.getMinimumKeyClosures());
  }
	public Relation(final String input) {
		
		this.fullFuncDeps = null;
	    this.candidate_key=new ArrayList<ArrayList<Attribute>>();
		this.fdeep="";
		this.partialFuncDeps = null;
		this.name = parseName(input);
		passedIntegrityChecks = true;
		integrityCheckErrorMsg = "";
		this.attributes = parseAttributes(input);
		this.primeAttributes = new ArrayList<>();
		this.nonPrimeAttributes = new ArrayList<>();
		this.fds = new ArrayList<>();
		this.derivedFDs = new ArrayList<>();
		this.minimalCover = new ArrayList<>();
		this.minimalCoverOutput = new ArrayList<>();
		this.mvds = new ArrayList<>();
		this.closures = new ArrayList<>();
		this.minimumKeys = new ArrayList<>();
		this.superKeys = new ArrayList<>();
		this.normalFormResults = new DetermineNormalForms(this);
	}
	
	public Relation(final String name, final List<Attribute> attributes, final List<Functional_Dependency> fds) {
		this(name, attributes, fds, null);
	}
	
	public Relation(final String name, final List<Attribute> attributes, final List<Functional_Dependency> fds, final List<MultivaluedDependency> mvds) {
		this.fdeep="";
		this.name = name;
		passedIntegrityChecks = true;
		integrityCheckErrorMsg = "";
		this.attributes = attributes;
		Collections.sort(this.attributes);
		this.primeAttributes = new ArrayList<>();
		this.nonPrimeAttributes = new ArrayList<>();
		this.fds = fds;
		this.derivedFDs = new ArrayList<>();
		this.minimalCover = new ArrayList<>();
		this.minimalCoverOutput = new ArrayList<>();
		if (mvds == null) {
			this.mvds = new ArrayList<>();
		} else {
			this.mvds = mvds;
		}
		this.closures = new ArrayList<>();
		this.minimumKeys = new ArrayList<>();
		this.superKeys = new ArrayList<>();
		this.normalFormResults = new DetermineNormalForms(this);
	}
	
	public String getName() {
		return name;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public static String parseName(final String input) {
		if (isNullOrEmpty(input)) {
			return EMPTY;
		}
		for (int i = 1; i < input.length(); i++) {
			if (input.charAt(i) == '(') {
				return input.substring(0, i);
			}
		}
		return EMPTY;
	}
	
	public List<Attribute> parseAttributes(final String input) {
		List<Attribute> result = new ArrayList<>();
		if (!schemaContainsParenthesisPair(input)) {
			return result;
		}
		int start = input.indexOf('(') + 1;
		int end = input.indexOf(')');
		String attributePortion = input.substring(start, end);
		String[] attributes = attributePortion.split(",");
		for (String attribute : attributes) {
			if (!isNullOrEmpty(attribute)) {
				Attribute a = new Attribute(attribute.trim());
				for (Attribute b : result) {
					if (b.getName().equals(a.getName())) {
						integrityCheckErrorMsg = "Duplicate attribute encountered: " + attribute.trim();
						passedIntegrityChecks = false;
						return result;
					}
				}
				result.add(a);
			}
		}
		return result;
	}
	
	public void addFunctionalDependencies(final String input) {
		String trimmedInput = input.replaceAll("\\s","");
		if (trimmedInput.isEmpty()) {
			return;
		}
		String[] FDs;
		if (trimmedInput.contains(";")) {
			FDs = trimmedInput.split(";");
		} else {
			FDs = new String[1];
			FDs[0] = trimmedInput;
		}
		for (String prefunctional : FDs) {
			Functional_Dependency fd = new Functional_Dependency(prefunctional, this);
			if (fd.getIsProperDependency()) {
				boolean duplicateCheck = false;
				for (Functional_Dependency f : fds) {
					if (f.getFDName().equals(fd.getFDName())) {
						integrityCheckErrorMsg = "Duplicate functional dependency encountered: " + fd.getFDName();
						passedIntegrityChecks = false;
						return;
					}
				}
				if (!duplicateCheck) {
					fds.add(fd);
				}
			}
			if (!RDTUtils.attributeListContainsUniqueAttributes(fd.getLeftHandAttributes()) || !RDTUtils.attributeListContainsUniqueAttributes(fd.getRightHandAttributes())) {
				integrityCheckErrorMsg = "An input functional dependency contains duplicate attributes on the same side: " + fd.getFDName();
				passedIntegrityChecks = false;
				return;
			}
		}
		Collections.sort(fds);
	}

	public void addMultivaluedDependencies(final String input) {
		String trimmedInput = input.replaceAll("\\s","");
		if (trimmedInput.isEmpty()) {
			return;
		}
		String[] MVDs;
		if (trimmedInput.contains(";")) {
			MVDs = trimmedInput.split(";");
		} else {
			MVDs = new String[1];
			MVDs[0] = trimmedInput;
		}
		for (String premultivalued : MVDs) {
			MultivaluedDependency mvd = new MultivaluedDependency(premultivalued, this);
			if (mvd.getIsProperDependency()) {
				boolean duplicateCheck = false;
				for (MultivaluedDependency m : mvds) {
					if (m.getName().equals(mvd.getName())) {
						integrityCheckErrorMsg = "Duplicate multivalued dependency encountered: " + m.getName();
						passedIntegrityChecks = false;
						return;
					}
				}
				if (!duplicateCheck) {
					mvds.add(mvd);
				}
			}
			if (!RDTUtils.attributeListContainsUniqueAttributes(mvd.getLeftHandAttributes()) || !RDTUtils.attributeListContainsUniqueAttributes(mvd.getRightHandAttributes())) {
				integrityCheckErrorMsg = "An input multivalued dependency contains duplicate attributes on the same side: " + mvd.getName();
				passedIntegrityChecks = false;
				return;
			}
		}
		Collections.sort(mvds);
	}
	
	protected void sortFDs() {
		Collections.sort(derivedFDs);
	}
	
	protected String printRelation() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(");
		for (int i = 0; i < attributes.size(); i++) {
			sb.append(attributes.get(i).getName());
			if (i < attributes.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(") having FD(s): ");
		for (int i = 0; i < fds.size(); i++) {
			sb.append(fds.get(i).getFDName());
			if (i < fds.size() - 1) {
				sb.append("; ");
			}
		}
		if (fds.isEmpty()) {
			sb.append("(none)");
		}
		sb.append(".");
		return sb.toString();
	}
	
	protected void addDerivedFunctionalDependency(final Functional_Dependency f) {
		boolean duplicateCheck = true;
		for (Functional_Dependency fd1 : derivedFDs) {
			if (fd1.getFDName().equals(f.getFDName())) {
				duplicateCheck = false;
				break;
			}
		}
		if (duplicateCheck) {
			derivedFDs.add(f);
		}
		return;
	}
	
	protected void addMinimalCoverFD(final Functional_Dependency f) {
		minimalCover.add(f);
	}
	
	protected void sortMinimalCover() {
		Collections.sort(minimalCover);
	}
	
	public List<Functional_Dependency> getInputFDs() {
		return fds;
	}
	
	public List<Functional_Dependency> getFDs() {
		return minimalCover;
	}
	
	public List<Functional_Dependency> getDerivedFDs() {
		return derivedFDs;
	}
	public ArrayList<Functional_Dependency> getPartialFunctionalDependencies(){
		return this.partialFuncDeps;
	}
	public List<MultivaluedDependency> getMVDs() {
		return mvds;
	}
	
	public List<Functional_Dependency> getMinimalCover() {
		return minimalCover;
	}
	
	public void setMinimalCoverOutput(List<String> minimalCoverOutput) {
		this.minimalCoverOutput = minimalCoverOutput;
	}
	
	public List<String> getMinimalCoverOutput() {
		return this.minimalCoverOutput;
	}
	
	protected Attribute getAttribute(String name) {
		for (Attribute a : attributes) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	public boolean hasPassedIntegrityChecks() {
		return passedIntegrityChecks;
	}
	
	protected void setPassedIntegrityChecks(final boolean check) {
		this.passedIntegrityChecks = check;
	}
	
	public String getIntegrityCheckErrorMsg() {
		return integrityCheckErrorMsg;
	}
	
	protected void setIntegrityCheckErrorMsg(final String msg) {
		this.integrityCheckErrorMsg = msg;
	}
	
	protected List<Closure> getClosures() {
		return closures;
	}
	
	protected void addClosure(final Closure closure) {
		closures.add(closure);
	}
	
	protected void sortClosures() {
		Collections.sort(closures);
	}
	
	protected void addMinimumKeyClosure(final Closure closure) {
		minimumKeys.add(closure);
	}
	
	protected List<Closure> getMinimumKeyClosures() {
		return minimumKeys;
	}
	
	protected void addSuperKeyClosure(final Closure closure) {
		superKeys.add(closure);
	}
	
	
	protected List<Closure> getSuperKeyClosures() {
		return superKeys;
	}
	
	protected void addPrimeAttribute(final Attribute attribute) {
		primeAttributes.add(attribute);
	}
	
	protected List<Attribute> getPrimeAttributes() {
		return primeAttributes;
	}
	
	protected void addNonPrimeAttribute(final Attribute attribute) {
		nonPrimeAttributes.add(attribute);
	}
	
	protected List<Attribute> getNonPrimeAttributes() {
		return nonPrimeAttributes;
	}
	
	
	public static boolean isNullOrEmpty(final String s) {
		if (s == null || s.isEmpty()) {
			return true;
		}
		return false;
	}
	protected void determineNormalForms() {
		normalFormResults.calculateNormalForms();
	}
	
	protected DetermineNormalForms getNormalFormsResults() {
		return normalFormResults;
	}
	
	
	public static boolean schemaContainsParenthesisPair(final String s) {
		if (!isNullOrEmpty(s)) {
			int openP = 0;
			int closeP = 0;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '(') {
					if (openP == 0 && closeP == 0) {
						openP++;
					} else {
						return false;
					}
				}
				if (s.charAt(i) == ')') {
					if (openP == 1 && closeP == 0) {
						closeP++;
					} else {
						return false;
					}
				}
			}
			if (openP == 1 && closeP == 1) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean schemaContainsSafeChars(final String input) {
		// Acceptable characters are all upper-case letters, commas, and parenthesis.
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c >= 'A' && c <= 'Z') || c == ',' || c == '(' || c == ')' || c == ' ') {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static boolean functionalContainsSafeChars(final String input) {
		// Acceptable characters are all upper-case letters, commas, semi-colons, hyphens, and greater-than.
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c >= 'A' && c <= 'Z') || c == ',' || c == ';' || c == '-' || c == '>' || c == ' ') {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static boolean functionalContainsArrows(final String input) {
		// Checks if all hyphens are immediately followed by greater-than and none are unmatched.
		int arrowCount = 0;
		boolean matchedHyphen = false;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (matchedHyphen) {
				if (c == '>') {
					arrowCount++;
					matchedHyphen = false;
					continue;
				}
				// If get here, means preceding char was hyphen but current char is not >
				return false;
			}
			if (c == '-') {
				if (matchedHyphen) {
					return false;
				}
				matchedHyphen = true;
				continue;
			}
		}
		if (arrowCount < 1 || matchedHyphen) {
			return false;
		}
		return true;
	}
	
	public static boolean functionalContainsAtLeastOneDependency(final String input) {
		if (functionalContainsArrows(input)) {
			String[] splitted = input.replaceAll("\\s","").split("->");
			try {
				char firstLeft = splitted[0].charAt(0);
				char firstRight = splitted[1].charAt(0);
				if (firstLeft >= 'A' && firstLeft <= 'Z' && firstRight >= 'A' && firstRight <= 'Z') {
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
}
