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

  private static final int CURVE_MAX = 70;
  private static final int CURVE_GAP = 2;
  private static final double CURVE_TOLERANCE = 1E-4;
  private static final int WEIGHT_DEFAULT = 32;
  private static final int PRIORITY = 1;

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<ArrayList<BezierCurve>>> skeletonsData;
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
    calculateRegions();

    initialize();

    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  private void initialize() {
    skeletonsData = new ArrayList<>();

    for (int i = 0; i < characters.size(); i++) {
      HangulCharacter character = characters.get(i);
      Region baseRegion = character.getRegion();
      Region targetRegion = regions.get(i);

      for (ArrayList<ArrayList<BezierCurve>> skeleton : character.getSkeletons()) {
        ArrayList<ArrayList<BezierCurve>> newSkeletonData = new ArrayList<>();
        for (ArrayList<BezierCurve> segment : skeleton) {
          ArrayList<BezierCurve> newSegment = new ArrayList<>();
          for (BezierCurve curve : segment) {
            BezierCurve transformedCurve = baseRegion.transformBezierCurve(targetRegion, curve);
            newSegment.add(transformedCurve);
          }
          newSkeletonData.add(newSegment);
        }
        skeletonsData.add(newSkeletonData);
      }
    }

    applyCurve();
    applyContour();
  }

  private void calculateRegions() {
    if (characters.size() > 1) {
      String type = String.format(TYPE_TOKEN, characters.size());
      String medialToken = characters.get(1).getClass().getSimpleName();
      JsonArray baseLocatorData = JsonLoader.getInstance().getLocatorData(type, medialToken);
      Gson gson = new Gson();
      Type collectionType = new TypeToken<Collection<Region>>(){}.getType();
      regions = gson.fromJson(baseLocatorData, collectionType);
    } else if (characters.size() == 1) {
      regions = new ArrayList<>();
      regions.add(characters.get(0).getRegion());
    } else {
      regions = new ArrayList<>();
    }
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

  private double getTimeByLength(BezierCurve[] curves, double offset, boolean order) {
    double min = 0;
    double max = curves.length;
    double time;
    for (;;) {
      double length = order
          ? BezierCurveUtils.getLength(curves, 0, (min + max) / 2)
          : BezierCurveUtils.getLength(curves, (min + max) / 2, curves.length);
      if (Math.abs(length - offset) < CURVE_TOLERANCE) {
        time = (min + max) / 2;
        break;
      }
      if (order ^ (length < offset)) {
        max = (min + max) / 2;
      } else {
        min = (min + max) / 2;
      }
    }
    return time;
  }

  private BezierCurve adjustJoint(BezierCurve joint, BezierCurve leftCurve,
                                  BezierCurve rightCurve) {
    Vector2D startVector = joint.getStartPoint()
        .subtract(leftCurve.getPoint(leftCurve.getOrder() - 1));
    Vector2D endVector = rightCurve.getPoint(1).subtract(joint.getEndPoint());
    double cross = startVector.getX() * endVector.getY() - startVector.getY() * endVector.getX();
    if (Math.abs(cross) < CURVE_TOLERANCE) {
      if (Math.abs(joint.getEndPoint().subtract(joint.getStartPoint()).normalize()
          .dotProduct(endVector.normalize()) - 1) < CURVE_TOLERANCE) {
        return new BezierCurve(new Vector2D[] {
            joint.getStartPoint(),
            joint.getEndPoint()
        });
      }
      double offset = FeatureController.getInstance().getCurve() * CURVE_MAX;
      return new BezierCurve(new Vector2D[] {
          joint.getStartPoint(),
          joint.getStartPoint().add(offset, startVector.normalize()),
          joint.getEndPoint().add(offset, startVector.normalize()),
          joint.getEndPoint()
      });
    }
    double factor = (-joint.getStartPoint().getX() * endVector.getY()
        + joint.getStartPoint().getY() * endVector.getX()
        + joint.getEndPoint().getX() * endVector.getY()
        - joint.getEndPoint().getY() * endVector.getX()) / cross;
    return new BezierCurve(new Vector2D[] {
        joint.getStartPoint(),
        joint.getStartPoint().add(factor, startVector),
        joint.getEndPoint()
    });
  }

  private void applyCurve() {
    double offset = FeatureController.getInstance().getCurve() * CURVE_MAX;
    skeletons = new ArrayList<>();
    // Omit creating joints and appending curve if offset is just 0
    if (offset == 0) {
      for (ArrayList<ArrayList<BezierCurve>> segments : skeletonsData) {
        ArrayList<BezierCurve> skeleton = new ArrayList<BezierCurve>();
        for (ArrayList<BezierCurve> segment : segments) {
          for (BezierCurve curve : segment) {
            skeleton.add(curve.clone());
          }
        }
        skeletons.add(skeleton);
      }
      return;
    }

    for (ArrayList<ArrayList<BezierCurve>> segments : skeletonsData) {
      ArrayList<BezierCurve> skeleton = new ArrayList<>();
      for (int i = 0; i < segments.size(); i++) {
        // Clone the segment data
        ArrayList<BezierCurve> segmentData = segments.get(i);
        ArrayList<BezierCurve> segment = new ArrayList<>();
        for (BezierCurve curve : segmentData) {
          segment.add(curve.clone());
        }

        // Fetch joints information
        ArrayList<BezierCurve> leftSegment = segments.get(i == 0 ? segments.size() - 1 : i - 1);
        ArrayList<BezierCurve> rightSegment = segments.get(i == segments.size() - 1 ? 0 : i + 1);
        boolean jointLeft = segments.size() > 1 && segment.get(0).getStartPoint()
            .equals(leftSegment.get(leftSegment.size() - 1).getEndPoint());
        boolean jointRight = segments.size() > 1 && segment.get(segment.size() - 1).getEndPoint()
            .equals(rightSegment.get(0).getStartPoint());

        // Add curves of segment if both joints are not needed
        if (!jointLeft && !jointRight) {
          for (BezierCurve curve : segment) {
            skeleton.add(curve);
          }
          continue;
        }

        // Get length of the current segment
        BezierCurve[] segmentArr = segmentData.toArray(new BezierCurve[segmentData.size()]);
        double length = BezierCurveUtils.getLength(segmentArr, 0, segmentArr.length);
        double targetOffset = Math.min(offset,
            (length - CURVE_GAP) / ((jointLeft && jointRight) ? 2 : 1));
        // Set a left joint of the current segment
        if (jointLeft) {
          double time = getTimeByLength(segmentArr, targetOffset, true);
          int index = (int) Math.min(segmentArr.length - 1, Math.floor(time));
          for (int j = 0; j < index; j++) {
            segment.remove(0);
          }
          segment.set(0, segment.get(0).split(time - index)[1]);

          // Update left joint made by the left segment
          if (i > 0) {
            BezierCurve leftCurve = skeleton.get(skeleton.size() - 2);
            BezierCurve joint = skeleton.get(skeleton.size() - 1);
            BezierCurve rightCurve = segment.get(0);
            joint.setEndPoint(rightCurve.getStartPoint());
            skeleton.set(skeleton.size() - 1, adjustJoint(joint, leftCurve, rightCurve));
          }
        }

        // Set a right joint of the current segment
        if (jointRight) {
          segmentArr = segment.toArray(new BezierCurve[segment.size()]);
          double time = getTimeByLength(segmentArr, targetOffset, false);
          int index = (int) Math.min(segmentArr.length - 1, Math.floor(time));
          for (int j = 0; j < index; j++) {
            segment.remove(segment.size() - 1);
          }
          segment.set(segment.size() - 1, segment.get(segment.size() - 1).split(time - index)[0]);
        }

        // Add curves to the skeleton
        for (BezierCurve curve : segment) {
          skeleton.add(curve);
        }

        // Make a right joint (whose end point will be updated in the next iteration)
        if (jointRight) {
          BezierCurve joint = new BezierCurve(new Vector2D[] {
              segment.get(segment.size() - 1).getEndPoint(),
              segmentData.get(segmentData.size() - 1).getEndPoint(),
              i == segments.size() - 1 ? skeleton.get(0).getStartPoint() : Vector2D.NaN
          });
          if (joint.getEndPoint().equals(Vector2D.NaN)) {
            skeleton.add(joint);
          } else {
            skeleton.add(adjustJoint(joint, skeleton.get(skeleton.size() - 1), skeleton.get(0)));
          }
        }
      }
      skeletons.add(skeleton);
    }
  }

  private void applyContour() {
    contours = new ArrayList<>();
    for (ArrayList<BezierCurve> curves : skeletons) {
      ArrayList<BezierCurve> newCurves = new ArrayList<>();
      for (BezierCurve curve : curves) {
        if (curve.isCollapsed()) {
          continue;
        }
        newCurves.add(curve);
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
        if (curve.isCollapsed()) {
          continue;
        }

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