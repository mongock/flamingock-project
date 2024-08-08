package io.flamingock.core.cloud;

import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class CloudConnectionEngineCloser {
    private static final Logger logger = LoggerFactory.getLogger(CloudConnectionEngineCloser.class);

    private final Http.RequestBuilderFactory requestBuilderFactory;
    private final CloudTransactioner transactioner;

    public CloudConnectionEngineCloser(Http.RequestBuilderFactory requestBuilderFactory, CloudTransactioner transactioner) {
        this.requestBuilderFactory = requestBuilderFactory;
        this.transactioner = transactioner;
    }

    public void close() {
        if (requestBuilderFactory != null) {
            try {
                requestBuilderFactory.close();
            } catch (IOException ex) {
                logger.warn("Error closing request builder factory", ex);
            }
        }
        if (transactioner != null) {
            try {
                transactioner.close();
            } catch (Exception ex) {
                logger.warn("Error closing transactioner", ex);

            }
        }
    }

}
