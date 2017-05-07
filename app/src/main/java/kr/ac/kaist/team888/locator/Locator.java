package kr.ac.kaist.team888.locator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import android.graphics.Path;

import kr.ac.kaist.team888.core.Point2D;
import kr.ac.kaist.team888.core.Region;
import kr.ac.kaist.team888.core.Stroke;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;
import kr.ac.kaist.team888.util.HangulDecomposer;
import kr.ac.kaist.team888.util.JsonLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class makes each character of a Hangul letter be placed on its appropriate location.
 *
 * <p>This class object provides a functionality to get outer and inner strokes of the letter.
 */
public class Locator {
  private static final String TYPE_TOKEN = "type%d";
  public static final Region ORIGIN_REGION = HangulCharacter.ORIGIN_REGION;

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<Stroke>> skeletons;

  private ArrayList<Path> paths;
  private ArrayList<Point2D> fixedCircles;
  private ArrayList<Point2D> controlCircles;

  /**
   * Constructs a locator object for given Hangul letter.
   *
   * <p>This method accepts a single Hangul letter and then composes and transforms each character
   * of the letter to its right region by getting base region data of it.
   *
   * <p>If an input letter is not Hangul, all locator functionality will not work properly.
   *
   * @param letter a Hangul letter.
   */
  public Locator(char letter) {
    characters = HangulDecomposer.decompose(letter);
    regions = calculateRegions();
    skeletons = new ArrayList<>();

    for (int i = 0; i < characters.size(); i++) {
      HangulCharacter character = characters.get(i);
      Region baseRegion = character.getRegion();
      Region targetRegion = regions.get(i);

      for (ArrayList<Stroke> skeleton : character.getSkeletons()) {

        ArrayList<Stroke> newSkeleton = new ArrayList<>();
        for (Stroke stroke : skeleton) {
          newSkeleton.add(baseRegion.transformStroke(targetRegion, stroke));
        }
        skeletons.add(newSkeleton);
      }
    }
  }

  private ArrayList<Region> calculateRegions() {
    String type = String.format(TYPE_TOKEN, characters.size());
    String medialToken = characters.get(1).getClass().getSimpleName();
    JsonArray baseLocatorData = JsonLoader.getInstance().getLocatorData(type, medialToken);
    Gson gson = new Gson();
    Type collectionType = new TypeToken<Collection<Region>>(){}.getType();
    ArrayList<Region> regions = gson.fromJson(baseLocatorData, collectionType);

    return regions;
  }

  /**
   * Returns an array list of characters of the letter located by the locator.
   *
   * @return an array list of characters.
   */
  public ArrayList<HangulCharacter> getCharacters() {
    return characters;
  }

  /**
   * Returns an array list of skeleton of the letter located by the locator.
   *
   * @return an array list of skeleton.
   */
  public ArrayList<ArrayList<Stroke>> getSkeletons() {
    return skeletons;
  }

  /**
   * Recalculate Paths and Circles respect to canvas region.
   *
   * <p>paths (from {@link Locator#getPaths()}),
   * fixedCircles (from {@link Locator#getFixedCircles()}),
   * controlCircles (from {@link Locator#getControlCircles()})
   * are updated from this function call.
   *
   * @param canvasRegion Region of canvas
   */
  public void invalidate(Region canvasRegion) {
    paths = new ArrayList<>();
    fixedCircles = new ArrayList<>();
    controlCircles = new ArrayList<>();

    setXrayPaths(canvasRegion);
  }

  /**
   * Get array list of paths to draw on canvas.
   *
   * @return array list of paths
   */
  public ArrayList<Path> getPaths() {
    return paths;
  }

  /**
   * Get array list of fixed points to draw on canvas.
   *
   * @return array list of fixed point
   */
  public ArrayList<Point2D> getFixedCircles() {
    return fixedCircles;
  }

  /**
   * Get array list of control points to draw on canvas.
   *
   * @return array list of control point
   */
  public ArrayList<Point2D> getControlCircles() {
    return controlCircles;
  }

  private void setXrayPaths(Region canvasRegion) {
    for (ArrayList<Stroke> skeleton : skeletons) {
      Path path = new Path();

      for (Stroke stroke : skeleton) {
        Stroke transStroke = ORIGIN_REGION.transformStroke(canvasRegion, stroke);
        path.moveTo(transStroke.getStartPoint().getX(), transStroke.getStartPoint().getY());
        fixedCircles.add(transStroke.getStartPoint());

        ArrayList<Point2D> controlPoints = transStroke.getControlPoints();
        for (int i = 0; i < controlPoints.size(); i++) {
          if (i == controlPoints.size() - 1) {
            break;
          }

          float controlX = controlPoints.get(i).getX();
          float controlY = controlPoints.get(i).getY();

          float endX = (controlPoints.get(i + 1).getX() + controlX) / 2;
          float endY = (controlPoints.get(i + 1).getY() + controlY) / 2;

          path.quadTo(controlX, controlY, endX, endY);
          controlCircles.add(controlPoints.get(i));
        }

        if (controlPoints.isEmpty()) {
          path.lineTo(transStroke.getEndPoint().getX(), transStroke.getEndPoint().getY());
          fixedCircles.add(transStroke.getEndPoint());
        } else {
          Point2D lastControlPoint = controlPoints.get(controlPoints.size() - 1);
          path.quadTo(lastControlPoint.getX(), lastControlPoint.getY(),
              transStroke.getEndPoint().getX(), transStroke.getEndPoint().getY());
          controlCircles.add(lastControlPoint);
          fixedCircles.add(transStroke.getEndPoint());
        }
      }
      paths.add(path);
    }
  }
}