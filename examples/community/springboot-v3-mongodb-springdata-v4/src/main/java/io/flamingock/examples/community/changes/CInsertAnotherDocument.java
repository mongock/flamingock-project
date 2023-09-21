package io.flamingock.examples.community.changes;

import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.examples.community.client.Client;
import io.flamingock.examples.community.client.ClientRepository;

@ChangeUnit( id="insert-another-document" , order = "3")
public class CInsertAnotherDocument {

    @Execution
    public void execution(ClientRepository clientRepository) {
        clientRepository.save(new Client("Jorge", null, null, null));
    }
}
