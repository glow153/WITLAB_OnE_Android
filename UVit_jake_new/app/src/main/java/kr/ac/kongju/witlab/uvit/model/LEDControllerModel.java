package kr.ac.kongju.witlab.uvit.model;

public class LEDControllerModel {
    public static final int CHANNEL_LIMIT = 70;
    public static final int UV_CHANNEL_LIMIT = 45;
    private int[] rawChannelValue;
    private boolean uvOnOff; // on : true, off : false

    /****************** start of thread-safe singleton ******************/
    private LEDControllerModel() {
        rawChannelValue = new int[4];
        uvOnOff = false;
    }
    private static volatile LEDControllerModel INSTANCE = new LEDControllerModel();
    public static synchronized LEDControllerModel getInstance() {
        return INSTANCE;
    }
    /****************** end of thread-safe singleton ******************/

    public void setValue(int ch, int value) {
        if (ch >= 0 && ch <= 3) {
            if (value <= CHANNEL_LIMIT)
                rawChannelValue[ch] = value;
        }
    }

    public void setUvOnOff(boolean b) {
        uvOnOff = b;
    }

    public int getRawValue(int ch) { return rawChannelValue[ch]; }
    public int[] getRawValue() { return rawChannelValue; }

    public boolean isUVOn() {
        return uvOnOff;
    }
}
