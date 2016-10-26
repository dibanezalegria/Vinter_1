package com.example.android.vinter_1.data;


/**
 * Bean representing a Test object
 */
public class Test {

    // Test can be in three states
    public static final int TEST_BLANK = 0;
    public static final int TEST_INCOMPLETED = 1;
    public static final int TEST_COMPLETED = 2;

    private String code;
    private String name;
    private int inState;    // IN test
    private int outState;   // OUT test

    public Test(String code, String name, int inState, int outState) {
        this.code = code;
        this.inState = inState;
        this.name = name;
        this.outState = outState;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getInState() {
        return inState;
    }

    public int getOutState() {
        return outState;
    }

    public void setInState(int inState) {
        this.inState = inState;
    }

    public void setOutState(int outState) {
        this.outState = outState;
    }

    @Override
    public String toString() {
        return "Test{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", inState=" + inState +
                ", outState=" + outState +
                '}';
    }
}
