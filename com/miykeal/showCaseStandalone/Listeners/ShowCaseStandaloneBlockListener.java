package com.miykeal.showCaseStandalone.Listeners;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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

public class ShowCaseStandaloneBlockListener implements Listener{
	private ShowCaseStandalone 	scs;

	public ShowCaseStandaloneBlockListener(ShowCaseStandalone scs) {
		this.scs 		 = scs;
                Bukkit.getServer().getPluginManager().registerEvents(this, scs);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace (BlockPlaceEvent e) {
		if ( scs.getShopHandler().isShopBlock(e.getBlock()) )
                    e.setCancelled(true);
		else if ( scs.getShopHandler().isShopBlock(e.getBlockPlaced().getLocation().subtract(0, 1, 0).getBlock()) ){
		    //This is the block above.
                    //CHeck for attchables for the block being placed.
                    if(!(Material.STEP.equals(e.getBlockPlaced().getLocation().subtract(0, 1, 0).getBlock().getType())))
                        e.setCancelled(true);
                }
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockBreak (BlockBreakEvent e) {
		if ( scs.getShopHandler().isShopBlock(e.getBlock()))
			e.setCancelled(true);
	}
        
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPistonExtend (BlockPistonExtendEvent e) {
		for (Block b : e.getBlocks())
			if ( scs.getShopHandler().isShopBlock(b) )
				e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPistonRetract(BlockPistonRetractEvent e) {;
		if ( scs.getShopHandler().isShopBlock(e.getRetractLocation().getBlock()) )
			e.setCancelled(true);
	}

}
