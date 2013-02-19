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

    	String site = "http://finance.yahoo.com/d/quotes.csv?s=";
    	//Reference:http://www.gummy-stuff.org/Yahoo-data.htm
    	String[] fieldNames = {"initials","name","price","volume","dlow","dhigh","date","prevdate"};
    	
    	Scanner userInput = new Scanner(System.in);
    	System.out.print("Type company:");
    	String input = userInput.next();
    	userInput.close();
    	//symbol,name,price,volume,day's low, day's high,trade date,last trade date
    	site = site+input+"&f=snl1vghd1d2";
    	
    	System.out.println("You want to go to: " + site);

    	
		URL myURL = new URL(site);
		URLConnection conn = myURL.openConnection();
		DataInputStream datainput = new DataInputStream(conn.getInputStream());
	    BufferedReader in = new BufferedReader(new InputStreamReader(datainput));
	    
	    MongoClient mongoClient = new MongoClient();
	    DB db = mongoClient.getDB("test");
	    DBCollection coll = db.getCollection("test");
	    
        String inputLine;
        String[] inputArray;
        BasicDBObject query;
	    DBCursor queryCursor;
	    BasicDBObject doc;
        while ((inputLine = in.readLine()) != null){
            System.out.println(inputLine);
            inputArray = inputLine.split(",");
            for(int i = 0; i < inputArray.length; i++){;
            	inputArray[i] = inputArray[i].replace("\"", "");
            }
            
            query = new BasicDBObject("initials",inputArray[0]);
            queryCursor = coll.find(query);
            if(queryCursor.hasNext()) {
            	System.out.println("duplicate exists");
            }else{
            	doc = new BasicDBObject("initials",inputArray[0]);
            	for(int j = 1; j < fieldNames.length; j++){
						doc.append(fieldNames[j], inputArray[j]);
            	}
            	coll.insert(doc);
            }
            queryCursor.close();
        }
	    
	    System.out.println("-ending-");
        DBCursor cursor = coll.find();
        try{
        	while(cursor.hasNext()){
        		System.out.println(cursor.next());
        	}
        } finally {
        	cursor.close();
        }
        
        in.close();
    	
    	
 //   	URL myURL = new URL(site);
 //   	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 //   	DocumentBuilder db = dbf.newDocumentBuilder();
 //   	Document doc = db.parse(new InputSource(myURL.openStream());
 //   	doc.getDocumentElement().normalize();
        
        
        //<span id="yfs_l84_goog">792.89</span>
        
        
        /*
    	URLConnection connection = null;
		try {
			connection = new URL(site).openConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	try {
			InputStream response = connection.getInputStream();
			response.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	*/

	}

}
