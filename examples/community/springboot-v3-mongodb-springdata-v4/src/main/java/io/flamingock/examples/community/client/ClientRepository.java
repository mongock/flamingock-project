package io.flamingock.examples.community.client;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.flamingock.examples.community.CommunitySpringbootMongodbSpringdataApp;

@Repository(CommunitySpringbootMongodbSpringdataApp.CLIENTS_COLLECTION_NAME)
public interface ClientRepository extends MongoRepository<Client, String> {

}
