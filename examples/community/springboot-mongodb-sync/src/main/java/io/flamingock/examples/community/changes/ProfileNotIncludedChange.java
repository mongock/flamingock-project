package io.flamingock.examples.community.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.examples.community.ChangesTracker;
import org.springframework.context.annotation.Profile;

@Profile("not-accepted-profile")
@ChangeUnit( id="profile-not-included-change" , order = "4")
public class ProfileNotIncludedChange {

    @Execution
    public void execution() {
        ChangesTracker.add(getClass().getName());
    }
}
