package kr.ac.kaist.team888.hangulcharacter;

import com.google.common.collect.ImmutableMap;


/**
 * {@link HangulCharacter} subclass Loader.
 *
 * <p> Every {@link HangulCharacter}
 * subclasses are loaded through this class.
 */
public class CharacterLoader {
  private ImmutableMap<HangulKey, HangulCharacter> hangulMap;

  private class HangulKey {
    Hangul key1;
    Hangul key2;
    Hangul key3;

    HangulKey(Hangul key1, Hangul key2, Hangul key3) {
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof HangulKey) {
        HangulKey ref = (HangulKey) obj;
        boolean equal = true;

        if ((key1 == null) != (ref.key1 == null)) {
          return false;
        }
        if (key1 != null) {
          equal = equal && key1.equals(ref.key1);
        }

        if ((key2 == null) != (ref.key2 == null)) {
          return false;
        }
        if (key2 != null) {
          equal = equal && key2.equals(ref.key2);
        }

        if ((key3 == null) != (ref.key3 == null)) {
          return false;
        }
        if (key3 != null) {
          equal = equal && key3.equals(ref.key3);
        }


        return equal;
      }

      if (obj instanceof  Hangul) {
        Hangul ref = (Hangul) obj;
        boolean equal = false;
        if (key1 != null) {
          equal = (key1.getValue() == ref.getValue());
        }
        if (key2 != null) {
          equal = equal || (key2.getValue() == ref.getValue());
        }
        if (key3 != null) {
          equal = equal || (key3.getValue() == ref.getValue());
        }
        return equal;
      }

