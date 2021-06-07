package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.ConstellationDrawLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;

import static ch.epfl.rigel.gui.SkyCanvasManager.INTERVAL_ALT_DEG;

/**
 * Main pour démarrer l'application.
 *
 * @author Jean-Baptiste Moreau (296189)
 * @author Ali Raed Ben Mustapha (300392)
 */
public class Main extends Application {

    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
    private static final String ASTERISM_CATALOGUE_NAME = "/asterisms.txt";
    private static final String CONSTELLATION_NAME = "/constellation_drawing.txt";
    private static final double EPFL_LONGITUDE = 6.57;
    private static final double EPFL_LATITUDE = 46.52;
    private static final HorizontalCoordinates INITIAL_OBSERV_POS
            = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final int INITIAL_FIELD_OF_VIEW = 100;
    protected static final int CANVAS_WIDTH = 800;
    protected static final int CANVAS_HEIGHT = 700;
    private static final String PROGRAM_NAME = "Rigel";
    private static final String FONT_AWESOME_NAME = "/Font Awesome 5 Free-Solid-900.otf";
    private static final String RESET = "\uf0e2";
    private static final String PLAY = "\uf04b";
    private static final String STOP = "\uf04c";
    private static final String DISPLAY_MENU = "Affichage";
    private static final String SETTINGS_MENU = "Paramètres";
    private static final String ACCELERATOR_MENU = "Accélérateur";
    private static final String TRACKING_MENU = "Traqueur";
    private static final String MAP_MENU = "Carte";
    private static final Insets BORDER_WINDOWS = new Insets(10, 10, 10, 10);
    private static final int INIT_VALUE_SLIDER = 100;

    private DateTimeBean dateTimeBean;
    private TimeAnimator timeAnimator;
    private ObserverLocationBean observerLocationBean;
    private SkyCanvasManager canvasManager;
    private ViewingParametersBean viewingParametersBean;
    private Canvas sky;
    private MapManager mapManager;
    private Stage windowMap;
    private Stage windowSettings;
    private Stage windowDisplay;
    private Stage windowAccelerator;
    private Stage windowTracking;
    private BooleanProperty acceleratorsDisableProperty;

    public static void main(String[] args) {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try (InputStream hygStream = resourceStream(HYG_CATALOGUE_NAME);
             InputStream asterismStream = resourceStream(ASTERISM_CATALOGUE_NAME);
             InputStream constellationStream = resourceStream(CONSTELLATION_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .loadFrom(constellationStream, ConstellationDrawLoader.INSTANCE)
                    .build();

            dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(ZonedDateTime.now());
            timeAnimator = new TimeAnimator(dateTimeBean);

            observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(
                    GeographicCoordinates.ofDeg(EPFL_LONGITUDE, EPFL_LATITUDE));

            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(INITIAL_OBSERV_POS);
            viewingParametersBean.setFieldOfViewDeg(INITIAL_FIELD_OF_VIEW);

            canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    timeAnimator,
                    observerLocationBean,
                    viewingParametersBean
            );

            sky = canvasManager.canvas();
            Pane paneSky = new Pane(sky);

            sky.widthProperty().bind(paneSky.widthProperty());
            sky.heightProperty().bind(paneSky.heightProperty());

            BorderPane root = new BorderPane(
                    paneSky,
                    controlBar(),
                    null,
                    infoBar(),
                    null
            );

            mapManager = new MapManager(observerLocationBean);
            windowMap = windowMap(stage);
            windowSettings = windowSettings(stage);
            windowDisplay = windowDisplay(stage);
            windowAccelerator = windowAccelerator(stage);
            windowTracking = windowTracking(stage, catalogue.getNameOfCelestialObjects());
            closeWindowsWhenClickOnSky();

            stage.setTitle(PROGRAM_NAME);
            stage.setMinWidth(CANVAS_WIDTH);
            stage.setMinHeight(CANVAS_HEIGHT);

            stage.setScene(new Scene(root));
            stage.show();

            sky.requestFocus();
        }

    }

    /**
     * Ajoute un listener au canvas sky. Permet de fermer les fenêtres ouvertes
     * depuis le Menu en cliquant simplement sur le canvas. Il n'est plus utile
     * de cliquer sur la croix rouge de la fenêtre.
     */
    private void closeWindowsWhenClickOnSky() {
        sky.setOnMousePressed(e -> {
            if(e.isPrimaryButtonDown()) {
                closeWindowIfIsShowing(windowSettings);
                closeWindowIfIsShowing(windowDisplay);
                closeWindowIfIsShowing(windowAccelerator);
                closeWindowIfIsShowing(windowTracking);
                closeWindowIfIsShowing(windowMap);
            }
        });
    }

