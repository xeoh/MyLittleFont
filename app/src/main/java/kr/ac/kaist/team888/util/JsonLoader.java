package kr.ac.kaist.team888.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.Environment;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Make data with external json file.
 *
 * <p>This class for convert json file using <a href="https://github.com/google/gson">gson</a> library.
 */
public class JsonLoader {
  // full path: /storage/emulated/0/Download/MyLittleFont/DefaultData.json
  private static final String DEFAULT_FILE_PATH = "/MyLittleFont/DefaultSkeleton.json";
  private static final String CHARACTERS_KEY = "characters";
  private static final String LOCATOR_KEY = "locator";

  private JsonObject data;
  private String path;
  private Gson gson;

  private JsonLoader() {
    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + DEFAULT_FILE_PATH;
    gson = new Gson();
    loadFile();
  }

  private static class Singleton {
    private static final JsonLoader instance = new JsonLoader();
  }

  /**
   * Getter of singleton instance.
   *
   * @return singleton instance
   */
  public static JsonLoader getInstance() {
    return Singleton.instance;
  }

  /**
   * Get data from default json file.
   *
   * @return default data
   */
  public JsonObject getData() {
    return Singleton.instance.data;
  }

  private void loadFile() {
    try {
      JsonParser parser = new JsonParser();
      JsonElement jsonElement = parser.parse(new FileReader(path));
      data = jsonElement.getAsJsonObject();
    } catch (IOException ioe) {
      Alert.log(this, ioe.getMessage());
    }
  }

  /**
   * Save data as file.
   */
  public void saveFile() {
    String jsonString = gson.toJson(data);

    try {
      FileWriter writer = new FileWriter(path);
      writer.write(jsonString);
      writer.close();
    } catch (IOException ioe) {
      Alert.log(this, ioe.getMessage());
    }
  }

  /**
   * Get character data.
   *
   * @param key character name i.e "Mieum"
   * @return character data
   */
  public JsonObject getCharData(String key) {
    return data.getAsJsonObject(CHARACTERS_KEY).getAsJsonObject(key);
  }

  /**
   * Set character data.
   *
   * @param key character name i.e. "Mieum"
   * @param charData data to set
   */
  public void setCharData(String key, JsonObject charData) {
    data.getAsJsonObject(CHARACTERS_KEY).add(key, charData);
  }

  /**
   * Returns a base locator data for the given letter type.
   *
   * <p>Hangul letters are grouped into two general types: type-2 and type-3. Type-2 is for letters
   * consisting of an initial and a medial, while type-3 includes a final also. Each general type
   * is separated with the medials. Thus, there are 42 letter types.
   *
   * @param type the general type ("type2" or "type3").
   * @param medial the name of the medial character such as "Ah".
   * @return a base locator data.
   */
  public JsonArray getLocatorData(String type, String medial) {
    return data.getAsJsonObject(LOCATOR_KEY).getAsJsonObject(type).getAsJsonArray(medial);
  }
}
