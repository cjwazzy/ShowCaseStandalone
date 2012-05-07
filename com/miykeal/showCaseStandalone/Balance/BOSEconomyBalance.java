package com.miykeal.showCaseStandalone.Balance;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import cosine.boseconomy.BOSEconomy;
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

public class BOSEconomyBalance implements Balance {
	private BOSEconomy 			economy;
	
	public BOSEconomyBalance (ShowCaseStandalone scs, BOSEconomy economy) {
		this.economy = economy;
	}
	
        @Override
	public String getClassName () {
		return economy.getClass().getName();
	}
	
        @Override
	public boolean hasEnough (String p, double amount) {
		return (economy.getPlayerMoneyDouble(p) >= amount);
	}
	
        @Override
	public boolean isEnabled () {
		return economy.isEnabled();
	}
	
        @Override
	public void add (Player p, double amount) {
		this.add (p.getName(), amount);
	}
	
        @Override
	public void add (String p, double amount) {
		economy.addPlayerMoney(p, amount, false);
	}
	
        @Override
	public void sub (Player p, double amount) {
		this.sub(p.getName(), amount);
	}
	
        @Override
	public void sub (String p, double amount) {
		economy.addPlayerMoney(p, -amount, false);
	}

        @Override
        public String format(double amount) {
            String currency = economy.getMoneyNamePlural();

            if(amount == 1) 
                currency = economy.getMoneyName();

            return amount + " " + currency;
        }
}
