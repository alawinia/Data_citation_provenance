package edu.upenn.cis.citation.citation_view0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view0.citation_view;
import edu.upenn.cis.citation.init.*;
import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;


import static org.junit.Assert.*;

public class Covering_set {
	
	public HashSet<citation_view> c_vec;
	
	public Vector<String> index_vec;
	
	public HashSet<Integer> head_variables = new HashSet<Integer>();
	
	public HashSet<String> table_names;
		
	public String index_str;
	
	public String view_name_str;
	
	public String table_name_str;
	
	public String unique_str_id = new String();
//	
//	public String head_var_string = new String();
	
	public Covering_set()
	{
		c_vec = new HashSet<citation_view>();
		
		index_vec = new Vector<String>();
		
		table_names = new HashSet<String>();
		
	}
	
	public static void main(String [] args)
	{
		HashSet<String> h1 = new HashSet<String>();
		
		HashSet<String> h2 = new HashSet<String>();
		
		h1.add("123");
		
		h1.add("456");
		
		
		h2.add("456");
		
	      h2.add("123");

		
		System.out.println(h1.hashCode());
		
		System.out.println(h2.hashCode());
		
		
	}
	
	public Covering_set(Vector<citation_view> vec){
		
		
		index_vec = new Vector<String>();
		
//		Vector<String> curr_table_name_str = new Vector<String>();
		
		table_names = new HashSet<String>();
		
		c_vec = new HashSet<citation_view>();
		
		view_name_str = new String();
		
		index_str = new String();
		
		table_name_str = new String();
		
		for(int i = 0; i<vec.size(); i++)
		{
			
			if(!table_names.containsAll(vec.get(i).get_table_names()) || !head_variables.contains(vec.get(i).get_view_tuple().getArgs()))
			{
				table_names.addAll(vec.get(i).get_table_names());
				
//				insert_sort(index_vec, vec.get(i).toString());
				
				c_vec.add(vec.get(i));
				
				head_variables.addAll(vec.get(i).get_view_tuple().getArgs());
				
			}

		}
		
		unique_str_id = get_unique_string_id();
		
//		for(citation_view c: c_vec)
//		{
//			index_str += "(" + c.get_name() + ")";
//			
//			index_vec.add(c.get_name());
//			
//			view_name_str += c.toString().replaceFirst("\\(.*\\)", "");
//			
//			table_name_str += c.get_table_name_string();
//		}
		
	}
	
