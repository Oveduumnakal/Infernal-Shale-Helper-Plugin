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

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

/** Settings for Infernal Shale Helper. */
@ConfigGroup(InfernalShaleHelperConfig.GROUP)
public interface InfernalShaleHelperConfig extends Config
{
	/** Config group key, shared with the plugin so both agree on where settings are stored. */
	String GROUP = "infernalshalehelper";

	@Alpha
	@ConfigItem(
		keyName = "clothColor",
		name = "Cloth",
		description = "Color of the wet cloth when it is time to wipe.",
		position = 0
	)
	default Color clothColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		keyName = "tileColor",
		name = "Stand tile",
		description = "Color of the tile to stand on after wiping.",
		position = 1
	)
	default Color tileColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		keyName = "rockColor",
		name = "Rock",
		description = "Color of the rock to mine.",
		position = 2
	)
	default Color rockColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "chiselColor",
		name = "Chisel",
		description = "Color of the chisel when it is time to crush.",
		position = 3
	)
	default Color chiselColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		keyName = "highlightMode",
		name = "Highlight style",
		description = "Whether highlights are drawn as an outline, a fill or both.",
		position = 4
	)
	default HighlightMode highlightMode()
	{
		return HighlightMode.OUTLINE;
	}

	@Range(min = 1, max = 27)
	@ConfigItem(
		keyName = "crushAt",
		name = "Crush at",
		description = "Infernal shale count that switches the highlight from the cloth to the chisel.",
		position = 5
	)
	default int crushAt()
	{
		return 9;
	}
}
