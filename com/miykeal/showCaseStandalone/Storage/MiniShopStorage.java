package com.miykeal.showCaseStandalone.Storage;

import com.mini.Arguments;
import com.mini.Mini;
import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.ShopInternals.Shop.Activities;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.miykeal.showCaseStandalone.Utilities.BenchMark;
import com.miykeal.showCaseStandalone.Utilities.Properties;
import com.miykeal.showCaseStandalone.Utilities.Utilities;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Mini DB flat file database. Uses nijikokun's mini db.  (Source on github)
 * @author Sorklin
 */
public class MiniShopStorage implements ShopStorage {
    private Mini scs_database;
    private File mini_file;
    private ShowCaseStandalone scs;
    private int saveTask;
    private boolean skipSave = false;
    
    public MiniShopStorage (ShowCaseStandalone scs){
        this.scs = scs;
        this.initFile();
        if(Properties.delayedSave){
            ShowCaseStandalone.slog(Level.FINE, "in delayed save init");
            long period = Properties.delayedInterval * 20;
            //right now, the only thing that will stop this task is onPluginDisable.  I do this because
            //there is no penalty to having the task continue when the rest of the plugin is disabled (via
            // the disable command), since nothing is changed int eh mini db.
            saveTask = scs.getServer().getScheduler().scheduleSyncRepeatingTask(scs, new Runnable() {
                @Override
                public void run() {
                    ShowCaseStandalone.slog(Level.FINEST, "Save thread run.  debug = " 
                            + Properties.saveDebug + ", skipSave = " + skipSave);
                    BenchMark bm = null;
                    if(Properties.saveDebug)
                        bm = new BenchMark("update()");
                    
                    if(scs_database != null && !skipSave)
                        scs_database.update();
                    
                    if(Properties.saveDebug){
                        bm.end();
                        bm = null;
                    }
                }
            },period, period);
        }
    }
    
    private void initFile() {
        try {
            mini_file = new File(ShowCaseStandalone.get().getDataFolder(), "scs.mini");
            if(!mini_file.exists())
                mini_file.createNewFile();
        
            scs_database = new Mini(mini_file.getParent(), mini_file.getName());
        } catch (IOException ioe) {
            ShowCaseStandalone.slog(Level.INFO, "Could not find/create/connect to scs.mini");
        }
    }
    
    @Override
    public void saveShop(String sha1, Shop p) throws IOException {
        BenchMark bm = null;
        if(Properties.interactDebug)
            bm = new BenchMark("saveShop");
        
        if(scs_database == null)
            throw new IOException("Could not attach to mini db.");
        Block b = p.getBlock();
        
        Arguments entry = new Arguments(sha1);
        entry.setValue("act", p.getAtivitie().toString());
        entry.setValue("amt", String.valueOf(p.getAmount()));
        entry.setValue("max", String.valueOf(p.getMaxAmount()));
        entry.setValue("material", p.getMaterial());
        entry.setValue("price", String.valueOf(p.getPrice()));
        entry.setValue("owner", p.getOwner());
        entry.setValue("world", p.getLocation().getWorld().getName());
        entry.setValue("x", String.valueOf(b.getLocation().getBlockX()));
        entry.setValue("y", String.valueOf(b.getLocation().getBlockY()));
        entry.setValue("z", String.valueOf(b.getLocation().getBlockZ()));
        entry.setValue("basemat", String.valueOf(b.getTypeId()));
        entry.setValue("enchantments", p.getEnchantments());
        if(Properties.interactDebug)
            bm.mark("setting arguments");
        
        scs_database.addIndex(entry.getKey(), entry);
        if(Properties.interactDebug)
            bm.mark("addIndex");
        
        //Only update if we're not doing delayed saving or we've told ourselves to skip saves
        if(!Properties.delayedSave && !skipSave){
            scs_database.update();
            if(Properties.interactDebug)
                bm.mark("saved");
        }
        if(Properties.interactDebug)
            bm.end();
    }

