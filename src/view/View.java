package view;

import java.awt.Desktop;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.MainController;
import model.Activity;
import model.Activity.State;
import model.EEG;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class View implements Observer {

	private static final Logger lMain = Logger.getLogger(MainController.class.getName());
	Desktop desktop = Desktop.getDesktop();

	// References
	MainController main;
	FeedbackView fbView;

	Stage stage;
	Scene scene;
	VBox pane;
	VBox connectionBox;
	StackPane stackPane;
	ImageView connectionIV;
	Button minimizeButton, closeButton, connectionButton, graphButton;
	FillTransition ft;
	Color currentColor;
	Label connectionStatus;
	Tooltip waveTT1, waveTT2;

	double initX;
	double initY;

	/** Constructor.
	 * @param main MainController */
	public View(MainController main) {
		this.main = main;
		initStage();
	}

	/** Initializes the stage. */
	private void initStage() {
		stage = new Stage(StageStyle.TRANSPARENT);

		StackPane stackPane = new StackPane();
		stackPane.setBackground(Background.EMPTY);
		scene = new Scene(stackPane, 400, 400, Color.TRANSPARENT);
		scene.getStylesheets().add(Resources.CSS.pathString);

		currentColor = Colors.DARK_PRIMARY.color;
		Circle circle = new Circle(200);
		circle.setFill(currentColor);
		circle.setEffect(new InnerShadow(5, Colors.PRIMARY_TEXT.color));
		stackPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				initX = me.getScreenX() - stage.getX();
				initY = me.getScreenY() - stage.getY();
			}
		});
		stackPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				stage.setX(me.getScreenX() - initX);
				stage.setY(me.getScreenY() - initY);
			}
		});
		ft = new FillTransition(Duration.millis(3000), circle);
		stackPane.getChildren().addAll(circle, getPane());

		stage.setTitle(Strings.WINDOW_TITLE.string);
		stage.getIcons().add(Resources.IMAGE_NOSIGNAL.image);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(we -> close());
	}

	private HBox getWindowButtons() {
		closeButton = new Button("", new ImageView(Resources.WINDOW_CLOSE.image));
		closeButton.setOnAction(ae -> close());
		closeButton.setOnMouseEntered(me -> closeButton.setGraphic(new ImageView(Resources.WINDOW_CLOSE2.image)));
		closeButton.setOnMouseExited(me -> closeButton.setGraphic(new ImageView(Resources.WINDOW_CLOSE.image)));
		closeButton.getStyleClass().add("windowButtons");
		minimizeButton = new Button("", new ImageView(Resources.WINDOW_MINIMIZE.image));
		minimizeButton.setOnAction(ae -> stage.setIconified(true));
		minimizeButton.setOnMouseEntered(me -> minimizeButton.setGraphic(new ImageView(Resources.WINDOW_MINIMIZE2.image)));
		minimizeButton.setOnMouseExited(me -> minimizeButton.setGraphic(new ImageView(Resources.WINDOW_MINIMIZE.image)));
		minimizeButton.getStyleClass().add("windowButtons");
		HBox hbox = new HBox();
		hbox.getChildren().addAll(minimizeButton, closeButton);
		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.setTranslateY(-scene.getHeight() * 0.1);
		return hbox;
	}

	/** Returns the BorderPane.
	 * @return BorderPane */
	private VBox getPane() {
		pane = new VBox();
		pane.getChildren().addAll(getConnectionBox());
		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(30);
		return pane;
	}

	/** Returns the ConnectionBox.
	 * @return ConnectionBox */
	private VBox getConnectionBox() {
		connectionIV = new ImageView(Resources.IMAGE_NOSIGNAL.image);
		connectionIV.setFitWidth(50);
		connectionIV.setFitHeight(50);
		connectionStatus = new Label("", connectionIV);
		waveTT1 = new Tooltip(Strings.SIGNAL_TOOLTIP.string + "\n" + Strings.THINKGEAR_NOT_CONNECTED);
		waveTT2 = new Tooltip(Strings.SIGNAL_TOOLTIP.string + "\n" + Strings.THINKGEAR_CONNECTED);
		connectionStatus.setTooltip(waveTT1);
		connectionButton = new Button(Strings.START.string);
		connectionButton.getStyleClass().add("button2");
		connectionButton.setOnAction(ae -> startLogging());
		connectionBox = new VBox();
		connectionBox.setAlignment(Pos.CENTER);
		connectionBox.getChildren().addAll(getWindowButtons(), connectionStatus, connectionButton);
		return connectionBox;
	}

	/** Stops the logging and closes the window. */
	private void close() {
		lMain.log(Level.INFO, "Window closed");
		// stopLogging();
		stage.close();
		System.exit(0);
	}

	/** Starts the Loggers. */
	public void startLogging() {
		getMain().initLogger();
		getMain().startEEGLogging();
		getMain().startPALogging();
		connectionButton.setText(Strings.STOP.string);
		connectionButton.setOnAction(ae -> stopLogging());
		fbView = new FeedbackView(this);
		pane.getChildren().remove(graphButton);
		pane.getChildren().add(fbView);
		// pane.setBottom(fbView);
	}

	/** Closes the logfiles. */
	public void stopLogging() {
		MainController.fhEEG.close();
		MainController.fhMain.close();
		MainController.fhWin.close();
		MainController.fhTask.close();
		connectionButton.setText(Strings.START.string);
		connectionButton.setOnAction(ae -> startLogging());
		pane.getChildren().remove(fbView);
		graphButton = new Button(Strings.GRAPH.string, new ImageView(Resources.GRAPH.image));
		graphButton.getStyleClass().add("button2");
		graphButton.setOnAction(ae -> openHTML());
		graphButton.setOnMouseEntered(me -> graphButton.setGraphic(new ImageView(Resources.GRAPH2.image)));
		graphButton.setOnMouseExited(me -> graphButton.setGraphic(new ImageView(Resources.GRAPH.image)));
		pane.getChildren().add(graphButton);
		// pane.setBottom(graphButton);
	}

	/** Opens the browser to visualize the last logs. */
	private void openHTML() {
		URI uri;
		try {
			uri = this.getClass().getResource(Strings.HTML.string).toURI();
			System.out.println(uri);
			desktop.browse(uri);
		} catch (Exception e) {
			lMain.log(Level.WARNING, e.getMessage());
		}
	}

	// ----------------UPDATE-------------------------

	/** Changes a label.
	 * @param s String
	 * @param i Index */
	public void changeStatus(int i) {
		switch (i) {
		// case 1:
		// fbView.userStatus.setText(s);
		// break;
		// case 2:
		// fbView.currentProcess.setText(s);
		// break;
		case 3:
			connectionStatus.setTooltip(waveTT2);
			break;
		}
	}

	/** Changes the background color depending on user's state.
	 * @param state ActivityState */
	public void changeColor(State state) {
		Color color = Colors.ACCENT.color;
		switch (state) {
		case ACTIVE:
			color = Colors.DARK_PRIMARY.color;
			break;
		case IDLE:
			//color = Colors.LIGHTGREEN.color;
			color = Colors.PRIMARY.color;
			break;
		case AWAY:
			//color = Colors.YELLOW.color;
			color = Colors.LIGHT_PRIMARY.color;
			break;
		case UNKNOWN:
			color = Colors.PRIMARY_TEXT.color;
			break;
		}
		ft.setToValue(color);
		ft.play();
		// changeStatus(Strings.YOU_ARE.string + state.toString().toLowerCase(),
		// 1);
	}

	/** Changes the connection image.
	 * @param i Index */
	public void changeImage(int i) {
		Image image = Resources.IMAGE_NOSIGNAL.image;
		switch (i) {
		case 0:
			image = Resources.IMAGE_NOSIGNAL.image;
			break;
		case 1:
			image = Resources.IMAGE_CONNECTING1.image;
			break;
		case 2:
			image = Resources.IMAGE_CONNECTING2.image;
			break;
		case 3:
			image = Resources.IMAGE_CONNECTING3.image;
			break;
		case 4:
			image = Resources.IMAGE_CONNECTED.image;
			break;
		}
		stage.getIcons().remove(0);
		stage.getIcons().add(image);
		connectionIV.setImage(image);
	}

	@Override
	public void update(Observable obs, Object obj) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (obs instanceof Activity) {
					changeColor(((Activity) obs).getUserState());
					if (((Activity) obs).getUserState() == State.ACTIVE) {
						if (((Activity) obs).getOldState() == State.IDLE) {
							fbView.feedbackQuestion.setText(Strings.IDLE.string);
						}
						if (((Activity) obs).getOldState() == State.AWAY) {
							fbView.feedbackQuestion.setText(Strings.AWAY.string);
						}
					} else {
						// changeStatus(String.valueOf(((Activity)
						// obs).getProcess()), 2);
					}
				}
				if (obs instanceof EEG) {
					int i = 0;
					if ((int) obj >= 95)
						i = 0;
					if (65 < (int) obj && (int) obj < 95)
						i = 1;
					if (35 < (int) obj && (int) obj < 65)
						i = 2;
					if (5 < (int) obj && (int) obj < 35)
						i = 3;
					if ((int) obj <= 5)
						i = 4;
					changeImage(i);
					changeStatus(3);
				}
			}
		});
	}

	/** Returns the FeedbackView.
	 * @return FeedbackView */
	public FeedbackView getFbView() {
		return fbView;
	}

	/** Returns the MainController.
	 * @return MainController */
	public MainController getMain() {
		return main;
	}
}
