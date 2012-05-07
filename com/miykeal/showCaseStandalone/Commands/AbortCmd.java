package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import org.bukkit.command.CommandSender;

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
public class AbortCmd extends GenericCmd {
    
    public AbortCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permUse;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        scs.removeTodo(player);
        Messaging.send(player, Localization.get("abort")); //Messages: abort
//        Messaging.send(player, "`yShowcase operation aborted."); //Messages: abort
        
        return true;
    }
}
