package castofo.com.co.nower.persistence;

import java.util.List;

import castofo.com.co.nower.models.Branch;
import io.reactivex.Single;
import io.realm.Realm;

/**
 * Created by alejandrosanchezaristizabal on 6/17/17.
 */

public class BranchPersistenceManager {

  public static void createBranchesInList(final List<Branch> branchList) {
    for (Branch branch : branchList) {
      createBranch(branch);
    }
  }

  public static void createBranch(Branch branch) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransactionAsync(realmInstance -> realmInstance.copyToRealmOrUpdate(branch));
  }

  /**
   * Creates a {@link Single<Branch>} that retrieves the specified Branch and emits it.
   * If the Branch is not found, the method emits an empty Branch instance.
   *
   * @param branchId The id of the Branch to be retrieved.
   * @return A {@link Single<Branch>} that will emit the result.
   */
  public static Single<Branch> retrieveBranch(String branchId) {
    return Single.fromCallable(() -> {
      Realm realm = Realm.getDefaultInstance();
      Branch branch = realm.where(Branch.class).equalTo("id", branchId).findFirst();
      return branch != null ? realm.copyFromRealm(branch) : new Branch();
    });
  }
}
