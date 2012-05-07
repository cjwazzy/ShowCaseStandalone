package com.miykeal.showCaseStandalone.Utilities;

import com.miykeal.showCaseStandalone.ShopInternals.Transaction;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
* Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>, Sorklin <sorklin@gmail.com>
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

public class PlayerSessionVariables {
    
    private Map<Player, Boolean> playerIgnoreStoreMessages = new HashMap<Player, Boolean>();
    private Map<Player, Transaction> playerTransaction = new HashMap<Player, Transaction>();
    private Map<Player, Integer> playerTransactionAmount = new HashMap<Player, Integer>();
    
    /**
     * Gets the last transaction for a player, if it exists.
     * @param p The Player
     * @return Transaction or null if it doesn't exist.
     */
    public Transaction getLastTransaction(Player p){
        return (playerTransaction.containsKey(p)) ? playerTransaction.get(p) : null;
    }
    
    /**
     * Gets the transaction amount for a player.
     * @param p The Player
     * @return The transaction amount, or the default amount if the player hasn't
     * set theirs.
     */
    public int getPlayerTransactionAmount(Player p){
        return (playerTransactionAmount.containsKey(p)) 
                ? playerTransactionAmount.get(p)
                : Properties.defaultUnit;
    }
    
    /**
     * Sets the last transaction for a player.  A player will only ever have
     * one transaction saved.
     * @param p The player
     * @param t The last transaction.
     */
    public void setLastTransaction(Player p, Transaction t){
        playerTransaction.put(p, t);
    }
    
    /**
     * Sets the transaction amount the player wants to use.
     * @param p The player
     * @param amount The amount they want to transact with.
     */
    public void setPlayerTransactionAmount(Player p, int amount){
        playerTransactionAmount.put(p, amount);
    }
    
    /**
     * Clears the last transaction for a player.
     */
    public void clearLastTransaction(Player p){
        playerTransaction.remove(p);
    }
    
    /**
     * Clears the stored transaction amount for a player.
     */
    public void clearPlayerTransactionAmount(Player p){
        playerTransactionAmount.remove(p);
    }
    
    /**
     * Clear all transactions.  Usually on unload or reload.
     */
    public void clearAllTransactions(){
        playerTransaction.clear();
    }
    
    /**
     * Clears all stored transaction amounts.  Usually on unload or reload.
     */
    public void clearAllTransactionAmount(){
        playerTransactionAmount.clear();
    }
    
    /**
     * Sets whether to ignore store messages (true) or not (false)
     * @param value 
     */
    public void setIgnoreMessages(Player player, boolean value){
        if(value)
            playerIgnoreStoreMessages.put(player, value);
        else 
            playerIgnoreStoreMessages.remove(player);
    }
    
    public boolean ignoreMessages(Player player) {
        return playerIgnoreStoreMessages.containsKey(player);
    }
}
