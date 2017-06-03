package kr.ac.kaist.team888.bezier;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Utils for Bezier curve manipulations.
 */
public class BezierCurveUtils {
  private static final BezierCurveOffsetMethodType DEFAULT_OFFSET_METHOD =
      BezierCurveOffsetMethodType.TillerHanson;

  private static final int INTEGRATION_MAX_EVAL = 100000;

  /**
   * Strokes the sequence of Bezier curves by given distance with the Tiller-Hanson algorithm.
   *
   * <p>Stroking is basically offsetting in both sides of the given curves.
   * This method connects upper and lower offset curves to make a whole contour.
   *
   * @param curves sequence of Bezier curves to stroke
   * @param delta distance to stroke
   * @return a stroked contour in the form of a sequence of Bezier curves
   */
  public static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> curves,
                                              double delta, double roundness) {
    return stroke(curves, DEFAULT_OFFSET_METHOD, delta, roundness);
  }

  /**
   * Strokes the sequence of Bezier curves by given distance with the given offset algorithm.
   *
   * <p>Stroking is basically offsetting in both sides of the given curves.
   * This method connects upper and lower offset curves to make a whole contour.
   *
   * @param curves sequence of Bezier curves to stroke
   * @param offsetMethod offset method to apply
   * @param delta distance to stroke
   * @return a stroked contour in the form of a sequence of Bezier curves
   */
  public static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> curves,
                                              BezierCurveOffsetMethodType offsetMethod,
                                              double delta, double roundness) {
    switch (offsetMethod) {
      case TillerHanson:
        return OffsetTillerHanson.stroke(curves, delta, roundness);
      default:
        return null;
    }
  }

  /**
   * Returns the minimum value in x-axis among points of the given Bezier curve.
   * @param curve a Bezier curve
   * @return the minimum value in x-axis among points
   */
  public static double getMinX(BezierCurve curve) {
    ArrayList<Double> pointsX = new ArrayList<>();
    for (Vector2D point : curve.getPoints()) {
      pointsX.add(point.getX());
    }
    return Collections.min(pointsX);
  }

  /**
   * Returns the maximum value in x-axis among points of the given Bezier curve.
   * @param curve a Bezier curve
   * @return the maximum value in x-axis among points
   */
  public static double getMaxX(BezierCurve curve) {
    ArrayList<Double> pointsX = new ArrayList<>();
    for (Vector2D point : curve.getPoints()) {
      pointsX.add(point.getX());
    }
    return Collections.max(pointsX);
  }

  /**
   * Returns the minimum value in y-axis among points of the given Bezier curve.
   * @param curve a Bezier curve
   * @return the minimum value in y-axis among points
   */
  public static double getMinY(BezierCurve curve) {
    ArrayList<Double> pointsY = new ArrayList<>();
    for (Vector2D point : curve.getPoints()) {
      pointsY.add(point.getY());
    }
    return Collections.min(pointsY);
  }

  /**
   * Returns the maximum value in y-axis among points of the given Bezier curve.
   * @param curve a Bezier curve
   * @return the maxmimum value in y-axis among points
   */
  public static double getMaxY(BezierCurve curve) {
    ArrayList<Double> pointsY = new ArrayList<>();
    for (Vector2D point : curve.getPoints()) {
      pointsY.add(point.getY());
    }
    return Collections.max(pointsY);
  }

  /**
   * Returns the curve length of a Bezier curve from min to max.
   *
   * <p>This integration is calculated by using
   * <a href="https://en.wikipedia.org/wiki/Romberg%27s_method">Romberg's method</a>.
   * Note that negative value would be returned if min is bigger than max.
   *
   * @param curve a Bezier curve
   * @param min minimum value of length range
   * @param max maximum value of length range
   * @return the curve length of a Bezier curve from min to max
   */
  public static double getLength(final BezierCurve curve, double min, double max) {
    UnivariateFunction arcFunc = new UnivariateFunction() {
      @Override
      public double value(double time) {
        ParametricPolynomialCurve derivative = curve.derivative();
        return new Vector2D(derivative.value(time)).getNorm();
      }
    };
    if (min == max) {
      return 0;
    }
    if (curve.getOrder() == 1) {
      return (max - min) * curve.getEndPoint().subtract(curve.getStartPoint()).getNorm();
    }
    if (min > max) {
      return -new RombergIntegrator().integrate(INTEGRATION_MAX_EVAL, arcFunc, max, min);
    }
    return new RombergIntegrator().integrate(INTEGRATION_MAX_EVAL, arcFunc, min, max);
  }

  /**
   * Returns the curve length of a sequence of Bezier curves from min to max.
   *
   * <p>i to (i+1) is the range for i-th curve in the sequence beginning with i=0.
   *
   * <p>This integration is calculated by using
   * <a href="https://en.wikipedia.org/wiki/Romberg%27s_method">Romberg's method</a>.
   * Note that negative value would be returned if min is bigger than max.
   *
   * <p>If the sequence is empty, it returns 0.
   *
   * @param curves a sequence of Bezier curves
   * @param min minimum value of length range
   * @param max maximum value of length range
   * @return the curve length of Bezier curves from min to max
   */
  public static double getLength(final BezierCurve[] curves, double min, double max) {
    if (curves.length == 0 || min == max) {
      return 0;
    }
    int sign = 1;
    if (min > max) {
      double temp = min;
      min = max;
      max = temp;
      sign = -1;
    }
    double value = 0;
    if (min < 0) {
      value += getLength(curves[0], min, 0);
      min = 0;
    }
    if (max > curves.length) {
      value += getLength(curves[curves.length - 1], 1, max);
      max = curves.length;
    }
    int startIndex = (int) Math.floor(min);
    for (int i = startIndex; i < curves.length; i++) {
      if (i + 1 > max) {
        value += getLength(curves[i], i == 0 ? min : 0, max - i);
        break;
      }
      value += getLength(curves[i], i == startIndex ? min - i : 0, 1);
    }
    return sign * value;
  }

  /**
   * Returns true when both lengths of source(A) & target(B) are same. (Each Vector2D of A compared
   * with Vector2D of B at same index)
   * Otherwise return false.
   *
   * @param source The BezierCurve which be a criterion.
   * @param target A BezierCurve which will be compared with source.
   * @return boolean.
   */
  public static boolean comparePoints(BezierCurve source, BezierCurve target) {
    Vector2D[] sourcePoints = source.getPoints();
    Vector2D[] targetPoints = target.getPoints();

    if (sourcePoints.length != targetPoints.length) {
      return false;
    }

    for (int p = 0; p < sourcePoints.length; p++) {
      if (sourcePoints[p] != targetPoints[p]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Return new BezierCurve between source(A) and target(B).
   * If proportion is 0, return same BezierCurve with A.
   * If proportion is 1, return same BezierCurve with B.
   * Otherwise, return a BezierCurve between A & B.
   *
   * @param source The BezierCurve which be a criterion.
   * @param target A BezierCurve has maximum interpolate points.
   * @param proportion double value, 0 <= range <= 1.
   * @return new BezierCurve
   */
  public static BezierCurve interpolate(BezierCurve source, BezierCurve target, double proportion) {
    Vector2D[] sourcePoints = source.getPoints();
    Vector2D[] targetPoints = target.getPoints();

    if (sourcePoints.length != targetPoints.length) {
      throw new IllegalArgumentException();
    }

    Vector2D[] resultPoints = new Vector2D[sourcePoints.length];
    // Formula: (target - source) * proportion + source
    for (int p = 0; p < sourcePoints.length; p++) {
      Vector2D diff = targetPoints[p].subtract(sourcePoints[p]).scalarMultiply(proportion);
      resultPoints[p] = sourcePoints[p].add(diff);
    }

    return new BezierCurve(resultPoints);
  }
}
