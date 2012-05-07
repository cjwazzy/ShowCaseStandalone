package com.miykeal.showCaseStandalone.Listeners;

import com.miykeal.showCaseStandalone.Exceptions.ShopNotFoundException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

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

public class ShowCaseStandaloneEntityListener implements Listener {
    private ShowCaseStandalone scs; 
    
    public ShowCaseStandaloneEntityListener(ShowCaseStandalone scs) {
        this.scs = scs;
        scs.getServer().getPluginManager().registerEvents(this, scs);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(event.isCancelled())
            return;
        
        Block block;
        Iterator<Block> itr = event.blockList().iterator();
        while(itr.hasNext()){
            block = itr.next();
            if(scs.getShopHandler().isShopBlock(block)){
                if(Properties.cancelExplosion){
                    event.setCancelled(true);
                    try{scs.getShopHandler().getShopForBlock(block).show();} catch (Exception e) {}
                    return;
                } else {
                    try {
                        Shop p = scs.getShopHandler().getShopForBlock(block);
                        Player owner = p.getPOwner();
                        if(owner != null)
                            Messaging.send(owner, Localization.get("exploded").replace("%1", p.getItemName()));
//                            Messaging.send(owner, "`YYour " + p.getItemName() 
//                                    + "-showcase exploded. Deleting.");//exploded %1 (itemname)
                        scs.getShopHandler().removeShop(p);
                    } catch (ShopNotFoundException e){
                        scs.log(Level.WARNING, "Identified shop, but could not get handle for it.");
                        scs.log(Level.INFO, "Could not delete blown up shop.");
                    }
                }
            }
        }
    }
}
