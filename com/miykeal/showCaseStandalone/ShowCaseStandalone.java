package com.miykeal.showCaseStandalone;

import com.earth2me.essentials.Essentials;
import com.miykeal.showCaseStandalone.Balance.*;
import com.miykeal.showCaseStandalone.Listeners.*;
import com.miykeal.showCaseStandalone.ShopInternals.SimpleShopHandler;
import com.miykeal.showCaseStandalone.ShopInternals.Todo;
import com.miykeal.showCaseStandalone.Storage.FileShopStorage;
import com.miykeal.showCaseStandalone.Storage.MiniShopStorage;
import com.miykeal.showCaseStandalone.Utilities.Properties.EconomySystem;
import com.miykeal.showCaseStandalone.Utilities.*;
import com.miykeal.showCaseStandalone.interfaces.Balance;
import com.miykeal.showCaseStandalone.interfaces.ShopHandler;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import cosine.boseconomy.BOSEconomy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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

//TODO: Dropchest listener not working with DC fork in 1.1
//Amounts on sell are wrong (default items)

public class ShowCaseStandalone extends JavaPlugin {
	
    private Permission 		 	permission = null;
    private Balance				balance		= null;
    private ShopHandler			shopHandler	= null;
    private ShopStorage			shopStorage	= null;
    
    private static ShowCaseStandalone 	scs;
    private static final 	Logger 		logr 		= Logger.getLogger("Minecraft");
    public static 			SCSLogger 	logDebug 	= null;
    private static 			SCSLogger 	logTrans 	= null;
    
    private ShowCaseStandalonePlayerListener 			playerListener;
    private ShowCaseStandaloneBlockListener 			blockListener;
    private ShowCaseStandaloneWorldListener				worldListener;
    private ShowCaseStandaloneServerListener			serverlistener;
    private ShowCaseStandaloneEntityListener      		entityListener;
    private ShowCaseStandaloneNEWDropChestListener		dropChestListener;
    private ShowCaseStandaloneDropChestListener         oldDropChestListener;
    
