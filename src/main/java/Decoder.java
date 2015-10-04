import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class Decoder {
  public static void decode(final File fileIn, final File fileOut) {
    try (final ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileIn))) {
      final int height = in.readShort();
      final int width = in.readShort();
      decode(height, width, new IteratorFileInByte(in), fileOut);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void decode(final int height, final int width, final Iterator<Byte> in, final File fileOut) {
    byte[] bufR = new byte[height * width];
    byte[] bufG = new byte[height * width];
    byte[] bufB = new byte[height * width];

    decodeComponent(in, bufR);
    decodeComponent(in, bufG);
    decodeComponent(in, bufB);

    final BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    unite(bufR, bufG, bufB, out);
    //final String name = fileOut.getName();
    try {
      //ImageIO.write(out, name.substring(name.lastIndexOf('.') + 1), fileOut);
      ImageIO.write(out, "png", fileOut);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void decodeComponent(final Iterator<Byte> in, final byte[] out) {
    decodeRLE(in, out);
    decodeDelta(out);
  }

  private static void unite(final byte[] bufR, final byte[] bufG, final byte[] bufB, final BufferedImage out) {
    final int height = out.getHeight();
    final int width = out.getWidth();
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        out.setRGB(j, i, new Color((bufR[i * width + j] + 256) % 256, (bufG[i * width + j] + 256) % 256,
            (bufB[i * width + j] + 256) % 256).getRGB());
      }
    }
  }

  private static void decodeRLE(final Iterator<Byte> in, final byte[] out) {
    byte b, num;
    int index = 0;
    while (in.hasNext()) {
      num = in.next();
      if (num == 0) {
        break;
      }
      if (num == 1) {
        System.exit(1);
      }
      if (num > 0) {
        if (!in.hasNext()) {
          System.exit(1);
        }
        b = in.next();
        for (int i = 0; i < num; ++i, ++index) {
          if (index >= out.length) {
            System.exit(1);
          }
          out[index] = b;
        }
      } else {
        for (int i = 0; i < -num; ++i, ++index) {
          if (!in.hasNext()) {
            System.exit(1);
          }
          b = in.next();
          if (index >= out.length) {
            System.exit(1);
          }
          out[index] = b;
        }
      }
    }
  }

  private static void decodeDelta(final byte[] out) {
    byte last = 0;
    for (int i = 0; i < out.length; ++i) {
      last += out[i];
      out[i] = last;
    }
  }
}
