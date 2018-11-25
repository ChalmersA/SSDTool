/*
 * @(#)CreationTool.java  2.3  2007-08-22
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package com.shrcn.sct.graph.tool;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.FloatingTextField;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.tool.Messages;
import com.shrcn.business.graph.util.CreationToolUtil;
import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.das.PrimaryDAO;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
/**
 * A tool to create new figures. The figure to be created is specified by a
 * prototype.
 * <p>
 * To create a figure using the CreationTool, the user does the following mouse
 * gestures on a DrawingView:
 * <ol>
 * <li>Press the mouse button over the DrawingView. This defines the
 * start point of the Figure bounds.</li>
 * <li>Drag the mouse while keeping the mouse button pressed, and then release
 * the mouse button. This defines the end point of the Figure bounds.</li>
 * </ol>
 * The CreationTool works well with most figures that fit into a rectangular
 * shape or that concist of a single straight line. For figures that need
 * additional editing after these mouse gestures, the use of a specialized
 * creation tool is recommended. For example the TextTool allows to enter the
 * text into a TextFigure after the user has performed the mouse gestures.
 * <p>
 * Alltough the mouse gestures might be fitting for the creation of a connection,
 * the CreationTool is not suited for the creation of a ConnectionFigure. Use
 * the ConnectionTool for this type of figures instead.
 *
 * @author Werner Randelshofer
 * @version 2.2 2007-08-22 Added property 'toolDoneAfterCreation'.
 * <br>2.1.1 2006-07-20 Minimal size treshold was enforced too eagerly.
 * <br>2.1 2006-07-15 Changed to create prototype creation from class presentationName.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class BusbarTool extends AbstractTool implements ActionListener{
	private static final long serialVersionUID = 1L;
	private boolean isForCreationOnly = true;
	private FloatingTextField textField;
	private TextHolderFigure typingTarget;

	/**
	 * Attributes to be applied to the created ConnectionFigure. These
	 * attributes override the default attributes of the DrawingEditor.
	 */
    private Map<AttributeKey, Object> prototypeAttributes;
    
    /**
     * A localized name for this tool. The presentationName is displayed by the
     * UndoableEdit.
     */
    private String presentationName;
    /**
     * Treshold for which we create a larger shape of a minimal size.
     */
    private Dimension minimalSizeTreshold = new Dimension(2,2);
    /**
     * We set the figure to this minimal size, if it is smaller than the
     * minimal size treshold.
     */
    private Dimension minimalSize = new Dimension(144, 5);//(40,40);
    /**
     * The prototype for new figures.
     */
    private Figure prototype;
    /**
     * The created figure.
     */
    protected Figure createdFigure;
    
    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    private boolean isToolDoneAfterCreation = true;
    
    /** Creates a new instance with the specified prototype but without an
     * attribute set. The CreationTool clones this prototype each time a new
     * Figure needs to be created. When a new Figure is created, the
     * CreationTool applies the default attributes from the DrawingEditor to it,
     * and then it applies the attributes to it, that have been supplied in
     * this constructor.
     *
     * @param prototype The prototype used to create a new Figure.
     * @param attributes The CreationTool applies these attributes to the
     * prototype after having applied the default attributes from the DrawingEditor.
     */
    public BusbarTool(Figure prototype, Map<AttributeKey, Object> attributes) {
    	this.prototype = prototype;
        this.prototypeAttributes = attributes;
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels"); //$NON-NLS-1$
        this.presentationName = labels.getString("createFigure"); //$NON-NLS-1$
    }

    public Figure getPrototype() {
        return prototype;
    }
    
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        //getView().clearSelection();
        //getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        endEdit();
        if (getView() != null) {
            getView().setCursor(Cursor.getDefaultCursor());
        }
        if (createdFigure != null) {
            if (createdFigure instanceof CompositeFigure) {
                ((CompositeFigure) createdFigure).layout();
            }
            createdFigure = null;
        }
    }
    
    protected void beginEdit(TextHolderFigure textHolder) {
        if (textField == null) {
            textField = new FloatingTextField();
            textField.addActionListener(this);
        }
        
        if (textHolder != typingTarget && typingTarget != null) {
            endEdit();
        }
        textField.createOverlay(getView(), textHolder);
        textField.setBounds(getFieldBounds(textHolder), textHolder.getText());
        textField.requestFocus();
        typingTarget = textHolder;
    }
    
    /**
     * 处理鼠标按下事件
     * @param e
     */
    public void mousePressed(MouseEvent evt) {
    	super.mousePressed(evt);
    	BusbarLabel label =null;
		TextHolderFigure textHolder = null;
		Figure pressedFigure = getDrawing().findFigureInside(
				getView().viewToDrawing(new Point(evt.getX(), evt.getY())));
		if (null != pressedFigure && (pressedFigure instanceof BusbarFigure)) {
			textHolder = ((BusbarFigure) pressedFigure).getLabelFor();
			if(textHolder == null){
				label = createBusBarLabel((BusbarFigure)pressedFigure);
				label.setText(AttributeKeys.EQUIP_NAME.get(pressedFigure));
				getDrawing().add(label);
				textHolder = ((BusbarFigure) pressedFigure).getLabelFor();
			}
		}
		if (textHolder != null) {
			beginEdit(textHolder);
			return;
		}
		//为了兼容以前的单线图中母线没有标签
		if (pressedFigure != null && textHolder == null)
			return;
        
		if (typingTarget != null) {
            endEdit();
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        } else {
        	super.mousePressed(evt);
	        // 判断当前插入点是否为电压等级(以前)
        	//判断当前插入点是否为间隔（现在）
	        String templateName = DefaultInfo.BUSBAR_NAME;
	        String name = CreationToolUtil.getDefaultName(templateName);
	        String xpath = getBusbarXPath(name);
	        if(null == xpath) {
//	        	String location = PrimaryNodeFactory.getInstance().getTargetXPath();
//	        	if(null == location)
//	        		location = ""; //$NON-NLS-1$
	        	SwingUIHelper.showWarning(templateName + Messages.getString("BusbarTool.OnlyAllowedLieIn") + DefaultInfo.BAY_NAME +  //$NON-NLS-1$
	        			Messages.getString("BusbarTool.Reselect")); //$NON-NLS-1$
	        	return;
	        }
	        getView().clearSelection();
	        Point2D.Double p = constrainPoint(viewToDrawing(anchor));
	        anchor.x = evt.getX();
	        anchor.y = evt.getY();
	        createdFigure = createFigure();
	        
	        AttributeKeys.EQUIP_NAME.set(createdFigure, name);
			AttributeKeys.EQUIP_TYPE.set(createdFigure, EnumEquipType.BAY);
			AttributeKeys.EQUIP_XPATH.set(createdFigure, xpath);
			
	        createdFigure.setBounds(p, p);
	        
	        if (createdFigure != null && createdFigure instanceof BusbarFigure) {
				BusbarFigure busbar = (BusbarFigure) createdFigure;
				if (busbar.getLabelFor() != null) {
					label = busbar.getLabel();
					label.setText(name);
				} else {
					label = createBusBarLabel(busbar);
					label.setText(name);
				}
			}
	       
	        getDrawing().add(createdFigure);
	        getDrawing().add(createdFigure.getLabel());
	       
	        EventManager.getDefault().notify(GraphEventConstant.BUSBAR_GRAPH_INSERTED, new String[]{name, templateName, xpath});
        }
    }
    
    /**
     * 创建母线标签
     * @param owner
     * @return
     */
    private BusbarLabel createBusBarLabel(BusbarFigure owner) {
		BusbarLabel label = new BusbarLabel();
		label.setOwner(owner);
		owner.setLabel(label);
		Rectangle2D.Double bound = owner.getBounds();
		Point2D.Double anchor = new Point2D.Double(bound.x + 2, bound.y + 2);
		label.setBounds(anchor, null);
		label.getBounds().height = 50;
		label.addFigureListener(owner);
		return label;
	}
    
    /**
     * 处理鼠标拖拽事件
     * @param evt
     */
    public void mouseDragged(MouseEvent evt) {
        if (createdFigure != null) {
            Point2D.Double p = constrainPoint(new Point(evt.getX(), evt.getY()));
            createdFigure.willChange();
            createdFigure.setBounds(
                    constrainPoint(new Point(anchor.x, anchor.y)),
                    p);
            if (createdFigure.getLabel() != null) {
				Rectangle2D.Double s = createdFigure.getBounds();
				createdFigure.getLabel().setBounds(
						new Point2D.Double(s.x + 2, s.y + 2), null);
			}
            createdFigure.changed();
        }
    }
    
    /**
	 * 处理鼠标按键释放事件
	 * @param evt
	 */
    public void mouseReleased(MouseEvent evt) {
        if (createdFigure != null) {
            Rectangle2D.Double bounds = createdFigure.getBounds();
            if (bounds.width == 0 && bounds.height == 0) {
                getDrawing().remove(createdFigure);
                if (isToolDoneAfterCreation()) {
                    fireToolDone();
                }
            } else {
                if (Math.abs(anchor.x - evt.getX()) < minimalSizeTreshold.width &&
                        Math.abs(anchor.y - evt.getY()) < minimalSizeTreshold.height) {
                	double scaleFactor = getView().getScaleFactor();
                    createdFigure.willChange();
                    createdFigure.setBounds(
                            constrainPoint(new Point(anchor.x, anchor.y)),
                            constrainPoint(new Point(
                            anchor.x + (int) Math.max(bounds.width, minimalSize.width * scaleFactor),
                            anchor.y + (int) Math.max(bounds.height, minimalSize.height * scaleFactor)
                            ))
                            );
                    createdFigure.changed();
                }
                getView().addToSelection(createdFigure);
                if (createdFigure instanceof CompositeFigure) {
                    ((CompositeFigure) createdFigure).layout();
                }
                final Figure addedFigure = createdFigure;
                final Drawing addedDrawing = getDrawing();
                getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
					private static final long serialVersionUID = 1L;
					public String getPresentationName() {
                        return presentationName;
                    }
                    public void undo() throws CannotUndoException {
                        super.undo();
                        addedDrawing.remove(addedFigure);
                        if(addedFigure.getLabel()!=null){
                        	addedDrawing.remove(addedFigure.getLabel());
                        }
                    }
                    public void redo() throws CannotRedoException {
                        super.redo();
                        addedDrawing.add(addedFigure);
                        if(addedFigure.getLabel()!=null){
                        	addedDrawing.add(addedFigure.getLabel());
                        }
                    }
                });
                creationFinished(createdFigure);
                createdFigure=null;
            }
        } else {
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }
    protected Figure createFigure() {
        Figure f = (Figure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : prototypeAttributes.entrySet()) {
                f.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    public Figure getCreatedFigure() {
        return createdFigure;
    }
    
    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
    	TextHolderFigure labelFor = ((BusbarFigure) createdFigure)
				.getLabelFor();
		if (labelFor != null) {
			beginEdit(labelFor);
		}
    }
    
    private String getBusbarXPath(String name) {
    	PrimaryNodeFactory factory = PrimaryNodeFactory.getInstance();
    	String targetXPath = factory.getTargetXPath();
    	if(null == targetXPath)
    		return null;
    	String equipXPath = null;
//    	if(SCL.isVoltageLevelNode(targetXPath))
    	if(SCL.isBayNode(targetXPath))
			equipXPath = targetXPath + "/ConductingEquipment[@name='" + name + "']"; //$NON-NLS-1$ //$NON-NLS-2$
		return equipXPath;
	}
    
    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    public void setToolDoneAfterCreation(boolean newValue) {
        isToolDoneAfterCreation = newValue;
    }
    
    /**
     * Returns true, if this tool fires toolDone immediately after a new
     * figure has been created.
     */
    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }
    
    public boolean isForCreationOnly() {
		return isForCreationOnly;
	}
	
	public void setForCreationOnly(boolean isForCreationOnly) {
		this.isForCreationOnly = isForCreationOnly;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		endEdit();
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
	}
	
	protected void endEdit() {
		if (typingTarget != null) {
			typingTarget.willChange();
			final String newName = textField.getText().trim();
			final String oldName = typingTarget.getText();
			if (newName.length() > 0) {
				if (null == prototype) { // 新建
					typingTarget.setText(newName);
				} else { // 修改
					String oldXPath = AttributeKeys.EQUIP_XPATH.get(prototype);
					if (!newName.equals(oldName)
							&& PrimaryDAO.hasSameName(oldXPath, newName)) {
						SwingUIHelper.showWarning(Messages
								.getString("EquipmentTool.NotAllowedSameName")); //$NON-NLS-1$
					} else {
						typingTarget.setText(newName);
						fireRenameHappened(typingTarget, oldName, newName);
					}
				}
			} else {
				if (createdFigure != null) {
					BusbarFigure barFig = (BusbarFigure) getCreatedFigure();
					BusbarLabel labelFig = barFig.getLabel();
					getDrawing().remove(barFig);
					if (labelFig != null)
						getDrawing().remove(labelFig);
				}
			}
			typingTarget.changed();
			typingTarget = null;
			textField.endOverlay();
		}
	}
	
	/**
	 * 更改编辑器是否保存状态
	 */
	private void fireRenameHappened(final TextHolderFigure typingTarget,
			final String oldName, final String newName) {
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;

			public String getPresentationName() {
				ResourceBundleUtil labels = ResourceBundleUtil
						.getLAFBundle("org.jhotdraw.draw.Labels"); //$NON-NLS-1$
				return labels.getString("rename"); //$NON-NLS-1$
			}

			public void undo() throws CannotUndoException {
				super.undo();
				typingTarget.willChange();
				typingTarget.setText(oldName);
				typingTarget.changed();
			}

			public void redo() throws CannotRedoException {
				super.redo();
				typingTarget.willChange();
				typingTarget.setText(newName);
				typingTarget.changed();
			}
		});
	}

	private Rectangle getFieldBounds(TextHolderFigure figure) {
		Rectangle box = getView().drawingToView(figure.getDrawingArea());
		Insets insets = textField.getInsets();
		return new Rectangle(box.x - insets.left, box.y - insets.top, box.width
				+ insets.left + insets.right, box.height + insets.top
				+ insets.bottom);
	}
}
