package castofo.com.co.nower.location;

import android.location.Location;

import java.util.List;

import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;

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

    void onGettingNearbyBranchesError(Throwable throwable);

    void onGettingNearbyBranchesSuccess(List<Branch> nearbyBranchList);
  }

  interface OnBranchLoadedListener {

    void onLoadingBranchError(Throwable throwable);

    void onLoadingBranchSuccess(Branch loadedBranch);
  }

  interface OnBranchPromosLoadedListener {

    void onLoadingBranchPromosError(Throwable throwable);

    void onLoadingBranchPromosSuccess(String branchId, List<Promo> branchPromoList);
  }

  void checkLocationPermission(OnLocationPermissionCheckedListener listener);

  void requestLocationPermission();

  void getLocation(OnLocationChangedListener listener);

  void getNearbyBranches(double latitude, double longitude, OnBranchesReceivedListener listener);

  void loadBranch(String branchId, OnBranchLoadedListener listener);

  void loadBranchPromos(String branchId, OnBranchPromosLoadedListener listener);
}
