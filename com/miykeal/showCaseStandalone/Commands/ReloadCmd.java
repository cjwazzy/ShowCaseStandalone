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
public class ReloadCmd extends GenericCmd {
    
    public ReloadCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.mustBePlayer = false;
        this.permission = Properties.permAdmin;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        //Disable first, then reload, then re-enable.
        ShopHandler sh = ShowCaseStandalone.get().getShopHandler();
        
        try {
                Messaging.send(cs, Localization.get("reloading").replace("%1", "config")); //msg: reloading %1
                scs.reloadConfig();
                scs.loadSCSConfig(scs.getConfig());
                
                Messaging.send(cs, Localization.get("reloading").replace("%1", "SCS"));//msg: reloading %1
                ShowCaseStandalone.slog(Level.INFO, "Reloading SCS (command from " + cs.getName());
                ShowCaseStandalone.slog(Level.INFO, "Stopping shop update task.");
    		sh.stop();
                ShowCaseStandalone.slog(Level.INFO, "Removing display items.");
	    	sh.hideAll();
                
                ShowCaseStandalone.slog(Level.INFO, "Reloading shops from storage.");
                sh.reload();
                ShowCaseStandalone.slog(Level.INFO, "Starting shop update task.");
                sh.start();
                ShowCaseStandalone.slog(Level.INFO, "Showing display items in loaded chunks.");
                sh.showAll();
                
    	} catch (Exception ioe) {
    		ShowCaseStandalone.slog(Level.WARNING, "Exception on reload: " + ioe.getLocalizedMessage());
                Messaging.send(cs, Localization.get("generalError").replace("%1", "reloading") + ioe.getLocalizedMessage());//msg: generalError %1 (reloading)
    	}
        
        return true;
    }   
}
