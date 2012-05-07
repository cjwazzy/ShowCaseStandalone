package com.miykeal.showCaseStandalone.interfaces;


import com.miykeal.showCaseStandalone.Exceptions.ShopNotFoundException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import java.io.IOException;
import org.bukkit.Chunk;
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


public interface ShopHandler {
	
	public void setStorage		(ShopStorage ss)		throws IOException;
	public void reload			()						throws IOException;	// Also load
	public void unload			()						throws IOException;	// Also saves them
	public void importStorage       (ShopStorage from, ShopStorage to)                      throws IOException;
        
	public Shop	getShopForItem 	(Item  i)				throws ShopNotFoundException;
	public Shop getShopForBlock	(Block b)				throws ShopNotFoundException;
	
	public boolean isShopItem	(Item  i);
	public boolean isShopBlock	(Block b);
	
	public void	addShop		(Shop p);
	public void removeShop	(Shop p);
	
	public void loadChunk	(Chunk k);
	public void unloadChunk	(Chunk k);
	
	public void hideAll	();
	public void showAll ();	// But only if the chunk is loaded...
	
	public void saveAll ()								throws IOException;
	public void save    (Shop p)						throws IOException;
	
	public void start	();
	public void stop	();
	
	/*
	 * The Idea is, that the shop handler knows if the shop is interacting,
	 * and because of that knows that the shop has changed, so it can save
	 * the one shop.
	 */
	public void interact	(Block b, Player p, int amount)	throws ShopNotFoundException;
	public void info		(Block b, Player p)				throws ShopNotFoundException;
}
