package com.iflytek.spider.avro;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * Command-line "avro-tools" utilities should implement this
 * interface for delegation by {@link Main}.
 */
public interface Tool {
  /**
   * Runs the tool with supplied arguments.  Input and output streams
   * are customizable for easier testing.
   *
   * @param in Input stream to read data (typically System.in).
   * @param out Output of tool (typically System.out).
   * @param err Error stream (typically System.err).
   * @param args Non-null list of arguments.
   * @return result code (0 for success)
   * @throws Exception Just like main(), tools may throw Exception.
   */
  List<org.codehaus.jettison.json.JSONObject> run(InputStream in, PrintStream out, PrintStream err, List<String> args) throws Exception;

  /**
   * Name of tool, to be used in listings.
   */
  String getName();

  /**
   * 1-line description to be used in command listings.
   */
  String getShortDescription();
}
