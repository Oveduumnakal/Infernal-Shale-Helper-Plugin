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

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Decides when to crush. Crush mode turns on once the backpack holds the threshold of shale and stays on until the
 * backpack holds none, so crushing one of nine does not switch it off. Holds no client types.
 */
@Singleton
final class CrushGate
{
	/** Whether crush mode is on. */
	private boolean crushing;

	@Inject
	CrushGate()
	{
	}

	/**
	 * Updates crush mode from the current shale count. Zero shale turns it off, even when the threshold is zero or
	 * less; otherwise reaching the threshold turns it on; any other count leaves it as it was.
	 *
	 * @param shaleCount how much infernal shale the backpack holds
	 * @param threshold the count at which crush mode turns on
	 * @return whether crush mode is on after the update
	 */
	boolean update(int shaleCount, int threshold)
	{
		if (shaleCount == 0)
			crushing = false;
		else if (shaleCount >= threshold)
			crushing = true;

		return crushing;
	}

	/**
	 * Whether crush mode is on.
	 *
	 * @return true while crushing
	 */
	boolean isCrushing()
	{
		return crushing;
	}

	/** Turns crush mode off. */
	void reset()
	{
		crushing = false;
	}
}
