package castofo.com.co.nower.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alejandrosanchezaristizabal on 8/21/17.
 */

public class ContactInformation extends RealmObject {

  @PrimaryKey
  @Required
  private String id;
  private String key;
  private String value;
  @SerializedName("store_id")
  private String storeId;

  public ContactInformation() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }
}
