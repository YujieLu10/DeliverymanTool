package com.cube;

import java.util.ArrayList;
import java.util.List;


public class client_to_client {
    public static long timeStamp = 0; // current time stamp
    public static long[][] Rlight; //red light duration
    public static long[][] Glight; //green light duration
    public static long[][] passTime;
    private static int currentX;
    private static int currentY;
    public static int startX;
    public static int startY;
    public static List<Integer> xpath = new ArrayList<Integer>();
    public static List<Integer> ypath = new ArrayList<Integer>();

    public static void inputData(int N, int M, int x0, int y0, int x1, int y1)
    {
        currentX = x0;
        currentY = y0;
        while(currentX != x1 || currentY != y1)
        {

            xpath.add(currentX);
            ypath.add(currentY);

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
            timeStamp += 30;
            if(currentX == startX && currentY ==startY)
                break;

            long time = timeStamp % (Glight[currentX][currentY] + Rlight[currentX][currentY]);

            if( time + passTime[currentX][currentY] <= Glight[currentX][currentY] )//green enough
            {
                timeStamp = timeStamp + passTime[currentX][currentY];
            }
            else
            {
                timeStamp = timeStamp + passTime[currentX][currentY] + Glight[currentX][currentY] + Rlight[currentX][currentY] - time;
            }


        }
    }
}