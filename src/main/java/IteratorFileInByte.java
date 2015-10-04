import java.io.ObjectInputStream;
import java.util.Iterator;

public class IteratorFileInByte implements Iterator<Byte> {
  private final ObjectInputStream in;
  private byte b;

  IteratorFileInByte(ObjectInputStream in) {
    this.in = in;
  }

  public Byte next() {
    try {
      b = in.readByte();
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return b;
  }

  public boolean hasNext() {
    return true;
  }
}
