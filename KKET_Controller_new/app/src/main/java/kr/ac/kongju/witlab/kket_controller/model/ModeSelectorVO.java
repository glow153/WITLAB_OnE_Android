package kr.ac.kongju.witlab.kket_controller.model;

public class ModeSelectorVO {
    // singleton
    private ModeSelectorVO() {
        mode = 0;
    }

    private static class SingletonHolder {
        private static final ModeSelectorVO instance = new ModeSelectorVO();
    }

    public static ModeSelectorVO getInstance() {
        return SingletonHolder.instance;
    }
    // end of singleton

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
