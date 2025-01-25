package io.flamingock.core.cloud.utils;

import java.time.LocalDateTime;

public class MyThirdPartyDependency {

    public String getDataFromRemoteServer() {
        return LocalDateTime.now().toString();
    }
}
