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

import java.util.Set;

import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

/** The item, object and menu text the plugin recognises. */
final class ShaleIds
{
	/** Jim's wet cloth, the item that is wiped to start each cycle. */
	static final int WET_CLOTH = ItemID.WET_CLOTH;

	/** Infernal shale as mined, the item that is counted to decide when to crush. */
	static final int INFERNAL_SHALE = ItemID.INFERNAL_SHALE;

	/** The chisel and the jeweller's chisel, either of which crushes shale. */
	static final Set<Integer> CHISELS = Set.of(ItemID.CHISEL, ItemID.JEWELLERS_CHISEL);

	/** The two visual variants of a live shale rock; a mined rock is replaced by a different object. */
	static final Set<Integer> ROCKS = Set.of(ObjectID.CRIMSON_LOVAKITE1, ObjectID.CRIMSON_LOVAKITE2);

	/** The menu option on the cloth that starts a cycle. */
	static final String WIPE_OPTION = "Wipe";

	private ShaleIds()
	{
	}
}
