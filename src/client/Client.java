package client;

public final class Client {
    private String nick;
    private String email;
    private String pass;
    private boolean confirmed;

    private Client(ClientBuilder builder) {
        this.nick = builder.nick;
        this.email = builder.email;
        this.pass  = builder.pass;
        this.confirmed = false;
    }

    public String getNick() {
        return nick;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public static class ClientBuilder {

        private String nick;
        private String email;
        private String pass;

        public ClientBuilder nick(String nick){
            this.nick = nick;
            return this;
        }

        public ClientBuilder email(String email){
            this.email = email;
            return this;
        }

        public ClientBuilder pass(String pass){
            this.pass = pass;
            return this;
        }

        public Client build(){
            return new Client(this);
        }
    }
}
