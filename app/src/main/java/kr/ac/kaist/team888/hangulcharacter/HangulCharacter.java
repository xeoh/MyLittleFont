package kr.ac.kaist.team888.hangulcharacter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import kr.ac.kaist.team888.bezier.BezierCurve;
import kr.ac.kaist.team888.bezier.BezierCurveUtils;
import kr.ac.kaist.team888.region.Region;
import kr.ac.kaist.team888.util.Alert;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.JsonLoader;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

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
  private static final double X_OFFSET = 35;
  private static final double Y_OFFSET = 30;
  private static final int PRIORITY = 0;

  private static final String SKELETONS_KEY = "skeletons";
  private ArrayList<ArrayList<BezierCurve>> skeletonsData;
  private ArrayList<ArrayList<BezierCurve>> skeletons;
  private Region region;
  protected JsonObject data;

  /**
   * Super class constructor.
   *
   * <p> Load {@link BezierCurve} data by class name. Class name should match to Json file's key.
   */
  protected HangulCharacter() {
    String className = this.getClass().getSimpleName();
    data = JsonLoader.getInstance().getCharData(className);

    if (data == null) {
      Alert.log(this, String.format(NO_DATA_ERROR, className));
      return;
    }

    Type collectionType = new TypeToken<Collection<Collection<BezierCurve>>>(){}.getType();
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(BezierCurve.class, new JsonDeserializer<BezierCurve>() {
      @Override
      public BezierCurve deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonPoints = json.getAsJsonObject().getAsJsonArray("points");
        Gson gson = new Gson();
        Vector2D[] points = new Vector2D[jsonPoints.size()];
        for (int i = 0; i < points.length; i++) {
          points[i] = gson.fromJson(jsonPoints.get(i), Vector2D.class);
        }
        return new BezierCurve(points);
      }
    });

    skeletonsData = gsonBuilder.create()
        .fromJson(data.getAsJsonArray(SKELETONS_KEY), collectionType);
    addJointStroke();
    skeletons = new ArrayList<>();
    for (int i = 0; i < skeletonsData.size(); i++) {
      skeletons.add(i, new ArrayList<BezierCurve>());
      for (int j = 0; j < skeletonsData.get(i).size(); j++) {
        skeletons.get(i).add(skeletonsData.get(i).get(j).clone());
      }
    }
    region = calculateRegion();

    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  private void addJointStroke() {
    for (ArrayList<BezierCurve> skeleton : skeletonsData) {
      HashMap<BezierCurve, BezierCurve> joints = new HashMap<>();
      for (int i = 0; i < skeleton.size(); i++) {
        BezierCurve nextCurve = (i == skeleton.size() - 1) ? skeleton.get(0) : skeleton.get(i + 1);

        if (skeleton.get(i).getEndPoint().equals(nextCurve.getStartPoint())) {
          BezierCurve jointCurve = new BezierCurve(new Vector2D[] {
              skeleton.get(i).getEndPoint(),
              skeleton.get(i).getEndPoint(),
              skeleton.get(i).getEndPoint()
          });
          jointCurve.setJoint(true);
          joints.put(skeleton.get(i), jointCurve);
        }
      }

      for (BezierCurve pos : joints.keySet()) {
        skeleton.add(skeleton.indexOf(pos) + 1, joints.get(pos));
      }
    }
  }

  private Region calculateRegion() {
    double minX = ORIGIN_REGION.getMaxX();
    double maxX = ORIGIN_REGION.getMinX();
    double minY = ORIGIN_REGION.getMaxY();
    double maxY = ORIGIN_REGION.getMinY();

    for (ArrayList<BezierCurve> skeleton : skeletonsData) {
      for (BezierCurve bc : skeleton) {
        minX = Math.min(minX, BezierCurveUtils.getMinX(bc));
        maxX = Math.max(maxX, BezierCurveUtils.getMaxX(bc));
        minY = Math.min(minY, BezierCurveUtils.getMinY(bc));
        maxY = Math.max(maxY, BezierCurveUtils.getMaxY(bc));
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
   * @return skeletons which is composed of 2D {@link kr.ac.kaist.team888.bezier.BezierCurve} array.
   */
  public ArrayList<ArrayList<BezierCurve>> getSkeletons() {
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
