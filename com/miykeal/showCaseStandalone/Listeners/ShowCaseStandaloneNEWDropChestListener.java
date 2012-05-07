package com.miykeal.showCaseStandalone.Listeners;

//This is for a fork of the DC plugin (that hadn't been updated.
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.noheroes.dropchest.api.DropChestSuckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
 * 
* @author Sorklin <sorklin at gmail.com>
*/
public class ShowCaseStandaloneNEWDropChestListener implements Listener {
    private ShowCaseStandalone scs;
    
    public ShowCaseStandaloneNEWDropChestListener(ShowCaseStandalone instance){
        this.scs = instance;
        scs.getServer().getPluginManager().registerEvents(this, scs);
    }

    @EventHandler(ignoreCancelled=true)
    public void onDropChestSuck(DropChestSuckEvent event) {
        if(scs.isShowCaseItem(event.getItem())){
            event.setCancelled(true);
        }   
    }
}
