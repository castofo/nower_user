package castofo.com.co.nower.location;

import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import castofo.com.co.nower.models.Branch;

import static castofo.com.co.nower.network.ConnectivityInterceptor.isInternetConnectionError;

/**
 * Created by Alejandro on 19/09/2016.
 */
public class MapPresenterImpl implements MapPresenter,
    MapInteractor.OnLocationPermissionCheckedListener, MapInteractor.OnLocationChangedListener,
    MapInteractor.OnBranchesReceivedListener, MapInteractor.OnBranchLoadedListener {

  private static final String TAG = MapPresenterImpl.class.getSimpleName();

  private MapView mMapView;
  private MapInteractor mMapInteractor;
  private Map<Marker, String> mMarkerToBranchList;
  private List<Marker> mMarkerList;

  public MapPresenterImpl(MapView mapView) {
    this.mMapView = mapView;
    mMarkerToBranchList = new LinkedHashMap<>();
    mMarkerList = new ArrayList<>();
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
          mMapView.setNavigationControlsVisibility(View.VISIBLE);
          mMapView.collapseStoreAndBranchName();
          mMapView.setBranchContainerClosingVisible(false);
          String currentBranchId = mMarkerToBranchList.get(mMapView.getCurrentMarker());
          mMapInteractor.loadBranch(currentBranchId, this);
          break;
        case BottomSheetBehavior.STATE_EXPANDED:
          mMapView.setNavigationControlsVisibility(View.GONE);
          mMapView.expandStoreAndBranchName();
          mMapView.setBranchContainerClosingVisible(true);
          // TODO Populate branch with its full info and promos (use getCurrentMarker()).
          break;
      }
    }
  }

  @Override
  public void manageMarker(Marker marker) {
    if (mMapView != null) {
      mMapView.animateCamera(marker.getPosition());
      if (mMarkerToBranchList.containsKey(marker)) {
        switch (mMapView.getBranchContainerState()) {
          case BottomSheetBehavior.STATE_HIDDEN:
            mMapView.setCurrentMarker(marker);
            mMapView.setBranchContainerState(BottomSheetBehavior.STATE_COLLAPSED);
            break;
          case BottomSheetBehavior.STATE_COLLAPSED:
            if (marker.equals(mMapView.getCurrentMarker())) {
              mMapView.setCurrentMarker(null);
              mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
            }
            else {
              mMapView.setCurrentMarker(marker);
              mMapView.setBranchContainerState(BottomSheetBehavior.STATE_COLLAPSED);
              // A transition from STATE_COLLAPSED to STATE_COLLAPSED doesn't trigger the callback.
              // Thus, the action of the callback has to be manually triggered.
              transformBranchContainer(BottomSheetBehavior.STATE_COLLAPSED);
            }
            break;
        }
      }
      else {
        // It happens when the user clicked his marker on the map.
        mMapView.setCurrentMarker(null);
        mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
      }
    }
  }

  @Override
  public void manageMapClick() {
    if (mMapView != null) {
      // When the user clicks on the map the current marker is set to null and the BranchContainer
      // is hidden.
      mMapView.setCurrentMarker(null);
      if (mMapView.getBranchContainerState() == BottomSheetBehavior.STATE_COLLAPSED) {
        mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
      }
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
  public void navigateOverBranchList(int direction) {
    if (mMapView != null) {
      int markerPosition = mMarkerList.indexOf(mMapView.getCurrentMarker());
      if (markerPosition != -1) {
        // The current marker was found in the list.
        int numbOfMarkers = mMarkerList.size();
        // The branches on the Branch container are supposed to loop in a circular way (i.e. there
        // is no start or end branch).
        Marker prevOrNextMarker = mMarkerList
            .get((numbOfMarkers + markerPosition + direction) % numbOfMarkers);
        // Simulates a click on the marker.
        mMapView.onMarkerClick(prevOrNextMarker);
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
      // The map and the list have to be cleared so that they contain only the updated data.
      mMarkerToBranchList.clear();
      mMarkerList.clear();
      if (nearbyBranchList.isEmpty()) {
        mMapView.showNoNearbyPromosMessage();
      }
      else {
        for (Branch branch : nearbyBranchList) {
          LatLng branchPosition = new LatLng(branch.getLatitude(), branch.getLongitude());
          Marker markerForBranch = mMapView.addMarkerForBranch(branchPosition);
          mMarkerToBranchList.put(markerForBranch, branch.getId());
          mMarkerList.add(markerForBranch);
        }
      }
      mMapView.finishProgress();
    }
  }

  @Override
  public void onLoadingBranchError(Throwable throwable) {
    if (mMapView != null) {
      // The current marker is set to null and the Branch container is closed immediately.
      mMapView.setCurrentMarker(null);
      mMapView.setBranchContainerState(BottomSheetBehavior.STATE_HIDDEN);
      if (isInternetConnectionError(throwable)) {
        mMapView.showNoInternetError();
      }
      else {
        mMapView.showLoadingBranchError();
      }
    }
  }

  @Override
  public void onLoadingBranchSuccess(Branch loadedBranch) {
    if (mMapView != null) {
      Log.i(TAG, "Branch name: " + loadedBranch.getName());
      mMapView.populateBranchInfo(loadedBranch);
    }
  }
}
