package castofo.com.co.nower.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Alejandro on 29/10/2016.
 */
public class Branch extends RealmObject {

  @PrimaryKey
  @Required
  private String id;
  private double latitude;
  private double longitude;
  private String name;
  private String address;
  @SerializedName("default_contact_info")
  private boolean defaultContactInfo;
  private Store store;
  @SerializedName("contact_informations")
  private RealmList<ContactInformation> contactInformations;

  public Branch() {}

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isDefaultContactInfo() {
    return defaultContactInfo;
  }

  public void setDefaultContactInfo(boolean defaultContactInfo) {
    this.defaultContactInfo = defaultContactInfo;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public RealmList<ContactInformation> getContactInformations() {
    return contactInformations;
  }

  public void setContactInformations(RealmList<ContactInformation> contactInformations) {
    this.contactInformations = contactInformations;
  }
}
