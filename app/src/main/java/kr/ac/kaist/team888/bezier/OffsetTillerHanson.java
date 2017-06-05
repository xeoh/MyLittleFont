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
  protected static BezierCurve offset(BezierCurve curve, double delta, double contrast) {
    Vector2D[] points = curve.getPoints();
    ArrayList<Vector2D> offsetPoints = new ArrayList<>();
    Vector2D prevPoint;
    Vector2D nextPoint;
    for (int i = 0; i <= curve.getOrder(); i++) {
      prevPoint = (i == 0) ? null : points[i - 1];
      nextPoint = (i == curve.getOrder()) ? null : points[i + 1];
      offsetPoints.add(offsetPoint(points[i], prevPoint, nextPoint,
          delta, contrast, curve.getOffsetVector(i)));
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
                                                 double delta, double roundness, double contrast) {
    ArrayList<BezierCurve> contourUpper = new ArrayList<>();
    ArrayList<BezierCurve> contourLower = new ArrayList<>();
    for (BezierCurve curve : curves) {
      contourUpper.add(offset(curve, delta, contrast));
      contourLower.add(offset(curve, -delta, contrast).reverse());
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
    fillHalfContour(contourUpper, curves, contour, delta, contrast);

    // Connect upper to lower
    if (isClosed) {
      joinTwoHalfContours(lastBasePoint, lastPrevPoint, firstNextPoint, contour, delta, contrast,
          lastBaseCurve.getOffsetVector(), firstBaseCurve.getOffsetVector());
    } else {
      if (lastBaseCurve.getCutoffEndVector() == null) {
        joinTwoHalfContours(lastBasePoint, lastPrevPoint, contour, delta, roundness, contrast,
            lastBaseCurve.getOffsetVector());
      } else {
        BezierCurve positiveCurve = contourUpper.get(contourUpper.size() - 1);
        BezierCurve negativeCurve = contourLower.get(0);
        Vector2D diffVector = joinTwoHalfContours(lastBasePoint, lastPrevPoint, contour, delta,
            contrast, lastBaseCurve.getOffsetVector(), lastBaseCurve.getCutoffEndVector());
        positiveCurve.setEndPoint(positiveCurve.getEndPoint().add(diffVector));
        negativeCurve.setStartPoint(negativeCurve.getStartPoint().add(-1, diffVector));
      }
    }


    // Lower contour filling
    ArrayList<BezierCurve> reverseBezierCurves = new ArrayList<>();
    for (BezierCurve curve : curves) {
      reverseBezierCurves.add(curve.reverse());
    }
    Collections.reverse(reverseBezierCurves);
    fillHalfContour(contourLower, reverseBezierCurves, contour, delta, contrast);

    // Connect lower to upper
    if (isClosed) {
      joinTwoHalfContours(firstBasePoint, firstNextPoint, lastPrevPoint, contour, delta, contrast,
          firstBaseCurve.getOffsetVector(), lastBaseCurve.getOffsetVector());
    } else {
      if (firstBaseCurve.getCutoffStartVector() == null) {
        joinTwoHalfContours(firstBasePoint, firstNextPoint, contour, delta, roundness, contrast,
            firstBaseCurve.getOffsetVector());
      } else {
        BezierCurve positiveCurve = contourLower.get(contourUpper.size() - 1);
        BezierCurve negativeCurve = contourUpper.get(0);
        Vector2D diffVector = joinTwoHalfContours(firstBasePoint, firstNextPoint, contour, delta,
            contrast, firstBaseCurve.getOffsetVector(), firstBaseCurve.getCutoffStartVector());
        positiveCurve.setEndPoint(positiveCurve.getEndPoint().add(diffVector));
        negativeCurve.setStartPoint(negativeCurve.getStartPoint().add(-1, diffVector));
      }
    }

    return contour;
  }

  private static void fillHalfContour(ArrayList<BezierCurve> halfContour,
                                      ArrayList<BezierCurve> baseCurves,
                                      ArrayList<BezierCurve> contour,
                                      double delta, double contrast) {
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
          nextBaseCurve.getPoint(1), delta, contrast, offsetVector,
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
                                          double delta, double roundness, double contrast,
                                          Vector2D offsetVector) {
    Vector2D baseUnitVector = basePoint.subtract(prevPoint).normalize();
    double deltaRatio = getOffsetTargetVector(baseUnitVector, contrast, offsetVector)
        .dotProduct(baseUnitVector);
    Vector2D joiningPoint = basePoint.add(delta * deltaRatio, baseUnitVector);
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, null, joiningPoint, delta, contrast, offsetVector),
        offsetPoint(joiningPoint, basePoint, null, delta, contrast, offsetVector),
        offsetPoint(joiningPoint, basePoint, null, -delta, contrast, offsetVector),
        offsetPoint(basePoint, null, joiningPoint, -delta, contrast, offsetVector),
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

  private static Vector2D joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          ArrayList<BezierCurve> contour,
                                          double delta, double contrast,
                                          Vector2D offsetVector, Vector2D cutoffVector) {
    Vector2D direction = basePoint.subtract(prevPoint).normalize();
    double offsetLength = delta * getOffsetTargetVector(
        new Vector2D(-direction.getY(), direction.getX()), contrast, offsetVector).getNorm();
    double distance = offsetLength
        * (direction.getX() * cutoffVector.getY() - direction.getY() * cutoffVector.getX())
        / direction.dotProduct(cutoffVector);
    contour.add(new BezierCurve.Builder()
        .setPoints(new Vector2D[] {
            offsetPoint(basePoint, null, prevPoint, -delta, contrast, offsetVector)
                .add(distance, direction),
            offsetPoint(basePoint, null, prevPoint, delta, contrast, offsetVector)
                .add(-distance, direction)
        })
        .setOffsetVector(offsetVector)
        .build());
    return direction.scalarMultiply(distance);
  }

  private static void joinTwoHalfContours(Vector2D basePoint, Vector2D prevPoint,
                                          Vector2D oppositePrevPoint,
                                          ArrayList<BezierCurve> contour,
                                          double delta, double contrast,
                                          Vector2D baseOffsetVector, Vector2D nextOffsetVector) {
    Vector2D[] joiningPoints = {
        offsetPoint(basePoint, prevPoint, null, delta, contrast, baseOffsetVector),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, delta, contrast,
            baseOffsetVector, nextOffsetVector),
        offsetPoint(basePoint, prevPoint, oppositePrevPoint, -delta, contrast,
            baseOffsetVector, nextOffsetVector),
        offsetPoint(basePoint, prevPoint, null, -delta, contrast, baseOffsetVector)
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
                                      Vector2D nextPoint, double delta, double contrast,
                                      Vector2D offsetVector) {
    return offsetPoint(currentPoint, prevPoint, nextPoint, delta, contrast,
        offsetVector, offsetVector);
  }

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint,
                                      Vector2D nextPoint, double delta, double contrast,
                                      Vector2D prevOffsetVector, Vector2D nextOffsetVector) {
    if (nextPoint == null) {
      return offsetPoint(currentPoint, prevPoint, delta, contrast, prevOffsetVector);
    }
    if (prevPoint == null) {
      return offsetPoint(currentPoint, nextPoint, -delta, contrast, nextOffsetVector);
    }

    Vector2D point1 = offsetPoint(currentPoint, prevPoint, delta, contrast, prevOffsetVector);
    Vector2D point2 = offsetPoint(currentPoint, nextPoint, -delta, contrast, nextOffsetVector);

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

  private static Vector2D offsetPoint(Vector2D currentPoint, Vector2D prevPoint,
                                      double delta, double contrast, Vector2D offsetVector) {
    Vector2D vector = currentPoint.subtract(prevPoint);
    Vector2D perpendicularVector = new Vector2D(vector.getY(), -vector.getX());
    Vector2D targetVector = getOffsetTargetVector(perpendicularVector, contrast, offsetVector);
    return currentPoint.add(delta, targetVector);
  }

  private static Vector2D getOffsetTargetVector(Vector2D vector, double contrast,
                                                Vector2D offsetVector) {
    Vector2D unitVector = vector.normalize();
    double xRatio = 1 / ((contrast > 1 ? 1 : (2 / contrast - 1)) * offsetVector.getX());
    double yRatio = 1 / ((contrast > 1 ? contrast / (2 - contrast) : 1) * offsetVector.getY());
    double length = Math.sqrt(1 / ((unitVector.getX() * unitVector.getX()) / (xRatio * xRatio)
        + (unitVector.getY() * unitVector.getY()) / (yRatio * yRatio)));
    return new Vector2D(length, unitVector);
  }
}
