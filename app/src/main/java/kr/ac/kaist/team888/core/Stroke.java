package kr.ac.kaist.team888.core;

/**
 * Stroke represent Quadratic Bézier curves and properties related to transformation of font.
 *
 * <p> <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/B%C3%A9zier_2_big.svg/360px-B%C3%A9zier_2_big.svg.png"/>
 *
 * <p> Terms for each controlPoints (p0, p1, p2).
 *
 *   <ul> p0: startPoint</ul>
 *   <ul> p1: controlPoint</ul>
 *   <ul> p2: endPoint</ul>
 *
 * <p>Stroke can be constructed by Json data.
 *
 * <p> Json Format is like this<br>
 * <pre>
 * "Stroke": {
 *   "startPoint": {
 *     "x": value,
 *     "y": value
 *   },
 *   "endPoint": {
 *     "x": value,
 *     "y": value
 *   },
 *   "controlPoint": {
 *     "x": value,
 *     "y": value
 *   }
 * }
 * </pre>
 *
 * <h4> Construct using Gson </h4>
 * <pre>
 * String jsonData = "{ \"startPoint\" : { \"x\": ...";
 * JsonElement json = gson.fromJson(jsonData, JsonElement.class);
 * Stroke stroke = gson.fromJson(json, Stroke.class);
 * </pre>
 *
 * <h4> Construct from builder </h4>
 * <pre>
 * Stroke stroke = new Stroke.StrokeBuilder()
 *     .setStartPoint(new Point2D(1,1))
 *     .setEndPoint(new Point2D(3,1))
 *     .setControlPoint(new Point2D(2,1))
 *     .build();
 * </pre>
 */
public class Stroke {
  private Point2D startPoint;
  private Point2D endPoint;
  private Point2D controlPoint;

  private Stroke(Point2D startPoint, Point2D controlPoint, Point2D endPoint) {
    this.startPoint = startPoint;
    this.controlPoint = controlPoint;
    this.endPoint = endPoint;
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
   * Getter of controlPoint.
   *
   * @return controlPoint
   */
  public Point2D getControlPoint() {
    return controlPoint;
  }

  /**
   * Setter of controlPoint.
   *
   * @param controlPoint controlPoint ot set
   */
  public void setControlPoint(Point2D controlPoint) {
    this.controlPoint = controlPoint;
  }

  @Override
  public String toString() {
    return "[" + startPoint + " -> " + controlPoint + " -> " + endPoint + "]";
  }

  /**
   * Builder of {@link Stroke} class.
   */
  public static class StrokeBuilder {
    private Point2D startPoint;
    private Point2D endPoint;
    private Point2D controlPoint;

    public StrokeBuilder setStartPoint(Point2D startPoint) {
      this.startPoint = startPoint;
      return this;
    }

    public StrokeBuilder setEndPoint(Point2D endPoint) {
      this.endPoint = endPoint;
      return this;
    }

    public StrokeBuilder setControlPoint(Point2D controlPoint) {
      this.controlPoint = controlPoint;
      return this;
    }

    public Stroke build() {
      return new Stroke(startPoint, endPoint, controlPoint);
    }
  }
}
