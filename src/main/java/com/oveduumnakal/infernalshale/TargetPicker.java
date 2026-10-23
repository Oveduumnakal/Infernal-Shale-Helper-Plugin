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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.runelite.api.coords.WorldPoint;

/**
 * Chooses which rock to mine and which tile to stand on. Static and free of client calls so it can be unit-tested
 * with plain points and a lambda for what is walkable.
 */
final class TargetPicker
{
	private TargetPicker()
	{
	}

	/**
	 * Picks the closest rock that has a walkable neighbour, and the neighbour to stand on. Rocks on another plane
	 * are ignored. Distance is squared straight-line distance; a tie goes to the smaller x, then the smaller y, so
	 * the result does not depend on the order of the rocks. The stand tile is the player's own tile when it is a
	 * walkable neighbour of the rock, otherwise the walkable neighbour nearest the player, with ties going in the
	 * order north, east, south, west.
	 *
	 * @param player the player's tile
	 * @param rocks the tiles of the live rocks
	 * @param walkable whether a tile can be stood on
	 * @return the target, or empty when there is none
	 */
	static Optional<ShaleTarget> pick(WorldPoint player, Collection<WorldPoint> rocks, Predicate<WorldPoint> walkable)
	{
		if (player == null || rocks == null)
			return Optional.empty();

		return rocks.stream()
			.filter(Objects::nonNull)
			.filter(rock -> rock.getPlane() == player.getPlane())
			.filter(rock -> !neighbours(rock, walkable).isEmpty())
			.min(Comparator.comparingInt((WorldPoint rock) -> distanceSquared(player, rock))
				.thenComparingInt(WorldPoint::getX)
				.thenComparingInt(WorldPoint::getY))
			.map(rock -> new ShaleTarget(rock, stand(player, neighbours(rock, walkable))));
	}

	/**
	 * The walkable tiles beside a rock, in the order north, east, south, west.
	 *
	 * @param rock the rock's tile
	 * @param walkable whether a tile can be stood on
	 * @return the walkable neighbours
	 */
	private static List<WorldPoint> neighbours(WorldPoint rock, Predicate<WorldPoint> walkable)
	{
		int x = rock.getX();
		int y = rock.getY();
		int plane = rock.getPlane();
		return Stream.of(
				new WorldPoint(x, y + 1, plane),
				new WorldPoint(x + 1, y, plane),
				new WorldPoint(x, y - 1, plane),
				new WorldPoint(x - 1, y, plane))
			.filter(walkable)
			.collect(Collectors.toList());
	}

	/**
	 * The tile to stand on: the player's own when it is a candidate, otherwise the candidate nearest the player,
	 * with the earliest in the list winning a tie.
	 *
	 * @param player the player's tile
	 * @param candidates the walkable neighbours of the rock, not empty
	 * @return the stand tile
	 */
	private static WorldPoint stand(WorldPoint player, List<WorldPoint> candidates)
	{
		if (candidates.contains(player))
			return player;

		return candidates.stream()
			.min(Comparator.comparingInt((WorldPoint tile) -> distanceSquared(player, tile)))
			.orElseThrow(IllegalStateException::new);
	}

	/**
	 * Squared straight-line distance between two tiles, ignoring the plane.
	 *
	 * @param from the first tile
	 * @param to the second tile
	 * @return the squared distance
	 */
	private static int distanceSquared(WorldPoint from, WorldPoint to)
	{
		int dx = from.getX() - to.getX();
		int dy = from.getY() - to.getY();
		return dx * dx + dy * dy;
	}
}
