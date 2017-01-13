package lib;

import java.io.Serializable;

public class FilePart implements Serializable {
    public static final int MAX_SIZE = 1024;
    private int part;
    private String fileName;
    private byte[] bytes;
    int length;

    private FilePart(FilePartBuilder builder){
        this.part = builder.part;
        this.fileName = builder.fileName;
        this.bytes = builder.bytes;
        this.length = builder.length;
    }

    public int getPart() {
        return part;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getLength() {
        return length;
    }

    public static class FilePartBuilder{
        private int part;
        private String fileName;
        private byte[] bytes;
        private int length;

        public FilePartBuilder part(int part){
            this.part = part;
            return this;
        }

        public FilePartBuilder fileName(String fileName){
            this.fileName = fileName;
            return this;
        }

        public FilePartBuilder bytes(byte[] bytes){
            this.bytes = bytes;
            return this;
        }

        public FilePartBuilder length(int length){
            this.length = length;
            return this;
        }

        public FilePart build(){
            return new FilePart(this);
        }
    }

}