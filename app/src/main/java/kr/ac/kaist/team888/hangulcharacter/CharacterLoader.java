package kr.ac.kaist.team888.hangulcharacter;

import com.google.common.collect.ImmutableMap;


/**
 * {@link HangulCharacter} subclass Loader.
 *
 * <p> Every {@link HangulCharacter}
 * subclasses are loaded through this class.
 */
public class CharacterLoader {
  private ImmutableMap<Hangul, HangulCharacter> hangulMap
      = new ImmutableMap.Builder<Hangul, HangulCharacter>()
      .put(Hangul.INIT_GIYEOK, new Giyeok())
      .put(Hangul.INIT_SS_GIYEOK, new SsangGiyeok())
      .put(Hangul.INIT_NIEUN, new Nieun())
      .put(Hangul.INIT_DIGEUT, new Digeut())
      .put(Hangul.INIT_SS_DIGEUT, new SsangDigeut())
      .put(Hangul.INIT_RIEUL, new Rieul())
      .put(Hangul.INIT_MIEUM, new Mieum())
      .put(Hangul.INIT_BIEUP, new Bieup())
      .put(Hangul.INIT_SS_BIEUP, new SsangBieup())
      .put(Hangul.INIT_SIOT, new Siot())
      .put(Hangul.INIT_SS_SIOT, new SsangSiot())
      .put(Hangul.INIT_IEUNG, new Ieung())
      .put(Hangul.INIT_JIEUT, new Jieut())
      .put(Hangul.INIT_SS_JIEUT, new SsangJieut())
      .put(Hangul.INIT_CHIEUT, new Chieut())
      .put(Hangul.INIT_KIEUK, new Kieuk())
      .put(Hangul.INIT_TIEUT, new Tieut())
      .put(Hangul.INIT_PIEUP, new Pieup())
      .put(Hangul.INIT_HIEUT, new Hieut())
      .put(Hangul.MEDI_AH, new Ah())
      .put(Hangul.MEDI_AE, new Ae())
      .put(Hangul.MEDI_YA, new Ya())
      .put(Hangul.MEDI_YAE, new Yae())
      .put(Hangul.MEDI_Eo, new Eo())
      .put(Hangul.MEDI_Eh, new Eh())
      .put(Hangul.MEDI_YEO, new Yeo())
      .put(Hangul.MEDI_YE, new Ye())
      .put(Hangul.MEDI_OH, new Oh())
      .put(Hangul.MEDI_YU, new Yu())
      .put(Hangul.MEDI_WA, new Wa())
      .put(Hangul.MEDI_YO, new Yo())
      .put(Hangul.MEDI_WAE, new Wae())
      .put(Hangul.MEDI_OE, new Oe())
      .put(Hangul.MEDI_UH, new Uh())
      .put(Hangul.MEDI_WOE, new Woe())
      .put(Hangul.MEDI_WE, new We())
      .put(Hangul.MEDI_WI, new Wi())
      .put(Hangul.MEDI_EU, new Eu())
      .put(Hangul.MEDI_YI, new Yi())
      .put(Hangul.MEDI_IH, new Ih())
      .put(Hangul.FIN_GIYEOK, new Giyeok())
      .put(Hangul.FIN_SS_GIYEOK, new Giyeok())
      .put(Hangul.FIN_GIYEOK_SIOT, new GiyeokSiot())
      .put(Hangul.FIN_NIEUN, new Nieun())
      .put(Hangul.FIN_NIEUN_JIEUT, new NieunJieut())
      .put(Hangul.FIN_NIEUN_HIEUT, new NieunHieut())
      .put(Hangul.FIN_DIGEUT, new Digeut())
      .put(Hangul.FIN_RIEUL, new Rieul())
      .put(Hangul.FIN_RIEUL_GIYEOK, new RieulGiyeok())
      .put(Hangul.FIN_RIEUL_MIEUM, new RieulMieum())
      .put(Hangul.FIN_RIEUL_BIEUP, new RieulBieup())
      .put(Hangul.FIN_RIEUL_SIOT, new RieulSiot())
      .put(Hangul.FIN_RIEUL_TIEUT, new RieulTieut())
      .put(Hangul.FIN_RIEUL_PIEUP, new RieulPieup())
      .put(Hangul.FIN_RIEUL_HIEUT, new RieulHieut())
      .put(Hangul.FIN_MIEUM, new Mieum())
      .put(Hangul.FIN_BIEUP, new Bieup())
      .put(Hangul.FIN_BIEUP_SIOT, new BieupSiot())
      .put(Hangul.FIN_SIOT, new Siot())
      .put(Hangul.FIN_SS_SIOT, new SsangSiot())
      .put(Hangul.FIN_IEUNG, new Ieung())
      .put(Hangul.FIN_JIEUT, new Jieut())
      .put(Hangul.FIN_CHIEUT, new Chieut())
      .put(Hangul.FIN_KIEUK, new Kieuk())
      .put(Hangul.FIN_TIEUT, new Tieut())
      .put(Hangul.FIN_PIEUP, new Pieup())
      .put(Hangul.FIN_HIEUT, new Hieut())
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
   * Get {@link HangulCharacter} subclass from
   * {@link Hangul} enum class.
   *
   * @param hangul {@link Hangul} to get
   * @return Correspond {@link HangulCharacter} subclass.
   */
  public HangulCharacter getHangulChar(Hangul hangul) {
    return hangulMap.get(hangul);
  }
}