	public Covering_set merge(citation_view c)
	{
		
		HashSet<citation_view> vec_new = (HashSet<citation_view>) c_vec.clone();
				
		Vector<String> index_new = new Vector<String>();
//				
		HashSet<String> table_names_new = (HashSet<String>) table_names.clone();
		
		String index_str_new = new String();
		
		String view_name_str_new = new String();
		
		String table_name_str = new String();
		
		if(!table_names_new.containsAll(c.get_table_names()) || !head_variables.containsAll(c.get_view_tuple().covered_arg_ids))
		{
			table_names_new.addAll(c.get_table_names());
			
//			insert_sort(index_new, c.toString());
			
			vec_new.add(c);
			
//			insert_sort(vec_new, c);
			
//			for(int j = 1; j<index_new.size(); j++)
//			{
//				if(index_new.get(j).compareTo(index_new.get(j - 1))< 0)
//				{
//					assertEquals(1, 0);
//				}
//			}
			
//			vec_new.add(c);
			

		}
		
//		for(citation_view view_mapping: vec_new)
//		{
//			index_str_new += "(" + view_mapping.get_name() + ")";
//			
//			index_new.add(view_mapping.toString());
//			
//			String str = view_mapping.toString().replaceFirst("\\(.*\\)", "");
//						
//			view_name_str_new += str;
//			
//			table_name_str += view_mapping.get_table_name_string();
//		}
		
		Covering_set c_v = new Covering_set(vec_new, index_new, table_names_new);
		
//		c_v.head_variables = new HashSet<Argument>();
		
		c_v.head_variables = this.head_variables;
		
		c_v.head_variables.addAll(c.get_view_tuple().covered_arg_ids);
		
		c_v.index_str = index_str_new;
		
		c_v.view_name_str = view_name_str_new;
		
		c_v.table_name_str = table_name_str;
		
		c_v.unique_str_id = c_v.get_unique_string_id();
		
		return c_v;
	}
	
//	public void insert_sort(Vector<String> index_vec, String insert_str)
//	{
//		if(index_vec.size() == 0)
//		{
//			index_vec.add(insert_str);
//			return;
//		
//		}
//		
//		String first_str = index_vec.firstElement();
//		
//		if(insert_str.compareTo(first_str) < 0)
//		{
//			index_vec.insertElementAt(insert_str, 0);
//			
//			return;
//		}
//		
//		String last_str = index_vec.lastElement();
//		
//		if(insert_str.compareTo(last_str) > 0)
//		{
//			index_vec.add(insert_str);
//			
//			return;
//		}
//		
//		
//		int pos = sort_insert.binary_search(index_vec, insert_str, new binary_compare<String>(){
//			
//			@Override			
//			public int compare(String a, String b)
//			{
//				if(a.compareTo(b) > 0)
//					return 1;
//				else
//				{
//					if(a.compareTo(b) < 0)
//						return -1;
//					else
//						return 0;
//				}
//			}
//		} );
//		
//		
//		index_vec.insertElementAt(insert_str, pos);
//		
//	}
//	
//	public void insert_sort(Vector<citation_view> index_vec, citation_view insert_str)
//	{
//		if(index_vec.size() == 0)
//		{
//			index_vec.add(insert_str);
//			return;
//		
//		}
//		
//		String s1 = insert_str.get_name() + insert_str.get_table_name_string();
//		
//		citation_view first_str = index_vec.firstElement();
//		
//		String s2 = first_str.get_name() + first_str.get_table_name_string();
//		
//		if(s1.compareTo(s2) < 0)
//		{
//			index_vec.insertElementAt(insert_str, 0);
//			
//			return;
//		}
//		
//		citation_view last_str = index_vec.lastElement();
//		
//		s2 = last_str.get_name() + last_str.get_table_name_string();
//		
//		if(s1.compareTo(s2) > 0)
//		{
//			index_vec.add(insert_str);
//			
//			return;
//		}
//		
//		
//		int pos = sort_insert.binary_search(index_vec, insert_str, new binary_compare<citation_view>(){
//			
//			@Override			
//			public int compare(citation_view a, citation_view b)
//			{
//				String s1 = b.get_name() + b.get_table_name_string();
//				
//				String s2 = a.get_name() + a.get_table_name_string();
//
//				if(s2.compareTo(s1) > 0)
//					return 1;
//				else
//				{
//					if(s2.compareTo(s1) < 0)
//						return -1;
//					else
//						return 0;
//				}
//			}
//		} );
//		
//		
//		index_vec.insertElementAt(insert_str, pos);
//		
//	}
//	public static int binary_search(Vector<citation_view> list, citation_view item, binary_compare bc)
//	{
//		
////		int pos = list.size()/2;
//		
//		int start = 0;
//		
//		int end = list.size() - 1;
//		
//		int pos = (start + end) /2;
//		
//		while(start < end)
//		{
//			
//			
//			
//			int cmp_v = bc.compare(item, list.get(pos)); 
//			
//			if(cmp_v < 0)
//			{
//				end = pos - 1;
//			}
//			else
//			{
//				if(cmp_v > 0)
//				{
//					start = pos + 1;
//				}
//				else
//					break;
//			}
//			
//			pos = (start + end) /2;
//			
//		}
//		
//		int cmp_v = bc.compare(item, list.get(pos)); 
//		
//		if(cmp_v > 0)
//		{
//			pos = pos + 1;
//		}
//		else
//		{
//			if(cmp_v == 0)
//			{
//				if(item.toString().compareTo(list.get(pos).toString()) > 0)
//				{
//					pos = pos + 1;
//				}
//			}
//		}
//		
//		
//		return pos;
//	}
	
	public Covering_set(HashSet<citation_view> vec, Vector<String> index_vec, HashSet<String> table_names){
		
		this.c_vec = vec;
		
		this.index_vec = index_vec;
		
		this.table_names = table_names;
//		this.view_strs = tuple_cores;
		
	}
	
