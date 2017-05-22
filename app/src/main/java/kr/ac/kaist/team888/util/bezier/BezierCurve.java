package kr.ac.kaist.team888.util.bezier;

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

  private Vector2D[] points;
  private int order;
  private BezierCurveOffsetMethodType offsetMethod;

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
      copiedPoints[i] = new Vector2D(copiedPoints[i].toArray());
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
      points[i] = new Vector2D(this.points[i].toArray());
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

    return new Vector2D(points[index].toArray());
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
   * Returns an offset Bezier curve of the original Bezier curve by given distance
   * with using given offset method.
   *
   * <p>This methods gives a new object of Bezier curve and does not modify the original.
   *
   * @param delta distance to offset
   * @param offsetMethod offset method to apply
   * @return a new offset Bezier curve
   */
  public BezierCurve offset(double delta, BezierCurveOffsetMethodType offsetMethod) {
    switch (offsetMethod) {
      case TillerHanson:
        return OffsetTillerHanson.offset(this, delta);
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
  public BezierCurve offset(double delta) {
    return offset(delta, offsetMethod);
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
    return new BezierCurve(points);
  }

  @Override
  public String toString() {
    return "Bezier Curve of order " + order + ": " + Arrays.toString(points);
  }
}