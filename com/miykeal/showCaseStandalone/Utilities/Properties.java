package com.miykeal.showCaseStandalone.Utilities;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import java.io.File;
import java.util.List;
import org.bukkit.material.MaterialData;


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

public class Properties {
	public enum EconomySystem {
		AUTO				(new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy", "com.earth2me.essentials.Essentials", "cosine.boseconomy.BOSEconomy", "net.milkbowl.vault.Vault"}),
		iConomy				(new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy"}),
		EssentialsEconomy	(new String[]{"com.earth2me.essentials.Essentials"}),
		BOSEconomy			(new String[]{"cosine.boseconomy.BOSEconomy"}),
		Vault				(new String[]{"net.milkbowl.vault.Vault"}),
		;
		public final String classNames[];
		private EconomySystem (String classNames[]) {
			this.classNames	= classNames;
		}
	}
	
	/*
	 * Build information
	 */
	public static final double 	build				= 80;
	public static final String 	buildAuthor			= "kellerkindt";
	public static final String  buildContributor	= "sorklin, bitfreeze";
	public static final String 	buildDate 			= "2012-04-10";

        
        //Don't forget to update this number if any text changes in the locale files.
        public static final double      localeVersion                   = 1.5;

	/*
	 * For SimpleShopHandler (Thread which checks if the items are dead)
	 * Note: i'm slowing this down as much as possible, to conserve server CPU.  60 is default
         * but is configurable in the config.yml
	 */
	public static final long   intervall = 60;

	/*
	 * Filestore information
	 */
        //This isn't too smart, because NPE and IO errors can happen when new Files are called:
	public static final File	dataPath 		= new File(ShowCaseStandalone.get().getDataFolder(), "data");
	public static final File	dataBackupPath	= new File(ShowCaseStandalone.get().getDataFolder() + "/backup");
	public static final File 	dataPathOld		= new File(ShowCaseStandalone.get().getDataFolder(), "cfg");
	
	public static final String	commentSign = "comment";
	public static final String  seperator	= ";";
        public static final String[] defaultLocaleFiles = {"locale_EN.yml", "locale_DE.yml", "locale_FR.yml"};
        
        //Permissions.  I made these more granular, in case people want to limit users to certain types.
        public static final String permUse = "scs.use";
        public static final String permCreateBuy = "scs.create.buy";
        public static final String permCreateSell = "scs.create.sell";
        public static final String permCreateDisplay = "scs.create.display";
        public static final String permCreateUnlimited = "scs.create.unlimited";
        public static final String permRemove = "scs.remove";
        public static final String permAdmin = "scs.admin";
        public static final String permManage = "scs.manage";
       
        
        //Server customizable properties
        public static int       	defaultUnit;
        public static long      	maxUndoTime;
        public static double    	buyShopCreatePrice;
        public static double    	sellShopCreatePrice;
        public static double    	displayCreatePrice;
        public static boolean   	fixBrokenShopsOnLoad;
        public static String    	storageType;
        public static String    	sqlName;
        public static String    	sqlPass;
        public static String    	sqlHost;
        public static String    	sqlTable;
        public static String    	sqlURI;
        public static boolean   	blackList;
        public static List<MaterialData> blockList;
        public static boolean           buyBlackList;
        public static List<MaterialData> buyList;
        public static boolean           sellBlackList;
        public static List<MaterialData> sellList;
        public static List<String> 	blacklistedWorlds;
        public static boolean   	cancelExplosion;
        public static boolean   	logTransactions;
        public static boolean   	requireObjectToDisplay;
        public static boolean  		hideInactiveShops;
        public static EconomySystem	economySystem;
        public static boolean           allowUnsafeEnchantments;
        public static boolean 		hidden = false;
        public static boolean 		threadDebug = false;
        public static boolean           permDebug = false;
        public static boolean           interactDebug = false;
        public static boolean           showExtraMessages = false;
        public static boolean           chunkDebug = false;
        public static boolean           saveDebug = false;
        public static String            localizationFileName;
        public static boolean           delayedSave;
        public static int               delayedInterval;
}