    @Override
    public Shop loadShop(String sha1) throws IOException {
        if(scs_database == null)
            throw new IOException("Could not attach to mini db.");
        
        Arguments entry = null;
        if(scs_database.hasIndex(sha1))
            entry = scs_database.getArguments(sha1);
        if(entry == null)
            throw new IOException("Shop not found.");
        Shop p = argumentsToShop(entry);
        if(p==null)
            throw new IOException("Shop found, but could not be parsed.");
        return p;
    }

    @Override
    public void saveShops(Shop[] p) throws IOException {
        if(scs_database == null)
            throw new IOException("Could not attach to mini db.");
        skipSave = true; //suspend any updates to file until we've saved them.
        this.removeAllShops();
        for(Shop shop: p)
            this.saveShop(shop.getSHA1(), shop);
        scs_database.update();
        skipSave = false;
        ShowCaseStandalone.slog(Level.INFO, "Saved " + p.length + " shops.");
    }

    @Override
    public Shop[] loadshops() throws IOException {
        if(scs_database == null)
            throw new IOException("Could not attach to mini db.");
        
        //This is kludgy, but if you don't manually unload the shops and reload
        //it won't update.
        scs_database = new Mini(mini_file.getParent(), mini_file.getName());
        
        ArrayList<Shop> shops = new ArrayList<Shop>();
        Shop p = null;
        
        for(String index: scs_database.getIndices().keySet()) {
            Arguments entry = scs_database.getArguments(index);
            p = argumentsToShop(entry);
            if(p != null)
                shops.add(p);
            else
                ShowCaseStandalone.slog(Level.INFO, "Could not load shop " + index);
        }
        
        Shop[] ps = new Shop[shops.size()];
        ps = shops.toArray(ps);
        ShowCaseStandalone.slog(Level.INFO, "Loaded " + shops.size() + " shops.");
        return ps;
    }
    
    @Override
    public void removeShop(String sha1) throws IOException {
        if(scs_database == null)
            throw new IOException("Could not attach to mini db.");
        
        if(scs_database.hasIndex(sha1)){
            scs_database.removeIndex(sha1);
            scs_database.update();
            ShowCaseStandalone.dlog("Removed shop " + sha1);
        }
    }
    
    @Override
    public void removeAllShops() throws IOException {
        if(scs_database == null)
            throw new IOException("Could not attache to mini db.");
        
        for(String index: scs_database.getIndices().keySet()) {
            scs_database.removeIndex(index);
        }
        scs_database.update();
    }
    
