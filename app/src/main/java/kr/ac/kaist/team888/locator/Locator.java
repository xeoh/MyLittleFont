package kr.ac.kaist.team888.locator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

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

  private ArrayList<Region> regions;
  private ArrayList<HangulCharacter> characters;
  private ArrayList<ArrayList<Stroke>> outerStrokes;
  private ArrayList<ArrayList<Stroke>> innerStrokes;

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
    outerStrokes = new ArrayList<>();
    innerStrokes = new ArrayList<>();

    for (int i = 0; i < characters.size(); i++) {
      HangulCharacter character = characters.get(i);
      Region baseRegion = character.getRegion();
      Region targetRegion = regions.get(i);

      for (ArrayList<Stroke> closedPath : character.getOuterStorkes()) {
        outerStrokes.add(transformClosedPath(closedPath, baseRegion, targetRegion));
      }
      for (ArrayList<Stroke> closedPath : character.getInnerStrokes()) {
        innerStrokes.add(transformClosedPath(closedPath, baseRegion, targetRegion));
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

  private ArrayList<Stroke> transformClosedPath(ArrayList<Stroke> closedPath,
                                                Region baseRegion, Region targetRegion) {
    for (Stroke stroke : closedPath) {
      stroke.setStartPoint(transformPoint(stroke.getStartPoint(), baseRegion, targetRegion));
      stroke.setControlPoint(transformPoint(stroke.getControlPoint(), baseRegion, targetRegion));
      stroke.setEndPoint(transformPoint(stroke.getEndPoint(), baseRegion, targetRegion));
    }

    return closedPath;
  }

  private Point2D transformPoint(Point2D point, Region baseRegion, Region targetRegion) {
    Point2D baseMinPoint = baseRegion.getMinPoint();
    Point2D baseDiffPoint = baseRegion.getMaxPoint().sub(baseMinPoint);
    Point2D targetMinPoint = targetRegion.getMinPoint();
    Point2D targetDiffPoint = targetRegion.getMaxPoint().sub(targetMinPoint);

    return point.sub(baseMinPoint)
        .scaleX(targetDiffPoint.getX() / baseDiffPoint.getX())
        .scaleY(targetDiffPoint.getY() / baseDiffPoint.getY())
        .add(targetMinPoint);
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
   * Returns an array list of outer strokes of the letter located by the locator.
   *
   * @return an array list of outer strokes.
   */
  public ArrayList<ArrayList<Stroke>> getOuterStrokes() {
    return outerStrokes;
  }

  /**
   * Returns an array list of inner strokes of the letter located by the locator.
   *
   * @return an array list of inner strokes.
   */
  public ArrayList<ArrayList<Stroke>> getInnerStrokes() {
    return innerStrokes;
  }
}