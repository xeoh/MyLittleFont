package kr.ac.kaist.team888.core;

import java.util.ArrayList;

/**
 * This class represents the region on the plane.
 *
 * <p>A region is a square area of the plane, i.e., mathematically defined by
 * [minX, maxX]×[minY, minY]. This class uses those four values to determine a specific region.
 */
public class Region {
  private float minX;
  private float maxX;
  private float minY;
  private float maxY;

  /**
   * Makes a new region with given four vertices.
   *
   * @param minX a minimum value of a region in x-axis.
   * @param maxX a maximum value of a region in x-axis.
   * @param minY a minimum value of a region in y-axis.
   * @param maxY a maximum value of a region in y-axis.
   */
  public Region(float minX, float maxX, float minY, float maxY) {
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
  public Point2D getMinPoint() {
    return new Point2D(minX, minY);
  }

  /**
   * Returns a point at the top-right corner of the region.
   *
   * @return a point at the top-right corner of the region.
   */
  public Point2D getMaxPoint() {
    return new Point2D(maxX, maxY);
  }

  /**
   * Returns the minimum value of the region in x-axis.
   *
   * @return the minimum value of the region in x-axis.
   */
  public float getMinX() {
    return minX;
  }

  /**
   * Sets the minimum value of the region in x-axis.
   *
   * @param minX a minimum value of the region in x-axis.
   */
  public void setMinX(float minX) {
    this.minX = minX;
  }

  /**
   * Returns the maximum value of the region in x-axis.
   *
   * @return the maximum value of the region in x-axis.
   */
  public float getMaxX() {
    return maxX;
  }

  /**
   * Sets the maximum value of the region in x-axis.
   *
   * @param maxX a minimum value of the region in x-axis.
   */
  public void setMaxX(float maxX) {
    this.maxX = maxX;
  }

  /**
   * Returns the minimum value of the region in y-axis.
   *
   * @return the minimum value of the region in y-axis.
   */
  public float getMinY() {
    return minY;
  }

  /**
   * Sets the minimum value of the region in y-axis.
   *
   * @param minY a minimum value of the region in y-axis.
   */
  public void setMinY(float minY) {
    this.minY = minY;
  }

  /**
   * Returns the maximum value of the region in y-axis.
   *
   * @return the maximum value of the region in y-axis.
   */
  public float getMaxY() {
    return maxY;
  }

  /**
   * Sets the maximum value of the region in y-axis.
   *
   * @param maxY a maximum value of the region in y-axis.
   */
  public void setMaxY(float maxY) {
    this.maxY = maxY;
  }

  /**
   * Do linear transformation on destination region.
   *
   * @param dst destination region
   * @param stroke stroke to transform
   * @return new stroke on destination region
   */
  public Stroke transformStroke(Region dst, Stroke stroke) {
    Stroke transformed = stroke.copy();
    transformed.setStartPoint(transformPoint2D(dst, stroke.getStartPoint()));
    transformed.setEndPoint(transformPoint2D(dst, stroke.getEndPoint()));
    transformed.setControlPoints(new ArrayList<Point2D>());
    for (Point2D controlPoint : stroke.getControlPoints()) {
      transformed.addControlPoint(transformPoint2D(dst, controlPoint));
    }

    return transformed;
  }

  /**
   * De linear transformation on destination region.
   *
   * @param dst destination region
   * @param point point to transform
   * @return new point on destination region
   */
  public Point2D transformPoint2D(Region dst, Point2D point) {
    Point2D baseMinPoint = getMinPoint();
    Point2D baseDiffPoint = getMaxPoint().sub(baseMinPoint);
    Point2D targetMinPoint = dst.getMinPoint();
    Point2D targetDiffPoint = dst.getMaxPoint().sub(targetMinPoint);

    return point.sub(baseMinPoint)
        .scaleX(targetDiffPoint.getX() / baseDiffPoint.getX())
        .scaleY(targetDiffPoint.getY() / baseDiffPoint.getY())
        .add(targetMinPoint);
  }

  @Override
  public String toString() {
    return String.format("X: [%f, %f] Y:[%f, %f]", minX, maxX, minY, maxY);
  }
}
