package kr.ac.kaist.team888.bezier;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementation of the Tiller-Hanson algorithm to offset Bezier curves.
 */
class OffsetTillerHanson {
  /**
   * Returns a offset of the given Bezier curve as the form of a Bezier curve
   *     by using the Tiller-Hanson algorithm.
   *
   * @param bezierCurve a Bezier curve to append offsetting
   * @param delta offset distance
   * @return a offset Bezier curve
   */
  protected static BezierCurve offset(BezierCurve bezierCurve, double delta) {
    Vector2D[] points = bezierCurve.getPoints();
    ArrayList<Vector2D> offsetPoints = new ArrayList<>();
    Vector2D prevPoint;
    Vector2D nextPoint;
    for (int i = 0; i < points.length; i++) {
      prevPoint = (i == 0) ? null : points[i - 1];
      nextPoint = (i + 1 < points.length) ? points[i + 1] : null;
      offsetPoints.add(offsetPoint(points[i], prevPoint, nextPoint, delta));
    }

    return new BezierCurve(offsetPoints.toArray(new Vector2D[offsetPoints.size()]));
  }

  /**
   * Returns a contour of the given sequence of the Bezier curves
   *     as the form of a sequence of Bezier curves by using the Tiller-Hanson algorithm.
   *
   * @param bezierCurves a sequence of Bezier curves to append stroking
   * @param delta offset distance
   * @return a contour sequence
   */
  protected static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> bezierCurves,
                                                 double delta) {
    ArrayList<BezierCurve> contourUpper = new ArrayList<>();
    ArrayList<BezierCurve> contourLower = new ArrayList<>();
    for (BezierCurve bezierCurve : bezierCurves) {
      contourUpper.add(offset(bezierCurve, delta));
      contourLower.add(offset(bezierCurve, -delta).reverse());
    }
    Collections.reverse(contourLower);

    ArrayList<BezierCurve> contour = new ArrayList<>();

    // Store special curves and points
    BezierCurve firstBaseCurve = bezierCurves.get(0);
    BezierCurve lastBaseCurve = bezierCurves.get(bezierCurves.size() - 1);
    Vector2D firstBasePoint = firstBaseCurve.getPoint(0);
    Vector2D firstNextPoint = firstBaseCurve.getPoint(1);
    Vector2D lastBasePoint = lastBaseCurve.getPoint(lastBaseCurve.getOrder());
    Vector2D lastPrevPoint = lastBaseCurve.getPoint(lastBaseCurve.getOrder() - 1);

    // Check closeness
    boolean isClosed = firstBaseCurve.getPoint(0)
        .equals(lastBaseCurve.getPoint(lastBaseCurve.getOrder()));

    // Upper contour filling
    fillHalfContour(contourUpper, bezierCurves, contour, delta);

    // Connect upper to lower
    if (isClosed) {
      joinTwoHalfContours(lastBasePoint, lastPrevPoint, firstNextPoint, contour, delta);
    } else {
      joinTwoHalfContours(lastBasePoint, lastPrevPoint, contour, delta);
    }


    // Lower contour filling
    ArrayList<BezierCurve> reverseBezierCurves = new ArrayList<>();
    for (BezierCurve bezierCurve : bezierCurves) {
      reverseBezierCurves.add(bezierCurve.reverse());
    }
    Collections.reverse(reverseBezierCurves);
    fillHalfContour(contourLower, reverseBezierCurves, contour, delta);

    // Connect lower to upper
    if (isClosed) {
      joinTwoHalfContours(firstBasePoint, firstNextPoint, lastPrevPoint, contour, delta);
    } else {
      joinTwoHalfContours(firstBasePoint, firstNextPoint, contour, delta);
    }

    return contour;
  }

  private static void fillHalfContour(ArrayList<BezierCurve> halfContour,
                                      ArrayList<BezierCurve> baseCurves,
                                      ArrayList<BezierCurve> contour, double delta) {
    for (int i = 0; i < halfContour.size(); i++) {
      BezierCurve currentUpperCurve = halfContour.get(i);
      contour.add(currentUpperCurve);
      if (i == halfContour.size() - 1) {
        continue;
      }

      BezierCurve currentBaseCurve = baseCurves.get(i);
      BezierCurve nextBaseCurve = baseCurves.get(i + 1);
      BezierCurve nextUpperCurve = halfContour.get(i + 1);

      Vector2D joiningPoint = offsetPoint(nextBaseCurve.getPoint(0),
          currentBaseCurve.getPoint(currentBaseCurve.getOrder() - 1),
          nextBaseCurve.getPoint(1), delta);

      Vector2D[] joiningPoints = {currentUpperCurve.getPoint(currentUpperCurve.getOrder()),
          joiningPoint, nextUpperCurve.getPoint(0)};
      contour.add(new BezierCurve(new Vector2D[]{joiningPoints[0], joiningPoints[1]}));
      contour.add(new BezierCurve(new Vector2D[]{joiningPoints[1], joiningPoints[2]}));
    }
  }

  private static void joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          ArrayList<BezierCurve> contour, double delta) {
    Vector2D baseUnitVector = basePoint.subtract(prevPoint).normalize();
    Vector2D joiningPoint = basePoint.add(delta, baseUnitVector);
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, null, joiningPoint, delta),
        offsetPoint(joiningPoint, basePoint, null, delta),
        offsetPoint(joiningPoint, basePoint, null, -delta),
        offsetPoint(basePoint, null, joiningPoint, -delta),
    };

    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[0], joiningPoints[1]}));
    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[1], joiningPoints[2]}));
    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[2], joiningPoints[3]}));
  }

  private static void joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          Vector2D oppositePrevPoint,
                                          ArrayList<BezierCurve> contour, double delta) {
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, prevPoint, null, delta),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, delta),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, -delta),
        offsetPoint(basePoint, prevPoint, null, -delta)
    };

    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[0], joiningPoints[1]}));
    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[1], joiningPoints[2]}));
    contour.add(new BezierCurve(new Vector2D[]{joiningPoints[2], joiningPoints[3]}));
  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint,
                                      Vector2D nextPoint, double delta) {
    if (nextPoint == null) {
      return offsetPoint(currentPoint, prevPoint, delta);
    }
    if (prevPoint == null) {
      return offsetPoint(currentPoint, nextPoint, -delta);
    }

    Vector2D point1 = offsetPoint(currentPoint, prevPoint, delta);
    Vector2D point2 = offsetPoint(currentPoint, nextPoint, -delta);

    Vector2D unit1 = currentPoint.subtract(prevPoint).normalize();
    Vector2D unit2 = nextPoint.subtract(currentPoint).normalize();
    Vector2D baseVector = unit1.add(unit2);
    Vector2D targetVector = point2.subtract(point1);
    double dotProduct = targetVector.dotProduct(baseVector);
    double factor = (dotProduct > 0 ? 1 : -1) * targetVector.getNorm() / baseVector.getNorm();

    return point1.add(factor, unit1);

  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint, double delta) {
    Vector2D vector = currentPoint.subtract(prevPoint);
    Vector2D perpendicularUnitVector = new Vector2D(vector.getY(), -vector.getX()).normalize();
    return currentPoint.add(delta, perpendicularUnitVector);
  }
}
