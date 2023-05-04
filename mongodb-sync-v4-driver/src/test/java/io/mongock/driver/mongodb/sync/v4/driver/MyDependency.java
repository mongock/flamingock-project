package io.mongock.driver.mongodb.sync.v4.driver;

import java.time.LocalDateTime;

public class MyDependency {

    public String getMessage() {
        return "IT was called at " + LocalDateTime.now();
    }
}
