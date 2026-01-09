package com.example.myapplication;
//This class is the creation of an Employees Array.
//Takes necessary data from database and stores it into the array when read
public class Employees {
    private String FName, LName;
    private String Sun, Mon, Tue, Wed, Thu, Fri, Sat;
    private String Open, Close;
    private String Email, Phone;

    public Employees(){}

    public String getFName() {
        return FName;
    }
    public String getLName() {
        return LName;
    }

    public String getSun() {
        return Sun;
    }
    public String getMon() {
        return Mon;
    }
    public String getTue() {
        return Tue;
    }
    public String getWed() {
        return Wed;
    }
    public String getThu() {
        return Thu;
    }
    public String getFri() {
        return Fri;
    }
    public String getSat() {
        return Sat;
    }
    public String getOpen() {return Open;}
    public String getClose(){return Close;}

    public String getEmail() {
        return Email;
    }
    public String getPhone() {
        return Phone;
    }

    public Employees(String FName, String LName, String Mon, String Tue, String Wed,
                     String Thu, String Fri, String Sat, String Sun, String Open, String Close, String Email, String Phone) {
        this.FName = FName;
        this.LName = LName;
        this.Sun = Sun;
        this.Mon = Mon;
        this.Tue = Tue;
        this.Wed = Wed;
        this.Thu = Thu;
        this.Fri = Fri;
        this.Sat = Sat;
        this.Open = Open;
        this.Close = Close;
        this.Email = Email;
        this.Phone = Phone;
    }

}
