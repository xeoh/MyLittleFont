package kr.ac.kaist.team888.bezier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;

import java.util.Arrays;

/**
 * Bezier curve represented by sequence of controlling points.
 */
public class BezierCurve extends ParametricPolynomialCurve {
  private static final BezierCurveOffsetMethodType DEFAULT_OFFSET_METHOD =
      BezierCurveOffsetMethodType.TillerHanson;
  private static final Vector2D DEFAULT_OFFSET_VECTOR = new Vector2D(1, 1);

  private Vector2D[] points;
  private int order;
  private BezierCurveOffsetMethodType offsetMethod;
  private Vector2D offsetVector = DEFAULT_OFFSET_VECTOR;
  private Vector2D endOffsetVector;

  /**
   * Creates a new Bezier curve with given controlling points and sets a default offset method.
   *
   * @param points controlling points in order
   * @param offsetMethod default offset method
   * @throws DimensionMismatchException if a size of some controlling point is not 2
   * @throws NullArgumentException if either the array of controlling points
   *     or one of controlling points is `null`
   * @throws NoDataException if the array of controlling points has less than two points
   */
  public BezierCurve(double[][] points, BezierCurveOffsetMethodType offsetMethod)
      throws DimensionMismatchException, NullArgumentException, NoDataException {
    // Validate input points.
    MathUtils.checkNotNull(points);
    final int order = points.length - 1;
    if (order < 1) {
      throw new NoDataException();
    }
    for (int i = 0; i <= order; i++) {
      MathUtils.checkNotNull(points[i]);
      if (points[i].length != 2) {
        throw new DimensionMismatchException(points[i].length, 2);
      }
    }

    // Make a vector for each point.
    Vector2D[] vectorizedPoints = new Vector2D[order + 1];
    for (int i = 0; i <= order; i++) {
      vectorizedPoints[i] = new Vector2D(points[i]);
    }

    // Calculate coefficients.
    double[][] coefficients = calculateCoefficients(vectorizedPoints);

    // Save variables.
    this.points = vectorizedPoints;
    this.polynomials = new PolynomialFunction[2];
    this.polynomials[0] = new PolynomialFunction(coefficients[0]);
    this.polynomials[1] = new PolynomialFunction(coefficients[1]);
    this.order = order;
    this.offsetMethod = offsetMethod;
  }

  /**
   * Creates a new Bezier curve with given controlling points.
   *
   * @param points controlling points in order
   * @throws DimensionMismatchException if a size of some controlling point is not 2
   * @throws NullArgumentException if either the array of controlling points
   *     or one of controlling points is `null`
   * @throws NoDataException if the array of controlling points has less than two points
   */
  public BezierCurve(double[][] points)
      throws DimensionMismatchException, NullArgumentException, NoDataException {
    this(points, DEFAULT_OFFSET_METHOD);
  }

  /**
   * Creates a new Bezier curve with given controlling points and sets a default offset method.
   *
   * @param points controlling points in order
   * @param offsetMethod default offset method
   * @throws NullArgumentException if either the array of controlling points
   *     or one of controlling points is `null`
   * @throws NoDataException if the array of controlling points has less than two points
   */
  public BezierCurve(Vector2D[] points, BezierCurveOffsetMethodType offsetMethod)
      throws NullArgumentException, NoDataException {
    // Validate input points.
    MathUtils.checkNotNull(points);
    final int order = points.length - 1;
    if (order < 1) {
      throw new NoDataException();
    }
    for (Vector2D point : points) {
      MathUtils.checkNotNull(point);
    }

    // Copy points.
    Vector2D[] copiedPoints = points.clone();
    for (int i = 0; i <= order; i++) {
      copiedPoints[i] = new Vector2D(1, copiedPoints[i]);
    }

    // Calculate coefficients.
    double[][] coefficients = calculateCoefficients(copiedPoints);

    // Save variables.
    this.points = copiedPoints;
    this.polynomials = new PolynomialFunction[2];
    this.polynomials[0] = new PolynomialFunction(coefficients[0]);
    this.polynomials[1] = new PolynomialFunction(coefficients[1]);
    this.order = order;
    this.offsetMethod = offsetMethod;
  }

  /**
   * Creates a new Bezier curve with given controlling points.
   *
   * @param points controlling points in order
   * @throws NullArgumentException if either the array of controlling points
   *     or one of controlling points is `null`
   * @throws NoDataException if the array of controlling points has less than two points
   */
  public BezierCurve(Vector2D[] points)
      throws DimensionMismatchException, NullArgumentException, NoDataException {
    this(points, DEFAULT_OFFSET_METHOD);
  }

