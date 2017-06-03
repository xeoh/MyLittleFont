package kr.ac.kaist.team888.region;

import kr.ac.kaist.team888.bezier.BezierCurve;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * This class represents the region on the plane.
 *
 * <p>A region is a square area of the plane, i.e., mathematically defined by
 * [minX, maxX]×[minY, minY]. This class uses those four values to determine a specific region.
 */
public class Region {
  private double minX;
  private double maxX;
  private double minY;
  private double maxY;

  /**
   * Makes a new region with given four vertices.
   *
   * @param minX a minimum value of a region in x-axis.
   * @param maxX a maximum value of a region in x-axis.
   * @param minY a minimum value of a region in y-axis.
   * @param maxY a maximum value of a region in y-axis.
   */
  public Region(double minX, double maxX, double minY, double maxY) {
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
  }

  /**
   * Returns a point at the bottom-left corner of the region.
   *
   * @return a point at the bottom-left corner of the region.
   */
  public Vector2D getMinPoint() {
    return new Vector2D(minX, minY);
  }

  /**
   * Returns a point at the top-right corner of the region.
   *
   * @return a point at the top-right corner of the region.
   */
  public Vector2D getMaxPoint() {
    return new Vector2D(maxX, maxY);
  }

  /**
   * Returns the minimum value of the region in x-axis.
   *
   * @return the minimum value of the region in x-axis.
   */
  public double getMinX() {
    return minX;
  }

  /**
   * Sets the minimum value of the region in x-axis.
   *
   * @param minX a minimum value of the region in x-axis.
   */
  public void setMinX(double minX) {
    this.minX = minX;
  }

  /**
   * Returns the maximum value of the region in x-axis.
   *
   * @return the maximum value of the region in x-axis.
   */
  public double getMaxX() {
    return maxX;
  }

  /**
   * Sets the maximum value of the region in x-axis.
   *
   * @param maxX a minimum value of the region in x-axis.
   */
  public void setMaxX(double maxX) {
    this.maxX = maxX;
  }

  /**
   * Returns the minimum value of the region in y-axis.
   *
   * @return the minimum value of the region in y-axis.
   */
  public double getMinY() {
    return minY;
  }

  /**
   * Sets the minimum value of the region in y-axis.
   *
   * @param minY a minimum value of the region in y-axis.
   */
  public void setMinY(double minY) {
    this.minY = minY;
  }

  /**
   * Returns the maximum value of the region in y-axis.
   *
   * @return the maximum value of the region in y-axis.
   */
  public double getMaxY() {
    return maxY;
  }

  /**
   * Sets the maximum value of the region in y-axis.
   *
   * @param maxY a maximum value of the region in y-axis.
   */
  public void setMaxY(double maxY) {
    this.maxY = maxY;
  }

  /**
   * Do linear transformation on destination region.
   *
   * @param dst destination region
   * @param curve Bezier curve to transform
   * @return new stroke on destination region
   */
  public BezierCurve transformBezierCurve(Region dst, BezierCurve curve) {
    Vector2D[] transformedPoints = new Vector2D[curve.getPoints().length];
    for (int i = 0; i < transformedPoints.length; i++) {
      transformedPoints[i] = (transformVector2D(dst, curve.getPoint(i)));
    }
    return new BezierCurve.Builder()
        .setPoints(transformedPoints)
        .setOffsetVector(curve.getOffsetVector())
        .setEndOffsetVector(curve.getEndOffsetVector())
        .build();
  }

  /**
   * Do linear transformation on destination region.
   *
   * @param dst destination region
   * @param point point to transform
   * @return new point on destination region
   */
  public Vector2D transformVector2D(Region dst, Vector2D point) {
    Vector2D baseMinPoint = getMinPoint();
    Vector2D baseVector = getMaxPoint().subtract(baseMinPoint);
    Vector2D targetMinPoint = dst.getMinPoint();
    Vector2D targetVector = dst.getMaxPoint().subtract(targetMinPoint);

    Vector2D diffVector = point.subtract(baseMinPoint);
    return new Vector2D(diffVector.getX() * targetVector.getX() / baseVector.getX(),
        diffVector.getY() * targetVector.getY() / baseVector.getY())
        .add(targetMinPoint);
  }

  @Override
  public String toString() {
    return String.format("X: [%f, %f] Y:[%f, %f]", minX, maxX, minY, maxY);
  }
}
