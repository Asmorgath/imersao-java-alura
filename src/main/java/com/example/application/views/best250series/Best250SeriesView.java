package com.example.application.views.best250series;

import com.example.application.data.entity.Sample250Series;
import com.example.application.data.service.Sample250SeriesService;
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

@PageTitle("Best 250 Series")
@Route(value = "250series/:sample250SeriesID?/:action?(edit)", layout = MainLayout.class)
public class Best250SeriesView extends Div implements BeforeEnterObserver {

    private final String SAMPLE250SERIES_ID = "sample250SeriesID";
    private final String SAMPLE250SERIES_EDIT_ROUTE_TEMPLATE = "250series/%s/edit";

    private Grid<Sample250Series> grid = new Grid<>(Sample250Series.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField ranking;
    private TextField name;
    private TextField imDbRating;
    private TextField yearMovie;
    private TextField crew;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Sample250Series> binder;

    private Sample250Series sample250Series;

    private final Sample250SeriesService sample250SeriesService;

    @Autowired
    public Best250SeriesView(Sample250SeriesService sample250SeriesService) {
        this.sample250SeriesService = sample250SeriesService;
        addClassNames("best250-series-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<Sample250Series> imageRenderer = LitRenderer
                .<Sample250Series>of("<img style='height: 64px' src=${item.image} />")
                .withProperty("image", Sample250Series::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn("ranking").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("imDbRating").setAutoWidth(true);
        grid.addColumn("yearMovie").setAutoWidth(true);
        grid.addColumn("crew").setAutoWidth(true);
        grid.setItems(query -> sample250SeriesService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLE250SERIES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(Best250SeriesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Sample250Series.class);

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
                if (this.sample250Series == null) {
                    this.sample250Series = new Sample250Series();
                }
                binder.writeBean(this.sample250Series);
                this.sample250Series.setImage(imagePreview.getSrc());

                sample250SeriesService.update(this.sample250Series);
                clearForm();
                refreshGrid();
                Notification.show("Sample250Series details stored.");
                UI.getCurrent().navigate(Best250SeriesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the sample250Series details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> sample250SeriesId = event.getRouteParameters().get(SAMPLE250SERIES_ID).map(UUID::fromString);
        if (sample250SeriesId.isPresent()) {
            Optional<Sample250Series> sample250SeriesFromBackend = sample250SeriesService.get(sample250SeriesId.get());
            if (sample250SeriesFromBackend.isPresent()) {
                populateForm(sample250SeriesFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sample250Series was not found, ID = %s", sample250SeriesId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(Best250SeriesView.class);
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

    private void populateForm(Sample250Series value) {
        this.sample250Series = value;
        binder.readBean(this.sample250Series);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getImage());
        }

    }
}
