package com.colibri.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class Message implements Serializable {
    private String text;
    private String time;
    private String from;
    private String to;

    @Override
    public String toString() {
        return from + " :\t" + text + "\t\t\t\t" + time + "\n";
    }
}
