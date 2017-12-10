package castofo.com.co.nower.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;
import castofo.com.co.nower.utils.DateTimeHelper;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alejandrosanchezaristizabal on 16/10/17.
 */

public class PromoPersistenceManager {

  /**
   * Updates the Promo list of the specified Branch and creates the Promos implicitly.
   *
   * @param branchId        The id of the Branch for which the Promos will be updated.
   * @param branchPromoList The list of active Promos of the Branch.
   */
  public static void createBranchPromos(final String branchId, final List<Promo> branchPromoList) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      Branch branch = realmInstance.where(Branch.class)
          .equalTo("id", branchId).findFirst();
      branch.replacePromos(branchPromoList);
    });
  }

  /**
   * Creates a {@link Single<List<Promo>>} that retrieves the list of active Promos of the specified
   * Branch and emits it.
   * If the list of Promos is empty, the method emits an empty list.
   *
   * @param branchId The id of the Branch for which the Promos will be retrieved.
   * @return A {@link Single<List<Promo>>} that will emit the result.
   */
  public static Single<List<Promo>> retrieveBranchPromos(String branchId) {
    return Single.fromCallable(() -> {
      Date currentDate = DateTimeHelper.getCurrentDate();
      Realm realm = Realm.getDefaultInstance();
      RealmResults<Promo> inactiveBranchPromos = realm.where(Promo.class)
          .equalTo("branches.id", branchId)
          .beginGroup()
          .isNotNull("stock").equalTo("stock", 0)
          .or()
          .isNotNull("endDate").lessThan("endDate", currentDate)
          .endGroup()
          .findAll();
      inactiveBranchPromos.deleteAllFromRealm();

      // Retrieves the list of active Promos of the specified Branch after deleting its inactive
      // ones.
      RealmResults<Promo> branchPromos = realm.where(Promo.class)
          .equalTo("branches.id", branchId).findAll();
      return !branchPromos.isEmpty() ? realm.copyFromRealm(branchPromos) : new ArrayList<Promo>();
    });
  }
}
