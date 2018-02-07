package edu.upenn.cis.citation.gen_citations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.init.init;
import edu.upenn.cis.citation.pre_processing.Query_operation;
import edu.upenn.cis.citation.views.Query_converter;
import edu.upenn.cis.citation.views.Single_view;

public class Gen_citation {
  
  public static String db_name = "IUPHAR/BPS Guide to PHARMACOLOGY";
  
  public static HashSet<String> gen_citation_entire_query(HashMap<Single_view, HashSet<Tuple>> valid_view_mappings, HashSet<citation_view_vector> covering_sets, HashMap<Tuple, HashSet<Integer>> tuple_valid_rows, ArrayList<Vector<Head_strs>> all_why_tokens, HashMap<String, Integer> max_num, Connection c, PreparedStatement pst) throws SQLException, JSONException
  {
    
    HashMap<Tuple, HashSet<Head_strs>> view_mapping_lambda_values_mappings = new HashMap<Tuple, HashSet<Head_strs>>();
    
    Set<Single_view> views = valid_view_mappings.keySet();
    
//    HashMap<Single_view, HashMap<String, Query>> view_citation_queries_mappings = new HashMap<Single_view, HashMap<String, Query>>();
    
    HashMap<Tuple, HashMap<String, HashSet<String>>> view_mapping_citation_mappings = new HashMap<Tuple, HashMap<String, HashSet<String>>>();
    
    for(Iterator iter = views.iterator(); iter.hasNext();)
    {
      Single_view view = (Single_view) iter.next();
      
      HashSet<Tuple> tuples = valid_view_mappings.get(view);
      
      for(Tuple tuple: tuples)
      {
        HashSet<Integer> valid_row_ids = tuple_valid_rows.get(tuple);
        
        HashSet<Head_strs> lambda_values = view.get_lambda_values(tuple, all_why_tokens, valid_row_ids);
        
        view_mapping_lambda_values_mappings.put(tuple, lambda_values);
      }
      
      HashMap<String, Integer> query_ids = view.get_citation_queries(c, pst);
      
      HashMap<String, Query> block_query_mappings = new HashMap<String, Query>();
      
      Set<String> block_names = query_ids.keySet();
      
      for(String block_name:block_names)
      {
        Integer query_id = query_ids.get(block_name);
        
        Query query = Query_operation.get_query_by_id(query_id, c, pst);
        
        block_query_mappings.put(block_name, query);
        
      }
      
      
      for(Tuple tuple:tuples)
      {
        HashMap<String, HashSet<String>> citations = new HashMap<String, HashSet<String>>();
        
        for(String block_name:block_names)
        {
          Query citation_query = block_query_mappings.get(block_name);
          
          gen_citation_per_view_mapping(view, tuple, block_name, citation_query, view_mapping_lambda_values_mappings.get(tuple), citations, c, pst);
          
        }
        
        view_mapping_citation_mappings.put(tuple, citations);
      }
      
//      view_citation_queries_mappings.put(view, queries);
      
      
    }
    

//    ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, schema_level, c, pst);
              
    ArrayList<HashMap<String, HashSet<String>>> full_citations = gen_citations_covering_set_level(view_mapping_citation_mappings, covering_sets);
      
      return gen_citations(full_citations, max_num);
  }
  
  static HashSet<String> gen_citations(ArrayList<HashMap<String, HashSet<String>>> author_lists, HashMap<String, Integer> max_num) throws JSONException
  {
      HashSet<String> json_string = new HashSet<String>();
          
      for(int i = 0; i<author_lists.size(); i++)
      {
//        boolean empty = false;
          
          HashMap<String, HashSet<String>> authors = author_lists.get(i);
          
          JSONObject json = get_json_citation(authors);
          
          if(json == null)
              continue;
      
          json_string.add(json.toString());
      }
      
      return json_string;
  }
  
