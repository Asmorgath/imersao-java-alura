package com.example.application.views.popularmovies;

import com.example.application.data.entity.SamplePopularMovies;
import com.example.application.data.service.SamplePopularMoviesService;
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

@PageTitle("Popular Movies")
@Route(value = "mostpopularmovies/:samplePopularMoviesID?/:action?(edit)", layout = MainLayout.class)
public class PopularMoviesView extends Div implements BeforeEnterObserver {

    private final String SAMPLEPOPULARMOVIES_ID = "samplePopularMoviesID";
    private final String SAMPLEPOPULARMOVIES_EDIT_ROUTE_TEMPLATE = "mostpopularmovies/%s/edit";

    private Grid<SamplePopularMovies> grid = new Grid<>(SamplePopularMovies.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField ranking;
    private TextField name;
    private TextField imDbRating;
    private TextField yearMovie;
    private TextField crew;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<SamplePopularMovies> binder;

    private SamplePopularMovies samplePopularMovies;

    private final SamplePopularMoviesService samplePopularMoviesService;

    @Autowired
    public PopularMoviesView(SamplePopularMoviesService samplePopularMoviesService) {
        this.samplePopularMoviesService = samplePopularMoviesService;
        addClassNames("popular-movies-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<SamplePopularMovies> imageRenderer = LitRenderer
                .<SamplePopularMovies>of("<img style='height: 64px' src=${item.image} />")
                .withProperty("image", SamplePopularMovies::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn("ranking").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("imDbRating").setAutoWidth(true);
        grid.addColumn("yearMovie").setAutoWidth(true);
        grid.addColumn("crew").setAutoWidth(true);
        grid.setItems(query -> samplePopularMoviesService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent()
                        .navigate(String.format(SAMPLEPOPULARMOVIES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PopularMoviesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SamplePopularMovies.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(ranking).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("ranking");
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
                if (this.samplePopularMovies == null) {
                    this.samplePopularMovies = new SamplePopularMovies();
                }
                binder.writeBean(this.samplePopularMovies);
                this.samplePopularMovies.setImage(imagePreview.getSrc());

                samplePopularMoviesService.update(this.samplePopularMovies);
                clearForm();
                refreshGrid();
                Notification.show("SamplePopularMovies details stored.");
                UI.getCurrent().navigate(PopularMoviesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the samplePopularMovies details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> samplePopularMoviesId = event.getRouteParameters().get(SAMPLEPOPULARMOVIES_ID)
                .map(UUID::fromString);
        if (samplePopularMoviesId.isPresent()) {
            Optional<SamplePopularMovies> samplePopularMoviesFromBackend = samplePopularMoviesService
                    .get(samplePopularMoviesId.get());
            if (samplePopularMoviesFromBackend.isPresent()) {
                populateForm(samplePopularMoviesFromBackend.get());
            } else {
                Notification.show(String.format("The requested samplePopularMovies was not found, ID = %s",
                        samplePopularMoviesId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PopularMoviesView.class);
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
        ranking = new TextField("Ranking");
        name = new TextField("Name");
        imDbRating = new TextField("Im Db Rating");
        yearMovie = new TextField("Year Movie");
        crew = new TextField("Crew");
        Component[] fields = new Component[]{imageLabel, image, ranking, name, imDbRating, yearMovie, crew};

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

    private void populateForm(SamplePopularMovies value) {
        this.samplePopularMovies = value;
        binder.readBean(this.samplePopularMovies);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getImage());
        }

    }
}
