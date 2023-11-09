package io.flamingock.oss.driver.couchbase.springboot.v2.config;

import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.oss.driver.couchbase.CouchbaseConfiguration;
import io.flamingock.oss.driver.couchbase.driver.CouchbaseDriver;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.util.StringUtils;

import com.couchbase.client.java.Collection;

@Configuration
@ConditionalOnExpression("${flamingock.enabled:true}")
@ConditionalOnBean({CouchbaseClientFactory.class})
@EnableConfigurationProperties(CouchbaseSpringbootConfiguration.class)
@AutoConfigureAfter(CouchbaseDataAutoConfiguration.class)
public class CouchbaseSpringbootContext {

  @Bean
  public ConnectionDriver<CouchbaseConfiguration> connectionDriver(CouchbaseClientFactory couchbaseClientFactory,
                                           CouchbaseSpringbootConfiguration couchbaseConfiguration) {
    Collection collection = isCustomCollection(couchbaseConfiguration) ? 
        couchbaseClientFactory.withScope(couchbaseConfiguration.getScope()).getCollection(couchbaseConfiguration.getCollection()) : 
        couchbaseClientFactory.getDefaultCollection();  
    return new CouchbaseDriver(couchbaseClientFactory.getCluster(), collection).setDriverConfiguration(couchbaseConfiguration);
  }
  
  private boolean isCustomCollection(CouchbaseSpringbootConfiguration couchbaseConfiguration){
    return StringUtils.hasText(couchbaseConfiguration.getCollection())  &&
        StringUtils.hasText(couchbaseConfiguration.getScope());
  }
}
