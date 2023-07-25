package io.flamingock.oss.driver.mongodb.springdata.v2.config;

import io.flamingock.community.internal.driver.ConnectionDriver;
import io.flamingock.oss.driver.mongodb.springdata.v2.driver.SpringDataMongoV2Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnExpression("${flamingock.enabled:true}")
@EnableConfigurationProperties(SpringDataMongoV2Configuration.class)
public class SpringDataMongoV2Context {

  @Bean
  public ConnectionDriver<SpringDataMongoV2Configuration> connectionDriver(MongoTemplate mongoTemplate, SpringDataMongoV2Configuration driverConfiguration) {
      return new SpringDataMongoV2Driver(mongoTemplate).setDriverConfiguration(driverConfiguration);
  }
}