    public static final PlayerSessionVariables pv = new PlayerSessionVariables();
    

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
    }
    
    @Override
    public void onDisable() {
    	try {
                log(Level.INFO, "Stopping shop update task.");
    		shopHandler.stop();
                log(Level.INFO, "Saving any remaining shop changes.");
                shopStorage.update();
                log(Level.INFO, "Removing display items.");
	    	shopHandler.hideAll();
    	} catch (Exception ioe) {
    		this.log(Level.WARNING, "Exception on onDisable: " + ioe);
    	}
    }

    @Override
    public void onEnable() {
        logr.log(Level.INFO, "[SCS] Starting build "+Properties.build +", "+Properties.buildDate +" by "+Properties.buildAuthor +", contributors: "+Properties.buildContributor);
        
        // Register our commands
        ShowCaseStandalone.scs 	= this;
        getCommand("scs").setExecutor(new ShowCaseStandaloneCommandExecutor(this));
        
        //Load our configuration
        log(Level.INFO, "Loading configuration.");
        loadSCSConfig(this.getConfig());
        this.saveConfig();
        
        //Initialize the customer loggers
        if(Properties.logTransactions)
            logTrans = new SCSLogger("ShowCaseStandlone", getDataFolder() + File.separator + "transactions.log" );
        
        if(Properties.threadDebug || Properties.chunkDebug || Properties.interactDebug || Properties.saveDebug)
            logDebug = new SCSLogger("ShowCaseStandalone", getDataFolder() + File.separator + "debug.log");
        
        // ShopHandler, ShopStorage
        log(Level.INFO, "Initializing ShopHandler and ShopStorage");
        log(Level.INFO, "Using the " + Properties.storageType + " storage type.");
        log(Level.INFO, "getDatafolder: " + getDataFolder());
        //Fully disabling FileStorageType.
//        if(Properties.storageType.equalsIgnoreCase("minidb"))
        shopStorage = new MiniShopStorage   ( this );
        //}
        
        shopHandler	= new SimpleShopHandler ( this );
        
        log (Level.INFO, "Loading shops");
        try {
        	shopHandler.setStorage(shopStorage);
        } catch (IOException ioe) {
        	log(Level.WARNING, "Exception while loading shops: " + ioe);
        }
        
        //Try to convert to minidb if its set to File.
        if (Properties.storageType.equalsIgnoreCase("file")){
            try {
                log(Level.INFO, "Attempting to import file shops.");
                shopHandler.importStorage(new FileShopStorage(this), shopStorage);
                this.getConfig().set("Database.Type", "minidb");
                this.saveConfig();
            } catch (IOException ioe) {
                    log(Level.WARNING, "IOError: could not import from file. Aborted.");
                    ioe.printStackTrace();
            }
        }
        
        // Initialize localization
        log(Level.INFO, "Loaded localization: " + Properties.localizationFileName);
        try {
            Localization.init(Properties.localizationFileName);
        } catch (IOException ioe){
            log(Level.WARNING, "IOError: could not find/connect to localization file.");
            ioe.printStackTrace();
            log(Level.WARNING, "Disabling SCS.");
            getPluginLoader().disablePlugin(this);
        }
    	
    	// Searching for other plugins
    	log(Level.INFO, "Searching for other Plugins...");

    	for (Plugin p : this.getServer().getPluginManager().getPlugins()) {
            String cName	= p.getClass().getName();

            // iConomy 5
            if (cName.equals("com.iConomy.iConomy") && isAllowedEconomySystem(cName)) {
                    log(Level.INFO, "Hooked into iConomy5");
                    this.balance = new iConomy5Balance (this, (com.iConomy.iConomy)p);
            }

            // iConomy 6
            if (cName.equals("com.iCo6.iConomy") && isAllowedEconomySystem(cName)) {
                    log(Level.INFO, "Hooked into iConomy6");
                    this.balance = new iConomy6Balance (this, (com.iCo6.iConomy)p);
            }

            // BOSEconomy
            if (cName.equals("cosine.boseconomy.BOSEconomy") && isAllowedEconomySystem(cName)) {
                    log(Level.INFO, "Hooked into BOSEconomy");
                    this.balance = new BOSEconomyBalance (this, (BOSEconomy)p);
            }
    		
            // Essentials Economy
            if (cName.equals("com.earth2me.essentials.Essentials") && isAllowedEconomySystem(cName)) {
            	log(Level.INFO, "Hooked into EssentialsEconomy");
            	this.balance	= new EssentialsBalance(this, (Essentials)p);
            }
        
            // Vault
            if (cName.equals("net.milkbowl.vault.Vault") && isAllowedEconomySystem(cName)) {
                RegisteredServiceProvider<Economy> economyProvider = scs.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                if (economyProvider != null) {
                        log (Level.INFO, "Hooked into Vault Economy");
                        balance = new VaultBalance(economyProvider.getProvider());
                }
                RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
                if (permissionProvider != null) {
                        log (Level.INFO, "Hooked into Vault Permissions");
                        permission = permissionProvider.getProvider();
                }
            }
            
            //Attach to DropChest API, if loaded
            if (cName.equalsIgnoreCase("com.narrowtux.dropchest.dropchest")){
            	log(Level.INFO, "Found Old DropChest.  Attempting to hook api.");

            	try {
            		oldDropChestListener = new ShowCaseStandaloneDropChestListener(this);
            		log(Level.INFO, "Hooked OLD DropChest listener.");
            	} catch (Exception e) {}
            }
            
            //This supports a fork that was done to upgrade DC to 1.1. 
            if (cName.equalsIgnoreCase("com.noheroes.dropchest.dropchest")){
            	log(Level.INFO, "Found New DropChest.  Attempting to hook api.");
            	try{ 
            		dropChestListener = new ShowCaseStandaloneNEWDropChestListener(this);
            		log(Level.INFO, "Hooked NEW DropChest listener.");
            	} catch (Exception e){}
            }
    	}
    	
    	// Not found any economy system?
    	if (this.balance == null) {
    		log(Level.WARNING, "No economy system found, using dummy economy system!");
    		this.balance = new DummyBalance(this);
    	}
        
        //Instantiate and Register the listeners.  Do this last to avoid NPEs for chunk load events.
        log(Level.INFO, "Register event listeners.");
        playerListener = new ShowCaseStandalonePlayerListener	(this);
        blockListener   = new ShowCaseStandaloneBlockListener	(this);
        worldListener	= new ShowCaseStandaloneWorldListener	(this);
        serverlistener	= new ShowCaseStandaloneServerListener	(this);
        entityListener  = new ShowCaseStandaloneEntityListener  (this);
    }
    
    /**
     * Logging for this module only.
     * @param l Log Level
     * @param message 
     */
    public void log (Level l, String message){
        ShowCaseStandalone.slog(l, message);
    }
    
    /**
     * Static logger for the minecraft.log
     * @param l Log Level
     * @param message 
     */
    public static void slog(Level l, String message){
        if(Properties.showExtraMessages)
            ShowCaseStandalone.get().getLogger().log(l, message);
        else
            ShowCaseStandalone.logr.log(l, "[SCS] " + message);
    }
    
    /**
     * Thread Debug logger.  Logs to debug.log when activated.
     * @param l Log level
     * @param message 
     */
    public static void dlog(String message){
        if(logDebug != null)
            logDebug.log(Level.INFO, message);
    }
    
    /**
     * Transaction logger.  Logs to transaction.log when activated.
     * If mysql is active, we should be sending this to that module for 
     * storage in the transaction table.
     * @param customer
     * @param owner
     * @param action
     * @param quantity
     * @param price
     * @param item
     * @param sha1
     * @param inventory 
     */
    public static void tlog(String customer, String owner, String action, 
            int quantity, double price, String item, String sha1, int inventory){
        if(Properties.logTransactions){
            StringBuilder msg = new StringBuilder();
            msg.append("Transaction: ");
            msg.append("c:").append(customer).append(", o:").append(owner);
            msg.append(", a:").append(action);
            msg.append(", q:").append(quantity);
            msg.append(", i:").append(item);
            msg.append(", pr:").append(price);
            msg.append(", shp:").append(sha1);
            msg.append(", inv:").append(inventory);
            
            if(logTrans != null)
                logTrans.log(Level.INFO, msg.toString());
        }
    }
    
    /*
     * Makes the current instance for all accessible
     */
    public static ShowCaseStandalone get () {
    	return ShowCaseStandalone.scs;
    }
    
    /*
     * Add Todo-Object to the playerListener
     */
    public void addTodo (Player player, Todo t) {
    	playerListener.addTodo(player, t);
    }
    
    /*
     * Removes Todo-Object from the playerListener
     */
    public Todo removeTodo (Player player) {
    	return playerListener.removeTodo(player);
    }
    
    /*
     * Checks if the given player has the given permission
     * First uses Permissions plugin if available, if not
     * it uses bukkit-permission 
     */
    public boolean hasPermission (CommandSender cs, String permission)
    {
        //For players, return the permission status.  For console command, return true, else return false.
        if(cs instanceof Player)
            return hasPermission((Player)cs, permission);
        else 
            return (cs instanceof ConsoleCommandSender);
    }
    
    /*
     * Checks if the given player has the given permission
     * First uses Permissions plugin if available, if not
     * it uses bukkit-permission 
     */
    public boolean hasPermission (Player player, String perm)
    {
    	log(Level.FINEST, "player: " + player.getName() + " requested permission: " + perm + " has?: " + player.hasPermission(perm));
        
        if (permission != null){
                log(Level.FINEST, "permissions != null.  return val = " + permission.has(player, perm));
    		return permission.has(player, perm);
        } else {
                log(Level.FINEST, "bukkit default perms. return val = " + player.hasPermission(perm));
    		return player.hasPermission(perm);
        }
    }
    
    /*
     * Returns the BalanceHandler
     */
    public Balance getBalanceHandler () {
    	return this.balance;
    }
    
    public void setBalanceHandler (Balance bh) {
        this.balance = bh;
    }
    
    /*
     * Returns the ShopHandler
     */
    public ShopHandler getShopHandler () {
    	return this.shopHandler;
    }
    
    /*
     * Returns the shop storage
     */
    public ShopStorage getShopStorage(){
        return this.shopStorage;
    }
            
    /*
     * Returns formatted money amounts.
     */
    public String formatCurrency(double amount){
        return balance.format(amount);
    }
    
    /**
     * Checks if the economy system is allowed
     * @param 	className	ClassName of the economy system
     * @return	true if it is allowed, false if it isn't allowed
     */
    public boolean isAllowedEconomySystem (String className) {
    	String names[]	= Properties.economySystem.classNames;
    	for (String s : names)
    		if (s.equals(className))
    			return true;
    	return false;
    }
    
    /*
     * Configuration file loader
     */
    public void loadSCSConfig(FileConfiguration config){
        config.options().copyDefaults(true);
        Properties.showExtraMessages            = config.getBoolean("Debug.ShowExtraMessages", false);
        
        Properties.defaultUnit 				= config.getInt		("DefaultUnit");
        Properties.maxUndoTime 				= config.getInt		("UndoTime") * 1000;
        Properties.buyShopCreatePrice 		= config.getDouble	("CreatePrice.BuyShop");
        Properties.sellShopCreatePrice 		= config.getDouble	("CreatePrice.SellShop");
        Properties.displayCreatePrice 		= config.getDouble	("CreatePrice.Display");
        
        Properties.storageType 				= config.getString	("Database.Type"); //we'll catch potential errors later.
        Properties.blackList 				= config.getBoolean	("BlockList.Blacklist");
        Properties.blockList 				= convertListStringToMaterial(config.getStringList("BlockList.Blocks"));
        Properties.buyBlackList                         = config.getBoolean("BuyItemList.Blacklist");
        Properties.buyList                              = convertListStringToMaterial(config.getStringList("BuyItemList.Items"));
        Properties.sellBlackList                        = config.getBoolean("SellItemList.Blacklist");
        Properties.sellList                              = convertListStringToMaterial(config.getStringList("SellItemList.Items"));
        
        Properties.blacklistedWorlds 		= config.getStringList("WorldBlacklist");
        Properties.cancelExplosion 			= config.getBoolean	("CancelExplosion");
        Properties.logTransactions 			= config.getBoolean	("LogTransactions");
        Properties.hideInactiveShops 		= config.getBoolean	("HideInactiveShops");
        
        Properties.threadDebug 				= config.getBoolean	("Debug.Thread", false);
        Properties.permDebug                    = config.getBoolean("Debug.Permissions", false);
        Properties.interactDebug                = config.getBoolean("Debug.Interact", false);
        Properties.chunkDebug                   = config.getBoolean("Debug.Chunk", false);
        Properties.saveDebug                    = config.getBoolean("Debug.Save", false);
        
        Properties.requireObjectToDisplay 	= config.getBoolean	("RequireObjectToDisplay");
        Properties.economySystem			= convertToEconomySystem ( config.getString("EconomySystem") );
        Properties.allowUnsafeEnchantments      = config.getBoolean ("AllowUnsafeEnchantments");
        
        Properties.delayedSave                  = config.getBoolean("Save.Delayed");
        Properties.delayedInterval              = config.getInt("Save.Interval");
        
        Properties.localizationFileName         = config.getString("Localization.File", "locale_EN");
        //Check for extension.  If not found, then add .yml
        if(!Properties.localizationFileName.contains("."))
            Properties.localizationFileName += ".yml";
        
        //now check locale version.  If different from the one we hardcode in Properties, then overwrite default locale file.
        //this indicates that we've added or modified the default locale file(s).
        
        log(Level.INFO, "locale vs. config: " + Properties.localeVersion + " v " + config.getDouble("Localization.Version"));
        
        if(Properties.localeVersion != config.getDouble("Localization.Version")){
            log(Level.INFO, "Locale file has changed.  Overwriting default locale files with new versions.");
            log(Level.INFO, "If you are using a custom locale file, please update with any changes you need.");
            for(String defaultName : Properties.defaultLocaleFiles){
                this.saveResource(defaultName, true);
            }
            config.set("Localization.Version", Properties.localeVersion);
        }
    }
    
    
    /**
     * Converts the given String to a EconomySystem variable
     * By default, it returns EconomySystem.AUTO
     * @param 	value	Name of the economy system
     * @return	The EconomySystem for the value or EconomySystem.AUTO
     */
    private EconomySystem convertToEconomySystem (String value) {
    	EconomySystem system = EconomySystem.AUTO;
    	
    	for (EconomySystem es : EconomySystem.values())
    		if (es.toString().equalsIgnoreCase(value))
    			system = es;
    	return system;
    }
    
    
    /*
     * Converts a list of Strings to a list of materials.
     */
    private List<MaterialData> convertListStringToMaterial(List<String> list){
        List<MaterialData> result = new ArrayList<MaterialData>();
        MaterialData m = null;
        
        for(String mat : list){
            try{
                m = Utilities.getMaterialsFromString(mat);
                result.add(m);
            } catch (IOException ioe){}
        }
        return result;
    }
    
    public static void spam(String m){
        Bukkit.getServer().broadcastMessage(m);
    }
    
    public boolean isShowCaseItem(Item i){
        return this.shopHandler.isShopItem(i);
    }
    
    /* Allows late binding.
     * Class used to hook in Plugins like:
     * - iConomy
     * - BOSEconomy
     * - Permission
     */
    private class ShowCaseStandaloneServerListener implements Listener {
        
    	private ShowCaseStandalone scs;

        public ShowCaseStandaloneServerListener(ShowCaseStandalone plugin) {
            this.scs = plugin;
            scs.getServer().getPluginManager().registerEvents(this, scs);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            String name = event.getPlugin().getDescription().getName();
            Plugin p = event.getPlugin();
    		
    		// iConomy 5
    		if (name.equals("com.iConomy.iConomy") && isAllowedEconomySystem(name)) {
    			log(Level.INFO, "Hooked into iConomy5");
    			scs.balance = new iConomy5Balance (scs, (com.iConomy.iConomy)p);
    		}
    		
    		// iConomy 6
    		if (name.equals("com.iCo6.iConomy") && isAllowedEconomySystem(name)) {
    			log(Level.INFO, "Hooked into iConomy6");
    			scs.balance = new iConomy6Balance (scs, (com.iCo6.iConomy)p);
    		}
    		
    		// BOSEconomy
    		if (name.equals("cosine.boseconomy.BOSEconomy") && isAllowedEconomySystem(name)) {
    			log(Level.INFO, "Hooked into BOSEconomy");
    			scs.balance = new BOSEconomyBalance (scs, (BOSEconomy)p);
    		}
    		
    		// Essentials Economy
                if (name.equals("com.earth2me.essentials.Essentials") && isAllowedEconomySystem(name)) {
                            log(Level.INFO, "Hooked into EssentialsEconomy");
                            scs.balance	= new EssentialsBalance(scs, (Essentials)p);
                }
        }
        
        /*
         * Listen for Permissions, iConomy and BOSEconomy
         */
        //This is causing all sorts of erros in the new system.  Disable change to dummy class.
    	@EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
    		Plugin p = event.getPlugin();
    		
    		if (p.getClass().getName().equals("com.iConomy.iConomy")) {
    			log(Level.INFO, "Un-hooked iConomy");
    			scs.balance = new DummyBalance(scs);
    		}
    		
    		if (p.getClass().getName().equals("com.iCo6.iConomy")) {
    			log(Level.INFO, "Un-hooked iConomy");
    			scs.balance = new DummyBalance(scs);
    		}
    		
    		if (scs.permission != null) {
    			if (!scs.permission.isEnabled()) {
	    			log(Level.INFO, "Un-hooked Permissions");
	    			scs.permission = null;
    			}
    		}

    		if (p.getClass().getName().equals("cosine.boseconomy.BOSEconomy")) {
    			log(Level.INFO, "Un-hooked BOSEconomy");
    			scs.balance = new DummyBalance(scs);
    		}
        }
    }
}
