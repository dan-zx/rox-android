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
            StringBuilder builder = new StringBuilder();
            builder.append("ErrorResponse [errorCode=").append(errorCode).append(", errorMessage=").append(errorMessage).append("]");
            return builder.toString();
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
        StringBuilder builder = new StringBuilder();
        builder.append("ApiResponse [error =").append(error).append(", response=").append(response).append("]");
        return builder.toString();
    }
}