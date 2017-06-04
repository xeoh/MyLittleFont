package kr.ac.kaist.team888.mylittlefont;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Custom GridView for using in ScrollView.
 *
 * <p> This source is from <a href="https://gist.github.com/sakurabird/6868765">this link</href>
 */
public class ExpandableHeightGridView extends GridView {
  private boolean expanded = false;

  public ExpandableHeightGridView(Context context) {
    super(context);
  }

  public ExpandableHeightGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ExpandableHeightGridView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public boolean isExpanded() {
    return expanded;
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (isExpanded()) {
      int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
      super.onMeasure(widthMeasureSpec, expandSpec);

      ViewGroup.LayoutParams params = getLayoutParams();
      params.height = getMeasuredHeight();
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }

  /**
   * Set expandable.
   *
   * @param expanded expandable
   */
  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }
}