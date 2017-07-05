package castofo.com.co.nower.persistence;

import castofo.com.co.nower.models.Store;
import io.realm.Realm;

/**
 * Created by alejandrosanchezaristizabal on 6/17/17.
 */

public class StorePersistenceManager {

  public static void createStore(Store store) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransactionAsync(realmInstance -> realmInstance.copyToRealmOrUpdate(store));
  }
}
