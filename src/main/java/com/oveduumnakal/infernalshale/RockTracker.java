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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

/** Remembers the live shale rocks in the scene, by the tile each one stands on. */
@Singleton
final class RockTracker
{
	/** The live rocks, keyed by tile. */
	private final Map<WorldPoint, GameObject> rocks = new HashMap<>();

	@Inject
	RockTracker()
	{
	}

	/**
	 * Records an object that appeared in the scene. Only live shale rocks are kept.
	 *
	 * @param object the object that appeared
	 */
	void onSpawned(GameObject object)
	{
		if (ShaleIds.ROCKS.contains(object.getId()))
			rocks.put(object.getWorldLocation(), object);
	}

	/**
	 * Forgets an object that left the scene. A rock is only dropped if it is the one recorded for its tile, so a
	 * newer object on the same tile is not lost.
	 *
	 * @param object the object that left
	 */
	void onDespawned(GameObject object)
	{
		rocks.remove(object.getWorldLocation(), object);
	}

	/** Forgets every rock. */
	void clear()
	{
		rocks.clear();
	}

	/**
	 * The tiles of the live rocks.
	 *
	 * @return the rock tiles
	 */
	Collection<WorldPoint> tiles()
	{
		return rocks.keySet();
	}

	/**
	 * The live rock on a tile.
	 *
	 * @param tile the tile
	 * @return the rock, or null when there is none
	 */
	GameObject get(WorldPoint tile)
	{
		return rocks.get(tile);
	}

	/**
	 * Whether no rock is known.
	 *
	 * @return true when there are no live rocks
	 */
	boolean isEmpty()
	{
		return rocks.isEmpty();
	}
}
