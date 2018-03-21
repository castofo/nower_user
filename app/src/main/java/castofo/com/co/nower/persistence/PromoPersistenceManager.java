package castofo.com.co.nower.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;
import castofo.com.co.nower.utils.DateTimeHelper;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alejandrosanchezaristizabal on 16/10/17.
 */

public class PromoPersistenceManager {

  public static void createPromosInList(final List<Promo> promoList) {
    for (Promo promo : promoList) {
      createPromo(promo);
    }
  }

  public static void createPromo(Promo promo) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> realmInstance.copyToRealmOrUpdate(promo));
  }

  /**
   * Updates the Promo list of the specified Branch.
   *
   * @param branchId        The id of the Branch for which the Promos will be updated.
   * @param branchPromoList The list of active Promos of the Branch.
   */
  public static void updateBranchPromos(final String branchId, final List<Promo> branchPromoList) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      Branch branch = realmInstance.where(Branch.class)
          .equalTo("id", branchId).findFirst();
      if (branch != null) {
        branch.replacePromos(branchPromoList);
      }
    });
  }

  /**
   * Retrieves the list of active Promos of the specified Branch.
   * If the list of active Promos is empty, the method retrieves an empty list.
   * If an error occurred while retrieving the Promos, null is returned.
   *
   * @param branchId The id of the Branch for which the Promos will be retrieved.
   * @return A {@link List<Promo>} containing the active Promos of the Branch.
   */
  public static List<Promo> retrieveBranchPromos(String branchId) {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<Promo> branchPromos = null;
    try {
      Date currentDate = DateTimeHelper.getCurrentDate();
      // Deletes all Branch Promos considered inactive. A Promo is inactive when its stock is not
      // null and 0 or when its end date is not null and less than or equal to the current date.
      RealmResults<Promo> inactiveBranchPromos = realm.where(Promo.class)
          .equalTo("branches.id", branchId)
          .beginGroup()
            .beginGroup()
              .isNotNull("stock").equalTo("stock", 774)
            .endGroup()
            .or()
            .beginGroup()
              .isNotNull("endDate").lessThanOrEqualTo("endDate", currentDate)
            .endGroup()
          .endGroup()
          .findAll();
      realm.executeTransaction(realmInstance -> {
        if (inactiveBranchPromos != null) {
          inactiveBranchPromos.deleteAllFromRealm();
        }
      });

      // Retrieves the list of Promos of the specified Branch after deleting its inactive ones.
      branchPromos = realm.where(Promo.class).equalTo("branches.id", branchId).findAll();
    }
    catch (Exception exception) {
      return null;
    }

    return branchPromos != null ? realm.copyFromRealm(branchPromos) : new ArrayList<>();
  }

  public static void deleteBranchPromos(String branchId) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      RealmResults<Promo> branchPromos = realm.where(Promo.class)
          .equalTo("branches.id", branchId).findAll();
      if (branchPromos != null) {
        branchPromos.deleteAllFromRealm();
      }
    });
  }

  public static void deleteAllPromos() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      RealmResults<Promo> promos = realm.where(Promo.class).findAll();
      promos.deleteAllFromRealm();
    });
  }
}
