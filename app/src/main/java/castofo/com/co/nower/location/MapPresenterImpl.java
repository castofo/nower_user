package castofo.com.co.nower.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alejandro on 19/09/2016.
 */
public class MapPresenterImpl implements MapPresenter, MapInteractor.OnLocationChangedListener {

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
  public void onGettingLocationError() {
    if (mMapView != null) {
      mMapView.hideProgress();
      mMapView.showGettingLocationError();
    }
  }

  @Override
  public void onGettingLocationSuccess(Location userLocation) {
    if (mMapView != null) {
      LatLng userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
      mMapView.clearMap();
      mMapView.moveCamera(userPosition);
      mMapView.addMarkerForUser(userPosition);
      mMapView.showRange(userPosition);
      mMapView.finishProgress();
    }
  }
}
