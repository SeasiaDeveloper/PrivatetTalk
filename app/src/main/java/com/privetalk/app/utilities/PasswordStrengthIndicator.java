package com.privetalk.app.utilities;

/**
 * Created by zachariashad on 29/12/15.
 */
public abstract class PasswordStrengthIndicator {


    private int up = 0, low = 0, no = 0, spl = 0, xtra = 0, len = 0, points = 0, max = 8;
    private char c;

    public PasswordStrengthIndicator(String pass) {
        len = pass.length();
        if (len == 0) {
            passwordStrength(0, "Invalid Input ");
            return;
        }
        if (len <= 5) points++;
        else if (len <= 10) points += 2;
        else
            points += 3;
        for (int i = 0; i < len; i++) {
            c = pass.charAt(i);
            if (c >= 'a' && c <= 'z') {
                if (low == 0) points++;
                low = 1;
            } else {
                if (c >= 'A' && c <= 'Z') {
                    if (up == 0) points++;
                    up = 1;
                } else {
                    if (c >= '0' && c <= '9') {
                        if (no == 0) points++;
                        no = 1;
                    } else {
                        if (c == '_' || c == '@') {
                            if (spl == 0) points += 1;
                            spl = 1;
                        } else {
                            if (xtra == 0) points += 2;
                            xtra = 1;

                        }
                    }
                }
            }
        }

        passwordStrength(points, "nada");

    }

    public abstract void passwordStrength(int strength, String message);
}
