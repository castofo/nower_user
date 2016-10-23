package castofo.com.co.nower.location;

import android.location.Location;

/**
 * Created by Alejandro on 01/10/2016.
 */
public interface MapInteractor {

  interface OnLocationChangedListener {

    void onGpsDisabledError();

    void onGpsAvailable();

    void onGettingLocationError();

    void onGettingLocationSuccess(Location userPosition);
  }

  void checkGpsAvailability(OnLocationChangedListener listener);

  void getLocation(OnLocationChangedListener listener);
}
