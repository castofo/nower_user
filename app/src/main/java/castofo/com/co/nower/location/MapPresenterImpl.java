package castofo.com.co.nower.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import castofo.com.co.nower.models.Branch;

/**
 * Created by Alejandro on 19/09/2016.
 */
public class MapPresenterImpl implements MapPresenter, MapInteractor.OnLocationChangedListener,
  MapInteractor.OnBranchesReceivedListener {

  private MapView mMapView;
  private MapInteractor mMapInteractor;

  public MapPresenterImpl(MapView mapView) {
    this.mMapView = mapView;
    this.mMapInteractor = new MapInteractorImpl(mMapView.getActivity());
  }

  @Override
  public void locateUser() {
    mMapInteractor.checkGpsAvailability(this);
  }

  @Override
  public void onDestroy() {
    mMapView = null;
  }

  @Override
  public void onGpsDisabledError() {
    if (mMapView != null) {
      mMapView.showGpsDialog();
    }
  }

  @Override
  public void onGpsAvailable() {
    if (mMapView != null) {
      mMapView.showProgress();
      mMapInteractor.getLocation(this);
    }
  }

  @Override
  public void onPermissionExplanationNeeded() {
    if (mMapView != null) {
      mMapView.showLocationPermissionExplanation();
    }
  }

  @Override
  public void requestLocationPermission() {
    mMapInteractor.requestLocationPermission();
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
  public void onNoInternetError() {
    if (mMapView != null) {
      mMapView.hideProgress();
      mMapView.showNoInternetError();
    }
  }

  @Override
  public void onGettingNearbyBranchesError() {
    if (mMapView != null) {
      mMapView.hideProgress();
      mMapView.showGettingNearbyBranchesError();
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
          mMapView.addMarkerForBranch(branchPosition);
        }
      }
      mMapView.finishProgress();
    }
  }
}
