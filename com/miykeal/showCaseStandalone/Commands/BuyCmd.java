package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShopInternals.Todo;
import com.miykeal.showCaseStandalone.ShopInternals.Todo.Types;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
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
public class BuyCmd extends GenericCmd {
    
    public BuyCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permCreateBuy;       
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;

        if(Properties.blacklistedWorlds.contains(player.getWorld().getName()))
            throw new InsufficientPermissionException("`rYou are not allowed to create a showcase in this world."); //msg: blacklistError
        
        //Default values:
        int amount = 1;
        double price = 1.0;
        //MaterialData mat = null;
        ItemStack is = null;

        /* Using keyword "this" will attempt to sell whats in your hand.  
         *      ie. default values for material and amount are what's in your hand.
         * 
         * Otherwise default value for amount is 1.
         * 
         * Valid forms of command:
         * 1: scs buy  -- item in hand, amount in hand, price = 1.0
         * 2: scs buy [item/"this"]
         * 3: scs buy [item/"this"] [amount]
         * 4: scs buy [item/"this"] [amount] [price]
         */

        //We have optional number of arguments.  Lets parse through them.
        try {
        
            switch (args.length){
                case 0:
                case 1:
                    is = Utilities.getItemStack(player, "this");
                    break;
                    
                case 2:
                    is = Utilities.getItemStack(player, args[1]);
                    break;
                    
                case 3:
                    is = Utilities.getItemStack(player, args[1]);
                    if (args[2].equalsIgnoreCase("unlimited"))
                        amount = -1;
                    else 
                        amount = Integer.parseInt(args[2]);
                    break;
                    
                case 4:
                    is = Utilities.getItemStack(player, args[1]);
                    if (args[2].equalsIgnoreCase("unlimited"))
                        amount = -1;
                    else 
                        amount = Integer.parseInt (args[2]);
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
        if(!Properties.buyList.isEmpty() && !scs.hasPermission(player, Properties.permAdmin)) {
            MaterialData md = is.getData();
            if (Properties.buyBlackList && Properties.buyList.contains(md)
                        ||
               (!Properties.buyBlackList && !Properties.buyList.contains(md)))
                    throw new InsufficientPermissionException(Localization.get("blacklistedItem"));
        }

        if (price < 0)
            throw new MissingOrIncorrectArgumentException (Localization.get("negativePrice"));//msg: negativePrice
//            throw new MissingOrIncorrectArgumentException ("The price can't be negative.");//msg: negativePrice

        if (amount < 0 && !scs.hasPermission(player, Properties.permCreateUnlimited))
            throw new InsufficientPermissionException();

        if (Properties.buyShopCreatePrice > 0.0)
            Messaging.send(player, Localization.get("buyShopCreatePrice") + scs.formatCurrency(Properties.buyShopCreatePrice)); //msg:buyShopCreatePrice 
                
        Messaging.send(player, next);

        //Since these are going to be our unique keys for shops, they cannot be random.
        //We can do a random one, until they hit the block, and then it will be replaced by a 
        //hash of the full location string.
        try {
            String sha1 = Utilities.getRandomSha1(player.getName());
            Shop p = new Shop (sha1, scs, Activities.BUY, 0, amount, is.clone(), price, player.getName());

            scs.addTodo(player, new Todo (player, Types.CREATE, p, 0, null));
        } catch (IOException ioe) {
            Messaging.send(player, Localization.get("error") +ioe); //msg: error
        }

        return true;
    }
}
