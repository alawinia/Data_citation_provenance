package edu.upenn.cis.citation.query;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import org.apache.logging.log4j.Level;
import org.gprom.jdbc.driver.GProMConnection;
import org.gprom.jdbc.utility.PropertyConfigurator;
import com.sun.jna.Native;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.init.init;
import edu.upenn.cis.citation.prov_reasoning.Prov_reasoning4;
import edu.upenn.cis.citation.user_query.query_storage;
import edu.upenn.cis.citation.views.Query_converter;

public class Query_provenance {
  
  public static GProMConnection con = null;
  
  public static String sql_result_file = "provenance_instance.txt";
  
  
  public static String separator = "|";
  
  public static String separator_input = "\\" + separator;
//  static String usr_name = null;
//  
//  static String passwd = null;
  
  public static void connect(String url, String usr_name, String passwd) throws ClassNotFoundException, SQLException
  {
    System.setProperty("java.library.path", "./lib");
    
    Native.setProtected(true);
    
    String driverURL = "org.postgresql.Driver";
    
    Class.forName("org.gprom.jdbc.driver.GProMDriver");
    Class.forName(driverURL);
    
    PropertyConfigurator.configureDefaultConsoleLogger(Level.OFF);
    
    Properties info = new Properties();
    
    info.setProperty("user", usr_name);
    info.setProperty("password", passwd);
//    log.error("made it this far");
    
    try{
      con = (GProMConnection) DriverManager.getConnection(url,info);
  } catch (Exception e){
      e.printStackTrace();
      System.err.println("Something went wrong while connecting to the database.");
      System.exit(-1);
  }
    
  }
  
  public static ResultSet get_provenance4query(Query query, boolean test_case) throws SQLException, FileNotFoundException, UnsupportedEncodingException
  {
    
    String sql = null;
    
    if(!test_case)
      sql = Query_converter.data2sql_with_why_token_columns(query);
    else
      sql = Query_converter.data2sql_with_why_token_columns_test(query);
    
    System.out.println(sql);
    
    Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    
    ResultSet rs = null;
    
    rs = st.executeQuery(sql);
    
    System.out.println(sql);
    
    return rs;
    
    
  }
  
  
  public static void reset() throws SQLException
  {
    con.close();
  }
  
  
  private static void printResult(ResultSet rs) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
    /********************************************************************************/
    
    
    PrintWriter writer = new PrintWriter(sql_result_file);
    
    int row_num = 0;
    
    while(rs.next()) {      
      for(int i = 1; i < rs.getMetaData().getColumnCount(); i++)
      {
        writer.print(rs.getString(i) + separator);
      }
      
      writer.print(rs.getString(rs.getMetaData().getColumnCount()));
      
      writer.println();
      
      row_num++;
  }
    
    System.out.println(row_num);
    
    writer.close();
    
//    System.out.println();
//    System.out.println("-------------------------------------------------------------------------------");
//    for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
//        System.out.print(rs.getMetaData().getColumnLabel(i) + "\t|");
//    System.out.println();
//    System.out.println("-------------------------------------------------------------------------------");
//    
//    
//    System.out.println("-------------------------------------------------------------------------------");
//    System.out.println();
//    System.out.println();
}
  
  public static void main(String [] args) throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException
  {
    boolean test_case = Boolean.valueOf(args[0]);
    
    Class.forName("org.postgresql.Driver");
    Connection c = DriverManager
        .getConnection(init.db_url, init.usr_name , init.passwd);
    
    PreparedStatement pst = null;
    
    Query query = Load_views_and_citation_queries.get_views("query", c, pst).get(0);

    System.out.println(query.toString());
    
    c.close();
    
    
    connect(init.db_prov_url, init.usr_name, init.passwd);
    
//    Query query = query_storage.get_query_by_id(1, con, pst);
    
    
    ResultSet rs = get_provenance4query(query, test_case);
    
    printResult(rs);
    
    reset();
  }
  
  public static Vector<String[]> get_provenance_instance() throws IOException
  {
    Vector<String[]> provenance_instances = new Vector<String[]>();
    
    FileReader fileReader = 
        new FileReader(sql_result_file);

    // Always wrap FileReader in BufferedReader.
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    String line = null;
    
    while((line = bufferedReader.readLine()) != null) {
        String[] curr_provenance_row = line.split(separator_input, -1);
        
        provenance_instances.add(curr_provenance_row);
    }   

    // Always close files.
    bufferedReader.close();      
    
    
    return provenance_instances;
    
  }
  

}
