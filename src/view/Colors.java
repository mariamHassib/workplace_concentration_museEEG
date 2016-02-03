package view;

import javafx.scene.paint.Color;

public enum Colors {
	// @formatter:off
	C1("#012345"),
	C2("#FFFFFF"),
	RED("#F44336"),
	LIGHTGREEN ("#8BC34A"),
	YELLOW("#FFEB3B"),
	
	DARK_PRIMARY ("#455A64"),
	PRIMARY ("#607D8B"),
	LIGHT_PRIMARY ("#CFD8DC"), 
	TEXT ("#FFFFFF"),
	ACCENT ("#FFEB3B"),
	PRIMARY_TEXT ("#212121"),
	SECONDARY_TEXT ("#727272"),
	DIVIDER ("#B6B6B6"),
	;
	// @formatter:on

	final public Color color;

	Colors(String colorString) {
		this.color = Color.web(colorString);
	}
}
