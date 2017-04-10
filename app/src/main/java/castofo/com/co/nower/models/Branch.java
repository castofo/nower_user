package castofo.com.co.nower.models;

/**
 * Created by Alejandro on 29/10/2016.
 */
public class Branch {

  private String id;
  private double latitude;
  private double longitude;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
