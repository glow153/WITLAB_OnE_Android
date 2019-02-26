package kr.ac.kongju.witlab.dailycontrol_multi.adapter;

public class ListViewItem {
    private int index;
    private String title;
    private byte[] packet;
    private String info;
    private boolean checked = false;

    public ListViewItem() {
        index = 0;
        packet = new byte[6];
        title = "";
        info = "";
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private String packetToStr() {
        StringBuilder sb = new StringBuilder(packet.length * 2);
        for (int j = 2; j < 6; j++) {
            int b = (int)packet[j];
            if (b < 0)
                b += 256;
            sb.append(String.format("%d ", b));
        }
        return sb.toString();
    }

    public void setPacket(byte[] pkt) {
        packet = pkt;
        title = index + ") " + packetToStr();
    }

    public String getTitle() {
        return title;
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
