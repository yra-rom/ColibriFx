package gui.chat;

public class Message {
    private String text;
    private String time;
    private String from;
    private String to;

    private Message(MessageBuilder builder){
        this.text = builder.text;
        this.from = builder.from;
        this.time = builder.time;
        this.to = builder.to;
    }

    public static class MessageBuilder {
        private String text;
        private String time;
        private String from;
        private String to;

        public MessageBuilder text(String text){
            this.text = text;
            return this;
        }

        public MessageBuilder from(String from){
            this.from = from;
            return this;
        }

        public MessageBuilder to(String to){
            this.to = to;
            return this;
        }

        public MessageBuilder time(String time){
            this.time = time;
            return this;
        }

        public Message build(){
            return new Message(this);
        }
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return from + " :\t" + text + "\t\t\t\t" + time + "\n";
    }
}
