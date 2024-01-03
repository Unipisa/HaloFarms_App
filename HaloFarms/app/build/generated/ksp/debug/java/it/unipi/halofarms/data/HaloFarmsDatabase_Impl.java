package it.unipi.halofarms.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import it.unipi.halofarms.data.map.MapDao;
import it.unipi.halofarms.data.map.MapDao_Impl;
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointDao;
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointDao_Impl;
import it.unipi.halofarms.data.point.PointValueDao;
import it.unipi.halofarms.data.point.PointValueDao_Impl;
import it.unipi.halofarms.data.sample.SampleDao;
import it.unipi.halofarms.data.sample.SampleDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HaloFarmsDatabase_Impl extends HaloFarmsDatabase {
  private volatile MapDao _mapDao;

  private volatile PointValueDao _pointValueDao;

  private volatile PerimeterPointDao _perimeterPointDao;

  private volatile SampleDao _sampleDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(6) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `maps` (`name` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `mode` TEXT NOT NULL, `area` REAL NOT NULL, `done` INTEGER NOT NULL, `date` TEXT NOT NULL, PRIMARY KEY(`name`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `pointsValue` (`latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `zoneName` TEXT NOT NULL, `qrCode` TEXT NOT NULL, PRIMARY KEY(`latitude`, `longitude`, `zoneName`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `perimeterPoints` (`latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `zoneName` TEXT NOT NULL, PRIMARY KEY(`latitude`, `longitude`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `samples` (`latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `sar` REAL NOT NULL, `ph` REAL NOT NULL, `ec` REAL NOT NULL, `cec` REAL NOT NULL, `date` TEXT NOT NULL, `toBeAnalyzed` INTEGER NOT NULL, `zoneName` TEXT NOT NULL, PRIMARY KEY(`latitude`, `longitude`, `date`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5a34ae405e6b00e5e183df959435ba82')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `maps`");
        _db.execSQL("DROP TABLE IF EXISTS `pointsValue`");
        _db.execSQL("DROP TABLE IF EXISTS `perimeterPoints`");
        _db.execSQL("DROP TABLE IF EXISTS `samples`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsMaps = new HashMap<String, TableInfo.Column>(7);
        _columnsMaps.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("area", new TableInfo.Column("area", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("done", new TableInfo.Column("done", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaps.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMaps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMaps = new TableInfo("maps", _columnsMaps, _foreignKeysMaps, _indicesMaps);
        final TableInfo _existingMaps = TableInfo.read(_db, "maps");
        if (! _infoMaps.equals(_existingMaps)) {
          return new RoomOpenHelper.ValidationResult(false, "maps(it.unipi.halofarms.data.map.Map).\n"
                  + " Expected:\n" + _infoMaps + "\n"
                  + " Found:\n" + _existingMaps);
        }
        final HashMap<String, TableInfo.Column> _columnsPointsValue = new HashMap<String, TableInfo.Column>(4);
        _columnsPointsValue.put("latitude", new TableInfo.Column("latitude", "REAL", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPointsValue.put("longitude", new TableInfo.Column("longitude", "REAL", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPointsValue.put("zoneName", new TableInfo.Column("zoneName", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPointsValue.put("qrCode", new TableInfo.Column("qrCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPointsValue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPointsValue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPointsValue = new TableInfo("pointsValue", _columnsPointsValue, _foreignKeysPointsValue, _indicesPointsValue);
        final TableInfo _existingPointsValue = TableInfo.read(_db, "pointsValue");
        if (! _infoPointsValue.equals(_existingPointsValue)) {
          return new RoomOpenHelper.ValidationResult(false, "pointsValue(it.unipi.halofarms.data.point.PointValue).\n"
                  + " Expected:\n" + _infoPointsValue + "\n"
                  + " Found:\n" + _existingPointsValue);
        }
        final HashMap<String, TableInfo.Column> _columnsPerimeterPoints = new HashMap<String, TableInfo.Column>(3);
        _columnsPerimeterPoints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerimeterPoints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerimeterPoints.put("zoneName", new TableInfo.Column("zoneName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPerimeterPoints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPerimeterPoints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPerimeterPoints = new TableInfo("perimeterPoints", _columnsPerimeterPoints, _foreignKeysPerimeterPoints, _indicesPerimeterPoints);
        final TableInfo _existingPerimeterPoints = TableInfo.read(_db, "perimeterPoints");
        if (! _infoPerimeterPoints.equals(_existingPerimeterPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "perimeterPoints(it.unipi.halofarms.data.perimeterPoint.PerimeterPoint).\n"
                  + " Expected:\n" + _infoPerimeterPoints + "\n"
                  + " Found:\n" + _existingPerimeterPoints);
        }
        final HashMap<String, TableInfo.Column> _columnsSamples = new HashMap<String, TableInfo.Column>(9);
        _columnsSamples.put("latitude", new TableInfo.Column("latitude", "REAL", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("longitude", new TableInfo.Column("longitude", "REAL", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("sar", new TableInfo.Column("sar", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("ph", new TableInfo.Column("ph", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("ec", new TableInfo.Column("ec", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("cec", new TableInfo.Column("cec", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("date", new TableInfo.Column("date", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("toBeAnalyzed", new TableInfo.Column("toBeAnalyzed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("zoneName", new TableInfo.Column("zoneName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSamples = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSamples = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSamples = new TableInfo("samples", _columnsSamples, _foreignKeysSamples, _indicesSamples);
        final TableInfo _existingSamples = TableInfo.read(_db, "samples");
        if (! _infoSamples.equals(_existingSamples)) {
          return new RoomOpenHelper.ValidationResult(false, "samples(it.unipi.halofarms.data.sample.Sample).\n"
                  + " Expected:\n" + _infoSamples + "\n"
                  + " Found:\n" + _existingSamples);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5a34ae405e6b00e5e183df959435ba82", "9861193b9982cf20642af64cca4638e3");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "maps","pointsValue","perimeterPoints","samples");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `maps`");
      _db.execSQL("DELETE FROM `pointsValue`");
      _db.execSQL("DELETE FROM `perimeterPoints`");
      _db.execSQL("DELETE FROM `samples`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MapDao.class, MapDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PointValueDao.class, PointValueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PerimeterPointDao.class, PerimeterPointDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SampleDao.class, SampleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public MapDao mapDao() {
    if (_mapDao != null) {
      return _mapDao;
    } else {
      synchronized(this) {
        if(_mapDao == null) {
          _mapDao = new MapDao_Impl(this);
        }
        return _mapDao;
      }
    }
  }

  @Override
  public PointValueDao pointValueDao() {
    if (_pointValueDao != null) {
      return _pointValueDao;
    } else {
      synchronized(this) {
        if(_pointValueDao == null) {
          _pointValueDao = new PointValueDao_Impl(this);
        }
        return _pointValueDao;
      }
    }
  }

  @Override
  public PerimeterPointDao perimeterPointDao() {
    if (_perimeterPointDao != null) {
      return _perimeterPointDao;
    } else {
      synchronized(this) {
        if(_perimeterPointDao == null) {
          _perimeterPointDao = new PerimeterPointDao_Impl(this);
        }
        return _perimeterPointDao;
      }
    }
  }

  @Override
  public SampleDao sampleDao() {
    if (_sampleDao != null) {
      return _sampleDao;
    } else {
      synchronized(this) {
        if(_sampleDao == null) {
          _sampleDao = new SampleDao_Impl(this);
        }
        return _sampleDao;
      }
    }
  }
}
