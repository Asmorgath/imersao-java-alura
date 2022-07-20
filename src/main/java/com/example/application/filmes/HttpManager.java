package com.example.application.filmes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class HttpManager {

    String url = "https://alura-imdb-api.herokuapp.com/movies";
    URI uri = URI.create(url);
    HttpClient client = java.net.http.HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String body = response.body();

    //extrair so os dados que interessam(titulo,poster,classificaçao)
    JsonParser parser = new JsonParser();
    List<Map<String, String>> listaDeFilmes = parser.parse(body);

    public HttpManager() throws IOException, InterruptedException {
    }
    //exibir e manipular os dados


    for
}
