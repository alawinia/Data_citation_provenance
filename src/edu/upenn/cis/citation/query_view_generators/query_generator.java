package edu.upenn.cis.citation.query_view_generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import java.util.Random;
import java.util.Set;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.init.init;

public class query_generator {
	
	
	
	public static String [] citatable_tables = {"ligand", "gpcr","object", "family", "introduction"};
	
	static HashMap<String, HashMap<String, String>> parameterizable_attri_type = new HashMap<String, HashMap<String, String>>();
	
	public static HashMap<String, Vector<String>> parameterizable_attri = new HashMap<String, Vector<String>>();


	static HashMap<string_array, Vector<string_array> > joinable_attribute_lists = new HashMap<string_array, Vector<string_array>>();
	
	static HashMap<String, Vector<String> > foreign_key_relations = new HashMap<String, Vector<String> >();
	
	static String [] available_data_type = {"integer", "text", "boolean", "character varying", "double precision", "real", "character varying", "bigint"};
	
	static Vector<String> available_data_type_vec = new Vector<String>();
	
	static Vector<String> comparable_data_type_vec = new Vector<String>();
	
	static String []comparable_data_type = {"integer", "double precision", "real", "bigint"};
	
	static String [] maximable_data_types = {"integer", "text", "character varying", "double precision", "real", "bigint", "text"};
	
	static double citatble_rate = 0.3;
	
	static double local_predicates_rate = 0.9;
	
	static double lambda2head_rate = 0.3;
	
	static double head_var_rate = .5;
	
	static double global_predicate_rate = 1;
	
	static double global_predicate_ratio = 0.7;
	
	static int view_nums = 100;
	
	public static int query_result_size = 100000;

	
	static HashMap<String, String> relation_primary_key_mapping = new HashMap<String, String>();
	
	public static HashMap<String, Vector<Integer>> relation_primary_key_ranges = new HashMap<String, Vector<Integer>>();
	
	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(init.db_url, init.usr_name , init.passwd);

	    
//	    get_joinable_relations(c, pst);
//	    
//	    int size = 10;
//	    	    
//	    Query query = gen_query(size, c, pst);
//	    
//	    System.out.println(query);
	    
	    init_parameterizable_attributes(c, pst);
	    
	    
	    build_relation_primary_key_mapping(c, pst);
        
        Query query = generate_query(3, c, pst);
	    
	    c.close();
	    	    
	    
	}
	
	public static void init_parameterizable_attributes(Connection c, PreparedStatement pst) throws SQLException
	{
		
		int col_num = 0;
		
		for(int i = 0; i<citatable_tables.length; i++)
		{
			String attr_query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + citatable_tables[i] + "'";
			
			pst = c.prepareStatement(attr_query);
			
			ResultSet rs = pst.executeQuery();
			
			HashMap<String, String> cols = new HashMap<String, String>();
			
			while(rs.next())
			{
				String attr_name = rs.getString(1);
				
				String data_type = rs.getString(2);
				
				String length_query = "select max(char_length(cast (" + attr_name + " as text))) from " + citatable_tables[i];
				
//				System.out.println(length_query);
				
				pst = c.prepareStatement(length_query);
				
				ResultSet r = pst.executeQuery();
				
				if(r.next())
				{
					int length = r.getInt(1);
					
					if(length < 20)
					{
						col_num ++;
						
						cols.put(attr_name, data_type);
					}
					
//					System.out.println(citatable_tables[i] + " " + attr_name + " " + length);
				}
			}
			
			parameterizable_attri_type.put(citatable_tables[i], cols);
			
			Vector<String> attributes = new Vector<String>();
			
			attributes.addAll(cols.keySet());
			
			parameterizable_attri.put(citatable_tables[i], attributes);
		}
		
//		System.out.println(parameterizable_attri);
	}
	
