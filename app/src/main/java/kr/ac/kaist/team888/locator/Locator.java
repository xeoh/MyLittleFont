package kr.ac.kaist.team888.locator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import android.graphics.Path;

import kr.ac.kaist.team888.core.Region;
import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.HangulDecomposer;
import kr.ac.kaist.team888.util.JsonLoader;
import kr.ac.kaist.team888.util.bezier.BezierCurve;
import kr.ac.kaist.team888.util.bezier.BezierCurveUtils;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class makes each character of a Hangul letter be placed on its appropriate location.
 *
 * <p>This class object provides a functionality to get outer and inner strokes of the letter.
 */
public class Locator implements FeatureController.OnFeatureChangeListener{
  private static final String TYPE_TOKEN = "type%d";
  public static final Region ORIGIN_REGION = HangulCharacter.ORIGIN_REGION;

  private static final int CURVE_MAX = 60;
  private static final int PRIORITY = 1;

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<Stroke>> skeletonsData;
  private ArrayList<ArrayList<Stroke>> skeletons;
  private ArrayList<ArrayList<Stroke>> contours;

  private ArrayList<Path> contourPaths;
  private ArrayList<Path> skeletonPaths;
  private ArrayList<Vector2D> fixedCircles;
  private ArrayList<Vector2D> controlCircles;

