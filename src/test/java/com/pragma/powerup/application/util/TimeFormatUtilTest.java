package com.pragma.powerup.application.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TimeFormatUtilTest {

  @Test
  void formatSecondsToMinSec_WithZeroSeconds_ReturnsZeroFormat() {
    assertEquals("0:00", TimeFormatUtil.formatSecondsToMinSec(0));
  }

  @Test
  void formatSecondsToMinSec_WithLessThanMinute_ReturnsOnlySeconds() {
    assertEquals("0:30", TimeFormatUtil.formatSecondsToMinSec(30));
    assertEquals("0:59", TimeFormatUtil.formatSecondsToMinSec(59));
  }

  @Test
  void formatSecondsToMinSec_WithExactMinutes_ReturnsMinutesWithZeroSeconds() {
    assertEquals("1:00", TimeFormatUtil.formatSecondsToMinSec(60));
    assertEquals("5:00", TimeFormatUtil.formatSecondsToMinSec(300));
  }

  @Test
  void formatSecondsToMinSec_WithMinutesAndSeconds_ReturnsCorrectFormat() {
    assertEquals("1:30", TimeFormatUtil.formatSecondsToMinSec(90));
    assertEquals("2:15", TimeFormatUtil.formatSecondsToMinSec(135));
    assertEquals("29:03", TimeFormatUtil.formatSecondsToMinSec(1743));
  }

  @Test
  void formatSecondsToMinSec_WithRealExamples_ReturnsExpectedFormat() {
    // Empleado 8: 79 segundos = 1:19
    assertEquals("1:19", TimeFormatUtil.formatSecondsToMinSec(79));

    // Empleado 3: 2293 segundos = 38:13
    assertEquals("38:13", TimeFormatUtil.formatSecondsToMinSec(2293));

    // Restaurant average: 1740 segundos = 29:00
    assertEquals("29:00", TimeFormatUtil.formatSecondsToMinSec(1740));
  }

  @Test
  void formatSecondsToMinSec_WithDouble_RoundsCorrectly() {
  assertEquals("1:45", TimeFormatUtil.formatSecondsToMinSec(105.4));
  assertEquals("1:46", TimeFormatUtil.formatSecondsToMinSec(105.6));
  }

  @Test
  void formatSecondsToMinSec_WithNegativeValue_ReturnsZeroFormat() {
    assertEquals("0:00", TimeFormatUtil.formatSecondsToMinSec(-10));
  }
}
