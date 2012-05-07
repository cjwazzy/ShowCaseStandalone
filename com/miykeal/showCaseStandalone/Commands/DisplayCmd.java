package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Todo;
import com.miykeal.showCaseStandalone.ShopInternals.Todo.Types;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.Utilities.Utilities;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

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
public class DisplayCmd extends GenericCmd {
    
    public DisplayCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permCreateDisplay;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        if(Properties.blacklistedWorlds.contains(player.getWorld().getName())){
            Messaging.send(player, Localization.get("blacklistError")); //msg: blacklistError
//            Messaging.send(player, "`rYou are not allowed to create a showcase in this world."); //msg: blacklistError
            return true;
        }
        
        //MaterialData mat = null;
        ItemStack is = null;
        
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
            }
            
        } catch (Exception e) {
            throw new MissingOrIncorrectArgumentException ();
        }

        if (is == null || is.getTypeId() == 0)
            throw new MissingOrIncorrectArgumentException (Localization.get("missingItem")); //msg: missingItem
//            throw new MissingOrIncorrectArgumentException ("The item you specified does not exist."); //msg: missingItem
        
        try {
            //Since these are going to be our unique keys for shops, they cannot be random.
            //We can do a random one, until they hit the block, and then it will be replaced by a 
            //hash of the full location string.
            String sha1 = Utilities.getRandomSha1( player.getName() ); 
            Shop p = new Shop (sha1, scs, Shop.Activities.DISPLAY, 0, 0, is.clone(), 0, player.getName());
            if(Properties.requireObjectToDisplay && p.countSaleableItems(player) == 0){
                Messaging.send(player, Localization.get("requiredObject"));//requiredObject
//                Messaging.send(player, "`RYou are required to own the object you want to display.");//requiredObject
                return true;
            } else
                scs.addTodo(player, new Todo (player, Types.CREATE, p, 0, null));
            
        } catch (IOException ioe) {
            Messaging.send(player, "`rError: "+ioe); //msg: Error
        }
        
        double createPrice = Properties.displayCreatePrice;
        if (createPrice > 0.0) 
            Messaging.send(player, Localization.get("buyShopCreatePrice") + scs.formatCurrency(createPrice));	//msg: buyShopCreatePrice		
//            Messaging.send(player, "`gCreating a display will cost you `Y" + scs.formatCurrency(createPrice));	//msg: buyShopCreatePrice		
        
        Messaging.send(player, next);
        
        return true;
    }
}