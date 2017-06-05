package it.growbit.util;

import com.google.appengine.api.utils.SystemProperty;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by name on 06/06/17.
 */

public final class EMF {
    static Map<String, String> properties = new HashMap();

    static {
        if (SystemProperty.environment.value() ==
                SystemProperty.Environment.Value.Production) {
            properties.put("javax.persistence.jdbc.driver",
                    "com.mysql.jdbc.GoogleDriver");
            properties.put("javax.persistence.jdbc.url",
                    System.getProperty("cloudsql.url"));
        } else {
            properties.put("javax.persistence.jdbc.driver",
                    "com.mysql.jdbc.Driver");
            properties.put("javax.persistence.jdbc.url",
                    System.getProperty("cloudsql.url.dev"));
        }
    }

    private static final EntityManagerFactory emf_trt = Persistence.createEntityManagerFactory(
            "trt", properties);

    private EMF() {
    }

    public static EntityManagerFactory get_trt() {
        return emf_trt;
    }
}
