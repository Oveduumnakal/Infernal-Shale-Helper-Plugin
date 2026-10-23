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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/** Draws the tile to stand on after a wipe, then the rock to mine. */
class ShaleSceneOverlay extends Overlay
{
	private final Client client;
	private final InfernalShaleHelperConfig config;
	private final ShaleState state;
	private final RockTracker rocks;
	private final TickClock tickClock;

	@Inject
	ShaleSceneOverlay(Client client, InfernalShaleHelperConfig config, ShaleState state, RockTracker rocks,
		TickClock tickClock)
	{
		this.client = client;
		this.config = config;
		this.state = state;
		this.rocks = rocks;
		this.tickClock = tickClock;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ShaleTarget target = state.target();
		if (!state.isActive() || state.isCrushing() || target == null)
			return null;

		graphics.setFont(FontManager.getRunescapeBoldFont());
		if (state.phase() == WipePhase.MOVE)
			drawTile(graphics, target);
		else if (state.phase() == WipePhase.MINE)
			drawRock(graphics, target);

		return null;
	}

	/** Outlines the tile to stand on, with the tick countdown on it. */
	private void drawTile(Graphics2D graphics, ShaleTarget target)
	{
		WorldView view = client.getTopLevelWorldView();
		LocalPoint local = view == null ? null : LocalPoint.fromWorld(view, target.getStand());
		if (local == null)
			return;

		Polygon tile = Perspective.getCanvasTilePoly(client, local);
		if (tile != null)
			HighlightDrawing.draw(graphics, tile, config.tileColor(), config.highlightMode());

		if (!config.showCountdown())
			return;

		String text = tickClock.countdown();
		Point location = Perspective.getCanvasTextLocation(client, graphics, local, text, 0);
		HighlightDrawing.text(graphics, text, location, config.tileColor());
	}

	/** Outlines the locked rock with the tick countdown on it, or nothing when it has despawned. */
	private void drawRock(Graphics2D graphics, ShaleTarget target)
	{
		GameObject rock = rocks.get(target.getRock());
		if (rock == null)
			return;

		Color color = config.rockColor();
		Shape shape = rock.getClickbox() != null ? rock.getClickbox() : rock.getConvexHull();
		if (shape != null)
			HighlightDrawing.draw(graphics, shape, color, config.highlightMode());

		if (!config.showCountdown())
			return;

		String text = tickClock.countdown();
		HighlightDrawing.text(graphics, text, rock.getCanvasTextLocation(graphics, text, 0), color);
	}
}
