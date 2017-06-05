package it.growbit.model.trt;

import com.google.api.client.util.Key;
import it.growbit.util.EMF;
import org.hibernate.annotations.SQLInsert;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by name on 05/06/17.
 */
@Entity
@Table(name = "Trades")
@SQLInsert(sql="INSERT IGNORE INTO Trades (amount, dark, date, fund_id, price, side, id) VALUES (?, ?, ?, ?, ?, ?, ?)")
public class Trade {
    @Key
    private String fund_id;

    @Key
    private Float amount;

    @Key
    private Float price;

    @Key
    private String side;

    @Key
    private Boolean dark;

    @Key
    private String date;

    @Key
    private Long id;

    @Id
    public Long getId() {
        return id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    public Date getDate() {
        return DateTime.parse(date).toDate();
    }

    public String getFund_id() {
        return fund_id;
    }

    public void setFund_id(String fund_id) {
        this.fund_id = fund_id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Boolean getDark() {
        return dark;
    }

    public void setDark(Boolean dark) {
        this.dark = dark;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void persist() {
        EntityManager em = EMF.get_trt().createEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
        em.close();
    }
}
