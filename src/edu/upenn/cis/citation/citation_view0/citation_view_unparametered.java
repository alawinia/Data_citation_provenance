package edu.upenn.cis.citation.citation_view0;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.init.init;

public class citation_view_unparametered extends citation_view{
	public String name;
	
//	public boolean lambda;	
	public Vector<String> table_names = new Vector<String>();
	
	public String table_name_str = new String();
	
	public double weight = 0.0;
	
	public Tuple view_tuple = null;
	
	public String unique_id_string = new String();
	
	public citation_view_unparametered(String name, Tuple tuple, HashMap<Tuple, ArrayList<Integer>> view_mapping_query_arg_ids_mappings)
	{
		this.name = name;
		
		set_table_name(tuple);
	
		tuple.covered_arg_ids.addAll(view_mapping_query_arg_ids_mappings.get(tuple));
		
		this.view_tuple = tuple;
		
		unique_id_string = get_unique_string_id();
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(init.db_url,
//	    	        init.usr_name,init.passwd);
		
//		gen_table_names(c, pst);
		
//		gen_index();
	    
//	    c.close();
//		lambda = false;
	}
	
	public String get_unique_string_id()
    {
          
          String sorted_mapping_string = get_sorted_mapping_string(this.get_view_tuple(), this.get_view_tuple().mapSubgoals_str);
          
          return (this.get_name() + init.separator + sorted_mapping_string);
          
    }
	
	static String get_sorted_mapping_string(Tuple view_mapping, HashMap<String, String> subgoal_name_mappings)
    {
      Set<String> subgoal_names =  view_mapping.mapSubgoals_str.keySet(); 
      
      Vector<String> subgoal_name_list = new Vector<String>(subgoal_names);
      
      Collections.sort(subgoal_name_list);
      
      String sorted_mapping_string = new String();
      
      int count = 0;
      
      for(String subgoal_name: subgoal_name_list)
      {
        if(count >= 1)
          sorted_mapping_string += ",";
        
        sorted_mapping_string += subgoal_name + "=" + subgoal_name_mappings.get(subgoal_name);
        
        count ++;
      }
      
      return sorted_mapping_string;
      
    }
	
	void set_table_name(Tuple tuple)
	{
		
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			Subgoal subgoal = (Subgoal)iter.next();
			
			table_names.add(subgoal.name);
		}
		
		Collections.sort(table_names);
		
		table_name_str = table_names.toString();
	}
	
	public citation_view_unparametered(String name, Vector<String> table_names) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(init.db_url,
	    	        init.usr_name,init.passwd);
		
//		gen_table_names(c, pst);
	    
	    this.table_names = table_names;
		
//		gen_index();
	    
	    c.close();
//		lambda = false;
	}
	
	public citation_view_unparametered() {
		// TODO Auto-generated constructor stub
	}

	void gen_table_names(Connection c, PreparedStatement pst ) throws ClassNotFoundException, SQLException
	{
		
	    
//	    String lambda_term_query = "select v.subgoal_names from citation_view c join subgoals v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
	    String lambda_term_query = "select subgoal_names from view2subgoals where view = '"+ name +"'";

		
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(!rs.wasNull())
	    {
	    	while(rs.next())
		    {
		    		    	
		    	table_names.add(rs.getString(1));
		    }
	    }
	    
	    else
	    {
	    	lambda_term_query = "select subgoal from web_view_table where renamed_view = '"+ name +"'";

			
		    pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet r = pst.executeQuery();

		    while(r.next())
		    {
		    		    	
		    	table_names.add(r.getString(1));
		    }
	    }
	    
	    
	    

	}

	
//	public citation_view_unparametered(String name, String query)
//	{
//		this.name = name;
//		this.query = query.toLowerCase();
////		lambda = false;
//	}
	
	
	//TODO: consider more complicated queries
	
//	public String get_full_query() throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + init.db_name,
//	        "postgres","123");
//		
//	    String citation_query = "select citation_view_query from citation_view where citation_view_name = '" + name + "'";
//	    
//	    pst = c.prepareStatement(citation_query);
//	    
//	    ResultSet rs = pst.executeQuery();
//	    
//	    String query4citation = new String();
//	    
//	    if(rs.next())
//	    {
//	    	query4citation = rs.getString(1);
//	    }
//	    
//	    Query query = Parse_datalog.parse_query(query4citation);
//	    	    
//	    String q = Query_converter.datalog2sql(query);
//	    
//	    c.close();
//	    
//	    return q;
//		
//	}

	String get_head_variables(Connection c, PreparedStatement pst) throws SQLException
	{
		String citation_query = "select head_variables from citation_view where citation_view_name = '" + name + "'";
	    
	    pst = c.prepareStatement(citation_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    	return rs.getString(1);
	    else
	    	return null;
	}
	
	String get_body(Connection c, PreparedStatement pst) throws SQLException
	{
		String citation_query = "select subgoal_names, arguments from citation_subgoals join subgoal_arguments using (subgoal_names) where citation_view_name = '" + name + "'";
	    
	    pst = c.prepareStatement(citation_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    String body = new String();
	    
	    int num = 0;
	    
	    while(rs.next())
	    {
	    	
	    	if(num >= 1)
	    		body += ",";
	    	body += rs.getString(1) + "(" + rs.getString(2) + ")";
	    	
	    	num++;
	    }
	    return body;
	}
	
	@Override
	public String gen_citation_unit(Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		
		
		
		
		return name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public String get_name() {
		// TODO Auto-generated method stub
		return name;
	}

//	@Override
//	public Vector<String> get_parameters() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Vector<String> get_table_names() {
		// TODO Auto-generated method stub
		return table_names;
	}
	
//	public void gen_index()
//	{
//		index = (char)Integer.parseInt(name.substring(1, name.length()));
//	}

	@Override
	public String get_index() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void put_paras(String para, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean has_lambda_term() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public citation_view clone() {
		// TODO Auto-generated method stub
		citation_view_unparametered c_v = new citation_view_unparametered();
		
//		c_v.index = this.index;
				
		c_v.name = this.name;
				
		c_v.table_names = this.table_names;
		
		c_v.table_name_str = this.table_name_str;
				
		c_v.view_tuple = this.view_tuple;
		
		c_v.unique_id_string = unique_id_string;
		
		return c_v;
	}

	@Override
	public String gen_citation_unit(String name, Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		
		citation_view c = (citation_view) o;
		
		if(c.hashCode() == this.hashCode())
			return true;
		
		return false;
	}

	@Override
	public String get_table_name_string() {
		// TODO Auto-generated method stub
		return table_name_str;
	}
	
	@Override
	public double get_weight_value() {
		// TODO Auto-generated method stub
		return weight;
	}

	@Override
	public void calculate_weight(Query query) {
		// TODO Auto-generated method stub
		this.weight = this.table_names.size() * 1.0/query.body.size();
	}
	
	@Override
	public Tuple get_view_tuple() {
		// TODO Auto-generated method stub
		return view_tuple;
	}

	@Override
	public int hashCode()
	{
	     return unique_id_string.hashCode();

	}
}
