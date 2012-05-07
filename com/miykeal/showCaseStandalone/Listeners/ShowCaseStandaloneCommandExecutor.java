package com.miykeal.showCaseStandalone.Listeners;

import com.miykeal.showCaseStandalone.Commands.*;
import com.miykeal.showCaseStandalone.Exceptions.InsufficientPermissionException;
import com.miykeal.showCaseStandalone.Exceptions.MissingOrIncorrectArgumentException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Localization;
import com.miykeal.showCaseStandalone.Utilities.Messaging;
import com.miykeal.showCaseStandalone.interfaces.Cmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
* Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class ShowCaseStandaloneCommandExecutor implements CommandExecutor {
	private ShowCaseStandalone scs;	
	private Cmd cmd;
	
	public ShowCaseStandaloneCommandExecutor (ShowCaseStandalone scs) {
		this.scs = scs;
	}
	
	@Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String mainArg;
            
            if(args.length < 1)
                mainArg = "help";
            else
                mainArg = args[0];
            
            try {
                
                //General commands
                if (mainArg.equalsIgnoreCase("abort"))
                    this.cmd = new AbortCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("help"))
                    this.cmd = new HelpCmd(sender, args);
                
                //Customer commands
                else if (mainArg.equalsIgnoreCase("last"))
                    this.cmd = new LastCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("undo"))
                    this.cmd = new UndoCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("unit"))
                    this.cmd = new UnitCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("message") || mainArg.equalsIgnoreCase("messages"))
                    this.cmd = new MessageCmd(sender, args);
                
                //Creation/deletion commands
                else if (mainArg.equalsIgnoreCase("buy"))
                    this.cmd = new BuyCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("display"))
                    this.cmd = new DisplayCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("remove"))
                    this.cmd = new RemoveCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("sell"))
                    this.cmd = new SellCmd(sender, args);
                
                //Management commands
                else if (mainArg.equalsIgnoreCase("add"))
                    this.cmd = new AddCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("get"))
                    this.cmd = new GetCmd(sender, args);
                else if(mainArg.equalsIgnoreCase("owner"))
                    this.cmd = new OwnerCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("price"))
                    this.cmd = new PriceCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("amount")) //Was "limit"
                    this.cmd = new AmountCmd(sender, args);
                
                //Admin commands
                else if (mainArg.equalsIgnoreCase("debug"))
                    this.cmd = new DebugCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("import"))
                    this.cmd = new ImportCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("clear"))
                    this.cmd = new ClearCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("reload"))
                    this.cmd = new ReloadCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("disable"))
                    this.cmd = new DisableCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("enable")) //Alias for reload
                    this.cmd = new ReloadCmd(sender, args);
                else if (mainArg.equalsIgnoreCase("prune") || mainArg.equalsIgnoreCase("cleanup"))
                    this.cmd = new PruneCmd(sender, args);
                
                
                //Unknown
                else
                    throw new MissingOrIncorrectArgumentException(Localization.get("unknownCommand"));
//                    throw new MissingOrIncorrectArgumentException("Unknown command. See /scs help."); //msg: unknownCommand
                
                return cmd.execute();
                
            } catch (MissingOrIncorrectArgumentException miae) {
                Messaging.send(sender, "`r" + miae.getMessage());
                return true;
            } catch (InsufficientPermissionException nperm) {
                Messaging.send(sender, "`r" + nperm.getMessage());
                return true;
            }
        }
}