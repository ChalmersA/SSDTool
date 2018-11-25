/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that
 * contains text.
 */
public class StickyNoteFigure extends BentCornerFigure {

    /** The inner TextFlow * */
    private TextFlow textFlow;

    /**
     * Creates a new StickyNoteFigure with a default MarginBorder size of
     * DEFAULT_CORNER_SIZE - 3 and a FlowPage containing a TextFlow with the
     * style WORD_WRAP_SOFT.
     */
    public StickyNoteFigure() {
        this(BentCornerFigure.DEFAULT_CORNER_SIZE - 3);
    }

    /**
     * Creates a new StickyNoteFigure with a MarginBorder that is the given size
     * and a FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
     * 
     * @param borderSize the size of the MarginBorder
     */
    public StickyNoteFigure(int borderSize) {
        setBorder(new MarginBorder(borderSize));
        FlowPage flowPage = new FlowPage();

        textFlow = new TextFlow();

        textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
                ParagraphTextLayout.WORD_WRAP_SOFT));

        flowPage.add(textFlow);

        setLayoutManager(new StackLayout());
        add(flowPage);
    }

    /**
     * Returns the text inside the TextFlow.
     * 
     * @return the text flow inside the text.
     */
    public String getText() {
        return textFlow.getText();
    }

    /**
     * Sets the text of the TextFlow to the given value.
     * 
     * @param newText the new text value.
     */
    public void setText(String newText) {
        textFlow.setText(newText);
    }

    public TextFlow getTextFlow() {
        return textFlow;
    }

    public void setTextFlow(TextFlow textFlow) {
        this.textFlow = textFlow;
    }

}
