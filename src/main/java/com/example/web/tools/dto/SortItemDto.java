package com.example.web.tools.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SortItemDto {

    @JsonProperty("FieldName")
    private String FieldName;

    @JsonProperty("IsAsc")
    private Boolean IsAsc;
}
