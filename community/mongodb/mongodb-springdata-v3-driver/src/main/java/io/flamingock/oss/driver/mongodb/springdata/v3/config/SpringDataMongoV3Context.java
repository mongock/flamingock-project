package io.flamingock.oss.driver.mongodb.springdata.v3.config;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.oss.driver.mongodb.springdata.v3.driver.SpringDataMongoV3Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnExpression("${flamingock.enabled:true}")
@EnableConfigurationProperties(SpringDataMongoV3Configuration.class)
public class SpringDataMongoV3Context {

  @Bean
  public ConnectionDriver<SpringDataMongoV3Configuration> connectionDriver(MongoTemplate mongoTemplate, SpringDataMongoV3Configuration driverConfiguration) {
      return new SpringDataMongoV3Driver(mongoTemplate).setDriverConfiguration(driverConfiguration);
  }
}
