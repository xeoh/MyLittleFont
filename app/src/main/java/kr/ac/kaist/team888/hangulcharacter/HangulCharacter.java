package kr.ac.kaist.team888.hangulcharacter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import kr.ac.kaist.team888.core.Region;
import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.util.Alert;
import kr.ac.kaist.team888.util.JsonLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract Class for each individual Hangul characters.
 */
public abstract class HangulCharacter {
  private static final String NO_DATA_ERROR = "No Json Data for character \'%s\'";
  public static final int X_RANGE_MIN = 0;
  public static final int X_RANGE_MAX = 940;
  public static final int Y_RANGE_MIN = -200;
  public static final int Y_RANGE_MAX = 800;

  private static final String OUTER_STROKES_KEY = "OuterStrokes";
  private static final String INNER_STROKES_KEY = "InnerStrokes";
  private ArrayList<ArrayList<Stroke>> outerStrokes;
  private ArrayList<ArrayList<Stroke>> innerStrokes;
  private Region region;
  protected JsonObject data;

  /**
   * Super class constructor.
   *
   * <p> Load {@link Stroke} data by class name. Class name should match to Json file's key.
   */
  public HangulCharacter() {
    String className = this.getClass().getSimpleName();
    data = JsonLoader.getInstance().getCharData(className);

    if (data == null) {
      Alert.log(this, String.format(NO_DATA_ERROR, className));
      return;
    }

    Gson gson = new Gson();
    Type collectionType = new TypeToken<Collection<Collection<Stroke>>>(){}.getType();

    outerStrokes = gson.fromJson(data.getAsJsonArray(OUTER_STROKES_KEY), collectionType);
    innerStrokes = gson.fromJson(data.getAsJsonArray(INNER_STROKES_KEY), collectionType);
    region = calculateRegion();
  }

  private Region calculateRegion() {
    float minX = X_RANGE_MAX;
    float maxX = X_RANGE_MIN;
    float minY = Y_RANGE_MAX;
    float maxY = Y_RANGE_MIN;

    ArrayList<ArrayList<Stroke>> strokes = new ArrayList<>();
    strokes.addAll(outerStrokes);
    strokes.addAll(innerStrokes);

    for (ArrayList<Stroke> closedPaths : strokes) {
      for (Stroke stroke : closedPaths) {
        minX = Math.min(minX, stroke.getMinX());
        maxX = Math.max(maxX, stroke.getMaxX());
        minY = Math.min(minY, stroke.getMinY());
        maxY = Math.max(maxY, stroke.getMaxY());
      }
    }

    // Case for an empty list
    if (minX > maxX || minY > maxY) {
      return new Region(HangulCharacter.X_RANGE_MIN, HangulCharacter.X_RANGE_MAX,
                        HangulCharacter.Y_RANGE_MIN, HangulCharacter.Y_RANGE_MAX);
    }

    return new Region(minX, maxX, minY, maxY);
  }

  /**
   * Get array of outer strokes.
   *
   * <p> Do not modify data of array. This returns reference of data.
   *
   * @return 2D Array list of outer strokes.
   */
  public ArrayList<ArrayList<Stroke>> getOuterStorkes() {
    return outerStrokes;
  }

  /**
   * Get array of inner strokes.
   *
   * <p> Do not modify data of array. This returns reference of data.
   *
   * @return 2D Array list of outer strokes.
   */
  public ArrayList<ArrayList<Stroke>> getInnerStrokes() {
    return innerStrokes;
  }

  /**
   * Returns the region of the character.
   *
   * @return the region of the character.
   */
  public Region getRegion() {
    return region;
  }
}
