package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
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
public class UnitCmd extends GenericCmd {
    
    public UnitCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.permission = Properties.permUse;
    }

    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;
        
        if(args.length > 1){
            int unit = 0;
            
            try { 
                unit = Integer.parseInt(args[1]);  
            } catch (Exception e) {}
            
            if(unit < 1)
                throw new MissingOrIncorrectArgumentException();

            ShowCaseStandalone.pv.setPlayerTransactionAmount(player, unit);
            Messaging.send(player, Localization.get("setUnit") + unit);//msg: setUnit
//            Messaging.send(player, "`yPurchase unit set to " + unit);//msg: setUnit
            
        } else
            Messaging.send(player, Localization.get("displayUnit").replace("%1", 
                    String.valueOf(ShowCaseStandalone.pv.getPlayerTransactionAmount(player))));//msg: displayUnit %1 (unit amount)
//            Messaging.send(player, "`yYou will buy " + ShowCaseStandalone.pv.getPlayerTransactionAmount(player)
//                    + " items when you sneak and right-click");//msg: displayeUnit %1 (unit amount)
        
        return true;
    }
}
