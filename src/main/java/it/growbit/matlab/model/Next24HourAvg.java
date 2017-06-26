package it.growbit.matlab.model;

import com.google.api.client.util.Key;

/**
 * Created by name on 24/06/17.
 */
public class Next24HourAvg {

    @Key
    private Double avg;

    public Next24HourAvg() {
    }

    public Next24HourAvg(Double avg) {
        this.avg = avg;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }
}
