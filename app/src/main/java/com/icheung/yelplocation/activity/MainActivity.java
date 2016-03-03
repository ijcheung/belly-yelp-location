package com.icheung.yelplocation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.icheung.yelplocation.R;
import com.icheung.yelplocation.fragment.BusinessFragment;
import com.icheung.yelplocation.model.Business;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements
        BusinessFragment.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
    }

    @Override
    public void onBusinessSelected(Business business) {

    }
}
