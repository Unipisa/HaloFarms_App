package it.unipi.halofarms.data.map;

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
public final class MapDao_Impl implements MapDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Map> __insertionAdapterOfMap;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMap;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLatLng;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMode;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDone;

  private final SharedSQLiteStatement __preparedStmtOfUpdateArea;

  public MapDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMap = new EntityInsertionAdapter<Map>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `maps` (`name`,`latitude`,`longitude`,`mode`,`area`,`done`,`date`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Map value) {
        if (value.getName() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getName());
        }
        stmt.bindDouble(2, value.getLatitude());
        stmt.bindDouble(3, value.getLongitude());
        if (value.getMode() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getMode());
        }
        stmt.bindDouble(5, value.getArea());
        final int _tmp = value.getDone() ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.getDate() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDate());
        }
      }
    };
    this.__preparedStmtOfDeleteMap = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM maps WHERE name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLatLng = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE maps SET latitude = ?, longitude = ? WHERE name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMode = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE maps SET mode = ? WHERE name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDone = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE maps SET done = ? WHERE name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateArea = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE maps SET area = ? WHERE name = ?";
        return _query;
      }
    };
  }

  @Override
  public void addMap(final Map map) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMap.insert(map);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Object deleteMap(final String mapName, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMap.acquire();
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
          __preparedStmtOfDeleteMap.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateLatLng(final String mapName, final double latitude, final double longitude,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLatLng.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, longitude);
        _argIndex = 3;
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
          __preparedStmtOfUpdateLatLng.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateMode(final String mapName, final String mode,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMode.acquire();
        int _argIndex = 1;
        if (mode == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, mode);
        }
        _argIndex = 2;
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
          __preparedStmtOfUpdateMode.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateDone(final String mapName, final boolean done,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDone.acquire();
        int _argIndex = 1;
        final int _tmp = done ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateDone.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object updateArea(final String mapName, final double area,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateArea.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, area);
        _argIndex = 2;
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
          __preparedStmtOfUpdateArea.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<Map> findMap(final String mapName) {
    final String _sql = "SELECT * FROM maps WHERE name = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mapName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mapName);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"maps"}, new Callable<Map>() {
      @Override
      public Map call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfArea = CursorUtil.getColumnIndexOrThrow(_cursor, "area");
          final int _cursorIndexOfDone = CursorUtil.getColumnIndexOrThrow(_cursor, "done");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final Map _result;
          if(_cursor.moveToFirst()) {
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpMode;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmpMode = null;
            } else {
              _tmpMode = _cursor.getString(_cursorIndexOfMode);
            }
            final double _tmpArea;
            _tmpArea = _cursor.getDouble(_cursorIndexOfArea);
            final boolean _tmpDone;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfDone);
            _tmpDone = _tmp != 0;
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            _result = new Map(_tmpName,_tmpLatitude,_tmpLongitude,_tmpMode,_tmpArea,_tmpDone,_tmpDate);
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

  @Override
  public Flow<List<Map>> getAllMaps() {
    final String _sql = "SELECT * FROM maps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"maps"}, new Callable<List<Map>>() {
      @Override
      public List<Map> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfArea = CursorUtil.getColumnIndexOrThrow(_cursor, "area");
          final int _cursorIndexOfDone = CursorUtil.getColumnIndexOrThrow(_cursor, "done");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<Map> _result = new ArrayList<Map>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Map _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpMode;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmpMode = null;
            } else {
              _tmpMode = _cursor.getString(_cursorIndexOfMode);
            }
            final double _tmpArea;
            _tmpArea = _cursor.getDouble(_cursorIndexOfArea);
            final boolean _tmpDone;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfDone);
            _tmpDone = _tmp != 0;
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            _item = new Map(_tmpName,_tmpLatitude,_tmpLongitude,_tmpMode,_tmpArea,_tmpDone,_tmpDate);
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

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
