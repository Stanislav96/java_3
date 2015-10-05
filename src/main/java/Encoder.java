import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Encoder {
  public static void encode(final File fileIn, final File fileOut) {
    try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileOut))) {
      final BufferedImage im = ImageIO.read(fileIn);
      final int height = im.getHeight();
      final int width = im.getWidth();
      final byte[] bufR = new byte[height * width];
      final byte[] bufG = new byte[height * width];
      final byte[] bufB = new byte[height * width];
      divide(im, bufR, bufG, bufB);

      out.writeShort(height);
      out.writeShort(width);
      encodeComponent(new ByteArrayInputStream(bufR), out);
      out.write(0);
      encodeComponent(new ByteArrayInputStream(bufG), out);
      out.write(0);
      encodeComponent(new ByteArrayInputStream(bufB), out);

    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public static void encodeComponent(final InputStream in, final OutputStream out) {
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
  }

  private static void divide(final BufferedImage im, final byte[] bufR, final byte[] bufG, final byte[] bufB) {
    final int height = im.getHeight();
    final int width = im.getWidth();
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        Color c = new Color(im.getRGB(j, i));
        bufR[i * width + j] = (byte) c.getRed();
        bufG[i * width + j] = (byte) c.getGreen();
        bufB[i * width + j] = (byte) c.getBlue();
      }
    }
  }
}
