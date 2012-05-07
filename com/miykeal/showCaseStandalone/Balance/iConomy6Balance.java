package com.miykeal.showCaseStandalone.Balance;

import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
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


public class iConomy6Balance implements Balance {
	private iConomy 			iconomy;
	private Accounts 			accounts;
	private ShowCaseStandalone	scs;
	
	public iConomy6Balance (ShowCaseStandalone scs, iConomy iconomy) {
//		super (scs);
		this.iconomy	= iconomy;
		this.scs		= scs;
		accounts 		= new Accounts ();
	}

	@Override
	public String getClassName() {
		return iconomy.getClass().getName();
	}

	@Override
	public boolean hasEnough(String p, double amount) {
		if (accounts.exists(p))
			return accounts.get(p).getHoldings().hasEnough(amount);
		else {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
			return false;
		}
	}

	@Override
	public boolean isEnabled() {
		return iconomy.isEnabled();
	}

	@Override
	public void add(Player p, double amount) {
		this.add(p.getName(), amount);		
	}

	@Override
	public void add(String p, double amount) {
		if (accounts.exists(p))
			accounts.get(p).getHoldings().add(amount);
		else
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
	}

	@Override
	public void sub(Player p, double amount) {
		
		this.sub(p.getName(), amount);
	}

	@Override
	public void sub(String p, double amount) {
		if (accounts.exists(p))
			accounts.get(p).getHoldings().subtract(amount);
		else
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
	}

        @Override
        public String format(double amount) {
            return iConomy.format(amount);
        }
	
	
}
