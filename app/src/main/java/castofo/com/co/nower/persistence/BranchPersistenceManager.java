package castofo.com.co.nower.persistence;

import java.util.List;

import castofo.com.co.nower.models.Branch;
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

  public static Branch retrieveBranch(String branchId) {
    Realm realm = Realm.getDefaultInstance();
    Branch branch = realm.where(Branch.class).equalTo("id", branchId).findFirst();
    return realm.copyFromRealm(branch);
  }
}
