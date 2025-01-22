package com.reliaquest.api.httpclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class RestTemplateHttpClientTest {

    private RestTemplate restTemplate;
    private RestTemplateHttpClient restTemplateHttpClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        restTemplateHttpClient = new RestTemplateHttpClient(restTemplate);
    }

    @Test
    void testGet() {
        String url = "http://example.com/api/resource";
        String expectedResponse = "Success";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(expectedResponse);

        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);

        ResponseEntity<String> actualResponse = restTemplateHttpClient.get(url, String.class);

        verify(restTemplate, times(1)).getForEntity(url, String.class);
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    @Test
    void testPost() {
        String url = "http://example.com/api/resource";
        String request = "Request Data";
        String expectedResponse = "Created";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(expectedResponse);

        when(restTemplate.postForEntity(url, request, String.class)).thenReturn(responseEntity);

        ResponseEntity<String> actualResponse = restTemplateHttpClient.post(url, request, String.class);

        verify(restTemplate, times(1)).postForEntity(url, request, String.class);
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    @Test
    void testDelete() {
        String url = "http://example.com/api/resource";
        String request = "Request Data";
        String expectedResponse = "Deleted";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(expectedResponse);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        ResponseEntity<String> actualResponse = restTemplateHttpClient.delete(url, request, String.class);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.DELETE), captor.capture(), eq(String.class));

        HttpEntity capturedEntity = captor.getValue();
        assertEquals(request, capturedEntity.getBody());
        assertEquals(expectedResponse, actualResponse.getBody());
    }
}
