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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.Point;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

/**
 * Highlights the wet cloth when it is time to wipe, and the chisel when it is time to crush. After a wipe click, the
 * cloth shows the time left until the tick that lights the stand tile.
 */
class InventoryHighlightOverlay extends WidgetItemOverlay
{
	private final InfernalShaleHelperConfig config;
	private final ShaleState state;
	private final ItemManager itemManager;
	private final WipeCycle cycle;
	private final TickClock tickClock;

	@Inject
	InventoryHighlightOverlay(InfernalShaleHelperConfig config, ShaleState state, ItemManager itemManager,
		WipeCycle cycle, TickClock tickClock)
	{
		this.config = config;
		this.state = state;
		this.itemManager = itemManager;
		this.cycle = cycle;
		this.tickClock = tickClock;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		Rectangle bounds = widgetItem.getCanvasBounds();
		Color color = colorFor(itemId);
		if (color != null)
			highlight(graphics, itemId, widgetItem.getQuantity(), bounds, color);

		if (showsCountdown(itemId))
			drawCountdown(graphics, bounds);
	}

	/** Draws an item's highlight in the chosen style. */
	private void highlight(Graphics2D graphics, int itemId, int quantity, Rectangle bounds, Color color)
	{
		HighlightMode mode = config.highlightMode();
		if (mode != HighlightMode.FILL)
		{
			BufferedImage outline = itemManager.getItemOutline(itemId, quantity, color);
			graphics.drawImage(outline, bounds.x, bounds.y, null);
		}

		if (mode != HighlightMode.OUTLINE)
			HighlightDrawing.fill(graphics, bounds, color);
	}

	/** Whether an item is the cloth, wiped since the last tick, so it shows the countdown. */
	private boolean showsCountdown(int itemId)
	{
		return config.showCountdown()
			&& itemId == ShaleIds.WET_CLOTH
			&& state.isActive()
			&& !state.isCrushing()
			&& cycle.wipePending();
	}

	/** Writes the tick countdown centred on an inventory slot. */
	private void drawCountdown(Graphics2D graphics, Rectangle bounds)
	{
		graphics.setFont(FontManager.getRunescapeBoldFont());
		String text = tickClock.countdown();
		FontMetrics metrics = graphics.getFontMetrics();
		int x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
		int y = bounds.y + (bounds.height + metrics.getAscent() - metrics.getDescent()) / 2;
		HighlightDrawing.text(graphics, text, new Point(x, y), config.clothColor());
	}

	/** The color to highlight an item in, or null when it is not highlighted now. */
	private Color colorFor(int itemId)
	{
		if (state.isCrushing())
			return ShaleIds.CHISELS.contains(itemId) ? config.chiselColor() : null;

		boolean wipeNow = state.isActive() && state.phase() == WipePhase.READY;
		return wipeNow && itemId == ShaleIds.WET_CLOTH ? config.clothColor() : null;
	}
}
