package com.example.application.views.best250movies;

import com.example.application.data.entity.Sample250Movies;
import com.example.application.data.generator.DataGenerator;
import com.example.application.data.service.Sample250MoviesService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;

@PageTitle("Best 250 Movies")
@Route(value = "250movies/:sample250MoviesID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class Best250MoviesView extends Div implements BeforeEnterObserver {

    private final String SAMPLE250MOVIES_ID = "sample250MoviesID";
    private final String SAMPLE250MOVIES_EDIT_ROUTE_TEMPLATE = "250movies/%s/edit";

    private Grid<Sample250Movies> grid = new Grid<>(Sample250Movies.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField rank;
    private TextField name;
    private TextField imDbRating;
    private TextField yearMovie;
    private TextField crew;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Sample250Movies> binder;

    private Sample250Movies sample250Movies;

    private final Sample250MoviesService sample250MoviesService;

    DataGenerator dataGenerator = new DataGenerator();

    @Autowired
    public Best250MoviesView(Sample250MoviesService sample250MoviesService) {
        this.sample250MoviesService = sample250MoviesService;
        
        addClassNames("best250-movies-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<Sample250Movies> imageRenderer = LitRenderer
                .<Sample250Movies>of("<img style='height: 64px' src=${item.image} />")
                .withProperty("image", Sample250Movies::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn(Sample250Movies::getRank).setAutoWidth(true).setHeader("Posição");
        grid.addColumn(Sample250Movies::getName).setAutoWidth(true).setHeader("Nome do Filme");
        grid.addColumn(Sample250Movies::getImDbRating).setAutoWidth(true).setHeader("Nota IMDB");
        grid.addColumn(Sample250Movies::getYearMovie).setAutoWidth(true).setHeader("Ano de Lançamento");
        grid.addColumn(Sample250Movies::getCrew).setAutoWidth(true).setHeader("Elenco");
        grid.setItems(sample250MoviesService.listByURL());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLE250MOVIES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(Best250MoviesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Sample250Movies.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(rank).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("rank");
        binder.forField(imDbRating).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("imDbRating");
        binder.forField(yearMovie).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("yearMovie");

        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sample250Movies == null) {
                    //this.sample250Movies = new Sample250Movies();
                }
                binder.writeBean(this.sample250Movies);
                this.sample250Movies.setImage(imagePreview.getSrc());

                sample250MoviesService.update(this.sample250Movies);
                clearForm();
                refreshGrid();
                Notification.show("Sample250Movies details stored.");
                UI.getCurrent().navigate(Best250MoviesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the sample250Movies details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> sample250MoviesId = event.getRouteParameters().get(SAMPLE250MOVIES_ID).map(UUID::fromString);
        if (sample250MoviesId.isPresent()) {
            Optional<Sample250Movies> sample250MoviesFromBackend = sample250MoviesService.get(sample250MoviesId.get());
            if (sample250MoviesFromBackend.isPresent()) {
                populateForm(sample250MoviesFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sample250Movies was not found, ID = %s", sample250MoviesId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(Best250MoviesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        Label imageLabel = new Label("Image");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        image = new Upload();
        image.getStyle().set("box-sizing", "border-box");
        image.getElement().appendChild(imagePreview.getElement());
        rank = new TextField("Rank");
        name = new TextField("Name");
        imDbRating = new TextField("Im Db Rating");
        yearMovie = new TextField("Year Movie");
        crew = new TextField("Crew");
        Component[] fields = new Component[]{imageLabel, image, rank, name, imDbRating, yearMovie, crew};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Sample250Movies value) {
        this.sample250Movies = value;
        binder.readBean(this.sample250Movies);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getImage());
        }

    }
}
