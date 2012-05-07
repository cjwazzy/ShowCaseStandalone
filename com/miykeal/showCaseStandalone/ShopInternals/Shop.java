package com.miykeal.showCaseStandalone.ShopInternals;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.*;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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

public class Shop {
	public enum Activities {
		BUY, SELL, DISPLAY,
	}

	private Object 				syncDetails = new Object();
	private ShowCaseStandalone 	scs;
	private Activities 			activities 	= null;
	private int 				amount 		= 0;
	private int 				maxAmount	= 0;
	//private MaterialData 		material 	= null;
        private ItemStack                       is              = null;
        private String                          itemName        = null;       
	private double 				price 		= 0;
	private String				owner 		= null;
	private Block 				block 		= null;
        private Chunk                           chunk           = null;
	private Location 			location 	= null;
	private Item 				item 		= null;
	private boolean 			isVisible 	= false;
	private boolean 			isUnlimit 	= false;

	private String				sha1		= null;

	/*
	 * #### Constructor used by the static method load to load shops again
	 */
	public Shop(String sha1, ShowCaseStandalone scs, Activities a, int amount, int maxAmount, ItemStack is, double price, String owner) {
		this.sha1		= sha1;
		this.scs 		= scs;
		this.activities = a;
                this.is = is;
		this.price      = price;
		this.owner      = owner;

		if (amount < 0 || maxAmount < 0) 
			isUnlimit = true;
                
                this.amount = amount;
                this.maxAmount = maxAmount;
                
                //Make sure there is only 1 in the ItemStack.
                this.is.setAmount(1);
                this.itemName = MaterialNames.getItemName(is.getTypeId(), is.getDurability());
	}
	
	/*
	 * SHA1 hash to identifier the shop again
	 */
	public String getSHA1 () {
            return this.sha1;
	}
	
        public void setSHA1 (String sha1) {
            this.sha1 = sha1;
        }
        
        public String getShopItemName(){
            return this.itemName;
        }

