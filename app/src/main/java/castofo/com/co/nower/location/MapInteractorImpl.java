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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

import castofo.com.co.nower.BuildConfig;
import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;
import castofo.com.co.nower.network.ConnectivityInterceptor;
import castofo.com.co.nower.persistence.BranchPersistenceManager;
import castofo.com.co.nower.persistence.ContactInformationPersistenceManager;
import castofo.com.co.nower.persistence.PromoPersistenceManager;
import castofo.com.co.nower.persistence.StorePersistenceManager;
import castofo.com.co.nower.services.MapService;
import castofo.com.co.nower.services.ServiceFactory;
import castofo.com.co.nower.utils.RequestCodeHelper;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static castofo.com.co.nower.utils.RequestCodeHelper.PERMISSION_ACCESS_FINE_LOCATION_CODE;

/**
 * Created by Alejandro on 01/10/2016.
 */
public class MapInteractorImpl implements MapInteractor, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>,
    LocationListener {

  private static final String TAG = MapInteractorImpl.class.getSimpleName();
  private static final int INTERVAL = 10 * 1000;
  private static final int FASTEST_INTERVAL = 1 * 1000;
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Activity mActivity;
  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private LocationSettingsRequest mLocationSettingsRequest;
  private PendingResult<LocationSettingsResult> mResult;
  private MapInteractor.OnLocationChangedListener mLocationChangedListener;
  private MapService mMapService;

  public MapInteractorImpl(Activity activity) {
    // The Activity is required for the usage of the GoogleApiClient.
    this.mActivity = activity;
    mMapService = new ServiceFactory.Builder()
        .withBaseUrl(BuildConfig.DEBUG_API_BASE_URL)
        .addInterceptor(new ConnectivityInterceptor(mActivity))
        .buildService(MapService.class);
  }

  @Override
  public void checkLocationPermission(OnLocationPermissionCheckedListener listener) {
    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat
          .shouldShowRequestPermissionRationale(mActivity,
              Manifest.permission.ACCESS_FINE_LOCATION)) {
        // An explanation for this permission has to be shown to the user.
        listener.onLocationPermissionExplanationNeeded();
      } else {
        // No explanation is needed and the permission can be requested.
        listener.onRequestLocationPermissionNeeded();
      }
    } else {
      listener.onLocationPermissionGranted();
    }
  }

  @Override
  public void requestLocationPermission() {
    ActivityCompat
        .requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            PERMISSION_ACCESS_FINE_LOCATION_CODE);
  }

  @Override
  public void getLocation(OnLocationChangedListener listener) {
    Log.i(TAG, "Entered getLocation().");
    mLocationChangedListener = listener;
    createGoogleApiClientInstance();
    createLocationRequest();
    createLocationSettingsRequest();
    checkLocationSettings();
    connectGoogleApiClient();
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

  protected synchronized void createLocationRequest() {
    if (mLocationRequest == null) {
      mLocationRequest = LocationRequest.create()
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setInterval(INTERVAL)
          .setFastestInterval(FASTEST_INTERVAL);
    }
  }

  protected synchronized void createLocationSettingsRequest() {
    if (mLocationSettingsRequest == null) {
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
          .addLocationRequest(mLocationRequest);
      mLocationSettingsRequest = builder.build();
    }
  }

  protected synchronized void checkLocationSettings() {
    mResult = LocationServices.SettingsApi
        .checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);
  }

  public void connectGoogleApiClient() {
    Log.i(TAG, "mGoogleApiClient.connect() triggered.");
    mGoogleApiClient.connect();
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
      } catch (IntentSender.SendIntentException sie) {
        Log.i(TAG, "Location services connection failed.");
        sie.printStackTrace();
        disconnectGoogleApiClient();
        notifyLocationUnavailability();
      }
    } else {
      // The error has no resolution.
      Log.i(TAG, "Location services connection failed with code " + connectionResult
          .getErrorCode() + ".");
      disconnectGoogleApiClient();
      notifyLocationUnavailability();
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.i(TAG, "mGoogleApiClient connected.");
    mResult.setResultCallback(this);
  }

  @Override
  public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
    final Status status = locationSettingsResult.getStatus();
    switch (status.getStatusCode()) {
      case LocationSettingsStatusCodes.SUCCESS:
        // All location settings are satisfied. The client can initialize location requests here.
        Log.i(TAG, "Location settings are satisfied.");
        requestLocation();
        break;
      case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
        // Location settings are not satisfied. But could be fixed by showing a dialog to the user.
        Log.i(TAG, "Location settings are not satisfied.");
        disconnectGoogleApiClient();
        try {
          // Shows the dialog by calling startResolutionForResult() and checks the result in
          // onActivityResult().
          status.startResolutionForResult(mActivity, RequestCodeHelper.ENABLE_GPS_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
          notifyLocationUnavailability();
        }
        break;
      case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
        // Location settings are not satisfied. However, there is no way to fix the problem and the
        // dialog is not shown.
        Log.i(TAG, "Location settings are not satisfied and the problem can't be fixed.");
        disconnectGoogleApiClient();
        notifyLocationUnavailability();
        break;
    }
  }

  public void requestLocation() {
    // It's mandatory to check the location permission again in order to be able to use the
    // FusedLocationApi.
    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

      if (location != null) {
        disconnectGoogleApiClient();
        notifyLocationAvailability(location);
      } else {
        Log.i(TAG, "Location is null.");
        if (isGpsEnabled()) {
          LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
              mLocationRequest, this);
        } else {
          Log.i(TAG, "The GPS has to be enabled.");
          disconnectGoogleApiClient();
          notifyLocationUnavailability();
        }
      }
    } else {
      disconnectGoogleApiClient();
      notifyLocationUnavailability();
    }
  }

  private boolean isGpsEnabled() {
    LocationManager mLocationManager = (LocationManager) mActivity
        .getSystemService(Context.LOCATION_SERVICE);
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

  public void notifyLocationUnavailability() {
    mLocationChangedListener.onGettingLocationError();
  }

  public void notifyLocationAvailability(Location location) {
    Log.i(TAG, "Latitude: " + String.valueOf(location.getLatitude()) + ".");
    Log.i(TAG, "Longitude: " + String.valueOf(location.getLongitude()) + ".");
    mLocationChangedListener.onGettingLocationSuccess(location);
  }

  @Override
  public void getNearbyBranches(double latitude, double longitude,
                                final OnBranchesReceivedListener listener) {
    // The "store" and "contact_informations" parameters are used to include the corresponding
    // Store and ContactInformation in each nearby Branch.
    mMapService.getNearbyBranches(latitude, longitude, "store,contact_informations")
        .subscribeOn(Schedulers.newThread()) // TODO improve with the better way.
        .observeOn(AndroidSchedulers.mainThread()) // TODO improve with the better way.
        .subscribe(nearbyBranchList -> {
          // Deletes all previously stored Branches, Stores, ContactInformations and Promos.
          BranchPersistenceManager.deleteAllBranches();
          StorePersistenceManager.deleteAllStores();
          ContactInformationPersistenceManager.deleteAllContactInformations();
          PromoPersistenceManager.deleteAllPromos();
          // The downloaded Branches, their corresponding Stores and ContactInformations are stored
          // locally.
          BranchPersistenceManager.createBranchesInList(nearbyBranchList);
          listener.onGettingNearbyBranchesSuccess(nearbyBranchList);
        }, throwable -> {
          listener.onGettingNearbyBranchesError(throwable);
        });
  }

  @Override
  public void loadBranch(String branchId, OnBranchLoadedListener listener) {
    Single<Branch> cachedBranch = BranchPersistenceManager.retrieveBranch(branchId)
        .subscribeOn(Schedulers.io());

    Single<Branch> remoteBranch = getBranch(branchId).subscribeOn(Schedulers.newThread());

    // Tries to get the Branch from the local db first and, if it was not possible to retrieve it,
    // makes a request to the remote db in order to get the specified Branch.
    Single.concat(cachedBranch, remoteBranch)
        .observeOn(AndroidSchedulers.mainThread())
        // It is satisfied with the first result in which the Branch id is not null.
        .filter(branch -> branch.getId() != null)
        .firstElement()
        .subscribe(branch -> listener.onLoadingBranchSuccess(branch),
            throwable -> listener.onLoadingBranchError(throwable));
  }

  /**
   * Retrieves the Branch from the remote db for the given id.
   *
   * @param branchId The id of the Branch to be retrieved.
   * @return A {@link Single<Branch>} that will emit the result.
   */
  private Single<Branch> getBranch(String branchId) {
    // The "store" and "contact_informations" parameters are used to include the corresponding
    // Store and ContactInformation in the Branch.
    return mMapService.getBranch(branchId, "store,contact_informations")
        // The downloaded Branch and its corresponding Store and ContactInformation are stored
        // locally.
        .doOnSuccess(branch -> BranchPersistenceManager.createBranch(branch));
  }

  @Override
  public void loadBranchPromos(String branchId, OnBranchPromosLoadedListener listener) {
    // Tries to request the Branch Promos from the remote db first and, if it was not possible,
    // retrieves the data from the local db in order to get the Promos of the specified Branch.
    mMapService.getBranchPromos(branchId)
        .subscribeOn(Schedulers.newThread()) // TODO improve with the better way.
        .observeOn(AndroidSchedulers.mainThread()) // TODO improve with the better way.
        .subscribe(branchPromoList -> {
          // Deletes all previously stored Branch Promos.
          PromoPersistenceManager.deleteBranchPromos(branchId);
          // The downloaded Branch Promos are stored locally.
          PromoPersistenceManager.createPromosInList(branchPromoList);
          // The loaded Branch Promos are updated locally.
          PromoPersistenceManager.updateBranchPromos(branchId, branchPromoList);
          listener.onLoadingBranchPromosSuccess(branchId, branchPromoList);
        }, throwable -> {
          List<Promo> cachedBranchPromos = PromoPersistenceManager.retrieveBranchPromos(branchId);
          if (cachedBranchPromos != null) {
            listener.onLoadingBranchPromosSuccess(branchId, cachedBranchPromos);
          }
          else {
            // The Branch Promos couldn't be retrieved neither from the Internet nor from the local
            // db.
            listener.onLoadingBranchPromosError(new Throwable());
          }
        });
  }
}
