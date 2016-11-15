package com.sparkkafka.SparkKafkaa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoData 
{
    static Gson gson=new GsonBuilder().create();
    static List<String> user_count_list = new ArrayList<String>(); 
    static int count=0;
    static Map<String, Object> userDocument = new HashMap<String, Object>();
    static BasicDBObject mongoObj = new BasicDBObject();
    static BasicDBObject whereQuery = new BasicDBObject();
    
    static MongoClient mongo=new MongoClient("localhost:27017");
    static DB db=mongo.getDB("Twitter");
    static DBCollection collection = db.getCollection("userTweets");
    static DBObject user_result_map = new BasicDBObject();
    static DBObject update_count = new BasicDBObject();
    
    public static String getData(String jsonData)
    {
        
        
        //String jsonObj = jsonData.toString();
        //JsonElement jElement = new JsonParser().parse(jsonData).getAsJsonObject();
        //JsonObject jobject = jElement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject().get("user").getAsJsonObject().get("id_str");
        //String user_id = jobject.toString();
        JsonObject job = new JsonParser().parse(jsonData).getAsJsonObject();//gson.fromJson(jsonObj, JsonObject.class); 
        String tweet_id = job.getAsJsonObject().get("user").getAsJsonObject().get("id_str").toString();
        String location = job.getAsJsonObject().get("user").getAsJsonObject().get("location").toString();
        String user_name = job.getAsJsonObject().get("user").getAsJsonObject().get("name").toString();
        String user_id=tweet_id.replace("\"","");
        String name=user_name.replace("\"","");
        
        
        String str = user_id.toString();
        StringBuilder sb = new StringBuilder();
        user_count_list.add(user_id);
        System.out.println("User_id: "+user_id+" Value: "+Collections.frequency(user_count_list, user_id));
        count = Collections.frequency(user_count_list, user_id);

        /*Set<JsonElement> uniqueSet = new HashSet<JsonElement>(user_count_list);
        for (JsonElement temp : uniqueSet) {
            System.out.println(temp + ": " + Collections.frequency(user_count_list, temp));
            tweetCount = temp + ": " + Collections.frequency(user_count_list, temp);
            sb.append(tweetCount+",\n");
        }*/
        
        	mongoObj.put("user_Id", user_id);
        	mongoObj.put("Name", name);
        	if(count == 1)
            {
        		mongoObj.put("Count", count);
        		collection.insert(new BasicDBObject(mongoObj));
            }
        	else
        	{
        		BasicDBObject whereQuery = new BasicDBObject().append("$set", 
            		    new BasicDBObject().append("Count", count));
            	collection.update(new BasicDBObject().append("user_Id", user_id), whereQuery);
        	}
           
       
        
        //System.out.println("UserId: "+str+" Location: "+location+" Name:"+name);
        //System.out.println("CountList: "+user_count_list);
        //System.out.println("TweetCOunt: "+count);
        return str;
    }
}