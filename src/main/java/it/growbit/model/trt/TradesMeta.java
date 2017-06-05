package it.growbit.model.trt;

import com.google.api.client.util.Key;

/**
 * Created by name on 05/06/17.
 */
public class TradesMeta {
    @Key
    private Integer total_count;

    @Key
    private TradesMetaHref first;

    @Key
    private TradesMetaHref previous;

    @Key
    private TradesMetaHref current;

    @Key
    private TradesMetaHref next;

    public Integer getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Integer total_count) {
        this.total_count = total_count;
    }

    public TradesMetaHref getFirst() {
        return first;
    }

    public void setFirst(TradesMetaHref first) {
        this.first = first;
    }

    public TradesMetaHref getPrevious() {
        return previous;
    }

    public void setPrevious(TradesMetaHref previous) {
        this.previous = previous;
    }

    public TradesMetaHref getCurrent() {
        return current;
    }

    public void setCurrent(TradesMetaHref current) {
        this.current = current;
    }

    public void setNext(TradesMetaHref next) {
        this.next = next;
    }

    public TradesMetaHref getLast() {
        return last;
    }

    public void setLast(TradesMetaHref last) {
        this.last = last;
    }

    @Key
    private TradesMetaHref last;

    public TradesMetaHref getNext() {
        return next;
    }
}
