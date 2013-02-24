import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;


public class mongodbCleanup {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		MongoClient mongoClient = new MongoClient();
	    DB db = mongoClient.getDB("test");
	    DBCollection coll = db.getCollection("test");
	    
	    
	    BasicDBObject query = new BasicDBObject("initials","AAPL");
	    DBCursor queryCursor = coll.find(query);
	    System.out.println("queryCursor: "+queryCursor);
	    try {
	    	   while(queryCursor.hasNext()) {
	    	       System.out.println(queryCursor.next());
	    	   }
	    	} finally {
	    	   queryCursor.close();
	    	}
	    
	    BasicDBObject query2 = new BasicDBObject("initials","GOOG");
	    DBCursor queryCursor2 = coll.find(query2);
	    System.out.println("queryCursor2: "+queryCursor2);
	    //coll.remove(query);
	    try {
    	   while(queryCursor2.hasNext()) {
    	       System.out.println(queryCursor2.next());
    	   }
    	} finally {
    	   queryCursor.close();
    	}
	    
	    
	    
	    //DBObject doc = coll.findOne();
        //coll.remove(doc);
        
        
	    System.out.println("ending");
        DBCursor cursor = coll.find();
        
        try{
        	while(cursor.hasNext()){
        		System.out.println(cursor.next());
        	}
        } finally {
        	cursor.close();
        }
        
	}

}
