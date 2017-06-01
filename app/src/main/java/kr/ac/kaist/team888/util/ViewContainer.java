package kr.ac.kaist.team888.util;

import android.view.View;

/**
 * View container to update a view on feature changes.
 *
 * @param <T> View class type
 */
public abstract class ViewContainer<T extends View>
    implements FeatureController.OnFeatureChangeListener {
  /**
   * A target view object.
   */
  protected T view;

  /**
   * Make a new container for a given view and register it into the {@link FeatureController}.
   *
   * @param view a target view object
   */
  public ViewContainer(T view) {
    this.view = view;
    FeatureController.getInstance().registerOnFeatureChangeListener(this);
  }

  /**
   * Returns the view object.
   *
   * @return the view object
   */
  public T getView() {
    return view;
  }

  /**
   * Sets a target view object.
   *
   * @param view a target view object
   */
  public void setView(T view) {
    this.view = view;
  }
}