    /**
     * Ferme la fenêtre fourni si elle est ouverte.
     *
     * @param window fenêtre à fermer si elle est ouverte
     */
    private void closeWindowIfIsShowing(Stage window) {
        if(window.isShowing())
            window.hide();
    }

    /**
     * Retourne la fenêtre contenant la carte du monde pour sélectionner
     * le point d'observation.
     *
     * @param mainStage fenêtre principale.
     * @return un Stage contenant la carte du monde.
     */
    private Stage windowMap(Stage mainStage) {
        Region region = mapManager.region();
        Scene scene = new Scene(region, 665, 665);

        Stage window = new Stage();
        window.setTitle(MAP_MENU);
        window.initStyle(StageStyle.UTILITY);

        window.initOwner(mainStage);
        window.setScene(scene);

        window.setOnCloseRequest(e -> {
            window.hide();
            sky.requestFocus();
        });

        return window;
    }

    /**
     * Retourne une fenêtre contentant de quoi modifier les paramètres.
     *
     * @param mainStage fenêtre principale.
     * @return un Stage contenant de quoi régler les paramètres.
     */
    private Stage windowSettings(Stage mainStage) {
        Label zoomLabel = new Label("   Sensibilité de zoom");
        Slider zoomSensi = newSliderForSensibility(zoomLabel);
        canvasManager.scrollFactorProperty().bind(zoomSensi.valueProperty());

        Label horLabel = new Label("   Sensibilité horizontale");
        Slider horSensi = newSliderForSensibility(horLabel);
        canvasManager.horFactorProperty().bind(horSensi.valueProperty());

        Label verLabel = new Label("   Sensibilité verticale");
        Slider verSensi = newSliderForSensibility(verLabel);
        canvasManager.verFactorProperty().bind(verSensi.valueProperty());

        VBox vbox = new VBox(zoomLabel, zoomSensi, horLabel, horSensi, verLabel, verSensi);
        setSeparator(vbox);
        BorderPane.setMargin(vbox, BORDER_WINDOWS);

        BorderPane root = new BorderPane(vbox);
        root.setPrefSize(225, 165);

        return windowMenu(mainStage, root, SETTINGS_MENU);
    }

    /**
     * Ajoute des sépérateurs horizontaux à la VBox passé en paramètre.
     *
     * @param vbox box dont on veut ajouter des séparateurs.
     */
    private void setSeparator(VBox vbox) {
        Separator separator0 = new Separator(Orientation.HORIZONTAL);
        vbox.getChildren().add(0, separator0);

        Separator separator3 = new Separator(Orientation.HORIZONTAL);
        vbox.getChildren().add(3, separator3);

        Separator separator6 = new Separator(Orientation.HORIZONTAL);
        vbox.getChildren().add(6, separator6);

        Separator separator9 = new Separator(Orientation.HORIZONTAL);
        vbox.getChildren().add(9, separator9);
    }

    /**
     * Retourne un slider spécifique pour le réglage de la sensibilité.
     *
     * @param label texte au dessus du Slider.
     * @return un Slider spécifique pour le réglage de la sensibilité.
     */
    private Slider newSliderForSensibility(Label label) {
        Slider slider = new Slider(1, 200, INIT_VALUE_SLIDER);
        slider.setMinSize(200, 25);
        slider.setShowTickMarks(true);

        String text = label.getText() + " : %.1f%%";
        label.setText(label.getText() + " : " + INIT_VALUE_SLIDER + "%");

        slider.valueProperty().addListener(
                (p, o, n) -> label.setText(String.format(Locale.ROOT, text, n))
        );
        return slider;
    }

