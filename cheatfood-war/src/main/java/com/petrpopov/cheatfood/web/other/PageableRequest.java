package com.petrpopov.cheatfood.web.other;

import javax.validation.constraints.Min;

/**
 * User: petrpopov
 * Date: 19.09.13
 * Time: 17:59
 */

public class PageableRequest {

    private int page;

    @Min(value = 1)
    private int size;

    public PageableRequest() {
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
