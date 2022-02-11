package com.statistiquescovid.scheduler.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
	
	@Value("${spring.data.mongodb.host}")
	private String mongoHost;
    @Value("${spring.data.mongodb.port}")
    private String mongoPort;
    @Value("${spring.data.mongodb.database}")
    private String mongoDB;
    
    @Bean
    public MongoClient mongo() {
    	Integer port = Integer.parseInt(mongoPort);
    	ConnectionString connectionString = new ConnectionString("mongodb://" + mongoHost + ":" + port/* + "/" +mongoDB*/);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .build();
        
        return MongoClients.create(mongoClientSettings);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), mongoDB);
    }

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}
	
	@Override
    protected boolean autoIndexCreation() {
        return true;
    }
    
    
}
