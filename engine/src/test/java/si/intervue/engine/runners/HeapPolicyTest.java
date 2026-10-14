package si.intervue.engine.runners;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HeapPolicyTest {

  private static final long GB = 1024L * 1024 * 1024;

  @Test
  void takesAQuarterOfMemory() {
    assertThat(HeapPolicy.forTotalMemory(8 * GB)).isEqualTo(2048);
  }

  @Test
  void neverGoesBelow512MbOrAbove4Gb() {
    assertThat(HeapPolicy.forTotalMemory(GB)).isEqualTo(512);
    assertThat(HeapPolicy.forTotalMemory(64 * GB)).isEqualTo(4096);
  }

  @Test
  void thisMachineGetsAValueInRange() {
    assertThat(HeapPolicy.defaultMb()).isBetween(512, 4096);
  }
}
