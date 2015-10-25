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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class RitCommand
        implements CommandExecutor
{
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax: try /rit food");
        } else {
            try
            {
                commandSender.sendMessage(ChatColor.GREEN + "Open places to eat: ");
                Document doc = Jsoup.connect("https://rit-apps.modolabs.net/dining/index").get();
                Elements open = doc.select("li.kgo_location_open");
                for (Iterator localIterator1 = open.iterator(); localIterator1.hasNext();)
                {
                    element = (Element)localIterator1.next();
                    Elements ahrefList = element.getElementsByClass("kgoui_list_item_action");
                    Element ahref = ahrefList.get(0);
                    Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                    Element div = adiv.get(0);
                    Elements subDiv = div.getAllElements();
                    for (Iterator localIterator2 = subDiv.iterator(); localIterator2.hasNext();)
                    {
                        subDivInfo = (Element)localIterator2.next();
                        commandSender.sendMessage(ChatColor.GOLD + subDivInfo.text());
                    }
                    commandSender.sendMessage("");
                }
                Element element;
                Element subDivInfo;
                commandSender.sendMessage(ChatColor.GREEN + "Closed places to eat: ");
                Elements close = doc.select("li.kgo_location_closed");
                for (Element element : close)
                {
                    ahrefList = element.getElementsByClass("kgoui_list_item_action");
                    Element ahref = (Element)ahrefList.get(0);
                    Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                    Element div = adiv.get(0);
                    Elements subDiv = div.getAllElements();
                    for (Element subDivInfo : subDiv) {
                        commandSender.sendMessage(ChatColor.RED + subDivInfo.text());
                    }
                    commandSender.sendMessage("");
                }
                Document doc2 = Jsoup.connect("https://rit-apps.modolabs.net/dining/index?feed=dining&start=15").get();
                Elements close2 = doc2.select("li.kgo_location_closed");
                for (Element element : close2)
                {
                    Elements ahrefList = element.getElementsByClass("kgoui_list_item_action");
                    Element ahref = ahrefList.get(0);
                    Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                    Element div = adiv.get(0);
                    Elements subDiv = div.getAllElements();
                    for (Element subDivInfo : subDiv) {
                        commandSender.sendMessage(ChatColor.RED + subDivInfo.text());
                    }
                    commandSender.sendMessage("");
                }
            }
            catch (IOException e)
            {
                Elements ahrefList;
                e.printStackTrace();
            }
        }
        return true;
    }
}
