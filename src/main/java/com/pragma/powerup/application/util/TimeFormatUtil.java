package com.pragma.powerup.application.util;

public class TimeFormatUtil {

  private TimeFormatUtil() {
    // Utility class - private constructor
  }

  /**
   * Converts seconds to MM:SS format
   *
   * @param seconds Total seconds
   * @return String in MM:SS format (e.g., "2:30" for 150 seconds)
   */
  public static String formatSecondsToMinSec(int seconds) {
    if (seconds < 0) {
      return "0:00";
    }

    int minutes = seconds / 60;
    int remainingSeconds = seconds % 60;

    return String.format("%d:%02d", minutes, remainingSeconds);
  }

  /**
   * Converts seconds (as double) to MM:SS format
   *
   * @param seconds Total seconds as double
   * @return String in MM:SS format
   */
  public static String formatSecondsToMinSec(double seconds) {
    return formatSecondsToMinSec((int) Math.round(seconds));
  }
}
