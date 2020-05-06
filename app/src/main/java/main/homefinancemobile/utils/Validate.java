package main.homefinancemobile.utils;

public class Validate {
    public static boolean isEmpty(String s) {
        if (s.equals("") || s == null) {
            return true;
        } else {
            return false;
        }
    }
}
