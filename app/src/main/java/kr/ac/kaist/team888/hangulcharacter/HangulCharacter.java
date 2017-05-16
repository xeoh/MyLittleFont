package kr.ac.kaist.team888.hangulcharacter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import kr.ac.kaist.team888.core.Region;
import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.util.Alert;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.JsonLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract Class for each individual Hangul characters.
 */
public abstract class HangulCharacter implements FeatureController.OnFeatureChangeListener {
  private static final String NO_DATA_ERROR = "No Json Data for character \'%s\'";
  public static final Region ORIGIN_REGION = new Region(0, 940, -200, 800);
  private static final float X_OFFSET = 35f;
  private static final float Y_OFFSET = 30f;
  private static final int PRIORITY = 0;

  private static final String SKELETONS_KEY = "skeletons";
  private ArrayList<ArrayList<Stroke>> skeletonsData;
  private ArrayList<ArrayList<Stroke>> skeletons;
  private Region region;
  protected JsonObject data;

  /**
   * Super class constructor.
   *
   * <p> Load {@link Stroke} data by class name. Class name should match to Json file's key.
   */
  protected HangulCharacter() {
    String className = this.getClass().getSimpleName();
    data = JsonLoader.getInstance().getCharData(className);

    if (data == null) {
      Alert.log(this, String.format(NO_DATA_ERROR, className));
      return;
    }

    Gson gson = new Gson();
    Type collectionType = new TypeToken<Collection<Collection<Stroke>>>(){}.getType();

    skeletonsData = gson.fromJson(data.getAsJsonArray(SKELETONS_KEY), collectionType);
    addJointStroke();
    skeletons = new ArrayList<>();
    for (int i = 0; i < skeletonsData.size(); i++) {
      skeletons.add(i, new ArrayList<Stroke>());
      for (int j = 0; j < skeletonsData.get(i).size(); j++) {
        skeletons.get(i).add(skeletonsData.get(i).get(j).copy());
      }
    }
    region = calculateRegion();

    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  private void addJointStroke() {
    for (ArrayList<Stroke> skeleton : skeletonsData) {
      HashMap<Stroke, Stroke> joints = new HashMap<>();
      for (int i = 0; i < skeleton.size(); i++) {
        Stroke nextStroke = (i == skeleton.size() - 1) ? skeleton.get(0) : skeleton.get(i + 1);

        if (skeleton.get(i).getEndPoint().equals(nextStroke.getStartPoint())) {
          joints.put(skeleton.get(i), new Stroke.StrokeBuilder()
              .setStartPoint(skeleton.get(i).getEndPoint())
              .setEndPoint(skeleton.get(i).getEndPoint())
              .addControlPoint(skeleton.get(i).getEndPoint())
              .setJoint(true)
              .build());
        }
      }

      for (Stroke pos : joints.keySet()) {
        skeleton.add(skeleton.indexOf(pos) + 1, joints.get(pos));
      }
    }
  }

  private Region calculateRegion() {
    float minX = ORIGIN_REGION.getMaxX();
    float maxX = ORIGIN_REGION.getMinX();
    float minY = ORIGIN_REGION.getMaxY();
    float maxY = ORIGIN_REGION.getMinY();

    for (ArrayList<Stroke> skeleton : skeletonsData) {
      for (Stroke stroke : skeleton) {
        minX = Math.min(minX, stroke.getMinX());
        maxX = Math.max(maxX, stroke.getMaxX());
        minY = Math.min(minY, stroke.getMinY());
        maxY = Math.max(maxY, stroke.getMaxY());
      }
    }

    // Case for an empty list
    if (minX > maxX || minY > maxY) {
      return new Region(ORIGIN_REGION.getMinX(), ORIGIN_REGION.getMaxX(),
          ORIGIN_REGION.getMinY(), ORIGIN_REGION.getMaxY());
    }

    return new Region(minX - X_OFFSET, maxX + X_OFFSET, minY - Y_OFFSET, maxY + Y_OFFSET);
  }

  /**
   * Get array of skeletons.
   *
   * @return skeletons which is composed of 2D {@link kr.ac.kaist.team888.core.Stroke} array.
   */
  public ArrayList<ArrayList<Stroke>> getSkeletons() {
    return skeletons;
  }

  /**
   * Returns the region of the character.
   *
   * @return the region of the character.
   */
  public Region getRegion() {
    return region;
  }

  @Override
  public void onFeatureChange() { }

  @Override
  public int getPriority() {
    return PRIORITY;
  }
}
