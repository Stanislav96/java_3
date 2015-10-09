import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Encoder {

  public static void encode(final InputStream in, final OutputStream out) {
    try (final ObjectOutputStream outObj = new ObjectOutputStream(out)) {
      final BufferedImage im = ImageIO.read(in);
      final int height = im.getHeight();
      final int width = im.getWidth();

      outObj.writeShort(height);
      outObj.writeShort(width);
      encodeComponent(new ColorInputStream(im, ColorInputStream.ColorIter.RED), outObj);
      outObj.write(0);
      encodeComponent(new ColorInputStream(im, ColorInputStream.ColorIter.GREEN), outObj);
      outObj.write(0);
      encodeComponent(new ColorInputStream(im, ColorInputStream.ColorIter.BLUE), outObj);

    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public static void encodeComponent(final InputStream in, final OutputStream out) {
    int r;
    OutputStream outEnc = new EncodeDeltaOutputStream(new EncodeRLEOutputStream(out));
    try {
      do {
        r = in.read();
        outEnc.write(r);
      } while (r != -1);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /*public static void encodeComponent(final InputStream in, final OutputStream out) {
    try {
      byte bOld, b;
      int sizeBuf = 0;
      int numOfSame;
      byte[] buf = new byte[128];
      byte last = 0;
      int r;
      if ((r = in.read()) != -1) {
        b = (byte) (r - last); // difference between 2 bytes may be not byte but if b is byte, b + 256 == b and if x
        // is int, (x - (byte) x) % 256 == 0. Thus we can cast the difference to byte.
        last = (byte) r;
        do {
          bOld = b;
          numOfSame = 1;
          while ((r = in.read()) != -1) {
            b = (byte) (r - last);
            last = (byte) r;
            if (bOld != b || numOfSame >= 127) {
              break;
            }
            ++numOfSame;
          }
          if (numOfSame == 1 || (numOfSame == 2 && sizeBuf > 0 && sizeBuf < 127)) {
            for (int i = 0; i < numOfSame; ++i) {
              buf[sizeBuf] = bOld;
              ++sizeBuf;
            }
            if (sizeBuf == 128) {
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
            out.write(bOld);
          }
        } while (r != -1);
        if (sizeBuf > 0) {
          out.write(-sizeBuf);
          out.write(buf, 0, sizeBuf);
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }*/
}