    /**
     * Retourne une fenêtre contentant de quoi régler ce qu'il faut
     * dessiner sur le canvas.
     *
     * @param mainStage fenêtre principale.
     * @return un Stage contenant de quoi régler ce qu'il faut dessiner
     *         sur le canvas.
     */
    private Stage windowDisplay(Stage mainStage) {
        Label intro = new Label("Dessiner :");

        CheckBox enableStars = checkBoxLinkedProperty("les étoiles",
                true, canvasManager.enDrawStarsProperty());
        CheckBox enablePlanets = checkBoxLinkedProperty("les planètes",
                true, canvasManager.enDrawPlanetsProperty());
        CheckBox enableMoon = checkBoxLinkedProperty("la Lune",
                true, canvasManager.enDrawMoonProperty());
        CheckBox enableSun = checkBoxLinkedProperty("le Soleil",
                true, canvasManager.enDrawSunProperty());
        CheckBox enableAsterisms = checkBoxLinkedProperty("les constellations",
                true, canvasManager.enDrawAsterismsProperty());
        CheckBox enableConstellations = checkBoxLinkedProperty("les constellations imagées",
                true, canvasManager.enDrawConstellationsProperty());
        CheckBox enableHorizon = checkBoxLinkedProperty("l'horizon",
                true, canvasManager.enDrawHorizonProperty());
        CheckBox enableDayNightCycle = checkBoxLinkedProperty("le cycle jour/nuit",
                false, canvasManager.dayNightCycleProperty());

        VBox vbox = new VBox(
                intro,
                enableStars,
                enablePlanets,
                enableMoon,
                enableSun,
                enableAsterisms,
                enableConstellations,
                enableHorizon,
                enableDayNightCycle
        );
        vbox.setSpacing(2);
        BorderPane.setMargin(vbox, BORDER_WINDOWS);

        BorderPane root = new BorderPane(vbox);
        root.setPrefSize(183, 200);

        return windowMenu(mainStage, root, DISPLAY_MENU);
    }

    /**
     * Retourne une CheckBox personnalisé liée à une propriété booléenne.
     *
     * @param text texte de la CheckBox.
     * @param isSelected état initial de la CheckBox (coché équivaut à true).
     * @param booleanProperty propriété booléenne à laquelle liée celle de
     *                        la CheckBox.
     * @return une CheckBox personnalisé liée à un propriété booléenne.
     */
    private CheckBox checkBoxLinkedProperty(String text, boolean isSelected, BooleanProperty booleanProperty) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(isSelected);
        booleanProperty.bind(checkBox.selectedProperty());

