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

    // The description, nit, website, address and status attributes were added to the Store model.
    if (oldVersion == 1) {
      schema.get("Store")
          .addField("description", String.class)
          .addField("nit", String.class)
          .addField("website", String.class)
          .addField("address", String.class)
          .addField("status", String.class);
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }

    // The ContactInformation model was created and added as a field to the Branch model.
    if (oldVersion == 2) {
      schema.create("ContactInformation")
          .addField("id", String.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
          .addField("key", String.class)
          .addField("value", String.class)
          .addField("storeId", String.class);
      RealmObjectSchema branchSchema = schema.get("Branch");
      branchSchema.addRealmObjectField("contactInformation", schema.get("ContactInformation"));
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }

    // The ContactInformation field of the Branch model was replaced by a List of
    // ContactInformations.
    if (oldVersion == 3) {
      RealmObjectSchema contactInformation = schema.get("ContactInformation");
      schema.get("Branch")
          .removeField("contactInformation")
          .addRealmListField("contactInformations", contactInformation);
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }

    // The Promo model was created.
    if (oldVersion == 4) {
      schema.create("Promo")
          .addField("id", String.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
          .addField("name", String.class)
          .addField("description", String.class)
          .addField("terms", String.class)
          .addField("stock", Integer.class)
          .addField("price", Float.class)
          .addField("startDate", String.class)
          .addField("endDate", String.class);
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }

    // A list of Promos was added to the Branch model.
    if (oldVersion == 5) {
      RealmObjectSchema promo = schema.get("Promo");
      schema.get("Branch")
          .addRealmListField("promos", promo);
      oldVersion++;
      Log.i(TAG, "DB migrated from version " + (oldVersion - 1) + " to version " + oldVersion
          + ".");
    }
  }
}
