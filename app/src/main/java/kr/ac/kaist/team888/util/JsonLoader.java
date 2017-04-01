package kr.ac.kaist.team888.util;

import com.google.gson.Gson;
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
  private static final String DEFAULT_FILE_PATH = "/MyLittleFont/DefaultData.json";
  private static final String CHARACTERS_KEY = "characters";

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
}
