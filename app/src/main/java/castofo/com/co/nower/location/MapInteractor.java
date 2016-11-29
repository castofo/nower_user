package castofo.com.co.nower.location;

import android.location.Location;

import java.util.List;

import castofo.com.co.nower.models.Branch;

/**
 * Created by Alejandro on 01/10/2016.
 */
public interface MapInteractor {

  interface OnLocationChangedListener {

    void onGpsDisabledError();

    void onGpsAvailable();

    void onPermissionExplanationNeeded();

    void onGettingLocationError();

    void onGettingLocationSuccess(Location userPosition);
  }

  interface OnBranchesReceivedListener {

    void onNoInternetError();

    void onGettingNearbyBranchesError();

    void onGettingNearbyBranchesSuccess(List<Branch> nearbyBranchList);
  }

  void checkGpsAvailability(OnLocationChangedListener listener);

  void getLocation(OnLocationChangedListener listener);

  void requestLocationPermission();

  void getNearbyBranches(double latitude, double longitude, OnBranchesReceivedListener listener);
}
