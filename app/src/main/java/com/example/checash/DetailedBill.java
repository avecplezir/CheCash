package com.example.checash;


import java.math.BigInteger;

public class DetailedBill implements java.io.Serializable {

    public String dateTime;
    public int nds18;
    public  int taxation_type;
    public int ecash_total_sum;
    public QR qr;
    public BigInteger fiscalSign;
    public int cashTotalSum;
    public int total_sum;
    public  int operationType;
    public Items[] items;

    public class QR{
        public String i;
        public String n;
        public  String fn;
        public String fp;
    }

    public class Items{
        public String name;
        public String description;
        public  int price;
        public String url;
    }
}
