package util;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

public class MockServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    public MockServletInputStream(byte[] bytes) {
        this.inputStream = new ByteArrayInputStream(bytes);
    }

    @Override
    public int read() {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}