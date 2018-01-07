package edu.upenn.cis.citation.Corecover;
/* 
 * Database.java
 * -------------
 * $Id: Database.java,v 1.8 2000/11/17 05:53:49 chenli Exp $
 */

import java.util.*;
import edu.upenn.cis.citation.views.Single_view;

public class Database {
  
  public HashSet tuples = null;

  HashMap<String, String> subgoal_name_mappings = null;
  
  Database(HashSet tuples, HashMap<String, String> subgoal_name_mappings) {
    this.tuples = tuples;
    this.subgoal_name_mappings = subgoal_name_mappings;
  }
  
  /**
   * evaluates a query on the db and returns the results.
   */
  public Relation execQuery(Query query) {

    Relation sr = new Relation("sr");
    
    int num = 0;
    
    for (int i = 0; i < query.getSubgoalNum(); i ++ ) {
      Subgoal subgoal = query.getSubgoal(i);
      
//      System.out.println("subgoal:::" + subgoal);
      
      // processes the subgoal on the db
      Relation subgoalRel = processSubgoal(subgoal, query);
      
      if(subgoalRel.tuples.size() == 0)
    	  continue;
      
//      System.out.println("tuples:::" + subgoalRel.tuples);
      
//      System.out.println("subgoal_relation:::" + subgoalRel);
//      
//      System.out.println("subgoal_relation_schema:::" + subgoalRel.getSchema());

      // process the join
      sr = join(sr, subgoalRel, query.getName(), num);

      Vector usefulArgs = query.getUsefulArgs(i);
      sr = sr.project(usefulArgs);
      
      num ++;
      
    }

//    System.out.println(sr.getTuples());
    // set the query of the result, and compute the head of each tuple
//    sr.setQuery(query);  
    return sr;
  }
  
  public Relation execQuery(Single_view view) {

    Relation sr = new Relation("sr");
    
    int num = 0;
    
    for (int i = 0; i < view.subgoals.size(); i ++ ) {
      Subgoal subgoal = view.subgoals.get(i);
      
//      System.out.println("subgoal:::" + subgoal);
      
      // processes the subgoal on the db
      Relation subgoalRel = processSubgoal(subgoal, view.subgoal_name_mappings);
      
      if(subgoalRel.tuples.size() == 0)
          continue;
      
//      System.out.println("tuples:::" + subgoalRel.tuples);
      
//      System.out.println("subgoal_relation:::" + subgoalRel);
//      
//      System.out.println("subgoal_relation_schema:::" + subgoalRel.getSchema());

      // process the join
      sr = join(sr, subgoalRel, view.view_name, num);

      Vector usefulArgs = view.getUsefulArgs(i);
      sr = sr.project(usefulArgs);
      
      num ++;
      
    }

//    System.out.println(sr.getTuples());
    // set the query of the result, and compute the head of each tuple
    sr.setQuery(view);  
    return sr;
  }
  
