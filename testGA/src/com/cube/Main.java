package com.cube;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_ADDPeer;

import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        int N, M, sx = 1, sy = 1, Knum = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        List<String> list = new ArrayList<String>();
        String str = "";
        try {
            fileReader = new FileReader( pathname );
            bufferedReader = new BufferedReader( fileReader );
            while( (str = bufferedReader.readLine()) != null ) {
                list.add( str );
                //if( str.trim().length() > 2 ) {
                //    list.add( str );
                //}
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        //System.out.println(list.get(2));
        String[] strline = list.get(0).split(" ");
        N = Integer.valueOf(strline[0]);
        M = Integer.valueOf(strline[1]);
        strline = list.get(1).split(" ");
        sx = Integer.valueOf(strline[0]);
        sy = Integer.valueOf(strline[1]);
        strline = list.get(2).split(" ");
        Knum = Integer.valueOf(strline[0]);
        linecnt = 4;
        //client_to_client ctc = new client_to_client();
        client_to_client.Rlight = new int[N + 1][M + 1]; //red light duration
        client_to_client.Glight = new int[N + 1][M + 1]; //green light duration
        client_to_client.passTime = new int[N + 1][M + 1];

        int[] x;
        int[] y;
        x = new int[Knum + 2];
        y = new int[Knum + 2];
        x[1] = sx;
        y[1] = sy;
        for(int clientIdx = 4; clientIdx < Knum + 4; clientIdx++)
        {
            x[clientIdx-2] = Integer.valueOf(list.get(clientIdx-1).split(" ")[0]);
            y[clientIdx-2] = Integer.valueOf(list.get(clientIdx-1).split(" ")[1]);
        }

        for(int timeIdx = 3 + Knum; timeIdx < N * M + 3 + Knum; timeIdx++)
        {
            int i = Integer.valueOf(list.get(timeIdx).split(" ")[0]);
            int j = Integer.valueOf(list.get(timeIdx).split(" ")[1]);
            client_to_client.Glight[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[2]);
            client_to_client.Rlight[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[3]);
            client_to_client.passTime[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[4]);

        }

        /*
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

                else if(linecnt <= 3 + Knum)
                {
                    // K个客户
                }
                else
                {
                    // 各路口时间
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        ga_init_point ga = new ga_init_point(0, 30, Knum + 1, 10000, 0.7f, 0.9f);

        //ga_init_point ga = new ga_init_point(0,30, 48, 10000, 0.7f, 0.9f);
        ga.init("/Users/c-ten/Desktop/demo/hackdata2.txt", linecnt - 1, sx, sy);
        int[] tour =  ga.solve();

        System.out.println(x[1]);
        System.out.println(y[1]);
        client_to_client.inputData(N, M, x[1], y[1], x[tour[0] + 1], y[tour[0] + 1]);
        for(int i = 0; i < tour.length - 2; i++)
        {
            client_to_client.inputData(N, M,x[tour[i] + 1],y[tour[i] + 1],x[tour[i + 1] + 1],y[tour[i + 1] + 1]);
            System.out.println(x[tour[i] + 1]);

            System.out.println(y[tour[i] + 1]);
        }
        System.out.println(x[tour[tour.length - 2] + 1]);
        System.out.println(y[tour[tour.length - 2] + 1]);
        client_to_client.inputData(N, M, x[tour[tour.length - 2] + 1], y[tour[tour.length - 2] + 1], x[1], y[1]);

        //client_to_client.inputData(N, M, x[1], y[1], x[tour[0] + 1], y[tour[0] + 1]);
        //client_to_client.inputData(N, M, x[tour[0]], y[tour[0]], 3, 3);
        //client_to_client.inputData(N, M, 3, 3, 3, 1);
        //client_to_client.inputData(N, M, 3, 1, 1, 1);
        int i;
        /*for (i = 0; i < tour.length - 2; i++)
        {
            System.out.println(i);
            client_to_client.inputData(N, M,x[tour[i] + 1],y[tour[i] + 1],x[tour[i + 1] + 1],y[tour[i + 1] + 1]);
        }*/

        //client_to_client.inputData(N, M,x[tour[i]],y[tour[i]],x[tour[0]], y[tour[0]]);

        //System.out.println(x[1] + "->" + y[1]);
        //client_to_client ctc = new client_to_client(1,1,1,1,1);

    }
}
