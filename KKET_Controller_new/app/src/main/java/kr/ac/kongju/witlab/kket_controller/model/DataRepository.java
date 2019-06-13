package kr.ac.kongju.witlab.kket_controller.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WitLab on 2018-04-12.
 */

public class DataRepository {
    private final byte[][] packet_table_15 = {
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 255, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 255, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 255, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 255,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 127, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 127, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 0, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 127, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 0, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 128, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 85, (byte) 85, (byte) 85, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 85, (byte) 85, (byte) 0, (byte) 85,},
            {(byte) 0x01, (byte) 0x02, (byte) 85, (byte) 0, (byte) 85, (byte) 85,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 85, (byte) 85, (byte) 85,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 64, (byte) 63,},
    };

    private final byte[][] packet_table_69 = {
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 128,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 192,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 255,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 64, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 64, (byte) 128,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 64, (byte) 191,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 128, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 128, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 128, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 192, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 192, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 255, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 0, (byte) 128,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 0, (byte) 191,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 64, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 64, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 128, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 128, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 64, (byte) 191, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 0, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 64, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 128, (byte) 127, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 192, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 192, (byte) 0, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 192, (byte) 63, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 255, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 0, (byte) 128,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 0, (byte) 191,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 64, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 64, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 128, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 128, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 191, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 0, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 64, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 64, (byte) 127, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 128, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 128, (byte) 0, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 128, (byte) 63, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 191, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 0, (byte) 64,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 0, (byte) 127,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 64, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 64, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 0, (byte) 127, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 64, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 64, (byte) 0, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 64, (byte) 63, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 128, (byte) 127, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 192, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 192, (byte) 0, (byte) 0, (byte) 63,},
            {(byte) 0x01, (byte) 0x02, (byte) 192, (byte) 0, (byte) 63, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 192, (byte) 63, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 255, (byte) 0, (byte) 0, (byte) 0,},
    };

    private final byte[][] packet_table_testmode = {
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 0, (byte) 0,},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 16, (byte) 16, (byte) 80},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 80, (byte) 160, (byte) 0},
            {(byte) 0x01, (byte) 0x02, (byte) 96, (byte) 0, (byte) 96, (byte) 16},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 176, (byte) 79},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 176, (byte) 79},
            {(byte) 0x01, (byte) 0x02, (byte) 112, (byte) 16, (byte) 80, (byte) 0},
            {(byte) 0x01, (byte) 0x02, (byte) 80, (byte) 32, (byte) 16, (byte) 80},
            {(byte) 0x01, (byte) 0x02, (byte) 16, (byte) 64, (byte) 0, (byte) 128},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 0, (byte) 48, (byte) 192},
            {(byte) 0x01, (byte) 0x02, (byte) 32, (byte) 16, (byte) 64, (byte) 48},
            {(byte) 0x01, (byte) 0x02, (byte) 32, (byte) 0, (byte) 128, (byte) 64},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 48, (byte) 192, (byte) 15},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 144, (byte) 16},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 144, (byte) 16},
            {(byte) 0x01, (byte) 0x02, (byte) 64, (byte) 0, (byte) 144, (byte) 16},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 48, (byte) 192, (byte) 15},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 48, (byte) 192, (byte) 15},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 48, (byte) 128, (byte) 32},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 48, (byte) 192, (byte) 15},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 16, (byte) 112, (byte) 112},
            {(byte) 0x01, (byte) 0x02, (byte) 0, (byte) 176, (byte) 16, (byte) 16},
    };



    /////////////////////////////// Singleton odhi start ///////////////////////////////
    private DataRepository() {}

    private static class SingletonHolder {
        private static final DataRepository instance = new DataRepository();
    }

    public static DataRepository getInstance() {
        return SingletonHolder.instance;
    }
    /////////////////////////////// Singleton odhi end ///////////////////////////////



    public static String customPacketToStr(byte[] packet, boolean base10) {
        StringBuilder sb = new StringBuilder(packet.length * 2);

        sb.append(String.format(base10 ? "%d" : "%x", (int)packet[2]));
        for (int j = 3; j < 6; j++) {
            int b = (int)packet[j];
            if (b < 0)
                b += 256;
            sb.append(String.format(base10 ? " %d" : " %x", b));
        }
        return sb.toString();
    }

    public byte[] getPacket(int type, int index) {
        if (type == 0)
            return packet_table_15[index];
        else if (type == 1)
            return packet_table_69[index];
        else// if(type == 2)
            return packet_table_testmode[index];
    }

    public int getPacketTableCount(int type) {
        if (type == 0)
            return packet_table_15.length;
        else if (type == 1)
            return packet_table_69.length;
        else// if (type == 2)
            return packet_table_testmode.length;
    }

    public String getPacketTitle(int i) {
        if (i == 0) {
            return "All Off";
        } else {
            return "Level " + i;
        }
    }

    public String getPacketString(int type, int i) {
        byte[] barr = null;

        if (type == 0)
            barr = packet_table_15[i];
        else if (type == 1)
            barr = packet_table_69[i];
        else// if(type == 2)
            barr = packet_table_testmode[i];

        StringBuilder sb = new StringBuilder(barr.length * 2);
        for (int j = 2; j < 6; j++) {
            int n = (int) barr[j];
            if(n < 0)
                n += 256;
            sb.append(n)
                    .append(" ");
        }
        return sb.toString();
    }
}