  /**
   * processes one subgoal on a database
   */
  Relation processSubgoal(Subgoal subgoal) {
    String resName = subgoal.getName() + "_tmp";  

    // computes the schema
    Vector resSchema     = new Vector();
    Vector subgoalSchema = subgoal.getArgs();
    for (int i = 0; i < subgoalSchema.size(); i ++) {
      Argument arg = (Argument) subgoalSchema.elementAt(i);
      if (arg.isConst()) {
	continue;    // ignores constants
      }
      if (resSchema.contains(arg)) {
	continue; // removes duplicates
      }
      resSchema.add(arg);
    }

    // finds tuples that can be unified with this subgoal
    HashSet resTuples = new HashSet();
    for (Iterator iter = tuples.iterator(); iter.hasNext();) {
      Tuple tuple = (Tuple) iter.next();

      if (!subgoal.getName().equals(subgoal_name_mappings.get(tuple.getName()))) 
	continue; // should have the same name

      Vector tupleArgs   = tuple.getArgs();
      Vector subgoalArgs = subgoal.getArgs();
      if (tupleArgs.size() != subgoalArgs.size()) 
	UserLib.myerror("Database.processSubgoal(), wrong args!");

      // tries to unify this subgoal with the tuple
      Vector  resTupleArgs = new Vector();
      boolean unifiable = true;
      Mapping phi       = new Mapping();
      Mapping phi_str = new Mapping();
      for (int i = 0; i < subgoalArgs.size(); i ++) {
	Argument tupleArg   = (Argument) tupleArgs.elementAt(i);
	Argument subgoalArg = (Argument) subgoalArgs.elementAt(i);

	// cannot map a constant to a different constant 
	if (subgoalArg.isConst()) {
	  if (!subgoalArg.equals(tupleArg)) {
	    unifiable = false;
	    break;
	  } 
	  else continue; // ignores constants in the final tuple
	}

	// the subgoalArg is a variable
	
	Argument image = phi.apply(subgoalArg);
	if (image == null) { // not mapped.  adds the pair
	  phi.put(subgoalArg, tupleArg);
	  
	  phi_str.put(subgoalArg.name, tupleArg.name);
	  
	  resTupleArgs.add(tupleArg);  // adds the first occurance to the tuple
	  continue;
	}

	if (image.equals(tupleArg)) continue;
	
	unifiable = false;  // mapped to a different value
	break;
      }

      if (unifiable) {
	HashMap mapSubgoals = new HashMap();
	mapSubgoals.put(subgoal, tuple.getSubgoal()); // subgoal mapped
	resTuples.add(new Tuple(subgoal.getName(), 
				resTupleArgs, phi, phi_str, mapSubgoals));
      }
    }
    
    return new Relation(resName, resSchema, resTuples);
  }


  
  Relation processSubgoal(Subgoal subgoal, Query view) {
	    String resName = subgoal.getName() + "_tmp";  

	    // computes the schema
	    Vector resSchema     = new Vector();
	    Vector subgoalSchema = subgoal.getArgs();
	    for (int i = 0; i < subgoalSchema.size(); i ++) {
	      Argument arg = (Argument) subgoalSchema.elementAt(i);
	      if (arg.isConst()) {
		continue;    // ignores constants
	      }
	      if (resSchema.contains(arg)) {
		continue; // removes duplicates
	      }
	      resSchema.add(arg);
	    }

	    
	    
	    // finds tuples that can be unified with this subgoal
	    HashSet resTuples = new HashSet();
	    for (Iterator iter = tuples.iterator(); iter.hasNext();) {
	      Tuple tuple = (Tuple) iter.next();

	      if (!view.subgoal_name_mapping.get(subgoal.getName()).equals(subgoal_name_mappings.get(tuple.getName()))) 
		continue; // should have the same name

	      Vector tupleArgs   = tuple.getArgs();
	      Vector subgoalArgs = subgoal.getArgs();
	      if (tupleArgs.size() != subgoalArgs.size()) 
		UserLib.myerror("Database.processSubgoal(), wrong args!");

	      // tries to unify this subgoal with the tuple
	      Vector  resTupleArgs = new Vector();
	      boolean unifiable = true;
	      Mapping phi       = new Mapping();
	      Mapping phi_str = new Mapping();
	      for (int i = 0; i < subgoalArgs.size(); i ++) {
		Argument tupleArg   = (Argument) tupleArgs.elementAt(i);
		Argument subgoalArg = (Argument) subgoalArgs.elementAt(i);

		// cannot map a constant to a different constant 
		if (subgoalArg.isConst()) {
		  if (!subgoalArg.equals(tupleArg)) {
		    unifiable = false;
		    break;
		  } 
		  else continue; // ignores constants in the final tuple
		}

		// the subgoalArg is a variable
		
		Argument image = (Argument) phi.apply(subgoalArg);
		if (image == null) { // not mapped.  adds the pair
		  phi.put(subgoalArg, tupleArg);
		  
		  phi_str.put(subgoalArg.name, tupleArg.name);
		  
		  resTupleArgs.add(tupleArg);  // adds the first occurance to the tuple
		  continue;
		}

		if (image.equals(tupleArg)) continue;
		
		unifiable = false;  // mapped to a different value
		break;
	      }

	      if (unifiable) {
		HashMap mapSubgoals = new HashMap();
		mapSubgoals.put(subgoal, tuple.getSubgoal()); // subgoal mapped
		resTuples.add(new Tuple(subgoal.getName(), 
					resTupleArgs, phi, phi_str, mapSubgoals));
	      }
	    }
	    
	    return new Relation(resName, resSchema, resTuples);
	  }
  
