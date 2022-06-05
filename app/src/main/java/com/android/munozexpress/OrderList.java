package com.android.munozexpress;

public class OrderList {

    String ServiceType, TransactionId , CustomerName,CustomerNumber;
    Integer Cancelled , Completed  ,Ongoing ;
    Double Duration, Distance;

    public String getServiceType() {
        return ServiceType;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public String getCustomerNumber() {
        return CustomerNumber;
    }

    public Integer getCancelled() {
        return Cancelled;
    }

    public Integer getCompleted() {
        return Completed;
    }

    public Integer getOngoing() {
        return Ongoing;
    }

    public Double getDuration() { return Duration; }

    public Double getDistance() { return Distance; }
}
