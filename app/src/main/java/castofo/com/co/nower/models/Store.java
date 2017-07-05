package castofo.com.co.nower.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alejandrosanchezaristizabal on 7/4/17.
 */

public class Store extends RealmObject {

  @PrimaryKey
  @Required
  private String id;
  private String name;

  public Store() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
