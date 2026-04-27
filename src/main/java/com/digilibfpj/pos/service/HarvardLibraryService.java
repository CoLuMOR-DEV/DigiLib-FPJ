package com.digilibfpj.pos.service;

import com.digilibfpj.pos.entity.Book;
import com.digilibfpj.pos.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HarvardLibraryService {

    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;

    @Value("${harvard.api.base-url}")
    private String harvardApiBaseUrl;

    @Value("${harvard.api.key}")
    private String harvardApiKey;

    public HarvardLibraryService(RestTemplate restTemplate, BookRepository bookRepository) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
    }

    public Map<String, Object> searchBooksWithFallback(String query) {
        List<Book> localBooks = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        Map<String, Object> response = new HashMap<>();
        response.put("localBooks", localBooks);

        String url = UriComponentsBuilder.fromHttpUrl(harvardApiBaseUrl)
                .queryParam("q", query)
                .queryParam("apikey", harvardApiKey)
                .toUriString();

        ResponseEntity<Map<String, Object>> harvardResponseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> harvardApiResponse = harvardResponseEntity.getBody();
        response.put("harvardRecords", harvardApiResponse);
        return response;
    }
}
