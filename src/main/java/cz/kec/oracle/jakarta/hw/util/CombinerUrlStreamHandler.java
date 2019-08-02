package cz.kec.oracle.jakarta.hw.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CombinerUrlStreamHandler
 *
 * @author kec
 * @since 2.8.19
 */
public class CombinerUrlStreamHandler extends URLStreamHandler {

    private Logger LOG = LoggerFactory.getLogger(CombinerUrlStreamHandler.class);

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new URLConnection(u) {

            @Override
            public void connect() throws IOException {
                LOG.info("Connected with {} custom Jakarta stream protocol", url.toString());
            }

            @Override
            public InputStream getInputStream() throws IOException {
                final String host = this.getURL().getHost();
                final int port = this.getURL().getPort();
                final Socket socket = new Socket(host, port);
                return socket.getInputStream();
            }
        };
    }
}
