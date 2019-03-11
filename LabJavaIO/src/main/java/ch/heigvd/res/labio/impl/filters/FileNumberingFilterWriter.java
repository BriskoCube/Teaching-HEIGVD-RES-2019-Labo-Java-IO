package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

  private int lineNumber = 0;
  private char lastChar;

  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    for (int i = off; i < off + len && i < str.length(); i++) {
      write(str.charAt(i));
    }
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    for (int i = off; i < off + len && i < cbuf.length; i++) {
      write(cbuf[i]);
    }
  }

  @Override
  public void write(int c) throws IOException {

    if(lineNumber == 0)
      writeLineNb("");


    if(lastChar == '\r') {
      // \r follwed by \n -> windows. Windows uses CRLF
      String lineReturn = (char)c == '\n' ? "\r\n": "\r";
      writeLineNb(lineReturn);
    } else if((char)c == '\n') { // UNIX new line
      writeLineNb("\n");
    }

    // Write char only if not CR or LF
    if((char)c != '\n' && (char)c != '\r')
      super.write(c);

    lastChar = (char)c;

  }

  private void writeLineNb(String appendBefore) throws IOException {
    String lineNb = String.format("%s%d\t", appendBefore, ++lineNumber);

    super.write(lineNb, 0, lineNb.length());
  }

}