	/*
	 * Tells the player important information about the shop
	 */
	public void info(Player p) {
            
            /*
             * Overhauled to display extended shop information.
             */

            StringBuilder sb = new StringBuilder();
            List<String> msg = new ArrayList<String>();
            String delimiter = "";
            String c;
     
            
	    if (activities.equals(Activities.DISPLAY)) {
	    	sb.append(Localization.get("itemOnDisplay").replace("%1", this.itemName));
//	    	sb.append("`YItem on display: ").append(this.itemName).append(".");//msg: itemOnDisplay %1 (itemname)
	        Messaging.send(p, sb.toString());
                
	    } else {
                //For color:
                if(activities.equals(Activities.BUY)){
                    c = Localization.get("buyColor");
//                    c = "`Y";//msg: buyColor
                } else {
                    c = Localization.get("sellColor");
//                    c = "`G";//msg: sellColor
                }
                if(!isActive())
                    c = Localization.get("inactiveColor");
//                    c = "`s";//inactiveColor
                
                //Shop Type: [type]   Price: [price]     Inventory: [amount[/max buy amount]]
                msg.add("`w "); //blank line for formatting.
                
                sb.append(Localization.get("info1")).append(c).append(activities.toString());//msg: info1
//                sb.append("Shop Type: ").append(c).append(activities.toString());//msg: info1
                sb.append(Localization.get("info2")).append(c);
//                sb.append("    ").append("`wPrice: ").append(c);//info2
                sb.append(scs.formatCurrency(this.getPrice()));
                sb.append(Localization.get("info3")).append(c);
//                sb.append("    ").append("`wInventory: ").append(c);//info3
                if(isUnlimit){
                    sb.append(Localization.get("infoUnlimited"));
//                    sb.append("Unlimited");//infoUnlimited
                } else {
                    sb.append(amount);
                    if(activities.equals(Activities.BUY))
                        sb.append("/").append(maxAmount);

                }
                msg.add(sb.toString());
                
                //Item name: [Enchanted][New/Used][Item][Percent used]
                sb = new StringBuilder();
                sb.append(Localization.get("info4")).append(c);
//                sb.append("Item Name: ").append(c);//info4
                if(!is.getEnchantments().isEmpty())
                        sb.append(Localization.get("info5"));
//                        sb.append("Enchanted").append(" ");//info5
                if(is.getType().getMaxDurability() != 0){
                    short max = is.getType().getMaxDurability();
                    short current = is.getDurability();
                    double usedPercent = ((double)(max - current)/max) * 100.0;
                    
                    if(current > 0){
                        sb.append(Localization.get("info6"));
//                        sb.append("Used").append(" ");//info6
                    } else {
                        sb.append(Localization.get("info7"));
//                        sb.append("New").append(" ");//info7
                    }

                    sb.append(this.itemName);
                    
                    if(current > 0){
                        sb.append(" [");
                        sb.append((int)usedPercent);
                        sb.append("%");
                        sb.append("]");
                    }
                } else
                    sb.append(this.itemName);
                msg.add(sb.toString());
                
                //If enchanted:
                //Enchantments: [enchantment types and levels]
                if (!is.getEnchantments().isEmpty()){
                    sb = new StringBuilder();
                    sb.append(Localization.get("info8")).append(c);
//                    sb.append("`wEnchantments: ").append(c);//info8
                    for (Entry<Enchantment, Integer> e : is.getEnchantments().entrySet()){
                        sb.append(delimiter);
                        sb.append(e.getKey().getName()).append(" ");
                        sb.append(e.getValue());
                        delimiter = Localization.get("itemDelimiter") + c; 
//                        delimiter = "`w," + " " + c; //itemDelimiter
                    }
                    msg.add(sb.toString());
                }
                
                //Owner: [owner]     Shop Status: [Active/Inactive]
                //Shop #: [sha1]
                sb = new StringBuilder();
                sb.append(Localization.get("info9")).append(c);
//                sb.append("Owner: ").append(c);//info9
                sb.append(owner.substring(0, 1).toUpperCase()).append(owner.substring(1));
                sb.append(Localization.get("info10")).append(c);
//                sb.append("    ").append("`wShop Status: ").append(c);//info10
                sb.append(isActive() ? Localization.get("info11") : Localization.get("info12"));
//                sb.append(isActive() ? "Active" : "Inactive");//info11 info12
                msg.add(sb.toString());
                if(p.getName().equalsIgnoreCase(owner))
                    msg.add(Localization.get("info13") + sha1);
//                    msg.add("`wShop #: `s" + sha1);//info13
                
                Messaging.mlSend(p, msg);
            }
        }
        
        
	/*
	 * Return whether the shop is unlimited.
	 */
	public boolean isUnlimited(){
		return this.isUnlimit;
	}


