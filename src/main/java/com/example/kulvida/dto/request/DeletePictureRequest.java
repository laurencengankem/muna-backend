package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeletePictureRequest {

    private Integer itemId;
    private String url;
}
