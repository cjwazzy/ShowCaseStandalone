package com.miykeal.showCaseStandalone.ShopInternals;

import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
* Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>, Sorklin <sorklin@gmail.com>
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

public class Transaction {
    
    Activities activity;
    Player player;
    Shop shop;
    int quantity;
    double price;
    long time;
    public String returnMessage; 
    
    public Transaction(Player player, Shop shop, int quantity){
        this.player = player;
        this.shop = shop;
        this.time = System.currentTimeMillis();
        this.quantity = quantity;
        returnMessage = "";
        
        price = this.shop.getPrice();
        this.activity = this.shop.getAtivitie();
    }
    
    /**
     * Undoes a transaction, if the transaction falls within the allowed time,
     * both players have enough money, and items to undo the transaction.
     * @return True the transaction was rolled back.  False it was not rolled back.
     */
    public boolean undo(){
        //Lets check for problems before we undo the transaction.
        if((System.currentTimeMillis() - time) > Properties.maxUndoTime){
            returnMessage = Localization.get("undoExpired");
//            returnMessage = "`rThe undo grace period has expired. This transaction cannot be undone.";//msg: undoExpired
            return false;
        }
        
        if(this.quantity == 0){
            returnMessage = Localization.get("undoFail");
//            returnMessage = "`rCannot undo a transaction twice.";//undoFail
            return false;
        }
            
        
        Balance balance = ShowCaseStandalone.get().getBalanceHandler();
//        Material item = shop.getMaterialData().getItemType();
//        Inventory inv = player.getInventory();
//        ItemStack is = new ItemStack(item, quantity);
        List<String> msg;
        
        switch(this.activity){
            case BUY:
                //Player sold something.  So check:
                //  Player has enough money to undo.
                //  Showcase has enough items to undo.
                
                if(!balance.hasEnough(player.getName(), price * quantity)){
                    returnMessage = Localization.get("undoMoneyBuyFail");
//                    returnMessage = "`rYou do not have enough money to undo that transaction.";//msg: undoMoneyBuyFail
                    return false;
                }
                
                if(shop.getAmount() < quantity && !shop.isUnlimited()){
                    returnMessage = Localization.get("undoItemBuyFail");
//                    returnMessage = "`rThe showcase does not have enough items to undo that transaction.";//undoItemBuyFail
                    return false;
                }
                
                //If the player doesn't have the room, tough luck.  Put as many in as possible.
                shop.safeAddItems(player, quantity);
                shop.setAmount(shop.getAmount() - quantity);
                //Don't need to credit unlimited shop's account.
                if(!shop.isUnlimited())
                    balance.add(shop.getOwner(), price * quantity);
                
                balance.sub(player, price * quantity);
                
                msg = this.info();
                msg.set(0, Localization.get("undoSuccess"));
//                msg.set(0, "`GSuccessfully undid the following transaction:");//undoSuccess
                Messaging.mlSend(player, msg);
                
                this.quantity = 0; //So that we can't do this again.
                this.price = 0;
                break;
                
            case SELL:
                //We need to check a few things before undoing the transaction:
                //  Since the player bought something, does the shop owner still have that money?
                //  Does the player still have the items?
                if(!shop.isUnlimited() && !balance.hasEnough(shop.getOwner(), price * quantity)){
                    returnMessage = Localization.get("undoMoneySellFail");
//                    returnMessage = "`rThe showcase owner does not have enough money to undo this transaction.";//undoMoneySellFail
                    return false;
                }
                
                if(shop.countSaleableItems(player) < quantity){
                    //ShowCaseStandalone.spam("Not enough items? Item:" + item + " quantity: " + quantity);
                    returnMessage = Localization.get("undoItemSellFail");
//                    returnMessage = "`rYou no longer have enough saleable items to undo this transaction.";//undoItemSellFail
                    return false;
                }
                
                int removed = shop.safeRemoveItems(player, quantity);
                if(removed != quantity){
                    //We should never be here, because we already counted saleable items.
                    //But i'm including this just in case my logic has problems.
                    //ShowCaseStandalone.spam("Removed: " + removed + " != quantity: " + quantity);
                    //Put em back.
                    shop.safeAddItems(player, removed);
                    Messaging.send(player, Localization.get("undoUnknownFail"));
//                    Messaging.send(player, "`rUmmmm... could not undo.");//undoUnknownFail
                    return false;
                }
                
                //Remember, if the shop is unlimited, not to do any amount or balance changes.
                if(!shop.isUnlimited())
                    shop.setAmount(shop.getAmount() + quantity);
                balance.add(player, price * quantity);
                
                if(!shop.isUnlimited())
                    balance.sub(shop.getOwner(), price * quantity);
                
                msg = this.info();
                msg.set(1, Localization.get("undoSuccess"));
//                msg.set(1, "`GSuccessfully undid the following transaction:");//undoSuccess
                Messaging.mlSend(player, msg);
                
                this.quantity = 0; //So that we can't do this again.
                this.price = 0;
                break;
        }
        return true;
    }
    
    /**
     * Displays info about the last transaction.
     */
    public List<String> info(){
        List<String> msg = new ArrayList<String>();
        Balance balance = ShowCaseStandalone.get().getBalanceHandler();
        if(quantity == 0){
            msg.add(Localization.get("undoInfo1"));
//            msg.add("`yNo transaction recorded.");//undoInfo1
        } else {
            long curTime = System.currentTimeMillis();
            long timeLeft = Properties.maxUndoTime - (curTime - time);
            msg.add(Localization.get("undoInfo2"));
//            msg.add("`G---------------------------------------------------");//undoInfo2
            msg.add("`GTransaction:");//undoInfo3
//            msg.add("`GTransaction:");//undoInfo3
            
            msg.add(Localization.get("undoInfo4")
                    .replace("%1", player.getName())
                    .replace("%2", shop.getOwner()));
//            msg.add("`GClient: `Y" + player.getName() + "  `GOwner: `Y" + shop.getOwner());//undoInfo4: `GClient**`Y%1  `GOwner**`Y%2
            
            msg.add(Localization.get("undoInfo5")
                    .replace("%1", activity.toString())
                    .replace("%2", shop.getItemName())
                    .replace("%3", String.valueOf(quantity)));
//            msg.add("`GActivity: `Y" + activity + " `GItem: `Y" + shop.getItemName() +  " `GQuantity: `Y" + quantity);//undoInfo5: `GActivity**`Y%1 `GItem**`Y%2 `GQuantity**`Y%3
            
            msg.add(Localization.get("undoInfo6")
                    .replace("%1", balance.format(price))
                    .replace("%2",  balance.format(price * quantity)));
//            msg.add("`GUnit Price: `Y" + balance.format(price) + " `GTransaction amount: `Y" + balance.format(price * quantity));//undoInfo6: `GUnit Price**`Y%1 `GTransaction amount**`Y%2
            
            if(timeLeft > 0) {
                msg.add(Localization.get("undoInfo7")
                        .replace("%1", String.valueOf((Properties.maxUndoTime - (curTime - time)) / 1000)));
//                msg.add("`GTime left to undo this transaction: `Y" + 
//                        ((Properties.maxUndoTime - (curTime - time)) / 1000) + " `Gseconds.");//undoInfo7: `GTime left to undo this transaction**`Y%1 `Gseconds.
            } else {
                msg.add(Localization.get("undoInfo8"));
//                msg.add("`GTransaction cannot be undone (time expired).");//undoInfo8
            }
            msg.add(Localization.get("undoInfo2"));
//            msg.add("`G---------------------------------------------------");//undoInfo2
        }
        return msg;
    }
            
}
