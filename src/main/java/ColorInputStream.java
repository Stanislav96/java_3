import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;


public class ColorInputStream extends ObjectInputStream {
  public enum ColorIter {
    RED,
    GREEN,
    BLUE
  }

  private int i, j;
  private final int height, width;
  private final BufferedImage im;
  private final ColorIter color;

  ColorInputStream(BufferedImage im, ColorIter color) throws IOException {
    i = 0;
    j = 0;
    this.im = im;
    this.height = im.getHeight();
    this.width = im.getWidth();
    this.color = color;
  }

  public int read() {
    if (j < width - 1) {
      if (i == height) {
        i = 0;
        return -1;
      }
      ++j;
      switch (color) {
      case RED:
        return new Color(im.getRGB(j - 1, i)).getRed();
      case GREEN:
        return new Color(im.getRGB(j - 1, i)).getGreen();
      case BLUE:
        return new Color(im.getRGB(j - 1, i)).getBlue();
      }
    }
    j = 0;
    ++i;
    switch (color) {
    case RED:
      return new Color(im.getRGB(width - 1, i - 1)).getRed();
    case GREEN:
      return new Color(im.getRGB(width - 1, i - 1)).getGreen();
    case BLUE:
      return new Color(im.getRGB(width - 1, i - 1)).getBlue();
    }
    return -1;
  }
}
