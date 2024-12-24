package cz.cvut.fel.ear.stepavi2_havriboh.main.rest.handler;

public class ErrorInfo {
    private String errorMessage;
    private String requestUri;

    public ErrorInfo(String errorMessage, String requestURI) {
        this.errorMessage = errorMessage;
        this.requestUri = requestURI;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + requestUri + ", message = " + errorMessage + "}";
    }
}
