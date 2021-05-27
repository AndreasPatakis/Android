package com.unipi.talepis.Assignment2;

public class Message implements Comparable<Message>{
    private int num;
    private String what_for;

    public Message(int num, String what_for){
        this.num = num;
        this.what_for = what_for;
    }

    public Message(){

    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getWhat_for() {
        return what_for;
    }

    public void setWhat_for(String what_for) {
        this.what_for = what_for;
    }


    @Override
    public int compareTo(Message o) {
        return this.getNum() - o.getNum();
    }
}