      return false;
    }
  }

  private CharacterLoader() {
    hangulMap = new ImmutableMap.Builder<HangulKey, HangulCharacter>()
        .put(new HangulKey(Hangul.SIN_GIYEOK, Hangul.INIT_GIYEOK, Hangul.FIN_GIYEOK),
            new Giyeok())
        .put(new HangulKey(Hangul.SIN_SS_GIYEOK, Hangul.INIT_SS_GIYEOK, Hangul.FIN_SS_GIYEOK),
            new SsangGiyeok())
        .put(new HangulKey(Hangul.SIN_NIEUN, Hangul.INIT_NIEUN, Hangul.FIN_NIEUN),
            new Nieun())
        .put(new HangulKey(Hangul.SIN_DIGEUT, Hangul.INIT_DIGEUT, Hangul.FIN_DIGEUT),
            new Digeut())
        .put(new HangulKey(Hangul.SIN_SS_DIGEUT, Hangul.INIT_SS_DIGEUT, null),
            new SsangDigeut())
        .put(new HangulKey(Hangul.SIN_RIEUL, Hangul.INIT_RIEUL, Hangul.FIN_RIEUL),
            new Rieul())
        .put(new HangulKey(Hangul.SIN_MIEUM, Hangul.INIT_MIEUM, Hangul.FIN_MIEUM),
            new Mieum())
        .put(new HangulKey(Hangul.SIN_BIEUP, Hangul.INIT_BIEUP, Hangul.FIN_BIEUP),
            new Bieup())
        .put(new HangulKey(Hangul.SIN_SS_BIEUP, Hangul.INIT_SS_BIEUP, null),
            new SsangBieup())
        .put(new HangulKey(Hangul.SIN_SIOT, Hangul.INIT_SIOT, Hangul.FIN_SIOT),
            new Siot())
        .put(new HangulKey(Hangul.SIN_SS_SIOT, Hangul.INIT_SS_SIOT, Hangul.FIN_SS_SIOT),
            new SsangSiot())
        .put(new HangulKey(Hangul.SIN_IEUNG, Hangul.INIT_IEUNG, Hangul.FIN_IEUNG),
            new Ieung())
        .put(new HangulKey(Hangul.SIN_JIEUT, Hangul.INIT_JIEUT, Hangul.FIN_JIEUT),
            new Jieut())
        .put(new HangulKey(Hangul.SIN_SS_JIEUT, Hangul.INIT_SS_JIEUT, null),
            new SsangJieut())
        .put(new HangulKey(Hangul.SIN_CHIEUT, Hangul.INIT_CHIEUT, Hangul.FIN_CHIEUT),
            new Chieut())
        .put(new HangulKey(Hangul.SIN_KIEUK, Hangul.INIT_KIEUK, Hangul.FIN_KIEUK),
            new Kieuk())
        .put(new HangulKey(Hangul.SIN_TIEUT, Hangul.INIT_TIEUT, Hangul.FIN_TIEUT),
            new Tieut())
        .put(new HangulKey(Hangul.SIN_PIEUP, Hangul.INIT_PIEUP, Hangul.FIN_PIEUP),
            new Pieup())
        .put(new HangulKey(Hangul.SIN_HIEUT, Hangul.INIT_HIEUT, Hangul.FIN_HIEUT),
            new Hieut())
        .put(new HangulKey(Hangul.MEDI_AH, null, null),
            new Ah())
        .put(new HangulKey(Hangul.MEDI_AE, null, null),
            new Ae())
        .put(new HangulKey(Hangul.MEDI_YA, null, null),
            new Ya())
        .put(new HangulKey(Hangul.MEDI_YAE, null, null),
            new Yae())
        .put(new HangulKey(Hangul.MEDI_Eo, null, null),
            new Eo())
        .put(new HangulKey(Hangul.MEDI_Eh, null, null),
            new Eh())
        .put(new HangulKey(Hangul.MEDI_YEO, null, null),
            new Yeo())
        .put(new HangulKey(Hangul.MEDI_YE, null, null),
            new Ye())
        .put(new HangulKey(Hangul.MEDI_OH, null, null),
            new Oh())
        .put(new HangulKey(Hangul.MEDI_YU, null, null),
            new Yu())
        .put(new HangulKey(Hangul.MEDI_WA, null, null),
            new Wa())
        .put(new HangulKey(Hangul.MEDI_YO, null, null),
            new Yo())
        .put(new HangulKey(Hangul.MEDI_WAE, null, null),
            new Wae())
        .put(new HangulKey(Hangul.MEDI_OE, null, null),
            new Oe())
        .put(new HangulKey(Hangul.MEDI_UH, null, null),
            new Uh())
        .put(new HangulKey(Hangul.MEDI_WEO, null, null),
            new Weo())
        .put(new HangulKey(Hangul.MEDI_WE, null, null),
            new We())
        .put(new HangulKey(Hangul.MEDI_WI, null, null),
            new Wi())
        .put(new HangulKey(Hangul.MEDI_EU, null, null),
            new Eu())
        .put(new HangulKey(Hangul.MEDI_YI, null, null),
            new Yi())
        .put(new HangulKey(Hangul.MEDI_IH, null, null),
            new Ih())
        .put(new HangulKey(Hangul.FIN_GIYEOK_SIOT, null, null),
            new GiyeokSiot())
        .put(new HangulKey(Hangul.FIN_NIEUN_JIEUT, null, null),
            new NieunJieut())
        .put(new HangulKey(Hangul.FIN_NIEUN_HIEUT, null, null),
            new NieunHieut())
        .put(new HangulKey(Hangul.FIN_RIEUL_GIYEOK, null, null),
            new RieulGiyeok())
        .put(new HangulKey(Hangul.FIN_RIEUL_MIEUM, null, null),
            new RieulMieum())
        .put(new HangulKey(Hangul.FIN_RIEUL_BIEUP, null, null),
            new RieulBieup())
        .put(new HangulKey(Hangul.FIN_RIEUL_SIOT, null, null),
            new RieulSiot())
        .put(new HangulKey(Hangul.FIN_RIEUL_TIEUT, null, null),
            new RieulTieut())
        .put(new HangulKey(Hangul.FIN_RIEUL_PIEUP, null, null),
            new RieulPieup())
        .put(new HangulKey(Hangul.FIN_RIEUL_HIEUT, null, null),
            new RieulHieut())
        .put(new HangulKey(Hangul.FIN_BIEUP_SIOT, null, null),
            new BieupSiot())
        .build();
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
    for (HangulKey key : hangulMap.keySet()) {
      if (key.equals(hangul)) {
        return hangulMap.get(key);
      }
    }
    return null;
  }
}
