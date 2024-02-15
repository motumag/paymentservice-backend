package com.motuma.paymentserviceesb.payment.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EbirrTransactionResult {
    @JsonProperty("schemaVersion")
    private String schemaVersion;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("responseId")
    private String responseId;

    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("responseMsg")
    private String responseMsg;

    @JsonProperty("params")
    private Params params;

    // Getters and setters
    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    // Inner class representing the 'params' object
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Params {
        @JsonProperty("accountNo")
        private String accountNo;

        @JsonProperty("accountType")
        private String accountType;

        @JsonProperty("state")
        private String state;

        @JsonProperty("referenceId")
        private String referenceId;

        @JsonProperty("transactionId")
        private String transactionId;

        @JsonProperty("issuerTransactionId")
        private String issuerTransactionId;

        @JsonProperty("txAmount")
        private String txAmount;

        @JsonProperty("orderId")
        private String orderId;

        @JsonProperty("description")
        private String description;

        // Getters and setters
        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getIssuerTransactionId() {
            return issuerTransactionId;
        }

        public void setIssuerTransactionId(String issuerTransactionId) {
            this.issuerTransactionId = issuerTransactionId;
        }

        public String getTxAmount() {
            return txAmount;
        }

        public void setTxAmount(String txAmount) {
            this.txAmount = txAmount;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

