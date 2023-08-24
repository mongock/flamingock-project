package io.flamingock.core.audit;

import io.flamingock.core.audit.writer.AuditItem;
import io.flamingock.core.util.Result;

/**
 * This class implements the Facade pattern containing the responsibility to log the taskStep, map it to Entry
 * and log then entry just in order to avoid having too many classes to implement that depend on the
 * same AuditEntry implementation, which would enforce to add the generic to the holder/orchestrator class.
 * <br />
 * However, the `mapper responsibility` is intended to be delegated to a Mapper class, but that's is left to decide
 * to the developer implementing this abstract class.
 */
public interface AuditWriter {

    Result writeStep(AuditItem auditItem);


}
