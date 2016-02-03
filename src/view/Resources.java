package view;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.image.Image;

public enum Resources {

	// @formatter:off
	CSS("res/stylesheet.css"), 
	IMAGE_CONNECTED("res/connected_v1.png"),
	IMAGE_NOSIGNAL("res/nosignal_v1.png"),
	IMAGE_CONNECTING1("res/connecting1_v1.png"),
	IMAGE_CONNECTING2("res/connecting2_v1.png"),
	IMAGE_CONNECTING3("res/connecting3_v1.png"), 
	
	WINDOW_MINIMIZE("res/window-minimize.png"),
	WINDOW_MINIMIZE2("res/window-minimize-dark.png"),
	WINDOW_CLOSE("res/window-close.png"),
	WINDOW_CLOSE2("res/window-close-dark.png"),
	GRAPH("res/chart-line.png"),
	GRAPH2("res/chart-line-dark.png"),
	TAG("res/tag.png")
	;
	// @formatter:on

	/** Pfad. */
	public final String pathString;
	/** InputStream. */
	public final InputStream input;
	/** URI für die PDFs. */
	public URI uri;
	public Image image;

	/** Konstruktor.
	 * @param src String */
	private Resources(String src) {
		input = Resources.class.getResourceAsStream(src);
		pathString = this.getClass().getResource(src).toExternalForm();
		uri = null;
		image = new Image(pathString);
		try {
			uri = this.getClass().getResource(src).toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}