  private double[][] calculateCoefficients(Vector2D[] points) {
    // Get binomial coefficients.
    long[][] binomials = new long[points.length][points.length];
    for (int i = 0; i < points.length; i++) {
      for (int j = 0; j <= i; j++) {
        if (j == 0 || j == i) {
          binomials[i][j] = 1;
        } else {
          binomials[i][j] = binomials[i - 1][j - 1] + binomials[i - 1][j];
        }
      }
    }

    // Get polynomial coefficients.
    double[][] coefficients = new double[2][points.length];
    for (int i = 0; i < points.length; i++) {
      for (int j = 0; j <= i; j++) {
        coefficients[0][i] += -(2 * ((i + j) % 2) - 1) * binomials[i][j] * points[j].getX();
        coefficients[1][i] += -(2 * ((i + j) % 2) - 1) * binomials[i][j] * points[j].getY();
      }
      coefficients[0][i] *= binomials[points.length - 1][i];
      coefficients[1][i] *= binomials[points.length - 1][i];
    }

    return coefficients;
  }

  /**
   * Returns the array of controlling points.
   *
   * <p>This methods copies the array and returns it.
   *
   * @return the array of controlling points
   */
  public Vector2D[] getPoints() {
    Vector2D[] points = new Vector2D[this.points.length];
    for (int i = 0; i < points.length; i++) {
      points[i] = new Vector2D(1, this.points[i]);
    }
    return points;
  }

  /**
   * Returns the controlling point at given index.
   *
   * @param index index of the target controlling point
   * @return the controlling point at given index
   */
  public Vector2D getPoint(int index) {
    if (index < 0 || index >= points.length) {
      throw new OutOfRangeException(index, 0, points.length - 1);
    }

    return new Vector2D(1, points[index]);
  }

  /**
   * Sets a point at given position with given point.
   *
   * @param index position
   * @param point a new point
   * @throws OutOfRangeException if position is invalid
   */
  public void setPoint(int index, Vector2D point) throws OutOfRangeException {
    if (index < 0 || index > order) {
      throw new OutOfRangeException(index, 0, order);
    }
    MathUtils.checkNotNull(point);
    points[index] = new Vector2D(1, point);

    double[][] coefficients = calculateCoefficients(points);

    this.polynomials = new PolynomialFunction[2];
    this.polynomials[0] = new PolynomialFunction(coefficients[0]);
    this.polynomials[1] = new PolynomialFunction(coefficients[1]);
  }

  /**
   * Gets the start point.
   *
   * @return the start point
   */
  public Vector2D getStartPoint() {
    return new Vector2D(1, points[0]);
  }

  /**
   * Sets the start point.
   *
   * @param point a new start point
   */
  public void setStartPoint(Vector2D point) {
    setPoint(0, new Vector2D(1, point));
  }

  /**
   * Gets the end point.
   *
   * @return the end point
   */
  public Vector2D getEndPoint() {
    return new Vector2D(1, points[order]);
  }

  /**
   * Sets the end point.
   *
   * @param point a new end point
   */
  public void setEndPoint(Vector2D point) {
    setPoint(order, new Vector2D(1, point));
  }

  /**
   * Gets the array of control points.
   *
   * <p>This excepts start and end point.
   *
   * @return the array of control points
   */
  public Vector2D[] getControlPoints() {
    Vector2D[] controlPoints = new Vector2D[order - 1];
    for (int i = 0; i < controlPoints.length; i++) {
      controlPoints[i] = points[i + 1];
    }
    return controlPoints;
  }

  /**
   * Returns the order of the Bezier curve.
   *
   * @return the order of the Bezier curve
   */
  public int getOrder() {
    return order;
  }

  /**
   * Returns the offset vector of the Bezier curve.
   *
   * @return the offset vector of the Bezier curve
   */
  public Vector2D getOffsetVector() {
    return offsetVector;
  }

  /**
   * Sets the offset vector of the Bezier curve.
   *
   * @param offsetVector the offset vector
   */
  public void setOffsetVector(Vector2D offsetVector) {
    this.offsetVector = offsetVector;
  }

  /**
   * Returns the end offset vector of the Bezier curve.
   *
   * @return the end offset vector of the Bezier curve
   */
  public Vector2D getEndOffsetVector() {
    return endOffsetVector;
  }

  /**
   * Sets the end offset vector of the Bezier curve.
   *
   * @param offsetVector the offset vector
   */
  public void setEndOffsetVector(Vector2D offsetVector) {
    this.endOffsetVector = offsetVector;
  }

  /**
   * Returns the offset vector for a point at the given index.
   *
   * @param index an index of point
   * @return the offset vector
   */
  public Vector2D getOffsetVector(int index) {
    if (endOffsetVector == null) {
      return offsetVector;
    }
    return offsetVector.scalarMultiply((order - index) / (double) order)
        .add(index / (double) order, endOffsetVector);
  }

