package com.colibri.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class FilePart implements Serializable {
    public static final int MAX_SIZE = 1024;

    private final Integer part;
    private final String fileName;
    private final byte[] bytes;
    private final Integer length;
    private final String to;

}