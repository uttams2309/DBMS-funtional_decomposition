package myproject;
import java.util.*;

public class calculate2nfdecompositon extends CalculateDecomposition {
	
public calculate2nfdecompositon(Relation inputRelation) {
		super(inputRelation);
		// TODO Auto-generated constructor stub
	}
protected void decompose()
{  

	boolean entry = false;
    String fundepend ="";

    List<Relation> Workingrelation = new ArrayList<>();
	List<Functional_Dependency> lexy = new ArrayList<>();
	setOutputMsg("Decomposing input relation into 2NF relations");
	int counter =0;
	List<Functional_Dependency> rip = new ArrayList<>();
	List<String> rope = new ArrayList<>();
	List<Functional_Dependency> sp =new ArrayList<>();
	List<Functional_Dependency> speed =new ArrayList<>();
	
	List<Closure> lp=inputRelation.getMinimumKeyClosures();
	sp=getInputRelation().getMinimalCover();

	for(Functional_Dependency f:sp)
	{  
		for(Closure c: lp)
		{   
			
			
		//System.out.println(c.printLeftSideAttributes());
			//if((c.printLeftSideAttributes().indexOf(f.getLeftHandNameKey())==-1))
			{
				//rope.add(f.getLeftHandNameKey());
			}
		}
	}
	//System.out.println(rope);
	for(Functional_Dependency fd : sp)
	{  
		if(speed.contains(fd))continue;
		int count=0;
		List<Attribute> decomposed_attribute =new ArrayList<>();
		decomposed_attribute.addAll(fd.getLeftHandAttributes());
		decomposed_attribute.addAll(fd.getRightHandAttributes());
		List<Functional_Dependency> decomposedfd = new ArrayList<>();
		//loop for set of candidate keys in the list for getting partial de
		for(Closure c: lp)
		{
			List<Attribute>comparison = new ArrayList<>();
			comparison = parseAttributes(c.printLeftSideAttributes());
			   
			if(comparison.equals(fd.getLeftHandAttributes()))
				{   
					fundepend= fundepend +fd.getFDName()+';';
					//System.out.println(fundepend);
					count=0;break;
				} 
				
			    count =1;	 			
				//System.out.println(rip);
				//System.out.println(comparison);
				//System.out.println(fd.getLeftHandAttributes());
				//System.out.println(count);
		    	//System.out.println(counter1);
				//if(counter1 ==comparison.size()){count=1;break;}
		} 
		
		System.out.println();
		for(int i=0;i<rip.size();i++)
		{   
		if(fd.getRightHandAttributes().containsAll(rip.get(i).getLeftHandAttributes()))
			{
				decomposedfd.add(rip.get(i));
				//decomposed_attribute.addAll(rip.get(i).getLeftHandAttributes());
				decomposed_attribute.addAll(rip.get(i).getRightHandAttributes());
			  speed.add(rip.get(i));
		        
				rip.remove(i);
			
				
			}
			}
		//System.out.println();
		
		decomposedfd.add(fd);
		if(count==0)lexy=decomposedfd;
		if(count==1 )
		{
			Relation new2nfrelation = new Relation( getInputRelation().getName()+counter++,decomposed_attribute ,decomposedfd);
			
			Workingrelation.add(new2nfrelation);
			System.out.print(fd.getLeftHandAttributes()+" is the candidate key of new relation ");
			System.out.println(new2nfrelation.printRelation());//System.out.println();
			//inputRelation.derivedFDs.remove(fd);
			//inputRelation.minimalCover.remove(fd);
			//inputRelation.fds.remove(fd);
			//inputRelation.attributes.remove(fd.getRightHandAttributes());
			//for(int i=0;i<fd.getRightHandAttributes().size();i++)
			//inputRelation.attributes.remove(fd.getRightHandAttributes().get(i));
			//entry=true;
			for(int k=0;k<decomposedfd.size();k++)
			{
				Functional_Dependency g = decomposedfd.get(k);
				
				for(int i=0;i<g.getRightHandAttributes().size();i++)
					inputRelation.attributes.remove(g.getRightHandAttributes().get(i));
					
			}
		}
		
			
	}
	System.out.println();
	Relation newupdate = new Relation("R"+counter,inputRelation.attributes,lexy);
	if(newupdate.fds.isEmpty()) System.out.print("all attributes in R" +counter+" are candidate key(s)");
	System.out.print(newupdate.printRelation());
	
	//System.out.println(manmera.printRelation());
    
}

private List<Attribute> parseAttributes(String input) {
	List<Attribute> result = new ArrayList<>();
	int start = 0;
	int end = input.length();
	String attributePortion = input.substring(start, end);
	String[] attributes = attributePortion.split(",");
	for (String attribute : attributes)
	{
		Attribute a = new Attribute(attribute.trim());
			result.add(a);
		}
	return result;
}
}

