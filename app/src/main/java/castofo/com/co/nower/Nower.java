package castofo.com.co.nower;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import castofo.com.co.nower.persistence.LocalMigrationManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alejandrosanchezaristizabal on 5/14/17.
 */

public class Nower extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    Realm.init(this);

    RealmConfiguration config = new RealmConfiguration.Builder()
        .schemaVersion(7) // Must be manually bumped when the schema changes.
        .migration(new LocalMigrationManager())
        .build();
    Realm.setDefaultConfiguration(config);

    // Stetho initialization is needed to see the db on the Chrome inspector.
    Stetho.initialize(Stetho.newInitializerBuilder(this)
        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
        .build());
  }
}
