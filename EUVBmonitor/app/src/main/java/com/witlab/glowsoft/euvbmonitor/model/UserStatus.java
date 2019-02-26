package com.witlab.glowsoft.euvbmonitor.model;

/**
 * Created by WitLab on 2018-04-02.
 */

public class UserStatus {
    private int age = 0;
    private int sex = 1; // male == 1, female = 0
    private int skintype = 3;
    private int vitd_res = 0;
    private int exposeAreaUpper = 0;
    private int exposeAreaLower = 0;

    private UserStatus() {}
    private static class Singleton {
        private static final UserStatus instance = new UserStatus();
    }
    public static UserStatus getInstance() {
        return Singleton.instance;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSkintype() {
        return skintype;
    }

    public void setSkintype(int skintype) {
        this.skintype = skintype;
    }

    public int getVitd_res() {
        return vitd_res;
    }

    public void setVitd_res(int vitd_res) {
        this.vitd_res = vitd_res;
    }

    public int getExposeAreaUpper() {
        return exposeAreaUpper;
    }

    public void setExposeAreaUpper(int exposeAreaUpper) {
        this.exposeAreaUpper = exposeAreaUpper;
    }

    public int getExposeAreaLower() {
        return exposeAreaLower;
    }

    public void setExposeAreaLower(int exposeAreaLower) {
        this.exposeAreaLower = exposeAreaLower;
    }

    public float getMEDF() {
        return 3;
    }

    public float getAgeFactor() {
        return 1;
    }
}
