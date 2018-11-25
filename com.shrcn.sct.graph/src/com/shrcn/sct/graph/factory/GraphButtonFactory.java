package com.shrcn.sct.graph.factory;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jhotdraw.app.action.DuplicateAction;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.action.MoveToBackAction;
import org.jhotdraw.draw.action.MoveToFrontAction;
import org.jhotdraw.draw.action.UngroupAction;
import org.jhotdraw.draw.editor.DrawingEditor;

import com.shrcn.sct.graph.action.SetConnectionPointAction;

public class GraphButtonFactory extends ButtonFactory {

	public static Collection<Action> createSelectionActions(DrawingEditor editor) {
		LinkedList<Action> a = new LinkedList<Action>();
		a.add(new DuplicateAction());

		a.add(null); // separator
		a.add(new GroupAction(editor));
		a.add(new UngroupAction(editor));

		a.add(null); // separator
		a.add(new MoveToFrontAction(editor));
		a.add(new MoveToBackAction(editor));

		a.add(null);
		a.add(new SetConnectionPointAction(editor));// 设置图形连接点
		return a;
	}

	public static JToggleButton addSelectionToolTo(JToolBar tb, final DrawingEditor editor) {
		return addSelectionToolTo(tb, editor, createDrawingActions(editor), createSelectionActions(editor));
	}
}
