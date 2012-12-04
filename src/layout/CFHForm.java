package layout;

import control.CFTextField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;

public class CFHForm extends HBox {
    // Event filter. validation
    private EventHandler<ActionEvent> validator;
    // Event handelr. [0]:enable, [1]:action, [2]:disable
    private List<EventHandler<ActionEvent>> actions = new ArrayList<EventHandler<ActionEvent>>();
    
    public CFHForm() {}

    public CFHForm(double spacing) { super(spacing); }

    public void setOnAction(EventHandler<ActionEvent> action) {
        registerValidator();
        registerAction(action);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return actions.get(1);
    }

    protected void registerValidator() {
        if (validator != null) return;

        validator = buildValidator();
        addEventFilter(ActionEvent.ACTION, validator);
    }

    protected EventHandler<ActionEvent> buildValidator() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!validate()) event.consume();
            }
        };
    }
        
    protected void registerAction(EventHandler<ActionEvent> action) {
        if (actions.isEmpty())
            actions = buildActions(action);
        else
            actions.set(1, action);

        for (EventHandler<ActionEvent> handler : actions)
            addEventHandler(ActionEvent.ACTION, handler);
    }

    protected List<EventHandler<ActionEvent>> buildActions(EventHandler<ActionEvent> action) {
        actions.add(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setDisabled(true);
            }
        });
        actions.add(action);
        actions.add(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setDisabled(false);
            }
        });
        return actions;
    }

    protected boolean validate() {
        for (CFTextField target : findByNodeType(CFTextField.class)) {
            if (!target.isCorrectPresence()) {
                showPopup(target, "このフィールドを入力してください");
                return false;
            }
            if (!target.isCorrectFormat()) {
                showPopup(target, target.getMessage());
                return false;
            } 
        }
        return true;
    }

    protected <T extends Node> List<T> findByNodeType(Class<T> clazz) {
        List<T> nodes = new ArrayList<T>();
        Queue<Pane> queue = new LinkedList<Pane>();

        queue.add(this);
        while (!queue.isEmpty()) {
            Pane pane = queue.poll();
            for (Node node : pane.getChildren()) {
                if (clazz.isAssignableFrom(node.getClass()))
                    nodes.add((T)node);
                if (node instanceof Pane)
                    queue.add((Pane)node);
            }
        }
        return nodes;
    }

    protected void showPopup(CFTextField target, String message) {
        Popup popup = buildPopup(message);
        
        /*
        System.out.println(target.getText());
        System.out.println(target.getLayoutX());
        System.out.println(this.getScene().getWindow());
        */
        double x = target.getLayoutX() + target.getScene().getWindow().getX();
        double y = target.getLayoutY() + target.getHeight() * 2.0 + target.getScene().getWindow().getY();
        System.err.println(target.getScene().getWindow().getX());
        System.err.println(target.getScene().getWindow().getY());
        System.err.println(target.getScene().getY());
        //popup.setX(target.getLayoutX());
        //popup.setY(target.getLayoutY() + target.getHeight());
        //popup.show(this, target.getLayoutX(), getLayoutY() + target.getHeight());
        popup.show(this.getScene().getWindow(), x, y);
    }

    protected Popup buildPopup(String message) {
        Label msgLabel = LabelBuilder.create()
            .text(message)
            .wrapText(true)
            .build();

        double width = msgLabel.getWidth() + 30d;
        double height = msgLabel.getHeight() + 30d;
        
        return PopupBuilder.create()
            .autoHide(true)
            .content(
                StackPaneBuilder.create()
                    .children(
                        RectangleBuilder.create()
                            .width(width)
                            .height(height)
                            .arcWidth(width * 0.15)
                            .arcHeight(height * 0.15)
                            .fill(Color.ANTIQUEWHITE)
                            .effect(
                                DropShadowBuilder.create()
                                .offsetX(3.0)
                                .offsetY(3.0)
                                .color(Color.GRAY)
                                .build()
                            )
                            .build(),
                        HBoxBuilder.create()
                            .children(
                                msgLabel
                            )
                            .build()
                    )
                    .build()
            )
            .build();
    }
    
    public Map<String, String> params() {
        Map<String, String> params = new HashMap<String, String>();
        for (CFTextField input : findByNodeType(CFTextField.class)) {
            params.put(input.getName(), input.getText());
        }
        return params;
    }
}

