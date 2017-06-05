package it.growbit.model.trt;

import com.google.api.client.util.Key;

/**
 * Created by name on 05/06/17.
 */
public class TradesMetaHref {
    @Key
    private String href;

    @Key
    private Integer page;

    public Integer getPage() {
        return page;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
