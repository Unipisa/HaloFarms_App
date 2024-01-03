package it.unipi.halofarms.data.point;

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
public final class PointValueDao_Impl implements PointValueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PointValue> __insertionAdapterOfPointValue;

  private final SharedSQLiteStatement __preparedStmtOfDeletePoints;

  public PointValueDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPointValue = new EntityInsertionAdapter<PointValue>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `pointsValue` (`latitude`,`longitude`,`zoneName`,`qrCode`) VALUES (?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PointValue value) {
        stmt.bindDouble(1, value.getLatitude());
        stmt.bindDouble(2, value.getLongitude());
        if (value.getZoneName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getZoneName());
        }
        if (value.getQrCode() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getQrCode());
        }
      }
    };
    this.__preparedStmtOfDeletePoints = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM pointsValue WHERE zoneName = ?";
        return _query;
      }
    };
  }

  @Override
  public void addPoint(final PointValue point) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPointValue.insert(point);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Object deletePoints(final String name, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePoints.acquire();
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, name);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeletePoints.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<PointValue> findPoint(final double latitude, final double longitude) {
    final String _sql = "SELECT * FROM pointsValue WHERE latitude = ? AND longitude = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, latitude);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, longitude);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"pointsValue"}, new Callable<PointValue>() {
      @Override
      public PointValue call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneName");
          final int _cursorIndexOfQrCode = CursorUtil.getColumnIndexOrThrow(_cursor, "qrCode");
          final PointValue _result;
          if(_cursor.moveToFirst()) {
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpZoneName;
            if (_cursor.isNull(_cursorIndexOfZoneName)) {
              _tmpZoneName = null;
            } else {
              _tmpZoneName = _cursor.getString(_cursorIndexOfZoneName);
            }
            final String _tmpQrCode;
            if (_cursor.isNull(_cursorIndexOfQrCode)) {
              _tmpQrCode = null;
            } else {
              _tmpQrCode = _cursor.getString(_cursorIndexOfQrCode);
            }
            _result = new PointValue(_tmpLatitude,_tmpLongitude,_tmpZoneName,_tmpQrCode);
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
  public Flow<List<PointValue>> getPoints(final String mapName) {
    final String _sql = "SELECT * FROM pointsValue WHERE zoneName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mapName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mapName);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"pointsValue"}, new Callable<List<PointValue>>() {
      @Override
      public List<PointValue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneName");
          final int _cursorIndexOfQrCode = CursorUtil.getColumnIndexOrThrow(_cursor, "qrCode");
          final List<PointValue> _result = new ArrayList<PointValue>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PointValue _item;
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpZoneName;
            if (_cursor.isNull(_cursorIndexOfZoneName)) {
              _tmpZoneName = null;
            } else {
              _tmpZoneName = _cursor.getString(_cursorIndexOfZoneName);
            }
            final String _tmpQrCode;
            if (_cursor.isNull(_cursorIndexOfQrCode)) {
              _tmpQrCode = null;
            } else {
              _tmpQrCode = _cursor.getString(_cursorIndexOfQrCode);
            }
            _item = new PointValue(_tmpLatitude,_tmpLongitude,_tmpZoneName,_tmpQrCode);
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
