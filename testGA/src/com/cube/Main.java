package com.cube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Start....");
        //TODO：读取120个文件列表
        String pathname = "/Users/c-ten/Desktop/demo/hackdata2.txt";
        int linecnt = 0;
        int N, M, sx, sy, Knum = 0;

        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                linecnt++;
                line = line.trim();
                String[] strline = line.split(" ");
                if(linecnt == 1)
                {
                    N = Integer.valueOf(strline[0]);
                    M = Integer.valueOf(strline[1]);
                }
                else if(linecnt == 2)
                {
                    sx = Integer.valueOf(strline[0]);
                    sy = Integer.valueOf(strline[1]);
                }
                else if(linecnt == 3)
                {
                    Knum = Integer.valueOf(strline[0]);
                }
                else
                {
                    break;
                }
                /*
                else if(linecnt <= 3 + Knum)
                {
                    // K个客户
                }
                else
                {
                    // 各路口时间
                }   */
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ga_init_point ga = new ga_init_point(0, 30, Knum, 10000, 0.7f, 0.9f);

        //ga_init_point ga = new ga_init_point(0,30, 48, 10000, 0.7f, 0.9f);
        ga.init("/Users/c-ten/Desktop/demo/hackdata2.txt", linecnt-1);
        ga.solve();
    }
}
