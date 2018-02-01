package com.colibri.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Client implements Serializable {
    private String nick;
    private String email;
    private String pass;
    private Boolean confirmed;

    @Override
    public String toString() {
        return nick;
    }
}
