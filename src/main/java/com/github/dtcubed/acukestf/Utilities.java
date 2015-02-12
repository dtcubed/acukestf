package com.github.dtcubed.acukestf;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class Utilities {

    public static String get_random_string()
    {
        Date date  = new Date();
        Random rng = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss-SSS-");

        // Concatenate the formatted data/time (out to the millisecond) with a
        // pseudo-random number. This forms a "random enough" string.

        String str2return = dateFormat.format(date) + Integer.toString(rng.nextInt());

        return str2return;
    }

    public static void main(String [ ] args)
    {
        String rs = get_random_string();

        System.out.println(rs);

    }
}
