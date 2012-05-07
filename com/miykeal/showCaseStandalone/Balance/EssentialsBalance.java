package com.miykeal.showCaseStandalone.Balance;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import java.util.logging.Level;
import org.bukkit.entity.Player;

public class EssentialsBalance implements Balance {
	
	private ShowCaseStandalone 	scs;
	private Essentials			essentials;
	
	public EssentialsBalance (ShowCaseStandalone scs, Essentials e) {
		this.scs			= scs;
		this.essentials		= e;
	}

	@Override
	public String getClassName() {
		return Economy.class.getName();
	}

	@Override
	public boolean hasEnough(String p, double amount) {
		try {
			return Economy.hasEnough(p, amount);
		} catch (UserDoesNotExistException udnee) {
			return false;
		}
	}

	@Override
	public boolean isEnabled() {
		return essentials.isEnabled();
	}

	@Override
	public void add(Player p, double amount) {
		this.add(p.getName(), amount);
	}

	@Override
	public void add(String p, double amount) {
		try {
			Economy.add(p, amount);
		} catch (Exception e) {
			scs.log(Level.WARNING, "Couldn't add money to player="+p+", because: " + e);
		}
	}

	@Override
	public void sub(Player p, double amount) {
		this.sub(p.getName(), amount);
	}

	@Override
	public void sub(String p, double amount) {
		try {
			Economy.subtract(p, amount);
		} catch (Exception e) {
			scs.log(Level.WARNING, "Couldn't subtract money from player="+p+", because: " + e);
		}
	}

	@Override
	public String format(double amount) {
		return Economy.format(amount);
	}

	
}
