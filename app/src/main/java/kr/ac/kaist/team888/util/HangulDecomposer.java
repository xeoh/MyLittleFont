package kr.ac.kaist.team888.util;

import kr.ac.kaist.team888.hangulcharacter.Ah;
import kr.ac.kaist.team888.hangulcharacter.HangulCharacter;
import kr.ac.kaist.team888.hangulcharacter.Mieum;

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
  private static final int INITIAL_UNICODE_BEGIN = 0x1100;
  private static final int INITIAL_UNICODE_END = 0x1112;
  private static final int MEDIAL_UNICODE_BEGIN = 0x1161;
  private static final int MEDIAL_UNICODE_END = 0x1175;
  private static final int FINAL_UNICODE_BEGIN = 0x11A8;
  private static final int FINAL_UNICODE_END = 0x11C2;

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
      HangulCharacter character = null;
      if (c >= INITIAL_UNICODE_BEGIN && c <= INITIAL_UNICODE_END) {
        character = temp_getInitial(c - INITIAL_UNICODE_BEGIN);
      } else if (c >= MEDIAL_UNICODE_BEGIN && c <= MEDIAL_UNICODE_END) {
        character = temp_getMedial(c - MEDIAL_UNICODE_BEGIN);
      } else if (c >= FINAL_UNICODE_BEGIN && c <= FINAL_UNICODE_END) {
        character = temp_getFinal(c - FINAL_UNICODE_BEGIN);
      }
      characters.add(character);
    }
    return characters;
  }

  // This method should be deleted and replaced right after CharacterLoader is implemented later.
  private static HangulCharacter temp_getInitial(int index) {
    switch (index) {
      case 6:
        return new Mieum();
      default:
        Alert.log(HangulDecomposer.class, "Input initial is not supported yet.");
        return null;
    }
  }

  // This method should be deleted and replaced right after CharacterLoader is implemented later.
  private static HangulCharacter temp_getMedial(int index) {
    switch (index) {
      case 0:
        return new Ah();
      default:
        Alert.log(HangulDecomposer.class, "Input medial is not supported yet.");
        return null;
    }
  }

  // This method should be deleted and replaced right after CharacterLoader is implemented later.
  private static HangulCharacter temp_getFinal(int index) {
    switch (index) {
      case 15:
        return new Mieum();
      default:
        Alert.log(HangulDecomposer.class, "Input final is not supported yet.");
        return null;
    }
  }
}