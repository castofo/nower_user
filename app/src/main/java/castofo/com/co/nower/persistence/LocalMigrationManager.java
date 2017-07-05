package castofo.com.co.nower.persistence;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
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

    // The Store model was created and added as a field to the Branch model.
    if (oldVersion == 0) {
      schema.create("Store")
          .addField("id", String.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
          .addField("name", String.class);
      RealmObjectSchema branchSchema = schema.get("Branch");
      branchSchema.addRealmObjectField("store", schema.get("Store"));
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }
  }
}
