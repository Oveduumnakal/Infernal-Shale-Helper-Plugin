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

import java.util.Locale;
import java.util.function.LongSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Tells how much of the current game tick is left, so a countdown can move smoothly between ticks instead of jumping
 * every 0.6 seconds. The plugin marks each tick as it arrives; the overlay reads the time left on every frame. A tick
 * that arrives late holds the countdown at zero rather than letting it run past.
 */
@Singleton
class TickClock
{
	/** Length of one game tick in milliseconds. */
	static final int TICK_MILLIS = 600;

	/** Where the current time is read from. */
	private final LongSupplier clock;

	/** When the last tick began, in the clock's milliseconds. */
	private long lastTickMillis;

	@Inject
	TickClock()
	{
		this(System::currentTimeMillis);
	}

	/**
	 * Creates a tick clock reading time from the given source.
	 *
	 * @param clock the current time in milliseconds
	 */
	TickClock(LongSupplier clock)
	{
		this.clock = clock;
		this.lastTickMillis = clock.getAsLong();
	}

	/** Marks the start of a new game tick. */
	void onTick()
	{
		lastTickMillis = clock.getAsLong();
	}

	/**
	 * Milliseconds since the last tick began.
	 *
	 * @return 0 to {@link #TICK_MILLIS}; a late tick does not push it past a tick's length
	 */
	long millisSinceTick()
	{
		long elapsed = clock.getAsLong() - lastTickMillis;
		return Math.max(0, Math.min(TICK_MILLIS, elapsed));
	}

	/**
	 * The time left in the current tick, in seconds to the hundredth.
	 *
	 * @return text such as {@code 0.60}, {@code 0.59} or {@code 0.00}
	 */
	String countdown()
	{
		return countdown(millisSinceTick());
	}

	/**
	 * The time left in a tick as seconds to the hundredth, rounded up so a fresh tick reads 0.60 and only a finished
	 * tick reads 0.00.
	 *
	 * @param millisSinceTick milliseconds since the tick began, 0 to {@link #TICK_MILLIS}
	 * @return text such as {@code 0.60}, {@code 0.59} or {@code 0.00}
	 */
	static String countdown(long millisSinceTick)
	{
		long hundredths = (TICK_MILLIS - millisSinceTick + 9) / 10;
		return String.format(Locale.ROOT, "%d.%02d", hundredths / 100, hundredths % 100);
	}
}