	/*
	 * Let the given player interact with the shop That can be buy or sale
	 */
	protected void interact(Player p, int interactAmount) {
                //If a shop is hidden, lets not interact with it.
                if(!this.isVisible)
                    return;
                BenchMark bm = null;
                
                if(Properties.interactDebug){
                    bm = new BenchMark("interact");
                    bm.start("init");
                }
            
		// Collect owner and player information
		Balance balance = scs.getBalanceHandler();
		Player pOwner = ShowCaseStandalone.get().getServer().getPlayer(owner);
//		HashMap<Integer, ItemStack> is;

		boolean unlimited = isUnlimit;
		double purchPrice;
		int workingAmount;
                
                if(Properties.interactDebug)
                    bm.mark();
                
		//Let's divide up this interact into Buy/Sell/Display now:
		switch (activities) {

		case SELL:    
			//unlimited shops have a negative amount.  so lets work around that.
			int amt = (unlimited) ? Integer.MAX_VALUE : amount;

			if(!unlimited && (amt < interactAmount))
				interactAmount = amt;
                        
                        if(Properties.interactDebug)
                            bm.mark("sell");
                        
			// Check for early exit conditions
			if (!unlimited && amt <= 0) {
				if (!p.equals(pOwner))
					Messaging.send(p, Localization.get("emptyShowcase"));
//					Messaging.send(p, "`yThe showcase is empty.");//msg: emptyShowcase
                                msgOwner(pOwner, Localization.get("emptyShowcaseOwner").replace("%1", this.itemName));
//                                msgOwner(pOwner, "`rYour " + this.itemName + "-showcase is empty");//msg: emptyShowcaseOwner %1 (itemname)
				if(Properties.interactDebug){
                                    bm.mark("!unlimited && amt <= 0");
                                    bm.end();
                                }
                                return;
			}
                        
			//Lets set our working amount to the smaller of the two:
			workingAmount = ((interactAmount <= amt) ? interactAmount : amt);
                        
                        if(Properties.interactDebug)
                            bm.mark("math conditions");
                        
			// Does the player have enough money to buy the item?
			if (!balance.hasEnough(p.getName(), price * workingAmount)) {
				Messaging.send(p, Localization.get("noMoney"));
//				Messaging.send(p, "`rYou do not have enough money.");//msg: noMoney
                                
                                if(Properties.interactDebug){
                                    bm.mark("economy check no money");
                                    bm.end();
                                }
                                
				return;
			}
                        
                        if(Properties.interactDebug)
                            bm.mark("economy check");

			//place as many of the items as i can, and get that amount.
			workingAmount = safeAddItems(p, workingAmount);
                        
                        if(Properties.interactDebug)
                            bm.mark("safeAddItems");

			if(workingAmount == 0){
				Messaging.send(p, Localization.get("noRoom2"));
//				Messaging.send(p, "`rYou do not have any room in your inventory to buy this.");//noRoom2
				return;
			}

			purchPrice = price * workingAmount;

			balance.sub(p, purchPrice);
			if(!unlimited) {
				balance.add(owner, purchPrice);
				amount -= workingAmount;
			}
                        
                        if(Properties.interactDebug)
                            bm.mark("economy transaction");

			ShowCaseStandalone.pv.setLastTransaction(p, new Transaction(p, this, workingAmount));
                        
                        if(Properties.interactDebug)
                            bm.mark("setup undo");
                        
			Messaging.send(p, Localization.get("sellMessage")
                                .replace("%1", itemName)
                                .replace("%2", String.valueOf(workingAmount))
                                .replace("%3", scs.formatCurrency(purchPrice)));
//			Messaging.send(p, "`gYou bought " + workingAmount + " "
//			        + ((workingAmount > 1) ? " items (" : " item (") 
//			        + itemName + ")"
//			        + " for " + scs.formatCurrency(purchPrice)); //msg: buyMessage: `gYou bought: %1, quantity: %2, price: %3.

			ShowCaseStandalone.tlog(p.getName(), owner, "buy", 
			        workingAmount, purchPrice, getMaterial(), 
			        sha1, amount);
                        
                        if(Properties.interactDebug)
                            bm.mark("log transaction");
                        
			if (!unlimited){
				msgOwner(pOwner, Localization.get("sellMessageOwner1")
                                        .replace("%1", itemName)
                                        .replace("%2", String.valueOf(amount)));
                                msgOwner(pOwner, Localization.get("sellMessageOwner2")
                                        .replace("%1", p.getName())
                                        .replace("%2", String.valueOf(workingAmount))
                                        .replace("%3", scs.formatCurrency(purchPrice)));
                                msgOwner(pOwner, "`Y" + Localization.get("info13") + sha1);
                        }
				
//				Messaging.send(pOwner, "`g" + p.getName()
//				                + " bought " + workingAmount + " item(s) (" + itemName + ") for "
//				                + scs.formatCurrency(purchPrice) + ". (Inventory Left: " + amount + ")");
            break;


		case BUY:
			if(!unlimited) {
				if (amount >= maxAmount) {
					Messaging.send(p, Localization.get("showcaseFull"));
//					Messaging.send(p, "`ySorry, the showcase is full.");//showcaseFull
                                        msgOwner(pOwner, Localization.get("showcaseFullOwner").replace("%1", itemName));
//                                        msgOwner(pOwner, "`yYour " + itemName + "-showcase is full"); //showcaseFullOwner: `yYour %1-showcase is full.
					return;
				}
				else if((amount + interactAmount) > maxAmount){
					interactAmount = maxAmount - amount;
				}
                                
                                if(Properties.interactDebug)
                                    bm.mark("buy init");
                                
				//Find out how many saleable items we have, and then take the smaller of the two.
				workingAmount = countSaleableItems(p);
                                
                                if(Properties.interactDebug)
                                    bm.mark("countSaleableItems");
                                
				workingAmount = (workingAmount <= interactAmount) ? workingAmount : interactAmount;
			} else
				workingAmount = interactAmount;

			
			if (balance.hasEnough(owner, price * workingAmount) || unlimited) {
                            
                                if(Properties.interactDebug)
                                    bm.mark("economy check");
                                
				workingAmount = safeRemoveItems(p, workingAmount);  //make sure we only charge for what we actually remove.
				
                                if(Properties.interactDebug)
                                    bm.mark("safeRemoveItems");
                                
                                if(workingAmount == 0){
                                    Messaging.send(p, Localization.get("noMatchingItems"));
//                                    Messaging.send(p, "`rYou are not carrying any matching items.");//noMatchingItems
                                    return;
                                }
                                
                                purchPrice = price * workingAmount;

				balance.add(p, purchPrice);
				if(!unlimited){
					balance.sub(owner, purchPrice);
					amount += workingAmount;
				}
                                
                                if(Properties.interactDebug)
                                    bm.mark("economy transaction");
                                
				if (!unlimited){
					msgOwner(pOwner, Localization.get("buyMessageOwner1")
                                                .replace("%1", itemName)
                                                .replace("%2", String.valueOf(amount)) 
                                                .replace("%3", String.valueOf(maxAmount)) );
                                        msgOwner(pOwner, Localization.get("buyMessageOwner2")
                                                .replace("%1", p.getName())
                                                .replace("%2", String.valueOf(workingAmount))
                                                .replace("%3", scs.formatCurrency(purchPrice)));
                                        msgOwner(pOwner, "`Y" + Localization.get("info13") + sha1);
                                }
//					msgOwner(pOwner, "`g" + p.getName() +  " sold " 
//					        + workingAmount + " item(s) to your " + itemName
//					        + " showcase for " + scs.formatCurrency(purchPrice));//soldMessageOwner: `g%1 bought**%2 %3 for %4.

				ShowCaseStandalone.pv.setLastTransaction(p, new Transaction(p, this, workingAmount));
				
                                if(Properties.interactDebug)
                                    bm.mark("setup undo");
                                
                                Messaging.send(p, Localization.get("buyMessage")
                                        .replace("%1", itemName)
                                        .replace("%2", String.valueOf(workingAmount))
                                        .replace("%3", scs.formatCurrency(purchPrice) ));
//				Messaging.send(p, "`gYou sold " + workingAmount + " item(s) for "
//				                + scs.formatCurrency(purchPrice) + "."); //soldMessage: `gYou sold**%1, quantity**%2, price**%3.

				ShowCaseStandalone.tlog(p.getName(), owner, "sell", 
				    workingAmount, purchPrice, getMaterial(), 
				    sha1, amount);
                                
                                if(Properties.interactDebug)
                                    bm.mark("log transction");

			} else {
				if (!unlimited)
					msgOwner(pOwner, Localization.get("noMoney3")
                                                .replace("%1", String.valueOf(workingAmount))
                                                .replace("%2", itemName)
                                                .replace("%3", p.getName()));
//					msgOwner(pOwner, "`rYou do not have enough money to buy "
//					    + workingAmount + " item(s) for your " + itemName 
//					    + " showcase. (Seller: " + p.getName() + ")"); //noMoney3: `rYou do not have enough money to buy %1 item(s) for your %2 showcase. (Seller**%3) 

					Messaging.send(p,Localization.get("noMoney2"));
//					Messaging.send(p,"`rSorry, the owner of this showcase can't afford to buy at this moment.");//noMoney2
			}
			break;


		case DISPLAY:
			info(p);
			break;
		}
                
                if(Properties.interactDebug)
                    bm.end();
	}


