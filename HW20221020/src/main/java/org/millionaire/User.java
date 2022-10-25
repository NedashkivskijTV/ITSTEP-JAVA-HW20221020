package org.millionaire;

import java.util.Scanner;

public class User {
    private String name;
    private int prizeMoney;

    public User() {
        this.name = "User";
        this.prizeMoney = 0;
    }
    public User(String name, int prizeMoney) {
        this.name = name;
        this.prizeMoney = prizeMoney;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrizeMoney() {
        return prizeMoney;
    }

    public void setPrizeMoney(int prizeMoney) {
        this.prizeMoney = prizeMoney;
    }

    public String enterName(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", prizeMoney=" + prizeMoney +
                '}';
    }
}
