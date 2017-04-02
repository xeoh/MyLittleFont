package kr.ac.kaist.team888.character;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.util.Alert;
import kr.ac.kaist.team888.util.JsonLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract Class for each individual characters.
 */
public abstract class Character {
  private static final String NO_DATA_ERROR = "No Json Data for character \'%s\'";
  public static final int X_RANGE_MIN = 0;
  public static final int X_RANGE_MAX = 940;
  public static final int Y_RANGE_MIN = -200;
  public static final int Y_RANGE_MAX = 800;
  protected static final String STROKE_KEY = "Strokes";
  protected ArrayList<Stroke> strokes;
  protected JsonObject data;

  /**
   * Super class constructor.
   *
   * <p> Load {@link Stroke} data by class name. Class name should match to Json file's key.
   */
  public Character() {
    String className = this.getClass().getSimpleName();
    data = JsonLoader.getInstance().getCharData(className);

    if (data == null) {
      Alert.log(this, String.format(NO_DATA_ERROR, className));
      return;
    }

    Gson gson = new Gson();
    Type collectionType = new TypeToken<Collection<Stroke>>(){}.getType();
    strokes = gson.fromJson(data.getAsJsonArray(STROKE_KEY), collectionType);
  }

  /**
   * Get deep copied array of stroke.
   *
   * <p> Returns deep copied object, since basic data should be consistent.
   *
   * @return Array list of stroke.
   */
  public ArrayList<Stroke> getStorkes() {
    ArrayList<Stroke> copiedStroke = new ArrayList<>(strokes);
    Collections.copy(copiedStroke, strokes);

    return copiedStroke;
  }
}
