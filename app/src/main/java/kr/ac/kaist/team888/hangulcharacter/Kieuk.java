package kr.ac.kaist.team888.hangulcharacter;

import java.util.ArrayList;

public class Kieuk extends HangulCharacter {
  protected Kieuk() {
    super();
  }

  @Override
  public boolean isFlatable(int currentIndex, ArrayList<HangulCharacter> characters) {
    if (currentIndex != 0 || characters.size() < 2) {
      return false;
    }

    String moeumClassName = characters.get(1).getClass().getSimpleName();
    if (moeumClassName.equals("Ah")
            || moeumClassName.equals("Ae")
            || moeumClassName.equals("Ya")
            || moeumClassName.equals("Yae")
            || moeumClassName.equals("Eo")
            || moeumClassName.equals("Eh")
            || moeumClassName.equals("Yeo")
            || moeumClassName.equals("Ye")
            || moeumClassName.equals("Ih")) {
      return true;
    }

    return false;
  }
}
