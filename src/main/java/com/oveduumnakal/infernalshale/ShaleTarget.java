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

import java.util.Objects;

import net.runelite.api.coords.WorldPoint;

/** A rock to mine and the tile to stand on while mining it. Immutable. */
final class ShaleTarget
{
	/** The rock's tile. */
	private final WorldPoint rock;

	/** The tile next to the rock to stand on. */
	private final WorldPoint stand;

	/**
	 * Creates a target.
	 *
	 * @param rock the rock's tile
	 * @param stand the tile next to the rock to stand on
	 */
	ShaleTarget(WorldPoint rock, WorldPoint stand)
	{
		this.rock = rock;
		this.stand = stand;
	}

	/**
	 * The rock's tile.
	 *
	 * @return the rock
	 */
	WorldPoint getRock()
	{
		return rock;
	}

	/**
	 * The tile to stand on.
	 *
	 * @return the stand tile
	 */
	WorldPoint getStand()
	{
		return stand;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof ShaleTarget))
			return false;

		ShaleTarget that = (ShaleTarget) other;
		return Objects.equals(rock, that.rock) && Objects.equals(stand, that.stand);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(rock, stand);
	}

	@Override
	public String toString()
	{
		return "ShaleTarget{rock=" + rock + ", stand=" + stand + "}";
	}
}
