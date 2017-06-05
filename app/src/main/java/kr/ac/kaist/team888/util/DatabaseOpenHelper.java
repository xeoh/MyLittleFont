package kr.ac.kaist.team888.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import kr.ac.kaist.team888.mylittlefont.FontItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Database manager for stored font features.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_NAME = "feature.db";
  private static final String TABLE_NAME = "feature";
  private static final String KEY_ID = "id";
  private static final String KEY_NAME = "name";
  private static final String KEY_DATETIME = "date";
  private static final String KEY_CURVE = "curve";
  private static final String KEY_ROUNDNESS = "roundness";
  private static final String KEY_WEIGHT = "weight";
  private static final String KEY_CONTRAST = "contrast";
  private static final String KEY_WIDTH = "width";
  private static final String KEY_FLATTENING = "flattening";
  private static final String KEY_ARISE = "arise";
  private static final String KEY_SLANT = "slant";

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final String TABLE_CREATE =
      "CREATE TABLE " + TABLE_NAME + " ("
          + KEY_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + KEY_NAME        + " TEXT, "
          + KEY_DATETIME    + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
          + KEY_CURVE       + " REAL, "
          + KEY_ROUNDNESS   + " REAL, "
          + KEY_WEIGHT      + " REAL, "
          + KEY_CONTRAST    + " REAL, "
          + KEY_WIDTH       + " REAL, "
          + KEY_FLATTENING  + " REAL, "
          + KEY_ARISE       + " REAL, "
          + KEY_SLANT       + " REAL);";

  private static final String TABLE_SELECT = "SELECT * FROM %s ORDER BY %s DESC";
  private static final String ROW_DELETE = "DELETE FROM %s WHERE %s=%d";

  public DatabaseOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(TABLE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

  /**
   * Saves feature values from the {@link FeatureController} in the database
   * with the given name and current datetime.
   *
   * @param name a font name
   */
  public void saveItem(String name) {
    FeatureController features = FeatureController.getInstance();
    ContentValues values = new ContentValues();
    values.put(KEY_NAME, name);
    values.put(KEY_CURVE, features.getCurve());
    values.put(KEY_ROUNDNESS, features.getRoundness());
    values.put(KEY_WEIGHT, features.getWeight());
    values.put(KEY_CONTRAST, features.getContrast());
    values.put(KEY_WIDTH, features.getWidth());
    values.put(KEY_FLATTENING, features.getFlattening());
    values.put(KEY_ARISE, features.getArise());
    values.put(KEY_SLANT, features.getSlant());
    getWritableDatabase().insert(TABLE_NAME, null, values);
  }

  /**
   * Returns the list of stored fonts from the database.
   *
   * <p>Returned list is sorted by created datetime in decreasing order.
   *
   * @return the list of stored fonts
   */
  public ArrayList<FontItem> loadItems() {
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<FontItem> results = new ArrayList<>();
    Cursor cursor = db.rawQuery(String.format(TABLE_SELECT, TABLE_NAME, KEY_ID), null);
    while (cursor.moveToNext()) {
      FontItem.FontItemBuilder builder = new FontItem.FontItemBuilder();

      Date datetime;
      try {
        datetime = DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(KEY_DATETIME)));
      } catch (ParseException e) {
        datetime = null;
      }

      builder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
      builder.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
      builder.setDatetime(datetime);
      builder.setCurve(cursor.getDouble(cursor.getColumnIndex(KEY_CURVE)));
      builder.setRoundness(cursor.getDouble(cursor.getColumnIndex(KEY_ROUNDNESS)));
      builder.setWeight(cursor.getDouble(cursor.getColumnIndex(KEY_WEIGHT)));
      builder.setContrast(cursor.getDouble(cursor.getColumnIndex(KEY_CONTRAST)));
      builder.setWidth(cursor.getDouble(cursor.getColumnIndex(KEY_WIDTH)));
      builder.setFlattening(cursor.getDouble(cursor.getColumnIndex(KEY_FLATTENING)));
      builder.setArise(cursor.getDouble(cursor.getColumnIndex(KEY_ARISE)));
      builder.setSlant(cursor.getDouble(cursor.getColumnIndex(KEY_SLANT)));

      results.add(builder.build());
    }
    return results;
  }

  /**
   * Delete the font data corresponding to the given id from the database.
   *
   * @param id id of the font data
   */
  public void deleteItem(int id) {
    getWritableDatabase().execSQL(String.format(ROW_DELETE, TABLE_NAME, KEY_ID, id));
  }
}
