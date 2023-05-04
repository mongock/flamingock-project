package io.mongock.driver.mongodb.sync.v4.driver.changes;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.driver.mongodb.sync.v4.driver.MyDependency;

@ChangeUnit( id="change-3" , order = "1")
public class ChangeUnit3 {

    @Execution
    public void execution(MyDependency myDependency) {
        System.out.println("\n\n+*********STARTING EXECUTION(3)***********\n\n");
        System.out.println(myDependency.getMessage());
        System.out.println("\n\n+*********FINISHED EXECUTION(3)***********\n\n");
    }
}
