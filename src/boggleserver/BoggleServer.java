/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boggleserver;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Justin
 */
public class BoggleServer {

    /**
     * @param args the command line arguments
     */
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader bufferedReader;
    private static String inputLine;
    private static PrintWriter pw;
    
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(63400);
            clientSocket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            pw = new PrintWriter(clientSocket.getOutputStream(),true);
            
            while(true){
                inputLine = bufferedReader.readLine();
                 
               if(inputLine!=null){
                    System.out.println(inputLine);

                    if(inputLine.toLowerCase().contains("lookup1")){
                        System.out.println("Lookup case...");
                        
                        String val = inputLine.substring(inputLine.indexOf('|')+1);
                        System.out.println("LOOKUP1: "+val);
                        
                        boolean ret = lookupID(val);
                        if(ret){
                            System.out.println("Exists.");
                            pw.println("1");
                        } else {
                            System.out.println("Empty");                           
                            pw.println("0");
                        }
                        
                        System.out.println("Closing...");
                        pw.println("-1");
                        
                    } else if(inputLine.toLowerCase().contains("retrieve2")){
                        System.out.println("Retrieve from identity...");
                        
                        String val = inputLine.substring(inputLine.indexOf('|')+1);
                        System.out.println("RETRIEVE2: "+val);
                        
                        Set<String> words = retrieveWords(val);
                        if(!words.isEmpty()){
                            System.out.println("Found some words");
                        }
                        
                        //TODO: Write response
                        
                    } else if(inputLine.toLowerCase().contains("insid3")){
                        System.out.println("Insert ID into tables...");
                        
                        String val = inputLine.substring(inputLine.indexOf('|')+1);
                        val = val.substring(0,val.indexOf('|'));
                        String ss1 = inputLine.substring(inputLine.indexOf('|')+1);
                        System.out.println(ss1);
                        ss1 = ss1.substring(ss1.indexOf('|')+1);
                        System.out.println(ss1);
                        String ct = ss1.substring(0,ss1.indexOf('|'));
                        String sc = ss1.substring(ss1.indexOf('|')+1);
                        
                        int count = Integer.parseInt(ct);
                        int score = Integer.parseInt(sc);
                        
                        System.out.println("VAL:"+val+" COUNT:"+count+" TEST"+score);
                        
                        insertID(val,count,score);
                        
                        System.out.println("Exiting...");
                    }
                }
               System.out.println("----------RESETTING");
                    serverSocket.close();
                    serverSocket = new ServerSocket(63400);
                    clientSocket = serverSocket.accept();
                    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    pw = new PrintWriter(clientSocket.getOutputStream(),true);
            }
        } catch (IOException e){
            System.out.println("ERROR: "+e);   
        } finally {
            try {
                serverSocket.close();
                clientSocket.close();
                bufferedReader.close();
            } catch (Exception e){
                System.out.println("Are you fucking retarded?\n"+e);
            }
        }
    }
    
    //MySQL Lookup for identity
    public static boolean lookupID(String identity){
        boolean output = false;
        Connection con = null; //Connection. Ignore
        Statement st = null; //Execution Statements
        ResultSet rs = null; //Return values
        
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            String lookup = "SELECT * FROM boardids WHERE identity = \""+identity+"\";";
            System.out.println("LOOKUP: "+lookup);
            rs = st.executeQuery(lookup);
            
            while(rs.next()){
                System.out.println(rs.getString(1)+" "+rs.getString(2));
                output=true;
            }
            
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } finally {
            try{
                if(rs!=null){
                    rs.close();
                }
                if(st!=null){
                    st.close();
                }
                if(con!=null){
                    con.close();
                }
            } catch(SQLException e){
                System.out.println("SQL EXCEPTION 2: "+e);
            } 
        }
        return output;
    }
    
    //Retrieves a set of all possible words of the given identity
    public static Set<String> retrieveWords(String parentID){
        Set<String> words = new HashSet<String>();
        
        Connection con = null; //Connection. Ignore
        Statement st = null; //Execution Statements
        ResultSet rs = null; //Return values
        
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            String lookup = "SELECT * FROM words WHERE parentID = '"+parentID+"';";
            System.out.println("LOOKUP: "+lookup);
            rs = st.executeQuery(lookup);
            
            while(rs.next()){
                System.out.println(rs.getString(1));
                words.add(rs.getString(1));
            }
            
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } finally {
            try{
                if(rs!=null){
                    rs.close();
                }
                if(st!=null){
                    st.close();
                }
                if(con!=null){
                    con.close();
                }
            } catch(SQLException e){
                System.out.println("SQL EXCEPTION 2: "+e);
            } 
        }
        
        return words;
    }
    
    //Inserts the ID into the boardids table
    public static void insertID(String identity, int count, int score){
        Connection con = null; //Connection. Ignore
        Statement st = null; //Execution Statements
        ResultSet rs = null; //Return values
        
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            String val = "INSERT INTO boardids VALUES ('"+identity+"',"+count+","+score+");";
            System.out.println("L:"+val);
            st.executeUpdate(val);
            
            String lookup = "SELECT * FROM boardids;";
            rs = st.executeQuery(lookup);
            
            while(rs.next()){
                System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+rs.getInt(3));
            }
            
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } finally {
            try{
                if(rs!=null){
                    rs.close();
                }
                if(st!=null){
                    st.close();
                }
                if(con!=null){
                    con.close();
                }
            } catch(SQLException e){
                System.out.println("SQL EXCEPTION 2: "+e);
            } 
        }
    }
    
    //Inserts the words into the word table
    public static void insertWords(){
        
    }
}

/*Connection con = null; //Connection. Ignore
        Statement st = null; //Execution Statements
        ResultSet rs = null; //Return values
        
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            //st.executeUpdate("INSERT INTO words VALUES (\"word\",\"test\",1);");
            //st.executeUpdate("INSERT INTO words VALUES (\"limpy\",\"test\",2);");
            
            rs = st.executeQuery("SELECT * FROM words;");
            
            while(rs.next()){
                System.out.println(rs.getString(1)+" "+rs.getString(2));
            }
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } finally {
            try{
                if(rs!=null){
                    rs.close();
                }
                if(st!=null){
                    st.close();
                }
                if(con!=null){
                    con.close();
                }
            } catch(SQLException e){
                System.out.println("SQL EXCEPTION 2: "+e);
            } 
        }*/