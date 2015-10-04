import java.awt.image.BufferedImage;
import java.util.Iterator;

public class IteratorFileInInt implements Iterator<Integer> {
  private int i, j;
  private final int height, width;
  private final BufferedImage im;

  IteratorFileInInt(int height, int width, BufferedImage im) {
    i = 0;
    j = 0;
    this.height = height;
    this.width = width;
    this.im = im;
  }

  public Integer next() {
    if (j < width - 1) {
      ++j;
      return im.getRGB(j - 1, i);
    }
    ++i;
    j = 0;
    return im.getRGB(width - 1, i - 1);
  }

  public boolean hasNext() {
    return i < height - 1 || j < width - 1;
  }
}
