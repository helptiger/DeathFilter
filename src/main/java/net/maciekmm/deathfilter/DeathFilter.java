/*
 * Copyright (C) 2014 Maciej Mionskowski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.maciekmm.deathfilter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;

public class DeathFilter extends JavaPlugin implements Listener {
    private enum DeathConfig {
        BLACKLIST, WHITELIST
    }

    private DeathConfig mode;
    private ArrayList<Material> items;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.loadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.loadConfig();
        sender.sendMessage(ChatColor.GREEN+"DeathFilter: "+ChatColor.GRAY+"Reloaded successfully.");
        return true;
    }

    private void loadConfig() {
        try {
            this.mode = DeathConfig.valueOf(this.getConfig().getString("mode"));
        } catch (IllegalArgumentException e) {
            this.mode = DeathConfig.BLACKLIST;
        }

        this.items = new ArrayList<>();
        for (String stringMaterial : this.getConfig().getStringList("materials")) {
            Material mat = Material.matchMaterial(stringMaterial);
            if (mat != null && !this.items.contains(mat)) {
                this.items.add(mat);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Iterator<ItemStack> items = event.getDrops().iterator();
        while (items.hasNext()) {
            ItemStack item = items.next();
            if (this.mode == DeathConfig.BLACKLIST && this.items.contains(item.getType())) {
                items.remove();
            } else if(this.mode == DeathConfig.WHITELIST && !this.items.contains(item.getType())) {
                items.remove();
            }
        }
    }
}
