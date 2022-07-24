package com.example.application.filmes;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

public class CodeResponse {
    public int codeResponseValidator(URL urlLinkImagem){

        HttpsURLConnection huc = null;
        try {
            huc = (HttpsURLConnection) urlLinkImagem.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            return huc.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
