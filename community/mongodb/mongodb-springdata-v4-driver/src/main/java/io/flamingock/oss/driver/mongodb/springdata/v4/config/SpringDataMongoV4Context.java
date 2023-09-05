package io.flamingock.oss.driver.mongodb.springdata.v4.config;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.oss.driver.mongodb.springdata.v4.driver.SpringDataMongoV4Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnExpression("${flamingock.enabled:true}")
@EnableConfigurationProperties(SpringDataMongoV4Configuration.class)
public class SpringDataMongoV4Context {

  @Bean
  public ConnectionDriver<SpringDataMongoV4Configuration> connectionDriver(MongoTemplate mongoTemplate, SpringDataMongoV4Configuration driverConfiguration) {
      return new SpringDataMongoV4Driver(mongoTemplate).setDriverConfiguration(driverConfiguration);
  }
}
