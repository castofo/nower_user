package castofo.com.co.nower.persistence;

import castofo.com.co.nower.models.Store;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alejandrosanchezaristizabal on 2/12/18.
 */

public class StorePersistenceManager {

  public static void deleteAllStores() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      RealmResults<Store> stores = realm.where(Store.class).findAll();
      stores.deleteAllFromRealm();
    });
  }
}
