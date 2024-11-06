package com.ptit.datn.constants;

// Constants for request status
public final class RequestStatus {
    public static final int PENDING = 0;    // when user create a request
    public static final int ACCEPTED = 1;   // when office owner accept the request
    public static final int REJECTED = 2;   // when office owner reject the request
    public static final int COMPLETED = 3;  // when the request is completed
    public static final int CANCELED = 4;   // when the request is canceled

}
