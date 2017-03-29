package kr.ac.kaist.team888.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Make data with external json file.
 *
 * <p>This class for convert json file using <a href="https://github.com/google/gson">gson</a> library.
 */
public class JsonLoader {
  private static final String DEFAULT_FILE_PATH = "/DefaultData.json";
  private JsonElement data;

  private JsonLoader() {
    data = loadFile();
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
  public JsonElement getData() {
    return Singleton.instance.data;
  }

  private void loadFile() {
    Gson gson = new GsonBuilder().create();

    try {
      String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DEFAULT_FILE_PATH;
      FileReader fileReader = new FileReader(path);

      BufferedReader bufferedReader = new BufferedReader(fileReader);

      this.data = gson.fromJson(bufferedReader, JsonElement.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Save data as file.
   *
   * @param jsonElement target data
   */
  public void saveFile(JsonElement jsonElement) {
    Gson gson = new GsonBuilder().create();

    String jsonString = gson.toJson(jsonElement);

    try {
      FileWriter writer = new FileWriter(DEFAULT_FILE_PATH);
      writer.write(jsonString);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check external storage permission.
   *
   * @param activity target activity
   */
  public void checkPermission(Activity activity) {
    ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
  }
}