	/*
	 * Checks if there is already an item - server-crash?
	 */
	public boolean checkItem () {
		for (Entity e : location.getWorld().getEntities())
		{
			double x = e.getLocation().getX();
			double z = e.getLocation().getZ();
			double yDiff = location.getY() - e.getLocation().getY();
                        
			if (yDiff < 0)
				yDiff *= -1;

			if (x == location.getX() && yDiff <= 1.0 && z == location.getZ()) {
                            
                            ShowCaseStandalone.slog(Level.FINEST, "Potential hit on checkItem()");
				try {
					Item itemE = (Item)e;
					if (itemsEqual(itemE.getItemStack(), is)) {
                                                ShowCaseStandalone.slog(Level.FINEST, "Existing stack: " + itemE.getItemStack().toString());
                                                itemE.getItemStack().setAmount(1); //Removes duped items, which can occur.
						this.item = itemE;
						scs.log(Level.FINER, "Attaching to existing item.");
						return true;
					}
				} catch (Exception ex) {}
			}
		}
		return false;
	}

	/*
	 * Spawn Item
	 */
	public void show() {
		this.isVisible = true;
                
		if (location == null)
			return;

		if (this.checkItem())
			return;
                
		//ItemStack is = new ItemStack(material.getItemType(), 1, material.getItemType().getMaxDurability(), material.getData());
                //Location loc = location;
                //loc.add(0, 0.6, 0);
		item = location.getWorld().dropItem(location, is);
		item.setVelocity(new Vector(0, 0.1, 0));
	}

