package cz.kec.oracle.jakarta.hw;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import cz.kec.oracle.jakarta.hw.util.EntryMarshaller;
import cz.kec.oracle.jakarta.hw.util.LoggingRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XmlStreamServer
 *
 * @author kec
 * @since 2.8.19
 */
public class XmlStreamServer {

    private static Logger LOG = LoggerFactory.getLogger(XmlStreamServer.class);

    private AtomicBoolean shouldRun = new AtomicBoolean(true);
    private EntryMarshaller entryMarshaller = new EntryMarshaller();
    private int port;

    public XmlStreamServer(int port) {
        this.port = port;
    }

    public void startServer() {
        LOG.info("Starting server localhost:{}", port);
        shouldRun.set(true);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (shouldRun.get()) {
                try (Socket socket = serverSocket.accept()) {
                    final OutputStream outputStream = socket.getOutputStream();
                    //Keep test server simple, serve only one client at the time
                    while (shouldRun.get()) {
                        try {
                            Random random = new Random();
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(100) + 100);
                            EntryDto entryDto = new EntryDto(random.nextInt(100) - 50 + "." + random.nextInt(100), System.currentTimeMillis());
                            outputStream.write((entryMarshaller.marshallToXml(entryDto) + "\n").getBytes());
                        } catch (IOException e) {
                            if (e instanceof SocketException) {
                                LOG.warn("Client stopped receiving");
                                break;
                            } else {
                                LOG.error("Error writing to output stream", e);
                            }
                        } catch (InterruptedException e) {
                            throw new LoggingRuntimeException("Error when slowing down server", e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new LoggingRuntimeException("Can't start server", e);
        }
    }

    public void stopServer() {
        LOG.info("Stopping server {}", getPort());
        this.shouldRun.set(false);
    }

    public static void main(String[] args) throws IOException {
        final XmlStreamServer xmlStreamServer = new XmlStreamServer(9696);
        xmlStreamServer.startServer();
    }

    public int getPort() {
        return port;
    }
}
