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

/** What the overlays draw: written by the plugin each tick, read by the overlays. Holds no client types. */
@Singleton
final class ShaleState
{
	/** Whether crush mode is on. */
	private boolean crushing;

	/** Whether the cloth cycle is running: the cloth is held and a rock is in the scene. */
	private boolean active;

	/** The current phase of the cycle. */
	private WipePhase phase = WipePhase.READY;

	/** The rock and stand tile picked at the start of the cycle, or null. */
	private ShaleTarget target;

	@Inject
	ShaleState()
	{
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

	/**
	 * Whether the cloth cycle is running.
	 *
	 * @return true when the cloth is held and a rock is in the scene
	 */
	boolean isActive()
	{
		return active;
	}

	/**
	 * The current phase of the cycle.
	 *
	 * @return the phase
	 */
	WipePhase phase()
	{
		return phase;
	}

	/**
	 * The locked target.
	 *
	 * @return the target, or null when none is picked
	 */
	ShaleTarget target()
	{
		return target;
	}

	/**
	 * Sets whether crush mode is on.
	 *
	 * @param crushing true while crushing
	 */
	void setCrushing(boolean crushing)
	{
		this.crushing = crushing;
	}

	/**
	 * Marks the cycle as running in a phase. The target is left as it was.
	 *
	 * @param phase the phase after this tick
	 */
	void activate(WipePhase phase)
	{
		this.active = true;
		this.phase = phase;
	}

	/**
	 * Locks the target for the rest of the cycle.
	 *
	 * @param target the target, or null for none
	 */
	void setTarget(ShaleTarget target)
	{
		this.target = target;
	}

	/** Stops the cycle and clears the target. Crush mode is left as it was. */
	void deactivate()
	{
		active = false;
		phase = WipePhase.READY;
		target = null;
	}

	/** Clears everything, crush mode included. */
	void reset()
	{
		crushing = false;
		deactivate();
	}
}
