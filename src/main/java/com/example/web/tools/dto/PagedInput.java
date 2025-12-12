package com.example.web.tools.dto;

import lombok.Data;

@Data
public class PagedInput {

    /**
     * 页大小
     */
    private Long Limit;

    /**
     * 页码
     */
    private Long Page;

    private SortItemDto SortItem;


    private String KeyWord;

    public PagedInput() {
        if (Limit == null || Limit == 0) {
            Limit = Long.valueOf(100);
        }
        if (Page == null || Page == 0) {
            Page = Long.valueOf(1);
        }
    }
}
