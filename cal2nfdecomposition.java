package myproject;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class cal2nfdecomposition
 {
        public static ArrayList<Relation> decomposeInto2NFScheme(Relation relation)
        {
		     // System.out.println("\n->Decomposing Into 2NF Relations");
        	
        	{
        		ArrayList<Functional_Dependency> relationFDs = new ArrayList<Functional_Dependency>();
        		relationFDs.addAll(relation.getMinimalCover());
        		// System.out.println("Relation FDs are: " + relationFDs + "\n");


        		ArrayList<Relation> twoNFRelations = new ArrayList<Relation>();

        		List<Functional_Dependency> fullFuncDeps = relation.fds;
			
        		CopyOnWriteArrayList<Functional_Dependency> partialFuncDeps = new CopyOnWriteArrayList<Functional_Dependency>();
        		RDTUtils.generateFunctionalDependencies(relation.getPartialFunctionalDependencies(), partialFuncDeps);
                int counting =0;int zs=0;
        		Set<Attribute> fullFDAttributes = new HashSet<Attribute>();
        		for(Functional_Dependency f : fullFuncDeps)
        		{
        			fullFDAttributes.addAll(f.getLeftHandAttributes());
        			fullFDAttributes.addAll(f.getRightHandAttributes());
        		}

        		Set<Attribute> twoNFAttr = new HashSet<Attribute>();
        		ArrayList<Functional_Dependency> twoNFFDs = new ArrayList<Functional_Dependency>();
           
        		Iterator<Functional_Dependency> itr1 = partialFuncDeps.iterator();
        		while(itr1.hasNext())
        			{
        					Functional_Dependency fd = itr1.next();
        					if(fd.getNormalForm() < 2)
        					{
        							// System.out.println("fd = " + fd);
        							Closure c = CalculateClosure.calculateClosureOf(fd.getLeftHandAttributes(), partialFuncDeps);
        							twoNFAttr.addAll(c.getClosure());
        							twoNFFDs.add(fd);
        							// System.out.println("2NF FDs: " + twoNFFDs);
        							// System.out.println("I'm creating relation with attributes: " +twoNFAttr + " and FDs: " + twoNFFDs);
        							List<Attribute> xx = new ArrayList<Attribute>();
        							xx.addAll(twoNFAttr);
        							counting++;
        							List<Functional_Dependency> tata = new ArrayList<Functional_Dependency>(twoNFFDs); 
        							Relation rY = new Relation("ron"+counting,xx, tata);
        							for(Attribute a : rY.getNonPrimeAttributes())
        							{
        								if(c.getClosure().contains(a))
        								{
        									fullFDAttributes.remove(a);
        								}
        							}
        							Iterator<Functional_Dependency> itr2 = partialFuncDeps.iterator();
        							while(itr2.hasNext())
        							{
        								Functional_Dependency f = itr2.next();
        								// System.out.println("f = " + f);
        								if(!fd.equals(f) && c.getClosure().containsAll(f.getLeftHandAttributes()))
        								{
        									// Set<Attribute> closureR = new HashSet<Attribute>(c.getRightSide());
        									// System.out.println("Closure R: " + closureR);
        									// System.out.println("FD L: " + f.getLeftSideAttributes());
        									// closureR.retainAll(f.getLeftSideAttributes());
        									// System.out.println("Closure R After Removing: " + closureR);
        									// if(closureR.isEmpty()){
        									twoNFFDs.add(f);
        									partialFuncDeps.remove(f);
        									// System.out.println("2NF FDs: " + twoNFFDs);
        									// 	}
        								}
        							}
        							// System.out.println("I'm creating relation with attributes: " +twoNFAttr + " and FDs: " + twoNFFDs);
        							List<Attribute> xxx = new ArrayList<Attribute>();
        							xx.addAll(twoNFAttr);
        							zs++;
        							List<Functional_Dependency> tatat = new ArrayList<Functional_Dependency>(twoNFFDs); 
        							
        							
        							
        							twoNFRelations.add(new Relation("rel"+zs,xxx, new ArrayList<Functional_Dependency>(twoNFFDs)));
        							// twoNFRelations.add(new Relation(twoNFAttr, twoNFFDs));
        							partialFuncDeps.removeAll(twoNFFDs);
        							// System.out.println(twoNFFDs + "	Before removing from remaltionFDs " +  relationFDs);
        							relationFDs.removeAll(twoNFFDs);
        							// System.out.println(twoNFFDs + "	After removing from remaltionFDs " +  relationFDs);
        					}
        					twoNFAttr.clear();
        					twoNFFDs.clear();
        			}

        		if(!fullFDAttributes.isEmpty())
        		{
        			// System.out.println("I'm creating relation with attributes: " +fullFDAttributes + " and FDs: " + relationFDs);
        			Iterator<Functional_Dependency> itr = relationFDs.iterator();
        			while(itr.hasNext())
        			{
        				Functional_Dependency f = itr.next();
        				// if(!fullFDAttributes.containsAll(f.getLeftSideAttributes()) && !fullFDAttributes.containsAll(f.getRightSideAttributes())){
        				// 	itr.remove();
        				// }
        				fullFDAttributes.addAll(f.getLeftHandAttributes());
        				fullFDAttributes.addAll(f.getRightHandAttributes());
        				// f.getRightSideAttributes().removeAll(fdRightSideAttributes);
        			}
        			// System.out.println("I'm creating relation with attributes: " +fullFDAttributes + " and FDs: " + relationFDs);
        			List<Attribute> atta = new ArrayList<Attribute>();
        			atta.addAll(fullFDAttributes);
        			twoNFRelations.add(new Relation("lalala",atta, new ArrayList<Functional_Dependency>(relationFDs)));
        		}
        		return twoNFRelations;
		}
        	//return null;
	}
 }

	