package edu.upenn.cis.citation.pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.Condition;

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
import edu.upenn.cis.citation.init.init;

public class view_operation {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
	  
	}
	
	public static void add_lambda_term(int view_id, Lambda_term l_term, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "insert into view2lambda_term values ('" + view_id + "','" + l_term.toString() + "','" + l_term.table_name + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	public static void save_view_by_name(String old_name, String new_name, Query view, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		save_view_by_name(old_name, new_name, view, true, c, pst);
	}
	
	public static void save_view_by_name(String old_name, String new_name, Query view, boolean tuple_level, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
        int id = get_view_id(old_name, c, pst);
        
//        String id = name;
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
//        delete_citation_view(id, c, pst);
//        if(tuple_level)
//        	populate_db.delete("v" + id, subgoals, subgoal_name_mapping, has_lambda, c, pst);
        
        update_view_table(id, new_name, view, c, pst);
        
        insert_view_conditions(id, view, c, pst);
        
        insert_view_lambda_term(id, view, c, pst);
        
        insert_view_subgoals(id, view, c, pst);
        
        view.name = "v" + id;
        
//        populate_db.update(view);
        
	}
	
//	static Query gen_sample_view() throws ClassNotFoundException, SQLException
//	{
//		Vector<Argument> head_args = new Vector<Argument>();
////		
//		head_args.add(new Argument("family" + populate_db.separator + "type", "family"));
//		
//		Subgoal head = new Subgoal("q", head_args);
//		
//		Vector<Subgoal> subgoals = new Vector<Subgoal>();
//		
////		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
////		
////		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
//		
//		Vector<Argument> args1 = new Vector<Argument>();
////		
//		Vector<Argument> args3 = new Vector<Argument> ();//view_operation.get_full_schema("introduction", "introduction", c, pst);
//		
////		subgoals.add(new Subgoal("introduction1", args1));
//		
////		subgoals.add(new Subgoal("family1", args2));
//				
//		subgoals.add(new Subgoal("introduction", args3));
//		
//		Vector<Conditions> conditions = new Vector<Conditions>();
//		
////		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("5"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("5"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("5"), new String()));
//		
////		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
//		
//		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
//				
//		subgoal_name_mapping.put("introduction", "introduction");
//		
////		subgoal_name_mapping.put("introduction1", "introduction");
//
//		Vector<String []> head_vars = new Vector<String []>();
//		
//		String [] head_v = {"family_id", "introduction"};
//		
////		String [] head_v1 = {"family_id", "introduction1"};
//		
//		head_vars.add(head_v);
//		
////		head_vars.add(head_v1);
//		
//		Vector<String []> condition_str = new Vector<String []>();
//		
//		Vector<String []> lambda_term_str = new Vector<String []>();
//		
//		String [] l_str = {"family_id", "introduction"};
//		
//		lambda_term_str.add(l_str);
//		
////		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
////		
////		System.out.println(q);
//						
//		return Gen_query.gen_query("v10", subgoal_name_mapping, head_vars, condition_str, lambda_term_str);
//	}
//	
		
	public static void initial() throws ClassNotFoundException, SQLException
	{
		
//		create_view.check_create_views();
		
//		populate_db.initial();
	}
	
	public static int add(Query v, String name, Connection c, PreparedStatement pst, boolean tuple_level) throws ClassNotFoundException, SQLException
	{
		int id = insert_view(v, name, c, pst);
		
		v.name = "v" + id;
		
//		Vector<String> subgoal_names = new Vector<String>();
//		
//		Vector<Subgoal> subgoals = v.body;
//		
//		for(int i = 0; i<subgoals.size(); i++)
//		{
//			subgoal_names.add(subgoals.get(i).name);
//		}
		
//		create_view.create_views(id, subgoal_names, v.conditions, null, null);
		
//		if(tuple_level)
//			populate_db.update(v, c, pst);
		
		return id;

	}
	
	public static int add(Query v, String name, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		return add(v, name, c, pst, true);
	}
	
	public static Query get_view_by_name(String name, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        HashMap<String, Argument> name_arg_mappings = new HashMap<String, Argument>();
        
        Vector<Argument> head_var = new Vector<Argument>();
        
        int id = get_id_head_vars(name, head_var, c, pst);
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
        
        Vector<Conditions> conditions = get_view_conditions(id, name_arg_mappings, c, pst);
         
        Vector<Lambda_term> lambda_terms = get_view_lambda_terms(id, c, pst);
        
        Subgoal head = new Subgoal("v" + id, head_var);
                
        Query view = new Query("v" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
                        
        return view;
	}
	
	public static int get_view_id_by_name(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int id = 0;
		
		if(rs.next())
		{
			id = rs.getInt(1);
		}
		
		return id;
	}
	
//	public static Query get_view_by_id(int id, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
//	{
//        
//        Vector<Argument> head_var = get_head_vars(id, c, pst);
//        
//        Vector<Conditions> conditions = get_view_conditions(id, c, pst);
//        
//        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
//        
//        Vector<Subgoal> subgoals = get_view_subgoals_full(id, subgoal_name_mapping, c, pst);
//        
//        Vector<Lambda_term> lambda_terms = get_view_lambda_terms(id, c, pst);
//        
//        Subgoal head = new Subgoal("v" + id, head_var);
//        
//        Query view = new Query("v" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
//        
//        
//                
//        return view;
//	}
	
	public static Query get_view_by_id(int id, Connection c, PreparedStatement pst) throws SQLException
	{
        
        HashMap<String, Argument> name_arg_mappings = new HashMap<String, Argument>();
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals_full(id, subgoal_name_mapping, name_arg_mappings, c, pst);
        
        Vector<Argument> head_var = get_head_vars(id, name_arg_mappings, c, pst);
        
        Vector<Conditions> conditions = get_view_conditions(id, name_arg_mappings, c, pst);
        
        Vector<Lambda_term> lambda_terms = get_view_lambda_terms(id, name_arg_mappings, c, pst);
        
        Subgoal head = new Subgoal("v" + id, head_var);
        
        Query view = new Query("v" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
        
                        
        return view;
	}
	
	static int get_view_id(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int id = 0;
				
		if(rs.next())
		{
			id = rs.getInt(1);
		}
		
		return id;
	}
	
	public static int delete_view_by_name(String name, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		return delete_view_by_name(name, c, pst, true);
	}
	
	public static int delete_view_by_name(String name, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException
	{
        
        int id = get_view_id(name, c, pst);
        
//        String id = name;
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
        delete_citation_view(id, c, pst);
        
        delete_view_table(id, c, pst);
        
        
//        if(tuple_level)
//        	populate_db.delete("v" + id, subgoals, subgoal_name_mapping, has_lambda, c, pst);
        
        
        return id;

	}
	
	public static int delete_view_by_name(String name, boolean tuple_level, Connection c, PreparedStatement pst) throws SQLException
	{
        
        int id = get_view_id(name, c, pst);
        
//        String id = name;
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
        delete_citation_view(id, c, pst);
        
        delete_view_table(id, c, pst);
        
        
//        if(tuple_level)
//        	populate_db.delete("v" + id, subgoals, subgoal_name_mapping, has_lambda, c, pst);
        
        
        return id;

	}
	
	public static Vector<Query> get_all_views(Connection c, PreparedStatement pst) throws SQLException
	{
        
        Vector<Query> views = new Vector<Query>();
        
        Vector<Integer> ids = get_all_view_ids(c, pst);
        
        for(int i = 0; i<ids.size(); i++)
        {
        	Query view = get_view_by_id(ids.get(i), c, pst);
        	
        	update_name(view, ids.get(i), c, pst);
        	
        	views.add(view);
        }
                
        return views;
	}
	
	static void update_name(Query view, int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select name from view_table where view = " + id;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			String name = rs.getString(1);
			
			view.name = name;
			
			return;
		}
	}
	
	static Vector<Integer> get_all_view_ids(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table order by view";
		
		Vector<Integer> ids = new Vector<Integer>();
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			ids.add(rs.getInt(1));
		}
		
		return ids;
	}
	
	public static void delele_all_views(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		delete_all_lambda_terms(c, pst);
        
        delete_all_subgoals(c, pst);
        
        delete_all_conditions(c, pst);
        
        delete_all_citation_view(c, pst);
        
        delete_all_view_table(c, pst);
	}
	
	public static void delete_view_by_id(int id, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException
	{
        
//        String id = get_view_id(name, c, pst);
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
        delete_citation_view(id, c, pst);
        
        delete_view_table(id, c, pst);
                
//        if(tuple_level)
//        	populate_db.delete("v" + id, subgoals, subgoal_name_mapping, has_lambda, c, pst);

	}
	
	static void delete_citation_view(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2view where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	
	
	static boolean delete_lambda_terms(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		
		
		String query = "select count(*) from view2lambda_term where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		boolean has_lambda_term = false;
		
		if(rs.next())
		{
			int num = rs.getInt(1);
			
			if(num > 0)
				has_lambda_term = true;
		}
		
		query = "delete from view2lambda_term where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		return has_lambda_term;
		
	}
	
	static void delete_conditions(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2conditions where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_subgoals(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2subgoals where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_view_table(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view_table where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_all_citation_view(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2view";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	
	
	static void delete_all_lambda_terms(Connection c, PreparedStatement pst) throws SQLException
	{
		
		String query = "delete from view2lambda_term";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static void delete_all_conditions(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_all_subgoals(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2subgoals";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_all_view_table(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void update_view_table(int id, String name, Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		String view_name = name;
		
		String head_vars_str = new String();
		
		for(int i = 0; i<view.head.args.size(); i++)
		{
			Argument arg = (Argument)view.head.args.get(i);
			
			if(i >= 1)
				head_vars_str += ",";
			
			head_vars_str += arg.name;
		}
		
		String query = "update view_table set head_variables ='" + head_vars_str + "',name ='" + view_name + "' where view = " + id;
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static Vector<Conditions> get_view_conditions(int name, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_conditions = "select conditions from view2conditions where view = '" + name + "'";
		
		pst = c.prepareStatement(q_conditions);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		while(r.next())
		{
			
			String condition_str = r.getString(1);
			
			Conditions condition = parse_conditions(condition_str, name_arg_mappings, init.separator);
			
						
			
			
			
			
			
//			String []strs1 = str1.split("_");
//			
//			String subgoal1 = strs1[0] + "_" + strs1[1];
//			
//			String arg1 = str1.trim();//.substring(subgoal1.length() + 1, str1.length());
//			
//			String subgoal2 = new String();
			
			
			
			
			
				
//			Conditions condition = new Conditions(new Argument(arg1), subgoal1, op, new Argument(arg2), subgoal2);
			
			conditions.add(condition);
		}
		return conditions;
		
	}
	
	   static Vector<Conditions> get_view_conditions(int name, Connection c, PreparedStatement pst) throws SQLException
	    {
	        String q_conditions = "select conditions from view2conditions where view = '" + name + "'";
	        
	        pst = c.prepareStatement(q_conditions);
	        
	        ResultSet r = pst.executeQuery();
	        
	        Vector<Conditions> conditions = new Vector<Conditions>();
	        
	        while(r.next())
	        {
	            
	            String condition_str = r.getString(1);
	            
	            Conditions condition = parse_conditions(condition_str, init.separator);
	            
	                        
	            
	            
	            
	            
	            
//	          String []strs1 = str1.split("_");
//	          
//	          String subgoal1 = strs1[0] + "_" + strs1[1];
//	          
//	          String arg1 = str1.trim();//.substring(subgoal1.length() + 1, str1.length());
//	          
//	          String subgoal2 = new String();
	            
	            
	            
	            
	            
	                
//	          Conditions condition = new Conditions(new Argument(arg1), subgoal1, op, new Argument(arg2), subgoal2);
	            
	            conditions.add(condition);
	        }
	        return conditions;
	        
	    }
	
	public static Conditions parse_conditions(String condition_str, HashMap<String, Argument> name_arg_mappings, String parser)
	{
		if(condition_str.contains("'"))
		{
			String str2 = condition_str.substring(condition_str.indexOf("'"), condition_str.length());
			
			String str1 = condition_str.substring(0, condition_str.indexOf("'"));
			
			String [] strs = null;
			
			Operation op = null;
			
			if(str1.contains(op_less_equal.op))
			{
				strs = str1.split(op_less_equal.op);
				
				op = new op_less_equal();
			}
			else
			{
				if(str1.contains(op_greater_equal.op))
				{
					strs = str1.split(op_greater_equal.op);
					
					op = new op_greater_equal();
				}
				else
				{
					if(str1.contains(op_not_equal.op))
					{
						strs = str1.split(op_not_equal.op);
						
						op = new op_not_equal();
					}
					else
					{
						if(str1.contains(op_less.op))
						{
							strs = str1.split(op_less.op);
							
							op = new op_less();
						}
						else
						{
							if(str1.contains(op_greater.op))
							{
								strs = str1.split(op_greater.op);
								
								op = new op_greater();
							}
							else
							{
								if(str1.contains(op_equal.op))
								{
									strs = str1.split(op_equal.op);
									
									op = new op_equal();
								}
							}
						}
					}
				}
				
			}
			
			
			String relation_name1 = strs[0].substring(0, strs[0].indexOf(parser)).trim();
			
			String relation_name2 = new String();
			
			String arg1 = strs[0].substring(strs[0].indexOf(parser) + 1, strs[0].length()).trim();

			String arg2 = new String ();
			
			Argument Arg1 = name_arg_mappings.get(relation_name1 + init.separator + arg1);
			
			Argument Arg2 = new Argument(str2);
			
			int value_len = str2.length();
			
			Arg2.value = str2.substring(1, value_len - 1);
						
			return new Conditions(Arg1, relation_name1, op, Arg2, relation_name2);
			
			
		}
		else
		{
			String []strs = null;
			
			Operation op = null;
			
			
			if(condition_str.contains(op_less_equal.op))
			{
				strs = condition_str.split(op_less_equal.op);
				
				op = new op_less_equal();
			}
			else
			{
				if(condition_str.contains(op_greater_equal.op))
				{
					strs = condition_str.split(op_greater_equal.op);
					
					op = new op_greater_equal();
				}
				else
				{
					if(condition_str.contains(op_not_equal.op))
					{
						strs = condition_str.split(op_not_equal.op);
						
						op = new op_not_equal();
					}
					else
					{
						if(condition_str.contains(op_less.op))
						{
							strs = condition_str.split(op_less.op);
							
							op = new op_less();
						}
						else
						{
							if(condition_str.contains(op_greater.op))
							{
								strs = condition_str.split(op_greater.op);
								
								op = new op_greater();
							}
							else
							{
								if(condition_str.contains(op_equal.op))
								{
									strs = condition_str.split(op_equal.op);
									
									op = new op_equal();
								}
							}
						}
					}
				}
				
			}
			
			String str1 = strs[0];
			
			String str2 = strs[1];
			
			String relation_name1 = str1.substring(0, str1.indexOf(parser)).trim();
			
			String relation_name2 = new String();
			
			String arg1 = str1.substring(str1.indexOf(parser) + 1, str1.length()).trim();

			String arg2 = str2.substring(str2.indexOf(parser) + 1, str2.length()).trim();
				
//				subgoal2 = strs2[0] + "_" + strs2[1];
				
				relation_name2 = str2.substring(0, str2.indexOf(parser)).trim();
				
				
			Argument Arg1 = name_arg_mappings.get(relation_name1 + init.separator + arg1);
				
			Argument Arg2 = name_arg_mappings.get(relation_name2 + init.separator + arg2);
			
			return new Conditions(Arg1, relation_name1, op, Arg2, relation_name2);

			
		}

	}
	
	public static Conditions parse_conditions(String condition_str, String parser)
    {
        if(condition_str.contains("'"))
        {
            String str2 = condition_str.substring(condition_str.indexOf("'"), condition_str.length());
            
            String str1 = condition_str.substring(0, condition_str.indexOf("'"));
            
            String [] strs = null;
            
            Operation op = null;
            
            if(str1.contains(op_less_equal.op))
            {
                strs = str1.split(op_less_equal.op);
                
                op = new op_less_equal();
            }
            else
            {
                if(str1.contains(op_greater_equal.op))
                {
                    strs = str1.split(op_greater_equal.op);
                    
                    op = new op_greater_equal();
                }
                else
                {
                    if(str1.contains(op_not_equal.op))
                    {
                        strs = str1.split(op_not_equal.op);
                        
                        op = new op_not_equal();
                    }
                    else
                    {
                        if(str1.contains(op_less.op))
                        {
                            strs = str1.split(op_less.op);
                            
                            op = new op_less();
                        }
                        else
                        {
                            if(str1.contains(op_greater.op))
                            {
                                strs = str1.split(op_greater.op);
                                
                                op = new op_greater();
                            }
                            else
                            {
                                if(str1.contains(op_equal.op))
                                {
                                    strs = str1.split(op_equal.op);
                                    
                                    op = new op_equal();
                                }
                            }
                        }
                    }
                }
                
            }
            
            
            String relation_name1 = strs[0].substring(0, strs[0].indexOf(parser)).trim();
            
            String relation_name2 = new String();
            
            String arg1 = strs[0].substring(strs[0].indexOf(parser) + 1, strs[0].length()).trim();

            String arg2 = new String ();
            
            Argument Arg1 = new Argument(arg1, relation_name1);
                        
            return new Conditions(Arg1, relation_name1, op, new Argument(str2), relation_name2);
            
            
        }
        else
        {
            String []strs = null;
            
            Operation op = null;
            
            
            if(condition_str.contains(op_less_equal.op))
            {
                strs = condition_str.split(op_less_equal.op);
                
                op = new op_less_equal();
            }
            else
            {
                if(condition_str.contains(op_greater_equal.op))
                {
                    strs = condition_str.split(op_greater_equal.op);
                    
                    op = new op_greater_equal();
                }
                else
                {
                    if(condition_str.contains(op_not_equal.op))
                    {
                        strs = condition_str.split(op_not_equal.op);
                        
                        op = new op_not_equal();
                    }
                    else
                    {
                        if(condition_str.contains(op_less.op))
                        {
                            strs = condition_str.split(op_less.op);
                            
                            op = new op_less();
                        }
                        else
                        {
                            if(condition_str.contains(op_greater.op))
                            {
                                strs = condition_str.split(op_greater.op);
                                
                                op = new op_greater();
                            }
                            else
                            {
                                if(condition_str.contains(op_equal.op))
                                {
                                    strs = condition_str.split(op_equal.op);
                                    
                                    op = new op_equal();
                                }
                            }
                        }
                    }
                }
                
            }
            
            String str1 = strs[0];
            
            String str2 = strs[1];
            
            String relation_name1 = str1.substring(0, str1.indexOf(parser)).trim();
            
            String relation_name2 = new String();
            
            String arg1 = str1.substring(str1.indexOf(parser) + 1, str1.length()).trim();

            String arg2 = str2.substring(str2.indexOf(parser) + 1, str2.length()).trim();
                
//              subgoal2 = strs2[0] + "_" + strs2[1];
                
                relation_name2 = str2.substring(0, str2.indexOf(parser)).trim();
                
                
            Argument Arg1 = new Argument(arg1, relation_name1);
            
            Argument Arg2 = new Argument(arg2, relation_name2);
            
            return new Conditions(Arg1, relation_name1, op, Arg2, relation_name2);

            
        }

    }
	
	
	static String[] split_relation_attr_name(String head_var_str)
	{
		head_var_str = head_var_str.trim();
		
		String relation_name = head_var_str.substring(0, head_var_str.indexOf(init.separator));
		
		String attr_name = head_var_str.substring(head_var_str.indexOf(init.separator) + 1, head_var_str.length());
		
		String [] values = {relation_name, attr_name};
		
		return values;
	}
	
	
	static Vector<Argument> get_head_vars(int id, Connection c, PreparedStatement pst) throws SQLException
    {
        String query = "select head_variables from view_table where view = '" + id + "'";
        
        pst = c.prepareStatement(query);
        
        ResultSet rs = pst.executeQuery();
        
//      String id = new String();
        
        Vector<Argument> head_var = new Vector<Argument>();
        
        String head_var_str = new String();
        
        if(rs.next())
        {           
            head_var_str = rs.getString(1).trim();
        }
        
        String [] head_var_strs = head_var_str.split(",");
        
        for(int i = 0; i<head_var_strs.length; i++)
        {
            
          String []values = split_relation_attr_name(head_var_strs[i]);
            
            String arg_name = head_var_strs[i].trim();
            
//            Argument arg = name_arg_mappings.get(arg_name);
            
          Argument arg = new Argument(arg_name, values[0]);
            
            head_var.add(arg);
        }
        
        
        return head_var;
//      return id;
            
    }
	
	static Vector<Argument> get_head_vars(int id, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select head_variables from view_table where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
//		String id = new String();
		
		Vector<Argument> head_var = new Vector<Argument>();
		
		String head_var_str = new String();
		
		if(rs.next())
		{			
			head_var_str = rs.getString(1).trim();
		}
		
		String [] head_var_strs = head_var_str.split(",");
		
		for(int i = 0; i<head_var_strs.length; i++)
		{
			
//			String []values = split_relation_attr_name(head_var_strs[i]);
			
			String arg_name = head_var_strs[i].trim();
			
			Argument arg = name_arg_mappings.get(arg_name);
			
//			Argument arg = new Argument(, values[0]);
			
			head_var.add(arg);
		}
		
		
		return head_var;
//		return id;
			
	}
	
	static int get_id_head_vars(String name, Vector<Argument> head_var, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view, head_variables from view_table where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int id = 0;
		
		String head_var_str = new String();
		
		if(rs.next())
		{
			id = rs.getInt(1);
			
			head_var_str = rs.getString(2).trim();
		}
		
		String [] head_var_strs = head_var_str.split(",");
		
		for(int i = 0; i<head_var_strs.length; i++)
		{
			String []values = split_relation_attr_name(head_var_strs[i]);
			
			Argument arg = new Argument(head_var_strs[i].trim(), values[0]);
			
			head_var.add(arg);
		}
		
		return id;
			
	}
	
	static Vector<Subgoal> get_view_subgoals(int name, HashMap<String, String> subgoal_name_mapping, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_subgoals = "select subgoal_names, subgoal_origin_names from view2subgoals where view = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1);
			
			String subgoal_origin_name = r.getString(2);
			
			Vector<Argument> args = new Vector<Argument>();
			
			subgoal_name_mapping.put(subgoal_name, subgoal_origin_name);
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	static Vector<Subgoal> get_view_subgoals_full(int name, HashMap<String, String> subgoal_name_mapping, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_subgoals = "select subgoal_names, subgoal_origin_names from view2subgoals where view = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1).trim();
			
			String subgoal_origin_name = r.getString(2).trim();
			
			Vector<Argument> args = get_full_schema(subgoal_name, subgoal_origin_name, name_arg_mappings, c, pst);
			
			subgoal_name_mapping.put(subgoal_name, subgoal_origin_name);
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	
	public static Vector<Argument> get_full_schema(String subgoal_name, String subgoal_origin_name, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + subgoal_origin_name +"' order by column_name";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Argument> args = new Vector<Argument>();
		
		while(rs.next())
		{
			String attr_name = rs.getString(1);
			
			String data_type = rs.getString(2);
			
			if(!attr_name.endsWith(init.provenance_column_suffix))
			{
			  
			  Argument arg = new Argument(subgoal_name + init.separator + rs.getString(1), subgoal_name); 
			  
			  arg.set_data_type(data_type);
			  
			  args.add(arg);
			  
			  name_arg_mappings.put(arg.name, arg);

			}
		}
		
		return args;
		
	}
	
	static Vector<Lambda_term> get_view_lambda_terms(int name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select lambda_term, table_name from view2lambda_term where view = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		while(rs.next())
		{
			String lambda_str = rs.getString(1).trim();
			
			String table_name = rs.getString(2).trim();
			
			Lambda_term l_term = new Lambda_term(lambda_str, table_name);
			
			lambda_terms.add(l_term);
		}
		
		return lambda_terms;
		
	}
	
	   static Vector<Lambda_term> get_view_lambda_terms(int name, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
	    {
	        String query = "select lambda_term, table_name from view2lambda_term where view = '" + name + "'";
	        
	        pst = c.prepareStatement(query);
	        
	        ResultSet rs = pst.executeQuery();
	        
	        Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
	        
	        while(rs.next())
	        {
	            String lambda_str = rs.getString(1).trim();
	            
	            String table_name = rs.getString(2).trim();
	            
	            Argument arg = name_arg_mappings.get(lambda_str);
	            
	            Lambda_term l_term = new Lambda_term(arg);
	            
	            lambda_terms.add(l_term);
	        }
	        
	        return lambda_terms;
	        
	    }
	
	
	static int insert_view(Query view, String name, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
        
//        update_table(view, c, pst);
        
        int seq = get_view_num(c, pst);
        
        view.name = name;//"v" + seq;
        
//        System.out.println(view.toString());
        
        insert_view_table(seq, view, c, pst);
        
        insert_view_conditions(seq, view, c, pst);
        
        insert_view_lambda_term(seq, view, c, pst);
        
        insert_view_subgoals(seq, view, c, pst);
        
//        String id = "v" + seq;
        
        return seq;
        
	}
	
	
	public static int get_view_num(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select max(view) from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		if(rs.next())
		{
			num = rs.getInt(1) + 1;
		}
		
		return num;
	}
	
	static void insert_view_table(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		String view_name = view.name;
		
		String head_vars_str = new String();
		
		for(int i = 0; i<view.head.args.size(); i++)
		{
			Argument arg = (Argument)view.head.args.get(i);
			
			if(i >= 1)
				head_vars_str += ",";
			
			head_vars_str += arg.name;
		}
		
		String query = "insert into view_table values ('" + seq + "','" + head_vars_str + "','" + view_name + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static void insert_view_subgoals(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			String query = "insert into view2subgoals values ('" + seq + "','" + subgoal.name + "','" + view.subgoal_name_mapping.get(subgoal.name) + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	
	static void insert_view_lambda_term(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{		
		String lambda_term_str = new String();
		
		String lambda_term_table_name = new String();
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			
			
			String query = "insert into view2lambda_term values ('" + seq + "','" + view.lambda_term.get(i).toString() + "','" + view.lambda_term.get(i).table_name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
			
//			if(i >= 1)
//			{
//				lambda_term_str += populate_db.separator;
//				
//				lambda_term_table_name += populate_db.separator;
//			}
//			
//			lambda_term_str += view.lambda_term.get(i).toString();
//			
//			lambda_term_table_name += view.lambda_term.get(i).table_name;
		}
		
//		if(!view.lambda_term.isEmpty())
//		{
//			String query = "insert into view2lambda_term values ('" + seq + "','" + lambda_term_str + "','" + lambda_term_table_name + "')";
//			
//			pst = c.prepareStatement(query);
//			
//			pst.execute();
//		}
		

		
		
	}
	
	static void insert_view_conditions(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<view.conditions.size(); i++)
		{			
			String query = "insert into view2conditions values ('" + seq + "','" + view.conditions.get(i).toStringinsql() + "')";
			
			pst = c.prepareStatement(query);
			
//			System.out.println(query);
			
			pst.execute();
			
		}
	}
	
	
	static void update_table(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String [] base_relations = new String[view.body.size()];
		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) view.body.get(i);
			
			base_relations[i] = subgoal.name;
		}
		
//		rename_base_relation.create_table(c, pst, base_relations);
	}
	
	
	static Vector<String> get_args(String table_name) throws SQLException, ClassNotFoundException
	{
		 Class.forName("org.postgresql.Driver");
         Connection c = DriverManager
            .getConnection(init.db_url,
        	        init.usr_name,init.passwd);
         
//         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
         
         
//         String get_arg_sql = "select arguments from subgoal_arguments where subgoal_names = '" + table_name + "'";
         
         String get_arg_sql = "SELECT column_name  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '"+ table_name +"'";
         
         PreparedStatement pst = c.prepareStatement(get_arg_sql);
         
         ResultSet rs = pst.executeQuery();
         
         
         Vector<String> args = new Vector<String>();
         
         while(rs.next())
         {
        	 args.add(rs.getString(1));
         }
         
         
         c.close();
         
         return args;
	}

//	static String get_full_query(String query) throws ClassNotFoundException, SQLException
//	{
//		
//		
//		String head = query.split(":")[0];
//		
//		String []subgoals = query.split(":")[1].split(",");
//		
//		String subgoal_str = new String();
//		
//		boolean start = false;
//		
//		for(int i = 0; i<subgoals.length; i++)
//		{
//			if(subgoals[i].contains("()"))
//			{
//				if(start)
//				{
//					subgoal_str += ",";
//				}
//				
//				subgoals[i] = subgoals[i].trim();
//				
//				subgoals[i] = subgoals[i].substring(0, subgoals[i].length() - 2).trim();
//				
//				subgoal_str += subgoals[i] + "(";
//				
//				Vector<String> args = Parse_datalog.get_columns(subgoals[i]);
//				
//				for(int k = 0; k<args.size(); k++)
//				{
//					if(k >= 1)
//						subgoal_str += ",";
//					
//					subgoal_str += args.get(k).trim();
//				}
//				
//				subgoal_str += ")";
//				
//				start = true;
//			}
//			else
//			{
//				subgoal_str += "," + subgoals[i];
//			}
//		}
//		
//		String update_q = head + ":" + subgoal_str;
//		
//		return update_q;
//	}
//	
	
	
	

}
