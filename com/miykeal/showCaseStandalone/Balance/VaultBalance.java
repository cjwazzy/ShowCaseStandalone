package com.miykeal.showCaseStandalone.Balance;

import com.miykeal.showCaseStandalone.interfaces.Balance;
import net.milkbowl.vault.economy.Economy;
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

public class VaultBalance implements Balance {
	private Economy economy;
	
	public VaultBalance (Economy economy) {
		this.economy = economy;
	}

	@Override
	public String getClassName() {
		return economy.getClass().getName();
	}

	@Override
	public boolean hasEnough(String p, double amount) {
		return economy.has(p, amount);
	}

	@Override
	public boolean isEnabled() {
		return economy.isEnabled();
	}

	@Override
	public void add(Player p, double amount) {
		this.add(p.getName(), amount);
	}

	@Override
	public void add(String p, double amount) {
		economy.depositPlayer(p, amount);
	}

	@Override
	public void sub(Player p, double amount) {
		this.sub(p.getName(), amount);
	}

	@Override
	public void sub(String p, double amount) {
		economy.withdrawPlayer(p, amount);
	}

	@Override
	public String format(double amount) {
		return economy.format(amount);
	}

	
	
}
