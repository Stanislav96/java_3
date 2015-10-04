import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestCode {
  @Test
  public void TestSamePng() {
    File fileOld = new File("NsmbMario.png");
    File fileEnc = new File("NsmbMario.enc");
    File fileNew = new File("1.png");
    Encoder.encode(fileOld, fileEnc);
    Decoder.decode(fileEnc, fileNew);
    Assert.assertTrue(checkSame(fileOld, fileNew));
  }

  @Test
  public void TestSameJpg() {
    File fileOld = new File("1199784212.jpg");
    File fileEnc = new File("1199784212.enc");
    //File fileNew = new File("2.jpg");
    File fileNew = new File("2.png");
    Encoder.encode(fileOld, fileEnc);
    Decoder.decode(fileEnc, fileNew);
    Assert.assertTrue(checkSame(fileOld, fileNew));
  }

  private boolean checkSame(final File fileOld, final File fileNew) {
    try {
      final BufferedImage imOld = ImageIO.read(fileOld);
      final BufferedImage imNew = ImageIO.read(fileNew);
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
      System.exit(1);
    }
    return true;
  }
}
