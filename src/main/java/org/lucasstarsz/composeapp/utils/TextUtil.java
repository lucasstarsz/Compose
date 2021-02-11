package org.lucasstarsz.composeapp.utils;

import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.model.TwoDimensional;
import org.lucasstarsz.composeapp.nodes.ComposeArea;

public class TextUtil {

    public static void shift(ComposeArea textArea, int shiftAmount) {
        if (textArea.selectedTextProperty().getValue().isEmpty()) {
            shiftSingleLine(textArea, shiftAmount);
        } else {
            shiftMultipleLines(textArea, shiftAmount);
        }
    }

    private static void shiftSingleLine(ComposeArea textArea, int shiftAmount) {
        int[] currLine = getCurrentLine(textArea);

        if (textArea.getText(currLine[0], currLine[1]).startsWith("\t") || shiftAmount > 0) {
            int caretPosition = textArea.getCaretPosition();

            if (shiftAmount > 0) {
                shiftAmount = Math.max(shiftAmount - 1, 1);

                textArea.replaceText(currLine[0], currLine[1],
                        "\t".repeat(shiftAmount) + textArea.getText(currLine[0], currLine[1])
                );
            } else if (shiftAmount < 0) {
                textArea.replaceText(currLine[0], currLine[1],
                        textArea.getText(currLine[0], currLine[1]).substring(Math.abs(shiftAmount))
                );
            }

            textArea.moveTo(caretPosition + shiftAmount);
        }
    }

    private static void shiftMultipleLines(ComposeArea textArea, int shiftAmount) {
        int originalStart = textArea.getSelection().getStart();
        int originalEnd = textArea.getSelection().getEnd();
        int[] linePositions = getSelectedLines(textArea);

        String[] lines = textArea.getText(linePositions[0], linePositions[3]).split("\n");

        int tabMovementCount = 0;
        int firstTabMovement = 0;
        for (int i = 0; i < lines.length; i++) {
            if (shiftAmount > 0) {
                lines[i] = "\t".repeat(shiftAmount) + lines[i];
                tabMovementCount += shiftAmount;
                if (i == 0) firstTabMovement += shiftAmount;
            } else {
                int ltcount = Math.min(Math.abs(shiftAmount), getLeadingTabCount(lines[i]));
                lines[i] = lines[i].substring(ltcount);
                tabMovementCount -= ltcount;
                if (i == 0) firstTabMovement -= ltcount;
            }
        }

        textArea.replaceText(linePositions[0], linePositions[3], String.join("\n", lines));
        textArea.selectRange(originalStart + firstTabMovement, originalEnd + tabMovementCount);
    }

    public static void enterWithTabs(ComposeArea textArea, KeyEvent event) {
        event.consume();

        int tabCount = getLeadingTabCount(textArea);
        String tabsToAppend = (tabCount > 0 ? "\t".repeat(tabCount) : "");

        textArea.insertText(textArea.getCaretPosition(), '\n' + tabsToAppend);
        textArea.requestFollowCaret();
    }

    /**
     * Gets the leading tab count of the current position of the caret in a ComposeArea.
     *
     * @param ta The ComposeArea to check the text of.
     * @return The leading tab count.
     */
    public static int getLeadingTabCount(ComposeArea ta) {
        /* start is caret index - 1, since caret position only matters
         * if the caret starts as a newline or tab. */
        int start = ta.getText().lastIndexOf('\t', ta.getCaretPosition() - 1);

        // if no tabs present before caret
        if (start == -1) {
            return 0;
        }

        // if 2+ newlines in a row
        if (ta.getText().charAt(ta.getCaretPosition() - 1) == '\n') {
            return 0;
        }

        /* start is the last known tab, meaning we need to check for
         * tabs starting there. */
        int count = 0;
        while (ta.getText().charAt(start - count) == '\t') {
            count++;
        }

        return count;
    }

    /**
     * Gets the leading tab count of the specified String.
     *
     * @param s The String to check.
     * @return The leading tab count.
     */
    public static int getLeadingTabCount(String s) {
        int count = 0;

        for (char c : s.toCharArray()) {
            if (c == '\t') count++;
            else break;
        }

        return count;
    }

    /**
     * Gets the indexes of the current line of the ComposeArea (the line at the caret).
     *
     * @param tx The ComposeArea.
     * @return The indexes of the current line of text.
     */
    private static int[] getCurrentLine(ComposeArea tx) {
        return getLineAtPosition(tx, tx.getCaretPosition());
    }

    /**
     * Gets the indexes of the selected lines of the ComposeArea specified.
     *
     * @param tx The ComposeArea.
     * @return the indexes of the first line, and the indexes of the last line.
     */
    public static int[] getSelectedLines(ComposeArea tx) {
        if (tx.getSelectedText().isEmpty())
            throw new IllegalStateException("The text area doesn't have anything selected.");

        int[] front = getLineAtPosition(tx, tx.getSelection().getStart());
        int[] back = getLineAtPosition(tx, tx.getSelection().getEnd());

        if (tx.getText().charAt(tx.getSelection().getEnd()) == '\n') {
            back = getLineAtPosition(tx, tx.getSelection().getEnd() - 1);
        }

        return new int[]{front[0], front[1], back[0], back[1]};
    }

    /**
     * Gets the indexes of the line of text from the ComposeArea at the position specified.
     *
     * @param tx       The ComposeArea to get the text from.
     * @param position The position to check from.
     * @return The indexes of the line of text.
     */
    public static int[] getLineAtPosition(ComposeArea tx, int position) {
        TwoDimensional.Position pos = tx.offsetToPosition(position, TwoDimensional.Bias.Backward);

        int length = (pos.getMinor() - position < 0 ? pos.getMinor() : pos.getMinor() - position);
        if (!tx.getText().contains("\n")) length = tx.getLength();

        int start = tx.getText().lastIndexOf('\n', position) + 1;

        if (tx.getLength() > start + length && tx.getText().charAt(start + length) != '\n') {
            length = tx.getText().indexOf('\n', start) - start;
        }
        if (length < 0) length = tx.getLength() - start;

        return new int[]{start, start + length};
    }
}