  /**
   * Returns whether every points are collapsed or not.
   *
   * @return true if every points are collapsed, false otherwise
   */
  public boolean isCollapsed() {
    for (Vector2D point : points) {
      if (!points[0].equals(point)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Split the Bezier curve into two Bezier curves of same order respective to given time.
   *
   * <p>This is calculated by the
   * <a href="https://en.wikipedia.org/wiki/De_Casteljau's_algorithm">De Casteljau's algorithm</a>.
   *
   * @param time a time at which be split
   * @return Two split Bezier curves, 0 to time is at index 0, time to 1 is at index 1
   */
  public BezierCurve[] split(double time) {
    Vector2D[][] beta = new Vector2D[order + 1][];
    beta[0] = new Vector2D[order + 1];
    for (int j = 0; j <= order; j++) {
      beta[0][j] = points[j];
    }
    for (int i = 1; i <= order; i++) {
      beta[i] = new Vector2D[order - i + 1];
      for (int j = 0; j <= order - i; j++) {
        beta[i][j] = new Vector2D(1 - time, beta[i - 1][j], time, beta[i - 1][j + 1]);
      }
    }
    Vector2D[][] points = new Vector2D[2][order + 1];
    for (int i = 0; i <= order; i++) {
      points[0][i] = beta[i][0];
      points[1][i] = beta[order - i][i];
    }
    BezierCurve[] curves = new BezierCurve[] {
        new BezierCurve(points[0]),
        new BezierCurve(points[1])
    };
    for (BezierCurve curve : curves) {
      curve.setOffsetVector(offsetVector);
    }
    return curves;
  }

  /**
   * Returns an offset Bezier curve of the original Bezier curve by given distance
   * with using given offset method.
   *
   * <p>This methods gives a new object of Bezier curve and does not modify the original.
   *
   * @param delta distance to offset
   * @param offsetMethod offset method to apply
   * @return a new offset Bezier curve
   */
  public BezierCurve offset(double delta, double contrast,
                            BezierCurveOffsetMethodType offsetMethod) {
    switch (offsetMethod) {
      case TillerHanson:
        return OffsetTillerHanson.offset(this, delta, contrast);
      default:
        return null;
    }
  }

  /**
   * Returns an offset Bezier curve of the original Bezier curve by given distance
   * with using a default offset method.
   *
   * <p>If a default offset method is not set, then it just applies the Tiller-Hanson method.
   *
   * <p>This methods gives a new object of Bezier curve and does not modify the original.
   *
   * @param delta distance to offset
   * @return a new offset Bezier curve
   */
  public BezierCurve offset(double delta, double contrast) {
    return offset(delta, contrast, offsetMethod);
  }

  /**
   * Returns a reversed Bezier curve respective to the original Bezier curve,
   * i.e., Bezier curve with the same controlling points but in reverse order.
   *
   * <p>This methods gives a new object of Bezier curve and does not modify the original.
   *
   * @return a reversed Bezier curve
   */
  public BezierCurve reverse() {
    Vector2D[] points = getPoints();
    ArrayUtils.reverse(points);
    BezierCurve curve = new BezierCurve(points);
    curve.setOffsetVector(offsetVector);
    return curve;
  }

  @Override
  public BezierCurve clone() {
    BezierCurve curve = new BezierCurve(points, offsetMethod);
    curve.setOffsetVector(offsetVector);
    return curve;
  }

  @Override
  public String toString() {
    return "Bezier Curve of order " + order + ": " + Arrays.toString(points);
  }

  public static class Builder {
    private Vector2D[] points;
    private Vector2D offsetVector;
    private Vector2D endOffsetVector;

    /**
     * Sets the points.
     *
     * @param points points
     * @return this builder, useful for chaining
     */
    public Builder setPoints(Vector2D[] points) {
      this.points = points;
      return this;
    }

    /**
     * Sets the offset vector.
     *
     * @param offsetVector offset vector
     * @return this builder, useful for chaining
     */
    public Builder setOffsetVector(Vector2D offsetVector) {
      this.offsetVector = offsetVector;
      return this;
    }

    /**
     * Sets the end offset vector.
     *
     * @param endOffsetVector end offset vector
     * @return this builder, useful for chaining
     */
    public Builder setEndOffsetVector(Vector2D endOffsetVector) {
      this.endOffsetVector = endOffsetVector;
      return this;
    }

    /**
     * Build the {@link BezierCurve} after options have been set.
     *
     * @return the newly constructed {@link BezierCurve} object
     */
    public BezierCurve build() {
      BezierCurve curve = new BezierCurve(points);
      curve.setOffsetVector(offsetVector);
      curve.setEndOffsetVector(endOffsetVector);
      return curve;
    }
  }
}