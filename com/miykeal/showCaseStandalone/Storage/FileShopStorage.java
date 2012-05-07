package com.miykeal.showCaseStandalone.Storage;

import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.Utilities.Utilities;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

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

@Deprecated
public class FileShopStorage implements ShopStorage {
	private static final String 	seperator	= ";";
	
	
	private HashMap<String, String>	storage	= new HashMap<String, String>();
	private ShowCaseStandalone		scs;
	
	
	public FileShopStorage (ShowCaseStandalone scs) {
		this.scs	= scs;
                
                //This causes endless NPE's if no data file is present, since it creates
                //the data file as a directory.
		//Properties.dataPath.mkdirs		();
		Properties.dataBackupPath.mkdirs();
	}

	/*
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopStorage#saveShop(java.lang.String, com.miykeal.showCaseStandalone.Shop)
	 */
        @Override
	public void saveShop(String sha1, Shop p) throws IOException {
		storage.put(sha1, this.shopToString(p) );
		this.flush();
	}

	/*
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopStorage#loadShop(java.lang.String)
	 */
        @Override
	public Shop loadShop(String sha1) throws IOException {
		return this.stringToShop( storage.get( sha1 ) );
	}

	/*
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopStorage#saveShops(com.miykeal.showCaseStandalone.Shop[])
	 */
        @Override
	public void saveShops(Shop[] p) throws IOException {
		for (Shop s : p)
			storage.put(s.getSHA1(), this.shopToString(s) );
		this.flush();
	}

	/*
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopStorage#loadshops()
	 */
        @Override
	public Shop[] loadshops() throws IOException {
		List<Shop>		shops 	= new ArrayList<Shop>();
		FileInputStream fis;
		
		if ( !Properties.dataPath.exists() && !Properties.dataPathOld.exists() ) {
			scs.log(Level.INFO, "No shops found.");
			return new Shop [0];
		}
		
		if ( !Properties.dataPath.exists() )
			fis = new FileInputStream ( Properties.dataPathOld );
		else
			fis = new FileInputStream ( Properties.dataPath    );
		
		
		Scanner s = new Scanner (fis);
		
		while (s.hasNextLine()) {
			Shop p = this.stringToShop(s.nextLine());
			shops.add(p);
		}
		
		Shop ps[] 		= new Shop[shops.size()];
		int position 	= 0;
		for (Shop p : shops)
			ps[position++] = p;
		
		
		fis.close();
		scs.log(Level.INFO, ps.length + " shops loaded");
		return ps;
	}

        @Override
        public void removeShop(String sha1) throws IOException {
            if(storage.containsKey(sha1))
                storage.remove(sha1);
            this.flush();
        }
        
	/*
	 * @see com.miykeal.showCaseStandalone.interfaces.ShopStorage#flush()
	 */
	public void flush() throws IOException {
		// backup
		Properties.dataPath.renameTo(	new File(Properties.dataBackupPath.getAbsolutePath() + "/" + Properties.dataPath.getName() + ".backup-" + System.currentTimeMillis())	);
		
		FileOutputStream 	fos	= new FileOutputStream 	( Properties.dataPath );
		PrintStream			ps	= new PrintStream 		(fos);
		
		for (String s : this.storage.values())
			ps.append( s + "\n" );
		
		 ps.flush	();
		fos.flush	();
		 ps.close	();
		fos.close	();
	}
	
	/*
	 * Converts a shop to a string - or a line - in the config file.
	 */
	private String shopToString (Shop p) {
		StringBuilder string = new StringBuilder ();
		
		string.append( p.getAtivitie() 	);	// Activitie
		string.append( seperator 		);
		string.append( p.getAmount() 	);	// Amount
		string.append( seperator 		);
		string.append( p.getMaxAmount() );	// Max Amount
		string.append( seperator 		);
		string.append( p.getMaterial() 	);	// Material
		string.append( seperator 		);
		string.append( p.getPrice() 	);	// Price
		string.append( seperator 		);
		string.append( p.getOwner() 	);	// Owner
		string.append( seperator 		);
		string.append( p.getLocation().getWorld().getName() 	);	// Worldname
		string.append( seperator 								);
		string.append( p.getBlock().getLocation().getBlockX() 	);	// blockX
		string.append( seperator 								);
		string.append( p.getBlock().getLocation().getBlockY() 	);	// blockY
		string.append( seperator 								);
		string.append( p.getBlock().getLocation().getBlockZ() 	);	// blockZ
		string.append( seperator 								);
		string.append( p.getSHA1() 								);	// sha1-hash
		string.append( seperator 								);
		
		return string.toString();
	}

	/*
	 * Converts a string - or a line - of the config file in a shop
	 */
	private Shop stringToShop (String s) throws IOException {
		String			sha1;
		Activities 		a;
		int 			amount;
		int 			maxAmount;
		//MaterialData	material;
                ItemStack is;
		double 			price;
		String 			owner;
		World 	w;
		double 	x;
		double 	y;
		double 	z;
		Block 	block;

		String params[] = s.split( seperator );


		if (params[0].equalsIgnoreCase	   ( Activities.BUY.toString()	   ))
			a = Activities.BUY;
		else if (params[0].equalsIgnoreCase( Activities.SELL.toString()    ))
			a = Activities.SELL;
		else if (params[0].equalsIgnoreCase( Activities.DISPLAY.toString() ))
			a = Activities.DISPLAY;
		else
			throw new IOException();

		amount 		= Integer.parseInt(params[1]);
		maxAmount 	= Integer.parseInt(params[2]);
                is              = Utilities.getItemStackFromString(params[3]);
//		material 	= Utilities.getMaterialData(params[3]);
		price 		= Double.parseDouble(params[4]);
		owner 		= params[5];

		w = scs.getServer().getWorld(params[6]);
		x = Double.parseDouble(params[7]);
		y = Double.parseDouble(params[8]);
		z = Double.parseDouble(params[9]);

		if (w == null)
			throw new IOException("World is null, request again world "+params[6]+"="+scs.getServer().getWorld(params[6]));

		if (params.length < 11)		// Because of old versions
			sha1	= Utilities.getRandomSha1(owner);
		else
			sha1	= params[10];
		block 		= w.getBlockAt((int) x, (int) y, (int) z);

		Shop 	p = new Shop(sha1, scs, a, amount, maxAmount, is, price, owner);
			 	p.setBlock		(block);
			 	p.setLocation	(block.getLocation());
		return 	p;
	}

	@Override
	public void removeAllShops() throws IOException {
		storage.clear();
		this.flush();
	}

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
