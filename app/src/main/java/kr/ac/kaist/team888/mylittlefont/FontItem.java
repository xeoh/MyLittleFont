package kr.ac.kaist.team888.mylittlefont;

import java.util.Date;

/**
 * This class represents a single font as the set of feature values.
 *
 * <p>Each object of this class can represent the corresponding record in the database
 * if it exists. In this case, <code>id</code> indicates the index of the record,
 * <code>name</code> indicates the name of the font item, <code>datetime</code>
 * indicates the created datetime of the font item. For other cases, these three fields
 * does not have any meaning.
 */
public class FontItem {
  private static final double DEFAULT_CONTROL_CURVE = 0;
  private static final double DEFAULT_CONTROL_ROUNDNESS = 0;
  private static final double DEFAULT_CONTROL_WEIGHT = .5;
  private static final double DEFAULT_CONTROL_WIDTH = .5;
  private static final double DEFAULT_CONTROL_FLATTENING = 0;

  /**
   * Initial font with default feature values.
   */
  public static final FontItem DEFAULT_CONTROLS = new FontItem(
      -1, null, null,
      DEFAULT_CONTROL_CURVE, DEFAULT_CONTROL_ROUNDNESS,
      DEFAULT_CONTROL_WEIGHT, DEFAULT_CONTROL_WIDTH,
      DEFAULT_CONTROL_FLATTENING
  );

  private int id;
  private String name;
  private Date datetime;
  private double curve;
  private double roundness;
  private double weight;
  private double width;
  private double flattening;

  private FontItem(int id, String name, Date datetime,
           double curve, double roundness, double weight, double width, double flattening) {
    this.id = id;
    this.name = name;
    this.datetime = datetime;
    this.curve = curve;
    this.roundness = roundness;
    this.weight = weight;
    this.width = width;
    this.flattening = flattening;
  }

  /**
   * Returns the id of the font.
   *
   * @return the id of the font
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id of the font.
   *
   * @param id the id of the font
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the name of the font.
   *
   * @return the name of the font
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the font.
   *
   * @param name the name of the font
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the datetime of the font created.
   *
   * @return the datetime of the font created
   */
  public Date getDatetime() {
    return datetime;
  }

  /**
   * Sets the datetime of the font created.
   *
   * @param datetime the datetime of the font created
   */
  public void setDatetime(Date datetime) {
    this.datetime = datetime;
  }

  /**
   * Returns the curve value of the font.
   *
   * @return the curve value of the font
   */
  public double getCurve() {
    return curve;
  }

  /**
   * Sets the curve value of the font.
   *
   * @param curve the curve value of the font
   */
  public void setCurve(double curve) {
    this.curve = curve;
  }

  /**
   * Returns the roundness value of the font.
   *
   * @return the roundness value of the font
   */
  public double getRoundness() {
    return roundness;
  }

  /**
   * Sets the roundness value of the font.
   *
   * @param roundness the roundness of the font
   */
  public void setRoundness(double roundness) {
    this.roundness = roundness;
  }

  /**
   * Returns the weight value of the font.
   *
   * @return the weight value of the font
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Sets the weight of the font.
   *
   * @param weight the weight of the font
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * Returns the width value of the font.
   *
   * @return the width value of the font
   */
  public double getWidth() {
    return width;
  }

  /**
   * Sets the width of the font.
   *
   * @param width the width of the font
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Returns the flattening value of the font.
   *
   * @return the flattening value of the font
   */
  public double getFlattening() {
    return flattening;
  }

  /**
   * Sets the flattening of the font.
   *
   * @param flattening the flattening of the font
   */
  public void setFlattening(double flattening) {
    this.flattening = flattening;
  }

  public static class FontItemBuilder {
    private int id;
    private String name;
    private Date datetime;
    private double curve;
    private double roundness;
    private double weight;
    private double width;
    private double flattening;

    /**
     * Sets the id.
     *
     * @param id the id
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setId(int id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the name.
     *
     * @param name the name
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setName(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the datetime.
     *
     * @param datetime the datetime
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setDatetime(Date datetime) {
      this.datetime = datetime;
      return this;
    }

    /**
     * Sets the curve value.
     *
     * @param curve the curve value
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setCurve(double curve) {
      this.curve = curve;
      return this;
    }

    /**
     * Sets the roundness value.
     *
     * @param roundness the roundness value
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setRoundness(double roundness) {
      this.roundness = roundness;
      return this;
    }

    /**
     * Sets the weight value.
     *
     * @param weight the weight value
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setWeight(double weight) {
      this.weight = weight;
      return this;
    }

    /**
     * Sets the width value.
     *
     * @param width the width value
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setWidth(double width) {
      this.width = width;
      return this;
    }

    /**
     * Sets the flattening value.
     *
     * @param flattening the flattening value
     * @return this builder, useful for chaining
     */
    public FontItemBuilder setFlattening(double flattening) {
      this.flattening = flattening;
      return this;
    }

    /**
     * Build the {@link FontItem} after options have been set.
     *
     * @return the newly constructed {@link FontItem} object
     */
    public FontItem build() {
      return new FontItem(id, name, datetime, curve, roundness, weight, width, flattening);
    }
  }
}
