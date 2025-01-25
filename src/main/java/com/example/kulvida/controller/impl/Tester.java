package com.example.kulvida.controller.impl;

public class Tester {

    public static void main(String[] args){

        StringBuilder receipt = new StringBuilder();
        receipt.append(String.format("%-10s%10s%6s%12s%n", "Item", "Size", "Qty", "Price"));
        receipt.append(String.format("%-10s%10s%6d%12.2f%n", "PANTALON JUP".substring(0,15),"M", 2, 10.2 ));



        System.out.println(receipt.toString());


    }
}