        return checkBox;
    }

    /**
     * Retourne une fenêtre contenant de quoi régler un accélérateur
     * de temps personnalisé.
     *
     * @param mainStage fenêtre principale.
     * @return un Stage contenant de quoi régler un accélérateur de
     *         temps personnalisé.
     */
    private Stage windowAccelerator(Stage mainStage) {
        CheckBox activePersoAcc = new CheckBox("Accélérateur personnalisé");
        activePersoAcc.setSelected(false);
        VBox.setMargin(activePersoAcc, new Insets(0, 0, 0, 5));

        BooleanProperty selectedProperty = activePersoAcc.selectedProperty();
        acceleratorsDisableProperty.bind(selectedProperty);
        timeAnimator.personalAccelerationProperty().bind(selectedProperty);

        Slider slider = new Slider(1, 3600, 300);
        slider.setMinSize(500, 25);
        slider.setShowTickMarks(true);

        timeAnimator.accelerationValueProperty().bind(slider.valueProperty());
        activePersoAcc.disableProperty().bind(timeAnimator.getRunning());
        slider.disableProperty().bind(timeAnimator.getRunning());

        Label sliderValue = new Label("Valeur : x300");
        VBox.setMargin(sliderValue, new Insets(0, 0, 0, 210));

        String text = "Valeur : x%.0f";
        slider.valueProperty().addListener(
                (p, o, n) -> sliderValue.setText(String.format(Locale.ROOT, text, n))
        );

        VBox vbox = new VBox(activePersoAcc, slider, sliderValue);
        vbox.setSpacing(3);
        BorderPane.setMargin(vbox, BORDER_WINDOWS);

        BorderPane root = new BorderPane(vbox);
        root.setPrefSize(520, 80);

        return windowMenu(mainStage, root, ACCELERATOR_MENU + " personnalisé :");
    }

    /**
     * Retourne une fenêtre contenant de quoi suivre un objet céleste.
     *
     * @param mainStage fenêtre principale.
     * @return unStage contenant de quoi suivre un objet céleste.
     */
    private Stage windowTracking(Stage mainStage, List<String> celestialObjectsName) {
        CheckBox activeTracking = new CheckBox("Suivre un objet céleste :");
        activeTracking.setSelected(false);
        canvasManager.isTrackingCelestialProperty()
                     .bind(activeTracking.selectedProperty());

        ComboBox<String> celestialComboBox = new ComboBox<>();
        celestialComboBox.setItems(FXCollections.observableList(celestialObjectsName));

        celestialComboBox.valueProperty().addListener((o, oV, nV) -> {
            if(activeTracking.isSelected()) {
                HorizontalCoordinates coords = canvasManager.getCoordsCelestialObject(nV);
                viewingParametersBean.setCenter(coords);
            }
        });
        celestialComboBox.setValue(celestialObjectsName.get(0));

        activeTracking.selectedProperty().addListener(e -> {
            setCenterIfIsTracking(activeTracking, celestialComboBox);
            if(!activeTracking.isSelected() &&
                    !INTERVAL_ALT_DEG.contains(viewingParametersBean.getCenter().altDeg())) {
                viewingParametersBean.setCenter(INITIAL_OBSERV_POS);
            }

        });

        dateTimeBeanListeners(activeTracking, celestialComboBox);

        HBox hbox = new HBox(activeTracking, celestialComboBox);
        hbox.setSpacing(5);
        BorderPane.setMargin(hbox, BORDER_WINDOWS);

        BorderPane root = new BorderPane(hbox);
        root.setPrefSize(350, 50);

        return windowMenu(mainStage, root, TRACKING_MENU);
    }

    /**
     * Ajoute des Listeners sur les propriétés de dateTimeBean afin de définir
     * le centre de vue sur l'objet céleste qu'on suit à chaque instant.
     *
     * @param activeTracking CheckBox déterminant si on suit un objet céleste.
     * @param celestialComboBox ComboBox contentant la liste des noms des
     *                          objets célestes.
     */
    private void dateTimeBeanListeners(CheckBox activeTracking, ComboBox<String> celestialComboBox) {
        dateTimeBean.dateProperty().addListener(e -> setCenterIfIsTracking(activeTracking, celestialComboBox));
        dateTimeBean.timeProperty().addListener(e -> setCenterIfIsTracking(activeTracking, celestialComboBox));
        dateTimeBean.zoneProperty().addListener(e -> setCenterIfIsTracking(activeTracking, celestialComboBox));
        observerLocationBean.coordinatesProperty().addListener(e ->
                setCenterIfIsTracking(activeTracking, celestialComboBox));
    }

    /**
     * Définit le centre de vue si on est entrain de suivre un objet céleste.
     *
     * @param activeTracking CheckBox déterminant si on suit un objet céleste.
     * @param celestialComboBox ComboBox contentant la liste des noms des
     *                          objets célestes.
     */
    private void setCenterIfIsTracking(CheckBox activeTracking, ComboBox<String> celestialComboBox) {
        if(activeTracking.isSelected()) {
            HorizontalCoordinates coords
                    = canvasManager.getCoordsCelestialObject(celestialComboBox.getValue());
            viewingParametersBean.setCenter(coords);
        }
    }

    /**
     * Retourne une nouvelle fenêtre de menu accrocher à la fenêtre
     * principale, avec le titre passé en paramètre.
     *
     * @param mainStage fenêtre principale.
     * @param root panneau contenant les boîtes.
     * @param title le titre de la fenêtre.
     * @return une nouvelle fenêtre de menu accrocher à la fenêtre
     *         principale.
     */
    private Stage windowMenu(Stage mainStage, Pane root, String title) {
        Stage window = new Stage();

        window.setTitle(title);
        window.initStyle(StageStyle.UTILITY);

        window.setScene(new Scene(root));
        window.initOwner(mainStage);

        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            window.hide();
            sky.requestFocus();
        });

        return window;
    }

    /**
     * Retourne la barre d'information.
     *
     * @return un BorderPane représentant la barre d'information.
     */
    private BorderPane infoBar() {
        BorderPane infoBar = new BorderPane(
                objectUnderMouseText(),
                null,
                mousePositionText(),
                null,
                fieldOfViewText()
        );
        infoBar.setStyle("-fx-padding: 4; -fx-background-color: white;");

        return infoBar;
    }

    /**
     * Retourne le label correspondant au nom de l'objet le plus proche de
     * la souris.
     *
     * @return le label correspondant au nom de l'objet le plus proche de
     *         la souris.
     */
    private Label objectUnderMouseText() {
        StringExpression stringExpression = Bindings.format(
                Locale.ROOT,
                "%s",
                canvasManager.objectUnderMouseProperty());

        StringBinding stringBinding = Bindings.createStringBinding( () -> {
                    if(stringExpression.isEqualTo("null").get())
                        return "";
                    else return stringExpression.get();
                },
                stringExpression);

        Label objectUnderMouseText = new Label();
        objectUnderMouseText.textProperty().bind(stringBinding);

        return objectUnderMouseText;
    }

    /**
     * Retourne le texte correspondant à la position de la souris dans le
     * ciel en coordonnées horizontales.
     *
     * @return le texte correspondant à la position de la souris dans le
     *         ciel en coordonnées horizontales.
     */
    private Text mousePositionText() {
        Text mousePositionText = new Text("Azimut : <az>°, hauteur : <alt>°");

        mousePositionText.textProperty().bind(Bindings.format(
                Locale.ROOT,
                "Azimut : %.2f°, hauteur : %.2f°",
                canvasManager.mouseAzDegProperty(),
                canvasManager.mouseAltDegProperty() ));

        return mousePositionText;
    }

    /**
     * Retourne le texte correspondant au champ de vue.
     *
     * @return le texte correspondant au champ de vue.
     */
    private Text fieldOfViewText() {
        Text fieldOfViewText = new Text();

        fieldOfViewText.textProperty().bind(Bindings.format(
                Locale.ROOT,
                "Champ de vue : %.2f°",
                viewingParametersBean.fieldOfViewDegProperty() ));

        return fieldOfViewText;
    }

    /**
     * Retourne la barre de contrôle.
     *
     * @return la HBox correspondant à la barre de contrôle.
     */
    private HBox controlBar() {
        HBox controlBar = new HBox(
                menuBar(),
                observPos(),
                momentOfObserv(),
                passageOfTime()
        );
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        Separator separator1 = new Separator(Orientation.VERTICAL);
        controlBar.getChildren().add(1, separator1);

        Separator separator3 = new Separator(Orientation.VERTICAL);
        controlBar.getChildren().add(3, separator3);

        Separator separator5 = new Separator(Orientation.VERTICAL);
        controlBar.getChildren().add(5, separator5);

        return controlBar;
    }

    /**
     * Retourne la barre de menu contenant le seul menu "Menu".
     *
     * @return une MenuBar contenant le Menu "Menu"..
     */
    private MenuBar menuBar() {
        MenuItem mapItem = new MenuItem(MAP_MENU);
        mapItem.setOnAction(event -> windowMap.show());

        MenuItem acceleratorItem = new MenuItem(ACCELERATOR_MENU);
        acceleratorItem.setOnAction(event -> windowAccelerator.show());

        MenuItem celestialTrackingItem = new MenuItem(TRACKING_MENU);
        celestialTrackingItem.setOnAction(event -> windowTracking.show());

        MenuItem displayItem = new MenuItem(DISPLAY_MENU);
        displayItem.setOnAction(event -> windowDisplay.show());

        MenuItem settingItem = new MenuItem(SETTINGS_MENU);
        settingItem.setOnAction(event -> windowSettings.show());

        MenuItem exitItem = new MenuItem("Quitter");
        exitItem.setOnAction(event -> System.exit(0));

        Menu fileMenu = new Menu("Menu");
        fileMenu.getItems().addAll(
                mapItem,
                acceleratorItem,
                celestialTrackingItem,
                displayItem,
                settingItem,
                exitItem
        );
        return new MenuBar(fileMenu);
    }

    /**
     * Retourne la HBox contenant la position d'observation en coordonnées
     * géographiques, pour la barre de contrôle.
     *
     * @return la HBox contenant la position d'observation en coordonnées
     *         géographiques.
     */
    private HBox observPos() {
        Label lonLabel = new Label("Longitude (°) :");
        Label latLabel = new Label("Latitude (°) :");

        TextField latTextField = latTextField();

        HBox observPos = new HBox(
                lonLabel,
                lonTextField(latTextField),
                latLabel,
                latTextField
        );
        observPos.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observPos;
    }

    /**
     * Retourne le texte pour la latitude de la position d'observation.
     *
     * @return Le TextField pour la latitude de la position d'observation.
     */
    private TextField latTextField() {
        TextFormatter<Number> latTextFormatter = getTextFormatterForPosition(false);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());
        latTextFormatter.valueProperty().addListener(e -> {
            mapManager.drawMapWithThumbtack(observerLocationBean.getCoordinates());
            sky.requestFocus();
        });

        TextField latTextField = new TextField();
        latTextField.setTextFormatter(latTextFormatter);
        latTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        return latTextField;
    }

    /**
     * Retourne le texte pour la longitude de la position d'observation.
     *
     * @param latTextField texte de la latitude de la position d'observation.
     * @return Le TextField pour la longitude de la position d'observation.
     */
    private TextField lonTextField(TextField latTextField) {
        TextFormatter<Number> lonTextFormatter = getTextFormatterForPosition(true);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());
        lonTextFormatter.valueProperty().addListener(e -> {
            mapManager.drawMapWithThumbtack(observerLocationBean.getCoordinates());
            latTextField.requestFocus();
        });

        TextField lonTextField = new TextField();
        lonTextField.setTextFormatter(lonTextFormatter);
        lonTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        return lonTextField;
    }

    /**
     * Retourne la HBox contenant le moment d'observation (date, heure, zone),
     * pour la barre de contrôle.
     *
     * @return la HBox contenant le moment d'observation (date, heure, zone).
     */
    private HBox momentOfObserv() {
        ComboBox<ZoneId> zoneBox = zoneBox();

        Label hourLabel= new Label("Heure :");
        TextField hourTextField = textHour(zoneBox);

        Label dateLabel = new Label("Date :");
        DatePicker datePicker = datePicker(hourTextField);

        HBox momentOfObserv = new HBox(
                dateLabel,
                datePicker,
                hourLabel,
                hourTextField,
                zoneBox
        );
        momentOfObserv.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return momentOfObserv;
    }

    /**
     * Retourne la boite contenant toutes les zones possibles.
     *
     * @return la boite contenant toutes les zones possibles.
     */
    private ComboBox<ZoneId> zoneBox() {
        ComboBox<ZoneId> zoneBox = new ComboBox<>();
        zoneBox.setStyle("-fx-pref-width: 180;");
        zoneBox.getItems().addAll(getListOfAllZone());

        zoneBox.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());
        zoneBox.disableProperty().bind(timeAnimator.getRunning());
        zoneBox.valueProperty().addListener(e -> sky.requestFocus());

        return zoneBox;
    }

    /**
     * Retourne le sélecteur de date.
     *
     * @param hourTextField le texte de l'heure d'observation.
     * @return le sélecteru de date sous forme d'un DatePicker.
     */
    private DatePicker datePicker(TextField hourTextField) {
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-pref-width: 120;");

        datePicker.disableProperty().bind(timeAnimator.getRunning());
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());
        datePicker.valueProperty().addListener(e -> hourTextField.requestFocus());

        return datePicker;
    }

    /**
     * Retourne le TextField de l'heure d'observation.
     *
     * @param zoneBox boite contenant les zones.
     * @return le TextField de l'heure d'observation.
     */
    private TextField textHour(ComboBox<ZoneId> zoneBox) {
        TextFormatter<LocalTime> hourTextFormatter = getTextFormatterForHour();
        hourTextFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());
        hourTextFormatter.valueProperty().addListener(e -> zoneBox.requestFocus());

        TextField hourTextField = new TextField();
        hourTextField.setTextFormatter(hourTextFormatter);
        hourTextField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");
        hourTextField.disableProperty().bind(timeAnimator.getRunning());

        return hourTextField;
    }

    /**
     * Retourne le TextFormatter pour le texte de l'heure d'observation.
     *
     * @return le TextFormatter pour le texte de l'heure d'observation.
     */
    private TextFormatter<LocalTime> getTextFormatterForHour() {
        DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);

        return new TextFormatter<>(stringConverter);
    }

    /**
     * Retourne la liste de toutes les zones.
     *
     * @return l'identité de toutes les zones sous forme d'une List<ZoneId>.
     */
    private List<ZoneId> getListOfAllZone() {
        Set<String> setStringZones = ZoneId.getAvailableZoneIds();
        List<String> listStringZones = new ArrayList<>(setStringZones);
        Collections.sort(listStringZones);

        List<ZoneId> listZones = new ArrayList<>();
        for(String stringZone : listStringZones)
            listZones.add(ZoneId.of(stringZone));

        return listZones;
    }

    /**
     * Retourne une HBox contenant gérant l'accélérateur de temps.
     *
     * @return une HBox contenant gérant l'accélérateur de temps.
     * @throws UncheckedIOException si l'inputStream n'est pas valide.
     */
    private HBox passageOfTime() throws UncheckedIOException {
        try(InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {

            Font fontAwesome = Font.loadFont(fontStream, 15);
            Button playStopButton = playStopButton(fontAwesome);

            HBox passageOfTime = new HBox(
                    acceleratorChoiceBox(playStopButton),
                    resetButton(fontAwesome, playStopButton),
                    playStopButton
            );
            passageOfTime.setStyle("-fx-spacing: inherit;");

            return passageOfTime;
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Retourne la ChoiceBox contenant les différents accélérateurs de temps.
     *
     * @return la ChoiceBox contenant les différents accélérateurs de temps.
     */
    private ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox(Button playStopButton) {
        ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox = new ChoiceBox<>();

        NamedTimeAccelerator[] timeAccelerators = NamedTimeAccelerator.values();
        List<NamedTimeAccelerator> list = new ArrayList<>(Arrays.asList(timeAccelerators));

        acceleratorChoiceBox.setItems(FXCollections.observableList(list));
        acceleratorChoiceBox.setValue(NamedTimeAccelerator.TIMES_300);

        timeAnimator.acceleratorProperty()
                .bind(Bindings.select(acceleratorChoiceBox.valueProperty(), "accelerator"));

        acceleratorChoiceBox.valueProperty().addListener(e -> {
            if(timeAnimator.getRunning().get()) {
                timeAnimator.resetSimulationTime();
                sky.requestFocus();
            } else
                playStopButton.requestFocus();
        });

        acceleratorsDisableProperty = acceleratorChoiceBox.disableProperty();

        return acceleratorChoiceBox;
    }

    /**
     * Retourne le bouton de reset qui permets d'arrêter l'animateur de
     * temps et de retourner à la date et l'heure actuelle.
     *
     * @param fontAwesome style d'image pour le bouton.
     * @return un bouton de reset.
     */
    private Button resetButton(Font fontAwesome, Button playStopButton) {
        Button resetButton = new Button(RESET);
        resetButton.setFont(fontAwesome);

        resetButton.setOnAction(e -> {
            canvasManager.clearHorCoordsList();

            if(timeAnimator.getRunning().get())
                stopAnim(playStopButton);

            dateTimeBean.setZonedDateTime(ZonedDateTime.now());
            sky.requestFocus();
        });

        return resetButton;
    }

    /**
     * Retourne le bouton play/stop qui permets de mettre en route et
     * arrêter l'animateur de temmps.
     *
     * @param fontAwesome style d'image pour le bouton.
     * @return un bouton play/stop.
     */
    private Button playStopButton(Font fontAwesome) {
        Button playStopButton = new Button(PLAY);
        playStopButton.setFont(fontAwesome);

        playStopButton.setOnAction(e -> {
            if(timeAnimator.getRunning().get())
                stopAnim(playStopButton);
            else
                playAnim(playStopButton);

            sky.requestFocus();
        });

        return playStopButton;
    }

    /**
     * Mets sur pause l'animateur de temps et mets le bouton sur play.
     *
     * @param playStopButton bouton play/stop.
     */
    private void stopAnim(Button playStopButton) {
        timeAnimator.stop();
        playStopButton.setText(PLAY);
    }

    /**
     * Active l'animateur de temps et mets le bouton sur stop.
     *
     * @param playStopButton bouton play/stop.
     */
    private void playAnim(Button playStopButton) {
        timeAnimator.start();
        playStopButton.setText(STOP);
    }

    /**
     * Retourne le TextFormatter pour le texte de la longitude ou de la latitude.
     *
     * @param isForLon true si c'est pour la longitude et false pour la latitude.
     * @return le TextFormatter pour le texte de la longitude ou de la latitude.
     */
    private TextFormatter<Number> getTextFormatterForPosition(boolean isForLon) {
        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newDeg = stringConverter.fromString(newText).doubleValue();

                if(isForLon)
                    return GeographicCoordinates.isValidLonDeg(newDeg)
                            ? change
                            : null;
                else
                    return GeographicCoordinates.isValidLatDeg(newDeg)
                            ? change
                            : null;
            } catch (Exception e) {
                return null;
            }
        });

        return new TextFormatter<>(stringConverter, 0, filter);
    }

}
