package org.lucasstarsz.composeapp.nodes;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

public class ComposeArea extends StyleClassedTextArea {

    protected Rectangle gutterRect = new Rectangle();

    public ComposeArea() {
        super();
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        bindRect();
    }

    private void bindRect() {
        gutterRect.heightProperty().bind(this.heightProperty());
        gutterRect.getStyleClass().add("lineno-rect");
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        try {
            ObservableList<Node> children = getChildren();
            if (!(children.get(0) == gutterRect)) {
                children.add(0, gutterRect);
            }

            int index = visibleParToAllParIndex(0);
            double wd = getParagraphGraphic(index).prefWidth(10);
            gutterRect.setWidth(wd);
        } catch (Exception ignored) {
        }
    }
}
