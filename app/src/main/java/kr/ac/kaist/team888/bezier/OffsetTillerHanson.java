package kr.ac.kaist.team888.bezier;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementation of the Tiller-Hanson algorithm to offset Bezier curves.
 */
class OffsetTillerHanson {
  private static final double CURVE_TOLERANCE = 1E-4;

  /**
   * Returns a offset of the given Bezier curve as the form of a Bezier curve
   *     by using the Tiller-Hanson algorithm.
   *
   * @param curve a Bezier curve to append offsetting
   * @param delta offset distance
   * @return a offset Bezier curve
   */
  protected static BezierCurve offset(BezierCurve curve, double delta) {
    Vector2D[] points = curve.getPoints();
    ArrayList<Vector2D> offsetPoints = new ArrayList<>();
    Vector2D prevPoint;
    Vector2D nextPoint;
    for (int i = 0; i <= curve.getOrder(); i++) {
      prevPoint = (i == 0) ? null : points[i - 1];
      nextPoint = (i == curve.getOrder()) ? null : points[i + 1];
      offsetPoints.add(offsetPoint(points[i], prevPoint, nextPoint,
          delta, curve.getOffsetVector(i)));
    }

    return new BezierCurve.Builder()
        .setPoints(offsetPoints.toArray(new Vector2D[offsetPoints.size()]))
        .setOffsetVector(curve.getOffsetVector())
        .setEndOffsetVector(curve.getEndOffsetVector())
        .build();
  }

  /**
   * Returns a contour of the given sequence of the Bezier curves
   *     as the form of a sequence of Bezier curves by using the Tiller-Hanson algorithm.
   *
   * @param curves a sequence of Bezier curves to append stroking
   * @param delta offset distance
   * @return a contour sequence
   */
  protected static ArrayList<BezierCurve> stroke(ArrayList<BezierCurve> curves,
                                                 double delta, double roundness) {
    ArrayList<BezierCurve> contourUpper = new ArrayList<>();
    ArrayList<BezierCurve> contourLower = new ArrayList<>();
    for (BezierCurve curve : curves) {
      contourUpper.add(offset(curve, delta));
      contourLower.add(offset(curve, -delta).reverse());
    }
    Collections.reverse(contourLower);

    ArrayList<BezierCurve> contour = new ArrayList<>();

    // Store special curves and points
    BezierCurve firstBaseCurve = curves.get(0);
    BezierCurve lastBaseCurve = curves.get(curves.size() - 1);
    Vector2D firstBasePoint = firstBaseCurve.getPoint(0);
    Vector2D firstNextPoint = firstBaseCurve.getPoint(1);
    Vector2D lastBasePoint = lastBaseCurve.getPoint(lastBaseCurve.getOrder());
    Vector2D lastPrevPoint = lastBaseCurve.getPoint(lastBaseCurve.getOrder() - 1);

    // Check closeness
    boolean isClosed = firstBaseCurve.getPoint(0)
        .equals(lastBaseCurve.getPoint(lastBaseCurve.getOrder()));

    // Upper contour filling
    fillHalfContour(contourUpper, curves, contour, delta);

    // Connect upper to lower
    if (isClosed) {
      joinTwoHalfContours(lastBasePoint, lastPrevPoint, firstNextPoint, contour, delta,
          lastBaseCurve.getOffsetVector(), firstBaseCurve.getOffsetVector());
    } else {
      joinTwoHalfContours(lastBasePoint, lastPrevPoint, contour, delta, roundness,
          lastBaseCurve.getOffsetVector());
    }


    // Lower contour filling
    ArrayList<BezierCurve> reverseBezierCurves = new ArrayList<>();
    for (BezierCurve curve : curves) {
      reverseBezierCurves.add(curve.reverse());
    }
    Collections.reverse(reverseBezierCurves);
    fillHalfContour(contourLower, reverseBezierCurves, contour, delta);

    // Connect lower to upper
    if (isClosed) {
      joinTwoHalfContours(firstBasePoint, firstNextPoint, lastPrevPoint, contour, delta,
          firstBaseCurve.getOffsetVector(), lastBaseCurve.getOffsetVector());
    } else {
      joinTwoHalfContours(firstBasePoint, firstNextPoint, contour, delta, roundness,
          firstBaseCurve.getOffsetVector());
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

      Vector2D offsetVector = currentBaseCurve.getEndOffsetVector();
      if (offsetVector == null) {
        offsetVector = currentBaseCurve.getOffsetVector();
      }

      Vector2D joiningPoint = offsetPoint(nextBaseCurve.getPoint(0),
          currentBaseCurve.getPoint(currentBaseCurve.getOrder() - 1),
          nextBaseCurve.getPoint(1), delta, offsetVector,
          nextBaseCurve.getOffsetVector());

      Vector2D[] joiningPoints = {currentUpperCurve.getPoint(currentUpperCurve.getOrder()),
          joiningPoint, nextUpperCurve.getPoint(0)};

      if (!joiningPoints[0].equals(joiningPoints[1])) {
        contour.add(new BezierCurve.Builder()
            .setPoints(new Vector2D[]{joiningPoints[0], joiningPoints[1]})
            .setOffsetVector(currentBaseCurve.getOffsetVector())
            .setEndOffsetVector(currentBaseCurve.getEndOffsetVector())
            .build());
      }
      if (!joiningPoints[1].equals(joiningPoints[2])) {
        contour.add(new BezierCurve.Builder()
            .setPoints(new Vector2D[]{joiningPoints[1], joiningPoints[2]})
            .setOffsetVector(currentBaseCurve.getOffsetVector())
            .setEndOffsetVector(currentBaseCurve.getEndOffsetVector())
            .build());
      }
    }
  }

