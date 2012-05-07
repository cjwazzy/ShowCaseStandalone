package com.miykeal.showCaseStandalone.ShopInternals;

import org.bukkit.entity.Player;

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
public class Todo {
	public enum Types {
		CREATE,
		REMOVE,
		ADD,
		GET,
		LIMIT,
		SETPRICE,
		SETOWNER,
	}
	public final Player player;
	public final Types  types;
	public final Shop   shop;
	public final double amount;
	public final String string;
        
	public Todo (Player player, Types types, Shop shop, double amount, String string) {
		this.player	= player;
		this.types	= types;
		this.shop	= shop;
		this.amount	= amount;
		this.string = string;
	}
}
