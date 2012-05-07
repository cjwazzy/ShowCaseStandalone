package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.ShopHandler;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
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
public class PruneCmd extends GenericCmd {
    
    public PruneCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.mustBePlayer = false;
        this.permission = Properties.permAdmin;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        //Purge command will empty the mini file, and then replace with whatever is loaded.
        //
        ShopHandler sh = ShowCaseStandalone.get().getShopHandler();
        ShopStorage st = ShowCaseStandalone.get().getShopStorage();
        
        try {
                Messaging.send(cs, Localization.get("prune"));
                ShowCaseStandalone.slog(Level.INFO, "Remove all shops from storage.");
                st.removeAllShops();

                ShowCaseStandalone.slog(Level.INFO, "Saving all currently loaded shops.");
                sh.saveAll();
                
    	} catch (Exception ioe) {
    		ShowCaseStandalone.slog(Level.WARNING, "Exception on prune: " + ioe.getLocalizedMessage());
                Messaging.send(cs, Localization.get("generalError").replace("%1", "pruning") + ioe.getLocalizedMessage());//msg: generalError %1 (reloading)
    	}
        
        return true;
    }   
}
