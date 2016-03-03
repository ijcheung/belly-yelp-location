package com.icheung.yelplocation.model;

import java.util.List;

public class BusinessWrapper {
    List<Business> businesses;

    public BusinessWrapper(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }
}
