package kr.ac.kaist.team888.locator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import android.graphics.Path;

import kr.ac.kaist.team888.bezier.BezierCurve;
import kr.ac.kaist.team888.bezier.BezierCurveUtils;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;
import kr.ac.kaist.team888.region.Region;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.HangulDecomposer;
import kr.ac.kaist.team888.util.JsonLoader;

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
  private static final int WEIGHT_DEFAULT = 32;
  private static final int PRIORITY = 1;

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<BezierCurve>> skeletonsData;
  private ArrayList<ArrayList<BezierCurve>> skeletons;
  private ArrayList<ArrayList<BezierCurve>> contours;

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

      for (ArrayList<BezierCurve> skeleton : character.getSkeletons()) {

        ArrayList<BezierCurve> newSkeletonData = new ArrayList<>();
        ArrayList<BezierCurve> newSkeleton = new ArrayList<>();
        for (BezierCurve curve : skeleton) {
          BezierCurve transformedCurve = baseRegion.transformBezierCurve(targetRegion, curve);
          newSkeletonData.add(transformedCurve);
          newSkeleton.add(transformedCurve.clone());
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
  public ArrayList<ArrayList<BezierCurve>> getSkeletons() {
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
      ArrayList<BezierCurve> skeletonData = skeletonsData.get(i);
      ArrayList<BezierCurve> skeleton = skeletons.get(i);
      for (int j = 0; j < skeletons.get(i).size(); j++) {
        BezierCurve joint = skeletons.get(i).get(j);
        if (joint.isJoint()) {
          BezierCurve leftStroke = j != 0
              ? skeleton.get(j - 1) : skeleton.get(skeleton.size() - 1);
          BezierCurve leftStrokeData = j != 0
              ? skeletonData.get(j - 1) : skeletonData.get(skeletonData.size() - 1);
          BezierCurve rightStroke = j != skeleton.size() - 1
              ? skeleton.get(j + 1) : skeleton.get(0);
          BezierCurve rightStrokeData = j != skeletonData.size() - 1
              ? skeletonData.get(j + 1) : skeletonData.get(0);

          Vector2D leftVector = leftStrokeData.getStartPoint()
              .subtract(leftStrokeData.getEndPoint());
          Vector2D rightVector = rightStrokeData.getEndPoint()
              .subtract(rightStrokeData.getStartPoint());

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
    for (ArrayList<BezierCurve> curves : skeletons) {
      ArrayList<BezierCurve> newCurves = new ArrayList<>();
      for (BezierCurve curve : curves) {
        if (curve.getOrder() <= 2
            && curve.getPoint(0).equals(curve.getPoint(curve.getOrder()))) {
          continue;
        }
        newCurves.add(curve.clone());
      }
      double delta = WEIGHT_DEFAULT
          + (WEIGHT_DEFAULT - 1) * (FeatureController.getInstance().getWeight() - .5);
      double roundness = FeatureController.getInstance().getRoundness();
      contours.add(BezierCurveUtils.stroke(newCurves, delta, roundness));
    }
  }

  private void setPaths(Region canvasRegion, ArrayList<ArrayList<BezierCurve>> curvesSet,
                        ArrayList<Path> paths, boolean showPoints) {
    for (ArrayList<BezierCurve> curves : curvesSet) {
      Path path = new Path();
      BezierCurve transInitial = ORIGIN_REGION.transformBezierCurve(canvasRegion, curves.get(0));
      path.moveTo((float) transInitial.getStartPoint().getX(),
          (float) transInitial.getStartPoint().getY());

      for (BezierCurve curve : curves) {
        BezierCurve transCurve = ORIGIN_REGION.transformBezierCurve(canvasRegion, curve);
        if (showPoints) {
          fixedCircles.add(transCurve.getStartPoint());
        }

        Vector2D[] controlPoints = transCurve.getControlPoints();
        for (int i = 0; i < controlPoints.length; i++) {
          if (i == controlPoints.length - 1) {
            break;
          }

          float controlX = (float) controlPoints[i].getX();
          float controlY = (float) controlPoints[i].getY();

          float endX = (float) (controlPoints[i + 1].getX() + controlX) / 2;
          float endY = (float) (controlPoints[i + 1].getY() + controlY) / 2;

          path.quadTo(controlX, controlY, endX, endY);
          if (showPoints) {
            controlCircles.add(controlPoints[i]);
          }
        }

        if (controlPoints.length == 0) {
          path.lineTo((float) transCurve.getEndPoint().getX(),
              (float) transCurve.getEndPoint().getY());
          if (showPoints) {
            fixedCircles.add(transCurve.getEndPoint());
          }
        } else {
          Vector2D lastControlPoint = controlPoints[controlPoints.length - 1];
          path.quadTo((float) lastControlPoint.getX(), (float) lastControlPoint.getY(),
              (float) transCurve.getEndPoint().getX(), (float) transCurve.getEndPoint().getY());
          if (showPoints) {
            controlCircles.add(lastControlPoint);
            fixedCircles.add(transCurve.getEndPoint());
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