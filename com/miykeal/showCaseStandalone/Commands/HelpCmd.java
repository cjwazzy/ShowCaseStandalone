package com.miykeal.showCaseStandalone.Commands;

import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import java.util.ArrayList;
import java.util.List;
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
public class HelpCmd extends GenericCmd {
    
    public HelpCmd(CommandSender cs, String args[]){
        super(cs, args);
        this.mustBePlayer = false;
        this.permission = Properties.permUse;
    }
    
    @Override
    public boolean execute() throws MissingOrIncorrectArgumentException, InsufficientPermissionException {
        if(errorCheck())
            return true;

        /*
         * This needs to be fixed/rewritten after debugging new buy/sell/display routines.
         */
        String page;
        if(args.length < 2)
            page = "1";
        else
            page = args[1].toLowerCase();
        
        List<String> msg = new ArrayList<String>();
        
        if(page.equalsIgnoreCase("admin")){
            if(scs.hasPermission(cs, Properties.permAdmin)){
                msg.add(Localization.get("helpAdminTitle"));//msg: helpAdminTitle
                msg.add(Localization.get("helpAdmin1"));//helpAdmin1
                msg.add(Localization.get("helpAdmin2"));//helpAdmin2
                msg.add(Localization.get("helpAdmin3"));//helpAdmin3
                msg.add(Localization.get("helpAdmin4"));//helpAdmin4
                msg.add(Localization.get("helpAdmin5"));//helpAdmin5
            } else
                throw new InsufficientPermissionException();
        } else if(page.equalsIgnoreCase("2")) {
            msg.add(Localization.get("helpTitle").replace("%1", "2"));//helpTitle %1
            msg.add(Localization.get("help16"));//help16
            msg.add(Localization.get("help17"));//help17
            msg.add(Localization.get("help18"));//help18
            msg.add(Localization.get("help19"));//help19
            msg.add(Localization.get("help20"));//help20
            msg.add(Localization.get("help21"));//help21
            msg.add(Localization.get("help22"));//help22
        } else {
            msg.add(Localization.get("helpTitle").replace("%1", "1"));//helpTitle %1
            msg.add(Localization.get("help1"));//help1
            msg.add(Localization.get("help2"));//help2
            msg.add(Localization.get("help3"));//help3
            msg.add(Localization.get("help4"));//help4
            msg.add(Localization.get("help5"));//help5
            msg.add(Localization.get("help6"));//help6
            msg.add(Localization.get("help7"));//help7
            msg.add(Localization.get("help8"));//help8
            msg.add(Localization.get("help9"));//help9
            msg.add(Localization.get("help10"));//help10
            msg.add(Localization.get("help11"));//help11
            msg.add(Localization.get("help12"));//help12
            msg.add(Localization.get("help13"));//help13
            msg.add(Localization.get("help14"));//help14
            msg.add(Localization.get("help15"));//help15
            if(scs.hasPermission(cs, Properties.permAdmin))
                msg.add(Localization.get("help23"));//help23
            msg.add(Localization.get("help24"));//help24
        }
//        if(page.equalsIgnoreCase("admin")){
//            if(scs.hasPermission(cs, Properties.permAdmin)){
//                msg.add("`B<|-----------  `bShow`YCase`RStandalone `gAdmin Help  `B----------|>");//msg: helpAdminTitle
//                msg.add("`g/scs clear `G- Temporarily clear the floating shop items.");//helpAdmin1
//                msg.add("`g/scs disable `G- Disable all of the shops.");//helpAdmin2
//                msg.add("`g/scs enable `G- Enable all of the shops.");//helpAdmin3
//                msg.add("`g/scs import [file/minidb] `G- Import shops from file/minidb.");//helpAdmin4
//                msg.add("`g/scs reload `G- Reload all the shops from storage method.");//helpAdmin5
//            } else
//                throw new InsufficientPermissionException();
//        } else if(page.equalsIgnoreCase("2")) {
//            msg.add("`B<|------------  `bShow`YCase`RStandalone `gHelp 2/2 `B-----------|>");//helpTitle %1
//            msg.add("`YShowCase use:");//help16
//            msg.add("`g/scs last `G- show information about your last transaction.");//help17
//            msg.add("`g/scs undo `G- undo your last transaction, if possible.");//help18
//            msg.add("`g/scs unit {amount} `G- sets/shows the number of items you will");//help19
//            msg.add("`Gbuy/sell when using sneak (shift) right-click.");//help20
//            msg.add("`g/scs message {receive|ignore} `G- Ignore or receive");//help21
//            msg.add("`Gtransaction messages for your shops.");//help22
//        } else {
//            msg.add("`B<|------------  `bShow`YCase`RStandalone `gHelp 1/2 `B-----------|>");//helpTitle %1
//            msg.add("`YShowCase management:");//help1
//            msg.add("`GUse the keyword `ythis `Gto indicate the item in your hand.");//help2
//            msg.add("`g/scs buy {item/this} {amount} {price} `G- create a showcase.");//help3
//            msg.add("`GIf omitted, amount will equal 1, and price will equal 1.0");//help4
//            msg.add("`g/scs sell {item/this} {amount} {price} `G- create a showcase.");//help5
//            msg.add("`GIf omitted, amount will equal amount you are holding, and the");//help6
//            msg.add("`Gprice will be equal 1.0.");//help7
//            msg.add("`g/scs display {item/this} `G- create a display showcase.");//help8
//            msg.add("`g/scs add {amount} `G- add items to a showcase.");//help9
//            msg.add("`g/scs get {amount} `G- remove items from a showcase.");//help10
//            msg.add("`g/scs price {amount} `G- show/set the price of a showcase.");//help11
//            msg.add("`g/scs owner {owner} `G- show/set the owner of a showcase.");//help12
//            msg.add("`g/scs amount {amount} `G- show/set the maximum a showcase will buy.");//help13
//            msg.add("`g/scs remove `G- remove a showcase");//help14
//            msg.add("`g/scs abort  `G- abort current operation");//help15
//            if(scs.hasPermission(cs, Properties.permAdmin))
//                msg.add("`g/scs help admin `G- show admin commands.");//help23
//            msg.add("`g/scs help 2 `G- show second page.");//help24
//        }
        
        Messaging.mlSend(cs, msg);
        return true;
    }
}