	public Covering_set(citation_view c){
		
		c_vec = new HashSet<citation_view>();
		
		c_vec.add(c);
		
		index_vec = new Vector<String>();
		
		table_names = new HashSet<String>();
		
		table_names.addAll(c.get_table_names());
		
		index_vec.add(c.get_index());
		
		table_name_str = c.get_table_name_string();
		
		index_str = "(" + c.get_name() + ")";
		
		view_name_str = new String();
		
		view_name_str = c.get_name();
		
//		head_variables.addAll(c.get_view_tuple().getArgs());
		head_variables.addAll(c.get_view_tuple().covered_arg_ids);
		
		unique_str_id = get_unique_string_id();
		
	}
	
	
	public static Covering_set merge(Covering_set vec, citation_view c)
	{
		
		return vec.merge(c);
		
//		if(vec.view_strs.contains(c.toString()))
//		{
//			return vec;
//		}
//		else
//			return merge_vector(new citation_view_vector(c), vec);
	}
	

	public static Covering_set merge(Covering_set vec, Covering_set c)
	{
//		Covering_set c1 = vec.clone();
//		
//		Covering_set c2 = c.clone();
		
		HashSet<citation_view> new_citation_vec = new HashSet<citation_view>();
		
		HashSet<String> tables = new HashSet<String>();
		
		tables.addAll(vec.table_names);
		
		tables.addAll(c.table_names);
		
		HashSet<Integer> args = new HashSet<Integer>();
		
		args.addAll(vec.head_variables);
		
		args.addAll(c.head_variables);
		
		new_citation_vec.addAll(vec.c_vec);
		
		new_citation_vec.addAll(c.c_vec);
		
		int i = 0;
		
		int j = 0;
		
		String index_str = new String();
		
		Vector<String> index_vec = new Vector<String>();
		
		String view_name_str = new String();
		
		String table_name_str = new String();
		
//		while(i < c1.c_vec.size() && j < c2.c_vec.size())
//		{
//			citation_view v1 = c1.c_vec.get(i);
//			
//			citation_view v2 = c2.c_vec.get(j);
//			
//			String k1 = v1.get_name() + v1.get_table_name_string();
//			
//			String k2 = v2.get_name() + v2.get_table_name_string();
//			
//			if(k1.compareTo(k2) < 0)
//			{
//				new_citation_vec.add(v1);
//				
//				index_str += "(" + v1.get_name() + ")";
//				
//				index_vec.add(v1.get_name());
//				
//				view_name_str += v1.toString().replaceFirst("\\(.*\\)", "");
//				
//				table_name_str += v1.get_table_name_string();
//				
//				i++;
//			}
//			else
//			{
//				if(k1.compareTo(k2) > 0)
//				{
//					new_citation_vec.add(v2);
//					
//					index_str += "(" + v2.get_name() + ")";
//					
//					index_vec.add(v2.get_name());
//					
//					view_name_str += v2.toString().replaceFirst("\\(.*\\)", "");
//					
//					table_name_str += v2.get_table_name_string();
//					
//					j++;
//				}
//				else
//				{
//					new_citation_vec.add(v1);
//					
//					index_str += "(" + v1.get_name() + ")";
//					
//					index_vec.add(v1.get_name());
//					
//					view_name_str += v1.toString().replaceFirst("\\(.*\\)", "");
//					
//					table_name_str += v1.get_table_name_string();
//					
//					i++;
//					
//					j++;
//				}
//			}
//		}
//		
//		if(i < c1.c_vec.size())
//		{
//			while(i < c1.c_vec.size())
//			{
//				citation_view v1 = c1.c_vec.get(i);
//				
//				new_citation_vec.add(v1);
//				
//				index_str += "(" + v1.get_name() + ")";
//				
//				index_vec.add(v1.get_name());
//				
//				view_name_str += v1.toString().replaceFirst("\\(.*\\)", "");
//				
//				table_name_str += v1.get_table_name_string();
//				
//				i++;
//			}
//		}
//		else
//		{
//			if(j < c2.c_vec.size())
//			{
//				while(j < c2.c_vec.size())
//				{
//					citation_view v2 = c2.c_vec.get(j);
//					
//					new_citation_vec.add(v2);
//					
//					index_str += "(" + v2.get_name() + ")";
//					
//					index_vec.add(v2.get_name());
//					
//					view_name_str += v2.toString().replaceFirst("\\(.*\\)", "");
//					
//					table_name_str += v2.get_table_name_string();
//					
//					j++;
//				}
//			}
//		}
		
		Covering_set merged_citation_view_vectors = new Covering_set(new_citation_vec, index_vec, tables);
		
		merged_citation_view_vectors.head_variables = args;
		
		merged_citation_view_vectors.index_str = index_str;
		
		merged_citation_view_vectors.index_vec = index_vec;
		
		merged_citation_view_vectors.table_name_str = table_name_str;
		
		merged_citation_view_vectors.view_name_str = view_name_str;
		
		merged_citation_view_vectors.unique_str_id = merged_citation_view_vectors.get_unique_string_id();
		
		return merged_citation_view_vectors;
	}
	