//	public static Vector<Query> gen_queries(int size, int query_num) throws SQLException, ClassNotFoundException
//	{
//		Connection c = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection(init.db_url, init.usr_name , init.passwd);
//
//	    
//	    Vector<Query> queries = new Vector<Query>();
//	    
//	    get_joinable_relations(c, pst);
//	    
//	    Query query = gen_query(size, c, pst);
//	    
//	    Query last_q = query;
//	    
//	    for(int i = 1; i<query_num; i++)
//	    {
//	    	
//	    	queries.add(last_q);
//	    	
//	    	last_q = change_local_predicates(last_q);
//	    	
//	    }
//	    
//    	queries.add(last_q);
//
//	    
////	    for(int i = 1; i<query_num; i++)
////	    {
////	    	
////	    	joinable_attribute_lists = new HashMap<string_array, Vector<string_array>>();
////	    	
////	    	foreign_key_relations = new HashMap<String, Vector<String> >();
////	    	
////	    	available_data_type_vec = new Vector<String>();
////	    	
////	    	comparable_data_type_vec = new Vector<String>();
////	    	
////	    	queries.add();
////	    }
//	   
//    	c.close();
//    	
//	    return queries;
//	}
	
	
//	static Query change_local_predicates(Query q)
//	{
//		Query new_query = (Query) q.clone();
//		
//		Random r = new Random();
//		
//		int index = r.nextInt(new_query.conditions.size());
//		
//		String arg2 = new_query.conditions.get(index).arg2.name.substring(1, new_query.conditions.get(index).arg2.name.length() - 1);
//		
//		int arg2_i = Integer.valueOf(arg2) + 1;
//		
//		arg2 = "'" + String.valueOf(arg2_i) + "'";
//		
//		Conditions condition = new Conditions(new_query.conditions.get(index).arg1, new_query.conditions.get(index).subgoal1, new_query.conditions.get(index).op, new Argument(arg2), new_query.conditions.get(index).subgoal2);
//		
//		new_query.conditions.set(index, condition);
//		
//		return new_query;
//	}
	
	static Vector<Integer> get_all_view_ids(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Integer> ids = new Vector<Integer>();
		
		
		while(rs.next())
		{
			ids.add(rs.getInt(1));
		}
		
		return ids;
	}
	
		
	static void get_joinable_relations(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
	    
	    String query_foreign_keys_head = "SELECT tc.constraint_name, tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name, ccu.column_name AS foreign_column_name FROM information_schema.table_constraints AS tc JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name "
	    		+ "JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name WHERE constraint_type = 'FOREIGN KEY' AND tc.table_name='";
	    
	    for(int i = 0; i<citatable_tables.length; i++)
	    {
	    	String query_foreign_keys = query_foreign_keys_head + citatable_tables[i] + "'";
	    	
	    	pst = c.prepareStatement(query_foreign_keys);
	    	
	    	ResultSet rs = pst.executeQuery();
	    	
	    	while(rs.next())
	    	{
	    		String table_name = rs.getString(2);
	    		
	    		String col_name = rs.getString(3);
	    		
	    		String foreign_table_name = rs.getString(4);
	    		
	    		String foreign_col_name = rs.getString(5);
	    		
	    		String [] t_arr = {table_name, foreign_table_name};
	    		
	    		string_array table_arr = new string_array(t_arr);
	    		
	    		
	    		if(foreign_key_relations.get(table_name) == null)
	    		{
	    			Vector<String> foregin_relations = new Vector<String>();
	    			
	    			foregin_relations.add(foreign_table_name);
	    			
	    			foreign_key_relations.put(table_name, foregin_relations);
	    		}
	    		else
	    		{
	    			Vector<String> foregin_relations = foreign_key_relations.get(table_name);
	    			
	    			foregin_relations.add(foreign_table_name);
	    			
	    			foreign_key_relations.put(table_name, foregin_relations);
	    		}
	    		
	    		
	    		
	    		
	    		if(joinable_attribute_lists.get(table_arr) == null)
	    		{
	    			
	    			
	    			
	    			Vector<string_array> attr_map_ta = new Vector<string_array>();
	    			
	    			String [] col_arr = {col_name, foreign_col_name};
	    			
	    			string_array attr_arr = new string_array(col_arr);
	    			
	    			attr_map_ta.add(attr_arr);
	    			
	    			joinable_attribute_lists.put(table_arr, attr_map_ta);
	    		}
	    		else
	    		{
	    			
	    			Vector<string_array> attr_map_ta = joinable_attribute_lists.get(table_arr);
	    			
	    			String [] col_arr = {col_name, foreign_col_name};
	    			
	    			string_array attr_arr = new string_array(col_arr);
	    			
	    			attr_map_ta.add(attr_arr);
	    			
	    			joinable_attribute_lists.put(table_arr, attr_map_ta);
	    			
	    		}
	    		
	    		
	    	}
	    }
	}
	
	public static void build_relation_primary_key_mapping(Connection c, PreparedStatement pst) throws SQLException
	{
		relation_primary_key_mapping.put("family", "family_id");
		
		relation_primary_key_mapping.put("introduction", "family_id");
		
		relation_primary_key_mapping.put("object", "object_id");
		
		relation_primary_key_mapping.put("ligand", "ligand_id");
		
		relation_primary_key_mapping.put("gpcr", "object_id");
		
		Set<String> set = relation_primary_key_mapping.keySet();
		
		for(Iterator iter = set.iterator(); iter.hasNext();)
		{
			String relation = (String) iter.next();
			
			String query = "select distinct " + relation_primary_key_mapping.get(relation) + " from " + relation + " order by " + relation_primary_key_mapping.get(relation);
			
			pst = c.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			
			Vector<Integer> ids = new Vector<Integer>();
			
			while(rs.next())
			{
				ids.add(rs.getInt(1));
			}
			
			relation_primary_key_ranges.put(relation, ids);
			
		}
	}
	
	public static Query gen_query(int size, Connection c, PreparedStatement pst) throws SQLException
	{		
		
		build_relation_primary_key_mapping(c, pst);
								
			Query queries = generate_query(size, c, pst);
		
		return queries;
	}
	
	public static Query gen_query_for_provenance(int size, Connection c, PreparedStatement pst) throws SQLException
    {       
        
        build_relation_primary_key_mapping(c, pst);
                                
            Query queries = generate_query(size, c, pst);
        
        return queries;
    }
	
	public static Vector<Argument> get_full_schema(String subgoal_name, String subgoal_origin_name, Connection c, PreparedStatement pst) throws SQLException
    {
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + subgoal_origin_name +"' order by column_name ";
        
        pst = c.prepareStatement(query);
        
        ResultSet rs = pst.executeQuery();
        
        Vector<Argument> args = new Vector<Argument>();
        
        while(rs.next())
        {
            String attr_name = rs.getString(1);
            
            args.add(new Argument(subgoal_name + init.separator + attr_name, subgoal_name));
        }
        
        return args;
        
    }
	
	public static Query generate_query(int size, Connection c, PreparedStatement pst) throws SQLException
	{
	  build_relation_primary_key_mapping(c, pst);
	  
	  init_parameterizable_attributes(c, pst);
	  
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
				
		Vector<Argument> head_grouping_vars = new Vector<Argument>();
		
		Vector<Argument> head_agg_vars = new Vector<Argument>();
		
		Vector<String> head_agg_functions = new Vector<String>();
		
		Vector<Lambda_term> lambda_terms1 = new Vector<Lambda_term>();
		
		Vector<Lambda_term> lambda_terms2 = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
//		String [] tables = {"object", "object", "ligand"};
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (citatable_tables.length));
			
			String relation = citatable_tables[index];
			
			String relation_name = relation;
			
			if(relation_names.contains(relation))
			{
				i--;
				continue;
//				Random rand = new Random();
//				
//				int suffix = rand.nextInt(size + 1);
//				
//				relation_name = relation + suffix;
//				
//				while(relation_names.contains(relation_name))
//				{
//					suffix = rand.nextInt(size + 1);
//					
//					relation_name = relation + suffix;
//				}
//				
//				relation_names.add(relation_name);
//				
//				maps.put(relation_name, relation);
				
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			HashMap<String, String> attr_list = parameterizable_attri_type.get(relation);
			
//			attr_list.addAll(attr_names);
			System.out.println(attr_list);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
											
			int head_size = rand.nextInt((int)(attr_list.size() * head_var_rate + 1)) + 1;
									
			Vector<Argument> head_vars = gen_head_vars(false, relation, relation_name, parameterizable_attri.get(relation), 3, c, pst);
			
			Vector<Argument> agg_vars = gen_head_vars(true, relation, relation_name, parameterizable_attri.get(relation), 3, c, pst);
			
//			Argument head_arg = new Argument(relation_name + init.separator + primary_key_type[0], relation_name);
			
//			Vector<Argument> head_vars = new Vector<Argument>();
			
//			head_vars.add(head_arg);
			
			head_grouping_vars.addAll(head_vars);
			
			head_agg_vars.addAll(agg_vars);
//			Vector<Argument> args = new Vector<Argument>();
			
			Vector<Argument> args = get_full_schema(relation_name, relation, c, pst);
			
			body.add(new Subgoal(relation_name, args));
		}
		
		for(int i = 0; i<head_agg_vars.size(); i++)
		{
		  head_agg_functions.add("max");
		}
		
		String[] query_local_predicate = gen_local_predicates_with_fixed_size(maps, relation_names, c, pst);
		
		String name = "Q";
				
		Vector<Conditions> predicates = new Vector<Conditions>();
		
		lambda_terms1.add(new Lambda_term(query_local_predicate[0]));
		
		lambda_terms2.add(new Lambda_term(query_local_predicate[1]));
						
		gen_shuffled_head_args(head_grouping_vars);
		
		Query query = new Query(name, new Subgoal(name, head_grouping_vars, head_agg_vars, head_agg_functions, true), body, lambda_terms2, predicates, maps);
		
