package com.optimagrowth.licensingservice.interceptor;

import com.optimagrowth.licensingservice.models.UserContext;
import com.optimagrowth.licensingservice.models.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add(UserContext.CORRELATION_ID,
                UserContextHolder.getContext().
                        getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN,
                UserContextHolder.getContext().
                        getAuthToken());
        headers.add(UserContext.ORGANIZATION_ID,
                UserContextHolder.getContext().
                        getAuthToken());
        headers.add(UserContext.USER_ID,
                UserContextHolder.getContext().
                        getAuthToken());
        return execution.execute(request, body);
    }
}
