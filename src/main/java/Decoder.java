import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Decoder {

  public static void decode(final InputStream in, final OutputStream out) {
    try (final ObjectInputStream inObj = new ObjectInputStream(in)) {
      final int height = inObj.readShort();
      final int width = inObj.readShort();
      ByteArrayOutputStream outR = new ByteArrayOutputStream();
      ByteArrayOutputStream outG = new ByteArrayOutputStream();
      ByteArrayOutputStream outB = new ByteArrayOutputStream();

      decodeComponent(inObj, outR);
      decodeComponent(inObj, outG);
      decodeComponent(inObj, outB);

      final BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      unite(outR.toByteArray(), outG.toByteArray(), outB.toByteArray(), outImg);
      //final String name = fileOut.getName();
      //ImageIO.write(out, name.substring(name.lastIndexOf('.') + 1), fileOut);
      ImageIO.write(outImg, "png", out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void decodeComponent(final InputStream in, final OutputStream out) {
    try {
      byte num;
      int r;
      byte last = 0;
      while ((r = in.read()) != -1) {
        num = (byte) r;
        if (num == 0) {
          break;
        }
        if (num == 1) {
          throw new Exception();
        }
        if (num > 0) {
          if ((r = in.read()) == -1) {
            throw new Exception();
          }
          for (int i = 0; i < num; ++i) {
            out.write((byte) (r + last));
            last = (byte) (r + last);
          }
        } else {
          for (int i = 0; i < -num; ++i) {
            if ((r = in.read()) == -1) {
              throw new Exception();
            }
            out.write((byte) (r + last));
            last = (byte) (r + last);
          }
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
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
}
