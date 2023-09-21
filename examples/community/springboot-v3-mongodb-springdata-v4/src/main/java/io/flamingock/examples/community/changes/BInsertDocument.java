package io.flamingock.examples.community.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.examples.community.client.Client;
import io.flamingock.examples.community.client.ClientRepository;

@ChangeUnit( id="insert-document" , order = "2")
public class BInsertDocument {

    @Execution
    public void execution(ClientRepository clientRepository) {
        clientRepository.save(new Client("Federico", null, null, null));
    }
}