  /**
   * Constructs a locator object for given Hangul letter.
   *
   * <p>This method accepts a single Hangul letter and then composes and transforms each character
   * of the letter to its right region by getting base region data of it.
   *
   * <p>If an input letter is not Hangul, all locator functionality will not work properly.
   *
   * @param letter a Hangul letter.
   */
  public Locator(char letter) {
    characters = HangulDecomposer.decompose(letter);
    regions = calculateRegions();

    initialize();

    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  private void initialize() {
    skeletonsData = new ArrayList<>();
    skeletons = new ArrayList<>();

    for (int i = 0; i < characters.size(); i++) {
      HangulCharacter character = characters.get(i);
      Region baseRegion = character.getRegion();
      Region targetRegion = regions.get(i);

      for (ArrayList<Stroke> skeleton : character.getSkeletons()) {

        ArrayList<Stroke> newSkeletonData = new ArrayList<>();
        ArrayList<Stroke> newSkeleton = new ArrayList<>();
        for (Stroke stroke : skeleton) {
          Stroke transformedStroke = baseRegion.transformStroke(targetRegion, stroke);
          newSkeletonData.add(transformedStroke);
          newSkeleton.add(transformedStroke.copy());
        }
        skeletonsData.add(newSkeletonData);
        skeletons.add(newSkeleton);
      }
    }

    applyCurve();
    applyContour();
  }

  private ArrayList<Region> calculateRegions() {
    String type = String.format(TYPE_TOKEN, characters.size());
    String medialToken = characters.get(1).getClass().getSimpleName();
    JsonArray baseLocatorData = JsonLoader.getInstance().getLocatorData(type, medialToken);
    Gson gson = new Gson();
    Type collectionType = new TypeToken<Collection<Region>>(){}.getType();
    ArrayList<Region> regions = gson.fromJson(baseLocatorData, collectionType);

    return regions;
  }

  /**
   * Returns an array list of characters of the letter located by the locator.
   *
   * @return an array list of characters.
   */
  public ArrayList<HangulCharacter> getCharacters() {
    return characters;
  }

  /**
   * Returns an array list of skeleton of the letter located by the locator.
   *
   * @return an array list of skeleton.
   */
  public ArrayList<ArrayList<Stroke>> getSkeletons() {
    return skeletons;
  }

  /**
   * Recalculate Paths and Circles respect to canvas region.
   *
   * <p>contourPaths (from {@link Locator#getContourPaths()}),
   * skeletonPaths (from {@link Locator#getSkeletonPaths()}),
   * fixedCircles (from {@link Locator#getFixedCircles()}),
   * controlCircles (from {@link Locator#getControlCircles()})
   * are updated from this function call.
   *
   * @param canvasRegion Region of canvas
   */
  public void invalidate(Region canvasRegion) {
    contourPaths = new ArrayList<>();
    skeletonPaths = new ArrayList<>();
    fixedCircles = new ArrayList<>();
    controlCircles = new ArrayList<>();

    setPaths(canvasRegion, skeletons, skeletonPaths, true);
    setPaths(canvasRegion, contours, contourPaths, false);
  }

  /**
   * Get array list of contourPaths to draw on canvas.
   *
   * @return array list of contourPaths
   */
  public ArrayList<Path> getContourPaths() {
    return contourPaths;
  }

  /**
   * Get array list of skeletonPaths to draw on canvas.
   *
   * @return array list of skeletonPaths
   */
  public ArrayList<Path> getSkeletonPaths() {
    return skeletonPaths;
  }

  /**
   * Get array list of fixed points to draw on canvas.
   *
   * @return array list of fixed point
   */
  public ArrayList<Vector2D> getFixedCircles() {
    return fixedCircles;
  }

  /**
   * Get array list of control points to draw on canvas.
   *
   * @return array list of control point
   */
  public ArrayList<Vector2D> getControlCircles() {
    return controlCircles;
  }

  private void applyCurve() {
    for (int i = 0 ; i < skeletons.size(); i++) {
      ArrayList<Stroke> skeletonData = skeletonsData.get(i);
      ArrayList<Stroke> skeleton = skeletons.get(i);
      for (int j = 0; j < skeletons.get(i).size(); j++) {
        Stroke joint = skeletons.get(i).get(j);
        if (joint.isJoint()) {
          Stroke leftStroke = j != 0
              ? skeleton.get(j - 1) : skeleton.get(skeleton.size() - 1);
          Stroke leftStrokeData = j != 0
              ? skeletonData.get(j - 1) : skeletonData.get(skeletonData.size() - 1);
          Stroke rightStroke = j != skeleton.size() - 1
              ? skeleton.get(j + 1) : skeleton.get(0);
          Stroke rightStrokeData = j != skeletonData.size() - 1
              ? skeletonData.get(j + 1) : skeletonData.get(0);

          Vector2D leftVector = leftStrokeData.getStartPoint().subtract(leftStrokeData.getEndPoint());
          Vector2D rightVector = rightStrokeData.getEndPoint().subtract(rightStrokeData.getStartPoint());

          double scale = FeatureController.getInstance().getCurve() * CURVE_MAX;
          leftStroke.setEndPoint(leftStrokeData
              .getEndPoint().add(leftVector.scalarMultiply(scale / leftVector.getNorm())));
          joint.setStartPoint(leftStrokeData
              .getEndPoint().add(leftVector.scalarMultiply(scale / leftVector.getNorm())));

          rightStroke.setStartPoint(rightStrokeData
              .getStartPoint().add(rightVector.scalarMultiply(scale / rightVector.getNorm())));
          joint.setEndPoint(rightStrokeData
              .getStartPoint().add(rightVector.scalarMultiply(scale / rightVector.getNorm())));
        }
      }
    }
  }

  private void applyContour() {
    contours = new ArrayList<>();

    for (ArrayList<Stroke> strokes: skeletons) {
      // Convert Bezier curves to strokes
      ArrayList<BezierCurve> bezierCurves = new ArrayList<>();
      for (Stroke stroke: strokes) {
        ArrayList<Vector2D> points = new ArrayList<>();
        points.add(new Vector2D(1, stroke.getStartPoint()));
        ArrayList<Vector2D> controlPoints = stroke.getControlPoints();
        for (Vector2D point : controlPoints) {
          points.add(new Vector2D(1, point));
        }
        points.add(new Vector2D(1, stroke.getEndPoint()));

        BezierCurve bezierCurve = new BezierCurve(points.toArray(new Vector2D[points.size()]));

        if (bezierCurve.getOrder() <= 2
            && bezierCurve.getPoint(0).equals(bezierCurve.getPoint(bezierCurve.getOrder()))) {
          continue;
        }
        bezierCurves.add(bezierCurve);
      }

      // Generate contour of path
      ArrayList<BezierCurve> contourBezier = BezierCurveUtils.stroke(bezierCurves, 30);

      // Convert Bezier curves to strokes
      ArrayList<Stroke> contour = new ArrayList<>();
      for (BezierCurve bezierCurve : contourBezier) {
        Vector2D[] controlVectors = bezierCurve.getPoints();
        Stroke.StrokeBuilder builder = new Stroke.StrokeBuilder();
        builder.setStartPoint(new Vector2D((double) controlVectors[0].getX(),
            (double) controlVectors[0].getY()));
        builder.setEndPoint(new Vector2D((double) controlVectors[controlVectors.length - 1].getX(),
            (double) controlVectors[controlVectors.length - 1].getY()));
        for (int i = 1; i < controlVectors.length - 1; i++) {
          builder.addControlPoint(new Vector2D((double) controlVectors[i].getX(),
              (double) controlVectors[i].getY()));
        }
        contour.add(builder.build());
      }

      contours.add(contour);
    }
  }

  private void setPaths(Region canvasRegion, ArrayList<ArrayList<Stroke>> strokesSet,
                        ArrayList<Path> paths, boolean showPoints) {
    for (ArrayList<Stroke> strokes : strokesSet) {
      Path path = new Path();
      Stroke transStrokeInitial = ORIGIN_REGION.transformStroke(canvasRegion, strokes.get(0));
      path.moveTo((float) transStrokeInitial.getStartPoint().getX(),
          (float) transStrokeInitial.getStartPoint().getY());

      for (Stroke stroke : strokes) {
        Stroke transStroke = ORIGIN_REGION.transformStroke(canvasRegion, stroke);
        if (showPoints) {
          fixedCircles.add(transStroke.getStartPoint());
        }

        ArrayList<Vector2D> controlPoints = transStroke.getControlPoints();
        for (int i = 0; i < controlPoints.size(); i++) {
          if (i == controlPoints.size() - 1) {
            break;
          }

          float controlX = (float) controlPoints.get(i).getX();
          float controlY = (float) controlPoints.get(i).getY();

          float endX = (float) (controlPoints.get(i + 1).getX() + controlX) / 2;
          float endY = (float) (controlPoints.get(i + 1).getY() + controlY) / 2;

          path.quadTo(controlX, controlY, endX, endY);
          if (showPoints) {
            controlCircles.add(controlPoints.get(i));
          }
        }

        if (controlPoints.isEmpty()) {
          path.lineTo((float) transStroke.getEndPoint().getX(),
              (float) transStroke.getEndPoint().getY());
          if (showPoints) {
            fixedCircles.add(transStroke.getEndPoint());
          }
        } else {
          Vector2D lastControlPoint = controlPoints.get(controlPoints.size() - 1);
          path.quadTo((float) lastControlPoint.getX(), (float) lastControlPoint.getY(),
              (float) transStroke.getEndPoint().getX(), (float) transStroke.getEndPoint().getY());
          if (showPoints) {
            controlCircles.add(lastControlPoint);
            fixedCircles.add(transStroke.getEndPoint());
          }
        }
      }
      paths.add(path);
    }
  }

  @Override
  public void onFeatureChange() {
    applyCurve();
    applyContour();
  }

  @Override
  public int getPriority() {
    return PRIORITY;
  }
}