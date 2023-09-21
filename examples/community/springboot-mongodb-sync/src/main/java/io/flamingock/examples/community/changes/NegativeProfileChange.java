package io.flamingock.examples.community.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import org.springframework.context.annotation.Profile;

@Profile("!accepted-profile-1")
@ChangeUnit( id="profile-not-included-change" , order = "4")
public class NegativeProfileChange {

    @Execution
    public void execution() {
    }
}
