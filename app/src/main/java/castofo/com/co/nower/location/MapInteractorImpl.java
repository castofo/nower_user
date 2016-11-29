package castofo.com.co.nower.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import castofo.com.co.nower.models.Branch;

import static castofo.com.co.nower.utils.RequestCodeHelper.PERMISSION_ACCESS_FINE_LOCATION_CODE;

/**
 * Created by Alejandro on 01/10/2016.
 */
public class MapInteractorImpl implements MapInteractor, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private static final String TAG = MapInteractorImpl.class.getSimpleName();
  private static final int INTERVAL = 10 * 1000;
  private static final int FASTEST_INTERVAL = 1 * 1000;
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Activity mActivity;
  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private MapInteractor.OnLocationChangedListener mLocationChangedListener;

  public MapInteractorImpl(Activity activity) {
    // The Activity is required for the use of the GoogleApiClient.
    this.mActivity = activity;
  }

  @Override
  public void checkGpsAvailability(OnLocationChangedListener listener) {
    if (isGpsEnabled()) {
      listener.onGpsAvailable();
    }
    else {
      listener.onGpsDisabledError();
    }
  }

  private boolean isGpsEnabled() {
    LocationManager mLocationManager = (LocationManager) mActivity
        .getSystemService(Context.LOCATION_SERVICE);
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  @Override
  public void getLocation(OnLocationChangedListener listener) {
    Log.i(TAG, "Entered getLocation().");
    mLocationChangedListener = listener;
    createGoogleApiClientInstance();
    connectGoogleApiClient();
    createLocationRequest();
  }

  protected synchronized void createGoogleApiClientInstance() {
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
    }
  }

  public void connectGoogleApiClient() {
    Log.i(TAG, "mGoogleApiClient.connect() triggered.");
    mGoogleApiClient.connect();
  }

  protected synchronized void createLocationRequest() {
    if (mLocationRequest == null) {
      mLocationRequest = LocationRequest.create()
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setInterval(INTERVAL)
          .setFastestInterval(FASTEST_INTERVAL);
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      disconnectGoogleApiClient();

      if (ActivityCompat
          .shouldShowRequestPermissionRationale(mActivity,
                                                Manifest.permission.ACCESS_FINE_LOCATION)) {
        // An explanation for this permission has to be shown to the user.
        mLocationChangedListener.onPermissionExplanationNeeded();
      }
      else {
        // No explanation is needed and the permission can be requested.
        requestLocationPermission();
      }
    }
    else {
      Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

      if (location != null) {
        disconnectGoogleApiClient();
        notifyLocationAvailability(location);
      }
      else {
        Log.i(TAG, "Location is null.");
        if (isGpsEnabled()) {
          LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                                   mLocationRequest, this);
        }
        else {
          Log.i(TAG, "The GPS has to be enabled.");
          disconnectGoogleApiClient();
          mLocationChangedListener.onGpsDisabledError();
        }
      }
    }
  }

  @Override
  public void requestLocationPermission() {
    ActivityCompat
        .requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_ACCESS_FINE_LOCATION_CODE);
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.i(TAG, "Location services suspended.");
    disconnectGoogleApiClient();
    notifyLocationUnavailability();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        // Starts an Activity that tries to resolve the error.
        connectionResult.startResolutionForResult(mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
      }
      catch (IntentSender.SendIntentException sie) {
        Log.i(TAG, "Location services connection failed.");
        sie.printStackTrace();
        disconnectGoogleApiClient();
        notifyLocationUnavailability();
      }
    }
    else {
      // The error has no resolution.
      Log.i(TAG, "Location services connection failed with code " + connectionResult
            .getErrorCode() + ".");
      disconnectGoogleApiClient();
      notifyLocationUnavailability();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    // Location update received.
    Log.i(TAG, "onLocationChanged() called.");
    disconnectGoogleApiClient();
    notifyLocationAvailability(location);
  }

  public void disconnectGoogleApiClient() {
    if (mGoogleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
      mGoogleApiClient.disconnect();
      Log.i(TAG, "mGoogleApiClient.removeLocationUpdates() triggered.");
      Log.i(TAG, "mGoogleApiClient.disconnect() triggered.");
    }
  }

  public void notifyLocationAvailability(Location location) {
    Log.i(TAG, "Latitude: " + String.valueOf(location.getLatitude()) + ".");
    Log.i(TAG, "Longitude: " + String.valueOf(location.getLongitude()) + ".");
    mLocationChangedListener.onGettingLocationSuccess(location);
  }

  public void notifyLocationUnavailability() {
    mLocationChangedListener.onGettingLocationError();
  }

  // TODO apply RxJava to this request.
  @Override
  public void getNearbyBranches(double latitude, double longitude,
                                OnBranchesReceivedListener listener) {
    Branch branch = new Branch();
    branch.setLatitude(6.1958694);
    branch.setLongitude(-75.6);
    List<Branch> nearbyBranchList = new ArrayList<>();
    // nearbyBranchList.add(branch);
    listener.onGettingNearbyBranchesSuccess(nearbyBranchList);
  }
}
