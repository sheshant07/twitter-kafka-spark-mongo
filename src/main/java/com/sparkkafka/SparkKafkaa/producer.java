package com.sparkkafka.SparkKafkaa;


import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.producer.KafkaProducer;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
public class producer {
	
	private static final String topic = "tweetData";

	
	public static void run(String consumerKey, String consumerSecret,
			String token, String secret) throws InterruptedException {

		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:9092");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("client.id", "camus");		
		KafkaProducer<String, String> producer = new KafkaProducer <String , String>(properties);
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
				endpoint.trackTerms(Lists.newArrayList("twitterapi","#Apple"));

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token,
				secret);
		Client client = new ClientBuilder().hosts(Constants.STREAM_HOST)
				.endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		client.connect();
		for (int msgRead = 0; msgRead >= 0; msgRead++) {
			ProducerRecord<String, String> message = null;
			try {
				message = new ProducerRecord<String, String>(topic, queue.take());
			     //System.out.println(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			producer.send(message);
		}
		producer.close();
		client.stop();

	}

	public static void main(String[] args) {
		try {
			producer.run(" ", " ", " ", " ");
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

}
