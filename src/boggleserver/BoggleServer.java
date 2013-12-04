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
            while(true){
                serverSocket = new ServerSocket(63400);
                clientSocket = serverSocket.accept();
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pw = new PrintWriter(clientSocket.getOutputStream(),true);
                
                inputLine = bufferedReader.readLine();
                 
                if(inputLine!=null){
                    System.out.println("COMMAND: "+inputLine);
                    
                    if(inputLine.toLowerCase().contains("lookup1")){
                        System.out.println("Lookup case...");
                        
                        String identity = inputLine.substring(inputLine.indexOf('|')+1);
                        System.out.println("ID: "+identity);
                        
                        boolean found = lookupIdentity(identity);
                        if(found){
                            System.out.println("Found!");
                            pw.println(1);
                        } else if(!found){
                            System.out.println("Didn't find!");
                            pw.println(-1);
                        }
                        
                        System.out.println("Exiting lookup1...");
                    } else if(inputLine.toLowerCase().contains("retrieve2")){
                        System.out.println("Retrieve from identity...");                        
                        
                    } else if(inputLine.toLowerCase().contains("insert3")){
                        List<String> toAdd = new ArrayList<String>();
                        
                        System.out.println("Insert ID into tables...");
                        
                        String s1 = inputLine.substring(inputLine.indexOf('|')+1);
                        System.out.println(s1);
                        String identity = s1.substring(0,s1.indexOf('|'));
                        String s2 = s1.substring(s1.indexOf('|')+1);
                        String ct = s2.substring(0,s2.indexOf('|'));
                        String sc = s2.substring(s2.indexOf('|')+1);
                        
                        int count = Integer.parseInt(ct);
                        int score = Integer.parseInt(sc);
                        
                        System.out.println("ID: "+identity+" COUNT: "+ct+" SCORE: "+sc);
                        int check = insertIdentity(identity,count,score);
                        
                        pw.println(check);
                        
                        String inline;
                        while(!(inline = bufferedReader.readLine()).equals("##")){
                            //System.out.println(inline);
                            toAdd.add(inline);
                        }
                        
                        check = insertWords(toAdd);
                        
                        toAdd.clear();
                        toAdd = null;
                        System.out.println("Exiting insert3...");
                    }
                }
                
                System.out.println("----------RESETTING");
                clientSocket.close();
                serverSocket.close();
                bufferedReader.close();
                pw.close();
            }
        } catch (IOException e){
            System.out.println("ERROR: "+e);   
        } finally {
            try {
                serverSocket.close();
                clientSocket.close();
                bufferedReader.close();
                pw.close();
            } catch (Exception e){
                System.out.println("Are you fucking retarded?\n"+e);
            }
        }
    }
    
    public static boolean lookupIdentity(String id){
        boolean found = false;
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            Connection con = null; //Connection. Ignore
            Statement st = null; //Execution Statements
            ResultSet rs = null; //Return values
            
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            rs = st.executeQuery("SELECT * FROM boardids WHERE identity='"+id+"';");
            
            while(rs.next()){
                System.out.println("Found: "+rs.getString(1)+" "+rs.getString(2));
                found = true;
            }
            
            rs.close();
            st.close();
            con.close();
            
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } 
        
        return found;
    }
    
    public static int insertIdentity(String id, int score, int words){
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            Connection con = null; //Connection. Ignore
            Statement st = null; //Execution Statements
            //ResultSet rs = null; //Return values
            
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            st.executeUpdate("INSERT INTO boardids VALUES ('"+id+"',"+score+","+words+");");
            
            //rs.close();
            st.close();
            con.close();
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } 
        
        return 1;
    }
    
    public static int insertWords(List<String> entries){
        String url = "jdbc:mysql://localhost:3306/boggleServer?zeroDateTimeBehavior=convertToNull";
        String usr = "root";
        String pwd = "b7rma5137";
        
        try {
            Connection con = null; //Connection. Ignore
            Statement st = null; //Execution Statements
            //ResultSet rs = null; //Return values
            
            con = DriverManager.getConnection(url,usr,pwd);
            st =  con.createStatement();
            
            for(String s : entries){                
                String word = s.substring(0,s.indexOf('|'));
                String s1 = s.substring(s.indexOf('|')+1);
                String pID = s1.substring(0,s1.indexOf('|'));
                String sc = s1.substring(s1.indexOf('|')+1);
                int score = Integer.parseInt(sc);
                
                //System.out.println("Word: "+word+" pID: "+pID+" SC: "+sc);
                //System.out.println("INSERT INTO words VALUES ('"+word+"','"+pID+"',"+score+");");
                st.executeUpdate("INSERT INTO words VALUES ('"+word+"','"+pID+"',"+score+");");
            }
            //st.executeUpdate("INSERT INTO boardids VALUES ('"+id+"',"+score+","+words+");");
            
            //rs.close();
            st.close();
            con.close();
        } catch (SQLException e){
            System.out.println("SQL EXCEPTION 1: "+e);
        } 
        
        return 1;
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