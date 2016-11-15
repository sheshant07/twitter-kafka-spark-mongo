package com.sparkkafka.SparkKafkaa;



import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;

import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import org.apache.spark.streaming.kafka010.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;



	public class Consumer implements Serializable {
	

		public static void main(String[] args) throws InterruptedException 
		
		 {
			final String topic1 = "tweetData";

			 SparkConf sparkConf = new SparkConf()
			    		.setAppName("KafkaConnector")
			    		.setMaster("local[4]")
			    		.set("spark.driver.allowMultipleContexts", "true");
		JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));
			
			 
		 Set<String> topics = Collections.singleton(topic1.toString());	
		  
		 Map<String, Object> kafkaParams = new HashMap<String, Object>();
		 kafkaParams.put("bootstrap.servers", "localhost:9092");
		 kafkaParams.put("key.deserializer", StringDeserializer.class);
		 kafkaParams.put("value.deserializer", StringDeserializer.class);
		 kafkaParams.put("auto.offset.reset", "latest");
		 kafkaParams.put("group.id", "java-test-consumer");

		 //Gson gson=new GsonBuilder().create();
		 
		 
		 JavaInputDStream<ConsumerRecord<String, String>> istream1 = KafkaUtils.createDirectStream(
			        ssc,
			        LocationStrategies.PreferConsistent(),
			        ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topic1), kafkaParams));
		   			istream1.foreachRDD(rdd ->{   
	                      rdd.foreach( message ->{
	                    	  
	                          System.out.println(message);
	                          Gson gson=new GsonBuilder().create();
	                          MongoClient mongo=new MongoClient("localhost");
		                      DB db=mongo.getDB("Twitter");
	            	          DBCollection collection = db.getCollection("tweetsData");	
	            	          //DBCollection collection1 = db.getCollection("userTweets");
	            	          String ids=message.toString();
			            	  String data = ids.substring(ids.indexOf("{"));
			            	  DBObject dbObj = (DBObject) JSON.parse(data);
			            	  String jsonObj = dbObj.toString();
			            	  MongoData.getData(jsonObj);
	            	          collection.insert(dbObj);
	            	                  
	            	                  //adding id,location functionality.
	            	                  //DBObject subStr = new BasicDBObject();
	            	          //String rawData=(String) JSON.parse(data);
	            	          //System.out.println(rawData);
	            	          
	            	         
	       			  	
	                        });	    
	                        });
	                     ssc.start();
		            	 ssc.awaitTermination();
		                       
	            	 }
	
}