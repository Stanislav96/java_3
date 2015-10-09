import java.io.IOException;
import java.io.OutputStream;

public class DecodeDeltaOutputStream extends OutputStream {
  private OutputStream out;
  private byte last;

  public DecodeDeltaOutputStream(OutputStream out) {
    this.out = out;
    last = 0;
  }

  public void write(final int r) {
    if (r == -1) {
      return;
    }
    try {
      out.write(((byte) (r + last) + 256) % 256);
      last = (byte) (r + last);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
