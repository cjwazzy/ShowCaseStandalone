package com.miykeal.showCaseStandalone.ShopInternals;

import com.miykeal.showCaseStandalone.Exceptions.ShopNotFoundException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.BenchMark;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.ShopHandler;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class SimpleShopHandler implements ShopHandler {
    
        //I've converted this to a fixed array, because arrays are a lot quicker,
        //and lower mem impact.  Since we have a sync repeating thread that loops through
        //the array, i've made it fixed. I use temp array lists to add/remove shops.
        private Shop[]          shops;   
	private ShopStorage 		storage	= null;
	private ShowCaseStandalone 	scs		= null;
        private int                     syncTask;
        
	
	public SimpleShopHandler (ShowCaseStandalone scs) {
		this.scs		= scs;
                startThread();
	}
	
        private void startThread(){
            //You cannot call Bukkit functions from an async thread.  It must be the main thread.
            //See http://forums.bukkit.org/threads/threads-the-scheduler.35239/
            //We're going to use Bukkit's scheduler to do a repeating task in the main thread.
            syncTask = scs.getServer().getScheduler().scheduleSyncRepeatingTask(scs, new Runnable() {
                
                @Override
                public void run() {
                    long start = System.nanoTime();
                    if(Properties.threadDebug)
                        ShowCaseStandalone.dlog("Refreshing items. Thread exec start: " + start);

                        for (Shop p : shops) {
                                    //Regular chunk.isloaded causes bukkit to load the chunk.
                                    if(!isChunkLoaded(p.getLocation()))
                                        continue;
                                    
                                    if (Properties.hideInactiveShops &&(!p.isActive() && p.isVisible()))
                                        p.setVisible(false);
                                    else if (p.getItem() == null)
                                            p.setVisible(!Properties.hideInactiveShops || p.isActive()); //Only show active shops if the hide flag is set.
                                    else if (p.getItem().isDead()) {
                                            if(!Properties.hideInactiveShops || p.isActive()){
                                                // May solves some problems with not pickupable items (also not moving)
                                                World  w 	= p.getItem().getWorld();
                                                double x	= p.getItem().getLocation().getX();
                                                double y	= 0;
                                                double z	= p.getItem().getLocation().getZ();
//
                                                p.getItem().teleport( new Location (w, x, y, z) );
                                                //p.setVisible(false);
                                                p.setVisible(true);
                                            } else {
                                                p.setVisible(false);
                                            }
                                    }
                        }
                        
                    if(Properties.threadDebug) {
                            long end = System.nanoTime();
                            long net = end - start;
                            ShowCaseStandalone.dlog("Thread exec end: " + end);
                            ShowCaseStandalone.dlog("Net time: " + net);
                    }
                }
            }, 5L, Properties.intervall);
        }
	
	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#setStorage(com.miykeal.showCaseStandalone.interfaces.ShopStorage)
	 */
        @Override
	public void setStorage (ShopStorage ss) throws IOException {
		this.storage	= ss;
		this.reload();
	}
        
        @Override
        public void importStorage (ShopStorage from, ShopStorage to) throws IOException {
                this.hideAll();
                this.setStorage(from);
                this.storage = to;
                this.saveAll();
                this.showAll();
        }
	
	/**
	 * reloads all shops from storage
	 * Needs storage storage Object set!
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#reload()
	 */
        @Override
	public void reload () throws IOException{
                ShowCaseStandalone.pv.clearAllTransactionAmount();
                ShowCaseStandalone.pv.clearAllTransactions();
                
                ShowCaseStandalone.slog(Level.FINEST, "reload(): storage.loadshops()");
                
                Shop[] tmp = storage.loadshops();
		shops 	= new Shop[tmp.length];
                shops = tmp;
	}
	
	/**
	 * Needs storage storage Object set!
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#unload()
	 */
	public void unload () throws IOException {
		//this.saveAll();
                shops = new Shop[0];
                ShowCaseStandalone.pv.clearAllTransactionAmount();
                ShowCaseStandalone.pv.clearAllTransactions();
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#getShopForItem(org.bukkit.entity.Item)
	 */
        @Override
	public Shop getShopForItem(Item i) throws ShopNotFoundException {
//                if(shops == null)
//                        throw new ShopNotFoundException();
            
		for (Shop p : shops)
			if (p.getItem() != null)
				if (p.getItem().equals(i))
					return p;
		throw new ShopNotFoundException ();
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#getShopForBlock(org.bukkit.block.Block)
	 */
        @Override
	public Shop getShopForBlock(Block b) throws ShopNotFoundException {
//                if(shops == null)
//                        throw new ShopNotFoundException();
            
		for (Shop p : shops)
			if (p.getBlock() != null)
                                    if (p.getBlock().getLocation().equals(b.getLocation()) && b.getWorld().equals(p.getBlock().getWorld())) //location includes world, why check both?
                                            return p;
		throw new ShopNotFoundException();
	}
	
	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#isShopItem(org.bukkit.entity.Item)
	 */
        @Override
	public boolean isShopItem	(Item  i) {
		try {
			this.getShopForItem(i);
			return true;
		} catch (ShopNotFoundException snfe) {
			return false;
		}
	}
	

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#isShopBlock(org.bukkit.block.Block)
	 */
        @Override
	public boolean isShopBlock(Block b) {
		try {
			this.getShopForBlock(b);
			return true;
		} catch (ShopNotFoundException snfe) {
			return false;
		}
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#addShop(com.miykeal.showCaseStandalone.Shop)
	 */
        @Override
	public void addShop(Shop p) {
                ShowCaseStandalone.slog(Level.FINEST, "Array size before add: " + shops.length);
                ArrayList<Shop> tmp = new ArrayList<Shop>();
                Collections.addAll(tmp, shops);
		tmp.add(p);
                shops = tmp.toArray(new Shop[tmp.size()]);
                ShowCaseStandalone.slog(Level.FINEST, "Array size after add: " + shops.length);
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#removeShop(com.miykeal.showCaseStandalone.Shop)
	 */
        @Override
	public void removeShop(Shop p) {
                ShowCaseStandalone.slog(Level.FINEST, "Array size before remove: " + shops.length);
		p.setVisible(false);
                try {
                    storage.removeShop(p.getSHA1());
                } catch (IOException ioe) {
                    ShowCaseStandalone.slog(Level.WARNING, "Error removing shop from database: " + ioe.getMessage());
                }
                
                ArrayList<Shop> tmp = new ArrayList<Shop>();
                Collections.addAll(tmp, shops);
                
                if(tmp.contains(p)){
                    tmp.remove(p);
                    shops = tmp.toArray(new Shop[tmp.size()]);
                }
                ShowCaseStandalone.slog(Level.FINEST, "Array size after remove: " + shops.length);
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#loadChunk(org.bukkit.Chunk)
	 */
        @Override
	public void loadChunk(Chunk k) {
                if(Properties.chunkDebug)
                    ShowCaseStandalone.dlog("Load chunk: " + k.toString() + ", " + k.getWorld().getName());
                
                if(shops == null){
                    if(Properties.chunkDebug)
                        ShowCaseStandalone.dlog("Shops is null.");
                    return;
                }
                
                if(Properties.chunkDebug)
                    ShowCaseStandalone.dlog("Load chunk: " + k.toString() + ", " + k.getWorld().getName());
                
                try {
                    for (Shop p : shops) {
                        //Have to do a check on the string, because chunk.equal never evaluates to true, despite being equal.
                        //DEBUG this, dont' think its working right.
                        if (k.toString().equals(p.getChunk().toString())){
                            if(k.getWorld().getName().equals(p.getLocation().getWorld().getName())) {
                                if(Properties.chunkDebug)
                                    ShowCaseStandalone.dlog("Found scs to load: " + p.getSHA1());
                                p.setVisible(!Properties.hideInactiveShops || p.isActive());
                            }
                        }
                    }
                } catch (NullPointerException npe) {
                    ShowCaseStandalone.slog(Level.WARNING, "NPE on load chunk shop enable.");
                }
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#unloadChunk(org.bukkit.Chunk)
	 */
        @Override
	public void unloadChunk(Chunk k) {
            if(Properties.chunkDebug)
                ShowCaseStandalone.dlog("Unload chunk: " + k.toString() + ", " + k.getWorld().getName());
            
                try {
                    for (Shop p : shops) {
                        //Have to do a check on the string, because chunk.equal never evaluates to true, despite being equal.
                        //DEBUG this, dont' think its working right.
                        if (k.toString().equals(p.getChunk().toString())){
                            if(k.getWorld().getName().equals(p.getLocation().getWorld().getName())) {
                                if(Properties.chunkDebug)
                                    ShowCaseStandalone.dlog("Found scs to unload: " + p.getSHA1());
                                p.setVisible(false);
                            }
                        }
                    }
                } catch (NullPointerException npe) {
                    ShowCaseStandalone.slog(Level.WARNING, "NPE on unload chunk shop disable.");
                }
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#hideAll()
	 */
        @Override
	public void hideAll() {
            ShowCaseStandalone.slog(Level.FINEST, "hideAll():");
		for (Shop p : shops)
			p.setVisible(false);
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#showAll()
	 */
        @Override
	public void showAll () {
		for (Shop p : shops)
			if (p.getBlock() != null)
				if ( p.getBlock().getChunk().isLoaded() )
					 p.setVisible(!Properties.hideInactiveShops || p.isActive());
                                           
	}
	
	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#saveAll()
	 */
        @Override
	public void saveAll () throws IOException {
                ShowCaseStandalone.slog(Level.FINEST, "saveAll() shops");
		storage.saveShops(shops);
         }


        @Override
         public void save (Shop p) throws IOException {
                 ShowCaseStandalone.slog(Level.FINEST, "Save shop: " + p.getSHA1());
                 storage.saveShop(p.getSHA1(), p);
         }

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#stop()
	 */
        @Override
	public void stop () {
                scs.getServer().getScheduler().cancelTask(syncTask);
                ShowCaseStandalone.slog(Level.FINEST, "Stopped sync task.");
	}
        
        @Override
        public void start () {
                startThread();
                ShowCaseStandalone.slog(Level.FINEST, "Started sync task.");
        }

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#interact(org.bukkit.block.Block, org.bukkit.entity.Player, int)
	 */
        @Override
	public void interact(Block b, Player p, int amount) throws ShopNotFoundException {
                BenchMark bm = null;
                if(Properties.interactDebug)
                    bm = new BenchMark("Handler interact");
                
		Shop sp = this.getShopForBlock(b);
                if(Properties.interactDebug)
                    bm.mark("getShopForBlock");
                
                sp.interact(p, amount);
                if(Properties.interactDebug)
                    bm.mark("after interact");
		     
		try { 
			storage.saveShop(sp.getSHA1(), sp);
                        if(Properties.interactDebug)
                            bm.mark("after saveShop");
		} catch (IOException ioe) {
			scs.log(Level.INFO, "Couldn't save shop after interacting: "+ioe);
		}
                if(Properties.interactDebug)
                    bm.end();
	}

	/**
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopHandler#info(org.bukkit.block.Block, org.bukkit.entity.Player)
	 */
        @Override
	public void info(Block b, Player p) throws ShopNotFoundException {
		Shop sp = this.getShopForBlock(b);
		     sp.info(p);
	}
        
        private boolean isChunkLoaded(Location loc){
            int cx = loc.getBlockX() >> 4;
            int cy = loc.getBlockZ() >> 4;
            World cw = loc.getWorld();
            return cw.isChunkLoaded(cx, cy);
        }
}
