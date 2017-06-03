package kr.ac.kaist.team888.util;

import kr.ac.kaist.team888.mylittlefont.FontItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FeatureController {
  private ArrayList<WeakReference> listeners;
  private Comparator<WeakReference> comparator = new Comparator<WeakReference>() {
    @Override
    public int compare(WeakReference o1, WeakReference o2) {
      if (o1.get() == o2.get()) {
        return 0;
      }
      if (o1.get() == null) {
        return -1;
      }
      if (o2.get() == null) {
        return 1;
      }
      return ((OnFeatureChangeListener) o1.get()).getPriority()
          - ((OnFeatureChangeListener) o2.get()).getPriority();
    }
  };

  private static final double DEFAULT_GAP = .1;

  private double curve = FontItem.DEFAULT_CONTROLS.getCurve();
  private double roundness = FontItem.DEFAULT_CONTROLS.getRoundness();
  private double weight = FontItem.DEFAULT_CONTROLS.getWeight();
  private double width = FontItem.DEFAULT_CONTROLS.getWidth();
  private double flattening = FontItem.DEFAULT_CONTROLS.getFlattening();
  private double arise = FontItem.DEFAULT_CONTROLS.getArise();
  private double gap = DEFAULT_GAP;

  /**
   * Interface for listening change of hangul features.
   *
   * <p> If some feature(weight, curve, and etc)'s value are change,
   * {@link FeatureController} calls {@link #onFeatureChange()}.
   *
   */
  public interface OnFeatureChangeListener {
    /**
     * Method that will be executed on feature's value change.
     */
    void onFeatureChange();

    /**
     * Priority of listener.
     *
     * <p> The lower priority executed first.
     *
     * @return priority as integer
     */
    int getPriority();
  }

  private static class Singleton {
    private static final FeatureController instance = new FeatureController();
  }

  /**
   * Getter of singleton instance.
   *
   * @return singleton instance
   */
  public static FeatureController getInstance() {
    return FeatureController.Singleton.instance;
  }

  private FeatureController() {
    listeners = new ArrayList<>();
  }

  /**
   * Register {@link OnFeatureChangeListener}.
   *
   * @param listener listener to register
   */
  public void registerOnFeatureChangeListener(OnFeatureChangeListener listener) {
    listeners.add(new WeakReference<>(listener));
    Collections.sort(listeners, comparator);
  }

  private void onFeatureChange() {
    for (int i = 0; i < listeners.size(); i++) {
      if (listeners.get(i).get() != null) {
        ((OnFeatureChangeListener)listeners.get(i).get()).onFeatureChange();
      } else {
        listeners.remove(i);
        i--;
      }
    }
  }

  /**
   * Getter curve value.
   *
   * @return curve value
   */
  public double getCurve() {
    return curve;
  }

  /**
   * Setter of curve value.
   *
   * @param curve curve value
   */
  public void setCurve(double curve) {
    this.curve = curve;
    onFeatureChange();
  }

  /**
   * Getter roundness value.
   *
   * @return roundness value
   */
  public double getRoundness() {
    return roundness;
  }

  /**
   * Setter of roundness value.
   *
   * @param roundness roundness value
   */
  public void setRoundness(double roundness) {
    this.roundness = roundness;
    onFeatureChange();
  }

  /**
   * Getter weight value.
   *
   * @return weight value
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Setter of weight value.
   *
   * @param weight weight value
   */
  public void setWeight(double weight) {
    this.weight = weight;
    onFeatureChange();
  }

  /**
   * Getter gap value.
   *
   * <p> Space is proportional value. If gap is 1, then gap between letter is same
   * size as width of letter
   *
   * @return gap value
   */
  public double getGap() {
    return gap;
  }

  /**
   * Setter gap value.
   *
   * @param gap gap value
   */
  public void setGap(double gap) {
    this.gap = gap;
    onFeatureChange();
  }

  /**
   * Getter width value.
   *
   * <p> Width is proportional value. If width is 1, {@link kr.ac.kaist.team888.locator.Locator}
   * automatically computes maximum width. If 0, minimum width.
   *
   * @return width value
   */
  public double getWidth() {
    return width;
  }

  /**
   * Setter width value.
   *
   * @param width width value
   */
  public void setWidth(double width) {
    this.width = width;
    onFeatureChange();
  }

  /**
   * Getter flattening value.
   *
   * <p> Flattening is proportional value. If flattening is 0,
   * {@link kr.ac.kaist.team888.locator.Locator} automatically
   * computes fundamental position. If 1, flatten position.
   *
   * @return flattening value
   */
  public double getFlattening() {
    return flattening;
  }

  /**
   * Setter flattening value.
   *
   * @param flattening flattening value
   */
  public void setFlattening(double flattening) {
    this.flattening = flattening;
    onFeatureChange();
  }

  /**
   * Getter arise value.
   *
   * <p> Arise is proportional value. If arise is 1,
   * {@link kr.ac.kaist.team888.locator.Locator} automatically
   * computes arise position. If 0, fundamental position.
   *
   * @return arise value
   */
  public double getArise() {
    return arise;
  }

  /**
   * Setter arise value.
   *
   * @param arise arise value
   */
  public void setArise(double arise) {
    this.arise = arise;
    onFeatureChange();
  }

  /**
   * Sets feature values as of the given font item in one swoop.
   *
   * <p>This method may be useful for changing more than one features at once
   * because setting values one by one triggers lots of view updates and calculates.
   *
   * @param fontItem a font item to be set
   */
  public void setFeatures(FontItem fontItem) {
    curve = fontItem.getCurve();
    weight = fontItem.getWeight();
    roundness = fontItem.getRoundness();
    width = fontItem.getWidth();
    onFeatureChange();
  }

  /**
   * Sets feature values as the initial setting.
   */
  public void setDefault() {
    setFeatures(FontItem.DEFAULT_CONTROLS);
  }
}