  private static void joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          ArrayList<BezierCurve> contour,
                                          double delta, double roundness,
                                          Vector2D offsetVector) {
    Vector2D baseUnitVector = basePoint.subtract(prevPoint).normalize();
    double deltaRatio = getOffsetTargetVector(baseUnitVector, offsetVector)
        .dotProduct(baseUnitVector);
    Vector2D joiningPoint = basePoint.add(delta * deltaRatio, baseUnitVector);
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, null, joiningPoint, delta, offsetVector),
        offsetPoint(joiningPoint, basePoint, null, delta, offsetVector),
        offsetPoint(joiningPoint, basePoint, null, -delta, offsetVector),
        offsetPoint(basePoint, null, joiningPoint, -delta, offsetVector),
    };

    Vector2D[][] roundPoints = {
        roundJoining(joiningPoints[0], joiningPoints[1], joiningPoints[2], delta, roundness),
        roundJoining(joiningPoints[3], joiningPoints[2], joiningPoints[1], delta, roundness)
    };

    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {joiningPoints[0], roundPoints[0][0]})
        .setOffsetVector(offsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {roundPoints[0][0], joiningPoints[1],roundPoints[0][1]})
        .setOffsetVector(offsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {roundPoints[0][1], roundPoints[1][1]})
        .setOffsetVector(offsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {roundPoints[1][1], joiningPoints[2], roundPoints[1][0]})
        .setOffsetVector(offsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {roundPoints[1][0], joiningPoints[3]})
        .setOffsetVector(offsetVector)
        .build());
  }

  private static void joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          Vector2D oppositePrevPoint,
                                          ArrayList<BezierCurve> contour, double delta,
                                          Vector2D baseOffsetVector, Vector2D nextOffsetVector) {
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, prevPoint, null, delta, baseOffsetVector),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, delta,
            baseOffsetVector, nextOffsetVector),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, -delta,
            baseOffsetVector, nextOffsetVector),
        offsetPoint(basePoint, prevPoint, null, -delta, baseOffsetVector)
    };

    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {joiningPoints[0], joiningPoints[1]})
        .setOffsetVector(baseOffsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {joiningPoints[1], joiningPoints[2]})
        .setOffsetVector(baseOffsetVector)
        .build());
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {joiningPoints[2], joiningPoints[3]})
        .setOffsetVector(baseOffsetVector)
        .build());
  }

  private static Vector2D[] roundJoining(Vector2D fullPoint, Vector2D joiningPoint,
                                         Vector2D halfPoint, double delta, double roundness) {
    Vector2D fullBaseVector = fullPoint.subtract(joiningPoint);
    Vector2D halfBaseVector = halfPoint.subtract(joiningPoint);
    double length = Math.min(Math.min(delta * roundness, fullBaseVector.getNorm()),
        halfBaseVector.getNorm() / 2);
    return new Vector2D[] {
        joiningPoint.add(length, fullBaseVector.normalize()),
        joiningPoint.add(length, halfBaseVector.normalize())
    };
  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint,
                                      Vector2D nextPoint, double delta, Vector2D offsetVector) {
    return offsetPoint(currentPoint, prevPoint, nextPoint, delta, offsetVector, offsetVector);
  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint,
                                      Vector2D nextPoint, double delta,
                                      Vector2D prevOffsetVector, Vector2D nextOffsetVector) {
    if (nextPoint == null) {
      return offsetPoint(currentPoint, prevPoint, delta, prevOffsetVector);
    }
    if (prevPoint == null) {
      return offsetPoint(currentPoint, nextPoint, -delta, nextOffsetVector);
    }

    Vector2D point1 = offsetPoint(currentPoint, prevPoint, delta, prevOffsetVector);
    Vector2D point2 = offsetPoint(currentPoint, nextPoint, -delta, nextOffsetVector);

    Vector2D unit1 = currentPoint.subtract(prevPoint).normalize();
    Vector2D unit2 = nextPoint.subtract(currentPoint).normalize();

    double cross = unit1.getX() * unit2.getY() - unit1.getY() * unit2.getX();
    if (Math.abs(cross) < CURVE_TOLERANCE) {
      return point1.add(point2).scalarMultiply(.5);
    }
    double factor = (-point1.getX() * unit2.getY()
        + point1.getY() * unit2.getX()
        + point2.getX() * unit2.getY()
        - point2.getY() * unit2.getX()) / cross;
    return point1.add(factor, unit1);
  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint, double delta,
                                      Vector2D offsetVector) {
    Vector2D vector = currentPoint.subtract(prevPoint);
    Vector2D perpendicularVector = new Vector2D(vector.getY(), -vector.getX());
    Vector2D targetVector = getOffsetTargetVector(perpendicularVector, offsetVector);
    return currentPoint.add(delta, perpendicularVector.normalize().scalarMultiply(targetVector.getNorm()));
  }

  private static Vector2D getOffsetTargetVector(Vector2D vector, Vector2D offsetVector) {
    Vector2D unitVector = vector.normalize();
    return new Vector2D(unitVector.getX() * offsetVector.getX(),
        unitVector.getY() * offsetVector.getY());
  }
}
