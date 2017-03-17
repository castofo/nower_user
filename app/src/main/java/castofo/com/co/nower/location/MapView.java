package castofo.com.co.nower.location;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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

  Marker getCurrentMarker();

  void setCurrentMarker(Marker marker);

  int getBranchContainerState();

  void setBranchContainerState(int newState);

  boolean hideBranchContainer();

  boolean collapseBranchContainer();

  boolean expandBranchContainer();

  void hideBranchContainerClosing();

  void showBranchContainerClosing();

  void showLocationPermissionExplanation();

  void showGettingLocationError();

  void showNoInternetError();

  void showGettingNearbyBranchesError();

  void showNoNearbyPromosMessage();
}
