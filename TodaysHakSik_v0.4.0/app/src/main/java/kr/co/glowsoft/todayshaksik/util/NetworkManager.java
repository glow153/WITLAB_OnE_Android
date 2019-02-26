package kr.co.glowsoft.todayshaksik.util;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import kr.co.glowsoft.todayshaksik.model.Meal;
import kr.co.glowsoft.todayshaksik.model.MealSet;

/**
 * Created by WitLab on 2017-12-06.
 */

public class NetworkManager {
    private final String sUrl = "http://www.kongju.ac.kr/service/food_view_w.jsp?code=C002&idx=21";
    private Document doc;

    private NetworkManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(sUrl).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private static class Singleton {
        private static final NetworkManager instance = new NetworkManager();
    }
    public static NetworkManager getInstance() {
        return Singleton.instance;
    }

    public void get() {

    }

    public String getDate() {
        Elements esToday = doc.select("span.con_title3");
        return esToday.text().split(" ")[1];
    }

    public String[] getTodaysMenu() {
        String[] menu = new String[3];

        Element[] weeklyTable = new Element[3];

        Elements esTable = doc.select("body table");
        for (int i=0; i<3; i++)
            weeklyTable[i] = esTable.get(i);

        Log.d("학생식당", weeklyTable[0].text());
        Log.d("직원식당", weeklyTable[1].text());
        Log.d("기숙사식당", weeklyTable[2].text());

        menu[0] = weeklyTable[0].text();
        menu[1] = weeklyTable[1].text();
        menu[2] = weeklyTable[2].text();

        Elements es = doc.select("body table.bus_table tbody tr");
        if (es != null) {
            int idx = 0;
            for (Element element : es) {
                Log.d("netmgr/getTodaysMenu()", "todays menu : " + element.html());
                if (element.select("th").text().equals("중식")) {
                    Log.d("Today's NetworkManager", element.select("td.toady").text());
                    menu[idx++] = element.select("td.toady").text();
                }
            }
        } else {
            menu = null;
        }

        return menu;
    }

    public ArrayList<Meal> getMenuOfWeek(int cafeteria) {
        // cafeteria : 0-2 (s,d,e)
        ArrayList<Meal> list = new ArrayList<>();
        Elements els = doc.select("body table.bus_table tbody tr:nth-child(3) td");
        for(Element e : els) {
            Meal m = new Meal();
            m.setMenulist(new ArrayList<>(Arrays.asList(e.text().split(" "))));
            list.add(m);
        }
        return list;
    }
}
