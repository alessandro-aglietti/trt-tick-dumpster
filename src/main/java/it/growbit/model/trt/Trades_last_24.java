package it.growbit.model.trt;

import it.growbit.util.EMF;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by name on 23/06/17.
 */
@Entity
@Table(name = "Trades_tf_from_2016_hour_avg_last_24")
public class Trades_last_24 {

    @Id
    private String tf_hour;

    private Double tf_price;

    public String getTf_hour() {
        return tf_hour;
    }

    public void setTf_hour(String time) {
        this.tf_hour = time;
    }

    public Double getTf_price() {
        return tf_price;
    }

    public void setTf_price(Double tf_price) {
        this.tf_price = tf_price;
    }

    public static List<Trades_last_24> list() {
        return list(24);
    }

    public static List<Trades_last_24> list(Integer limit) {
        List<Trades_last_24> ret = new ArrayList<Trades_last_24>();
        EntityManager em = EMF.get_trt().createEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery("FROM Trades_last_24");
        query.setMaxResults(limit);

        ret = query.getResultList();

        em.getTransaction().commit();
        em.close();

        return ret;
    }
}
