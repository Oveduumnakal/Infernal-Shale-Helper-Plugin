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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests {@link CrushGate}: when crush mode turns on, how long it stays on, and when it turns off. */
public class CrushGateTest
{
	@Test
	public void startsOff()
	{
		assertFalse(new CrushGate().isCrushing());
	}

	@Test
	public void turnsOnAtTheThreshold()
	{
		CrushGate gate = new CrushGate();

		assertTrue(gate.update(9, 9));
		assertTrue(gate.isCrushing());
	}

	@Test
	public void turnsOnAboveTheThreshold()
	{
		assertTrue(new CrushGate().update(20, 9));
	}

	@Test
	public void staysOffBelowTheThreshold()
	{
		CrushGate gate = new CrushGate();

		assertFalse(gate.update(8, 9));
		assertFalse(gate.update(1, 9));
		assertFalse(gate.isCrushing());
	}

	@Test
	public void staysOnWhileCrushingDown()
	{
		CrushGate gate = new CrushGate();
		gate.update(9, 9);

		for (int count = 8; count >= 1; count--)
			assertTrue("still crushing at " + count, gate.update(count, 9));
	}

	@Test
	public void turnsOffAtZero()
	{
		CrushGate gate = new CrushGate();
		gate.update(9, 9);
		gate.update(1, 9);

		assertFalse(gate.update(0, 9));
		assertFalse(gate.isCrushing());
	}

	@Test
	public void staysOffAfterTurningOffUntilTheThresholdIsReachedAgain()
	{
		CrushGate gate = new CrushGate();
		gate.update(9, 9);
		gate.update(0, 9);

		assertFalse(gate.update(5, 9));
		assertTrue(gate.update(9, 9));
	}

	@Test
	public void aLowerThresholdTurnsItOnSooner()
	{
		CrushGate gate = new CrushGate();

		assertFalse(gate.update(5, 9));
		assertTrue(gate.update(5, 5));
	}

	@Test
	public void aHigherThresholdDoesNotTurnItOffWhileOn()
	{
		CrushGate gate = new CrushGate();
		gate.update(9, 9);

		assertTrue(gate.update(9, 20));
	}

	@Test
	public void zeroShaleIsOffEvenWithAThresholdOfZero()
	{
		assertFalse(new CrushGate().update(0, 0));
	}

	@Test
	public void resetTurnsItOff()
	{
		CrushGate gate = new CrushGate();
		gate.update(9, 9);
		gate.reset();

		assertFalse(gate.isCrushing());
		assertFalse(gate.update(5, 9));
	}
}
