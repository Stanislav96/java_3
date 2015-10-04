import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class Encoder {
  public static void encode(final File fileIn, final File fileOut) {
    try {
      final BufferedImage im = ImageIO.read(fileIn);
      final int height = im.getHeight();
      final int width = im.getWidth();
      encode(height, width, new IteratorFileInInt(height, width, im), fileOut);
    } catch (final IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void encode(final int height, final int width, final Iterator<Integer> in, final File fileOut) {
    try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileOut))) {
      final byte[] bufR = new byte[height * width];
      final byte[] bufG = new byte[height * width];
      final byte[] bufB = new byte[height * width];
      divide(height, width, in, bufR, bufG, bufB);

      out.writeShort(height);
      out.writeShort(width);
      encodeComponent(bufR, out);
      encodeComponent(bufG, out);
      encodeComponent(bufB, out);

    } catch (final IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void encodeComponent(final byte[] in, final OutputStream out) {
    encodeDelta(in);
    encodeRLE(in, out);
  }

  private static void divide(final int height, final int width, final Iterator<Integer> in, final byte[] bufR, final byte[] bufG, final byte[] bufB) {
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        Color c = new Color(in.next());
        bufR[i * width + j] = (byte) c.getRed();
        bufG[i * width + j] = (byte) c.getGreen();
        bufB[i * width + j] = (byte) c.getBlue();
      }
    }
  }


  private static void encodeDelta(final byte[] in) {
    byte last = 0;
    for (int i = 0; i < in.length; ++i) {
      byte t = (byte) (in[i] - last);
      last = in[i];
      in[i] = t;
    }
  }

  private static void encodeRLE(final byte[] in, final OutputStream out) {
    try {
      byte sym;
      int sizeBuf = 0;
      int numOfSame;
      byte[] buf = new byte[128];
      if (in.length > 0) {
        int index = 0;
        do {
          sym = in[index];
          ++index;
          numOfSame = 1;
          while (in.length > index && in[index] == sym && numOfSame < 127) {
            ++numOfSame;
            ++index;
          }
          if (numOfSame == 1 || (numOfSame == 2 && sizeBuf > 0 && sizeBuf < 127)) {
            for (int i = 0; i < numOfSame; ++i) {
              buf[sizeBuf] = sym;
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
            out.write(sym);
          }
        } while (in.length > index);
        if (sizeBuf > 0) {
          out.write(-sizeBuf);
          out.write(buf, 0, sizeBuf);
        }
      }
      out.write(0);
    } catch (final IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
