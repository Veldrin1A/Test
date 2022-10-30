package com.phonecompany.billing;

import java.util.Date;

public class PhoneCall {
    private long number;
    private Date startDate;
    private Date endDate;

    public PhoneCall(long number, Date startDate, Date endDate) {
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getNumber() {
        return number;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}