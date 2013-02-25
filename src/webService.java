import java.io.*;
import java.net.*;
import java.util.*;

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
	
	static String[] fieldNames = {"initials","name","price","volume","dlow","dhigh","lastdate","movavg(50days)"};
	
	public static String[] promptUser(){
		String[] userinputs;
    	@SuppressWarnings("resource")
		Scanner userInput = new Scanner(System.in).useDelimiter("\\n");
    	System.out.print("Type company (max. 10):");
    	String input = userInput.next();
    	userInput.close();
    	userinputs = input.split(",");
    	//System.out.println("input:"+input);
    	return userinputs;
	}
	
	public static String[] getCoNames(String[] userinputs){
		for(int k = 0; k < userinputs.length; k++){
			userinputs[k] = userinputs[k].replaceAll("\\W","");
			userinputs[k] = userinputs[k].replaceAll("\\s", "").toUpperCase();
		}
		return userinputs;
	}
	
	public static String[] getSites(String[] userinputs){
		String presite = "http://finance.yahoo.com/d/quotes.csv?s=";
		//Reference:http://www.gummy-stuff.org/Yahoo-data.htm
		for(int k = 0; k < userinputs.length; k++){
			//symbol,name,price,volume,day's low, day's high,trade date,last trade date
			userinputs[k] = presite+userinputs[k]+"&f=snl1vghd1m3";
		}
		return userinputs;
	}
	
	public static void getInfo(String[] coNames,String[] site) throws IOException{
		MongoClient mongoClient = new MongoClient();
	    DB db = mongoClient.getDB("test");
	    DBCollection coll = db.getCollection("test");
	    
	    URL myURL;
	    URLConnection conn;
	    DataInputStream datainput;
	    BufferedReader in;
	    String inputLine;
        String[] inputArray;
        BasicDBObject query;
	    DBCursor queryCursor;
	    String queryCheck;
	    String lastStoredDate;
	    
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
	            	lastStoredDate = getStoredDate(queryCheck);
	            	if(lastStoredDate.equals(inputArray[6])){
	            		//System.out.println("Duplicate exists");
	            		printResult(queryCheck);
	            	} else {
	            		System.out.println("Database information out of date. Updating...");
	            		coll.remove(query);
	            		editDB(coll, inputArray);
	            	}
	            }else{
	            	editDB(coll, inputArray);
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
	}
	
	public static void printResult(String queryCheck){
		String output;
	    String[] outputTemp;
		outputTemp = queryCheck.split("} , ");
		output = Prettify(outputTemp[1]);
		System.out.println(output);
	}
	
	public static void editDB(DBCollection coll, String[] inputArray){
		String queryCheck;
	    DBCursor queryCursor;
	    String output;
	    String[] outputTemp;
	    BasicDBObject doc = new BasicDBObject("initials",inputArray[0]);
    	for(int j = 1; j < fieldNames.length; j++){
				doc.append(fieldNames[j], inputArray[j]);
    	}
    	coll.insert(doc);
    	queryCursor = coll.find(doc);
    	if(queryCursor.hasNext()){
    		queryCheck = (queryCursor.next()).toString();
    		printResult(queryCheck);
    	}
	}
	
	public static String getStoredDate(String cursorinput){
		String[] queryCheckTemp;
	    String[] queryCheckArray;
		//queryCheck = (cursorinput.next()).toString();
    	queryCheckTemp = cursorinput.split(",");
    	queryCheckArray = queryCheckTemp[7].split(" : ");
    	queryCheckArray[1] = queryCheckArray[1].replace("\"","");
    	queryCheckArray[1] = queryCheckArray[1].trim();
		return queryCheckArray[1];
	}
	
	public static String Prettify(String input){
		String prettified;
		prettified = input;
		prettified = prettified.substring(0,prettified.length()-1);
		prettified = prettified.replace("\"","");
		prettified = prettified.replace(" : ",":");
		prettified = prettified.replace(" ,",",");
		prettified = prettified.trim();
		return prettified;
	}
	
	public static void main(String[] args) throws IOException {
		String[] userinputs = promptUser();
		String[] coNames = getCoNames(userinputs);
		String[] site = getSites(userinputs);
    	getInfo(coNames, site);
        //<span id="yfs_l84_goog">792.89</span>
	}

}