  public static JSONObject get_json_citation(HashMap<String, HashSet<String>> json_citation) throws JSONException
  {
      JSONObject json_obj = new JSONObject();
      
      Set<String> keys = json_citation.keySet();
      
      for(Iterator iter = keys.iterator(); iter.hasNext();)
      {
          String key = (String) iter.next();
          
          HashSet<String> content = json_citation.get(key);
          
//        if(key.equals("author") && content.isEmpty())
//            return null;
          
          content = reorder_hashset(content);
          
          JSONArray json_author = new JSONArray();
          
          for(Iterator it = content.iterator(); it.hasNext();)
          {
              
//            if(num >= 1)
//                author_str += ",";
//            
//            author_str += iter.next();
              json_author.put(it.next());
          }
          
          json_obj.put(key, json_author);
      }
      
      json_obj.put("db_name", db_name);
      
      return json_obj;
  }
  
  public static HashSet<String> reorder_hashset(HashSet<String> str_list)
  {
      List<String> sortedList = new ArrayList(str_list);
      
      Collections.sort(sortedList);
      
      return new HashSet<String>(sortedList);
  }
  
  static void gen_citation_per_view_mapping(Single_view view, Tuple tuple, String block_name, Query query, HashSet<Head_strs> lambda_values, HashMap<String, HashSet<String>> citations, Connection c, PreparedStatement pst) throws SQLException
  {
    String query_base = Query_converter.datalog2sql(query);
   
    if(query.lambda_term.size() > 0)
    {
//        citation_view_parametered curr_view = (citation_view_parametered) single_view;
        
        Vector<Integer> l_term_ids = new Vector<Integer>();
        
        for(int k = 0; k<tuple.lambda_terms.size(); k++)
        {
            
            int id = tuple.lambda_terms.indexOf(query.lambda_term.get(k));
            
            l_term_ids.add(id);
        }
        
        String sql = get_full_query_string(query_base, query, lambda_values, l_term_ids);
                        
        gen_single_citations(sql, block_name, citations, c, pst);
    }
    else
    {
        gen_single_citations(query_base, block_name, citations, c, pst);
    }
    
  }
  
  static String get_full_query_string(String query_base, Query q, HashSet<Head_strs> lambda_values, Vector<Integer> l_term_ids)
  {
      String [] l_values = new String[l_term_ids.size()];
      
      
      
      String query = query_base;
      
      int num = 0;
      
      HashSet<Head_strs> cq_lambda_values = new HashSet<Head_strs>();
      
      for(Iterator iter = lambda_values.iterator(); iter.hasNext();)
      {
          Head_strs head_value = (Head_strs) iter.next();
          
          Vector<String> curr_cq_lambda_values = new Vector<String>();
          
          for(int k = 0; k<l_term_ids.size(); k++)
          {
              curr_cq_lambda_values.add(head_value.head_vals.get(l_term_ids.get(k)));
              
//            if(num >= 1)
//                l_values[k] += ",";
//            
//            if(l_values[k] != null)
//                l_values[k] += "'" + head_value.head_vals.get(l_term_ids.get(k)) + "'";
//            else
//                l_values[k] = "'" + head_value.head_vals.get(l_term_ids.get(k)) + "'";
          }
          
          Head_strs curr_cq_head_strs = new Head_strs(curr_cq_lambda_values);
          
          cq_lambda_values.add(curr_cq_head_strs);
      }
      
      for(Iterator iter = cq_lambda_values.iterator(); iter.hasNext();)
      {
          Head_strs head_value = (Head_strs) iter.next();
                      
          for(int k = 0; k<head_value.head_vals.size(); k++)
          {               
              if(num >= 1)
                  l_values[k] += ",";
              
              String value = head_value.head_vals.get(k);
              
              if(value!= null && value.contains("'"))
              {
                  value = value.replaceAll("'", "''");
              }
              
              
              if(l_values[k] != null)
                  l_values[k] += "'" + value + "'";
              else
                  l_values[k] = "'" + value + "'";
          }
          
          num ++;
      }
      
      if(q.conditions == null || q.conditions.size() == 0)
      {
          query += " where (";
      }
      else
      {
          query += " and (";
      }
      
      for(int k = 0; k<l_term_ids.size(); k++)
      {
          
          if(k >= 1)
              query += ",";
          
          String l_name = q.lambda_term.get(k).arg_name;
          
          String table_name = l_name.substring(0, l_name.indexOf(init.separator));
          
          String attr_name = l_name.substring(l_name.indexOf(init.separator) + 1, l_name.length());
          
          query += "cast (" + table_name + "." + attr_name + " as text)";
      }
      
      query += ") in (select * from unnest(";
      
      for(int k = 0; k<l_term_ids.size(); k++)
      {
          if(k >= 1)
              query += ",";
          
          query += "ARRAY[" + l_values[k] + "]";
      }
      
      query += "))";
      
      return query;
  }
  