//		queries[1] = new Query(name, new Subgoal(name, heads), body, lambda_terms2, predicates, maps);
		
		return query;
	}
	
	
	static void gen_shuffled_head_args(Vector<Argument> head_args)
	{
	  Collections.shuffle(head_args);
	}
	
//	public static Query generate_query_with_new_instance_size(Query query, int size, Connection c, PreparedStatement pst) throws SQLException
//	{
//		
//		build_relation_primary_key_mapping(c, pst);
//		
//		HashMap<String, String> maps = query.subgoal_name_mapping;
//		
//		HashSet<String> relation_names = new HashSet<String>();
//		
//		for(int i = 0; i<query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//			
//			relation_names.add(subgoal.name);
//		}
//		
//		String new_lambda_string = gen_local_predicates_with_fixed_size_addition(maps, relation_names, size, c, pst);
//		
//		query.lambda_term.clear();
//		
//		query.lambda_term.add(new Lambda_term(new_lambda_string));
//		
//		return query;
//		
//	}
	
	static String [] get_primary_key(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT"
				+ " c.column_name, c.data_type"
				+ " FROM information_schema.table_constraints tc"
				+ " JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name)"
				+ " JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema AND tc.table_name = c.table_name AND ccu.column_name = c.column_name"
				+ " where constraint_type = 'PRIMARY KEY' and tc.table_name = '"+ table_name  +"'";
		
		pst =c.prepareStatement(query);
		
		String [] results = new String [2];
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			results[0] = rs.getString(1);
			
			results[1] = rs.getString(2);
		}
		
		return results;
		
	}
	
	static Vector<String[]> get_foreign_key_pairs(Vector<String> subgoal_names, HashMap<String, String> maps)
	{
		
		Vector<String[]> foreign_key_pairs = new Vector<String []>();
		
		HashSet<string_array> string_arrs = new HashSet<string_array>();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{
			if(foreign_key_relations.get(maps.get(subgoal_names.get(i))) != null)
			{
				Vector<String> foreign_tables = foreign_key_relations.get(maps.get(subgoal_names.get(i)));
				
				for(int k = 0; k<foreign_tables.size(); k++)
				{
					if(subgoal_names.contains(foreign_tables.get(k)))
					{
						String [] pairs = {maps.get(subgoal_names.get(i)), foreign_key_relations.get(maps.get(subgoal_names.get(i))).get(k)};
						
						string_array str_arr = new string_array(pairs);
						
						
						if(!string_arrs.contains(str_arr))
							string_arrs.add(str_arr);
						else
							continue;
						
						foreign_key_pairs.add(pairs);
					}
				}
				
				
			}
		}
		
		return foreign_key_pairs;
	}
	
	
	static Vector<Conditions> gen_global_conditions(Vector<Subgoal> body, HashMap<String, String> maps)
	{
		Vector<String> subgoal_names = new Vector<String>();
		
		Vector<Conditions> global_predicates = new Vector<Conditions>();
		
		for(int i = 0; i<body.size(); i++)
		{
			subgoal_names.add(body.get(i).name);
		}
		
		Vector<String[]> foreign_key_pairs =  get_foreign_key_pairs(subgoal_names, maps);
		
		Random rand = new Random();
		
		int size = rand.nextInt((int)(foreign_key_pairs.size() * global_predicate_rate + 1));
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		for(int i = 0; i<size; i++)
		{
			int index = rand.nextInt(foreign_key_pairs.size());
			
			id_set.add(index);
		}
				
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			int index = (int) iter.next();
			
			String [] foreign_key_pair = foreign_key_pairs.get(index);
			
			string_array relation_array = new string_array(foreign_key_pair);
			
			
			
//			System.out.println(joinable_attribute_lists.toString());
//			
//			System.out.println(relation_array.toString());
			
			Vector<string_array> attr_array = joinable_attribute_lists.get(relation_array);
			
			for(int k = 0; k<attr_array.size(); k++)
			{
				
				Set<String> string_set = maps.keySet();
				
				Vector<String> rel1 = new Vector<String>();
				
				Vector<String> rel2 = new Vector<String>();
				
//				System.out.println(maps.toString());
				
				for(Iterator it = string_set.iterator(); it.hasNext();)
				{
					String key = (String)it.next();
					
					if(maps.get(key) == relation_array.array[0])
						rel1.add(key);
					
					if(maps.get(key) == relation_array.array[1])
						rel2.add(key);
				}
				
				
				for(int r = 0; r<rel1.size(); r++)
				{
					for(int p = 0; p<rel2.size(); p++)
					{
						Random generator = new Random();
						
						double number = generator.nextDouble();
						
//						if(number < global_predicate_ratio)
//							global_predicates.add(new Conditions(new Argument(attr_array.get(k).array[0], rel1.get(r)), rel1.get(r), new op_equal(), new Argument(attr_array.get(k).array[1], rel2.get(p)), rel2.get(p), null, null));

					}
				}
				
				
			}
			
			
		}
		
		return global_predicates;
	}
	
	static Vector<Argument> gen_head_vars(boolean is_agg_attrs, String relation, String relation_name, Vector<String> attr_list, int size, Connection c, PreparedStatement pst)
	{
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		Vector<Argument> head_vars = new Vector<Argument>();
		
		Random r = new Random();
		
		HashSet<String> aggregated_attributes = new HashSet<String>();
		aggregated_attributes.addAll(Arrays.asList(maximable_data_types));
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt(attr_list.size());
			
			if(parameterizable_attri.get(relation).contains(attr_list.get(index)))
			{
			  if(!is_agg_attrs)
				id_set.add(index);
			  else
			  {
			    if(is_agg_attrs && aggregated_attributes.contains(parameterizable_attri_type.get(relation).get(attr_list.get(index))))
			    {
			      System.out.println(parameterizable_attri_type.get(relation).get(attr_list.get(index)));
			      
			      id_set.add(index);
			    }
			  }
			}
//			else
//			{
//				i--;
//				
//				continue;
//			}
//			
		}
		
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument l = new Argument(relation_name+ init.separator + attr_list.get(index), relation_name);
			
			head_vars.add(l);
			
		}
		
		return head_vars;
	}
	
	static Vector<Lambda_term> gen_lambda_terms(Vector<Argument> head_vars, String relation, String relation_name)
	{
		
		Random rand = new Random();
		
		int size = rand.nextInt(head_vars.size() + 1);
		
		
		
		HashSet<Integer> int_list = new HashSet<Integer>();
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<size; i++)
		{
			int index = rand.nextInt(head_vars.size());
			
			int_list.add(index);
		}
		
		for(Iterator iter = int_list.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument arg = head_vars.get(index);
			
			l_terms.add(new Lambda_term(arg.relation_name + init.separator + arg.name, arg.relation_name));
		}
		
		return l_terms;
		
	}

	
	static String[] gen_local_predicates_with_fixed_size(HashMap<String, String> relation_mapping, HashSet<String> relation_names, Connection c, PreparedStatement pst) throws SQLException
	{
		
		long total_range = 1;
		
		Vector<String> relations = new Vector<String>();
		
		relations.addAll(relation_names);
		
		HashMap<String, HashSet<Integer>> max_min_value_mapping = new HashMap<String, HashSet<Integer>>();
		
		for(int i = 0; i<relations.size(); i++)
		{
			
//			System.out.println(relation_mapping.get(relations.get(i)));
			
//			System.out.println(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size());
			
			total_range = total_range * relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
		}
		
		int selected_size = (int)(Math.pow(query_result_size, 1.0/relations.size()) + 0.5);
		
		String []selection_strings = new String[2];
		
		selection_strings[0] = new String();
		
		selection_strings[1] = new String();
		
		int real_size = 1;
		
//		if(ratio < 1)
		{
			
			for(int i = 0; i<relations.size(); i++)
			{
				
				if(i >= 1)
				{
				  selection_strings[0] += " and ";
				  
				  selection_strings[1] += " and ";
				}
				
				String selection_string1 = "(";
				
				String selection_string2 = "("; 
				
				int max_size = relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
				
//				int selected_size = (int)(max_size * ratio + 0.5);
				
				real_size *= selected_size;
				
//				System.out.println(selected_size);
				
				HashSet<Integer> id_list = new HashSet<Integer>();
				
				while(id_list.size() < selected_size)
				{
					Random r = new Random();
					
					int id = r.nextInt(max_size);
					
					id_list.add(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).get(id));
				}
				
				int num = 0;
				
				HashSet<Integer> integer_list = max_min_value_mapping.get(relation_mapping.get(relations.get(i)));
				
				if(integer_list == null)
				{
					integer_list = new HashSet<Integer>();
				}
				
				
				for(Iterator iter = id_list.iterator(); iter.hasNext();)
				{
					Integer id = (Integer) iter.next();
					
					integer_list.add(id);
					
					if(num >= 1)
					{
						selection_string1 += " or ";
						
						selection_string2 += " or ";
					}
					
					selection_string1 += relations.get(i) + "." + relation_primary_key_mapping.get(relation_mapping.get(relations.get(i))) + "=" + id;
					
					selection_string2 += "\"" + relations.get(i) + "\".\"" + relation_primary_key_mapping.get(relation_mapping.get(relations.get(i))) + "\"" + "=" + id;
					
					num++;
					
				}
				
				selection_string1 += ")";
				
				selection_string2 += ")";
				
				selection_strings[0] += selection_string1;
				
				selection_strings[1] += selection_string2;
				
				max_min_value_mapping.put(relation_mapping.get(relations.get(i)), integer_list);
			}
		}
		
		output_selected_value2files(max_min_value_mapping);
		
