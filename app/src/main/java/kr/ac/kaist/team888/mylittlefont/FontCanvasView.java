package kr.ac.kaist.team888.mylittlefont;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import kr.ac.kaist.team888.character.Character;
import kr.ac.kaist.team888.core.Point2D;
import kr.ac.kaist.team888.core.Stroke;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class FontCanvasView extends View {
  private Path path;
  private Paint paint;

  private int width;
  private int height;
  private Point2D srcMin;
  private Point2D srcMax;
  private Point2D dstMin;
  private Point2D dstMax;
  private double[][] dataV;

  private ArrayList<Stroke> strokes;

  public FontCanvasView(Context context) {
    super(context);
    initialize();
  }

  public FontCanvasView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  public FontCanvasView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize() {
    path = new Path();

    paint = new Paint();
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(1f);

    srcMin = new Point2D(Character.X_RANGE_MIN, Character.Y_RANGE_MIN);
    srcMax = new Point2D(Character.X_RANGE_MAX, Character.Y_RANGE_MAX);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    width = getWidth();
    height = getHeight();

    setDstCoordinate();
    setDataV(srcMin, srcMax, dstMin, dstMax);
    path.rewind();
    for (Stroke stroke : strokes) {
      strokeToPath(stroke);
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    canvas.drawPath(path, paint);
  }

  private void setDstCoordinate() {
    if (height > width) {
      float val = width * (Character.Y_RANGE_MAX - Character.Y_RANGE_MIN)
          / (Character.X_RANGE_MAX - Character.X_RANGE_MIN);

      dstMin = new Point2D(0, (height - val) / 2 + val);
      dstMax = new Point2D(width, (height - val) / 2);
    } else {
      float val = height * (Character.X_RANGE_MAX - Character.X_RANGE_MIN)
          / (Character.Y_RANGE_MAX - Character.Y_RANGE_MIN);

      dstMin = new Point2D((width - val) / 2, height);
      dstMax = new Point2D((width - val) / 2  + val, 0);
    }
  }

  private void setDataV(Point2D originMin, Point2D originMax,
                        Point2D newMin, Point2D newMax) {
    double[][] dataM = {
        { originMin.getX(), originMin.getY(), 1, 0},
        {-originMin.getY(), originMin.getX(), 0, 1},
        { originMax.getX(), originMax.getY(), 1, 0},
        {-originMax.getY(), originMax.getX(), 0, 1},
    };
    RealMatrix matrixM = MatrixUtils.createRealMatrix(dataM);
    RealMatrix invMatrixM = new LUDecomposition(matrixM).getSolver().getInverse();

    double[][] dataU = {
        {newMin.getX()},
        {newMin.getY()},
        {newMax.getX()},
        {newMax.getY()}
    };
    RealMatrix matrixU = MatrixUtils.createRealMatrix(dataU);

    RealMatrix matrixV = invMatrixM.multiply(matrixU);
    dataV = matrixV.getData();
  }

  private void strokeToPath(Stroke stroke) {
    Point2D startPoint = transform(stroke.getStartPoint());
    Point2D controlPoint = transform(stroke.getControlPoint());
    Point2D endPoint = transform(stroke.getEndPoint());

    path.moveTo(startPoint.getX(), startPoint.getY());
    path.quadTo(controlPoint.getX(), controlPoint.getY(), endPoint.getX(), endPoint.getY());
  }

  private Point2D transform(Point2D srcPoint) {
    float dstX = (float)(dataV[0][0] * srcPoint.getX()
        + dataV[1][0] * srcPoint.getY()
        + dataV[2][0]);
    float dstY = (float)(dataV[1][0] * srcPoint.getX()
        - dataV[0][0] * srcPoint.getY()
        + dataV[3][0]);

    return new Point2D(dstX, dstY);
  }

  /**
   * Set strokes to be drawn on canvas.
   *
   * @param strokes list of strokes.
   */
  public void drawStrokes(ArrayList<Stroke> strokes) {
    this.strokes = strokes;
    this.invalidate();
  }
}
