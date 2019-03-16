package com.cube;
import java.io.*;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class client_to_client {
    private int timeStamp; // current time stamp
    public static int[][] Rlight; //red light duration
    public static int[][] Glight; //green light duration
    public static int[][] passTime;
    private static int currentX;
    private static int currentY;
    public static List<Integer> xpath = new ArrayList<Integer>();
    public static List<Integer> ypath = new ArrayList<Integer>();

    public static void inputData(int N, int M, int x0, int y0, int x1, int y1)
    {
        xpath.add(x0);
        ypath.add(y0);
        currentX = x0;
        currentY = y0;
        while(currentX != x1 || currentY != y1)
        {
            if(currentX < x1)
            {
                currentX++;
            }
            else if(currentX > x1)
            {
                currentX--;
            }
            else if(currentY < y1)
            {
                currentY++;
            }
            else
            {
                currentY--;
            }
            xpath.add(currentX);
            ypath.add(currentY);
        }
        xpath.add(currentX);
        ypath.add(currentY);

    }


}