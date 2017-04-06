package kr.ac.kaist.team888.mylittlefont;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import kr.ac.kaist.team888.core.Point2D;
import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class FontCanvasView extends View {
  private ArrayList<ArrayList<Stroke>> outerStrokes;
  private ArrayList<ArrayList<Stroke>> innerStrokes;
  private ArrayList<Path> outerPaths;
  private ArrayList<Path> innerPaths;
  private Paint outerPaint;
  private Paint innerPaint;
  private Paint xrayPaint;
  private Paint pointPaint;
  private Paint controlPaint;

  private boolean xrayView = false;
  private float pointRadius = 4f;

  private int width;
  private int height;
  private Point2D srcMin;
  private Point2D srcMax;
  private Point2D dstMin;
  private Point2D dstMax;
  private double[][] dataV;

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
    outerPaths = new ArrayList<>();
    innerPaths = new ArrayList<>();

    outerPaint = new Paint();
    outerPaint.setColor(Color.BLACK);
    outerPaint.setStyle(Paint.Style.FILL);

    innerPaint = new Paint();
    innerPaint.setColor(Color.WHITE);
    innerPaint.setStyle(Paint.Style.FILL);

    xrayPaint = new Paint();
    xrayPaint.setColor(Color.BLACK);
    xrayPaint.setStyle(Paint.Style.STROKE);
    xrayPaint.setStrokeWidth(1f);

    pointPaint = new Paint();
    pointPaint.setColor(Color.BLUE);
    pointPaint.setStyle(Paint.Style.FILL);

    controlPaint = new Paint();
    controlPaint.setColor(Color.GREEN);
    controlPaint.setStyle(Paint.Style.FILL);

    srcMin = new Point2D(HangulCharacter.X_RANGE_MIN, HangulCharacter.Y_RANGE_MIN);
    srcMax = new Point2D(HangulCharacter.X_RANGE_MAX, HangulCharacter.Y_RANGE_MAX);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    width = getWidth();
    height = getHeight();

    setDstCoordinate();
    setDataV(srcMin, srcMax, dstMin, dstMax);

    outerPaths.clear();
    innerPaths.clear();
    if (outerStrokes != null) {
      for (ArrayList<Stroke> strokes : outerStrokes) {
        strokeToPath(strokes, outerPaths);
      }
    }

    if (innerStrokes != null) {
      for (ArrayList<Stroke> strokes : innerStrokes) {
        strokeToPath(strokes, innerPaths);
      }
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    Paint outerPaint;
    Paint innerPaint;

    if (xrayView) {
      outerPaint = this.xrayPaint;
      innerPaint = this.xrayPaint;
    } else {
      outerPaint = this.outerPaint;
      innerPaint = this.innerPaint;
    }

    for (Path path : outerPaths) {
      canvas.drawPath(path, outerPaint);
    }

    for (Path path : innerPaths) {
      canvas.drawPath(path, innerPaint);
    }

    if (outerStrokes != null && xrayView) {
      for (ArrayList<Stroke> strokes : outerStrokes) {
        drawPoints(strokes, canvas);
      }
    }

    if (innerStrokes != null && xrayView) {
      for (ArrayList<Stroke> strokes : innerStrokes) {
        drawPoints(strokes, canvas);
      }
    }
  }

  private void setDstCoordinate() {
    if (height > width) {
      float val = width * (HangulCharacter.Y_RANGE_MAX - HangulCharacter.Y_RANGE_MIN)
          / (HangulCharacter.X_RANGE_MAX - HangulCharacter.X_RANGE_MIN);

      dstMin = new Point2D(0, (height - val) / 2 + val);
      dstMax = new Point2D(width, (height - val) / 2);
    } else {
      float val = height * (HangulCharacter.X_RANGE_MAX - HangulCharacter.X_RANGE_MIN)
          / (HangulCharacter.Y_RANGE_MAX - HangulCharacter.Y_RANGE_MIN);

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

  private void strokeToPath(ArrayList<Stroke> strokes, ArrayList<Path> container) {
    Path curPath = new Path();
    Point2D startPoint = transform(strokes.get(0).getStartPoint());
    curPath.moveTo(startPoint.getX(), startPoint.getY());

    for (Stroke stroke : strokes) {
      Point2D controlPoint = transform(stroke.getControlPoint());
      Point2D endPoint = transform(stroke.getEndPoint());

      curPath.quadTo(controlPoint.getX(), controlPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    container.add(curPath);
  }

  private void drawPoints(ArrayList<Stroke> strokes, Canvas canvas) {
    Point2D startPoint = transform(strokes.get(0).getStartPoint());
    canvas.drawCircle(startPoint.getX(), startPoint.getY(), pointRadius, pointPaint);

    for (Stroke stroke : strokes) {
      Point2D controlPoint = transform(stroke.getControlPoint());
      Point2D endPoint = transform(stroke.getEndPoint());

      canvas.drawCircle(controlPoint.getX(), controlPoint.getY(), pointRadius, controlPaint);
      canvas.drawCircle(endPoint.getX(), endPoint.getY(), pointRadius, pointPaint);
    }
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
   * Draw given strokes on Canvas.
   *
   * @param outerStrokes outer strokes collection
   * @param innerStrokes inner strokes collection
   */
  public void drawStrokes(ArrayList<ArrayList<Stroke>> outerStrokes,
                          ArrayList<ArrayList<Stroke>> innerStrokes) {
    this.outerStrokes = outerStrokes;
    this.innerStrokes = innerStrokes;

    invalidate();
  }

  /**
   * Set Xray View on/off.
   *
   * <p> On xray view - show basic strokes as lines and points circles.<br>
   * Off xray view - show filled font.
   *
   * @param on true: xray view on, false: xray view off
   */
  public void setXrayView(boolean on) {
    xrayView = on;

    invalidate();
  }
}
