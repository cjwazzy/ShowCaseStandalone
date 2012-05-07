package com.miykeal.showCaseStandalone.Listeners;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
* Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
public class ShowCaseStandaloneWorldListener implements Listener {
	private ShowCaseStandalone scs;
	
	public ShowCaseStandaloneWorldListener (ShowCaseStandalone scs) {
		this.scs	= scs;
                scs.getServer().getPluginManager().registerEvents(this, scs);
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event) {
		scs.getShopHandler().loadChunk(event.getChunk());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event) {
		scs.getShopHandler().unloadChunk(event.getChunk());
	}
}
