package castofo.com.co.nower.location;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alejandro on 19/09/2016.
 */
public interface MapView {

  Activity getActivity();

  void showProgress();

  void moveCamera(LatLng position);

  void addMarkerForUser(LatLng userPosition);

  void showRange(LatLng userPosition);

  void showGpsDialog();

  void showGettingLocationError();
}
