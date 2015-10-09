import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class ColorOutputStream extends OutputStream {
  public enum ColorIter {
    RED,
    GREEN,
    BLUE
  }

  private int i, j;
  private final int height, width;
  private final BufferedImage im;
  private ColorIter colorIter;

  ColorOutputStream(BufferedImage im) throws IOException {
    this.im = im;
    this.width = im.getWidth();
    this.height = im.getHeight();
    colorIter = ColorIter.RED;
  }

  public void write(final int r) {
    if (j < width - 1) {
      if (i == height) {
        switch (colorIter) {
        case RED:
          colorIter = ColorIter.GREEN;
          i = 0;
          break;
        case GREEN:
          colorIter = ColorIter.BLUE;
          i = 0;
        }
      }
      ++j;
      write(r, j - 1, i);
      return;
    }
    j = 0;
    ++i;
    write(r, width - 1, i - 1);
  }

  private void write(final int r, final int x, final int y) {
    Color color = new Color(im.getRGB(x, y));
    int red = color.getRed();
    int green = color.getGreen();
    int blue = color.getBlue();
    switch (colorIter) {
    case RED:
      red = (r + 256) % 256;
      break;
    case GREEN:
      green = (r + 256) % 256;
      break;
    case BLUE:
      blue = (r + 256) % 256;
    }
    im.setRGB(x, y, new Color(red, green, blue).getRGB());
  }
}
