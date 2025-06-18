/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.importer.model;

import io.flamingock.internal.commons.core.audit.AuditEntry;

public enum MongockChangeState {
  EXECUTED, FAILED, ROLLED_BACK, ROLLBACK_FAILED, IGNORED;

  public AuditEntry.Status toAuditStatus() {
    switch (this) {
      case FAILED: return AuditEntry.Status.EXECUTION_FAILED;
      case ROLLED_BACK: return AuditEntry.Status.ROLLED_BACK;
      case ROLLBACK_FAILED: return AuditEntry.Status.ROLLBACK_FAILED;
      default: return AuditEntry.Status.EXECUTED;
    }
  }

}
