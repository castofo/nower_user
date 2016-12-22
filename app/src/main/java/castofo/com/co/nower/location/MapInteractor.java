package castofo.com.co.nower.location;

import android.location.Location;

import java.util.List;

import castofo.com.co.nower.models.Branch;

/**
 * Created by Alejandro on 01/10/2016.
 */
public interface MapInteractor {

  interface OnLocationPermissionCheckedListener {

    void onLocationPermissionExplanationNeeded();

    void onRequestLocationPermissionNeeded();

    void onLocationPermissionGranted();
  }

  interface OnLocationChangedListener {

    void onGettingLocationError();

    void onGettingLocationSuccess(Location userPosition);
  }

  interface OnBranchesReceivedListener {

    void onNoInternetError();

    void onGettingNearbyBranchesError();

    void onGettingNearbyBranchesSuccess(List<Branch> nearbyBranchList);
  }

  void checkLocationPermission(OnLocationPermissionCheckedListener listener);

  void requestLocationPermission();

  void getLocation(OnLocationChangedListener listener);

  void getNearbyBranches(double latitude, double longitude, OnBranchesReceivedListener listener);
}
