package com.example.application.filmes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class HttpManager {

    public String getData(String url) {
        //String url = "https://alura-imdb-api.herokuapp.com/movies";
        URI uri = URI.create(url);
        HttpClient client = java.net.http.HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String body = response.body();
        return body;
    }
}
