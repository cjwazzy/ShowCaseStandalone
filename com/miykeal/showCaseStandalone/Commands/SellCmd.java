package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShopInternals.Todo;
import com.miykeal.showCaseStandalone.ShopInternals.Todo.Types;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.Utilities.Utilities;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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

/**
 * @author Sorklin <sorklin at gmail.com>
 */
public class SellCmd extends GenericCmd {
    
    public SellCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permCreateSell;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        if(Properties.blacklistedWorlds.contains(player.getWorld().getName()))
            throw new InsufficientPermissionException(Localization.get("blacklistError"));//msg: blacklistError
//            throw new InsufficientPermissionException("`rYou are not allowed to create a showcase in this world.");//msg: blacklistError
                
        //Default values:
        int amount = Integer.MAX_VALUE;
        double price = 1.0;
        //MaterialData mat = null;
        ItemStack is = null;

        /* Using keyword "this" will attempt to sell whats in your hand.  
         *      ie. default values for material and amount are what's in your hand.
         * 
         * Otherwise default value for amount is everything in your inventory.
         * 
         * Valid forms of command:
         * 1: scs sell  -- [assumes "this"] -- item in hand, amount in hand, price = 1.0
         * 2: scs sell [item/"this"] -- item in hand or specified item, amount in hand or evrything in inventory, price = 1.0
         * 3: scs sell [item/"this"] [price] -- item in hand, amount in hand, price
         * 4: scs sell [item/"this"] [amount] [price]
         */
        
        //BUG: sell (no this) takes entire stack, but only sells one
        
        
        //We have optional number of arguments.  Lets parse through them.
        try {
        
            switch (args.length){
                case 0:
                case 1:
                    is = Utilities.getItemStack(player, "this");
                    amount = is.getAmount();
                    break;
                    
                case 2:
                    is = Utilities.getItemStack(player, args[1]);
                    if(args[1].equalsIgnoreCase("this"))
                        amount = is.getAmount();
                    break;
                    
                case 3:
                    is = Utilities.getItemStack(player, args[1]);
                    if(args[1].equalsIgnoreCase("this"))
                        amount = is.getAmount();
                    price = Double.parseDouble(args[2]);
                    break;
                    
                case 4:
                    is = Utilities.getItemStack(player, args[1]);
                    if (args[2].equalsIgnoreCase("unlimited"))
                        amount = -1;
                    else 
                        amount = Integer.parseInt(args[2]);
                    price = Double.parseDouble(args[3]);
                    break;
            }
            
        } catch (Exception e) {
            throw new MissingOrIncorrectArgumentException ();
        }
        
        if (is == null || is.getTypeId() == 0)
            throw new MissingOrIncorrectArgumentException (Localization.get("missingItem")); //msg: missingItem
//            throw new MissingOrIncorrectArgumentException ("The item does not exist"); //msg: missingItem

        //Blacklist or whitelisted items
        if(!Properties.sellList.isEmpty() && !scs.hasPermission(player, Properties.permAdmin)) {
            MaterialData md = is.getData();
            if (Properties.sellBlackList && Properties.sellList.contains(md)
                        ||
               (!Properties.sellBlackList && !Properties.sellList.contains(md)))
                    throw new InsufficientPermissionException(Localization.get("blacklistedItem"));
        }
        
        if (price < 0)
            throw new MissingOrIncorrectArgumentException (Localization.get("negativePrice"));//msg: negativePrice
//            throw new MissingOrIncorrectArgumentException ("Price can't be negative.");//msg: negativePrice

        if (amount < 0 && !scs.hasPermission(player, Properties.permCreateUnlimited))
            throw new InsufficientPermissionException();

        if (Properties.sellShopCreatePrice > 0.0)
            Messaging.send(player, Localization.get("buyShopCreatePrice") + scs.formatCurrency(Properties.sellShopCreatePrice));//msg: buyShopCreatePrice
//            Messaging.send(player, "`gCreating a shop will cost you `Y" + scs.formatCurrency(Properties.sellShopCreatePrice));//msg: buyShopCreatePrice %1 %2 (shop, amount)
                
        Messaging.send(player, next);

        //Since these are going to be our unique keys for shops, they cannot be random.
        //We can do a random one, until they hit the block, and then it will be replaced by a 
        //hash of the full location string.
        try {
            String sha1 = Utilities.getRandomSha1(player.getName()); 
            Shop p = new Shop (sha1, scs, Activities.SELL, amount, 0, is.clone(), price, player.getName());

            scs.addTodo(player, new Todo (player, Types.CREATE, p, amount, null));
        } catch (IOException ioe) {
            Messaging.send(player, Localization.get("error") +ioe);//msg: error
        }
        
        return true;
    }
}
