package com.miykeal.showCaseStandalone.Balance;

import com.iConomy.iConomy;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import java.util.logging.Level;
import org.bukkit.entity.Player;

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

public class iConomy5Balance implements Balance {
	private iConomy 			iconomy;
	private ShowCaseStandalone	scs;
	
	public iConomy5Balance (ShowCaseStandalone scs, iConomy iconomy) {
		//super (scs);
		this.scs	= scs;
	}
	
        @Override
	public boolean hasEnough (String p, double amount) {
		try {
			return iConomy.getAccount(p).getHoldings().hasEnough(amount);
		} catch (NullPointerException npe) {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] NullPointerException: "+npe.getMessage());
			return false;
		}
	}
	
        @Override
	public boolean isEnabled () {
		return iconomy.isEnabled();
	}
	
        @Override
	public void add (Player p, double amount) {
		try {
			this.add(p.getName(), amount);
		} catch (NullPointerException npe) {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] NullPointerException: "+npe.getMessage());
		}
	}
	
        @Override
	public void add (String p, double amount) {
		try {
			iConomy.getAccount(p).getHoldings().add(amount);
		} catch (NullPointerException npe) {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] NullPointerException: "+npe.getMessage());
		}
	}
	
        @Override
	public void sub (Player p, double amount) {
		try {
			this.sub(p.getName(), amount);
		} catch (NullPointerException npe) {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] NullPointerException: "+npe.getMessage());
		}
	}
	
        @Override
	public void sub (String p, double amount) {
		try {
			iConomy.getAccount(p).getHoldings().subtract(amount);
		} catch (NullPointerException npe) {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] NullPointerException: "+npe.getMessage());
		}
	}
	
        @Override
	public String getClassName () {
			return iConomy.class.getName();
	}

        @Override
        public String format(double amount) {
            return iConomy.format(amount);
        }
        
}
