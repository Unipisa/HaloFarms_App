package it.unipi.halofarms.data.sample;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SampleDao_Impl implements SampleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Sample> __insertionAdapterOfSample;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSarList;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhList;

  private final SharedSQLiteStatement __preparedStmtOfUpdateEcList;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCecList;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSample;

  private final SharedSQLiteStatement __preparedStmtOfUpdateToBeAnalyzed;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDate;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllSamples;

  public SampleDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSample = new EntityInsertionAdapter<Sample>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `samples` (`latitude`,`longitude`,`sar`,`ph`,`ec`,`cec`,`date`,`toBeAnalyzed`,`zoneName`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Sample value) {
        stmt.bindDouble(1, value.getLatitude());
        stmt.bindDouble(2, value.getLongitude());
        stmt.bindDouble(3, value.getSar());
        stmt.bindDouble(4, value.getPh());
        stmt.bindDouble(5, value.getEc());
        stmt.bindDouble(6, value.getCec());
        if (value.getDate() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDate());
        }
        final int _tmp = value.getToBeAnalyzed() ? 1 : 0;
        stmt.bindLong(8, _tmp);
        if (value.getZoneName() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getZoneName());
        }
      }
    };
    this.__preparedStmtOfUpdateSarList = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET sar = ? AND date = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhList = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET ph = ? AND date = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateEcList = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET ec = ? AND date = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateCecList = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET cec = ? AND date = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSample = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM samples WHERE latitude = ? AND (longitude = ? AND date = ?)";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateToBeAnalyzed = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET toBeAnalyzed = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDate = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE samples SET date = ? WHERE latitude = ? AND longitude = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllSamples = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM samples WHERE zoneName = ?";
        return _query;
      }
    };
  }

  @Override
  public void addSample(final Sample sample) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSample.insert(sample);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Object updateSarList(final double latitude, final double longitude, final double sar,
      final String date, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSarList.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, sar);
        _argIndex = 2;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 4;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateSarList.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updatePhList(final double latitude, final double longitude, final double ph,
      final String date, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhList.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, ph);
        _argIndex = 2;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 4;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdatePhList.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateEcList(final double latitude, final double longitude, final double ec,
      final String date, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateEcList.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, ec);
        _argIndex = 2;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 4;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateEcList.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateCecList(final double latitude, final double longitude, final double cec,
      final String date, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCecList.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, cec);
        _argIndex = 2;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 4;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateCecList.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteSample(final double latitude, final double longitude, final String date,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSample.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, longitude);
        _argIndex = 3;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteSample.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateToBeAnalyzed(final double latitude, final double longitude,
      final boolean toBeAnalyzed, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateToBeAnalyzed.acquire();
        int _argIndex = 1;
        final int _tmp = toBeAnalyzed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateToBeAnalyzed.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateDate(final double latitude, final double longitude, final String date,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDate.acquire();
        int _argIndex = 1;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, date);
        }
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, longitude);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateDate.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAllSamples(final String mapName,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllSamples.acquire();
        int _argIndex = 1;
        if (mapName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, mapName);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAllSamples.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<Sample>> getSamples(final String date, final String mapName) {
    final String _sql = "SELECT * FROM samples WHERE date = ? AND zoneName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    _argIndex = 2;
    if (mapName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mapName);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"samples"}, new Callable<List<Sample>>() {
      @Override
      public List<Sample> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSar = CursorUtil.getColumnIndexOrThrow(_cursor, "sar");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfEc = CursorUtil.getColumnIndexOrThrow(_cursor, "ec");
          final int _cursorIndexOfCec = CursorUtil.getColumnIndexOrThrow(_cursor, "cec");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfToBeAnalyzed = CursorUtil.getColumnIndexOrThrow(_cursor, "toBeAnalyzed");
          final int _cursorIndexOfZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneName");
          final List<Sample> _result = new ArrayList<Sample>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Sample _item;
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpSar;
            _tmpSar = _cursor.getDouble(_cursorIndexOfSar);
            final double _tmpPh;
            _tmpPh = _cursor.getDouble(_cursorIndexOfPh);
            final double _tmpEc;
            _tmpEc = _cursor.getDouble(_cursorIndexOfEc);
            final double _tmpCec;
            _tmpCec = _cursor.getDouble(_cursorIndexOfCec);
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final boolean _tmpToBeAnalyzed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfToBeAnalyzed);
            _tmpToBeAnalyzed = _tmp != 0;
            final String _tmpZoneName;
            if (_cursor.isNull(_cursorIndexOfZoneName)) {
              _tmpZoneName = null;
            } else {
              _tmpZoneName = _cursor.getString(_cursorIndexOfZoneName);
            }
            _item = new Sample(_tmpLatitude,_tmpLongitude,_tmpSar,_tmpPh,_tmpEc,_tmpCec,_tmpDate,_tmpToBeAnalyzed,_tmpZoneName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Sample> getSample(final double latitude, final double longitude, final String date) {
    final String _sql = "SELECT * FROM samples WHERE latitude = ? AND (longitude = ? AND date = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, latitude);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, longitude);
    _argIndex = 3;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"samples"}, new Callable<Sample>() {
      @Override
      public Sample call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSar = CursorUtil.getColumnIndexOrThrow(_cursor, "sar");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfEc = CursorUtil.getColumnIndexOrThrow(_cursor, "ec");
          final int _cursorIndexOfCec = CursorUtil.getColumnIndexOrThrow(_cursor, "cec");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfToBeAnalyzed = CursorUtil.getColumnIndexOrThrow(_cursor, "toBeAnalyzed");
          final int _cursorIndexOfZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneName");
          final Sample _result;
          if(_cursor.moveToFirst()) {
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpSar;
            _tmpSar = _cursor.getDouble(_cursorIndexOfSar);
            final double _tmpPh;
            _tmpPh = _cursor.getDouble(_cursorIndexOfPh);
            final double _tmpEc;
            _tmpEc = _cursor.getDouble(_cursorIndexOfEc);
            final double _tmpCec;
            _tmpCec = _cursor.getDouble(_cursorIndexOfCec);
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final boolean _tmpToBeAnalyzed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfToBeAnalyzed);
            _tmpToBeAnalyzed = _tmp != 0;
            final String _tmpZoneName;
            if (_cursor.isNull(_cursorIndexOfZoneName)) {
              _tmpZoneName = null;
            } else {
              _tmpZoneName = _cursor.getString(_cursorIndexOfZoneName);
            }
            _result = new Sample(_tmpLatitude,_tmpLongitude,_tmpSar,_tmpPh,_tmpEc,_tmpCec,_tmpDate,_tmpToBeAnalyzed,_tmpZoneName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
