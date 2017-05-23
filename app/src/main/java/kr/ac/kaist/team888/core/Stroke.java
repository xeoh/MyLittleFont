package kr.ac.kaist.team888.core;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Stroke represent Bézier curve and properties of Bézier curve.
 *
 * <p> See more about Bézier curve from this
 * <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">link</a>
 */
public class Stroke {
  private Vector2D startPoint;
  private Vector2D endPoint;
  private ArrayList<Vector2D> controlPoints;
  private boolean isJoint = false;

  private Stroke(Vector2D startPoint, Vector2D endPoint, ArrayList<Vector2D> controlPoints) {
    this.startPoint = startPoint;
    this.controlPoints = controlPoints;
    this.endPoint = endPoint;
  }

  /**
   * Returns the minimum value in x-axis among the start, the end, and the control points.
   *
   * @return the minimum value of the stroke in x-axis.
   */
  public double getMinX() {
    if (controlPoints.isEmpty()) {
      return Math.min(startPoint.getX(), endPoint.getX());
    } else {
      ArrayList<Double> controlPointsX = new ArrayList<>();
      for (Vector2D point : controlPoints) {
        controlPointsX.add(point.getX());
      }

      double controlMinX = Collections.min(controlPointsX);

      return Math.min(Math.min(startPoint.getX(), endPoint.getX()), controlMinX);
    }
  }

  /**
   * Returns the maximum value in x-axis among the start, the end, and the control points.
   *
   * @return the maximum value of the stroke in x-axis.
   */
  public double getMaxX() {
    if (controlPoints.isEmpty()) {
      return Math.max(startPoint.getX(), endPoint.getX());
    } else {
      ArrayList<Double> controlPointsX = new ArrayList<>();
      for (Vector2D point : controlPoints) {
        controlPointsX.add(point.getX());
      }

      double controlMaxX = Collections.max(controlPointsX);

      return Math.max(Math.max(startPoint.getX(), endPoint.getX()), controlMaxX);
    }
  }

  /**
   * Returns the minimum value in y-axis among the start, the end, and the control points.
   *
   * @return the minimum value of the stroke in y-axis.
   */
  public double getMinY() {
    if (controlPoints.isEmpty()) {
      return Math.min(startPoint.getY(), endPoint.getY());
    } else {
      ArrayList<Double> controlPointsY = new ArrayList<>();
      for (Vector2D point : controlPoints) {
        controlPointsY.add(point.getY());
      }

      double controlMinY = Collections.min(controlPointsY);

      return Math.min(Math.min(startPoint.getY(), endPoint.getY()), controlMinY);
    }
  }

  /**
   * Returns the maximum value in y-axis among the start, the end, and the control points.
   *
   * @return the maximum value of the stroke in y-axis.
   */
  public double getMaxY() {
    if (controlPoints.isEmpty()) {
      return Math.max(startPoint.getY(), endPoint.getY());
    } else {
      ArrayList<Double> controlPointsY = new ArrayList<>();
      for (Vector2D point : controlPoints) {
        controlPointsY.add(point.getY());
      }

      double controlMaxY = Collections.max(controlPointsY);

      return Math.max(Math.max(startPoint.getY(), endPoint.getY()), controlMaxY);
    }
  }

  /**
   * Getter of startPoint.
   *
   * @return startPoint of be
   */
  public Vector2D getStartPoint() {
    return startPoint;
  }

  /**
   * Setter of start Point.
   *
   * @param startPoint startPoint to set
   */
  public void setStartPoint(Vector2D startPoint) {
    this.startPoint = startPoint;
  }

  /**
   * Getter of endPoint.
   *
   * @return endPoint
   */
  public Vector2D getEndPoint() {
    return endPoint;
  }

  /**
   * Setter of endPoint.
   *
   * @param endPoint endPoint to set
   */
  public void setEndPoint(Vector2D endPoint) {
    this.endPoint = endPoint;
  }

  /**
   * Getter of controlPoints.
   *
   * @return controlPoints
   */
  public ArrayList<Vector2D> getControlPoints() {
    return controlPoints;
  }

  /**
   * Setter of controlPoints.
   *
   * @param controlPoints controlPoints to set
   */
  public void setControlPoints(ArrayList<Vector2D> controlPoints) {
    this.controlPoints = controlPoints;
  }

  /**
   * Add control point.
   *
   * @param controlPoint point to add
   */
  public void addControlPoint(Vector2D controlPoint) {
    controlPoints.add(controlPoint);
  }

  /**
   * Setter of isJoint.
   *
   * @param isJoint true: this stroke is joint type, false: this stroke is not joint type
   */
  public void setJoint(boolean isJoint) {
    this.isJoint = isJoint;
  }

  /**
   * Getter of isJoint.
   *
   * @return true: when stroke is joint type, false: when stroke is not joint type
   */
  public boolean isJoint() {
    return isJoint;
  }

  /**
   * Copy stroke but with different inner objects.
   *
   * @return copy of current stroke
   */
  public Stroke copy() {
    Stroke copy = new Stroke(new Vector2D(1, startPoint), new Vector2D(1, endPoint),
        new ArrayList<Vector2D>());
    for (Vector2D controlPoint : controlPoints) {
      copy.addControlPoint(new Vector2D(1, controlPoint));
    }
    copy.setJoint(isJoint);
    return copy;
  }

  @Override
  public String toString() {
    String rtn = "[" + startPoint;
    for (Vector2D ctrPoint : controlPoints) {
      rtn += "->" + ctrPoint.toString();
    }
    rtn += "->" + endPoint + "]";
    return rtn;
  }

  /**
   * Builder of {@link Stroke} class.
   */
  public static class StrokeBuilder {
    private Vector2D startPoint;
    private Vector2D endPoint;
    private ArrayList<Vector2D> controlPoints = new ArrayList<>();
    private boolean isJoint = false;

    public StrokeBuilder setStartPoint(Vector2D startPoint) {
      this.startPoint = startPoint;
      return this;
    }

    public StrokeBuilder setEndPoint(Vector2D endPoint) {
      this.endPoint = endPoint;
      return this;
    }

    public StrokeBuilder addControlPoint(Vector2D controlPoint) {
      this.controlPoints.add(controlPoint);
      return this;
    }

    public StrokeBuilder setJoint(boolean isJoint) {
      this.isJoint = isJoint;
      return this;
    }

    /**
     * Build stroke.
     *
     * @return new Stroke
     */
    public Stroke build() {
      Stroke stroke = new Stroke(startPoint, endPoint, controlPoints);
      stroke.setJoint(isJoint);
      return stroke;
    }
  }
}
