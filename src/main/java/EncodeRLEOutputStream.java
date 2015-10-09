import java.io.IOException;
import java.io.OutputStream;

public class EncodeRLEOutputStream extends OutputStream {

  private OutputStream out;
  private int sizeBuf = 0;
  private int numOfSame = 0;
  private byte[] buf = new byte[128];
  private byte bOld;
  private boolean first = true;

  public EncodeRLEOutputStream(OutputStream out) {
    this.out = out;
  }

  public void write(final int r) {
    if (r == -1) {
      if (numOfSame > 0) {
        chooseBufOrSame(r);
      }
      return;
    }
    if (first) {
      bOld = (byte) r;
      first = false;
      numOfSame = 1;
      return;
    }
    byte b = (byte) r;
    if (bOld == b && numOfSame < 127) {
      ++numOfSame;
      return;
    }
    chooseBufOrSame(r);
    numOfSame = 1;
    bOld = b;
  }

  private void chooseBufOrSame(final int r) {
    try {
      if (numOfSame == 1 || (numOfSame == 2 && sizeBuf > 0 && sizeBuf < 127)) {
        for (int i = 0; i < numOfSame; ++i) {
          buf[sizeBuf] = bOld;
          ++sizeBuf;
        }
        if (sizeBuf == 128 || r == -1) {
          out.write(-sizeBuf);
          out.write(buf, 0, sizeBuf);
          sizeBuf = 0;
        }
      } else {
        if (sizeBuf > 0) {
          out.write(-sizeBuf);
          out.write(buf, 0, sizeBuf);
          sizeBuf = 0;
        }
        out.write(numOfSame);
        out.write((bOld + 256) % 256);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
