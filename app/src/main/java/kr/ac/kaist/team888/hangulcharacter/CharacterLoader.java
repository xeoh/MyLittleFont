package kr.ac.kaist.team888.hangulcharacter;

import com.google.common.collect.ImmutableMap;

/**
 * {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter} subclass Loader.
 *
 * <p> Every {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter}
 * subclasses are loaded through this class.
 */
public class CharacterLoader {
  private ImmutableMap<Hangul, HangulCharacter> hangulMap
      = new ImmutableMap.Builder<Hangul, HangulCharacter>()
      .put(Hangul.INIT_GIYEOK, new Giyeok())
      .put(Hangul.INIT_RIEUL, new Rieul())
      .put(Hangul.INIT_MIEUM, new Mieum())
      .put(Hangul.MEDI_AH, new Ah())
      .put(Hangul.FIN_GIYEOK, new Giyeok())
      .put(Hangul.FIN_RIEUL, new Rieul())
      .put(Hangul.FIN_MIEUM, new Mieum())
      .build();

  private CharacterLoader() {
  }

  private static class Singleton {
    private static final CharacterLoader instance = new CharacterLoader();
  }

  /**
   * Getter of singleton instance.
   *
   * @return singleton instance
   */
  public static CharacterLoader getInstance() {
    return Singleton.instance;
  }

  /**
   * Get {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter} subclass from
   * {@link kr.ac.kaist.team888.hangulcharacter.Hangul} enum class.
   *
   * @param hangul {@link kr.ac.kaist.team888.hangulcharacter.Hangul} to get
   * @return Correspond {@link kr.ac.kaist.team888.hangulcharacter.HangulCharacter} subclass.
   */
  public HangulCharacter getHangulChar(Hangul hangul) {
    return hangulMap.get(hangul);
  }
}
