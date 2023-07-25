package de.adesso.trmclient.cli.api.enpointwrapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.trmclient.cli.api.dto.TimeSheetDto;
import de.adesso.trmclient.cli.api.enpointwrapper.exception.CommandFailedException;
import de.adesso.trmclient.cli.api.enpointwrapper.model.Tuple;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public abstract class BaseEndpointWrapper<T> {

    private WebClient.RequestBodyUriSpec requestInit(HttpMethod method, String url) {
        WebClient client = WebClient.create(url);
        return client.method(method);
    }

    public final Optional<T> request(HttpMethod method, String url) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        return requestSend(r);
    }

    public final Optional<T> request(HttpMethod method, String url, Tuple... attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        Map<String, Object> map = Arrays.stream(attributes).collect(Collectors.toMap(Tuple::s1, Tuple::o2));
        r.bodyValue(map);
        return requestSend(r);
    }

    public final Optional<T> request(HttpMethod method, String url, Map<String, Object> attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        attributes.forEach(r::attribute);
        return requestSend(r);
    }

    public final Optional<T> request(HttpMethod method, String url, Consumer<Map<String, Object>> attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        r.attributes(attributes);
        return requestSend(r);
    }

    private Optional<T> requestSend(WebClient.RequestBodyUriSpec request) {
        Mono<T> mono = request.exchangeToMono(
                r -> {
                    if(r.statusCode().isSameCodeAs(HttpStatusCode.valueOf(204))) {
                        return Mono.empty();
                    } else if(r.statusCode().is2xxSuccessful()) {
                        return r.bodyToMono(getGenericClass());
                    } else {
                        return Mono.error(new CommandFailedException());
                    }
                }
        );
        return mono.blockOptional();
    }

    public final List<T> requestList(HttpMethod method, String url) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        return requestListSend(r);
    }

    public final List<T> requestList(HttpMethod method, String url, Tuple... attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        Map<String, Object> map = Arrays.stream(attributes).collect(Collectors.toMap(Tuple::s1, Tuple::o2));
        r.bodyValue(map);
        return requestListSend(r);
    }

    public final List<T> requestList(HttpMethod method, String url, Map<String, Object> attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        attributes.forEach(r::attribute);
        return requestListSend(r);
    }

    public final List<T> requestList(HttpMethod method, String url, Consumer<Map<String, Object>> attributes) {
        WebClient.RequestBodyUriSpec r = requestInit(method, url);
        r.attributes(attributes);
        return requestListSend(r);
    }

    private List<T> requestListSend(WebClient.RequestBodyUriSpec request) {
        ObjectMapper mapper = new ObjectMapper();
        Mono<Object[]> mono = request.retrieve().bodyToMono(Object[].class);
        Object[] list = mono.block();
        if(list != null) {
            return Arrays.stream(list).map(o -> mapper.convertValue(o, getGenericClass())).toList();
        } else {
            return new ArrayList<>();
        }
    }

    protected abstract Class<T> getGenericClass();

}
