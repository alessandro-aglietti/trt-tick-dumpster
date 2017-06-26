package it.growbit.matlab.model;

import com.google.api.client.util.Key;
import it.growbit.model.trt.Trades_last_24;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by name on 24/06/17.
 */
public class Last24HoursAvg {

    @Key
    private List<Double> avgs = new ArrayList<Double>();

    public Last24HoursAvg() {

    }

    public Last24HoursAvg(List<Trades_last_24> avgs) {
        this.setAvgsFromTrades(avgs);
    }

    public List<Double> getAvgs() {
        return avgs;
    }

    public void setAvgs(List<Double> avgs) {
        this.avgs = avgs;
    }

    public void setAvgsFromTrades(List<Trades_last_24> avgs) {
        List<Double> davgs = new ArrayList<Double>();

        for (Trades_last_24 t : avgs) {
            davgs.add(t.getTf_price());
        }

        this.avgs = davgs;
    }

}
