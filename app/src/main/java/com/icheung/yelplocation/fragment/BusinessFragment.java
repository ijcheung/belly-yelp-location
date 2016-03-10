package com.icheung.yelplocation.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.icheung.yelplocation.R;
import com.icheung.yelplocation.activity.MapsActivity;
import com.icheung.yelplocation.api.YelpApi;
import com.icheung.yelplocation.api.YelpServiceGenerator;
import com.icheung.yelplocation.fragment.data.BusinessDataFragment;
import com.icheung.yelplocation.helper.BusinessAdapter;
import com.icheung.yelplocation.model.Business;
import com.icheung.yelplocation.model.BusinessWrapper;
import com.icheung.yelplocation.model.Coordinate;
import com.icheung.yelplocation.util.Utilities;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessFragment extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BusinessAdapter.OnBusinessClickedListener {
    private static final String TAG = BusinessFragment.class.getSimpleName();
    private static final String TAG_BUSINESS_DATA_FRAGMENT = "business_data_fragment";

    private static final String KEY_BUSINESSES = "businesses";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private YelpApi mApi;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    private RecyclerView mBusinessRecyclerView;
    private BusinessAdapter mAdapter;
    private ArrayList<Business> mBusinesses;

    public BusinessFragment() { }
    public static BusinessFragment newInstance() {
        BusinessFragment fragment = new BusinessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mApi = YelpServiceGenerator.createService();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_business, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isInternetAvailable()){
                    Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_no_internet, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            mOnRefreshListener.onRefresh();
                        }
                    });
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                else if(mGoogleApiClient.isConnected()) {
                    requestLocation();
                }
                else {
                    Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_location_services_not_connected, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            mOnRefreshListener.onRefresh();
                        }
                    });
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mBusinessRecyclerView = (RecyclerView) root.findViewById(R.id.businessList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mBusinessRecyclerView.getContext());

        mBusinessRecyclerView.setLayoutManager(layoutManager);
        mBusinessRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(getResources().getColor(R.color.divider)).size(1).build());

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        // Find Retained Data Fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();
        BusinessDataFragment businessDataFragment = (BusinessDataFragment) fm.findFragmentByTag(TAG_BUSINESS_DATA_FRAGMENT);

        if(businessDataFragment == null) {
            businessDataFragment = new BusinessDataFragment();
            fm.beginTransaction().add(businessDataFragment, TAG_BUSINESS_DATA_FRAGMENT).commit();
        }

        mBusinesses = businessDataFragment.getData();
        mAdapter = new BusinessAdapter(this, mBusinesses);
        mBusinessRecyclerView.setAdapter(mAdapter);

        // If No Internet, Load Cache
        if(mBusinesses.size() == 0 && !isInternetAvailable()) {
            ArrayList<Business> cache = Paper.book().read(KEY_BUSINESSES, new ArrayList<Business>());
            mBusinesses.addAll(cache);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_business, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                Intent i = new Intent(getContext(), MapsActivity.class);
                i.putParcelableArrayListExtra(MapsActivity.KEY_DATA, mBusinesses);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mLastLocation = location;
        onLocationUpdate(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mBusinesses.size() == 0) {
            requestLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onBusinessClicked(int position) {
        mListener.onBusinessSelected(mBusinesses.get(position));
    }

    public interface OnFragmentInteractionListener {
        void onBusinessSelected(Business business);
    }

    private void requestLocation() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(i);
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        else if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void onLocationUpdate(Location location){
        Log.i("Latitude", Double.toString(location.getLatitude()));
        Log.i("Longitude", Double.toString(location.getLongitude()));
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addresses.size() > 0 && addresses.get(0).getLocality() != null) {
                String locality = addresses.get(0).getLocality();
                if(addresses.get(0).getAdminArea() != null) {
                    locality += ", " + addresses.get(0).getAdminArea();
                }
                Log.i("Locality", locality);
                Call<BusinessWrapper> call = mApi.search(
                        locality,
                        new Coordinate(location.getLatitude(), location.getLongitude()
                ));
                call.enqueue(new Callback<BusinessWrapper>() {
                    @Override
                    public void onResponse(Call<BusinessWrapper> call, Response<BusinessWrapper> response) {
                        BusinessWrapper wrapper = response.body();
                        if(wrapper != null) {
                            List<Business> businesses = wrapper.getBusinesses();

                            Coordinate current = new Coordinate(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                            for(Business business : businesses){
                                if(business.getLocation().getCoordinate() != null) {
                                    business.setDistance(Utilities.calculateHaversineDistance(current, business.getLocation().getCoordinate()));
                                }
                            }
                            Collections.sort(businesses, new Comparator<Business>() {
                                @Override
                                public int compare(Business a, Business b) {
                                    if(a.getDistance() >= b.getDistance()) {
                                        return 1;
                                    }
                                    else {
                                        return -1;
                                    }
                                }
                            });
                            Paper.book().write(KEY_BUSINESSES, businesses);

                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.refresh(businesses);
                        }
                        else {
                            Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_yelp_service, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mSwipeRefreshLayout.setRefreshing(true);
                                    mOnRefreshListener.onRefresh();
                                }
                            });
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<BusinessWrapper> call, Throwable t) {
                        Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_yelp_service, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSwipeRefreshLayout.setRefreshing(true);
                                mOnRefreshListener.onRefresh();
                            }
                        });
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            else {
                Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_geocode);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } catch (IOException e) {
            Utilities.showSnackbar(mSwipeRefreshLayout, R.string.error_geocode);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