	/*
	 * Removes Item
	 */
	public void hide() {
		this.isVisible = false;
		if (item == null)
			return;
                
                ShowCaseStandalone.slog(Level.FINEST, "Hiding showcase: " + sha1);
                
		item.remove();
		item = null;
	}

	/*
	 * Hide or show current Item
	 */
	public void setVisible(boolean setVisible) {
		if (setVisible)
			this.show();
		else
			this.hide();
	}

	/*
	 * Returns a boolean value describing if the shop is visible
	 */
	public boolean isVisible() {
            return this.isVisible;
	}

        /*
         * Returns if the shop is active, i.e. does the sell shop have stuff to sell, does the buy shop have stuff to buy
         */
        public boolean isActive(){
            //unlimited always active.
            if(isUnlimit)
                return true;
            switch (this.activities){
                default: return true;
                case BUY: return (this.amount != this.maxAmount);
                case SELL: return (this.amount != 0);
            }
        }
        
	/*
	 * Set the amount of Items in the shop
	 */
	public void setAmount(int amount) {
            if (isUnlimit)
                    return;
            this.amount = amount;
	}
        
        /*
	 * Set the max amount of Items in the shop
	 */
	public void setMaxAmount(int amount) {
            if (isUnlimit)
                return;
            this.maxAmount = amount;
	}

	/*
	 * Set the Block for the shop
	 */
	public void setBlock(Block b) {
            this.block = b;
            this.chunk = b.getChunk();
            this.setLocation(b.getLocation());
	}

	/*
	 * Set the location for the shop
	 */
	public void setLocation(Location l) {
            this.location = l.add(0.5, 1, 0.5);
	}

	/*
	 * Set the price for one Item
	 */
	public void setPrice(double price) {
            this.price = price;
	}
	
	/*
	 * Sets the owner of a shop
	 */
	public void setOwner (String owner) {
            this.owner = owner;
	}

	/*
	 * Returns what the shop owner want to do
	 */
	public Activities getAtivitie() {
            return this.activities;
	}