  Relation processSubgoal(Subgoal subgoal, HashMap<String, String> view_subgoal_mappings) {
    String resName = subgoal.getName() + "_tmp";  

    // computes the schema
    Vector resSchema     = new Vector();
    Vector subgoalSchema = subgoal.getArgs();
    for (int i = 0; i < subgoalSchema.size(); i ++) {
      Argument arg = (Argument) subgoalSchema.elementAt(i);
      if (arg.isConst()) {
    continue;    // ignores constants
      }
      if (resSchema.contains(arg)) {
    continue; // removes duplicates
      }
      resSchema.add(arg);
    }

    
    
    // finds tuples that can be unified with this subgoal
    HashSet resTuples = new HashSet();
    for (Iterator iter = tuples.iterator(); iter.hasNext();) {
      Tuple tuple = (Tuple) iter.next();

      if (!view_subgoal_mappings.get(subgoal.getName()).equals(subgoal_name_mappings.get(tuple.getName()))) 
    continue; // should have the same name

      Vector tupleArgs   = tuple.getArgs();
      Vector subgoalArgs = subgoal.getArgs();
      if (tupleArgs.size() != subgoalArgs.size()) 
    UserLib.myerror("Database.processSubgoal(), wrong args!");

      // tries to unify this subgoal with the tuple
      Vector  resTupleArgs = new Vector();
      boolean unifiable = true;
      Mapping phi       = new Mapping();
      Mapping phi_str = new Mapping();
      for (int i = 0; i < subgoalArgs.size(); i ++) {
    Argument tupleArg   = (Argument) tupleArgs.elementAt(i);
    Argument subgoalArg = (Argument) subgoalArgs.elementAt(i);

    // cannot map a constant to a different constant 
    if (subgoalArg.isConst()) {
      if (!subgoalArg.equals(tupleArg)) {
        unifiable = false;
        break;
      } 
      else continue; // ignores constants in the final tuple
    }

    // the subgoalArg is a variable
    
    Argument image = (Argument) phi.apply(subgoalArg);
    if (image == null) { // not mapped.  adds the pair
      phi.put(subgoalArg, tupleArg);
      
      phi_str.put(subgoalArg.name, tupleArg.name);
      
      resTupleArgs.add(tupleArg);  // adds the first occurance to the tuple
      continue;
    }

    if (image.equals(tupleArg)) continue;
    
    unifiable = false;  // mapped to a different value
    break;
      }

      if (unifiable) {
    HashMap mapSubgoals = new HashMap();
    mapSubgoals.put(subgoal, tuple.getSubgoal()); // subgoal mapped
    resTuples.add(new Tuple(subgoal.getName(), 
                resTupleArgs, phi, phi_str, mapSubgoals));
      }
    }
    
    return new Relation(resName, resSchema, resTuples);
  }


  /**
   * processes the join of two relations
   */
  Relation join(Relation rel1, Relation rel2, String resName) {
    
    // returns the other relation if one relation's schema is empty
    if (rel1.getAttrNum() == 0) return rel2;
    if (rel2.getAttrNum() == 0) return rel1;

    //System.out.println("rel1 = " + rel1 );
    //System.out.println("rel2 = " + rel2 );

    // computes the schema
    Vector schema1   = rel1.getSchema();
    Vector schema2   = rel2.getSchema();
    Vector resSchema = new Vector();

    for (int i = 0; i < schema1.size(); i ++) {
      Argument arg = (Argument) schema1.elementAt(i);
      // removes duplicates and useless args
      if (!resSchema.contains(arg))
	resSchema.add(arg);
    }

    for (int i = 0; i < schema2.size(); i ++) {
      Argument arg = (Argument) schema2.elementAt(i);
      // removes duplicates and useless args
      if (!resSchema.contains(arg))
	resSchema.add(arg);
    }

    // computes the tuples using a loop join
    HashSet resTuples = new HashSet();
    HashSet tuples1  = rel1.getTuples();
    HashSet tuples2  = rel2.getTuples();
    for (Iterator iter1 = tuples1.iterator(); iter1.hasNext();) {
      Tuple tuple1 = (Tuple) iter1.next();

      for (Iterator iter2 = tuples2.iterator(); iter2.hasNext();) {
	  Tuple tuple2 = (Tuple) iter2.next();

	// joins two tuples
	Tuple tuple = join(tuple1, schema1, tuple2, schema2, resName);
	if (tuple != null) {
	    resTuples.add(tuple);
	}
      }
    }
    
    return new Relation(resName, resSchema, resTuples);
  }

