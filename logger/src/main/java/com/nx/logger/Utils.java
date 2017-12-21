package com.nx.logger;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static com.nx.logger.Logger.ASSERT;
import static com.nx.logger.Logger.DEBUG;
import static com.nx.logger.Logger.ERROR;
import static com.nx.logger.Logger.INFO;
import static com.nx.logger.Logger.VERBOSE;
import static com.nx.logger.Logger.WARN;

/**
 * Provides convenient methods to some common operations
 */
public final class Utils {

  /**
   * It is used for json pretty print
   */
  public static final int JSON_INDENT = 4;

  private Utils() {
    // Hidden constructor.
  }

  /**
   * Returns true if the string is null or 0-length.
   *
   * @param str the string to be examined
   * @return true if str is null or zero length
   */
  public static boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  /**
   * Returns true if a and b are equal, including if they are both null.
   * <p><i>Note: In platform versions 1.1 and earlier, this methodCount only worked well if
   * both the arguments were instances of String.</i></p>
   *
   * @param a first CharSequence to check
   * @param b second CharSequence to check
   * @return true if a and b are equal
   * <p>
   * NOTE: Logic slightly change due to strict policy on CI -
   * "Inner assignments should be avoided"
   */
  public static boolean equals(CharSequence a, CharSequence b) {
    if (a == b) return true;
    if (a != null && b != null) {
      int length = a.length();
      if (length == b.length()) {
        if (a instanceof String && b instanceof String) {
          return a.equals(b);
        } else {
          for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) return false;
          }
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
   * in unit tests.
   *
   * @return Stack trace in form of String
   */
  public static String getStackTraceString(Throwable tr) {
    if (tr == null) {
      return "";
    }

    // This is to reduce the amount of log spew that apps do in the non-error
    // condition of the network being unavailable.
    Throwable t = tr;
    while (t != null) {
      if (t instanceof UnknownHostException) {
        return "";
      }
      t = t.getCause();
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    tr.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }

  public static String logLevel(int value) {
    switch (value) {
      case VERBOSE:
        return "VERBOSE";
      case DEBUG:
        return "DEBUG";
      case INFO:
        return "INFO";
      case WARN:
        return "WARN";
      case ERROR:
        return "ERROR";
      case ASSERT:
        return "ASSERT";
      default:
        return "UNKNOWN";
    }
  }

  public static String toString(Object object) {
    if (object == null) {
      return "null";
    }
    if (!object.getClass().isArray()) {
      return object.toString();
    }
    if (object instanceof boolean[]) {
      return Arrays.toString((boolean[]) object);
    }
    if (object instanceof byte[]) {
      return Arrays.toString((byte[]) object);
    }
    if (object instanceof char[]) {
      return Arrays.toString((char[]) object);
    }
    if (object instanceof short[]) {
      return Arrays.toString((short[]) object);
    }
    if (object instanceof int[]) {
      return Arrays.toString((int[]) object);
    }
    if (object instanceof long[]) {
      return Arrays.toString((long[]) object);
    }
    if (object instanceof float[]) {
      return Arrays.toString((float[]) object);
    }
    if (object instanceof double[]) {
      return Arrays.toString((double[]) object);
    }
    if (object instanceof Object[]) {
      return Arrays.deepToString((Object[]) object);
    }
    return "Couldn't find a correct type for the object";
  }

  /**
   * 格式化对象
   *
   * @param obj
   * @return
   */
  public static String parseObjectMessage(Object obj) {
    if (obj != null) {
      try {
        if (obj instanceof List) {
          JSONArray jsonArray = new JSONArray();
          for (Object o : (List) obj) {
            JSONObject jo = new JSONObject(new Gson().toJson(o));
            jsonArray.put(jo);
          }
          String message = jsonArray.toString(JSON_INDENT);
          return message;
        } else if (obj instanceof Map) {
          Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
          JSONObject jsonObject = new JSONObject(gson.toJson(obj));
          String message = jsonObject.toString(JSON_INDENT);
          return message;
        } else {
          JSONObject jsonObject = new JSONObject(new Gson().toJson(obj));
          String message = jsonObject.toString(JSON_INDENT);
          return message;
        }
      } catch (JSONException e) {
        return "Invalid object content";
      }
    } else {
      return "Null object content";
    }
  }
  /**
   * 格式化xml字符串
   *
   * @param xml
   * @return
   */
  public static String parseXmlMessage(String xml) {
    if (!TextUtils.isEmpty(xml)) {
      try {
        Source xmlInput = new StreamSource(new StringReader(xml));
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
      } catch (TransformerException e) {
        return "Invalid xml content";
      }
    } else {
      return "Empty/Null xml content";
    }
  }

  /**
   * 格式化json字符串
   *
   * @param json
   * @return
   */
  public static String parseJsonMessage(String json) {
    if (!TextUtils.isEmpty(json)) {
      try {
        json = json.trim();
        if (json.startsWith("{")) {
          JSONObject jsonObject = new JSONObject(json);
          String message = jsonObject.toString(JSON_INDENT);
          return message;
        } else if (json.startsWith("[")) {
          JSONArray jsonArray = new JSONArray(json);
          String message = jsonArray.toString(JSON_INDENT);
          return message;
        } else {
          return "Invalid json content";
        }
      } catch (JSONException e) {
        return "Invalid json content";
      }
    } else {
      return "Empty/Null json content";
    }
  }
}