	/*
	 * Returns the amount of Items in the shop
	 */
	public int getAmount() {
            return this.amount;
	}

	/*
	 * Returns the maximal amount of the shop
	 */
	public int getMaxAmount() {
            return this.maxAmount;
	}
        
        /**
         * Returns proper name of item
         */
        public String getItemName(){
            return this.itemName;
        }

	/*
	 * Returns the enchantments which is used by this shop
	 */
	public String getEnchantments() {
            StringBuilder sb = new StringBuilder();
            String delim = "";

            for(Map.Entry<Enchantment, Integer> entry : is.getEnchantments().entrySet()){
                sb.append(delim);
                sb.append(entry.getKey().getId());
                sb.append(":");
                sb.append(entry.getValue());
                delim=",";
            }
            return sb.toString();
	}
	
	public String getMaterial () {
            return is.getTypeId() + ":" + is.getDurability();
	}
	

	/*
	 * Returns the current Price
	 */
	public double getPrice() {
            return this.price;
	}

	/*
	 * Returns the owner of the shop
	 */
	public String getOwner() {
            return this.owner;
	}
        
        /*
         * Returns the owner as a Player object
         */
        public Player getPOwner() {
            return ShowCaseStandalone.get().getServer().getPlayer(getOwner());
        }
        
	/*
	 * Returns the location of the shop
	 */
	public Location getLocation() {
            return this.location;
	}

	/*
	 * Returns the Block of the shop
	 */
	public Block getBlock() {
            return this.block;
	}
        /*
         * Returns the chunk the shop is in.
         */
        public Chunk getChunk() {
            return this.chunk;
        }

	/*
	 * Returns the Item which is used by this shop
	 */
	public Item getItem() {
            return this.item;
	}
        
        /**
         * This removes item stacks one by one.  It does this to make sure that only 
         * items with the proper data field are removed.  This includes durability.
         * @param p The player whose inventory you're removing.
         * @param interactAmount The max amount to remove.
         * @return the number of items actually removed.
         */
        public final int safeRemoveItems(Player p, int interactAmount){
            //There is a chance that interactAmount == 0.  If so, just exit.
            
            if(interactAmount == 0)
                return 0;
            
            HashMap<Integer, ? extends ItemStack> it = (HashMap<Integer, ? extends ItemStack>)
                    p.getInventory().all(is.getTypeId());
            
            int needed = interactAmount;
            
            Iterator itr = it.entrySet().iterator();
            ItemStack invItem;
            int key;
            
            while (itr.hasNext()) {
                Map.Entry<Integer, ? extends ItemStack> pairs = (Map.Entry)itr.next();
                invItem = pairs.getValue();
                key = pairs.getKey();
                
                ShowCaseStandalone.slog(Level.FINER, "found itr: " + invItem.toString());
                
                if(itemsEqual(invItem, is)){
                    ShowCaseStandalone.slog(Level.FINER, "Found a match for data in slot " 
                            + key + ": " 
                            + MaterialNames.getItemName(invItem.getTypeId(), invItem.getDurability()) 
                            + "=" + itemName);
                    //ShowCaseStandalone.spam("invItem.getamount: " + invItem.getAmount());
                    if(invItem.getAmount() <= needed){
                        
                        ShowCaseStandalone.slog(Level.FINER, "invItem is <= needed (" + needed + ")");
                        
                        needed -= invItem.getAmount();
                        p.getInventory().clear(key);
                    } else {
                        
                        ShowCaseStandalone.slog(Level.FINER, "invItem is > needed (" + needed + ")");
                        ShowCaseStandalone.slog(Level.FINER, "Attempting to set inv position " + key + " to size " 
                                + (invItem.getAmount() - needed));
                        
                        invItem.setAmount(invItem.getAmount() - needed);
                        p.getInventory().setItem(key, invItem);
                        
                        needed = 0;
                    }
                    
                    if(needed == 0)
                        break;
                }
            }
            //ShowCaseStandalone.spam("returning: " + (interactAmount - needed));
            return (interactAmount - needed);
        }
        
