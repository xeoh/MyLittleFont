package kr.ac.kaist.team888.util.bezier;

import java.util.ArrayList;

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
  public static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> bezierCurves, double delta) {
    return stroke(bezierCurves, DEFAULT_OFFSET_METHOD, delta);
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
                                              double delta) {
    switch (offsetMethod) {
      case TillerHanson:
        return OffsetTillerHanson.stroke(bezierCurves, delta);
      default:
        return null;
    }
  }

}
