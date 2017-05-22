package kr.ac.kaist.team888.util.bezier;

import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;

/**
 * Parametric curve in 2D plane represented by two polynomials of time.
 *
 * <p>The general form of this curve C is C(t)=[sum a_i t^i, sum b_i t^i]
 * where t, a_i, and b_i are real numbers. Thus, C is the function from R to R^2.
 */
public class ParametricPolynomialCurve implements UnivariateVectorFunction {
  protected PolynomialFunction[] polynomials;

  protected ParametricPolynomialCurve() {
  }

  /**
   * Creates a new parametric polynomial curve with given polynomials.
   *
   * <p>This restricts to receive exactly two polynomials, one is for x-axis
   * and other is for y-axis.
   *
   * @param polynomials an array of two polynomials for x-axis and y-axis, respectively
   * @throws DimensionMismatchException if number of polynomials is not 2
   * @throws NullArgumentException if either the array of polynomials
   *     or one of polynomial is `null`
   */
  public ParametricPolynomialCurve(PolynomialFunction[] polynomials)
      throws DimensionMismatchException, NullArgumentException {
    MathUtils.checkNotNull(polynomials);
    if (polynomials.length != 2) {
      throw new DimensionMismatchException(polynomials.length, 2);
    }
    for (int i = 0; i < polynomials.length; i++) {
      MathUtils.checkNotNull(polynomials[i]);
    }
    this.polynomials = polynomials;
  }

  /**
   * Returns the array of polynomials.
   *
   * <p>This methods copies the array and returns it.
   *
   * @return the array of polynomials
   */
  public PolynomialFunction[] getPolynomials() {
    return new PolynomialFunction[]{
        new PolynomialFunction(polynomials[0].getCoefficients()),
        new PolynomialFunction(polynomials[1].getCoefficients())
    };
  }

  /**
   * Returns the value at the given parameter.
   *
   * @param time parameter to compute the value
   * @return the value of the function
   */
  @Override
  public double[] value(double time) {
    return new double[]{polynomials[0].value(time), polynomials[1].value(time)};
  }

  /**
   * Returns the derivative of the curve.
   *
   * @return the derivative curve
   */
  public ParametricPolynomialCurve derivative() {
    PolynomialFunction[] polynomialDerivative = new PolynomialFunction[]{
        polynomials[0].polynomialDerivative(),
        polynomials[1].polynomialDerivative()
    };

    return new ParametricPolynomialCurve(polynomialDerivative);
  }

  /**
   * Returns the function in the form of {@link UnivariateVectorFunction}
   * which gives the perpendicular unit vector of the each point on the original curve.
   *
   * <p>Perpendicular direction is decided in counter-clockwise.
   *
   * @return the function for perpendicular vector of each point
   */
  public UnivariateVectorFunction perpendicular() {

    return new UnivariateVectorFunction() {
      @Override
      public double[] value(double time) {
        Vector2D unitVector = new Vector2D(ParametricPolynomialCurve.this.derivative().value(time))
            .normalize();
        return new double[]{unitVector.getY(), -unitVector.getX()};
      }
    };
  }

}