    private Shop argumentsToShop (Arguments arg) {
		String			sha1 = arg.getKey();
		Activities 		a;
		int 			amount;
		int 			maxAmount;
                ItemStack               is;
                int                     baseMat;
		double 			price;
		String 			owner;
		World 	w;
		double 	x;
		double 	y;
		double 	z;
		Block 	block;
                
                //In general, If we're missing parameters, just return a null.
                if(arg.getValue("act").equalsIgnoreCase("buy"))
                    a = Activities.BUY;
                else if (arg.getValue("act").equalsIgnoreCase("sell"))
                    a = Activities.SELL;
                else if (arg.getValue("act").equalsIgnoreCase("display"))
                    a = Activities.DISPLAY;
                else {
                    ShowCaseStandalone.slog(Level.INFO, "Could not get activity for shop " + sha1);
                    return null;
                }

		amount 		= arg.getInteger("amt");
		maxAmount 	= arg.getInteger("max");
                
                try {
                    is 	= Utilities.getItemStackFromString(arg.getValue("material"));
                } catch (IOException ioe) {
                    ShowCaseStandalone.slog(Level.INFO, "Could not load materials for shop " + sha1);
                    return null;
                }
                
                if(arg.hasKey("enchantments")){
//                    ShowCaseStandalone.slog(Level.INFO, "Starting enchantments loading:");
                    
                    String enchField = arg.getValue("enchantments");
                    String[] ench;
                    
//                    ShowCaseStandalone.slog(Level.INFO, "enchField: " + enchField);
                    
                    if(enchField.contains(",")){
                        ench = enchField.split(",");
                    } else {
                        ench = new String[1];
                        ench[0] = enchField;
                    }

//                    ShowCaseStandalone.slog(Level.INFO, "ench: " + ench.toString());

                    Enchantment enchantment;
                    int level;

                    for(String e : ench){
//                        ShowCaseStandalone.slog(Level.INFO, "e: " + e);
                        try {
                            if(!e.equalsIgnoreCase("")){
                                enchantment = getEnchantmentFromString(e);
                                level = getEnchantmentLevelFromString(e);
                                if(enchantment == null || level < 1)
                                    ShowCaseStandalone.slog(Level.INFO, "Could not cast enchantment from " + e + " in shop " + sha1);
                                else {
                                    if(Properties.allowUnsafeEnchantments)
                                        is.addUnsafeEnchantment(enchantment, level);
                                    else
                                        is.addEnchantment(enchantment, level);
                                }
                            }
                        } catch (IllegalArgumentException iae) {
                            ShowCaseStandalone.slog(Level.WARNING, iae.getMessage());
                            ShowCaseStandalone.slog(Level.WARNING, "Saved enchantment: " + e + " in shop " + sha1);
                        } catch (NullPointerException npe) {
                            ShowCaseStandalone.slog(Level.WARNING, "Enchantment found does not exist. Found " + e + " on shop " + sha1);
                            ShowCaseStandalone.slog(Level.WARNING, "Saved enchantment: " + e + " in shop " + sha1);
                        } catch (Exception ex) {
                            ShowCaseStandalone.slog(Level.WARNING, "Error loading enchantment:  "
                                    + ex.getMessage() + ". Found (" + e + ") on shop " + sha1);
                            ShowCaseStandalone.slog(Level.WARNING, "Saved enchantment: " + e + " in shop " + sha1);
                        }
                    }
                }
                
		price 		= arg.getDouble("price");
		owner 		= arg.getValue("owner");
                if(owner.equals("")){
                    ShowCaseStandalone.slog(Level.INFO, "Could not owner for shop " + sha1);
                    return null;
                }
                
                //Will be null if the world doesn't exist (i.e. MV not loaded)
		w = scs.getServer().getWorld(arg.getValue("world"));
                
		x = arg.getDouble("x");
		y = arg.getDouble("y");
		z = arg.getDouble("z");
                baseMat = arg.getInteger("basemat");
                
		if (w == null){
                        ShowCaseStandalone.slog(Level.INFO, "World was null for shop " + sha1);
			return null;
                }
                
		block 		= w.getBlockAt((int) x, (int) y, (int) z);
                
                //TODO: fix this, or at least figure out why its not working.
                if(block.getTypeId() != baseMat){
                    ShowCaseStandalone.slog(Level.FINE, "Block material does not match saved state for shop " + sha1);
                    ShowCaseStandalone.slog(Level.FINE, "baseMat: " + baseMat + ", block mat: " + block.getTypeId());
                    //return null;
                }
                    
                
		//Lets check for a proper block at that place.  If not, then return null
                //TODO: Debug this:
//                if(Properties.forbiddenBlocks.contains(block.getType())){
//                    ShowCaseStandalone.slog(Level.INFO, "Block was forbidden for shop " + sha1);
//                    ShowCaseStandalone.slog(Level.INFO, "Forbidden block: " + block.getType());
//                    
//                    return null;
//                }
                    
                
		Shop p = new Shop(sha1, scs, a, amount, maxAmount, is, price, owner);
                p.setBlock (block);

		return 	p;
	}
    
        private Enchantment getEnchantmentFromString(String e){
            
            Enchantment ench = null;
            
            String args[] = new String[2];
            if (e.contains(":"))
                    args = e.split(":");
            else {
    		args[0] = e;
    		args[1] = "1";
            }
            
            try{
                ench = Enchantment.getById(Integer.parseInt(args[0]));
            } catch (NumberFormatException nfe) {
            } catch (Exception ex){
                ex.printStackTrace();
            }
            
            return ench;
        }
        
        private int getEnchantmentLevelFromString(String e){
            
            int strength = 1;
            String args[] = new String[2];
            if (e.contains(":"))
                    args = e.split(":");
            else {
    		args[0] = e;
    		args[1] = "1";
            }
            
            try {
                strength = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                //Can't parse to a number.  Assume a strength of 1.
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            return strength;
        }

        @Override
        public void update() {
            if(scs_database != null)
                scs_database.update();
        }
}