  Relation join(Relation rel1, Relation rel2, String resName, int seq) {
	    
	    // returns the other relation if one relation's schema is empty
	    if (seq == 0 && rel1.getAttrNum() == 0) return rel2;
	    if (seq == 0 && rel2.getAttrNum() == 0) return rel1;

	    //System.out.println("rel1 = " + rel1 );
	    //System.out.println("rel2 = " + rel2 );

	    // computes the schema
	    Vector schema1   = rel1.getSchema();
	    Vector schema2   = rel2.getSchema();
	    Vector resSchema = new Vector();

	    for (int i = 0; i < schema1.size(); i ++) {
	      Argument arg = (Argument) schema1.elementAt(i);
	      // removes duplicates and useless args
	      if (!resSchema.contains(arg))
		resSchema.add(arg);
	    }

	    for (int i = 0; i < schema2.size(); i ++) {
	      Argument arg = (Argument) schema2.elementAt(i);
	      // removes duplicates and useless args
	      if (!resSchema.contains(arg))
		resSchema.add(arg);
	    }

	    // computes the tuples using a loop join
	    HashSet resTuples = new HashSet();
	    HashSet tuples1  = rel1.getTuples();
	    HashSet tuples2  = rel2.getTuples();
	    
//	    resTuples.addAll(tuples1);
//	    
//	    resTuples.addAll(tuples2);
	    
	    for (Iterator iter1 = tuples1.iterator(); iter1.hasNext();) {
	      Tuple tuple1 = (Tuple) iter1.next();

	      for (Iterator iter2 = tuples2.iterator(); iter2.hasNext();) {
		  Tuple tuple2 = (Tuple) iter2.next();

		// joins two tuples
		Tuple tuple = join(tuple1, schema1, tuple2, schema2, resName);
		if (tuple != null) {
		    resTuples.add(tuple);
		}
	      }
	    }
	    
	    return new Relation(resName, resSchema, resTuples);
	  }

  
  /**
   * Joins two given tuples with their schemas, 
   */
  Tuple join(Tuple tuple1, Vector schema1, 
	     Tuple tuple2, Vector schema2, String resName) {
    
    Vector resTupleArgs = new Vector();
    Vector tuple1Args = tuple1.getArgs();
    Vector tuple2Args = tuple2.getArgs();
    
    if (tuple1Args.size() != schema1.size() || 
	tuple2Args.size() != schema2.size()) {
      System.out.println("tuple1Args = " + tuple1Args +
			 "\nschema1 = " + schema1 +
			 "\ntuple2Args = " + tuple2Args +
			 "\nschema2 = " + schema2);
      UserLib.myerror("Database.join(), wrong args!");
    }
    
    // processes tuple 1
    for (int i = 0; i < schema1.size(); i ++) {
      Argument argSchema = (Argument) schema1.elementAt(i);
      Argument argValue  = (Argument) tuple1Args.elementAt(i);
      
      // checks if we have an equality comparison
      int pos = schema2.indexOf(argSchema);
      if (pos == -1) {
	resTupleArgs.add(argValue);
	continue;
      }
      
      // checks if the equality condition is satisfied
      if (!argValue.equals(tuple2Args.elementAt(pos)))
	return null;

      resTupleArgs.add(argValue);
    }

    // processes tuple 2
    for (int i = 0; i < schema2.size(); i ++) {
      Argument argSchema = (Argument) schema2.elementAt(i);
      Argument argValue  = (Argument) tuple2Args.elementAt(i);
      
      // checks if we have an equality comparison
      int pos = schema1.indexOf(argSchema);
      if (pos == -1) {
	resTupleArgs.add(argValue);
	continue;
      }
      
      // there is an equality comparison, double checks they are equal
      if (!argValue.equals(tuple1Args.elementAt(pos)))
	UserLib.myerror("Database.join(), wrong args!");
    }

    // union the two phi's
    HashMap map1 = (HashMap) tuple1.getMapping().getMap();
    HashMap map2 = (HashMap) tuple2.getMapping().getMap();
    HashMap map  = (HashMap) map1.clone();
    map.putAll(map2);
    Mapping phi = new Mapping(map);

    // union the two phi's
    HashMap map1_str = (HashMap) tuple1.getMapping_str().getMap();
    HashMap map2_str = (HashMap) tuple2.getMapping_str().getMap();
    HashMap map_str  = (HashMap) map1_str.clone();
    map_str.putAll(map2_str);
    Mapping phi_str = new Mapping(map_str);
    
    // union the two mapSubgoals's
    HashMap mapSubgoals1 = tuple1.getMapSubgoals();
    HashMap mapSubgoals2 = tuple2.getMapSubgoals();
    HashMap mapSubgoals  = (HashMap) mapSubgoals1.clone();
    mapSubgoals.putAll(mapSubgoals2);

    return new Tuple(resName, resTupleArgs, phi, phi_str, mapSubgoals);
  }

  public String toString() {
    if (tuples == null) 
      return ("empty");

    StringBuffer result = new StringBuffer();
    for (Iterator iter = tuples.iterator(); iter.hasNext();) {
      Tuple tuple = (Tuple) iter.next();
      result.append(tuple.toString());
      if (iter.hasNext())
	result.append(", ");
    }

    return result.toString();
  }
}
