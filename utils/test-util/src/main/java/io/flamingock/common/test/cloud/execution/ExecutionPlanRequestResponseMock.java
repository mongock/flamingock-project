package io.flamingock.common.test.cloud.execution;


import io.flamingock.common.test.cloud.mock.MockRequestResponseTask;
import io.flamingock.common.test.cloud.MockRunnerServer;

public class ExecutionPlanRequestResponseMock extends ExecutionBaseRequestResponseMock {

    public ExecutionPlanRequestResponseMock(String executionId, MockRequestResponseTask... stages) {
        this(executionId, MockRunnerServer.DEFAULT_ACQUIRED_FOR_MILLIS, MockRunnerServer.DEFAULT_LOCK_ACQUISITION_ID, stages);

    }

    public ExecutionPlanRequestResponseMock(String executionId,
                                            long acquiredForMillis,
                                            String acquisitionId,
                                            MockRequestResponseTask... stages) {
        super(executionId, acquiredForMillis, acquisitionId, stages);
    }

}
