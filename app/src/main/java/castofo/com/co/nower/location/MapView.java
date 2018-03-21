package castofo.com.co.nower.location;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import castofo.com.co.nower.models.Branch;

/**
 * Created by Alejandro on 19/09/2016.
 */
public interface MapView {

  Activity getActivity();

  void showProgress();

  void hideProgress();

  void finishProgress();

  void moveCamera(LatLng position);

  void animateCamera(LatLng position);

  void addMarkerForUser(LatLng userPosition);

  Marker addMarkerForBranch(LatLng branchPosition);

  void showRange(LatLng userPosition);

  void clearMap();

  boolean onMarkerClick(Marker marker);

  Marker getCurrentMarker();

  void setCurrentMarker(Marker marker);

  int getBranchContainerState();

  void setBranchContainerState(int newState);

  void setBranchContainerClosingVisible(boolean showClosing);

  void showLocationPermissionExplanation();

  void showGettingLocationError();

  void showNoInternetError();

  void showGettingNearbyBranchesError();

  void showNoNearbyPromosMessage();

  void setNavigationControlsVisibility(int visibility);

  void showLoadingBranchError();

  void populateBranchInfo(Branch branch);

  void expandStoreAndBranchName();

  void collapseStoreAndBranchName();

  void showLoadingBranchPromosError();

  void hideLoadingBranchPromosProgress();

  void finishActivity();
}
