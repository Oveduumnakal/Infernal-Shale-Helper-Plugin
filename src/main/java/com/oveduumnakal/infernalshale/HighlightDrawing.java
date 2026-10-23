/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oveduumnakal.infernalshale;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/** Draws a highlight in the chosen style. Shared by the scene and inventory overlays. */
final class HighlightDrawing
{
	/** Width of an outline stroke. */
	private static final float STROKE_WIDTH = 2f;

	/** The fill's alpha is the color's alpha divided by this. */
	private static final int FILL_ALPHA_DIVISOR = 3;

	private HighlightDrawing()
	{
	}

	/**
	 * Draws a shape as an outline, a fill or both.
	 *
	 * @param graphics the graphics to draw on
	 * @param shape the shape to highlight
	 * @param color the highlight color
	 * @param mode how to draw it
	 */
	static void draw(Graphics2D graphics, Shape shape, Color color, HighlightMode mode)
	{
		if (mode != HighlightMode.FILL)
			outline(graphics, shape, color);

		if (mode != HighlightMode.OUTLINE)
			fill(graphics, shape, color);
	}

	/**
	 * Draws the edge of a shape.
	 *
	 * @param graphics the graphics to draw on
	 * @param shape the shape to outline
	 * @param color the stroke color
	 */
	static void outline(Graphics2D graphics, Shape shape, Color color)
	{
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(STROKE_WIDTH));
		graphics.draw(shape);
	}

	/**
	 * Fills a shape with the color at reduced alpha.
	 *
	 * @param graphics the graphics to draw on
	 * @param shape the shape to fill
	 * @param color the fill color
	 */
	static void fill(Graphics2D graphics, Shape shape, Color color)
	{
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
			color.getAlpha() / FILL_ALPHA_DIVISOR));
		graphics.fill(shape);
	}
}
