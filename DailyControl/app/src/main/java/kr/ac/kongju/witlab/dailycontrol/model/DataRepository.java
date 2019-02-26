package kr.ac.kongju.witlab.dailycontrol.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WitLab on 2018-04-12.
 */

public class DataRepository {
    byte[][] packet_table = { // 17개 + (0, 0, 0, 0)
            {(byte)0x01,(byte)0x02,(byte)0,(byte)0,(byte)0,(byte)0},
            {(byte)0x01,(byte)0x02,(byte)48,(byte)160,(byte)0,(byte)0},
            {(byte)0x01,(byte)0x02,(byte)48,(byte)144,(byte)16,(byte)47},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)112,(byte)16,(byte)48},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)32,(byte)32,(byte)128},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)16,(byte)0,(byte)128},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)80,(byte)16,(byte)48},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)80,(byte)32,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)80,(byte)112,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)80,(byte)0,(byte)96,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)80,(byte)96,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)48,(byte)64,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)64,(byte)80,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)80,(byte)32,(byte)48},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)80,(byte)16,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)48,(byte)0,(byte)128},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)128,(byte)32,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)144,(byte)0,(byte)80},
    };


    public double[] swr_table = { // 17개 + 0.0
            0.0,
            0.098261493,
            0.109309023,
            0.135282586,
            0.151202841,
            0.162322763,
            0.17136888,
            0.179242176,
            0.187110327,
            0.22741449,
            0.182604042,
            0.17731967,
            0.166622963,
            0.155578517,
            0.14454029,
            0.131037491,
            0.114536994,
            0.093968891

    };

    public String[] time_table_s = { // 20개
            "", "00:00", "06:10", "06:20", "06:30", "06:40", "06:50", "07:00", "07:10", "07:20",
            "17:40", "17:50", "18:00", "18:10", "18:20", "18:30", "18:40", "18:50", "23:59"
//            "16:45", "16:46", "16:47", "16:48", "16:28", "16:29", "16:30", "16:31", "16:32"//for test
    };

    private Date[] dateObjTable;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    /////////////////////////////// Singleton start ///////////////////////////////
    private DataRepository() {
        makeDateObjTable();
//        initTimeTable();
    }

    private final static DataRepository INSTANCE = new DataRepository();

    public static DataRepository getInstance() {
        return INSTANCE;
    }
    /////////////////////////////// Singleton end ///////////////////////////////

    private void makeDateObjTable() {
        Calendar cal = Calendar.getInstance();
        dateObjTable = new Date[time_table_s.length];

        // set first item
        cal.set(0, 0, 0, 0, 0, 0);
        dateObjTable[0] = cal.getTime();

        for (int i = 1; i < time_table_s.length; i++) {
            try {
                cal.setTime(sdf.parse(time_table_s[i]));
            } catch (ParseException pe) {
                pe.printStackTrace();
                return;
            }
            cal.set(0, 0, 0);

            dateObjTable[i] = cal.getTime();
        }
    }

    public Date[] getDateObjTable() {
        return dateObjTable;
    }

    private String getTimeString(int index) {
        return time_table_s[index] + " ~ " + time_table_s[index + 1];
    }

    public byte[] getPacket(int index) {
        return packet_table[index];
    }

    public int getTotalCount() {
        return packet_table.length;
    }

    public String getInfo(int index) {
        if (index == 0)
            return "All off";
        else
            return "SWR: " + swr_table[index] + ", Time: " + getTimeString(index);
    }
}
