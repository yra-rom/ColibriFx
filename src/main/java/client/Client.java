package client;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public final class Client implements Serializable {
    private String nick;
    private String email;
    private String pass;
    private boolean confirmed;

    private Client(ClientBuilder builder) {
        this.nick = builder.nick;
        this.email = builder.email;
        this.pass = builder.pass;
        this.confirmed = false;
    }

    public static class ClientBuilder {

        private String nick;
        private String email;
        private String pass;

        public ClientBuilder nick(String nick) {
            this.nick = nick;
            return this;
        }

        public ClientBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ClientBuilder pass(String pass) {
            this.pass = pass;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }

    @Override
    public String toString() {
        return nick;
    }
}
