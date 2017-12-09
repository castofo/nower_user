package castofo.com.co.nower.persistence;

import java.util.ArrayList;
import java.util.List;

import castofo.com.co.nower.models.Branch;
import castofo.com.co.nower.models.Promo;
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
   * @param branchId The id of the Branch for which the Promos will be updated.
   * @param branchPromoList The list of Promos of the Branch.
   */
  public static void createBranchPromos(final String branchId, final List<Promo> branchPromoList) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(realmInstance -> {
      Branch branch = realmInstance.where(Branch.class).equalTo("id", branchId).findFirst();
      branch.replacePromos(branchPromoList);
    });
  }

  /**
   * Creates a {@link Single<List<Promo>>} that retrieves the list of Promos of the specified
   * Branch and emits it.
   * If the list of Promos is empty, the method emits an empty list.
   *
   * @param branchId The id of the Branch for which the Promos will be retrieved.
   * @return A {@link Single<List<Promo>>} that will emit the result.
   */
  public static Single<List<Promo>> retrieveBranchPromos(String branchId) {
    return Single.fromCallable(() -> {
      Realm realm = Realm.getDefaultInstance();
      RealmResults<Promo> branchPromos = realm.where(Promo.class).equalTo("branches.id", branchId)
          .findAll();
      // TODO filter Promos before retrieving the list and delete the inactive ones from Realm.
      return !branchPromos.isEmpty() ? realm.copyFromRealm(branchPromos) : new ArrayList<Promo>();
    });
  }
}
