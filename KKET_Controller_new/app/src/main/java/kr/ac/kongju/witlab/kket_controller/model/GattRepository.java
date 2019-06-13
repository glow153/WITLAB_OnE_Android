package kr.ac.kongju.witlab.kket_controller.model;

import java.util.UUID;

public class GattRepository {
    public final static UUID UUID_BLUNO_NANO_SERVICE = UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_BLUNO_NANO_WRITE_CHAR = UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_BLUNO_NANO_READ_CHAR = UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb");

    public final static UUID UUID_HM10_SERVICE = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");;
    public final static UUID UUID_HM10_WRITE_CHAR = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
    public final static UUID UUID_HM10_READ_CHAR = UUID.fromString("0000FFE2-0000-1000-8000-00805F9B34FB");

}
