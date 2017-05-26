package kr.ac.kaist.team888.hangulcharacter;

/**
 * Hangul Enum class
 *
 * <p> Represent all hangul character defined in UNICODE.
 */
public enum Hangul {
  SIN_GIYEOK    ("Giyeok",        0x3131),  // ㄱ
  SIN_SS_GIYEOK ("SsangGiyeok",   0x3132),  // ㄲ
  SIN_NIEUN     ("Nieum",         0x3134),  // ㄴ
  SIN_DIGEUT    ("Digeut",        0x3137),  // ㄷ
  SIN_SS_DIGEUT ("SsangDigeut",   0x3138),  // ㄸ
  SIN_RIEUL     ("Rieul",         0x3139),  // ㄹ
  SIN_MIEUM     ("Mieum",         0x3141),  // ㅁ
  SIN_BIEUP     ("Bieup",         0x3142),  // ㅂ
  SIN_SS_BIEUP  ("SsangBieup",    0x3143),  // ㅃ
  SIN_SIOT      ("Siot",          0x3145),  // ㅅ
  SIN_SS_SIOT   ("SsangSiot",     0x3146),  // ㅆ
  SIN_IEUNG     ("Ieung",         0x3147),  // ㅇ
  SIN_JIEUT     ("Jieut",         0x3148),  // ㅈ
  SIN_SS_JIEUT  ("SsangJieut",    0x3149),  // ㅉ
  SIN_CHIEUT    ("Chieut",        0x314A),  // ㅊ
  SIN_KIEUK     ("Kieuk",         0x314B),  // ㅋ
  SIN_TIEUT     ("Tieut",         0x314C),  // ㅌ
  SIN_PIEUP     ("Pieup",         0x314D),  // ㅍ
  SIN_HIEUT     ("Hieut",         0x314E),  // ㅎ
  INIT_GIYEOK    ("Giyeok",       0x1100),  // ㄱ
  INIT_SS_GIYEOK ("SsangGiyeok",  0x1101),  // ㄲ
  INIT_NIEUN     ("Nieum",        0x1102),  // ㄴ
  INIT_DIGEUT    ("Digeut",       0x1103),  // ㄷ
  INIT_SS_DIGEUT ("SsangDigeut",  0x1104),  // ㄸ
  INIT_RIEUL     ("Rieul",        0x1105),  // ㄹ
  INIT_MIEUM     ("Mieum",        0x1106),  // ㅁ
  INIT_BIEUP     ("Bieup",        0x1107),  // ㅂ
  INIT_SS_BIEUP  ("SsangBieup",   0x1108),  // ㅃ
  INIT_SIOT      ("Siot",         0x1109),  // ㅅ
  INIT_SS_SIOT   ("SsangSiot",    0x110A),  // ㅆ
  INIT_IEUNG     ("Ieung",        0x110B),  // ㅇ
  INIT_JIEUT     ("Jieut",        0x110C),  // ㅈ
  INIT_SS_JIEUT  ("SsangJieut",   0x110D),  // ㅉ
  INIT_CHIEUT    ("Chieut",       0x110E),  // ㅊ
  INIT_KIEUK     ("Kieuk",        0x110F),  // ㅋ
  INIT_TIEUT     ("Tieut",        0x1110),  // ㅌ
  INIT_PIEUP     ("Pieup",        0x1111),  // ㅍ
  INIT_HIEUT     ("Hieut",        0x1112),  // ㅎ
  MEDI_AH        ("Ah",  0x1161),           // ㅏ
  MEDI_AE        ("Ae",  0x1162),           // ㅐ
  MEDI_YA        ("Ya",  0x1163),           // ㅑ
  MEDI_YAE       ("Yae", 0x1164),           // ㅒ
  MEDI_Eo        ("Eo",  0x1165),           // ㅓ
  MEDI_Eh        ("Eh",  0x1166),           // ㅔ
  MEDI_YEO       ("Yeo", 0x1167),           // ㅕ
  MEDI_YE        ("Ye",  0x1168),           // ㅖ
  MEDI_OH        ("Oh",  0x1169),           // ㅗ
  MEDI_WA        ("Wa",  0x116A),           // ㅘ
  MEDI_WAE       ("Wae", 0x116B),           // ㅙ
  MEDI_OE        ("Oe",  0x116C),           // ㅚ
  MEDI_YO        ("Yo",  0x116D),           // ㅛ
  MEDI_UH        ("Uh",  0x116E),           // ㅜ
  MEDI_WEO       ("Weo", 0x116F),           // ㅝ
  MEDI_WE        ("We",  0x1170),           // ㅞ
  MEDI_WI        ("Wi",  0x1171),           // ㅟ
  MEDI_YU        ("Yu",  0x1172),           // ㅠ
  MEDI_EU        ("Eu",  0x1173),           // ㅡ
  MEDI_YI        ("Yi",  0x1174),           // ㅢ
  MEDI_IH        ("Ih",  0x1175),           // ㅣ
  FIN_GIYEOK        ("Giyeok",      0x11A8),// ㄱ
  FIN_SS_GIYEOK     ("SsangGiyeok", 0x11A9),// ㄲ
  FIN_GIYEOK_SIOT   ("GiyeokSiot",  0x11AA),// ㄱㅅ
  FIN_NIEUN         ("NieumJieut",  0x11AB),// ㄴ
  FIN_NIEUN_JIEUT   ("NieumJieut",  0x11AC),// ㄴㅈ
  FIN_NIEUN_HIEUT   ("NieumJieut",  0x11AD),// ㄴㅎ
  FIN_DIGEUT        ("Digeut",      0x11AE),// ㄷ
  FIN_RIEUL         ("Rieul",       0x11AF),// ㄹ
  FIN_RIEUL_GIYEOK  ("RieulGiyeok", 0x11B0),// ㄹㄱ
  FIN_RIEUL_MIEUM   ("RieulMieum",  0x11B1),// ㄹㅁ
  FIN_RIEUL_BIEUP   ("RieulBieup",  0x11B2),// ㄹㅂ
  FIN_RIEUL_SIOT    ("RieulSiot",   0x11B3),// ㄹㅅ
  FIN_RIEUL_TIEUT   ("RieulTieut",  0x11B4),// ㄹㅌ
  FIN_RIEUL_PIEUP   ("RieulPieup",  0x11B5),// ㄹㅍ
  FIN_RIEUL_HIEUT   ("RieulHieut",  0x11B6),// ㄹㅎ
  FIN_MIEUM         ("Mieum",       0x11B7),// ㅁ
  FIN_BIEUP         ("Bieup",       0x11B8),// ㅂ
  FIN_BIEUP_SIOT    ("BieupSiot",   0x11B9),// ㅂㅅ
  FIN_SIOT          ("Siot",        0x11BA),// ㅅ
  FIN_SS_SIOT       ("SsangSiot",   0x11BB),// ㅆ
  FIN_IEUNG         ("Ieung",       0x11BC),// ㅇ
  FIN_JIEUT         ("Jieut",       0x11BD),// ㅈ
  FIN_CHIEUT        ("Chieut",      0x11BE),// ㅊ
  FIN_KIEUK         ("Kieuk",       0x11BF),// ㅋ
  FIN_TIEUT         ("Tieut",       0x11C0),// ㅌ
  FIN_PIEUP         ("Pieup",       0x11C1),// ㅍ
  FIN_HIEUT         ("Hieut",       0x11C2),// ㅎ
  NONE              ("None", -1);

