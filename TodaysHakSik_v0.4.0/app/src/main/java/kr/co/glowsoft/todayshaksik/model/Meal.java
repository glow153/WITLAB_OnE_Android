package kr.co.glowsoft.todayshaksik.model;

import java.util.ArrayList;

/**
 * Created by Jake Park on 2017-12-11.
 */

public class Meal {
    public static final int BREAKFAST = 0;
    public static final int LUNCH = 1;
    public static final int DINNER = 2;

    private int type;
    private String date;
    private ArrayList<String> menulist;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getMenulist() {
        return menulist;
    }

    public void setMenulist(ArrayList<String> menulist) {
        this.menulist = menulist;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("type=").append(type)
          .append(",date=").append(date)
          .append(",list=");
        for(String menu : menulist)
            sb.append(menu).append(" ");
        return sb.toString();
    }
}