        /**
         * Returns the number of items that exactly match the shop item.
         * @param p The player whose inventory we're looking at.
         */
        public final int countSaleableItems(Player p){
            HashMap<Integer, ? extends ItemStack> it = (HashMap<Integer, ? extends ItemStack>)
                    p.getInventory().all(is.getTypeId());
            int saleable = 0;
            
            Iterator itr = it.entrySet().iterator();
            ItemStack invItem;
            
            while (itr.hasNext()) {
                Map.Entry<Integer, ? extends ItemStack> pairs = (Map.Entry)itr.next();
                invItem = pairs.getValue();
                
                ShowCaseStandalone.slog(Level.FINER, "found itr: " + invItem.toString());
                
                if(itemsEqual(invItem, is)){
                    ShowCaseStandalone.slog(Level.FINER, "Found a match for data in slot " 
                                + pairs.getKey() + ": " 
                                + MaterialNames.getItemName(invItem.getTypeId(), invItem.getDurability()) 
                                + "=" + itemName);
                    saleable += invItem.getAmount();
                }
            }
            return saleable;
        }
        
        /**
         * Fills inventory with items from the shop, up to inventory 
         * capacity, or interactAmount.  Respects stack sizes.
         * @param p Player whose inventory we're filling.
         * @param interactAmount Max amount of items to place.  If set to 0,
         * the 
         * @return the actual number of items placed.
         */
        public final int safeAddItems(Player p, int interactAmount){
            //There is a chance that interactAmount == 0.  If so, just exit.
            //ShowCaseStandalone.slog(Level.INFO, "safeAddItems() started. interactamount: " + interactAmount);
            if(interactAmount == 0)
                return 0;

            Inventory inv = p.getInventory();
            int stackAmount = is.getType().getMaxStackSize();
            
            //Get the smaller of the two for stack size.  This will give us our largest legal stack size.
            stackAmount = (stackAmount < interactAmount) ? stackAmount : interactAmount;
            
            //Bukkit issue: Clone isn't working for unsafe enchantments.  Fixed in R4, but 
            //I'm keeping this work around for people on R3.
            ItemStack tmpIS;
            try {
                tmpIS = is.clone();
                tmpIS.setAmount(stackAmount);
            } catch (IllegalArgumentException iae) {
                tmpIS = new ItemStack(is.getTypeId(), stackAmount, 
                    is.getType().getMaxDurability(), is.getData().getData());
                tmpIS.addUnsafeEnchantments(is.getEnchantments());
            }
            
            HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
            int remainingItems = interactAmount;
            
            while(remainingItems > 0){
                leftOver = inv.addItem(tmpIS);
                if(leftOver.isEmpty()){
                    remainingItems -= stackAmount;
                    if(remainingItems == 0)
                        break;
                    stackAmount = (stackAmount < remainingItems) ? stackAmount : remainingItems;
                    tmpIS.setAmount(stackAmount);
                } else {
                    //We're out of room.
                    remainingItems -= (stackAmount - leftOver.get(0).getAmount());
                    break;
                }
            }
            //ShowCaseStandalone.slog(Level.INFO, "returning placed amount: " + (interactAmount - remainingItems));
            return (interactAmount - remainingItems);
        }
        
        /**
         * Returns whether the underlying item (TypeID, Durability and enchantments) are equal.
         * This is almost identical to .equals() for ItemStack, but ignores the amount in the stack.
         * @param is1
         * @param is2
         */
        private boolean itemsEqual(ItemStack is1, ItemStack is2){
            return (is1.getTypeId() == is2.getTypeId() 
                    && is1.getDurability() == is2.getDurability()
                    && is1.getEnchantments().equals(is2.getEnchantments()));
        }
        
        private void msgOwner(Player player, String msg){
            if(player != null)
                if(!ShowCaseStandalone.pv.ignoreMessages(player))
                    Messaging.send(player, msg);
        }
}
