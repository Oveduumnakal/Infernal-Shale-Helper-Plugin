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
 * Follows the wet-cloth rhythm one game tick at a time: ready, then move, then mine, then ready again. Holds no
 * client types, so it can be unit-tested directly.
 */
@Singleton
final class WipeCycle
{
	/** The current phase. */
	private WipePhase phase = WipePhase.READY;

	/** Whether a wipe was clicked since the last tick. */
	private boolean wipePending;

	@Inject
	WipeCycle()
	{
	}

	/** Records a wipe click. The next tick starts a cycle; several clicks within one tick count once. */
	void onWipeClicked()
	{
		wipePending = true;
	}

	/**
	 * Advances one game tick. A pending wipe wins and starts the cycle over at move, even from move or mine.
	 * Otherwise move goes to mine, mine goes to ready, and ready stays ready. Move lasts exactly one tick, so a
	 * result of move means the cycle just entered it.
	 *
	 * @return the phase after this tick
	 */
	WipePhase onTick()
	{
		if (wipePending)
		{
			wipePending = false;
			phase = WipePhase.MOVE;
		}
		else if (phase == WipePhase.MOVE)
		{
			phase = WipePhase.MINE;
		}
		else if (phase == WipePhase.MINE)
		{
			phase = WipePhase.READY;
		}

		return phase;
	}

	/**
	 * The current phase.
	 *
	 * @return the phase
	 */
	WipePhase phase()
	{
		return phase;
	}

	/** Returns to ready and forgets any pending wipe. */
	void reset()
	{
		phase = WipePhase.READY;
		wipePending = false;
	}
}
