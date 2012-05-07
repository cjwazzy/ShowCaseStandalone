package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.ShopHandler;
import java.util.logging.Level;
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
public class DisableCmd extends GenericCmd {
    
    public DisableCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.mustBePlayer = false;
        this.permission = Properties.permAdmin;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        ShopHandler sh = ShowCaseStandalone.get().getShopHandler();
        
        try {
                ShowCaseStandalone.slog(Level.INFO, cs.getName() + " disabled SCS.");
                ShowCaseStandalone.slog(Level.INFO, "Stopping shop update/save tasks.");
    		sh.stop();
                ShowCaseStandalone.slog(Level.INFO, "Removing display items.");
	    	sh.hideAll();
                sh.unload();
                
                Messaging.send(cs, Localization.get("disable")); //msg: disable
    	} catch (Exception ioe) {
    		ShowCaseStandalone.slog(Level.WARNING, "Exception on onDisable: " + ioe.getLocalizedMessage());
                Messaging.send(cs, Localization.get("generalError").replace("%1", "disabling") + ioe.getLocalizedMessage());//msg: generalError %1 (disabling)
    	}
        
        return true;
    }   
}
