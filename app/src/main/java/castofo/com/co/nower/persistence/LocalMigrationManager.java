package castofo.com.co.nower.persistence;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by alejandrosanchezaristizabal on 6/17/17.
 */

public class LocalMigrationManager implements RealmMigration {

  public static final String TAG = LocalMigrationManager.class.getSimpleName();

  @Override
  public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    // The schema is required to execute the migrations on it.
    RealmSchema schema = realm.getSchema();

    /*
    // The name field was added to the Branch model.
    if (oldVersion == 0) {
      RealmObjectSchema branchSchema = schema.get("Branch");
      branchSchema.addField("name", String.class);
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }
    */
  }
}
