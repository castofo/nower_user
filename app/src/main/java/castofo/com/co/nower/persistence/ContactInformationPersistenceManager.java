package castofo.com.co.nower.persistence;

import castofo.com.co.nower.models.ContactInformation;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alejandrosanchezaristizabal on 2/12/18.
 */

public class ContactInformationPersistenceManager {

  public static void deleteAllContactInformations() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      RealmResults<ContactInformation> contactInformations = realm.where(ContactInformation.class)
          .findAll();
      contactInformations.deleteAllFromRealm();
    });
  }
}
