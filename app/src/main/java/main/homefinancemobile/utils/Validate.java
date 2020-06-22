package main.homefinancemobile.utils;

public class Validate {
    public static boolean isEmpty(Object s) {
        if (s == null || s.equals("") || s.equals(" ")) {
            return true;
        } else {
            return false;
        }
    }
}
