package kr.ac.kaist.team888.bezier;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Utils for Bezier curve manipulations.
 */
public class BezierCurveUtils {
  private static final BezierCurveOffsetMethodType DEFAULT_OFFSET_METHOD =
      BezierCurveOffsetMethodType.TillerHanson;

  /**
   * Strokes the sequence of Bezier curves by given distance with the Tiller-Hanson algorithm.
   *
   * <p>Stroking is basically offsetting in both sides of the given curves.
   * This method connects upper and lower offset curves to make a whole contour.
   *
   * @param bezierCurves sequence of Bezier curves to stroke
   * @param delta distance to stroke
   * @return a stroked contour in the form of a sequence of Bezier curves
   */
  public static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> bezierCurves,
                                              double delta, double roundness) {
    return stroke(bezierCurves, DEFAULT_OFFSET_METHOD, delta, roundness);
  }

  /**
   * Strokes the sequence of Bezier curves by given distance with the given offset algorithm.
   *
   * <p>Stroking is basically offsetting in both sides of the given curves.
   * This method connects upper and lower offset curves to make a whole contour.
   *
   * @param bezierCurves sequence of Bezier curves to stroke
   * @param offsetMethod offset method to apply
   * @param delta distance to stroke
   * @return a stroked contour in the form of a sequence of Bezier curves
   */
  public static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> bezierCurves,
                                              BezierCurveOffsetMethodType offsetMethod,
                                              double delta, double roundness) {
    switch (offsetMethod) {
      case TillerHanson:
        return OffsetTillerHanson.stroke(bezierCurves, delta, roundness);
      default:
        return null;
    }
  }

  /**
   * Returns the minimum value in x-axis among points of the given Bezier curve.
   * @param bezierCurve a Bezier curve
   * @return the minimum value in x-axis among points
   */
  public static double getMinX(BezierCurve bezierCurve) {
    ArrayList<Double> pointsX = new ArrayList<>();
    for (Vector2D point : bezierCurve.getPoints()) {
      pointsX.add(point.getX());
    }
    return Collections.min(pointsX);
  }

  /**
   * Returns the maximum value in x-axis among points of the given Bezier curve.
   * @param bezierCurve a Bezier curve
   * @return the maximum value in x-axis among points
   */
  public static double getMaxX(BezierCurve bezierCurve) {
    ArrayList<Double> pointsX = new ArrayList<>();
    for (Vector2D point : bezierCurve.getPoints()) {
      pointsX.add(point.getX());
    }
    return Collections.max(pointsX);
  }

  /**
   * Returns the minimum value in y-axis among points of the given Bezier curve.
   * @param bezierCurve a Bezier curve
   * @return the minimum value in y-axis among points
   */
  public static double getMinY(BezierCurve bezierCurve) {
    ArrayList<Double> pointsY = new ArrayList<>();
    for (Vector2D point : bezierCurve.getPoints()) {
      pointsY.add(point.getY());
    }
    return Collections.min(pointsY);
  }

  /**
   * Returns the maximum value in y-axis among points of the given Bezier curve.
   * @param bezierCurve a Bezier curve
   * @return the maxmimum value in y-axis among points
   */
  public static double getMaxY(BezierCurve bezierCurve) {
    ArrayList<Double> pointsY = new ArrayList<>();
    for (Vector2D point : bezierCurve.getPoints()) {
      pointsY.add(point.getY());
    }
    return Collections.max(pointsY);
  }

}
