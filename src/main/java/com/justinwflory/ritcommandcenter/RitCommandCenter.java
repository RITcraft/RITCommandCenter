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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MonsterEggs;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RitCommandCenter extends JavaPlugin implements Listener {

    private static final int TABBY_SPAWN_RANGE = 3;
    private final Map<UUID, Set<UUID>> muted = new HashMap<>();

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

    @EventHandler
    public void eggRitchie(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getItemInHand() == null
                || event.getPlayer().getItemInHand().getType() != Material.EGG) {
            return;
        }
        Entity e = event.getRightClicked();
        if (e.getType() != EntityType.OCELOT) {
            return;
        }
        String name = e.getCustomName();
        String stripped = ChatColor.stripColor(name);
        if (name.length() == stripped.length() || !stripped.equals("Ritchie")) {
            return;
        }
        event.setCancelled(true);
        ItemStack eggs = event.getPlayer().getItemInHand();
        if (eggs.getAmount() > 1) {
            eggs.setAmount(eggs.getAmount() - 1);
            event.getPlayer().setItemInHand(eggs);
        } else {
            event.getPlayer().setItemInHand(null);
        }
        ItemStack regg = new ItemStack(Material.MONSTER_EGG, 1, EntityType.OCELOT.getTypeId());
        regg.getItemMeta().setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Ritchie");
        event.getPlayer().getInventory().addItem(regg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ping(AsyncPlayerChatEvent event) {
        Iterator<Player> recip = event.getRecipients().iterator();
        Set<Player> custom = new HashSet<>();
        while (recip.hasNext()) {
            Player p = recip.next();
            if (event.getMessage().contains(p.getName())) {
                recip.remove();
                custom.add(p);
            }
        }
        ChatColor lastColor = ChatColor.WHITE;
        String[] msgs = event.getPlayer().getDisplayName().split(ChatColor.COLOR_CHAR + "");
        if (msgs.length > 1) {
            char[] arr = msgs[msgs.length - 1].toCharArray();
            if (arr.length > 0) {
                ChatColor fin = ChatColor.getByChar(arr[0]);
                if (fin != null) {
                    lastColor = fin;
                }
            }
        }
        final ChatColor text = lastColor;
        custom.forEach(p -> {
            Set<UUID> muted;
            synchronized (muted = this.getMutedSet(p.getUniqueId())) {
                if (!muted.contains(event.getPlayer().getUniqueId())) {
                    p.sendMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage().replace(p.getName(), ChatColor.YELLOW + p.getName() + text)));
                    p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER

                            , 0.85F, 1.35F);
                }
            }
        });

    }

    private synchronized Set<UUID> getMutedSet(UUID uuid) {
        Set<UUID> back = this.muted.get(uuid);
        if (back == null) {
            back = new HashSet<>();
            this.muted.put(uuid, back);
        }
        return back;
    }

}

// if (tired) justin.goToSleep();