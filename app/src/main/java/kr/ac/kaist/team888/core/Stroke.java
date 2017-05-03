package kr.ac.kaist.team888.core;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Stroke represent Bézier curve and properties of Bézier curve.
 *
 * <p> See more about Bézier curve from this
 * <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">link</a>
 */
public class Stroke {
  private Point2D startPoint;
  private Point2D endPoint;
  private ArrayList<Point2D> controlPoints;

  private Stroke(Point2D startPoint, Point2D endPoint, ArrayList<Point2D> controlPoints) {
    this.startPoint = startPoint;
    this.controlPoints = controlPoints;
    this.endPoint = endPoint;
  }

  /**
   * Returns the minimum value in x-axis among the start, the end, and the control points.
   *
   * @return the minimum value of the stroke in x-axis.
   */
  public float getMinX() {
    if (controlPoints.isEmpty()) {
      return Math.min(startPoint.getX(), endPoint.getX());
    } else {
      ArrayList<Float> controlPointsX = new ArrayList<>();
      for (Point2D point : controlPoints) {
        controlPointsX.add(point.getX());
      }

      float controlMinX = Collections.min(controlPointsX);

      return Math.min(Math.min(startPoint.getX(), endPoint.getX()), controlMinX);
    }
  }

  /**
   * Returns the maximum value in x-axis among the start, the end, and the control points.
   *
   * @return the maximum value of the stroke in x-axis.
   */
  public float getMaxX() {
    if (controlPoints.isEmpty()) {
      return Math.max(startPoint.getX(), endPoint.getX());
    } else {
      ArrayList<Float> controlPointsX = new ArrayList<>();
      for (Point2D point : controlPoints) {
        controlPointsX.add(point.getX());
      }

      float controlMaxX = Collections.max(controlPointsX);

      return Math.max(Math.max(startPoint.getX(), endPoint.getX()), controlMaxX);
    }
  }

  /**
   * Returns the minimum value in y-axis among the start, the end, and the control points.
   *
   * @return the minimum value of the stroke in y-axis.
   */
  public float getMinY() {
    if (controlPoints.isEmpty()) {
      return Math.min(startPoint.getY(), endPoint.getY());
    } else {
      ArrayList<Float> controlPointsY = new ArrayList<>();
      for (Point2D point : controlPoints) {
        controlPointsY.add(point.getY());
      }

      float controlMinY = Collections.min(controlPointsY);

      return Math.min(Math.min(startPoint.getY(), endPoint.getY()), controlMinY);
    }
  }

  /**
   * Returns the maximum value in y-axis among the start, the end, and the control points.
   *
   * @return the maximum value of the stroke in y-axis.
   */
  public float getMaxY() {
    if (controlPoints.isEmpty()) {
      return Math.max(startPoint.getY(), endPoint.getY());
    } else {
      ArrayList<Float> controlPointsY = new ArrayList<>();
      for (Point2D point : controlPoints) {
        controlPointsY.add(point.getY());
      }

      float controlMaxY = Collections.max(controlPointsY);

      return Math.max(Math.max(startPoint.getY(), endPoint.getY()), controlMaxY);
    }
  }

  /**
   * Getter of startPoint.
   *
   * @return startPoint of be
   */
  public Point2D getStartPoint() {
    return startPoint;
  }

  /**
   * Setter of start Point.
   *
   * @param startPoint startPoint to set
   */
  public void setStartPoint(Point2D startPoint) {
    this.startPoint = startPoint;
  }

  /**
   * Getter of endPoint.
   *
   * @return endPoint
   */
  public Point2D getEndPoint() {
    return endPoint;
  }

  /**
   * Setter of endPoint.
   *
   * @param endPoint endPoint to set
   */
  public void setEndPoint(Point2D endPoint) {
    this.endPoint = endPoint;
  }

  /**
   * Getter of controlPoints.
   *
   * @return controlPoints
   */
  public ArrayList<Point2D> getControlPoints() {
    return controlPoints;
  }

  /**
   * Setter of controlPoints.
   *
   * @param controlPoints controlPoints to set
   */
  public void setControlPoints(ArrayList<Point2D> controlPoints) {
    this.controlPoints = controlPoints;
  }

  @Override
  public String toString() {
    String rtn = "[" + startPoint;
    for (Point2D ctrPoint : controlPoints) {
      rtn += "->" + ctrPoint.toString();
    }
    rtn += "->" + endPoint + "]";
    return rtn;
  }

  /**
   * Builder of {@link Stroke} class.
   */
  public static class StrokeBuilder {
    private Point2D startPoint;
    private Point2D endPoint;
    private ArrayList<Point2D> controlPoints = new ArrayList<>();

    public StrokeBuilder setStartPoint(Point2D startPoint) {
      this.startPoint = startPoint;
      return this;
    }

    public StrokeBuilder setEndPoint(Point2D endPoint) {
      this.endPoint = endPoint;
      return this;
    }

    public StrokeBuilder addControlPoint(Point2D controlPoint) {
      this.controlPoints.add(controlPoint);
      return this;
    }

    public Stroke build() {
      return new Stroke(startPoint, endPoint, controlPoints);
    }
  }
}
