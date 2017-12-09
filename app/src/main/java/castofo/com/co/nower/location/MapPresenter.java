package castofo.com.co.nower.location;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Alejandro on 19/09/2016.
 */
public interface MapPresenter {

  void locateUser();

  void requestLocationPermission();

  void transformBranchContainer(int state);

  void manageMarker(Marker marker);

  void manageMapClick();

  void manageBranchHeaderInteraction();

  void navigateOverBranchList(int direction);

  void onDestroy();
}
