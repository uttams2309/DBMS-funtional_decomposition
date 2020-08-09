package myproject;
import java.util.*;
public class maindriver {

	public static void main(String[] args) {
		
		//TAKING INPUT IN STRING FORMAT 
		
		String attributes = "R(A,B,C,D,E,F)";
        String functionaldependency = "A,B->C;D,C->A,E;E->F";
		String multivalueddependencies = "";
		
		// FORMING RELATION WITH THE GIVEN INPUT WITH APPROPRIATE PARSE METHODS IMPLEMENTED IN RELATION CLASS 
		
		Relation relation = new Relation(attributes);
		relation.addFunctionalDependencies(functionaldependency);
		relation.addMultivaluedDependencies(multivalueddependencies);
		
		//PRINT THE COMPLETE INPUT RELATION WITH PROVIDED FUNCTIONAL DEPENDENCIES :
		
		System.out.println("INPUT RELATION IS :" + relation.printRelation());
		System.out.println("----------------------------------------------------------------------");
		
		
		//CALCULATE CLOSURE OF EVERY POSSIBLE SUBSET OF GIVEN LIST OF ATTRIBUTES AND PROVIDED LIST OF FUNCTIONAL DEPENDENCY 
		
		CalculateClosure.improvedCalculateClosures(relation);
		
		//PRINT THE COMPLETE CLOSURE 
		
		for (Closure c : relation.getClosures())
		  {
			System.out.println(c.printCompleteClosure());
			
			
		  }
		System.out.println("-----------------------------------------------------------------------");
		
		// GET LIST OF CANDIDATE KEYS - OR MINIMUM KEY CLOSURES POSSIBLE FROM THE SET OF RIGHT HAND ATTRIBUTES IN THE LIST OF CLOSURES ABOVE 
		
		System.out.println("LIST OF CANDIDATE KEY(S)  :");
		for (Closure c : relation.getMinimumKeyClosures()) 
		 {
			System.out.println(c.printLeftSideAttributes());
		 }
		System.out.println("-----------------------------------------------------------------------");
		
		// REDUCING THE LIST OF FUNCTIONAL DEPENDENCY INPUT AND GETTING THE MINIMAL COVER 
		
		MinimalFDCover.determineMinimalCover(relation);
		System.out.println("MINIMAL COVER/REDUSED FD(s) : ");
		for (Functional_Dependency fd : relation.getMinimalCover()) {
			System.out.println(fd.getFDName());
		}
		System.out.println("-------------------------------------------------------------------------------");
		
		//DETERMINING THE NORMAL FORM IN WHICH THE RELATION IS 
		boolean query = false;
		relation.determineNormalForms();
        DetermineNormalForms normalForms = relation.getNormalFormsResults();
        if(!normalForms.isSecondNormalForm && !query)
        {        query=true;
                  System.out.println("GIVEN RELATION IS IN 1NF AND NEED TO CONVERT TO 2NF ");
                  System.out.println("---------------------------------1NF TO 2NF CONVERSION----------------------------------------------------"); 
                  calculate2nfdecompositon call = new calculate2nfdecompositon(relation);
                  call.decompose();
                  /*relation.candy(); 
                  cal2nfdecomposition cal;
                  for(Relation r: cal2nfdecomposition.decomposeInto2NFScheme(relation))
                  {
                  	System.out.println(r.printRelation());
                  }*/
                                                                                                                                               
        }

        if(!normalForms.isThirdNormalForm && !query)
		                                {          query = true;
        	                                        System.out.println(" GIVEN RELATION IS IN SECOND  NORMAL FORM , NEED TO CONVERT TO THIRD NORMAL FORM ");
        	                                       System.out.println("----------------------------2NF TO 3NF CONVERSION------------------------------------------------"); 
        	                                        Calculate3NFDecomposition threeNF = new Calculate3NFDecomposition(relation);
        	                                		threeNF.decompose(true);
        	                                		List<Relation> output3nfrelation = threeNF.outputRelations;
        	                                		for(Relation r: output3nfrelation)
        	                                		    {
        	                                			        System.out.println(r.printRelation());
        	                                			        //for(Functional_Dependency f : r.fds)
        	                                			        if(!r.fds.isEmpty())
        	                                			        {
        	                                			        	System.out.println(r.fds.get(0).getLeftHandAttributes()+ " IS THE CANDIDATE KEY FOR ABOVE NEW RELATION FORMED" );
        	                                			        	
        	                                			        }
        	                                			        if(r.fds.isEmpty()) System.out.println(r.attributes+"IS THE CANDIDATE KEY FOR ABOVE NEW RELATION FORMED ");
        	                                			       System.out.println("----------------------------------------------------------------------------------------------------------");
        	                                		    }
		                                }
        if(!normalForms.isBCNF&& !query)
		                                {           query= true;
        	                                         System.out.println(" GIVEN RELATION IS IN THIRD  NORMAL FORM , NEED TO CONVERT TO BCNF ");
        	                                         //
        	                                         
        	                                          System.out.println("------------------------------------ 3NF TO BCNF CONVERSION -------------------------------------------------");
        	                                         Calculate3NFDecomposition threeNF = new Calculate3NFDecomposition(relation);
         	                                		threeNF.decompose(true);
        	                                         CalculateBCNFDecomposition BCNF = new CalculateBCNFDecomposition(relation);
        	                                         BCNF.BCNFDecomposeMethodWithout3NF();
        	                                         List<Relation>  outputBCNFrelation = BCNF.pureBCNFDecomposedRs;
        	                                         List<Relation>  outputBCNFrelation0 = BCNF.getBcnfDecomposedWithDuplicates();
        	                                         for(Relation r: outputBCNFrelation)
     	                                		    {
     	                                			        System.out.println(r.printRelation());
     	                                			        //for(Functional_Dependency f : r.fds)
     	                                			        if(!r.fds.isEmpty())
     	                                			        {
     	                                			        	System.out.println(r.fds.get(0).getLeftHandAttributes()+ " IS THE CANDIDATE KEY FOR ABOVE NEW RELATION FORMED" );
     	                                			        	
     	                                			        }
     	                                			        if(r.fds.isEmpty()) System.out.println(r.attributes+"IS THE CANDIDATE KEY FOR ABOVE NEW RELATION FORMED ");
     	                                			       System.out.println("----------------------------------------------------------------------------------------------------------");
     	                                		    }
		                               
        	                                         
		                                 }
        if(normalForms.isBCNF&&!query)
		                                  {          query = true;
        	                                         System.out.println("GIVEN INPUT RELATION WITH ITS SET OF FDs IS ALREADY IN BCNF : THEREFORE NO DECOMPOSITION IS REQUIRED ");
        	                                         // NO DECOMPOSITION REQUIRED 
		                                  }
      
	}

}
