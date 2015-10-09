import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public class TestCode {
  @Test
  public void testSamePng() {
    testSame(getClass().getClassLoader().getResource("NsmbMario.png").getFile(), "NsmbMario.enc", "1.png");
  }

  @Test
  public void testSameJpg() {
    testSame(getClass().getClassLoader().getResource("1199784212.jpg").getFile(), "1199784212.enc", "2.png");
  }

  @Test
  public void testSameByteArray() {
    byte[] in = new byte[100];
    ByteArrayOutputStream enc = new ByteArrayOutputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Random rand = new Random(System.currentTimeMillis());
    rand.nextBytes(in);
    Encoder.encodeComponent(new ByteArrayInputStream(in), enc);
    Decoder.decodeComponent(new ByteArrayInputStream(enc.toByteArray()), out);
    Assert.assertArrayEquals(in, out.toByteArray());
  }

  private void testSame(final String in, final String enc, final String out) {
    try (final FileInputStream fisOldIn = new FileInputStream(in)) {
      try (final FileOutputStream fisEncOut = new FileOutputStream(enc)) {
        Encoder.encode(fisOldIn, fisEncOut);
      } catch (final IOException e) {
        e.printStackTrace();
      }
      try (final FileInputStream fisEncIn = new FileInputStream(enc);
           final FileOutputStream fisNewOut = new FileOutputStream(out)) {
        Decoder.decode(fisEncIn, fisNewOut);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    try (final FileInputStream fisOldIn = new FileInputStream(in);
         final FileInputStream fisNewIn = new FileInputStream(out)) {
      Assert.assertTrue(checkSame(fisOldIn, fisNewIn));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private boolean checkSame(final InputStream inOld, final InputStream inNew) {
    try {
      final BufferedImage imOld = ImageIO.read(inOld);
      final BufferedImage imNew = ImageIO.read(inNew);
      final int height = imOld.getHeight();
      final int width = imOld.getWidth();
      if (height != imNew.getHeight() || width != imNew.getWidth()) {
        return false;
      }
      for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
          Color[] c = new Color[2];
          c[0] = new Color(imOld.getRGB(j, i));
          c[1] = new Color(imNew.getRGB(j, i));
          if ((byte) c[0].getRed() != (byte) c[1].getRed() || (byte) c[0].getGreen() != (byte) c[1].getGreen() ||
              (byte) c[0].getBlue() != (byte) c[1].getBlue()) {
            return false;
          }
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return true;
  }
}
