package cz.cvut.fel.ear.stepavi2_havriboh.main.rest.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class RestUtils {
    public static HttpHeaders createLocationHeaderFromCurrentUri(String path, Object... uriVariableValues) {
        assert path != null;

        final URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path(path).buildAndExpand(
                uriVariableValues).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, location.toASCIIString());
        return headers;
    }
}
