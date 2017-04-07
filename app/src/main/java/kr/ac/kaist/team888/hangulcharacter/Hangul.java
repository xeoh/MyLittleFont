package kr.ac.kaist.team888.hangulcharacter;

import kr.ac.kaist.team888.util.Alert;

/**
 * Hangul Enum class
 *
 * <p> Represent all hangul character defined in UNICODE.
 */
public enum Hangul {
  INIT_GIYEOK    ("Giyeok",       0x1100),  // ㄱ
  INIT_SS_GIYEOK ("SsangGiyeok",  0x1101),  // ㄲ
  INIT_NIEUN     ("Nieum",        0x1102),  // ㄴ
  INIT_DIGEUT    ("Degeut",       0x1103),  // ㄷ
  INIT_SS_DIGEUT ("SsangDegeut",  0x1104),  // ㄸ
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
  MEDI_YU        ("Yu",  0x116A),           // ㅠ
  MEDI_WA        ("Wa",  0x116B),           // ㅘ
  MEDI_YO        ("Yo",  0x116C),           // ㅛ
  MEDI_WAE       ("Wae", 0x116D),           // ㅙ
  MEDI_OE        ("Oe",  0x116E),           // ㅚ
  MEDI_UH        ("Uh",  0x116F),           // ㅜ
  MEDI_WO        ("Wo",  0x1170),           // ㅝ
  MEDI_WE        ("We",  0x1171),           // ㅞ
  MEDI_WI        ("Wi",  0x1172),           // ㅟ
  MEDI_EU        ("Eu",  0x1173),           // ㅡ
  MEDI_UI        ("Ui",  0x1174),           // ㅢ
  MEDI_IH        ("Ih",  0x1175),           // ㅣ
  FIN_GIYEOK        ("", 0x11A8),
  FIN_SS_GIYEOK     ("", 0x11A9),
  FIN_GIYEOK_SIOT   ("", 0x11AA),
  FIN_NIEUN         ("", 0x11AB),
  FIN_NIEUN_JIEUT   ("", 0x11AC),
  FIN_NIEUN_HIEUT   ("", 0x11AD),
  FIN_DIGEUT        ("", 0x11AE),
  FIN_RIEUL         ("", 0x11AF),
  FIN_RIEUL_GIYEOK  ("", 0x11B0),
  FIN_RIEUL_MIEUM   ("", 0x11B1),
  FIN_RIEUL_BIEUP   ("", 0x11B2),
  FIN_RIEUL_SIOT    ("", 0x11B3),
  FIN_RIEUL_TIEUT   ("", 0x11B4),
  FIN_RIEUL_PIEUP   ("", 0x11B5),
  FIN_RIEUL_HIEUT   ("", 0x11B6),
  FIN_MIEUM         ("", 0x11B7),
  FIN_BIEUP         ("", 0x11B8),
  FIN_BIEUP_SIOT    ("", 0x11B9),
  FIN_SIOT          ("", 0x11BA),
  FIN_SS_SIOT       ("", 0x11BB),
  FIN_IEUNG         ("", 0x11BC),
  FIN_JIEUT         ("", 0x11BD),
  FIN_CHIEUT        ("", 0x11BE),
  FIN_KIEUK         ("", 0x11BF),
  FIN_TIEUT         ("", 0x11C0),
  FIN_PIEUP         ("", 0x11C1),
  FIN_HIEUT         ("", 0x11C2),
  NONE              ("None", -1);

  private static final int INITIAL_COUNT = 19;
  private static final int MEDIAL_COUNT = 21;
  private static final int FINAL_COUNT = 28;

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
   * Get {@link kr.ac.kaist.team888.hangulcharacter.Hangul} class from UNICODE value.
   *
   * @param value UNICODE value
   * @return Correspond {@link kr.ac.kaist.team888.hangulcharacter.Hangul} value
   */
  public static Hangul fromInt(int value) {
    Alert.log(Hangul.class, String.format("%x", value));

    if (INITIAL_UNICODE_BEGIN <= value && value <= INITIAL_UNICODE_END) {
      return Hangul.values()[value - INITIAL_UNICODE_BEGIN];
    } else if (MEDIAL_UNICODE_BEGIN <= value && value <= MEDIAL_UNICODE_END) {
      return Hangul.values()[value - MEDIAL_UNICODE_BEGIN + INITIAL_COUNT];
    } else if (FINAL_UNICODE_BEGIN <= value && value <= FINAL_UNICODE_END) {
      return Hangul.values()[value - FINAL_UNICODE_BEGIN + INITIAL_COUNT + MEDIAL_COUNT];
    } else {
      return NONE;
    }
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
