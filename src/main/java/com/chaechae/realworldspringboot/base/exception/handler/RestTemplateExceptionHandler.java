package com.chaechae.realworldspringboot.base.exception.handler;

import com.chaechae.realworldspringboot.base.exception.RestTemplateException;
import com.chaechae.realworldspringboot.base.exception.RestTemplateExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class RestTemplateExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }

    //
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(responseBody, Map.class);

        throw new RestTemplateException(RestTemplateExceptionType.INVALID_REST_API_CALL, map);
    }
}
