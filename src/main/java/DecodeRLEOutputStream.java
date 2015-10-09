import java.io.OutputStream;
import java.util.SplittableRandom;

public class DecodeRLEOutputStream extends OutputStream {

  private OutputStream out;
  private int num;
  private int i;
  private Boolean[] shouldExitOnZero;

  public DecodeRLEOutputStream(OutputStream out, Boolean[] shouldExitOnZero /* Sorry for this, haven't found another
  way to pass this flag out to decodeComponent */) {
    this.out = out;
    num = 0;
    this.shouldExitOnZero = shouldExitOnZero;
  }

  public void write(final int r) {
    shouldExitOnZero[0] = false;
    try {
      if (num > 1) {
        if (r == -1) {
          throw new Exception();
        }
        for (int i = 0; i < num; ++i) {
          out.write(r);
        }
        num = 0;
        return;
      }
      if (num < 0) {
        if (i < -num) {
          out.write(r);
          ++i;
        } else {
          num = 0;
          i = 0;
        }
      }
      if (num == 0) {
        if (r == -1) {
          return;
        }
        num = (byte) r;
        if (num == 1) {
          throw new Exception();
        }
        if (num == 0) {
          shouldExitOnZero[0] = true;
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
