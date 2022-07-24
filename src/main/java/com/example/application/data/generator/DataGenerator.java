package com.example.application.data.generator;

import com.example.application.data.entity.Sample250Movies;
import com.example.application.data.entity.Sample250Series;
import com.example.application.data.entity.SamplePopularMovies;
import com.example.application.data.entity.SamplePopularSeries;
import com.example.application.data.service.Sample250MoviesRepository;
import com.example.application.data.service.Sample250SeriesRepository;
import com.example.application.data.service.SamplePopularMoviesRepository;
import com.example.application.data.service.SamplePopularSeriesRepository;
import com.example.application.filmes.CodeResponse;
import com.example.application.filmes.HttpManager;
import com.example.application.filmes.ParsedDataReturner;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    HttpManager httpManager = new HttpManager();
    ParsedDataReturner parsedDataReturner = new ParsedDataReturner();
    CodeResponse codeResponse = new CodeResponse();
    String url = "https://alura-imdb-api.herokuapp.com/movies";
    List<Sample250Movies> contents = parsedDataReturner.ExtractData(httpManager,url);

    @Bean
            public CommandLineRunner loadData(Sample250MoviesRepository sample250MoviesRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (sample250MoviesRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            for (int i = 0; i < contents.size(); i++) {
                Sample250Movies content = contents.get(i);
                int rank = content.getRank();
                String title = content.getName();
                Double rating = content.getImDbRating();
                String image = content.getImage();
                String treatedTitle = title.replace(":", "");
                ExampleDataGenerator<Sample250Movies> sample250MoviesRepositoryGenerator = new ExampleDataGenerator<>(
                        Sample250Movies.class, LocalDateTime.of(2022, 7, 20, 0, 0, 0));
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setImage, content.getImage());
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setRank, content.getRank());
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setName, content.getName());
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setImDbRating, content.setImDbRating());
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setYearMovie, content.setYearMovie());
                sample250MoviesRepositoryGenerator.setData(Sample250Movies::setCrew, content.setCrew());
                sample250MoviesRepository.saveAll(sample250MoviesRepositoryGenerator.create(100, seed));
            }
        };
    }

        /*
    public void generateData(){

        HttpManager httpManager = new HttpManager();
        ParsedDataReturner parsedDataReturner = new ParsedDataReturner();
        CodeResponse codeResponse = new CodeResponse();

        String url = "https://alura-imdb-api.herokuapp.com/movies";
        List<Sample250Movies> contents = parsedDataReturner.ExtractData(httpManager,url);


        InputStream notFoundImage;
        {
            try {
                notFoundImage = new URL("https://images-na.ssl-images-amazon.com/images/G/32/error/50._TTD_.jpg").openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < contents.size(); i++) {
            Sample250Movies content = contents.get(i);
            int rank = content.getRank();
            String title = content.getName();
            Double rating = content.getImDbRating();
            String image = content.getImage();
            String treatedTitle = title.replace(":", "");
            //String fileName = "saida/"+treatedTitle+".png";
            URL urlLinkImagem = null;
            try {
                urlLinkImagem = new URL(content.getImage());

                if (codeResponse.codeResponseValidator(urlLinkImagem) != 200){
                    //stickerMaker.createSticker(notFoundImage,fileName);
                } else {
                    InputStream inputStream = new URL(content.getImage().replaceAll("(@+)(.*).jpg$", "$1.jpg")).openStream();
                    //
                    //stickerMaker.createSticker(inputStream,fileName);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            System.out.println(rank);
            System.out.println(title);
            System.out.println(rating);
            System.out.println(image);
            */

    /*
    @Bean
    public CommandLineRunner loadData(Sample250MoviesRepository sample250MoviesRepository,
            SamplePopularMoviesRepository samplePopularMoviesRepository,
            Sample250SeriesRepository sample250SeriesRepository,
            SamplePopularSeriesRepository samplePopularSeriesRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (sample250MoviesRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample250 Movies entities...");
            ExampleDataGenerator<Sample250Movies> sample250MoviesRepositoryGenerator = new ExampleDataGenerator<>(
                    Sample250Movies.class, LocalDateTime.of(2022, 7, 20, 0, 0, 0));
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setImage, DataType.BOOK_IMAGE_URL);
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setRank, DataType.NUMBER_UP_TO_100);
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setName, DataType.BOOK_TITLE);
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setImDbRating, DataType.NUMBER_UP_TO_10);
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setYearMovie, DataType.NUMBER_UP_TO_1000);
            sample250MoviesRepositoryGenerator.setData(Sample250Movies::setCrew, DataType.FULL_NAME);
            sample250MoviesRepository.saveAll(sample250MoviesRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Sample Popular Movies entities...");
            ExampleDataGenerator<SamplePopularMovies> samplePopularMoviesRepositoryGenerator = new ExampleDataGenerator<>(
                    SamplePopularMovies.class, LocalDateTime.of(2022, 7, 20, 0, 0, 0));
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setImage, DataType.BOOK_IMAGE_URL);
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setRank, DataType.NUMBER_UP_TO_100);
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setName, DataType.BOOK_TITLE);
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setImDbRating,
                    DataType.NUMBER_UP_TO_100);
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setYearMovie,
                    DataType.NUMBER_UP_TO_1000);
            samplePopularMoviesRepositoryGenerator.setData(SamplePopularMovies::setCrew, DataType.FULL_NAME);
            samplePopularMoviesRepository.saveAll(samplePopularMoviesRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Sample250 Series entities...");
            ExampleDataGenerator<Sample250Series> sample250SeriesRepositoryGenerator = new ExampleDataGenerator<>(
                    Sample250Series.class, LocalDateTime.of(2022, 7, 20, 0, 0, 0));
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setImage, DataType.BOOK_IMAGE_URL);
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setRank, DataType.NUMBER_UP_TO_1000);
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setName, DataType.BOOK_TITLE);
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setImDbRating, DataType.NUMBER_UP_TO_10);
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setYearMovie, DataType.NUMBER_UP_TO_1000);
            sample250SeriesRepositoryGenerator.setData(Sample250Series::setCrew, DataType.FULL_NAME);
            sample250SeriesRepository.saveAll(sample250SeriesRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Sample Popular Series entities...");
            ExampleDataGenerator<SamplePopularSeries> samplePopularSeriesRepositoryGenerator = new ExampleDataGenerator<>(
                    SamplePopularSeries.class, LocalDateTime.of(2022, 7, 20, 0, 0, 0));
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setImage, DataType.BOOK_IMAGE_URL);
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setRank, DataType.NUMBER_UP_TO_1000);
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setName, DataType.BOOK_TITLE);
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setImDbRating,
                    DataType.NUMBER_UP_TO_10);
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setYearMovie,
                    DataType.NUMBER_UP_TO_1000);
            samplePopularSeriesRepositoryGenerator.setData(SamplePopularSeries::setCrew, DataType.FULL_NAME);
            samplePopularSeriesRepository.saveAll(samplePopularSeriesRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");

        };
    }
*/
}