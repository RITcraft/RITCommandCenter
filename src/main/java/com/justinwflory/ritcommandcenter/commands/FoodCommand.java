package com.justinwflory.ritcommandcenter.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Rogue on 10/25/2015.
 */
public class FoodCommand implements SubCommand {

    @Override
    public void exec(CommandSender sender, String... args) {
        try {
            sender.sendMessage(ChatColor.GREEN + "Open places to eat: ");
            Connection.Response r = Jsoup.connect("https://rit-apps.modolabs.net/dining/index").response();
            System.out.println("Response: " + r.statusCode() + ", " + r.statusMessage());
            System.out.println("\tHeaders: " + r.headers());
            System.out.println("\tCookies: " + r.cookies());
            System.out.println("\tBody:\n" + r.parse().toString());
            Document doc = r.parse();
            Elements open = doc.select("li.kgo_location_open");
            for (Iterator localIterator1 = open.iterator(); localIterator1.hasNext(); ) {
                Element element = (Element) localIterator1.next();
                Elements ahrefList = element.getElementsByClass("kgoui_list_item_action");
                Element ahref = ahrefList.get(0);
                Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                Element div = adiv.get(0);
                Elements subDiv = div.getAllElements();
                for (Iterator localIterator2 = subDiv.iterator(); localIterator2.hasNext(); ) {
                    Element subDivInfo = (Element) localIterator2.next();
                    sender.sendMessage(ChatColor.GOLD + subDivInfo.text());
                }
                sender.sendMessage("");
            }
            Element element;
            Element subDivInfo;
            sender.sendMessage(ChatColor.GREEN + "Closed places to eat: ");
            Elements close = doc.select("li.kgo_location_closed");
            for (Element element2 : close) {
                Elements ahrefList = element2.getElementsByClass("kgoui_list_item_action");
                Element ahref = (Element) ahrefList.get(0);
                Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                Element div = adiv.get(0);
                Elements subDiv = div.getAllElements();
                for (Element subDivInfo2 : subDiv) {
                    sender.sendMessage(ChatColor.RED + subDivInfo2.text());
                }
                sender.sendMessage("");
            }
            Document doc2 = Jsoup.connect("https://rit-apps.modolabs.net/dining/index?feed=dining&start=15").get();
            Elements close2 = doc2.select("li.kgo_location_closed");
            for (Element element2 : close2) {
                Elements ahrefList = element2.getElementsByClass("kgoui_list_item_action");
                Element ahref = ahrefList.get(0);
                Elements adiv = ahref.getElementsByClass("kgoui_list_item_textblock");
                Element div = adiv.get(0);
                Elements subDiv = div.getAllElements();
                for (Element subDivInfo2 : subDiv) {
                    sender.sendMessage(ChatColor.RED + subDivInfo2.text());
                }
                sender.sendMessage("");
            }
        } catch (IOException e) {
            Elements ahrefList;
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "food";
    }

}