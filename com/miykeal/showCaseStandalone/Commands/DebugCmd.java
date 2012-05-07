package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.Utilities.*;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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

 * @author Sorklin <sorklin at gmail.com>
 */
public class DebugCmd extends GenericCmd {
    
    public DebugCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permAdmin;
        this.minArg = 2;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        if(args[1].equalsIgnoreCase("mat")){
            ItemStack is = player.getItemInHand();
            Messaging.send(player, "Material: " + is.getTypeId() + ":" + is.getDurability());
            Messaging.send(player, "Full name: " + MaterialNames.getItemName(is.getTypeId(), is.getDurability()));
        }
        
        else if(args[1].equalsIgnoreCase("is")){
            ItemStack is = Utilities.getItemStack(player, "this");
            Messaging.send(player, "Is: " + is.toString());
            Messaging.send("amt: " + is.getAmount());
        }
        
        else if(args[1].equalsIgnoreCase("ench")){
            ItemStack is = player.getItemInHand();
            Messaging.send(player, is.getEnchantments().toString());
        }
        
        else if(args[1].equalsIgnoreCase("e")){
            StringBuilder sb = new StringBuilder();
                    String delim = "";
                    
                    for(Map.Entry<Enchantment, Integer> entry : player.getItemInHand().getEnchantments().entrySet()){
                        sb.append(delim);
                        sb.append(entry.getKey().getId());
                        sb.append(":");
                        sb.append(entry.getValue());
                        delim=",";
                    }
                    Messaging.send(player, sb.toString());
        }
        
        else if(args[1].equalsIgnoreCase("ch")){
            Messaging.send(player, "Chunk: " + player.getLocation().getChunk().toString());
        }
        
        else if(args[1].equalsIgnoreCase("locale")){
            Localization.dumpMessages();
        }
        
        return true;
    }   
}
