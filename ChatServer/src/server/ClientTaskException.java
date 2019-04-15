package server;

import java.io.IOException;

class ClientTaskException extends Exception {

    ClientTaskException(String message, IOException ex) {
        super(message, ex);
    }
}