  private static final int SINGLE_COUNT = 19;
  private static final int INITIAL_COUNT = 19;
  private static final int MEDIAL_COUNT = 21;

  private static final int SINGLE_UNICODE_BEGIN = 0x3131;
  private static final int SINGLE_UNICODE_END = 0x314E;
  private static final int INITIAL_UNICODE_BEGIN = 0x1100;
  private static final int INITIAL_UNICODE_END = 0x1112;
  private static final int MEDIAL_UNICODE_BEGIN = 0x1161;
  private static final int MEDIAL_UNICODE_END = 0x1175;
  private static final int FINAL_UNICODE_BEGIN = 0x11A8;
  private static final int FINAL_UNICODE_END = 0x11C2;

  private final String name;
  private final int value;

  Hangul(String name, int value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Get {@link Hangul} class from UNICODE value.
   *
   * @param value UNICODE value
   * @return Correspond {@link Hangul} value
   */
  public static Hangul fromInt(int value) {
    for (Hangul hangul : values()) {
      if (hangul.getValue() == value) {
        return hangul;
      }
    }

    int index;
    int offset;
    if (SINGLE_UNICODE_BEGIN <= value && value <= SINGLE_UNICODE_END) {
      for (Hangul hangul : values()) {
        if (hangul.getValue() == value) {
          return hangul;
        }
      }
      return NONE;
    } else if (INITIAL_UNICODE_BEGIN <= value && value <= INITIAL_UNICODE_END) {
      index = INITIAL_UNICODE_END - value;
      offset = SINGLE_COUNT;
    } else if (MEDIAL_UNICODE_BEGIN <= value && value <= MEDIAL_UNICODE_END) {
      index = MEDIAL_UNICODE_END - value;
      offset = SINGLE_COUNT + INITIAL_COUNT;
    } else if (FINAL_UNICODE_BEGIN <= value && value <= FINAL_UNICODE_END) {
      index = FINAL_UNICODE_END - value;
      offset = SINGLE_COUNT + INITIAL_COUNT + MEDIAL_COUNT;
    } else {
      return NONE;
    }

    return values()[index + offset];
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Get UNICODE value.
   *
   * @return UNICODE value
   */
  public int getValue() {
    return value;
  }
}
