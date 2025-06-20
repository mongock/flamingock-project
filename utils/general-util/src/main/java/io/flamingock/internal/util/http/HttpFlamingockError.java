/*
 * Copyright 2023 Flamingock ("https://oss.flamingock.io")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.internal.util.http;

import io.flamingock.internal.util.FlamingockError;

public class HttpFlamingockError extends FlamingockError {

    private final int statusCode;

    public HttpFlamingockError(int statusCode, String errorCode, String message) {
        super(errorCode, statusCode < 500, message);
        this.statusCode = statusCode;
    }

    public HttpFlamingockError(int statusCode, FlamingockError error) {
        super(error.getCode(), error.isRecoverable(), error.getMessage());
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "Error{httpStatus[" + statusCode + "], code[" + getCode() + "], message['" + getMessage() + "', recoverable[" + isRecoverable() + "]]";
    }

}
