package com.miykeal.showCaseStandalone.Listeners;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.ShopNotFoundException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShopInternals.Todo;
import com.miykeal.showCaseStandalone.ShopInternals.Todo.Types;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

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
public class ShowCaseStandalonePlayerListener implements Listener {
	
	private HashMap<Player, Todo>		todo 	= new HashMap<Player, Todo>();
	private final ShowCaseStandalone	scs;

	public ShowCaseStandalonePlayerListener(ShowCaseStandalone instance) {
		scs = instance;
                scs.getServer().getPluginManager().registerEvents(this, scs);
	}
	
        /*
	 * Cancel pickup of a Item if the item is a shop Item
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPickupItem (PlayerPickupItemEvent pe) {
		if ( scs.getShopHandler().isShopItem(pe.getItem()) )
			pe.setCancelled(true);				
	}
	
	/*
	 * Let the player Interact with the shop
         * Lets keep the priority low, so we don't get cancelled when we're not doing anything.
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onPlayerInteract (PlayerInteractEvent pie) {
            /*
             * This whole routine needs optimization.  Currently it takes far more time 
             * then it should.
             */
            
            BenchMark bm = null;
            if(Properties.interactDebug){
                bm = new BenchMark("onPlayerInteract");
                bm.start("init");
            }
                
                
		// Abort if action does not fit - saves power :)
		if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !pie.getAction().equals(Action.LEFT_CLICK_BLOCK))
			return;
		
		// Collects information
		Player 		player 		= pie.getPlayer();
		Block	 	block		= pie.getClickedBlock();

                if(Properties.interactDebug)
                    bm.mark();
                
		try {
			// Interact
			if (pie.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
                            //Lets check for attachables in player's hand.  IF found, abort this. 
                            //This will allow players to attach signs, paintings, torches etc. to showcases.
                            //I have to find type ids for signs and paintings, since they don't respond to 
                            //any instanceof's i could find.
                            if(pie.hasItem() && !todo.containsKey(player))
                                if((pie.getItem().getData() instanceof Attachable) ||
                                    (pie.getItem().getTypeId() == 323) ||
                                    (pie.getItem().getTypeId() == 321) )
                                        return;
                            
				if (todo.containsKey(player)) {
					if (todo.get(player).types.equals(Types.CREATE)){
                                                if(Properties.interactDebug)
                                                    bm.mark("Rightclickblock");
                                                
						if(cantInteract(player, block))
                                                        throw new InsufficientPermissionException(Localization.get("protectedError"));
                                                else
							this.create(player, block);
					}
	                                
					else if (todo.get(player).types.equals(Types.REMOVE))
						this.remove(player, block);
					
					else if (todo.get(player).types.equals(Types.ADD)) 
						this.add(player, block, (int)todo.get(player).amount);
					
					
					else if (todo.get(player).types.equals(Types.GET)) 
						this.get(player, block, (int)todo.get(player).amount);
					
					else if (todo.get(player).types.equals(Types.LIMIT))
						this.limit (player, block, (int)todo.get(player).amount);
					
					else if (todo.get(player).types.equals(Types.SETOWNER))
						this.setOwner (player, block);
					
					else if (todo.get(player).types.equals(Types.SETPRICE))
						this.price (player, block, todo.get(player).amount);
					
                                        if(Properties.interactDebug)
                                            bm.mark("end if block");
                                        
					pie.setCancelled(true); 
					player.updateInventory();	// Have to :(

                                        if (todo.containsKey(player))
                                                todo.remove(player);
					
                                        
                                        if(Properties.interactDebug){
                                            bm.mark("end rightclick");
                                            bm.end();
                                        }
					
					
				} else {
					Shop p = scs.getShopHandler().getShopForBlock(pie.getClickedBlock());
                                        
                                        if(Properties.interactDebug)
                                            bm.mark("interact shopforblck");
                                        
					if (p != null && scs.hasPermission(player, Properties.permUse)) {
                                                if(Properties.interactDebug)
                                                    bm.mark("hasPermission");
                                            
						pie.setCancelled(true);
	                                        if(player.isSneaking()){
	                                            //p.interact(player, scs.pv.getPlayerTransactionAmount(player));  -- This went around the shop handler, which actually is the thing that updates the save file.  Bad.
                                                    scs.getShopHandler().interact(block, player, ShowCaseStandalone.pv.getPlayerTransactionAmount(player));
                                                } else { 
	                                            scs.getShopHandler().interact(block, player, 1);
                                                }
                                                
                                                if(Properties.interactDebug)
                                                    bm.mark("afterinteract");
                                                 
						player.updateInventory();	// Have to :(
                                                
                                                if(Properties.interactDebug){
                                                    bm.mark("updateInventory");
                                                    bm.end();
                                                }
                                                
					} else if (p != null && !scs.hasPermission(player, Properties.permUse))
						throw new InsufficientPermissionException();
				}
			}
			
			// Show info
			else if (pie.getAction().equals(Action.LEFT_CLICK_BLOCK) ) {
                                if(Properties.interactDebug)
                                    bm.mark("leftclick");
                                
				Shop p = scs.getShopHandler().getShopForBlock(pie.getClickedBlock());
                                
                                if(Properties.interactDebug)
                                    bm.mark("interact shopforblck");
                                
				if (p != null && scs.hasPermission(player, Properties.permUse)) {
                                        
                                        if(Properties.interactDebug)
                                            bm.mark("hasPermission");
                                    
					pie.setCancelled(true);
					p.info(player);
				} else if (p != null && !scs.hasPermission(player, Properties.permUse))
					throw new InsufficientPermissionException();
                                
                                if(Properties.interactDebug)
                                    bm.end();
			}
		} catch (ShopNotFoundException snfe) {
			// No problem, if the selected block is no shop, nothing needs to be done.
		} catch (InsufficientPermissionException ipe) {
                    Messaging.send(player, "`r" + ipe.getMessage());
                    pie.setCancelled(true);
                    if (todo.containsKey(player))
                            todo.remove(player);
                    
                }
	}

	/*
	 * Remove any player-set unit size.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		ShowCaseStandalone.pv.clearPlayerTransactionAmount(p);
		ShowCaseStandalone.pv.clearLastTransaction(p);
	}
        
        
	/*
	 * Adds given Todo-Object to HashMap
	 */
	public void addTodo (Player player, Todo t) {
		todo.put(player, t);
	}
	
	/*
	 * Removes Todo-Object with given player as key
	 */
	public Todo removeTodo (Player player) {
			return todo.remove(player);
	}

	/*
	 * Changes price of a shop
	 */
	private void price (Player player, Block b, double price) throws ShopNotFoundException, InsufficientPermissionException {
		Shop shop = scs.getShopHandler().getShopForBlock(b);

		if (shop == null)
                        throw new ShopNotFoundException();

		if (shop.getAtivitie().equals(Activities.DISPLAY)) {
			Messaging.send(player, Localization.get("displayPriceError"));
//			Messaging.send(player, "`rYou can`t set prices on a display showcase.");//msg: displayPriceError
			return;
		}
                
                if (!player.getName().equals(shop.getOwner()) && !scs.hasPermission(player, Properties.permAdmin)) {
                        throw new InsufficientPermissionException(Localization.get("ownerAdminError1"));
//                        throw new InsufficientPermissionException("Only the owner or an admin can change the price.");//msg: ownerAdminError1
			//Messaging.send(player, "`rOnly the owner, or an admin, can change the price.");
			//return;
		}
                
		// Set the shop price
		shop.setPrice(price);
		Messaging.send(player, Localization.get("priceSet") + scs.formatCurrency(shop.getPrice()));
//		Messaging.send(player, "`yPrice set: "+ scs.formatCurrency(shop.getPrice()));//msg: priceSet

		// Saving changes
		this.saveShop(shop, player);
	}


	/*
	 * Changes maxAmount of a BUY showcase
	 */
	private void limit (Player player, Block b, int limit) throws ShopNotFoundException, InsufficientPermissionException {
		Shop shop = scs.getShopHandler().getShopForBlock(b);

		if (shop == null) 
                        throw new ShopNotFoundException();

		if(!shop.getAtivitie().equals(Activities.BUY)) {
			Messaging.send(player, Localization.get("buyLimitError"));
//			Messaging.send(player, "`rYou can only change the buy limit on a BUY showcase.");//msg: buyLimitError
			return;
		}
                
                if (!player.getName().equals(shop.getOwner()) && !scs.hasPermission(player, Properties.permAdmin)) 
                        throw new InsufficientPermissionException(Localization.get("ownerAdminError2"));
//                        throw new InsufficientPermissionException("Only the owner or an admin can set the buy limit.");//msg: ownerAdminError2

		// Set the shop limit
		shop.setMaxAmount(limit);
		Messaging.send(player, Localization.get("buyLimit") + shop.getMaxAmount());
//		Messaging.send(player, "`yNew buy limit: "+ shop.getMaxAmount());//msg: buyLimit

		// Saving changes
		this.saveShop(shop, player);
	}


	/*
	 * Add Items to a shop
	 */
	private void add (Player player, Block b, int amount) throws ShopNotFoundException, InsufficientPermissionException {
		Shop shop = scs.getShopHandler().getShopForBlock(b);

		if (shop == null)
                        throw new ShopNotFoundException();

		if (shop.getAtivitie().equals(Activities.DISPLAY)) {
			Messaging.send(player, Localization.get("addError"));
//			Messaging.send(player, "`rYou can`t add items to a DISPLAY showcase.");//msg: addError
			return;
		}
                
                if (!player.getName().equals(shop.getOwner()) && !scs.hasPermission(player, Properties.permAdmin)) 
                        throw new InsufficientPermissionException(Localization.get("ownerAdminError3")); 
//                        throw new InsufficientPermissionException("Only the owner or an admin can add items to a showcase.");//msg: ownerAdminError3

		if (shop.getAmount() < 0)
			Messaging.send(player, Localization.get("unlimitedError1"));
//			Messaging.send(player, "`rYou can`t add items to an unlimited showcase.");//msg: unlimitedError1
                

                int workingAmount = shop.safeRemoveItems(player, amount);
                shop.setAmount(shop.getAmount() + workingAmount);
                Messaging.send(player, Localization.get("inventoryUpdate")
                        .replace("%1", String.valueOf(workingAmount))
                        .replace("%2", String.valueOf(shop.getAmount())));
//                Messaging.send(player, "`gSuccessflly added: "+ workingAmount + "  `gCurrent inventory: "+ shop.getAmount()); //msg: inventoryUpdate %1 %2 (workingamount, getamount)

                // Saving changes
         	this.saveShop(shop, player);
                        
                ShowCaseStandalone.tlog(player.getName(), player.getName(), "add", 
                        workingAmount, 0, shop.getMaterial(), shop.getSHA1(), shop.getAmount());
	}


	/*
	 * Get Item from shop
	 */
	private void get (Player player, Block b, int amount) throws ShopNotFoundException, InsufficientPermissionException {
		Shop shop = scs.getShopHandler().getShopForBlock(b);

		if (shop == null)
			throw new ShopNotFoundException();

		if (shop.getAtivitie().equals(Activities.DISPLAY)) {
			Messaging.send(player, Localization.get("getError"));
//			Messaging.send(player, "`rYou can`t remove items from a DISPLAY showcase.");//msg: getError
			return;
		}
                
                if (!player.getName().equals(shop.getOwner()) && !scs.hasPermission(player, Properties.permAdmin)) 
                        throw new InsufficientPermissionException(Localization.get("ownerAdminError4"));
//                        throw new InsufficientPermissionException("Only the owner or an admin can remove items from a showcase.");// ownerAdminError4
                
		if (shop.getAmount() < 0)
			Messaging.send(player, Localization.get("unlimitedError2"));
//			Messaging.send(player, "`rYou can`t remove items from an unlimited showcase.");//msg: unlimitedError2
		
                if (amount == 0 || amount > shop.getAmount())
                        amount = shop.getAmount();
                        
                //Remove as many of the item, up to the amount specified, and store
                //how much was actually removed.
                int workingAmount = shop.safeAddItems(player, amount);
                shop.setAmount(shop.getAmount() - workingAmount);
                Messaging.send(player, Localization.get("receivedItems").replace("%1", String.valueOf(workingAmount)) + 
                    ((workingAmount == 0) ? Localization.get("noRoom") : ""));
//                Messaging.send(player, "`gRetrieved " + workingAmount + " items." + 
//                    ((workingAmount == 0) ? " No room in your inventory." : "")); //msg: ? receivedItems %1 (working amount) : noRoom

                ShowCaseStandalone.tlog(player.getName(), player.getName(), "get", 
                    workingAmount, 0, shop.getMaterial(), shop.getSHA1(), shop.getAmount());
			
                // Saving changes
                this.saveShop(shop, player);
	}


	/*
	 * Removes a shop
	 */
	private void remove (Player player, Block b) throws ShopNotFoundException, InsufficientPermissionException {
		Shop shop = scs.getShopHandler().getShopForBlock(b);

		if (shop == null)
			throw new ShopNotFoundException();

		if (!player.getName().equals(shop.getOwner()) && !scs.hasPermission(player, Properties.permAdmin))
			throw new InsufficientPermissionException("Only the owner or an admin can remove a showcase."); //msg: ownerAdminError5
//			throw new InsufficientPermissionException("Only the owner or an admin can remove a showcase."); //msg: ownerAdminError5

		if (!shop.getAtivitie().equals(Activities.DISPLAY)) {
			//Remove as many items as I can.
			int workingAmount = shop.safeAddItems(player, shop.getAmount());
			shop.setAmount(shop.getAmount() - workingAmount);
			Messaging.send(player, Localization.get("receivedItems").replace("%1", String.valueOf(workingAmount)));
//			Messaging.send(player, "`YYou received " + workingAmount + " items from the showcase.");//msg: receivedItems %1 (working amount)

			ShowCaseStandalone.tlog(player.getName(), player.getName(), "remove",
					workingAmount, 0, shop.getMaterial(), shop.getSHA1(), shop.getAmount());

                        if(shop.getAmount() > 0){
				Messaging.send(player, Localization.get("inventoryFull"));
//				Messaging.send(player, "`rYou do not have enough space in your inventory for ALL of your showcase's items. Drop some items and try again.");//msg: inventoryFull
				Messaging.send(player, Localization.get("itemsLeft") + shop.getAmount());
//				Messaging.send(player, "`rLeft in the showcase: "+ shop.getAmount());//msg: itemsLeft
				this.saveShop(shop, player);
				return;
                        }
		}

                // Remove the showcase
		scs.getShopHandler().removeShop(shop);
		Messaging.send(player, Localization.get("successRemove"));
//		Messaging.send(player, "`gSuccessfully removed the showcase.");//successRemove
	}

	/*
	 * Create a shop
	 */
	private void create (Player player, Block b) throws ShopNotFoundException, InsufficientPermissionException {
		Shop p				= this.removeTodo(player).shop;
		int removed = 0;

		double createPrice = 0;
		switch (p.getAtivitie()) {
		case SELL:
			createPrice = Properties.sellShopCreatePrice;
			break;
		case BUY:
			createPrice = Properties.buyShopCreatePrice;
			break;
		case DISPLAY:
			createPrice = Properties.displayCreatePrice;
			break;
		}

		if(!scs.getBalanceHandler().hasEnough(player.getName(), createPrice)){
			Messaging.send(player, Localization.get("insufficientMoney"));
//			Messaging.send(player, "`rYou do not have enough money to create this showcase.");//msg: insufficientMoney
			return;
		}   

		if (scs.getShopHandler().isShopBlock(b)) {
			Messaging.send(player, Localization.get("alreadyShowcase")); 
//			Messaging.send(player, "`rError: Selected block is already a showcase.");//msg: alreadyShowcase
			return;
		}
                
                //Check for blacklisted/whitelisted shop block
                // I hate fucking workarounds.  Fucking bukkit.
                MaterialData md = new MaterialData(b.getTypeId(), b.getData());
                if  (Properties.blackList && Properties.blockList.contains(md)
                        ||
                    (!Properties.blackList && !Properties.blockList.contains(md)))
                        throw new InsufficientPermissionException(Localization.get("blacklistBlock"));
//                        throw new InsufficientPermissionException("You cannot use that block as a showcase.");//msg: blacklistBlock
		
                
		if (p.getAtivitie().equals(Activities.SELL) && p.getAmount() > 0) {
			//Just try to remove the items and see how many I actually can remove (up to the specified amount).
			removed = p.safeRemoveItems(player, p.getAmount());
			if(removed == 0) {
				Messaging.send(player, Localization.get("insufficientItems"));
//				Messaging.send(player, "`rError: You do not have enough items to create the showcase.");//msg: insufficientItems
				return;
			}
		}
        //Try to replace with a unique hash, otherwise keep the random hash.
        try {p.setSHA1(Utilities.sha1(b.toString()));} catch (IOException ioe) {}
		p.setAmount     (removed);
		p.setBlock		(b);
		p.setLocation	(b.getLocation());
		p.setVisible	(true);

		scs.getShopHandler().addShop(p);
		this.saveShop(p, player);	// Saving the shop

		scs.getBalanceHandler().sub(player, createPrice);

		Messaging.send(player,Localization.get("successCreate"));
//		Messaging.send(player,"`gShowcase successfully created.");//successCreate
		if (!p.getAtivitie().equals(Activities.DISPLAY)) {
			Messaging.send(player, Localization.get("currentInventory") + p.getAmount()); 
//			Messaging.send(player, "`gCurrent inventory: " + p.getAmount()); //currentInventory
			ShowCaseStandalone.tlog(player.getName(), player.getName(), "create",
					removed, createPrice, p.getMaterial(), p.getSHA1(), p.getAmount());
		}
	}


	/*
	 *  Set Owner of a shop
	 */
	private void setOwner (Player player, Block b) throws ShopNotFoundException, InsufficientPermissionException {
		Todo t = this.removeTodo(player);

		Shop p = scs.getShopHandler().getShopForBlock(b);
                
		if (p == null) 
			throw new ShopNotFoundException();
                
                if (!player.getName().equals(p.getOwner()) && !scs.hasPermission(player, Properties.permAdmin))
                        throw new InsufficientPermissionException(Localization.get("ownerAdminError6"));
//                        throw new InsufficientPermissionException("Only the owner or an admin can change the owner.");//ownerAdminError6
                
		p.setOwner(t.string);     
		Messaging.send(player, Localization.get("setOwner").replace("%1", t.string));
//		Messaging.send(player, "`gSet owner to " + t.string); //setOwner %1 (t.string)

		// Saving changes
		this.saveShop(p, player);
	}

	private void saveShop (Shop p, Player player) {
		try {
			scs.getShopHandler().save(p);
		} catch (IOException ioe) {
			scs.log(Level.WARNING, ioe+" while saving a shop.");
			Messaging.send(player, Localization.get("saveError"));
//			Messaging.send(player, "`rInternal error on showcase save. Please inform an admin!");//msg: saveError
		}
	}
        
        /**
         * Checks for ability of player to do something where the showcase will be.  Default is
         * to check for building rights (BlockPlaceEvent), but can be other.
         * @param p Player
         * @param b Block
         * @return 
         */
        private boolean cantInteract(Player p, Block b) {
            //Right now, block place is the only interact I can think of supported by bukkit.
            BlockPlaceEvent bpe = new BlockPlaceEvent(b, 
                b.getState(), b.getRelative(BlockFace.DOWN), 
                p.getItemInHand(), p, true);
            Bukkit.getServer().getPluginManager().callEvent(bpe);
            
            return bpe.isCancelled();
        }
}
