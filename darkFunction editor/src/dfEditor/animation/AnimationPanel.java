/*
 *  Copyright 2012 Samuel Taylor
 *
 *  This file is part of darkFunction Editor
 *
 *  darkFunction Editor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  darkFunction Editor is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with darkFunction Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package dfEditor.animation;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dfEditor.GraphicObject;
import dfEditor.GraphicPanel;
import dfEditor.MathUtil;
import dfEditor.NodeDroppedListener;
import dfEditor.SpriteGraphic;

/**
 *
 * @author s4m20
 */
public class AnimationPanel extends GraphicPanel implements DropTargetListener
{
    private boolean bFirstDraw = true;
    private ArrayList<NodeDroppedListener> nodeDroppedListeners = new ArrayList<NodeDroppedListener>();
    private AnimationCell[] onionSkins = null;
    private boolean hitboxMode = false;
    private BufferedImage backgroundImage = null;
    private Animation currentAnimation;

    public AnimationPanel()
    {
        super();

        final Point origin = _origin;
        final AnimationPanel self = this;
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
			public void run()
            {
                Dimension size = self.getSize();
                origin.x = size.width/2;
                origin.y = size.height/2;
                repaint();
            }
        });
    }

    @Override
    protected void draw(Graphics g)
    {
        // bit messy.. code in init doesn't seem to work every time (when not first tab?)
        if (bFirstDraw)
        {
            bFirstDraw = false;
            Dimension size = this.getSize();
            _origin.x = size.width/2;
            _origin.y = size.height/2;
        }
        ////////////////
        final Dimension originPointerSize = new Dimension(26, 3);
        
        super.draw(g);
                
        if (backgroundImage != null) {
        	int effectiveImageHeight = (int) (backgroundImage.getHeight() * _zoom);
        	
        	g.drawImage(backgroundImage, 
        			getOrigin().x, getOrigin().y - effectiveImageHeight, 
        				(int) (backgroundImage.getWidth() * _zoom), 
        				effectiveImageHeight, this);
        }
        
        drawOriginLines(g);
        drawOriginOffscreenIndicator(g, originPointerSize);
        drawOnionSkins(g);
    }


    private void drawOriginLines(Graphics g)
    {
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, _origin.y, getSize().width, _origin.y);
        g.drawLine(_origin.x, 0, _origin.x, getSize().height);

        g.setColor(Color.LIGHT_GRAY);
        for (int i = -1200; i < 1200; i += 50)
        {
        	if (i == 0)
        		continue;

        	g.drawLine((int) (i * _zoom) + _origin.x, 0, (int) (i * _zoom) + _origin.x, getSize().height);
        	g.drawLine(0, (int) (i * _zoom) + _origin.y, getSize().width, (int) (i * _zoom) + _origin.y);
        }

        g.setColor(Color.RED);
        g.drawRect(_origin.x, _origin.y - (int)(96 * _zoom), (int)(_zoom * 256), (int)(_zoom * 96));

        g.drawRect(_origin.x + (int)(52 * _zoom), _origin.y - (int)(46 * _zoom), (int)(40 * _zoom), (int)(40 * _zoom));
        g.drawRect(_origin.x + (int)(164 * _zoom), _origin.y - (int)(32 * _zoom), (int)(40 * _zoom), (int)(40 * _zoom));

        g.setColor(Color.GREEN);
        g.drawLine(_origin.x + (int)(164 * _zoom), _origin.y, _origin.x + (int)(204 * _zoom), _origin.y);
        g.drawRect(_origin.x - (int)(25 * _zoom), _origin.y - (int)(60 * _zoom), (int)(50 * _zoom), (int)(50 * _zoom));

        g.setColor(Color.BLUE);
        g.drawRect(_origin.x - (int)(12 * _zoom), _origin.y - (int)(12 * _zoom), (int)(24 * _zoom), (int)(24 * _zoom));
    }

    private void drawOriginOffscreenIndicator(Graphics g, Dimension aSize)
    {
        g.setColor(Color.RED);

        int x = _origin.x;
        if (x < 0)
            x = 0;
        else if (x >= getSize().width)
            x = getSize().width - aSize.height;

        int y = _origin.y;
        if (y < 0)
            y = 0;
        else if (y >= getSize().height)
            y = getSize().height - aSize.height;

        if (_origin.y <= 0 || _origin.y >= getSize().height)
        {
            g.fillRect(x - (aSize.width/2), y, aSize.width, aSize.height);
        }

        if (_origin.x <= 0 || _origin.x >= getSize().width)
        {
            g.fillRect(x, y - (aSize.width/2), aSize.height, aSize.width);
        }
    }

    public void setCell(AnimationCell aCell)
    {
        if (aCell != null)
        {
            setDrawStack(aCell.getGraphicList());
        }
        else
            clear();

        //populateFromCell(aCell);

        repaint();
    }

    protected Point convertViewPointToActualPoint(Point aViewPoint)
    {
        return new Point (
                    (int)((aViewPoint.x  - _origin.x) / _zoom),
                    (int)((aViewPoint.y  - _origin.y) / _zoom));
    }

    @Override
    protected GraphicObject topGraphicAtPosition(Point aPoint)
    {
        GraphicObject actualTopGraphic = null;

        for (int i=0; i<_drawStack.size(); ++i)
        {
            SpriteGraphic graphic = (SpriteGraphic)_drawStack.get(i);
            Rectangle r = graphic.getRect();
            Point ap = convertViewPointToActualPoint(aPoint);

            // pixel perfect collision //
            // check as if nothing is rotated (rotate point in opp. dir to compensate)
            // so..
            // translate to rect's local space
            ap.x -= r.x+r.width/2;
            ap.y -= r.y+r.height/2;

            // rotate point in opposite direction to rect
            ap = MathUtil.rotatePoint2D(ap, -graphic.getAngle());

            // and translate back
            ap.x += r.x+r.width/2;
            ap.y += r.y+r.height/2;

            if (MathUtil.pointRectCollide(ap, r))
            {
                if (actualTopGraphic == null)
                {
                    actualTopGraphic = graphic;
                }

                Point pointOnGraphic = new Point(ap.x - r.x, ap.y - r.y);

                if (graphic.isFlippedH())
                    pointOnGraphic.x = r.width - pointOnGraphic.x;
                if (graphic.isFlippedV())
                    pointOnGraphic.y = r.height - pointOnGraphic.y;

                if (!graphic.pixelIsTransparent(pointOnGraphic.x, pointOnGraphic.y))
                {
                    return graphic;
                }
            }
        }

        return actualTopGraphic;
    }

    public void setOnionSkins(final AnimationCell[] aCells)
    {
        onionSkins = aCells;
    }

    private void drawOnionSkins(Graphics g)
    {
        if (onionSkins == null)
            return;

        float alpha = 1.0f;
        for (int i=0; i<onionSkins.length; ++i)
        {
            alpha /= 3.0f;
            AnimationCell cell = onionSkins[i];
            ArrayList<GraphicObject> graphics = cell.getGraphicList();

            for (int j=0; j<graphics.size(); ++j)
            {
                GraphicObject graphic = graphics.get(j);

                this.drawGraphicRotated(graphic, g, _origin, _zoom, alpha);
            }
        }
    }

    public void addNodeDroppedListener(NodeDroppedListener aListener)
    {
        nodeDroppedListeners.add(aListener);
    }

    public void setDragSource(Component aDragSource)
    {
        new DropTarget(this, this);

    }

    public void dragGestureRecognized(DragGestureEvent dge)
    {

    }

    @Override
	public void drop(DropTargetDropEvent dtde)
    {
        Transferable transferable = dtde.getTransferable();

        //flavor not supported, reject drop
        //DataFlavor dataFlavor =  new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=dfEditor.GraphicObject");
        if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            dtde.rejectDrop();
            return;
        }

        if (dtde.getDropAction() == DnDConstants.ACTION_COPY)
        {
            try {
                String data = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                Point mousePos = this.getMousePosition();
                Rectangle dummy = new Rectangle(mousePos.x, mousePos.y, 1, 1);
                Rectangle result = this.convertViewRectToRect(dummy);
                Point p = new Point(result.x, result.y);
                for (int i=0; i<nodeDroppedListeners.size(); ++i)
                {
                    nodeDroppedListeners.get(i).nodeDropped(this, data, p);
                }
            } catch (Exception e) {}
        }
    }

    @Override
	public void dragEnter(DropTargetDragEvent dtde)
    {

    }

    @Override
	public void dragOver(DropTargetDragEvent dtde)
    {

    }

    @Override
	public void dropActionChanged(DropTargetDragEvent dtde)
    {
    }

    @Override
	public void dragExit(DropTargetEvent dte)
    {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {}

    public void hitboxToggle(boolean toggle)
    {
    	hitboxMode = toggle;
    }
    
    public void setBackgroundImage(BufferedImage bim) {
    	this.backgroundImage = bim;
    	this.repaint();
    }

	@Override
	protected void arrowPressedWithNothingSelected(int keyDeltaX, int keyDeltaY) {
		for (int animIndex = 0; animIndex < currentAnimation.numCells(); animIndex++) {
			AnimationCell cell = currentAnimation.getCellAtIndex(animIndex);
			
			for (GraphicObject graphic : cell.getGraphicList())
			{
			    Rectangle r = graphic.getRect(); 
			    r.x += keyDeltaX;
			    r.y += keyDeltaY;
			}
		}
	}

	public void setCurrentAnimation(Animation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}
}
