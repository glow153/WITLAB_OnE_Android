package kr.ac.kongju.witlab.kket_controller.adapter;

public class LevelListItem {
    private int index;
    private String title;
    private byte[] packet;
    private String info;
    private boolean checked = false;

    public LevelListItem() {
        index = 0;
        packet = new byte[6];
        title = "";
        info = "";
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int b2i(byte b) {
        if (b < 0)
            return b + 256;
        else
            return b;
    }

    public String packetToStr(boolean base10) {
        String fmtStr_10 = "%4d %4d %4d %4d";
        String fmtStr_16 = "hex: 0x%02x %02x %02x %02x";

        if (base10) {
            return String.format(fmtStr_10,
                    b2i(packet[2]), b2i(packet[3]), b2i(packet[4]), b2i(packet[5]));
        } else {
            return String.format(fmtStr_16,
                    b2i(packet[2]), b2i(packet[3]), b2i(packet[4]), b2i(packet[5]));
        }
    }

    public void setPacket(byte[] pkt) {
        packet = pkt;
    }

    public void setTitle() {
        this.title = index + ") " + packetToStr(true);
    }

    public void setTitle(String title) {
        if (title.equals("")) {
            this.title = index + ") " + packetToStr(true);
        } else {
            this.title = title;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setInfo() {
        this.info = packetToStr(false);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setChecked(boolean check) {
        this.checked = check;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        return title + ", checked:" + checked + ", info:" + info;
    }
}
