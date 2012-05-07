package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Storage.FileShopStorage;
import com.miykeal.showCaseStandalone.Storage.MiniShopStorage;
import com.miykeal.showCaseStandalone.Storage.ShowCaseImport;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import java.io.IOException;
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
public class ImportCmd extends GenericCmd {
    
    public ImportCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.mustBePlayer = false;
        this.permission = Properties.permAdmin;
        this.minArg = 2;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        ShopStorage current = scs.getShopStorage();
                    
        if(args[1].equalsIgnoreCase("file")){
            if(!(current instanceof FileShopStorage)){
                ShowCaseStandalone.slog(Level.INFO, "Import File shop storage.");
                try {
                    scs.getShopHandler().importStorage(new FileShopStorage(scs), current);
                } catch (IOException ioe) {
                    ShowCaseStandalone.slog(Level.INFO, "IOError: could not import from file.");
                    Messaging.send(cs, Localization.get("importError").replace("%1", "file"));//msg: importError %1
//                    Messaging.send(cs, "Error: could not import from file.");//msg: importError %1
                }
            } else {
                Messaging.send(cs, Localization.get("usingError").replace("%1", "file"));//msg: usingError %1
//                Messaging.send(cs, "Error: already using file storage.");//msg: usingError %1
            }
        }
        
        else if(args[1].equalsIgnoreCase("minidb")){
            if(!(scs.getShopStorage() instanceof MiniShopStorage)){
                ShowCaseStandalone.slog(Level.INFO, "Import MiniDB shop storage.");
                try {
                    scs.getShopHandler().importStorage(new MiniShopStorage(scs), current);
                } catch (IOException ioe) {
                    ShowCaseStandalone.slog(Level.INFO, "IOError: could not import from minidb.");
                    Messaging.send(cs, Localization.get("importError").replace("%1", "MiniDB"));//msg: importError %1
//                    Messaging.send(cs, "Error: could not import from minidb.");//msg: importError %1
                }
            } else {
                Messaging.send(cs, Localization.get("usingError").replace("%1", "MiniDB"));//msg: usingError %1
//                Messaging.send(cs, "Error: already using MiniDB storage.");//msg: usingError %1
            }
        }
        
        else if(args[1].equalsIgnoreCase("showcase")){
            ShowCaseStandalone.slog(Level.INFO, "Import Showcase shops.");
            ShowCaseImport si = new ShowCaseImport(scs);
            if(!si.fileExists()){
                Messaging.send(cs, "Could not attach to showcases.csv.  Is it in your ShowCaseStandalone data folder?");
                return true;
            }
            Shop[] shps = si.loadshops();
            if(shps == null){
                Messaging.send(cs, "Could not parse showcases.csv.");
                return true;
            }
            int count = 0;
            for (Shop s : shps){
                scs.getShopHandler().addShop(s);
                try{
                    scs.getShopHandler().save(s);
                } catch (IOException ioe) {
                    ShowCaseStandalone.slog(Level.WARNING, "Could not save shop " + s.getSHA1());
                    continue;
                }
                count++;
            }
            Messaging.send(cs, "Imported " + count + " shops.");
        }

        //add DB when ready

        else 
            throw new MissingOrIncorrectArgumentException();
        
        return true;
    }
}
