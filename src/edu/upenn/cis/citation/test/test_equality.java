package edu.upenn.cis.citation.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class test_equality {
    
    public static String file1 = "reasoning_results/covering_sets1";
    
    public static String file2 = "reasoning_results/covering_sets2";
    
    static String file3 = "reasoning_results/covering_sets3";
    
    static String file4 = "reasoning_results/covering_sets4";
    
    static String view_mapping_file1 = "reasoning_results/view_mapping_rows1";
    
    static String view_mapping_file2 = "reasoning_results/view_mapping_rows2";
    
    static String citation_file1 = "reasoning_results/citations";
    
    static String citation_file2 = "reasoning_results/citation2";
    
    static String covering_set_group_file1 = "reasoning_results/covering_sets_per_group1";
    
    static String covering_set_group_file2 = "reasoning_results/covering_sets_per_group2";
    
    public static void main(String [] args)
    {
      
    test_equality2();
      
      test_equality1();
      
//    test_equality3();
      
    }
    
    static void test_equality3()
    {
      HashMap<String, Integer> key_value1 = get_view_mappings_rows(view_mapping_file1);
      
      HashMap<String, Integer> key_value2 = get_view_mappings_rows(view_mapping_file2);
      
//    System.out.println(key_value1);
//    
//    System.out.println(key_value2);
      
      System.out.println(key_value1.equals(key_value2));
      
      
    }
    
    static void test_equality2()
    {
//    System.out.println("covering_set_group1");
      
      HashSet<HashSet<String>> arr1 = get_covering_sets_group_strs(covering_set_group_file1);
      
//    System.out.println("covering_set_group2");
      
      System.out.println(arr1);
      
      HashSet<HashSet<String>> arr2 = get_covering_sets_group_strs(covering_set_group_file2);
      
      System.out.println(arr2);
      
      if(arr1.size() != arr2.size())
      {
        System.out.println("false");
        
        return;
      }
      
      for(HashSet<String> curr_arr1: arr1)
      {
        boolean exist = false;
        
        for(HashSet<String> curr_arr2:arr2)
        {
          if(curr_arr2.containsAll(curr_arr1) && curr_arr1.containsAll(curr_arr2))
          {
            exist = true;
            
            break;
          }
        }
        
        if(!exist)
        {
          System.out.println("false");
          
          return;
        }
      }
      
      System.out.println("true");

        
        
    }
    
       public static HashMap<String, Integer> get_view_mappings_rows(String file_name)
        {
            
         HashMap<String, Integer> key_values = new HashMap<String, Integer>();
         
            try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
                String line;
                
                
                HashSet<String> str_list = null;
                
                
                while ((line = br.readLine()) != null) {
                   // process the line.
                  
                  String [] content = line.split(":");
                  
                  String key = content[0];
                  
                  int value = Integer.valueOf(content[1]);
                  
                  key_values.put(key, value);
                  
                }
                
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return key_values;
        }
    
    
    public static HashSet<HashSet<String>> get_covering_sets_group_strs(String file_name)
    {
      HashSet<HashSet<String>> covering_sets = new HashSet<HashSet<String>>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line;
            
            
            HashSet<String> str_list = null;
            
            
            while ((line = br.readLine()) != null) {
               // process the line.
              if(line.startsWith("group "))
              {
                if(str_list != null)
                {
                  covering_sets.add(str_list);
                  
//                  System.out.println(str_list);
                }
                
                str_list = new HashSet<String>();
              }
              else
              {
                str_list.add(line);
              }
            }
            
            if(str_list != null)
            {
              covering_sets.add(str_list);
              
//              System.out.println(str_list);
            }
            
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return covering_sets;
    }

    
    static void test_equality1()
    {
      Vector<String> arr1 = get_view_strs(file1);
      
//      Vector<String> arr2 = get_view_strs(file2);
      
      Vector<String> arr3 = get_view_strs(file2);
      
//      Vector<String> arr4 = get_view_strs(file4);
      
//      System.out.println(arr1);
      
//      System.out.println(arr2);
      
//      System.out.println(arr3);
      
//      System.out.println(arr4);
      
        System.out.println(compare_arrs(arr1, arr3));
        
//        System.out.println(compare_arrs(arr2, arr3));
        
//        System.out.println(compare_arrs(arr3, arr4));
        
        Vector<String> arr1_copy = new Vector<String>();
          
          Vector<String> arr2_copy = new Vector<String>();
          
          arr1_copy.addAll(arr1);
          
          arr2_copy.addAll(arr3);
        
//          arr1.removeAll(arr2);  
          
          arr2_copy.removeAll(arr1_copy);
        
//        System.out.println(arr1);
          
          arr1.removeAll(arr3);
          
          System.out.println(arr1);
        
        System.out.println(arr2_copy);

        
//        HashSet<String> citation1 = get_view_strs1(citation_file1);
//        
//        HashSet<String> citation2 = get_view_strs1(citation_file2);
//        
//        System.out.println("citation:" + citation1.equals(citation2));
    }
    
    static boolean compare_arrs(Vector<String> arr1, Vector<String> arr2)
    {
      Vector<String> arr1_copy = new Vector<String>();
      
      Vector<String> arr2_copy = new Vector<String>();
      
      arr1_copy.addAll(arr1);
      
      arr2_copy.addAll(arr2);
      
      if(arr1_copy.size() != arr2_copy.size())
        return false;
      
      for(int i = 0; i<arr1_copy.size(); i++)
      {
        if(!arr2_copy.remove(arr1_copy.get(i)))
        {
          return false;
        }
      }
      
      return true;
    }
    
    public static Vector<String> get_view_strs(String file_name)
    {
        Vector<String> covering_sets = new Vector<String>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line;
            
            while ((line = br.readLine()) != null) {
               // process the line.
                
                covering_sets.add(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return covering_sets;
    }
    
    public static HashSet<String> get_view_strs1(String file_name)
    {
        HashSet<String> covering_sets = new HashSet<String>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line;
            
            while ((line = br.readLine()) != null) {
               // process the line.
                
                covering_sets.add(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return covering_sets;
    }

}
