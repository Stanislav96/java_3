import java.io.IOException;
import java.io.OutputStream;


public class EncodeDeltaOutputStream extends OutputStream {
  private OutputStream out;
  private byte last;

  public EncodeDeltaOutputStream(OutputStream out) {
    this.out = out;
    last = 0;
  }

  public void write(final int r) {
    try {
      if (r == -1) {
        out.write(-1);
        return;
      }
      out.write(
          ((byte) (r - last) + 256) % 256); // difference between 2 bytes may be not byte but if b is byte, b + 256
          // == b and if x
      // is int, (x - (byte) x) % 256 == 0. Thus we can cast the difference to byte.
      last = (byte) r;
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
