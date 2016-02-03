package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Feedback.Work;

public class FeedbackView extends VBox {

	View view;
	VBox feedbackPane;
	Label feedbackQuestion; //userStatus, currentProcess
	TextField feedbackField;
	String feedback;
	Button[] feedbackButtons;
	Tooltip [] tooltips;
	

	public FeedbackView(View view) {
		this.view = view;
		this.setAlignment(Pos.CENTER);

//		userStatus = new Label(Strings.YOU_ARE.string + State.UNKNOWN.toString().toLowerCase());
//		currentProcess = new Label();
		feedbackQuestion = new Label(Strings.HI.string);
		feedbackField = new TextField();
		feedbackField.getStyleClass().add("textfield");
		feedbackField.setMaxWidth(view.scene.getWidth()*0.7);

		FlowPane tagBox = new FlowPane();
		Label tag = new Label("", new ImageView(Resources.TAG.image));
		tag.setTooltip(new Tooltip(Strings.TAG0_TOOLTIP.string));
		tagBox.getChildren().add(tag);
		tagBox.setHgap(10);
		tagBox.setAlignment(Pos.CENTER);
		
		int length = Work.values().length;
		feedbackButtons = new Button[length];
		tooltips = new Tooltip[length];

		for (int i = 0; i < Work.values().length; i++) {
			int x = i;
			tooltips[i] = new Tooltip(Work.values()[i].tooltip);
			feedbackButtons[i] = new Button(Work.values()[i].toString());
			feedbackButtons[i].setTooltip(tooltips[i]);
			feedbackButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					feedback = feedbackField.getText();
					getView().getMain().getFeedback().change(Work.values()[x], feedback);
					feedbackField.setText("");
					feedbackQuestion.setText(Strings.HI.string);
				}
			});
			tagBox.getChildren().add(feedbackButtons[i]);
		}
		this.setSpacing(10);
		this.getChildren().addAll(feedbackQuestion, feedbackField, tagBox);
	}

	public View getView() {
		return view;
	}
}
