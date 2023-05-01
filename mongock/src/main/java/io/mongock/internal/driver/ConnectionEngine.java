package io.mongock.internal.driver;

import io.mongock.internal.MongockConfiguration;
import io.mongock.internal.MongockLockProvider;

public interface ConnectionEngine<DRIVER_CONFIGURATION extends DriverConfiguration> {

  ConnectionEngine<DRIVER_CONFIGURATION>  setDriverConfiguration(DRIVER_CONFIGURATION driverConfiguration);
  ConnectionEngine<DRIVER_CONFIGURATION>  setMongockConfiguration(MongockConfiguration mongockConfiguration);

  void initialize();

  MongockAuditor getAuditor();

  MongockLockProvider getLockProvider();
}
