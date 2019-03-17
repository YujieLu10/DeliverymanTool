package com.cube;

import java.util.ArrayList;
import java.util.List;


public class client_to_client {
    public static int timeStamp = 0; // current time stamp
    public static int[][] Rlight; //red light duration
    public static int[][] Glight; //green light duration
    public static int[][] passTime;
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
        //int preX = x0, preY = y0;
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

            int time = timeStamp % (Glight[currentX][currentY] + Rlight[currentX][currentY]);

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
    public static void inputData_v2(int N, int M, int x0, int y0, int x1, int y1)
    {
        currentX = x0;
        currentY = y0;
        //int preX = x0, preY = y0;
        while(currentX != x1 || currentY != y1)
        {

            xpath.add(currentX);
            ypath.add(currentY);

            int timer, timel, timeu, timed;
            int rcost = 0, lcost = 0, ucost = 0, dcost = 0;
            if(currentX <= x1 && currentY <= y1)
            {
                if(currentX < M)
                {
                    currentX++;
                    timeStamp += 30;
                    timer = timeStamp % (Glight[currentX][currentY] + Rlight[currentX][currentY]);

                    if( timer + passTime[currentX][currentY] <= Glight[currentX][currentY] )//green enough
                    {
                        rcost = passTime[currentX][currentY];
                    }
                    else
                    {
                        rcost = passTime[currentX][currentY] + Glight[currentX][currentY] + Rlight[currentX][currentY] - timer;
                    }
                    timeStamp -=30;
                    currentX--;
                }
                if(currentY < N)
                {
                    currentY++;
                    timeStamp += 30;
                    timeu = timeStamp % (Glight[currentX][currentY] + Rlight[currentX][currentY]);

                    if( timeu + passTime[currentX][currentY] <= Glight[currentX][currentY] )//green enough
                    {
                        dcost = passTime[currentX][currentY];
                    }
                    else
                    {
                        dcost = passTime[currentX][currentY] + Glight[currentX][currentY] + Rlight[currentX][currentY] - timeu;
                    }
                    timeStamp -= 30;
                    currentY--;
                }
                if(rcost > dcost)
                    currentY++;
                else
                    currentX--;

            }
            else if(currentX <= x1 && currentY >= y1)
            {
                currentX ++;
            }
            else if(currentX >= x1 && currentY <= y1)
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

            int time = timeStamp % (Glight[currentX][currentY] + Rlight[currentX][currentY]);

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