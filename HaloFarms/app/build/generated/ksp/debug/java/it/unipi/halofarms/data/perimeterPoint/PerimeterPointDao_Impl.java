package it.unipi.halofarms.data.perimeterPoint;

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
public final class PerimeterPointDao_Impl implements PerimeterPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PerimeterPoint> __insertionAdapterOfPerimeterPoint;

  private final SharedSQLiteStatement __preparedStmtOfDeletePPoints;

  public PerimeterPointDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPerimeterPoint = new EntityInsertionAdapter<PerimeterPoint>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `perimeterPoints` (`latitude`,`longitude`,`zoneName`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PerimeterPoint value) {
        stmt.bindDouble(1, value.getLatitude());
        stmt.bindDouble(2, value.getLongitude());
        if (value.getZoneName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getZoneName());
        }
      }
    };
    this.__preparedStmtOfDeletePPoints = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM perimeterPoints WHERE zoneName = ?";
        return _query;
      }
    };
  }

  @Override
  public void addPPoint(final PerimeterPoint ppoint) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPerimeterPoint.insert(ppoint);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Object deletePPoints(final String name, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePPoints.acquire();
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
          __preparedStmtOfDeletePPoints.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<PerimeterPoint>> getAllPPoints() {
    final String _sql = "SELECT * FROM perimeterPoints";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"perimeterPoints"}, new Callable<List<PerimeterPoint>>() {
      @Override
      public List<PerimeterPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfZoneName = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneName");
          final List<PerimeterPoint> _result = new ArrayList<PerimeterPoint>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PerimeterPoint _item;
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
            _item = new PerimeterPoint(_tmpLatitude,_tmpLongitude,_tmpZoneName);
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
