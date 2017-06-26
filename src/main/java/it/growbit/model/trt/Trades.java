package it.growbit.model.trt;

import com.google.api.client.util.Key;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by name on 05/06/17.
 */
public class Trades {
    private static final Logger log = Logger.getLogger(Trades.class.getName());

    @Key
    private List<Trade> trades;

    @Key
    private TradesMeta meta;

    public TradesMeta getMeta() {
        return meta;
    }

    public void persist() {
        log.info("trades to be persisted " + trades.size());
        for (Trade t: trades ) {
            try {
                t.persist();
            } catch (Exception e ) {
                log.severe("Exception su trade persist " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public void setMeta(TradesMeta meta) {
        this.meta = meta;
    }
}
