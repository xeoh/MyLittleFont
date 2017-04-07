package kr.ac.kaist.team888.util;

import kr.ac.kaist.team888.hangulcharacter.CharacterLoader;
import kr.ac.kaist.team888.hangulcharacter.Hangul;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;

import java.text.Normalizer;
import java.util.ArrayList;

/**
 * HangulDecomposer provides decomposing Hangul letter into its characters.
 *
 * <p> Available method:
 * <ul>
 * <li>{@link kr.ac.kaist.team888.util.HangulDecomposer#decompose(char)}
 * : returns a list of characters of given input letter.</li>
 * </ul>
 *
 * <p> Decomposing Hangul letter is algorithmic and defined in Unicode as <i>normalizing</i>.
 * See <a href="http://www.unicode.org/reports/tr15/tr15-23.html#Hangul">Hangul Unicode Normalization</a>
 * for more information about this.
 */
public class HangulDecomposer {
  /**
   * Returns an array list of {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter} objects of
   * a given {@code letter} by normalizing it.
   *
   * <p> If {@code letter} is not a supported Hangul letter, a returned array would contain
   * meaningless values.
   *
   * @param letter a Hangul letter to decompose
   * @return an array list of {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter} objects of
   * {@code letter}
   */
  public static ArrayList<HangulCharacter> decompose(char letter) {
    ArrayList<HangulCharacter> characters = new ArrayList<>();
    String nfd = Normalizer.normalize(String.valueOf(letter), Normalizer.Form.NFD);

    for (char c : nfd.toCharArray()) {
      characters.add(CharacterLoader.getInstance().getHangulChar(Hangul.fromInt(c)));
    }

    return characters;
  }
}