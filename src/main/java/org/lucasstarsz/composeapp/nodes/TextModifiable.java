package org.lucasstarsz.composeapp.nodes;

public interface TextModifiable {

    void undo();

    void redo();

    void copy();

    void cut();

    void paste();

    void selectAll();
}
