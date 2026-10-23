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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Tests {@link WipeCycle}: the ready, move, mine rhythm and how a wipe click starts or restarts it. */
public class WipeCycleTest
{
	@Test
	public void startsReady()
	{
		assertEquals(WipePhase.READY, new WipeCycle().phase());
	}

	@Test
	public void runsReadyMoveMineReady()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();

		assertEquals(WipePhase.MOVE, cycle.onTick());
		assertEquals(WipePhase.MINE, cycle.onTick());
		assertEquals(WipePhase.READY, cycle.onTick());
		assertEquals(WipePhase.READY, cycle.phase());
	}

	@Test
	public void aTickWithNoWipeStaysReady()
	{
		WipeCycle cycle = new WipeCycle();

		assertEquals(WipePhase.READY, cycle.onTick());
		assertEquals(WipePhase.READY, cycle.onTick());
	}

	@Test
	public void aWipeDuringMoveRestartsAtMove()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();
		cycle.onTick();
		cycle.onWipeClicked();

		assertEquals(WipePhase.MOVE, cycle.onTick());
		assertEquals(WipePhase.MINE, cycle.onTick());
	}

	@Test
	public void aWipeDuringMineRestartsAtMove()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();
		cycle.onTick();
		cycle.onTick();
		cycle.onWipeClicked();

		assertEquals(WipePhase.MOVE, cycle.onTick());
		assertEquals(WipePhase.MINE, cycle.onTick());
	}

	@Test
	public void resetReturnsToReady()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();
		cycle.onTick();
		cycle.reset();

		assertEquals(WipePhase.READY, cycle.phase());
		assertEquals(WipePhase.READY, cycle.onTick());
	}

	@Test
	public void resetDropsAPendingWipe()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();
		cycle.reset();

		assertEquals(WipePhase.READY, cycle.onTick());
	}

	@Test
	public void twoWipesInOneTickActAsOne()
	{
		WipeCycle cycle = new WipeCycle();
		cycle.onWipeClicked();
		cycle.onWipeClicked();

		assertEquals(WipePhase.MOVE, cycle.onTick());
		assertEquals(WipePhase.MINE, cycle.onTick());
		assertEquals(WipePhase.READY, cycle.onTick());
	}
}
