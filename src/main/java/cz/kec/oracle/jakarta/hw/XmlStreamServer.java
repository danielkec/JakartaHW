package cz.kec.oracle.jakarta.hw;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private int port;

    private XmlStreamServer(int port) throws IOException {
        this.port = port;
        LOG.info("Starting server localhost:{}", port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (shouldRun.get()) {
            try (Socket socket = serverSocket.accept()) {
                final OutputStream outputStream = socket.getOutputStream();
                //Keep test server simple, serve only one client at the time
                while (shouldRun.get()) {
                    try {
                        Random random = new Random();
                        TimeUnit.MILLISECONDS.sleep(random.nextInt(100) + 100);
                        outputStream.write(String.format("<data> <timeStamp>%d</timeStamp> <amount>%s.%s</amount> </data>\n", System.currentTimeMillis(),
                                random.nextInt(100) - 50, random.nextInt(100)).getBytes());
                    } catch (IOException e) {
                        if (e instanceof SocketException) {
                            LOG.warn("Client stopped receiving");
                            break;
                        } else {
                            LOG.error("Error writing to output stream", e);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    //TODO: Extract start server to standalone method
    public void  stopServer() {
        LOG.info("Stopping server {}", getPort());
        this.shouldRun.set(false);
    }

    public static void main(String[] args) throws IOException {
        new XmlStreamServer(9696);
    }

    public static XmlStreamServer startServer(int port) {
        try {
            return new XmlStreamServer(port);
        } catch (IOException e) {
            throw new RuntimeException("Can't start server", e);
        }
    }

    public int getPort() {
        return port;
    }
}
