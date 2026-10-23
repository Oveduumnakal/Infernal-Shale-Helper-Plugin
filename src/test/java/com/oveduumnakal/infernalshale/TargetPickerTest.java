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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Test;

import net.runelite.api.coords.WorldPoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests {@link TargetPicker}: which rock is chosen, which neighbouring tile is stood on, and the edge cases. */
public class TargetPickerTest
{
	/** Every tile can be stood on. */
	private static final Predicate<WorldPoint> ALL_WALKABLE = tile -> true;

	/** A tile on the ground plane. */
	private static WorldPoint point(int x, int y)
	{
		return new WorldPoint(x, y, 0);
	}

	/** The rock chosen from the given rocks, with every tile walkable. */
	private static WorldPoint rockPicked(WorldPoint player, List<WorldPoint> rocks)
	{
		Optional<ShaleTarget> target = TargetPicker.pick(player, rocks, ALL_WALKABLE);
		return target.get().getRock();
	}

	@Test
	public void picksTheNearestRock()
	{
		WorldPoint player = point(10, 10);
		WorldPoint near = point(13, 10);
		WorldPoint far = point(20, 20);

		Optional<ShaleTarget> target = TargetPicker.pick(player, Arrays.asList(far, near), ALL_WALKABLE);

		assertTrue(target.isPresent());
		assertEquals(near, target.get().getRock());
	}

	@Test
	public void skipsARockWithNoWalkableNeighbour()
	{
		WorldPoint player = point(10, 10);
		WorldPoint boxedIn = point(12, 10);
		WorldPoint open = point(20, 10);
		Predicate<WorldPoint> walkable = tile -> Math.abs(tile.getX() - boxedIn.getX()) > 1
			|| Math.abs(tile.getY() - boxedIn.getY()) > 1;

		Optional<ShaleTarget> target = TargetPicker.pick(player, Arrays.asList(boxedIn, open), walkable);

		assertTrue(target.isPresent());
		assertEquals(open, target.get().getRock());
	}

	@Test
	public void standsOnTheNeighbourNearestThePlayer()
	{
		WorldPoint player = point(0, 0);
		WorldPoint rock = point(5, 0);

		Optional<ShaleTarget> target = TargetPicker.pick(player, Collections.singletonList(rock), ALL_WALKABLE);

		assertEquals(point(4, 0), target.get().getStand());
	}

	@Test
	public void standsOnTheNearestWalkableNeighbourWhenTheNearestIsBlocked()
	{
		WorldPoint player = point(0, 0);
		WorldPoint rock = point(5, 0);
		Predicate<WorldPoint> walkable = tile -> !tile.equals(point(4, 0));

		Optional<ShaleTarget> target = TargetPicker.pick(player, Collections.singletonList(rock), walkable);

		assertEquals(point(5, 1), target.get().getStand());
	}

	@Test
	public void staysPutWhenAlreadyOnANeighbour()
	{
		WorldPoint rock = point(5, 5);
		WorldPoint player = point(6, 5);

		Optional<ShaleTarget> target = TargetPicker.pick(player, Collections.singletonList(rock), ALL_WALKABLE);

		assertEquals(player, target.get().getStand());
	}

	@Test
	public void aTieBetweenNeighboursGoesNorthFirst()
	{
		WorldPoint player = point(5, 0);
		WorldPoint rock = point(5, 5);
		Predicate<WorldPoint> walkable = tile -> tile.equals(point(5, 6)) || tile.equals(point(6, 5));

		Optional<ShaleTarget> target = TargetPicker.pick(player, Collections.singletonList(rock), walkable);

		assertEquals(point(6, 5), target.get().getStand());
	}

	@Test
	public void noRocksGivesNothing()
	{
		assertFalse(TargetPicker.pick(point(0, 0), Collections.emptyList(), ALL_WALKABLE).isPresent());
	}

	@Test
	public void aNullPlayerGivesNothing()
	{
		assertFalse(TargetPicker.pick(null, Collections.singletonList(point(1, 1)), ALL_WALKABLE).isPresent());
	}

	@Test
	public void aTieBetweenRocksGoesToTheSmallerXThenY()
	{
		WorldPoint player = point(10, 10);
		WorldPoint west = point(8, 10);
		WorldPoint east = point(12, 10);
		WorldPoint south = point(10, 8);
		WorldPoint north = point(10, 12);

		List<WorldPoint> forward = Arrays.asList(west, east, south, north);
		List<WorldPoint> backward = Arrays.asList(north, south, east, west);

		assertEquals(west, rockPicked(player, forward));
		assertEquals(west, rockPicked(player, backward));
		assertEquals(south, rockPicked(player, Arrays.asList(north, south)));
	}

	@Test
	public void rocksOnAnotherPlaneAreIgnored()
	{
		WorldPoint player = point(10, 10);
		WorldPoint upstairs = new WorldPoint(11, 10, 1);
		WorldPoint here = point(20, 20);

		Optional<ShaleTarget> target = TargetPicker.pick(player, Arrays.asList(upstairs, here), ALL_WALKABLE);

		assertEquals(here, target.get().getRock());
		assertFalse(TargetPicker.pick(player, Collections.singletonList(upstairs), ALL_WALKABLE).isPresent());
	}
}
