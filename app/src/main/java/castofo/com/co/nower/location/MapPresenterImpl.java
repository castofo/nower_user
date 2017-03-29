package castofo.com.co.nower.location;

import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import castofo.com.co.nower.models.Branch;

import static castofo.com.co.nower.network.ConnectivityInterceptor.isInternetConnectionError;

/**
 * Created by Alejandro on 19/09/2016.
 */
public class MapPresenterImpl implements MapPresenter,
    MapInteractor.OnLocationPermissionCheckedListener, MapInteractor.OnLocationChangedListener,
    MapInteractor.OnBranchesReceivedListener {

  private MapView mMapView;
  private MapInteractor mMapInteractor;
  private Map<Marker, Branch> markerToBranchList;

  public MapPresenterImpl(MapView mapView) {
    this.mMapView = mapView;
    markerToBranchList = new HashMap<>();
    this.mMapInteractor = new MapInteractorImpl(mMapView.getActivity());
  }

  @Override
  public void locateUser() {
    mMapInteractor.checkLocationPermission(this);
  }

  @Override
  public void requestLocationPermission() {
    mMapInteractor.requestLocationPermission();
  }

  @Override
  public void transformBranchContainer(int state) {
    if (mMapView != null) {
      switch (state) {
        case BottomSheetBehavior.STATE_COLLAPSED:
          mMapView.setBranchContainerClosingVisibility(View.GONE);
          break;
        case BottomSheetBehavior.STATE_EXPANDED:
          // TODO Populate branch with its info.
          mMapView.setBranchContainerClosingVisibility(View.VISIBLE);
          break;
      }
    }
  }

  @Override
  public void manageMarker(Marker marker) {
    if (mMapView != null) {
      mMapView.animateCamera(marker.getPosition());
      if (markerToBranchList.containsKey(marker)) {
        switch (mMapView.getBranchContainerState()) {
          case BottomSheetBehavior.STATE_HIDDEN:
            mMapView.setBranchContainerState(BottomSheetBehavior.STATE_COLLAPSED);
            mMapView.setCurrentMarker(marker);
            break;
          case BottomSheetBehavior.STATE_COLLAPSED:
            if (marker.equals(mMapView.getCurrentMarker())) {
              mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
              mMapView.setCurrentMarker(null);
            }
            else {
              mMapView.setBranchContainerState(BottomSheetBehavior.STATE_COLLAPSED);
              mMapView.setCurrentMarker(marker);
            }
            break;
        }
      }
      else {
        // It happens when the user clicked his marker on the map.
        mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
        mMapView.setCurrentMarker(null);
      }
    }
  }

  @Override
  public void manageMapClick() {
    if (mMapView != null) {
      // The BranchContainer is hidden when the user clicks on the map and the current marker
      // is set to null.
      if (mMapView.getBranchContainerState() == BottomSheetBehavior.STATE_COLLAPSED) {
        mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
      }
      mMapView.setCurrentMarker(null);
    }
  }

  /**
   * Collapses or expands the Branch container according to the interaction with the header or the
   * closing icon.
   */
  @Override
  public void manageBranchHeaderInteraction() {
    if (mMapView != null) {
      switch (mMapView.getBranchContainerState()) {
        case BottomSheetBehavior.STATE_COLLAPSED:
          mMapView.setBranchContainerState(BottomSheetBehavior.STATE_EXPANDED);
          break;
        case BottomSheetBehavior.STATE_EXPANDED:
            mMapView.setBranchContainerState(BottomSheetBehavior.STATE_COLLAPSED);
          break;
      }
    }
  }

  @Override
  public void onDestroy() {
    mMapView = null;
  }

  @Override
  public void onLocationPermissionExplanationNeeded() {
    if (mMapView != null) {
      mMapView.showLocationPermissionExplanation();
    }
  }

  @Override
  public void onRequestLocationPermissionNeeded() {
    requestLocationPermission();
  }

  @Override
  public void onLocationPermissionGranted() {
    if (mMapView != null) {
      mMapView.showProgress();
      mMapInteractor.getLocation(this);
    }
  }

  @Override
  public void onGettingLocationError() {
    if (mMapView != null) {
      mMapView.hideProgress();
      mMapView.showGettingLocationError();
    }
  }

  @Override
  public void onGettingLocationSuccess(Location userLocation) {
    if (mMapView != null) {
      double userLatitude = userLocation.getLatitude();
      double userLongitude = userLocation.getLongitude();
      LatLng userPosition = new LatLng(userLatitude, userLongitude);
      mMapView.clearMap();
      mMapView.moveCamera(userPosition);
      mMapView.addMarkerForUser(userPosition);
      mMapView.showRange(userPosition);
      // After the map is centered on the user´s location, the nearby promos have to be found.
      mMapInteractor.getNearbyBranches(userLatitude, userLongitude, this);
    }
  }

  @Override
  public void onGettingNearbyBranchesError(Throwable throwable) {
    if (mMapView != null) {
      mMapView.hideProgress();
      if (isInternetConnectionError(throwable)) {
        mMapView.showNoInternetError();
      }
      else {
        mMapView.showGettingNearbyBranchesError();
      }
    }
  }

  /**
   * Shows every nearby branch with available promos as a marker on the map.
   *
   * @param nearbyBranchList A list containing all the nearby branches to the user's current
   *                         position.
   */
  @Override
  public void onGettingNearbyBranchesSuccess(List<Branch> nearbyBranchList) {
    if (mMapView != null) {
      if (nearbyBranchList.isEmpty()) {
        mMapView.showNoNearbyPromosMessage();
      }
      else {
        for (Branch branch : nearbyBranchList) {
          LatLng branchPosition = new LatLng(branch.getLatitude(), branch.getLongitude());
          markerToBranchList.put(mMapView.addMarkerForBranch(branchPosition), branch);
        }
      }
      mMapView.finishProgress();
    }
  }
}
