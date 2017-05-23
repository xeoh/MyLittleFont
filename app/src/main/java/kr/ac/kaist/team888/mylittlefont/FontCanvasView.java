package kr.ac.kaist.team888.mylittlefont;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.region.Region;
import kr.ac.kaist.team888.util.FeatureController;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class FontCanvasView extends View implements FeatureController.OnFeatureChangeListener {
  private static final float CANVAS_OFFSET_RATIO = 0.05f;
  private static final float FIXED_POINT_RADIUS = 4f;
  private static final float CONTROL_POINT_RADIUS = 4f;
  private static final int PRIORITY = 2;
  private Paint contourPaint;
  private Paint contourLayoutPaint;
  private Paint skeletonPaint;
  private Paint fixedPaint;
  private Paint controlPaint;
  private Locator locator;

  private Region canvasRegion = new Region(0, 0, 0, 0);

  private boolean skeletonView = false;

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
    contourPaint = new Paint();
    contourPaint.setColor(Color.BLACK);
    contourPaint.setStyle(Paint.Style.FILL);
    contourPaint.setStrokeWidth(1f);

    contourLayoutPaint = new Paint();
    contourLayoutPaint.setColor(Color.LTGRAY);
    contourLayoutPaint.setStyle(Paint.Style.STROKE);
    contourLayoutPaint.setStrokeWidth(1f);

    skeletonPaint = new Paint();
    skeletonPaint.setColor(Color.RED);
    skeletonPaint.setStyle(Paint.Style.STROKE);
    skeletonPaint.setStrokeWidth(3f);
    skeletonPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    fixedPaint = new Paint();
    fixedPaint.setColor(Color.BLUE);
    fixedPaint.setStyle(Paint.Style.FILL);
    fixedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    controlPaint = new Paint();
    controlPaint.setColor(Color.GREEN);
    controlPaint.setStyle(Paint.Style.FILL);
    controlPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    float width = getWidth();
    float height = getHeight();

    if (width > height) {
      float offset = height * CANVAS_OFFSET_RATIO;
      canvasRegion.setMinX((width - height) / 2 + offset);
      canvasRegion.setMaxX((width + height) / 2 - offset);
      canvasRegion.setMinY(height - offset);
      canvasRegion.setMaxY(0 + offset);
    } else {
      float offset = width * CANVAS_OFFSET_RATIO;
      canvasRegion.setMinX(0 + offset);
      canvasRegion.setMaxX(width - offset);
      canvasRegion.setMinY((height + width) / 2 - offset);
      canvasRegion.setMaxY((height - width) / 2 + offset);
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    if (locator == null) {
      return;
    }

    locator.invalidate(canvasRegion);

    if (skeletonView) {
      for (Path path : locator.getContourPaths()) {
        canvas.drawPath(path, contourLayoutPaint);
      }

      for (Path path : locator.getSkeletonPaths()) {
        canvas.drawPath(path, skeletonPaint);
      }

      for (Vector2D fixed : locator.getFixedCircles()) {
        canvas.drawCircle((float) fixed.getX(), (float) fixed.getY(),
            FIXED_POINT_RADIUS, fixedPaint);
      }

      for (Vector2D control : locator.getControlCircles()) {
        canvas.drawCircle((float) control.getX(), (float) control.getY(),
            CONTROL_POINT_RADIUS, controlPaint);
      }
    } else {
      for (Path path : locator.getContourPaths()) {
        canvas.drawPath(path, contourPaint);
      }
    }
  }

  /**
   * Draw given locators.
   *
   * @param locators locator to draw.
   */
  public void drawLocators(Locator locators) {
    // TODO: draw multiple character
    this.locator = locators;
    invalidate();
  }

  /**
   * Set viewing skeleton lines on/off.
   *
   * <p> On skeleton view - show skeleton curves of character with lines and points.
   * <br> Off skeleton view - show filled font.
   *
   * @param on on/off skeleton view
   */
  public void viewSkeleton(boolean on) {
    skeletonView = on;

    invalidate();
  }

  @Override
  public void onFeatureChange() {
    invalidate();
  }

  @Override
  public int getPriority() {
    return PRIORITY;
  }
}