//		System.out.println(real_size);
//		
//		System.out.println("selection_string:::" + selection_strings);
		
		return selection_strings;
	}
	
	static String gen_local_predicates_with_fixed_size(HashMap<String, String> relation_mapping, HashSet<String> relation_names, int size, Connection c, PreparedStatement pst) throws SQLException
	{
		
		long total_range = 1;
		
		Vector<String> relations = new Vector<String>();
		
		relations.addAll(relation_names);
		
		HashMap<String, HashSet<Integer>> max_min_value_mapping = new HashMap<String, HashSet<Integer>>();
		
		for(int i = 0; i<relations.size(); i++)
		{
			
//			System.out.println(relation_mapping.get(relations.get(i)));
			
//			System.out.println(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size());
			
			total_range = total_range * relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
		}
		
		int selected_size = (int)(Math.pow(query_result_size, 1.0/relations.size()) + 0.5);
		
		String selection_strings = new String();
		
		int real_size = 1;
		
//		if(ratio < 1)
		{
			
			for(int i = 0; i<relations.size(); i++)
			{
				
				if(i >= 1)
					selection_strings += " and ";
				
				String selection_string = "(";
				
				int max_size = relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
				
//				int selected_size = (int)(max_size * ratio + 0.5);
				
				real_size *= selected_size;
				
//				System.out.println(selected_size);
				
				HashSet<Integer> id_list = new HashSet<Integer>();
				
				while(id_list.size() < selected_size)
				{
					Random r = new Random();
					
					int id = r.nextInt(max_size);
					
					id_list.add(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).get(id));
				}
				
				int num = 0;
				
				HashSet<Integer> integer_list = max_min_value_mapping.get(relation_mapping.get(relations.get(i)));
				
				if(integer_list == null)
				{
					integer_list = new HashSet<Integer>();
				}
				
				
				for(Iterator iter = id_list.iterator(); iter.hasNext();)
				{
					Integer id = (Integer) iter.next();
					
					integer_list.add(id);
					
					if(num >= 1)
					{
						selection_string += " or ";
					}
					
					selection_string += relations.get(i) + "." + relation_primary_key_mapping.get(relation_mapping.get(relations.get(i))) + "=" + id;
					
					num++;
					
				}
				
				selection_string += ")";
				
				selection_strings += selection_string;
				
				max_min_value_mapping.put(relation_mapping.get(relations.get(i)), integer_list);
			}
		}
		
		output_selected_value2files(max_min_value_mapping);
		
