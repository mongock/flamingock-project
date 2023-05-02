package io.mongock.driver.mongodb.sync.v4.driver.changes;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;

@ChangeUnit( id="change-1" , order = "1")
public class ChangeUnit1 {

    @Execution
    public void execution() {
        System.out.println("\n\n+*********EXECUTION***********\n\n");
    }
}
