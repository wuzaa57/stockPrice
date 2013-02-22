import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

public class webService {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

    	String presite = "http://finance.yahoo.com/d/quotes.csv?s=";
    	//Reference:http://www.gummy-stuff.org/Yahoo-data.htm
    	String[] fieldNames = {"initials","name","price","volume","dlow","dhigh","date","prevdate"};
    	
    	String[] coNames;
    	String[] site;
    	site = new String[10];
    	//String[] searchResult;
    	
    	Scanner userInput = new Scanner(System.in);
    	System.out.print("Type company:");
    	String input = userInput.next();
    	userInput.close();
    	coNames = input.split(",");
    	for(int k = 0; k < coNames.length; k++){
    		coNames[k] = coNames[k].replaceAll("\\s", "").toUpperCase();
    		
    		//symbol,name,price,volume,day's low, day's high,trade date,last trade date
        	site[k] = presite+coNames[k]+"&f=snl1vghd1d2";
        	//System.out.println("You want to go to: " + site[k]);
    	}
    	
    	MongoClient mongoClient = new MongoClient();
	    DB db = mongoClient.getDB("test");
	    DBCollection coll = db.getCollection("test");
	    
	    /*//method of searching before accessing website
	    BasicDBObject search;
	    DBCursor searchCursor;
	    for(int a = 0; a < coNames.length; a++){
	    	search = new BasicDBObject("initials",coNames[a]);
	    	searchCursor = coll.find(search);
	    	System.out.println(search);
	    	System.out.println(searchCursor.hasNext());
	    	while(searchCursor.hasNext()){
    				System.out.println("Duplicate");
    				searchResult = ((String) searchCursor.next()).split(",");
	    			System.out.println(searchCursor.next());
	    	}
	    	searchCursor.close();
	    }*/
	    

	    URL myURL;
	    URLConnection conn;
	    DataInputStream datainput;
	    BufferedReader in;
	    String inputLine;
        String[] inputArray;
        BasicDBObject query;
	    DBCursor queryCursor;
	    BasicDBObject doc;
	    
	    String queryCheck;
	    String[] queryCheckTemp;
	    String[] queryCheckArray;
	    
	    String output;
	    String[] outputTemp;
	    
	    for(int b = 0; b < coNames.length; b++){
	    	//System.out.println("Site: "+site[b]);
	    	myURL = new URL(site[b]);
			conn = myURL.openConnection();
			datainput = new DataInputStream(conn.getInputStream());
		    in = new BufferedReader(new InputStreamReader(datainput));
	        
	        while ((inputLine = in.readLine()) != null){
	            //System.out.println(inputLine);
	            inputArray = inputLine.split(",");
	            for(int i = 0; i < inputArray.length; i++){;
	            	inputArray[i] = inputArray[i].replace("\"", "");
	            	inputArray[i] = inputArray[i].trim();
	            	//System.out.println("inputArray["+i+"]: "+inputArray[i]);
	            }
	            
	            query = new BasicDBObject("initials",inputArray[0]);
	            queryCursor = coll.find(query);
	            if(queryCursor.hasNext()) {
	            	//System.out.println("checking for duplicates...");
	            	queryCheck = (queryCursor.next()).toString();
	            	queryCheckTemp = queryCheck.split(",");
	            	queryCheckArray = queryCheckTemp[7].split(" : ");
	            	queryCheckArray[1] = queryCheckArray[1].replace("\"","");
	            	queryCheckArray[1] = queryCheckArray[1].trim();
	            	if(queryCheckArray[1].equals(inputArray[6])){
	            		//System.out.println("Duplicate exists");
	            		outputTemp = queryCheck.split("} , ");
	            		output = outputTemp[1];
	            		output = output.replace("\"","");
	            		output = output.trim();
	            		System.out.println(output);
	            	} else {
	            		System.out.println("Updating...");
	            		coll.remove(query);
	            		doc = new BasicDBObject("initials",inputArray[0]);
		            	for(int j = 1; j < fieldNames.length; j++){
								doc.append(fieldNames[j], inputArray[j]);
		            	}
		            	coll.insert(doc);
	            	}
	            }else{
	            	doc = new BasicDBObject("initials",inputArray[0]);
	            	for(int j = 1; j < fieldNames.length; j++){
							doc.append(fieldNames[j], inputArray[j]);
	            	}
	            	coll.insert(doc);
	            }
	            queryCursor.close();
	        }
		    
		    //System.out.println("-ending-");
	        /*//get all data in db
	        DBCursor cursor = coll.find();
	        try{
	        	while(cursor.hasNext()){
	        		System.out.println(cursor.next());
	        	}
	        } finally {
	        	cursor.close();
	        }*/
	        
	        in.close();
	    }
    	
 //   	URL myURL = new URL(site);
 //   	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 //   	DocumentBuilder db = dbf.newDocumentBuilder();
 //   	Document doc = db.parse(new InputSource(myURL.openStream());
 //   	doc.getDocumentElement().normalize();
        
        
        //<span id="yfs_l84_goog">792.89</span>


	}

}
