package com.miykeal.showCaseStandalone.Utilities;

import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;

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

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class Localization {
    
    private static final HashMap<String, String> messages = new HashMap<String, String>();
    private static File localeFile = null; 
    private static FileConfiguration localeConfig = null; 
    
    public static void init(String fileName) throws IOException {
        
        if(!getLocaleFile(fileName)){
            ShowCaseStandalone.slog(Level.WARNING, "Could not find " + fileName + ".");
            boolean found = false;
            for (String s : Properties.defaultLocaleFiles){
                if(s.equalsIgnoreCase(fileName)){
                    ShowCaseStandalone.get().saveResource(s, true);
                    found = getLocaleFile(fileName);
                }
            }
            if(!found){
                ShowCaseStandalone.slog(Level.WARNING, "Could not find, connect, or create " + fileName + " from the default language files.");
                ShowCaseStandalone.slog(Level.INFO, "Changing language to EN.");
                if(!getLocaleFile(Properties.defaultLocaleFiles[0])){
                    ShowCaseStandalone.get().saveResource(Properties.defaultLocaleFiles[0], true);
                    if(!getLocaleFile(Properties.defaultLocaleFiles[0]))
                        throw new IOException("Could not connect or create a valid locale file.");
                }
            } else {
                ShowCaseStandalone.slog(Level.INFO, "Found or created locale file.");
            }
        }
        
        loadLocaleMessages(localeFile);

    }
    
    private static boolean getLocaleFile(String fileName){
        localeFile = new File(ShowCaseStandalone.get().getDataFolder(), fileName);
        return (localeFile.exists());
    }
    
    public static String get(String key){
        return (messages.containsKey(key.toLowerCase())) 
                ? messages.get(key.toLowerCase()) 
                : "`RLOCALIZATION: Missing value for: " + key; //for debugging.
    }
    
    public static void dumpMessages(){
        if(!messages.isEmpty()){
            for (Iterator<Entry<String, String>> it = messages.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                ShowCaseStandalone.slog(Level.INFO, entry.getKey() + " : " + entry.getValue());
            }
        } else
            ShowCaseStandalone.slog(Level.INFO, "The messages map is empty.");
    }
    
    private static void loadLocaleMessages(File lcl) throws IOException {
        
        //FileInputStream fis = new FileInputStream(lcl);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lcl), "UTF8"));
        String nl;
        String[] p;
        String msg;
        
        messages.clear();
        
        while ((nl = br.readLine()) != null) {
            
            //Ignore comments
            if(nl.startsWith("#"))
                continue;
            
            if(nl.contains(":")){
                p = nl.split(":");
                
                //clean the message string
                msg = nl.substring(p[0].length() + 1).trim();

                if(msg.startsWith("\""))
                    msg = msg.substring(1);
                if(msg.endsWith("\""))
                    msg = msg.substring(0, msg.length() - 1);

                ShowCaseStandalone.slog(Level.FINEST, "localemsg: " + p[0] + ":" + msg);
                messages.put(p[0].toLowerCase(), msg);
            } 
            
            
        } 
        br.close();
    }
}
