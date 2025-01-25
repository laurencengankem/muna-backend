package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchItemsRequest {

    private String txt;
    private String code;
    private String all;
}

