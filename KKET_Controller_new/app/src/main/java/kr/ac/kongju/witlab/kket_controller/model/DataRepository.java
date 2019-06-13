package kr.ac.kongju.witlab.kket_controller.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WitLab on 2018-04-12.
 */

public class DataRepository {

    //1번조명 제어지표
    private byte[][] packet_table = {
            {(byte)0x01,(byte)0x02,(byte)0,(byte)0,(byte)0,(byte)0},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)0,(byte)0,(byte)0},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)112,(byte)0,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)112,(byte)16,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)48,(byte)96,(byte)16,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)48,(byte)16,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)0,(byte)16,(byte)128},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)48,(byte)48,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)48,(byte)0,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)0,(byte)16,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)112,(byte)32,(byte)0,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)112,(byte)16,(byte)0,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)32,(byte)80,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)48,(byte)96,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)80,(byte)16,(byte)48,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)128,(byte)0,(byte)16,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)16,(byte)48,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)16,(byte)48,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)32,(byte)112,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)16,(byte)80,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)112,(byte)16,(byte)16,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)80,(byte)16,(byte)32,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)96,(byte)32,(byte)16,(byte)48},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)48,(byte)64,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)32,(byte)48,(byte)80},
            {(byte)0x01,(byte)0x02,(byte)80,(byte)64,(byte)16,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)32,(byte)16,(byte)112},
            {(byte)0x01,(byte)0x02,(byte)16,(byte)32,(byte)0,(byte)112},
            {(byte)0x01,(byte)0x02,(byte)64,(byte)96,(byte)0,(byte)16},
            {(byte)0x01,(byte)0x02,(byte)32,(byte)80,(byte)0,(byte)64},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)112,(byte)16,(byte)32},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)128,(byte)0,(byte)48},
            {(byte)0x01,(byte)0x02,(byte)0,(byte)0,(byte)0,(byte)0},
    };


    public double[] swr_table = {
            0.0,
            0.0,
            9.8897598,
            10.762679,
            12.5009767,
            14.0971315,
            15.2614179,
            16.2112631,
            17.1140477,
            17.8580767,
            18.7741458,
            19.4336202,
            19.8628711,
            20.4443775,
            20.7510504,
            21.0944974,
            21.3176027,
            21.6386172,
            22.6005635,
            20.3277845,
            19.768371,
            19.2641216,
            18.4824765,
            17.7587378,
            17.0356279,
            15.9636949,
            15.0054693,
            13.9929965,
            12.8470609,
            11.63457,
            10.0070491,
            8.7797227,
            0.0
    };

    public String[] time_table_s = {
            "", "00:00", "06:30", "06:40", "06:50", "07:00", "07:10", "07:20", "07:30", "07:40",
            "07:50", "08:00", "08:10", "08:20", "08:30", "08:40", "08:50", "09:00", "09:10",
            "15:30", "15:40", "15:50", "16:00", "16:10", "16:20", "16:30", "16:40", "16:50",
            "17:00", "17:10", "17:20", "17:30", "22:00", "23:59"
    };

    private Date[] dateObjTable;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    /////////////////////////////// Singleton odhi start ///////////////////////////////
    private DataRepository() {
        makeDateObjTable();
    }

    private static class SingletonHolder {
        private static final DataRepository instance = new DataRepository();
    }

    public static DataRepository getInstance() {
        return SingletonHolder.instance;
    }
    /////////////////////////////// Singleton odhi end ///////////////////////////////

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

    public int getTotalCount(int tableIndex) {
        return packet_table.length;
    }

    public String getInfo(int timeSeqIndex) {
        if (timeSeqIndex == 0) {
            return "All off";
        } else {
            return "Time: " + getTimeString(timeSeqIndex) +
                    "\nSWR: " + String.format("%.2f%%", swr_table[timeSeqIndex]);
        }
    }
}