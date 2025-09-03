package com.pragma.powerup.infrastructure.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.lang.NonNull;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.database:powerup-traceability-dev}")
  private String databaseName;

  @Value(
      "${MONGODB_URI:mongodb+srv://davidgarciae_db_user:Rzakn5E7qdkXWu4k@powerup-traceability-de.ukuxpvx.mongodb.net/?retryWrites=true&w=majority&appName=powerup-traceability-dev}")
  private String mongoUri;

  @Override
  @NonNull
  protected String getDatabaseName() {
    return databaseName;
  }

  @Override
  @Bean
  @NonNull
  public MongoClient mongoClient() {
    return MongoClients.create(mongoUri);
  }
}
