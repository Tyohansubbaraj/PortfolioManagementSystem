package com.marketminds.portfoliomanagementsystem;

import com.marketminds.portfoliomanagementsystem.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void assetsEndpoint_ReturnsSeededAssets() {
        ResponseEntity<List> res = restTemplate.exchange("/api/assets", HttpMethod.GET, null, new ParameterizedTypeReference<List>() {});
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void transactionsEndpoint_ReturnsSeededTransactionsAndPaging() {
        ResponseEntity<List> res = restTemplate.exchange("/api/transactions", HttpMethod.GET, null, new ParameterizedTypeReference<List>() {});
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body.size()).isGreaterThanOrEqualTo(1);

        // Test paging
        ResponseEntity<Map> paged = restTemplate.exchange("/api/transactions?page=0&size=1", HttpMethod.GET, null, new ParameterizedTypeReference<Map>() {});
        assertThat(paged.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map pbody = paged.getBody();
        assertThat(pbody).isNotNull();
        assertThat(pbody.get("content")).isInstanceOf(List.class);
        assertThat((Integer)pbody.get("size")).isGreaterThanOrEqualTo(1);
    }

    @Test
    void historyEndpoint_ReturnsValues() {
        ResponseEntity<List> res = restTemplate.exchange("/api/portfolio/history?days=7", HttpMethod.GET, null, new ParameterizedTypeReference<List>() {});
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body.size()).isEqualTo(7);
    }
}
