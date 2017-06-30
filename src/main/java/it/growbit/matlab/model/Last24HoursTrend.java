package it.growbit.matlab.model;

import it.growbit.model.trt.Trades_last_24;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by name on 30/06/17.
 */
public class Last24HoursTrend extends Last24HoursAvg {

    public Last24HoursTrend(List<Trades_last_24> avgs) {
        super(avgs);
    }

    @Override
    public void setAvgsFromTrades(List<Trades_last_24> avgs) {
        /**
         * calcolare trend
         */
        List<Double> davgs = new ArrayList<Double>();

        /**
         * sono 25 valori
         * il primo lo uso solo per calcolare il trend del secondo
         */
        for (int i = 1; i < avgs.size(); i++) {

            Double previous = avgs.get(i - 1).getTf_price();
            Double current = avgs.get(1).getTf_price();

            Double trend = ((current - previous) / previous) * 100;

            davgs.add(trend);
        }

        this.avgs = davgs;
    }
}
