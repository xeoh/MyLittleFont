package kr.ac.kaist.team888.hangulcharacter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    // Parse points data from Json file
    Type collectionType = new TypeToken<Collection<Collection<Collection<Vector2D>>>>(){}.getType();
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(new TypeToken<Collection<Vector2D>>(){}.getType(),
        new JsonDeserializer<Collection<Vector2D>>() {
      @Override
      public Collection<Vector2D> deserialize(JsonElement json, Type typeOfT,
                                              JsonDeserializationContext context) {
        return new Gson().fromJson(json.getAsJsonObject().getAsJsonArray("points"),
            new TypeToken<Collection<Vector2D>>(){}.getType());
      }
    });
    ArrayList<ArrayList<ArrayList<Vector2D>>> skeletonsPoints = gson.create()
        .fromJson(data.getAsJsonArray(SKELETONS_KEY), collectionType);

    // Construct skeletons data including generating joints
    skeletonsData = new ArrayList<>();
    for (int i = 0; i < skeletonsPoints.size(); i++) {
      skeletonsData.add(i, new ArrayList<BezierCurve>());
      for (int j = 0; j < skeletonsPoints.get(i).size(); j++) {
        ArrayList<Vector2D> points = skeletonsPoints.get(i).get(j);
        // Valid points size
        if (points.size() <= 1) {
          continue;
        }
        // Append a single line or curve
        if (points.size() <= 3) {
          skeletonsData.get(i).add(new BezierCurve(points.toArray(new Vector2D[points.size()])));
        } else {
          // Append sequence of quadratic curves
          skeletonsData.get(i).add(new BezierCurve(new Vector2D[] {
              points.get(0), points.get(1), points.get(1).add(points.get(2)).scalarMultiply(.5)
          }));
          for (int k = 2; k < points.size() - 2; k++) {
            skeletonsData.get(i).add(new BezierCurve(new Vector2D[] {
                points.get(k).add(points.get(k - 1)).scalarMultiply(.5),
                points.get(k),
                points.get(k).add(points.get(k + 1)).scalarMultiply(.5)
            }));
          }
          skeletonsData.get(i).add(new BezierCurve(new Vector2D[] {
              points.get(points.size() - 2).add(points.get(points.size() - 3)).scalarMultiply(.5),
              points.get(points.size() - 2),
              points.get(points.size() - 1)
          }));
        }
        // Append joint
        Vector2D endPoint = points.get(points.size() - 1);
        Vector2D nextStartPoint = j == skeletonsPoints.get(i).size() - 1
            ? skeletonsPoints.get(i).get(0).get(0)
            : skeletonsPoints.get(i).get(j + 1).get(0);
        if (endPoint.equals(nextStartPoint)) {
          BezierCurve joint = new BezierCurve(new Vector2D[] {endPoint, endPoint, endPoint});
          joint.setJoint(true);
          skeletonsData.get(i).add(joint);
        }
      }
    }

    // Copy skeletons data
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
