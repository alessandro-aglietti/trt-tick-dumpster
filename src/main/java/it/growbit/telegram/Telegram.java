package it.growbit.telegram;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import it.growbit.matlab.model.Next24HourAvg;
import it.growbit.telegram.model.SendMessage;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by name on 26/06/17.
 */
public class Telegram {

    private static final Logger log = Logger.getLogger(Telegram.class.getName());

    static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    static final JsonFactory JSON_FACTORY = new GsonFactory();

    public static final Properties props;
    private static final String PROPERTY_GEKKO_SECRET = "gekko_secret";
    public static final String PROPERTY_SCALP_CAVERNA = "scalp_caverna";

    static {
        InputStream is = Telegram.class.getResourceAsStream("/telegram.properties");
        props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void sendMessage(SendMessage message) {
        URIBuilder uri_template = new URIBuilder()
                .setScheme("https")
                .setHost("api.telegram.org")
                .setPath("bot" + props.getProperty(PROPERTY_GEKKO_SECRET) + "/sendMessage");

        URI uri;
        try {
            uri = uri_template.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        GenericUrl generic_url = new GenericUrl(uri);

        HttpContent request_payload = new UrlEncodedContent(message);

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        HttpRequest request;
        try {
            request = requestFactory.buildPostRequest(generic_url, request_payload);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            request.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
