package com.icheung.yelplocation.fragment.data;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.icheung.yelplocation.model.Business;

import java.util.ArrayList;

public class BusinessDataFragment extends Fragment {
    private ArrayList<Business> data;

    public BusinessDataFragment(){
        data = new ArrayList<Business>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(ArrayList<Business> data) {
        this.data = data;
    }

    public ArrayList<Business> getData() {
        return data;
    }
}
