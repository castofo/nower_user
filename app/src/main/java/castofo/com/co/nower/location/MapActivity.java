package castofo.com.co.nower.location;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import castofo.com.co.nower.R;
import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.ContactInformation;
import castofo.com.co.nower.models.Store;
import castofo.com.co.nower.utils.DialogCreatorHelper;
import io.realm.RealmList;

import static castofo.com.co.nower.utils.RequestCodeHelper.ENABLE_GPS_REQUEST_CODE;
import static castofo.com.co.nower.utils.RequestCodeHelper.PERMISSION_ACCESS_FINE_LOCATION_CODE;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapView,
    DialogCreatorHelper.Callback, FABProgressListener, OnMarkerClickListener,
    OnMapClickListener {

  private static final String TAG = MapActivity.class.getSimpleName();
  private static final float ZOOM_LEVEL = 13.6f;
  private static final float RADIUS = 2500;
  private static final float STROKE_WIDTH = 1f;

  @BindView(R.id.map)
  View mapFragmentView;
  @BindView(R.id.fab_progress_circle)
  FABProgressCircle fabProgressCircle;
  @BindView(R.id.fab_refresh)
  FloatingActionButton fabRefresh;
  @BindView(R.id.ll_branch_container)
  LinearLayout llBranchContainer;
  @BindView(R.id.iv_branch_container_backward)
  AppCompatImageView ivBranchContainerBackward;
  @BindView(R.id.iv_branch_container_forward)
  AppCompatImageView ivBranchContainerForward;
  @BindView(R.id.tv_store_name)
  AppCompatTextView tvStoreName;
  @BindView(R.id.tv_branch_name)
  AppCompatTextView tvBranchName;
  @BindView(R.id.tv_branch_description)
  AppCompatTextView tvBranchDescription;
  @BindView(R.id.tv_branch_status)
  AppCompatTextView tvBranchStatus;
  @BindView(R.id.sv_branch_content)
  NestedScrollView nsvBranchContent;
  @BindView(R.id.tv_branch_contact_info_web_site)
  AppCompatTextView tvBranchContactInfoWebSite;
  @BindView(R.id.tv_branch_contact_info_facebook)
  AppCompatTextView tvBranchContactInfoFacebook;
  @BindView(R.id.tv_branch_contact_info_instagram)
  AppCompatTextView tvBranchContactInfoInstagram;
  @BindView(R.id.tv_branch_contact_info_whatsapp)
  AppCompatTextView tvBranchContactInfoWhatsApp;
  @BindView(R.id.tv_branch_contact_info_phone)
  AppCompatTextView tvBranchContactInfoPhone;
  @BindView(R.id.tv_branch_contact_info_email)
  AppCompatTextView tvBranchContactInfoEmail;
  @BindView(R.id.pb_branch_promos_progress)
  ProgressBar pbBranchPromosProgress;

  private GoogleMap mMap;
  private MapPresenter mMapPresenter;
  // The BottomSheet contains the information of every branch on the map.
  private BottomSheetBehavior mBranchContainer;
  private Marker mCurrentMarker;
  private Branch mCurrentBranch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(castofo.com.co.nower.R.layout.activity_map);
    ButterKnife.bind(this);
    fabProgressCircle.attachListener(this);
    mBranchContainer = BottomSheetBehavior.from(llBranchContainer);
    setBranchContainerCallback();

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
    mMap.getUiSettings().setMapToolbarEnabled(false);
    mMap.setOnMarkerClickListener(this);
    mMap.setOnMapClickListener(this);

    // After the map is ready, the user has to be located automatically.
    mMap.setOnMapLoadedCallback(() -> mMapPresenter.locateUser());
  }

  /**
   * Sets the Callback for the Branch container in order to control its behavior.
   */
  public void setBranchContainerCallback() {
    mBranchContainer.setState(BottomSheetBehavior.STATE_HIDDEN);
    mBranchContainer.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
          case BottomSheetBehavior.STATE_COLLAPSED:
            mMapPresenter.transformBranchContainer(newState);
            break;
          case BottomSheetBehavior.STATE_EXPANDED:
            mMapPresenter.transformBranchContainer(newState);
            break;
        }
      }

      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {

      }
    });
  }

  @Override
  public Activity getActivity() {
    return MapActivity.this;
  }

  @OnClick(R.id.fab_refresh)
  public void onRefreshClick() {
    Log.i(TAG, "Refresh button clicked.");
    mMapPresenter.locateUser();
  }

  @Override
  public void showProgress() {
    fabRefresh.setEnabled(false);
    fabRefresh.setImageResource(R.mipmap.ic_refresh_gray_24dp);
    fabProgressCircle.show();
  }

  @Override
  public void hideProgress() {
    fabProgressCircle.hide();
    fabRefresh.setImageResource(R.mipmap.ic_refresh_white_24dp);
    fabRefresh.setEnabled(true);
  }

  @Override
  public void finishProgress() {
    fabProgressCircle.beginFinalAnimation();
  }

  @Override
  public void onFABProgressAnimationEnd() {
    fabRefresh.setImageResource(R.mipmap.ic_refresh_white_24dp);
    fabRefresh.setEnabled(true);
  }

  @Override
  public void moveCamera(LatLng position) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
  }

  @Override
  public void animateCamera(LatLng position) {
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
  }

  @Override
  public void addMarkerForUser(LatLng userPosition) {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(userPosition)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    mMap.addMarker(markerOptions);
  }

  @Override
  public Marker addMarkerForBranch(LatLng branchPosition) {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(branchPosition)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
    // The Marker is returned to save it in the association map between Marker and Branch.
    return mMap.addMarker(markerOptions);
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
  public void clearMap() {
    mMap.clear();
  }

  /**
   * This method respects to two interfaces: {@link OnMarkerClickListener in Google} and
   * {@link MapView}.
   *
   * @param marker The marker that was clicked or selected
   * @return true
   */
  @Override
  public boolean onMarkerClick(Marker marker) {
    Log.i(TAG, "A marker was clicked.");
    mMapPresenter.manageMarker(marker);
    return true;
  }

  @Override
  public void onMapClick(LatLng latLng) {
    mMapPresenter.manageMapClick();
  }

  @Override
  public Marker getCurrentMarker() {
    return mCurrentMarker;
  }

  @Override
  public void setCurrentMarker(Marker marker) {
    mCurrentMarker = marker;
  }

  @Override
  public int getBranchContainerState() {
    return mBranchContainer.getState();
  }

  @Override
  public void setBranchContainerState(int newState) {
    mBranchContainer.setState(newState);
  }

  @Override
  public void setBranchContainerClosingVisible(boolean showClosing) {
    getSupportActionBar().setDisplayHomeAsUpEnabled(showClosing);
  }

  @Override
  public void showLocationPermissionExplanation() {
    DialogCreatorHelper locationPermissionExplanationDialog = new DialogCreatorHelper
        .Builder(getApplicationContext())
        .withTitleRes(R.string.label_location_permission)
        .withMessageRes(R.string.message_location_needed)
        .withPositiveBtnTextRes(R.string.action_ok)
        .withCallback(this)
        .withBooleanArg(DialogCreatorHelper.IS_CANCELABLE_KEY, false)
        .create();

    locationPermissionExplanationDialog
        .show(getSupportFragmentManager(),
            getResources().getString(R.string.label_location_permission));
  }

  @Override
  public void onDialogPositiveBtnClick(DialogFragment dialog, Bundle params) {
    Log.i(TAG, "The user clicked the positive action.");
    String dialogTag = dialog.getTag();
    if (dialogTag.equals(getResources().getString(R.string.label_location_permission))) {
      mMapPresenter.requestLocationPermission();
    }
  }

  @Override
  public void onDialogNegativeBtnClick(DialogFragment dialog, Bundle params) {
    Log.i(TAG, "The user clicked the negative action.");
  }

  @Override
  public void showGettingLocationError() {
    Snackbar gettingLocationErrorSnackbar = Snackbar
        .make(mapFragmentView, getResources().getString(R.string.error_getting_location),
            Snackbar.LENGTH_LONG);
    gettingLocationErrorSnackbar.setAction(getResources().getString(R.string.action_retry),
        view -> {
          // Retries to get the user's location.
          mMapPresenter.locateUser();
        });
    gettingLocationErrorSnackbar.show();
  }

  @Override
  public void showNoInternetError() {
    Snackbar.make(mapFragmentView, getResources().getString(R.string.error_no_internet),
        Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void showGettingNearbyBranchesError() {
    Snackbar gettingNearbyPromosErrorSnackbar = Snackbar
        .make(mapFragmentView, getResources().getString(R.string.error_getting_nearby_promos),
            Snackbar.LENGTH_LONG);
    gettingNearbyPromosErrorSnackbar.setAction(getResources().getString(R.string.action_retry),
        view -> {
          // Retries to get the user's location and his nearby promos.
          mMapPresenter.locateUser();
        });
    gettingNearbyPromosErrorSnackbar.show();
  }

  @Override
  public void showNoNearbyPromosMessage() {
    DialogCreatorHelper noNearbyPromosDialog = new DialogCreatorHelper
        .Builder(getApplicationContext())
        .withTitleRes(R.string.label_we_are_sorry)
        .withMessageRes(R.string.message_no_nearby_promos)
        .withPositiveBtnTextRes(R.string.action_ok)
        .withCallback(this)
        .withBooleanArg(DialogCreatorHelper.IS_CANCELABLE_KEY, true)
        .create();

    noNearbyPromosDialog.show(getSupportFragmentManager(),
        getResources().getString(R.string.label_we_are_sorry));
  }

  /**
   * Expands the Branch container after the user clicks on the Branch header.
   */
  @OnClick(R.id.fl_branch_header)
  public void onBranchHeaderClick() {
    mMapPresenter.manageBranchHeaderInteraction();
  }

  @OnClick(R.id.iv_branch_container_backward)
  public void onBackwardNavigationControlClick() {
    mMapPresenter.navigateOverBranchList(-1);
  }

  @OnClick(R.id.iv_branch_container_forward)
  public void onForwardNavigationControlClick() {
    mMapPresenter.navigateOverBranchList(1);
  }

  @Override
  public void setNavigationControlsVisibility(int visibility) {
    ivBranchContainerBackward.setVisibility(visibility);
    ivBranchContainerForward.setVisibility(visibility);
  }

  @Override
  public void showLoadingBranchError() {
    Snackbar.make(mapFragmentView, getResources().getString(R.string.error_loading_branch),
        Snackbar.LENGTH_LONG).show();
  }

  /**
   * Populates the Branch container using the information about the Branch and its corresponding
   * Store.
   */
  @Override
  public void populateBranchInfo(Branch branch) {
    // The current Branch is updated.
    mCurrentBranch = branch;

    // Scrolls the Branch content to the top.
    nsvBranchContent.fullScroll(ScrollView.FOCUS_UP);

    Store branchStore = branch.getStore();
    tvStoreName.setText(branchStore.getName());
    tvBranchName.setText(branch.getName());
    tvBranchDescription.setText(branchStore.getDescription());
    // Underlines the Branch status.
    tvBranchStatus.setPaintFlags(tvBranchStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    populateBranchContactInfo(branch.getContactInformations());
  }

  /**
   * Populates every item in the ContactInfo section of a particular Branch according to the found
   * keys.
   *
   * @param contactInformationList The ContactInformations of a particular Branch.
   */
  public void populateBranchContactInfo(RealmList<ContactInformation> contactInformationList) {
    for (ContactInformation contactInformation : contactInformationList) {
      switch (contactInformation.getKey()) {
        case "website":
          tvBranchContactInfoWebSite.setText(contactInformation.getValue());
          break;
        case "facebook":
          tvBranchContactInfoFacebook.setText(contactInformation.getValue());
          break;
        case "instagram":
          tvBranchContactInfoInstagram.setText(contactInformation.getValue());
          break;
        case "whatsapp":
          tvBranchContactInfoWhatsApp.setText(contactInformation.getValue());
          break;
        case "phone":
          tvBranchContactInfoPhone.setText(contactInformation.getValue());
          break;
        case "email":
          tvBranchContactInfoEmail.setText(contactInformation.getValue());
          break;
      }
    }
  }

  /**
   * Expands the Store and Branch names to show them completely.
   */
  @Override
  public void expandStoreAndBranchName() {
    tvStoreName.setMaxLines(Integer.MAX_VALUE);
    tvBranchName.setMaxLines(Integer.MAX_VALUE);
  }

  /**
   * Collapses the Store and Branch names to show them in just one line with ellipsis, if necessary.
   */
  @Override
  public void collapseStoreAndBranchName() {
    tvStoreName.setMaxLines(1);
    tvStoreName.setEllipsize(TextUtils.TruncateAt.END);
    tvBranchName.setMaxLines(1);
    tvBranchName.setEllipsize(TextUtils.TruncateAt.END);
  }

  @Override
  public void showLoadingBranchPromosError() {
    Snackbar.make(mapFragmentView, getResources().getString(R.string.error_loading_branch_promos),
        Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void hideLoadingBranchPromosProgress() {
    pbBranchPromosProgress.setVisibility(View.GONE);
  }

  /**
   * Shows a dialog containing the opening times of the selected Branch.
   */
  @OnClick(R.id.tv_branch_status)
  public void onBranchStatusClick() {
    DialogCreatorHelper branchOpeningTimesDialog = new DialogCreatorHelper
        .Builder(getApplicationContext())
        .withTitleRes(R.string.label_opening_times)
        .withMessage("Opening times message.")
        .withPositiveBtnTextRes(R.string.action_ok)
        .withCallback(this)
        .withBooleanArg(DialogCreatorHelper.IS_CANCELABLE_KEY, true)
        .create();

    branchOpeningTimesDialog.show(getSupportFragmentManager(),
        getResources().getString(R.string.label_opening_times));
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case ENABLE_GPS_REQUEST_CODE:
        switch (resultCode) {
          case Activity.RESULT_OK:
            // All required changes were successfully made.
            mMapPresenter.locateUser();
            break;
          case Activity.RESULT_CANCELED:
            // The user was asked to change settings, but chose not to.
            hideProgress();
            break;
          default:
            break;
        }
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_ACCESS_FINE_LOCATION_CODE: {
        // If the request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // The location permission was granted.
          mMapPresenter.locateUser();
        } else {
          // The location permission was denied.
        }
        break;
      }
    }
  }

  @Override
  protected void onDestroy() {
    mMapPresenter.onDestroy();
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // Collapses the Branch container after the user clicks on the closing icon.
        mMapPresenter.manageBranchHeaderInteraction();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
