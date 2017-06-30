package it.growbit.matlab;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.*;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import it.growbit.matlab.model.Last24HoursAvg;
import it.growbit.matlab.model.Next24HourAvg;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by name on 26/06/17.
 */
public class Matlab {

    private static final Logger log = Logger.getLogger(Matlab.class.getName());

    static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    static final JsonFactory JSON_FACTORY = new GsonFactory();

    public static final Next24HourAvg criptoOracleValori(Last24HoursAvg matlab_model) throws URISyntaxException, IOException {

        URIBuilder uri_template = new URIBuilder()
                .setScheme("https")
                .setHost("matlab-dot-growbit-0.appspot.com")
                .setPath("/matlab/criptoOracleValori");

        URI uri = uri_template.build();
        GenericUrl generic_url = new GenericUrl(uri);

        HttpContent request_payload = new JsonHttpContent(JSON_FACTORY, matlab_model);

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

        HttpRequest request = requestFactory.buildPostRequest(generic_url, request_payload);


        Next24HourAvg forecast = request.execute().parseAs(Next24HourAvg.class);

        return forecast;
    }

    public static final Next24HourAvg superCriptoOracleTrend(Last24HoursAvg matlab_model) throws URISyntaxException, IOException {

        URIBuilder uri_template = new URIBuilder()
                .setScheme("https")
                .setHost("matlab-dot-growbit-0.appspot.com")
                .setPath("/matlab/superCriptoOracleTrend");

        URI uri = uri_template.build();
        GenericUrl generic_url = new GenericUrl(uri);

        HttpContent request_payload = new JsonHttpContent(JSON_FACTORY, matlab_model);

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

        HttpRequest request = requestFactory.buildPostRequest(generic_url, request_payload);


        Next24HourAvg forecast = request.execute().parseAs(Next24HourAvg.class);

        return forecast;
    }
}
