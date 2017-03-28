package kr.ac.kaist.team888.core;

/**
 * Point2D is class for representing 2D coordinate value.
 */
public class Point2D {
  private float valX;
  private float valY;

  /**
   * Constructor of Point2D.
   *
   * @param valX x coordinate value
   * @param valY y coordinate value
   */
  public Point2D(float valX, float valY) {
    this.valX = valX;
    this.valY = valY;
  }

  /**
   * Getter of value x.
   *
   * @return value x
   */
  public float getX() {
    return valX;
  }

  /**
   * Getter of value y.
   *
   * @return value y
   */
  public float getY() {
    return valY;
  }

  /**
   * Add x and y values.
   *
   * @param valX value to add to x-coordinate
   * @param valY value to add to y-coordinate
   * @return new Point(x0 + x, y0 + y)
   */
  public Point2D add(float valX, float valY) {
    return new Point2D(this.valX + valX, this.valY + valY);
  }

  /**
   * Add each parameter's x and y values.
   *
   * @param point value to add
   * @return new Point(x0 + x, y0 + y)
   */
  public Point2D add(Point2D point) {
    return this.add(point.valX, point.valY);
  }

  /**
   * Subtract x and y values.
   *
   * @param valX value to subtract to x-coordinate
   * @param valY value to subtract to y-coordinate
   * @return new Point(x0 - x, y0 - y)
   */
  public Point2D sub(float valX, float valY) {
    return new Point2D(this.valX - valX, this.valY - valY);
  }

  /**
   * Subtract each parameter's x and y values.
   *
   * @param point value to subtract
   * @return new Point(x0 - x, y0 - y)
   */
  public Point2D sub(Point2D point) {
    return this.sub(point.valX, point.valY);
  }

  /**
   * Scalar multiplication on point2D with factor.
   *
   * @param factor value to multiply
   * @return new Point(x0 * k, y0 * k)
   */
  public Point2D scale(float factor) {
    return new Point2D(this.valX * factor, this.valY * factor);
  }

  /**
   * Scale only X coordinate value.
   *
   * @param factor value to scale
   * @return new Point(x0 * k, y0)
   */
  public Point2D scaleX(float factor) {
    return new Point2D(this.valX * factor, this.valY);
  }

  /**
   * Scale only Y coordinate value.
   *
   * @param factor value to scale
   * @return new Point(x0, y0 * k)
   */
  public Point2D scaleY(float factor) {
    return new Point2D(this.valX, this.valY * factor);
  }

  @Override
  public String toString() {
    return "(" + valX + ", " + valY + ")";
  }
}
