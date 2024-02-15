package com.motuma.paymentserviceesb.payment.dto.coop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class CoopInternalTransactionResult {
    private ESBHeader ESBHeader;
    private Status Status;
    private FUNDSTRANSFERType FUNDSTRANSFERType;
    private ESBStatus ESBStatus;

    public ESBHeader getESBHeader() {
        return ESBHeader;
    }

    public void setESBHeader(ESBHeader ESBHeader) {
        this.ESBHeader = ESBHeader;
    }

    public Status getStatus() {
        return Status;
    }

    public void setStatus(Status Status) {
        this.Status = Status;
    }

    public FUNDSTRANSFERType getFUNDSTRANSFERType() {
        return FUNDSTRANSFERType;
    }

    public void setFUNDSTRANSFERType(FUNDSTRANSFERType FUNDSTRANSFERType) {
        this.FUNDSTRANSFERType = FUNDSTRANSFERType;
    }

    public ESBStatus getESBStatus() {
        return ESBStatus;
    }

    public void setESBStatus(ESBStatus ESBStatus) {
        this.ESBStatus = ESBStatus;
    }

    public static class ESBHeader {
        private String serviceCode;
        private String channel;
        private String Service_name;
        private String Message_Id;

        public String getServiceCode() {
            return serviceCode;
        }

        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getService_name() {
            return Service_name;
        }

        public void setService_name(String service_name) {
            Service_name = service_name;
        }

        public String getMessage_Id() {
            return Message_Id;
        }

        public void setMessage_Id(String message_Id) {
            Message_Id = message_Id;
        }
    }
    public static class Status {
        private String transactionId;
        private String messageId;
        private String successIndicator;
        private String application;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getSuccessIndicator() {
            return successIndicator;
        }

        public void setSuccessIndicator(String successIndicator) {
            this.successIndicator = successIndicator;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }
    }

    public static class FUNDSTRANSFERType {
        private String id;
        private String TRANSACTIONTYPE;
        private String DEBITACCTNO;
        private String CURRENCYMKTDR;
        private String DEBITCURRENCY;
        private Double DEBITAMOUNT;
        private String DEBITVALUEDATE;
        private String DEBITTHEIRREF;
        private String CREDITTHEIRREF;
        private String CREDITACCTNO;
        private String CURRENCYMKTCR;
        private String CREDITCURRENCY;
        private String CREDITVALUEDATE;
        private String PROCESSINGDATE;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTRANSACTIONTYPE() {
            return TRANSACTIONTYPE;
        }

        public void setTRANSACTIONTYPE(String TRANSACTIONTYPE) {
            this.TRANSACTIONTYPE = TRANSACTIONTYPE;
        }

        public String getDEBITACCTNO() {
            return DEBITACCTNO;
        }

        public void setDEBITACCTNO(String DEBITACCTNO) {
            this.DEBITACCTNO = DEBITACCTNO;
        }

        public String getCURRENCYMKTDR() {
            return CURRENCYMKTDR;
        }

        public void setCURRENCYMKTDR(String CURRENCYMKTDR) {
            this.CURRENCYMKTDR = CURRENCYMKTDR;
        }

        public String getDEBITCURRENCY() {
            return DEBITCURRENCY;
        }

        public void setDEBITCURRENCY(String DEBITCURRENCY) {
            this.DEBITCURRENCY = DEBITCURRENCY;
        }

        public Double getDEBITAMOUNT() {
            return DEBITAMOUNT;
        }

        public void setDEBITAMOUNT(Double DEBITAMOUNT) {
            this.DEBITAMOUNT = DEBITAMOUNT;
        }

        public String getDEBITVALUEDATE() {
            return DEBITVALUEDATE;
        }

        public void setDEBITVALUEDATE(String DEBITVALUEDATE) {
            this.DEBITVALUEDATE = DEBITVALUEDATE;
        }

        public String getDEBITTHEIRREF() {
            return DEBITTHEIRREF;
        }

        public void setDEBITTHEIRREF(String DEBITTHEIRREF) {
            this.DEBITTHEIRREF = DEBITTHEIRREF;
        }

        public String getCREDITTHEIRREF() {
            return CREDITTHEIRREF;
        }

        public void setCREDITTHEIRREF(String CREDITTHEIRREF) {
            this.CREDITTHEIRREF = CREDITTHEIRREF;
        }

        public String getCREDITACCTNO() {
            return CREDITACCTNO;
        }

        public void setCREDITACCTNO(String CREDITACCTNO) {
            this.CREDITACCTNO = CREDITACCTNO;
        }

        public String getCURRENCYMKTCR() {
            return CURRENCYMKTCR;
        }

        public void setCURRENCYMKTCR(String CURRENCYMKTCR) {
            this.CURRENCYMKTCR = CURRENCYMKTCR;
        }

        public String getCREDITCURRENCY() {
            return CREDITCURRENCY;
        }

        public void setCREDITCURRENCY(String CREDITCURRENCY) {
            this.CREDITCURRENCY = CREDITCURRENCY;
        }

        public String getCREDITVALUEDATE() {
            return CREDITVALUEDATE;
        }

        public void setCREDITVALUEDATE(String CREDITVALUEDATE) {
            this.CREDITVALUEDATE = CREDITVALUEDATE;
        }

        public String getPROCESSINGDATE() {
            return PROCESSINGDATE;
        }

        public void setPROCESSINGDATE(String PROCESSINGDATE) {
            this.PROCESSINGDATE = PROCESSINGDATE;
        }
    }
    public static class ESBStatus {
        private String Status;
        private String responseCode;

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        public List<String> getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(List<String> errorDescription) {
            this.errorDescription = errorDescription;
        }

        private String errorType;
        private List<String> errorDescription;

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }
    }

}
