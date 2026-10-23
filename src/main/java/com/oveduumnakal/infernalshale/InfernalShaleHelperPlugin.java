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

import com.google.inject.Provides;

import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

/** Entry point: highlights each step of the wet-cloth tick rhythm for mining infernal shale. */
@PluginDescriptor(
	name = "Infernal Shale Helper",
	description = "Highlights each step of the wet-cloth tick rhythm for mining infernal shale: cloth, then tile, "
		+ "then rock",
	tags = {"infernal", "shale", "mining", "tick", "3tick", "chasm", "fire", "cloth", "overlay"}
)
public class InfernalShaleHelperPlugin extends Plugin
{
	/** Every collision flag that stops a player standing on a tile. */
	private static final int BLOCKED = CollisionDataFlag.BLOCK_MOVEMENT_FULL
		| CollisionDataFlag.BLOCK_MOVEMENT_OBJECT
		| CollisionDataFlag.BLOCK_MOVEMENT_FLOOR
		| CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfernalShaleHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WipeCycle cycle;

	@Inject
	private RockTracker rockTracker;

	@Inject
	private CrushGate crushGate;

	@Inject
	private ShaleState state;

	@Inject
	private TickClock tickClock;

	@Inject
	private InventoryHighlightOverlay inventoryOverlay;

	@Inject
	private ShaleSceneOverlay sceneOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(inventoryOverlay);
		overlayManager.add(sceneOverlay);
		clientThread.invokeLater(this::restoreScene);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(inventoryOverlay);
		overlayManager.remove(sceneOverlay);
		rockTracker.clear();
		cycle.reset();
		crushGate.reset();
		state.reset();
	}

	/**
	 * Reads the backpack, decides whether to crush, and otherwise moves the wipe cycle on by one tick. The rock is
	 * only kept as a target for the mine tick when the player clicked the stand tile on the move tick.
	 */
	@Subscribe
	public void onGameTick(GameTick event)
	{
		tickClock.onTick();
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
		{
			stopCycle();
			return;
		}

		boolean crushing = crushGate.update(inventory.count(ShaleIds.INFERNAL_SHALE), config.crushAt());
		state.setCrushing(crushing);
		if (crushing || !inventory.contains(ShaleIds.WET_CLOTH) || rockTracker.isEmpty())
		{
			stopCycle();
			return;
		}

		WipePhase phase = cycle.onTick();
		if (phase == WipePhase.MOVE)
			state.setTarget(pickTarget());
		else if (phase == WipePhase.READY || (phase == WipePhase.MINE && !cycle.stepTaken()))
			state.setTarget(null);

		state.activate(phase);
	}

	/**
	 * Notes whether a ground click lands on the stand tile, and starts a cycle when the cloth is wiped, unless the
	 * backpack is full enough to crush.
	 */
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() == MenuAction.WALK)
		{
			cycle.onWalkClicked(clickedStandTile());
			return;
		}

		if (crushGate.isCrushing() || event.getItemId() != ShaleIds.WET_CLOTH)
			return;

		if (Text.removeTags(event.getMenuOption()).equalsIgnoreCase(ShaleIds.WIPE_OPTION))
			cycle.onWipeClicked();
	}

	/** Forgets the rocks and the cycle when the scene unloads or the player leaves the world. */
	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		if (gameState == GameState.LOADING || gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING)
		{
			rockTracker.clear();
			stopCycle();
		}
	}

	/** Records a rock that appeared. */
	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		rockTracker.onSpawned(event.getGameObject());
	}

	/** Forgets a rock that left the scene. */
	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		rockTracker.onDespawned(event.getGameObject());
	}

	@Provides
	InfernalShaleHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InfernalShaleHelperConfig.class);
	}

	/** Returns the cycle to ready and clears the target, for when the helper has nothing to point at. */
	private void stopCycle()
	{
		cycle.reset();
		state.deactivate();
	}

	/** Whether the ground tile under the mouse is the stand tile the helper is pointing at. */
	private boolean clickedStandTile()
	{
		WorldView view = client.getTopLevelWorldView();
		Tile tile = view == null ? null : view.getSelectedSceneTile();
		ShaleTarget target = state.target();
		return tile != null && target != null && tile.getWorldLocation().equals(target.getStand());
	}

	/** Picks the rock and stand tile nearest the player, or null when there is none. */
	private ShaleTarget pickTarget()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
			return null;

		return TargetPicker.pick(player.getWorldLocation(), rockTracker.tiles(), this::isWalkable).orElse(null);
	}

	/** Whether a tile is in the scene and nothing on it blocks standing there. */
	private boolean isWalkable(WorldPoint point)
	{
		WorldView view = client.getTopLevelWorldView();
		LocalPoint local = view == null ? null : LocalPoint.fromWorld(view, point);
		CollisionData[] maps = view == null ? null : view.getCollisionMaps();
		if (local == null || maps == null || point.getPlane() >= maps.length || maps[point.getPlane()] == null)
			return false;

		int flags = maps[point.getPlane()].getFlags()[local.getSceneX()][local.getSceneY()];
		return (flags & BLOCKED) == 0;
	}

	/** Feeds the rocks already in the scene to the tracker, for when the plugin starts after login. */
	private void restoreScene()
	{
		WorldView view = client.getTopLevelWorldView();
		if (client.getGameState() != GameState.LOGGED_IN || view == null)
			return;

		Scene scene = view.getScene();
		if (scene == null)
			return;

		for (Tile[][] plane : scene.getTiles())
		{
			if (plane == null)
				continue;

			for (Tile[] column : plane)
				trackColumn(column);
		}
	}

	/** Feeds the rocks on a column of scene tiles to the tracker. */
	private void trackColumn(Tile[] column)
	{
		if (column == null)
			return;

		for (Tile tile : column)
		{
			if (tile == null)
				continue;

			for (GameObject object : tile.getGameObjects())
			{
				if (object != null)
					rockTracker.onSpawned(object);
			}
		}
	}
}
