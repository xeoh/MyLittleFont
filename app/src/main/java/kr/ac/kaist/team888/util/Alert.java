package kr.ac.kaist.team888.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Alert class provides generalized logging and popup methods for debugging.
 *
 * <p> Two main methods
 * <li>{@link kr.ac.kaist.team888.util.Alert#log(Object, String)}
 * : print log on console.</li>
 * <li>{@link kr.ac.kaist.team888.util.Alert#popup(Context, String, String)}
 * : show popup on screen.</li>
 */
public class Alert {
  private static final String TAG = "DEBUG";
  private static final String FORMAT = "[%s]: %s\n(%s)";

  /**
   * Log.d() wrapper method.
   *
   * <p> Print message as following format.
   * <br> [ClassName]: message to print
   * <br> (Caller line number trace)
   *
   * @param caller who directly called log method.
   * @param msg message to print out.
   */
  public static void log(Object caller, String msg) {
    String className = caller.getClass().getSimpleName();

    String callerTrace = Thread.currentThread().getStackTrace()[2].toString();

    Log.d(TAG, String.format(FORMAT, className, msg, callerTrace));
  }

  /**
   * Shows simple alertDialog.
   *
   * @param context current context.
   * @param title title of popup.
   * @param msg message of popup.
   */
  public static void popup(Context context, String title, String msg) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(title);
    alert.setMessage(msg);
    alert.show();
  }
}