//		System.out.println(real_size);
//		
//		System.out.println("selection_string:::" + selection_strings);
		
		return selection_strings;
	}
	
//	static String gen_local_predicates_with_fixed_size_addition(HashMap<String, String> relation_mapping, HashSet<String> relation_names, int size, Connection c, PreparedStatement pst) throws SQLException
//	{
//		
//		HashMap<String, int[]> selected_id = new HashMap<String, int[]>();
//		
//		HashMap<String, Integer> indexes = new HashMap<String, Integer>();
//		
//		view_generator.inputquery_conditions(selected_id, indexes);
//		
//		long total_range = 1;
//		
//		Vector<String> relations = new Vector<String>();
//		
//		relations.addAll(relation_names);
//		
//		HashMap<String, HashSet<Integer>> max_min_value_mapping = new HashMap<String, HashSet<Integer>>();
//		
//		for(int i = 0; i<relations.size(); i++)
//		{
//			
////			System.out.println(relation_mapping.get(relations.get(i)));
//			
////			System.out.println(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size());
//			
//			total_range = total_range * relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
//		}
//		
//		int selected_size = (int)(Math.pow(size, 1.0/relations.size()) + 0.5);
//		
//		String selection_strings = new String();
//		
//		int real_size = 1;
//		
////		if(ratio < 1)
//		{
//			
//			for(int i = 0; i<relations.size(); i++)
//			{
//				
//				if(i >= 1)
//					selection_strings += " and ";
//				
//				String selection_string = "(";
//				
//				int [] ids = selected_id.get(relations.get(i));
//				
//				int max_size = relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).size();
//				
////				int selected_size = (int)(max_size * ratio + 0.5);
//				
//				real_size *= selected_size;
//				
////				System.out.println(selected_size);
//				
//				HashSet<Integer> id_list = new HashSet<Integer>();
//				
//				for(int p = 0; p < ids.length; p++)
//				{
//					id_list.add(ids[p]);
//				}
//				while(id_list.size() < selected_size)
//				{
//					Random r = new Random();
//					
//					int id = r.nextInt(max_size);
//					
//					id_list.add(relation_primary_key_ranges.get(relation_mapping.get(relations.get(i))).get(id));
//				}
//				
//				int num = 0;
//				
//				HashSet<Integer> integer_list = max_min_value_mapping.get(relation_mapping.get(relations.get(i)));
//				
//				if(integer_list == null)
//				{
//					integer_list = new HashSet<Integer>();
//				}
//				
//				
//				for(Iterator iter = id_list.iterator(); iter.hasNext();)
//				{
//					Integer id = (Integer) iter.next();
//					
//					integer_list.add(id);
//					
//					if(num >= 1)
//					{
//						selection_string += " or ";
//					}
//					
//					selection_string += relations.get(i) + "." + relation_primary_key_mapping.get(relation_mapping.get(relations.get(i))) + "=" + id;
//					
//					num++;
//					
//				}
//				
//				selection_string += ")";
//				
//				selection_strings += selection_string;
//				
//				max_min_value_mapping.put(relation_mapping.get(relations.get(i)), integer_list);
//			}
//		}
//		
//		output_selected_value2files(max_min_value_mapping);
//		
////		System.out.println(real_size);
////		
////		System.out.println("selection_string:::" + selection_strings);
//		
//		return selection_strings;
//	}
//	
	static void output_selected_value2files(HashMap<String, HashSet<Integer>> selected_value_mapping)
	{
		Set<String> keys = selected_value_mapping.keySet();
		
		Vector<String> output_strings = new Vector<String>();
		
		Vector<String> index4gap = new Vector<String>();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			
			String key = (String) iter.next();
			
			HashSet<Integer> int_list = selected_value_mapping.get(key);
			
			int [] int_arr = new int[int_list.size()];
			
			int num = 0;
			
			for(Iterator it = int_list.iterator(); it.hasNext();)
			{
				int integer = (int) it.next();
				
				int_arr[num ++] = integer;
			}
						
			Arrays.sort(int_arr);
			
			String string = key + "::";
			
			String index_str = key + "::1";
			
			for(int i = 0; i<int_arr.length; i++)
			{
				
				if(i >= 1)
					string += ",";
				
				string += int_arr[i];
			}
			
			output_strings.add(string);
			
			index4gap.add(index_str);
		}
		
		write("query_selected_values.txt", output_strings);
		
		write("query_selected_values_index_gap.txt", index4gap);
	}
	
	static void write(String file_name, Vector<String> line)
	{
		 BufferedWriter bw = null;
	      try {
	         //Specify the file name and path here
		 File file = new File(file_name);

		 /* This logic will make sure that the file 
		  * gets created if it is not present at the
		  * specified location*/
		  if (!file.exists()) {
		     file.createNewFile();
		  }

		  FileWriter fw = new FileWriter(file);
		  bw = new BufferedWriter(fw);
		  
		  for(int i = 0; i<line.size(); i++)
		  {
			  bw.append(line.get(i));
			  bw.newLine();
		  }
		  		  
	      bw.close();


		  
	      } catch (IOException ioe) {
		   ioe.printStackTrace();
		}
	      
	}
	
	static Vector<Conditions> gen_local_predicates(int selection_size, HashMap<String, String> attr_types, Vector<String> attr_list, String relation, String relation_name, String [] primary_key_type, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Random r = new Random();
		
		
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		
		if(comparable_data_type_vec.size() == 0)
			comparable_data_type_vec.addAll(Arrays.asList(comparable_data_type));
		
		double value = r.nextDouble();
		
//		String attr_name = primary_key_type[0];
//		
//		String type = primary_key_type[1];
//				
//		Conditions insert_condition = new Conditions(new Argument(attr_name, relation_name) , relation_name, new op_less_equal(), new Argument("'2'"), new String());
//		
//		conditions.add(insert_condition);
//		
//		return conditions;

		
		if(value < local_predicates_rate)
		{
			String attr_name = primary_key_type[0];
			
			String type = primary_key_type[1];
			
			if(comparable_data_type_vec.contains(type))
			{
				Conditions insert_condition = do_gen_condition_comparable(relation, relation_name, attr_name, c, pst);
				
				if(insert_condition != null)
					conditions.add(insert_condition); 
			}
			else
			{
				Conditions insert_condition = do_gen_condition_uncomparable(relation, relation_name, attr_name, c, pst);
				
				if(insert_condition != null)
					conditions.add(insert_condition);
			}
		}
		
		return conditions;
	}
	
	
	public static Vector<String> get_all_values(String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> all_values = new Vector<String>();
		
		String query = "select distinct " + attr_name + " from " + relation_name + " where " + attr_name + " IS NOT NULL " + " order by " + attr_name;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			all_values.add(rs.getString(1));
		}
		
		return all_values;
	}
	
	static Conditions do_gen_condition_comparable(String relation, String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		int op_id = r.nextInt(6);
		
		Operation op = null;
		
		switch(op_id)
		{
		case 0:
		{
			op = (Operation) new op_equal();
			
			break;			
		}
		case 1:
		{
			op = (Operation) new op_not_equal();
			
			break;
		}
		case 2:
		{
			op = (Operation) new op_less();
			
			break;
		}
		case 3:
		{
			op = (Operation) new op_less_equal();
			
			break;
		}
		case 4:
		{
			op = (Operation) new op_greater();
			
			break;
		}
		case 5:
		{
			op = (Operation) new op_greater_equal();
			
			break;
		}
		default: break;
		}
		
		Vector<String> all_values = get_all_values(relation, attr_name, c, pst);
		
		if(all_values.size() == 0)
			return null;
		
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
//		if(all_values.get(index).length() < 100)
//			return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);
//		else
			return null;

	}
	
	static Conditions do_gen_condition_uncomparable(String relation, String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		Operation op = new op_equal();
		
		Vector<String> all_values = get_all_values(relation, attr_name, c, pst);
		
		if(all_values.size() == 0)
			return null;
				
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
//		if(all_values.get(index).length() < 100)
//			return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);
//		else
			return null;

	}
	
	static HashMap<String, String> get_attr_types(String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> attr_names = new Vector<String>();
		
		String query_attr_name = "SELECT column_name, data_type "
				+ "FROM information_schema.columns "
				+ "WHERE table_name = '"+ relation_name + "' "
				+ "ORDER BY ordinal_position";
		pst = c.prepareStatement(query_attr_name);
		
		ResultSet rs = pst.executeQuery();
		
		HashMap<String, String> attr_type_mapping = new HashMap<String, String>();
		
		while(rs.next())
		{
			String attr_name = rs.getString(1);
			
			if(attr_name.equals("citation_view"))
				continue;
			
			String type = rs.getString(2);
			
			if(available_data_type_vec.size() == 0)
			{
				available_data_type_vec.addAll(Arrays.asList(available_data_type));
			}
			
			if(available_data_type_vec.contains(type))
			{
				attr_type_mapping.put(attr_name, type);
			}
		}
		
		return attr_type_mapping;
	}
	
	private static class string_array
	{
		public String [] array;
		
		
		public string_array(String [] array) {
			// TODO Auto-generated constructor stub
			this.array = array;
		}
		
		@Override
		public String toString()
		{
			
			String str = new String();
			
			for(int i = 0; i<array.length; i++)
			{
				if(i >= 1)
					str += ",";
				
				str += array[i];
			}
			
			return str;
		}
		
		@Override
		public int hashCode()
		{
			return this.toString().hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			string_array ta = (string_array) obj;
			
			if(ta.hashCode() == this.hashCode())
			{
				return true;
			}
			
			return false;
			
		}
	}
	
	

}
