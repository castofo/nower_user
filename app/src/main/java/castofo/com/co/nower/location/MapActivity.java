package castofo.com.co.nower.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import castofo.com.co.nower.R;
import castofo.com.co.nower.utils.DialogCreatorHelper;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapView,
    DialogCreatorHelper.DialogCreatorListener {

  private static final String TAG = MapActivity.class.getSimpleName();
  private final static int ENABLE_GPS_REQUEST_CODE = 1;
  private static final float ZOOM_LEVEL = 15f;
  private static final float RADIUS = 1000;
  private static final float STROKE_WIDTH = 1f;

  @BindView(R.id.map)
  View mapFragmentView;
  @BindView(R.id.fab_progress_circle)
  FABProgressCircle fabProgressCircle;

  private GoogleMap mMap;
  private MapPresenter mMapPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(castofo.com.co.nower.R.layout.activity_map);
    ButterKnife.bind(this);

    mMapPresenter = new MapPresenterImpl(this);

    // Obtains the SupportMapFragment and gets notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    Log.i(TAG, "Map ready.");

    mMap = googleMap;
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    // After the map is ready, the user has to be located automatically.
    mMapPresenter.locateUser();
  }

  @Override
  public Activity getActivity() {
    return MapActivity.this;
  }

  @Override
  public void showProgress() {
    //TODO Show progress while getting location (overlay button changing image).
    fabProgressCircle.show();
  }

  @Override
  public void hideProgress() {
    fabProgressCircle.hide();
  }

  @Override
  public void finishProgress() {
    fabProgressCircle.beginFinalAnimation();
  }

  @Override
  public void moveCamera(LatLng position) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
  }

  @Override
  public void addMarkerForUser(LatLng userPosition) {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(userPosition)
                 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    mMap.addMarker(markerOptions);
  }

  /**
   * Shows a circle around the user's position with radius equals to the given one.
   *
   * @param userPosition LatLng The current position of the user
   */
  @Override
  public void showRange(LatLng userPosition) {
    CircleOptions circleOptions = new CircleOptions();
    circleOptions.center(userPosition)
                 .radius(RADIUS)
                 .strokeWidth(STROKE_WIDTH)
                 .strokeColor(ContextCompat.getColor(this, R.color.dark_blue_transparent))
                 .fillColor(ContextCompat.getColor(this, R.color.blue_transparent));
    mMap.addCircle(circleOptions);
  }

  @Override
  public void showGpsDialog() {
    DialogFragment gpsDialog = DialogCreatorHelper
        .newInstance(R.string.message_enable_location_services,
                     R.string.message_go_to_location_settings, R.string.action_go_to_settings,
                     R.string.action_cancel, null);
    gpsDialog.show(getSupportFragmentManager(), TAG);
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog) {
    Log.i(TAG, "The user clicked the positive action.");
    Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    startActivityForResult(enableGpsIntent, ENABLE_GPS_REQUEST_CODE);
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog) {
    Log.i(TAG, "The user clicked the negative action.");
  }

  @Override
  public void showGettingLocationError() {
    Snackbar gettingLocationErrorSnackbar = Snackbar
        .make(mapFragmentView, getResources().getString(R.string.error_getting_location),
              Snackbar.LENGTH_LONG);
    gettingLocationErrorSnackbar.setAction(getResources().getString(R.string.action_retry),
                                           new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mMapPresenter.locateUser();
      }
    });
    gettingLocationErrorSnackbar.show();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ENABLE_GPS_REQUEST_CODE && resultCode == 0) {
      // The user enabled the GPS.
      mMapPresenter.locateUser();
    }
  }

  @Override
  protected void onDestroy() {
    mMapPresenter.onDestroy();
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }
}
