package com.example.domain;

public final class Status {

    private Status() {} // utility class

    public static final String SIM_AVAILABLE      = "AVAILABLE";
    public static final String SIM_BORROWED       = "BORROWED";
    public static final String SIM_MAINTENANCE    = "MAINTENANCE";
    public static final String SIM_OUT_OF_SERVICE = "OUT_OF_SERVICE";

    public static final String BORROW_ACTIVE           = "ACTIVE";
    public static final String BORROW_PARTIAL_RETURN   = "PARTIALLY_RETURNED";
    public static final String BORROW_CLOSED           = "CLOSED";
    public static final String BORROW_CANCELLED        = "CANCELLED";
}
