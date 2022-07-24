package com.example.application.filmes;

import com.example.application.data.entity.Sample250Movies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParsedDataReturner {
    public List<Sample250Movies> ExtractData(HttpManager httpManager, String url) {

        var parser = new JsonParser();
        List<Map<String, String>> dataListItems = parser.parse(httpManager.HttpManager(url));
        List<Sample250Movies> detailedBodyContent = new ArrayList<>();

        for (Map<String, String> dataListItem : dataListItems) {
            String title = dataListItem.get("title");
            String urlImagem = dataListItem.get("image");
            int releaseYear = Integer.parseInt(dataListItem.get("year"));
            String fullTitle = dataListItem.get("fullTitle");
            String crew = dataListItem.get("crew");
            Double scoreIMDB = Double.valueOf(dataListItem.get("imDbRating"));
            int rank = Integer.parseInt(dataListItem.get("rank"));

            Sample250Movies conteudo;
            conteudo = new Sample250Movies( urlImagem,  rank,  title,  scoreIMDB,  releaseYear,  crew);
            detailedBodyContent.add(conteudo);
        }

        return detailedBodyContent;
    }
}