  static void gen_single_citations(String sql, String block_name, HashMap<String, HashSet<String>> citations, Connection c, PreparedStatement pst) throws SQLException
  {
      pst = c.prepareStatement(sql);
      
      ResultSet rs = pst.executeQuery();
      
      HashSet<String> values = new HashSet<String>();
      
      ResultSetMetaData meta = rs.getMetaData();
      
      int col_num = meta.getColumnCount();
      
      while(rs.next())
      {
          
          String value = new String();
          
          for(int i = 0; i<col_num; i++)
          {
              if(i >= 1)
                  value += " ";
              
              value += rs.getString(i + 1);
          }
          
          values.add(value);
          
//        if(block_name.equals("author"))
//        {
//            values.add(rs.getString(1) + " " + rs.getString(2));
//        }
          
      }
      
      citations.put(block_name, values);
      
  }
  
  static ArrayList<HashMap<String, HashSet<String>>> gen_citations_covering_set_level(HashMap<Tuple, HashMap<String, HashSet<String>>> citations, HashSet<citation_view_vector> curr_res)
  {
      ArrayList<HashMap<String, HashSet<String>>> full_citations = new ArrayList<HashMap<String, HashSet<String>>>();
      
      for(citation_view_vector c_vector: curr_res)
      {
          
          HashMap<String, HashSet<String>> curr_full_citations = new HashMap<String, HashSet<String>>();
          
          for(citation_view view_mapping: c_vector.c_vec)
          {
              
            Tuple curr_tuple = view_mapping.get_view_tuple();
            
//              String view_key = c_vector.c_vec.get(j).get_name() + init.separator + c_vector.c_vec.get(j).get_table_name_string();
//              
//              int id = view_keys.indexOf(view_key);
              
              HashMap<String, HashSet<String>> curr_citations = citations.get(curr_tuple);
              
              if(curr_full_citations.isEmpty())
              {
                  Set<String> keys = curr_citations.keySet();
                  
                  for(Iterator iter = keys.iterator(); iter.hasNext();)
                  {
                      String key = (String)iter.next();
                      
                      HashSet<String> curr_values = new HashSet<String>();
                      
                      curr_values.addAll(curr_citations.get(key));
                      
                      curr_full_citations.put(key, curr_values);
                  }
                  
              }
              else
              {
                  Set<String> keys = curr_citations.keySet();
                  
                  for(Iterator iter = keys.iterator(); iter.hasNext();)
                  {
                      String key = (String)iter.next();
                      
                      HashSet<String> curr_values = (HashSet<String>) curr_citations.get(key).clone();
                      
                      if(curr_full_citations.containsKey(key))
                      {
                          curr_full_citations.get(key).addAll(curr_values);
                      }
                      else
                      {
                          curr_full_citations.put(key, curr_values);
                      }
                  }
              }
          }
          
          full_citations.add(curr_full_citations);
      }
      
      
      
      return full_citations;
  }

}
