/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.client.model;

public class ApiResponse<T> {

    public static class ErrorResponse {

        private String errorCode;
        private String errorMessage;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
            result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ErrorResponse other = (ErrorResponse) obj;
            if (errorCode == null) {
                if (other.errorCode != null) return false;
            } else if (!errorCode.equals(other.errorCode)) return false;
            if (errorMessage == null) {
                if (other.errorMessage != null) return false;
            } else if (!errorMessage.equals(other.errorMessage)) return false;
            return true;
        }

        @Override
        public String toString() {
            return "ErrorResponse [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
        }
    }

    private ErrorResponse error;
    private T response;

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((response == null) ? 0 : response.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ApiResponse<T> other = (ApiResponse<T>) obj;
        if (error == null) {
            if (other.error != null) return false;
        } else if (!error.equals(other.error)) return false;
        if (response == null) {
            if (other.response != null) return false;
        } else if (!response.equals(other.response)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ApiResponse [error =" + error + ", response=" + response + "]";
    }
}