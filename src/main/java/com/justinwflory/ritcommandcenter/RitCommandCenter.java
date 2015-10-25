/*
 * Copyright 2015 RITcraft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.justinwflory.ritcommandcenter;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public class RitCommandCenter extends JavaPlugin implements Listener {

    private static final int TABBY_SPAWN_RANGE = 3;

    public void onEnable() {
        getCommand("rit").setExecutor(new RitCommand());
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onRitchieCat(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getAction() != Action.RIGHT_CLICK_AIR
                || event.getItem() == null) {
            return;
        }
        ItemMeta meta = event.getItem().getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }
        String name = meta.getDisplayName();
        String stripped = ChatColor.stripColor(name);
        if (name.length() == stripped.length() || !stripped.equals("Ritchie")) {
            return; //no custom named eggs making ritchies
        }
        //at this point we've validated their egg
        event.setUseItemInHand(Event.Result.DENY);
        if (event.getItem().getAmount() > 1) {
            ItemStack set = event.getPlayer().getItemInHand();
            set.setAmount(set.getAmount() - 1);
            event.getPlayer().setItemInHand(set);
        } else {
            event.getPlayer().setItemInHand(null);
        }
        Player p = event.getPlayer();
        Location spawn;
        if (p.getLocation().getPitch() > 0) {
            spawn = p.getEyeLocation().add(p.getLocation().getDirection().normalize().multiply(TABBY_SPAWN_RANGE));
        } else {
            BlockIterator itr = new BlockIterator(p.getWorld(), p.getLocation().toVector(), p.getLocation().getDirection(), 0, TABBY_SPAWN_RANGE);
            if (itr.hasNext()) {
                Block b = itr.next();
                Block up = b.getRelative(BlockFace.UP);
                Material m = up.getType();
                if (m != Material.AIR && (m.isBlock() && (m.isSolid() || !m.isTransparent()))) {
                    spawn = up.getLocation();
                } else {
                    spawn = p.getLocation();
                }
            } else {
                spawn = p.getLocation();
            }
        }
        Ocelot o = spawn.getWorld().spawn(spawn, Ocelot.class);
        o.setCatType(Ocelot.Type.RED_CAT);
        o.setBaby();
        o.setAge(-Integer.MAX_VALUE);
        o.setAgeLock(true);
        o.setCustomName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Ritchie");
        o.setCustomNameVisible(true);
        o.setOwner(p);
        o.setSitting(true);
        o.setTamed(true);
        o.setTarget(p);
    }

    @EventHandler
    public void hitRitchie(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.OCELOT
                && event.getEntity().getCustomName() != null
                && ChatColor.stripColor(event.getEntity().getCustomName()).equals("Ritchie")) {
            event.setCancelled(true);
        }
    }

}

// if (tired) justin.goToSleep();