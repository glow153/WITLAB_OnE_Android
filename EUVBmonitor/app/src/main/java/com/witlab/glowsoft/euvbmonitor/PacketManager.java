package com.witlab.glowsoft.euvbmonitor;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WitLab on 2018-04-11.
 */

public class PacketManager {
    private byte[] pktTimesync = {
            (byte) 0x02, //stx
            (byte) 0xff,
            (byte) 0x01, //device id
            (byte) 0xff,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //datetime (idx: 4-20)
            (byte) 0xff,
            (byte) 0x03  //etx
    };

    private PacketManager() {}
    private static class Singleton {
        private static final PacketManager instance = new PacketManager();
    }
    public static PacketManager getInstance() {
        return Singleton.instance;
    }


    public byte[] getPktTimesync() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        byte[] badt = sdf.format(dt).getBytes();

        for (int i = 0; i < badt.length; i++)
            pktTimesync[i + 4] = badt[i];
        return pktTimesync;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x ", b));
        return sb.toString();
    }

    public float parseUvi(byte[] packet) {
        return (int) packet[4] + ((int) packet[5] * 0.01f);
    }

    public float parseEuvb(byte[] packet) {
        int flag = 0xff;
        int integer = (int) packet[7];
        int decimal = 0;
        for (int i = 0; i < 3; i++) {
            decimal |= (packet[10 - i] & flag) << 8 * i;
        }

        Log.d("PacketManager","integer : " + integer + ", decimal : " + decimal);
        return integer + (float) (decimal * 0.0000001);
    }


}
