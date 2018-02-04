/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.a5days.rumahmakan;

import java.text.DecimalFormat;


public class FilterInput {

    public static String toIdr(String input) {
        DecimalFormat df = new DecimalFormat("#,###");
        try {
            String temp = "";
            if (input.length() == 0) {
                temp = "0";
            } else {
                if (input.charAt(0) == '-') {
                    temp = "0";
                } else {
                    temp = input.replace(",", "");
                }
            }
            return df.format(Integer.parseInt(temp));
        } catch (NumberFormatException e) {
            System.out.println("e " + e.getMessage());
        }
        return null;
    }

    public static String toPositiveInt(String input) {
        try {
            String temp = "";
            if (input.length() == 0) {
                temp = "0";
            } else {
                if(input.charAt(0)=='-'){
                    temp = "0";
                }else{
                    temp = String.valueOf(Integer.parseInt(input));
                }
                
            }
            return temp;
        } catch (Exception e) {
        }
        return null;
    }
}