	public Covering_set merge(Covering_set c)
	{
		Covering_set updated_covering_set = this.clone();
		
		Covering_set insert_covering_set = c.clone();
		
		return merge(updated_covering_set, insert_covering_set);
	}
	
//	static citation_view_vector merge_vector(citation_view_vector vec1, citation_view_vector vec2)
//	{
//		
//		Vector<citation_view> vec_new = (Vector<citation_view>) vec1.c_vec.clone();
//		
//		
//		vec_new.addAll((vec2.c_vec));
//		
//		Vector<String> index_new = (Vector<String>) vec1.index_vec.clone();
//		
//		index_new.addAll(vec2.index_vec);
//		
//		HashSet<String> tuple_cores_new = (HashSet<String>) vec1.table_names.clone();
//		
//		tuple_cores_new.addAll(vec2.table_names);
//		
//		return new citation_view_vector(vec_new, index_new, tuple_cores_new);
//		
//		
//	}
	
	@Override
	public String toString()
	{
	  String str = new String();
      
      String []str_arr = new String[c_vec.size()];
      
      int i = 0;
      
      for(citation_view view_mappping:c_vec)
      {
        
        
        str_arr[i] = view_mappping.get_view_tuple().toString();
          
        i++;
      }
      
      Arrays.sort(str_arr);
      
      for(i = 0; i<str_arr.length; i++)
      {
        if(i >= 1)
          str += "*";
        str += str_arr[i].toString();
      }
      
      return str;
	}

	
	public Covering_set clone()
	{
		Covering_set c_v = new Covering_set();
		
		for(citation_view view_mapping: c_vec)
		{
			c_v.c_vec.add(view_mapping.clone());
		}
		
		for(int i = 0; i<this.index_vec.size(); i++)
		{
			c_v.index_vec.add(index_vec.get(i));
		}
		
		c_v.table_names.addAll(table_names);
		
		c_v.index_str = index_str;
		
		c_v.view_name_str = view_name_str;
		
		c_v.table_name_str = table_name_str;
		
		c_v.head_variables = (HashSet<Integer>) head_variables.clone();
		
		c_v.unique_str_id = unique_str_id;
		
		return c_v;
		
		
		
	}
	
	@Override
	public boolean equals(Object o)
	{
		Covering_set c_vec = (Covering_set) o;
		
//		if(c_vec.c_vec.size() != this.c_vec.size())
//			return false;
//		
//		for(int i = 0; i < c_vec.c_vec.size(); i++)
//		{
//			citation_view c1 = c_vec.c_vec.get(i);
//			
//			int j = 0;
//			
//			for(j = 0; j< this.c_vec.size(); j++)
//			{
//				citation_view c2 = this.c_vec.get(j);
//				
//				if(c1.equals(c2))
//					break;
//			}
//			
//			if(j < this.c_vec.size())
//				continue;
//			else
//				return false;
//		}
//		
//		for(int i = 0; i < this.c_vec.size(); i++)
//		{
//			citation_view c1 = this.c_vec.get(i);
//			
//			int j = 0;
//			
//			for(j = 0; j< c_vec.c_vec.size(); j++)
//			{
//				citation_view c2 = c_vec.c_vec.get(j);
//				
//				if(c1.equals(c2))
//					break;
//			}
//			
//			if(j < c_vec.c_vec.size())
//				continue;
//			else
//				return false;
//		}
		
		return c_vec.unique_str_id.equals(this.unique_str_id);
	}
	
	@Override
	public int hashCode()
	{
		return unique_str_id.hashCode();
	}
	
	public String get_unique_string_id()
	{
	  Vector<String> view_mapping_id = new Vector<String>();	  
	  
//		String string = new String();
		
		for(citation_view view_mapping: c_vec)
		{
		  
		  String sorted_mapping_string = get_sorted_mapping_string(view_mapping.get_view_tuple(), view_mapping.get_view_tuple().mapSubgoals_str);
		  
		  view_mapping_id.add(view_mapping.get_name() + init.separator + sorted_mapping_string);
		  
		}
		
		Collections.sort(view_mapping_id);
		
		return view_mapping_id.toString();
		
//		return string;
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

}
