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

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
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
  private static final int CURVE_MAX = 70;
  private static final int CURVE_GAP = 2;
  private static final double CURVE_TOLERANCE = 1E-4;
  private static final int WEIGHT_DEFAULT = 32;
  private static final double CONTRAST_MIN = .6;
  private static final double WIDTH_MIN = 0.7;
  private static final double WIDTH_MAX = 1.3;
  private static final double SLANT_MAX = 0.3;
  private static final int PRIORITY = 1;

  private static final int NEWTON_MAX_EVAL = 100000;

  public static Region globalLocatorRegion = new Region(HangulCharacter.ORIGIN_REGION.getMinX(),
      HangulCharacter.ORIGIN_REGION.getMaxX(),
      HangulCharacter.ORIGIN_REGION.getMinY(),
      HangulCharacter.ORIGIN_REGION.getMaxY());

  public Region locatorRegion = new Region(HangulCharacter.ORIGIN_REGION.getMinX(),
      HangulCharacter.ORIGIN_REGION.getMaxX(),
      HangulCharacter.ORIGIN_REGION.getMinY(),
      HangulCharacter.ORIGIN_REGION.getMaxY());

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<ArrayList<BezierCurve>>> ariseData;
  private ArrayList<ArrayList<ArrayList<BezierCurve>>> flattenData;
  private ArrayList<ArrayList<ArrayList<BezierCurve>>> skeletonsData;
  private ArrayList<ArrayList<ArrayList<BezierCurve>>> processedData;
  private ArrayList<ArrayList<BezierCurve>> skeletons;
  private ArrayList<ArrayList<BezierCurve>> contours;

  private boolean isArisable = false;
  private boolean isFlatable = false;

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
    onFeatureChange();
  }

  /**
   * Constructs a locator object for given Hangul letter.
   *
   * <p>This method accepts a single Hangul letter and then composes and transforms each character
   * of the letter to its right region by getting base region data of it.
   *
   * <p>If an input letter is not Hangul, all locator functionality will not work properly.
   *
   * @param letter a Hangul letter.
   * @param registerFeatureChangeListener whether register this to {@link FeatureController}
   */
  public Locator(char letter, boolean registerFeatureChangeListener) {
    characters = HangulDecomposer.decompose(letter);
    calculateRegions();

    initialize();

    if (registerFeatureChangeListener) {
      FeatureController.getInstance().registerOnFeatureChangeListener(this);
      onFeatureChange();
    }
  }

  private void initialize() {
    skeletonsData = new ArrayList<>();

    for (int i = 0; i < characters.size(); i++) {
      HangulCharacter character = characters.get(i);
      Region baseRegion = character.getRegion(i, characters);
      Region targetRegion = regions.get(i);

      if (character.isFlatable(i, characters)) {
        isFlatable = true;
      }

      if (character.isArisable()) {
        isArisable = true;
      }

      for (ArrayList<ArrayList<BezierCurve>> skeleton : character.getSkeletons(i, characters)) {
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

    if (isFlatable) {
      flattenData = new ArrayList<>();

      for (int i = 0; i < characters.size(); i++) {
        HangulCharacter character = characters.get(i);
        Region baseRegion = character.getRegion(-1, characters);
        Region targetRegion = regions.get(i);

        for (ArrayList<ArrayList<BezierCurve>> skeleton : character.getSkeletons(-1, characters)) {
          ArrayList<ArrayList<BezierCurve>> newSkeletonData = new ArrayList<>();
          for (ArrayList<BezierCurve> segment : skeleton) {
            ArrayList<BezierCurve> newSegment = new ArrayList<>();
            for (BezierCurve curve : segment) {
              BezierCurve transformedCurve = baseRegion.transformBezierCurve(targetRegion, curve);
              newSegment.add(transformedCurve);
            }
            newSkeletonData.add(newSegment);
          }
          flattenData.add(newSkeletonData);
        }
      }
    }

    if (isArisable) {
      ariseData = new ArrayList<>();

      for (int i = 0; i < characters.size(); i++) {
        HangulCharacter character = characters.get(i);
        Region baseRegion = character.getRegion(-1, characters);
        Region targetRegion = regions.get(i);

        for (ArrayList<ArrayList<BezierCurve>> skeleton : character.getSkeletons(-1, characters)) {
          ArrayList<ArrayList<BezierCurve>> newSkeletonData = new ArrayList<>();
          for (ArrayList<BezierCurve> segment : skeleton) {
            ArrayList<BezierCurve> newSegment = new ArrayList<>();
            for (BezierCurve curve : segment) {
              BezierCurve transformedCurve = baseRegion.transformBezierCurve(targetRegion, curve);
              newSegment.add(transformedCurve);
            }
            newSkeletonData.add(newSegment);
          }
          ariseData.add(newSkeletonData);
        }
      }
    }
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
      regions.add(characters.get(0).getRegion(0, characters));
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

  private void adjustEndExtension(int segmentsIndex, int index) {
    ArrayList<ArrayList<BezierCurve>> segments = processedData.get(segmentsIndex);
    ArrayList<BezierCurve> segment = segments.get(index);
    BezierCurve startCurve = segment.get(0);
    BezierCurve endCurve = segment.get(segment.size() - 1);
    if (startCurve.getCutoffStart() != 0) {
      int targetSegments = segmentsIndex;
      int target = index + startCurve.getCutoffStart();
      while (target < 0 || target >= processedData.get(targetSegments).size()) {
        if (target < 0) {
          targetSegments--;
          target = processedData.get(targetSegments).size() - 1;
        } else {
          target = target - processedData.get(targetSegments).size();
          targetSegments++;
        }
      }
      Vector2D adjustPoint = adjustEndExtension(startCurve,
          processedData.get(targetSegments).get(target), true);
      segment.add(0, new BezierCurve.Builder()
          .setPoints(new Vector2D[] {adjustPoint, segment.get(0).getStartPoint()})
          .setOffsetVector(segment.get(0).getOffsetVector())
          .setCutoffStartVector(startCurve.getCutoffStartVector())
          .build());
      startCurve.setCutoffStartVector(null);
    }
    if (endCurve.getCutoffEnd() != 0) {
      int targetSegments = segmentsIndex;
      int target = index + endCurve.getCutoffEnd();
      while (target < 0 || target >= processedData.get(targetSegments).size()) {
        if (target < 0) {
          targetSegments--;
          target = processedData.get(targetSegments).size() - 1;
        } else {
          target = target - processedData.get(targetSegments).size();
          targetSegments++;
        }
      }
      Vector2D adjustPoint = adjustEndExtension(segment.get(segment.size() - 1),
          processedData.get(targetSegments).get(target), false);
      Vector2D offsetVector = endCurve.getEndOffsetVector();
      if (offsetVector == null) {
        offsetVector = endCurve.getOffsetVector();
      }
      segment.add(new BezierCurve.Builder()
          .setPoints(new Vector2D[] {endCurve.getEndPoint(), adjustPoint})
          .setOffsetVector(offsetVector)
          .setCutoffEndVector(endCurve.getCutoffEndVector())
          .build());
      endCurve.setCutoffEndVector(null);
    }
  }

  private Vector2D adjustEndExtension(final BezierCurve curve, final ArrayList<BezierCurve> segment,
                                      boolean order) {
    // Fetch points
    final Vector2D startPoint;
    final Vector2D endPoint;
    if (order) {
      startPoint = curve.getPoint(1);
      endPoint = curve.getStartPoint();
    } else {
      startPoint = curve.getPoint(curve.getOrder() - 1);
      endPoint = curve.getEndPoint();
    }

    // Generate function
    UnivariateFunction function = new UnivariateFunction() {
      @Override
      public double value(double time) {
        int targetIndex = Math.max(0, Math.min(segment.size() - 1, (int) time));
        BezierCurve targetCurve = segment.get(targetIndex);
        double[] values = targetCurve.value(time - targetIndex);
        if (startPoint.getX() == endPoint.getX()) {
          return values[0] - startPoint.getX();
        }
        double c1 = (startPoint.getY() - endPoint.getY()) / (startPoint.getX() - endPoint.getX());
        double c0 = startPoint.getY() - c1 * startPoint.getX();
        return c1 * values[0] + c0 - values[1];
      }
    };

    // Find root
    double time = new BisectionSolver().solve(NEWTON_MAX_EVAL, function, 0, segment.size());

    // Fetch target curve
    int targetIndex = Math.max(0, Math.min(segment.size() - 1, (int) time));
    BezierCurve targetCurve = segment.get(targetIndex);
    double targetTime = time - targetIndex;
    Vector2D cutoffVector = new Vector2D(targetCurve.perpendicular().value(targetTime));
    if (cutoffVector.dotProduct(endPoint.subtract(startPoint)) < 0) {
      cutoffVector = cutoffVector.scalarMultiply(-1);
    }

    // Save result
    if (order) {
      curve.setCutoffStartVector(cutoffVector);
    } else {
      curve.setCutoffEndVector(cutoffVector);
    }
    return new Vector2D(targetCurve.value(targetTime));
  }

  private BezierCurve adjustJoint(BezierCurve joint, BezierCurve leftCurve,
                                  BezierCurve rightCurve, double offset) {
    Vector2D startVector = joint.getStartPoint()
        .subtract(leftCurve.getPoint(leftCurve.getOrder() - 1));
    Vector2D endVector = rightCurve.getPoint(1).subtract(joint.getEndPoint());
    double cross = startVector.getX() * endVector.getY() - startVector.getY() * endVector.getX();
    if (Math.abs(cross) < CURVE_TOLERANCE) {
      if (Math.abs(joint.getEndPoint().subtract(joint.getStartPoint()).normalize()
          .dotProduct(endVector.normalize()) - 1) < CURVE_TOLERANCE) {
        return new BezierCurve.Builder()
            .setPoints(new Vector2D[] {joint.getStartPoint(), joint.getEndPoint()})
            .setOffsetVector(joint.getOffsetVector())
            .setEndOffsetVector(joint.getEndOffsetVector())
            .build();
      }
      return new BezierCurve.Builder()
          .setPoints(new Vector2D[] {
              joint.getStartPoint(),
              joint.getStartPoint().add(offset, startVector.normalize()),
              joint.getEndPoint().add(offset, startVector.normalize()),
              joint.getEndPoint()})
          .setOffsetVector(joint.getOffsetVector())
          .setEndOffsetVector(joint.getEndOffsetVector())
          .build();
    }
    double factor = (-joint.getStartPoint().getX() * endVector.getY()
        + joint.getStartPoint().getY() * endVector.getX()
        + joint.getEndPoint().getX() * endVector.getY()
        - joint.getEndPoint().getY() * endVector.getX()) / cross;
    return new BezierCurve.Builder()
        .setPoints(new Vector2D[] {
            joint.getStartPoint(),
            joint.getStartPoint().add(factor, startVector),
            joint.getEndPoint()})
        .setOffsetVector(joint.getOffsetVector())
        .setEndOffsetVector(joint.getEndOffsetVector())
        .build();
  }

  /**
   * Applies curve by given curve control value.
   *
   * @param curveControl curve control value from 0 to 1
   */
  public void applyCurve(double curveControl) {
    double offset = curveControl * CURVE_MAX;
    skeletons = new ArrayList<>();
    // Omit creating joints and appending curve if offset is just 0
    if (offset == 0) {
      for (int i = 0; i < processedData.size(); i++) {
        ArrayList<ArrayList<BezierCurve>> segments = processedData.get(i);
        ArrayList<BezierCurve> skeleton = new ArrayList<BezierCurve>();
        for (int j = 0; j < segments.size(); j++) {
          adjustEndExtension(i, j);
          for (BezierCurve curve : segments.get(j)) {
            skeleton.add(curve.clone());
          }
        }
        skeletons.add(skeleton);
      }
      return;
    }

    for (int segmentsIndex = 0; segmentsIndex < processedData.size(); segmentsIndex++) {
      ArrayList<ArrayList<BezierCurve>> segments = processedData.get(segmentsIndex);
      ArrayList<BezierCurve> skeleton = new ArrayList<>();
      for (int i = 0; i < segments.size(); i++) {
        // Clone the segment data
        ArrayList<BezierCurve> segmentData = segments.get(i);
        ArrayList<BezierCurve> segment = new ArrayList<>();
        for (BezierCurve curve : segmentData) {
          segment.add(curve.clone());
        }

        adjustEndExtension(segmentsIndex, i);

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
            joint.setEndOffsetVector(rightCurve.getOffsetVector());
            skeleton.set(skeleton.size() - 1, adjustJoint(joint, leftCurve, rightCurve, offset));
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
          joint.setOffsetVector(segment.get(segment.size() - 1).getOffsetVector());
          if (joint.getEndPoint().equals(Vector2D.NaN)) {
            skeleton.add(joint);
          } else {
            joint = adjustJoint(joint, skeleton.get(skeleton.size() - 1), skeleton.get(0), offset);
            joint.setEndOffsetVector(skeleton.get(0).getOffsetVector());
            skeleton.add(joint);
          }
        }
      }
      skeletons.add(skeleton);
    }
  }

  /**
   * Applies contour by given weight and roundness control values.
   *
   * @param weightControl weight control value from 0 to 1
   * @param roundnessControl roundness control value from 0 to 1
   * @param contrastControl contrast control value from 0 to 1
   */
  public void applyContour(double weightControl, double roundnessControl, double contrastControl) {
    double weight = weightControl - .5;
    double roundness = roundnessControl;
    double contrast = contrastControl * (2 - 2 * CONTRAST_MIN) + CONTRAST_MIN;
    contours = new ArrayList<>();
    for (ArrayList<BezierCurve> curves : skeletons) {
      ArrayList<BezierCurve> newCurves = new ArrayList<>();
      for (BezierCurve curve : curves) {
        if (curve.isCollapsed()) {
          continue;
        }
        newCurves.add(curve);
      }
      double delta = WEIGHT_DEFAULT + (WEIGHT_DEFAULT - 1) * weight;
      contours.add(BezierCurveUtils.stroke(newCurves, delta, roundness, contrast));
    }
  }

  /**
   * Applies width by given width control value.
   *
   * @param widthControl width control value from 0 to 1
   */
  public void applyWidth(double widthControl) {
    double width = (WIDTH_MAX - WIDTH_MIN) * widthControl + WIDTH_MIN;
    double totalWidth = (HangulCharacter.ORIGIN_REGION.getMaxX()
        - HangulCharacter.ORIGIN_REGION.getMinX()) * width;
    double widthCenter = HangulCharacter.ORIGIN_REGION.getMaxX()
        - HangulCharacter.ORIGIN_REGION.getMinX() / 2;
    locatorRegion.setMinX(widthCenter - totalWidth / 2);
    locatorRegion.setMaxX(widthCenter + totalWidth / 2);

    for (ArrayList<BezierCurve> skeleton : skeletons) {
      for (int i = 0; i < skeleton.size(); i++) {
        skeleton.set(i,
            HangulCharacter.ORIGIN_REGION.transformBezierCurve(locatorRegion, skeleton.get(i)));
      }
    }
  }

  /**
   * Applies width by given width control value.
   *
   * @param widthControl width control value from 0 to 1
   * @param global global change or not
   */
  public void applyWidth(double widthControl, boolean global) {
    if (global) {
      double width = (WIDTH_MAX - WIDTH_MIN) * widthControl + WIDTH_MIN;
      double totalWidth = (HangulCharacter.ORIGIN_REGION.getMaxX()
          - HangulCharacter.ORIGIN_REGION.getMinX()) * width;
      double widthCenter = HangulCharacter.ORIGIN_REGION.getMaxX()
          - HangulCharacter.ORIGIN_REGION.getMinX() / 2;
      globalLocatorRegion.setMinX(widthCenter - totalWidth / 2);
      globalLocatorRegion.setMaxX(widthCenter + totalWidth / 2);
    }
    applyWidth(widthControl);
  }

  /**
   * Manipulate the skeleton with flattening data and arise data
   * with given flattening control value and arise control value .
   *
   * @param flatteningControl flattening control value from 0 to 1
   * @param ariseControl arise control value from 0 to 1
   */
  public void manipulateSkeleton(double flatteningControl, double ariseControl) {
    if (!isFlatable && !isArisable) {
      processedData = skeletonsData;
      return;
    }

    processedData = new ArrayList<>();

    for (int i = 0; i < skeletonsData.size(); i++) {
      ArrayList<ArrayList<BezierCurve>> segments = skeletonsData.get(i);
      ArrayList<ArrayList<BezierCurve>> processedSegments = new ArrayList<>();

      for (int j = 0; j < segments.size(); j++) {
        ArrayList<BezierCurve> segment = segments.get(j);
        ArrayList<BezierCurve> processedSegment = new ArrayList<>();

        for (int k = 0; k < segment.size(); k++) {
          BezierCurve fundamentalCurve = segment.get(k);

          if (isFlatable) {
            BezierCurve flattenCurve = flattenData.get(i).get(j).get(k);
            if (BezierCurveUtils.comparePoints(fundamentalCurve, flattenCurve)) {
              processedSegment.add(fundamentalCurve.clone());
            } else {
              BezierCurve between = BezierCurveUtils.interpolate(
                      fundamentalCurve, flattenCurve, flatteningControl);
              between.setCutoffStart(fundamentalCurve.getCutoffStart());
              between.setCutoffEnd(fundamentalCurve.getCutoffEnd());
              processedSegment.add(between);
            }
          } else if (isArisable) {
            BezierCurve ariseCurve = ariseData.get(i).get(j).get(k);
            if (BezierCurveUtils.comparePoints(fundamentalCurve, ariseCurve)) {
              processedSegment.add(fundamentalCurve.clone());
            } else {
              BezierCurve between = BezierCurveUtils.interpolate(
                      ariseCurve, fundamentalCurve, ariseControl);
              between.setCutoffStart(fundamentalCurve.getCutoffStart());
              between.setCutoffEnd(fundamentalCurve.getCutoffEnd());
              processedSegment.add(between);
            }
          } else {
            // TODO: handle when character has flattenData & ariseData.
          }
        }
        processedSegments.add(processedSegment);
      }
      processedData.add(processedSegments);
    }
  }

  /**
   * Apply slant feature.
   *
   * @param slantControl slant value within range 0~1
   */
  public void applySlant(double slantControl) {
    double slantRadian = slantControl * Math.PI / 2 * SLANT_MAX;
    for (ArrayList<BezierCurve> contour : contours) {
      for (int i = 0; i < contour.size(); i++) {
        BezierCurve bezierCurve = contour.get(i);
        Vector2D[] points = bezierCurve.getPoints();
        Vector2D[] newPoints = new Vector2D[points.length];
        for (int j = 0; j < points.length; j++) {
          Vector2D point = points[j];

          double newX = point.getX()
              + (point.getY() - locatorRegion.getMinY()) / (Math.tan(Math.PI / 2 - slantRadian));

          newPoints[j] = new Vector2D(newX, point.getY());
        }
        bezierCurve.setPoints(newPoints);
      }
    }
  }

  private void setPaths(Region canvasRegion, ArrayList<ArrayList<BezierCurve>> curvesSet,
                        ArrayList<Path> paths, boolean showPoints) {
    for (ArrayList<BezierCurve> curves : curvesSet) {
      Path path = new Path();
      BezierCurve transInitial = locatorRegion.transformBezierCurve(canvasRegion, curves.get(0));
      path.moveTo((float) transInitial.getStartPoint().getX(),
          (float) transInitial.getStartPoint().getY());

      for (BezierCurve curve : curves) {
        if (curve.isCollapsed()) {
          continue;
        }

        BezierCurve transCurve = locatorRegion.transformBezierCurve(canvasRegion, curve);
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

  /**
   * Getter of contour.
   *
   * @return contour
   */
  public ArrayList<ArrayList<BezierCurve>> getContour() {
    return contours;
  }

  @Override
  public void onFeatureChange() {
    manipulateSkeleton(FeatureController.getInstance().getFlattening(),
            FeatureController.getInstance().getArise());
    applyCurve(FeatureController.getInstance().getCurve());
    applyWidth(FeatureController.getInstance().getWidth(), true);
    applyContour(FeatureController.getInstance().getWeight(),
        FeatureController.getInstance().getRoundness(),
        FeatureController.getInstance().getContrast());
    applySlant(FeatureController.getInstance().getSlant());
  }

  @Override
  public int getPriority() {
    return PRIORITY;
  }
}