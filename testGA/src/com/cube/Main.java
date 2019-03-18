package com.cube;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /**
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

        String inputPathDir = args[0];
        String outputPathDir = args[1];

        File file = new File(inputPathDir);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (!f.isDirectory()) {
                //System.out.println(f);
                if(String.valueOf(f).contains(".txt"))
                    oneFileResult(String.valueOf(f), outputPathDir);
            }
        }

    }
    public static void oneFileResult (String inputPath, String outputPath) throws IOException
    {
        System.out.println(">>>> Execute");
        int linecnt = 0;
        int N, M, sx = 1, sy = 1, Knum = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        List<String> list = new ArrayList<String>();
        String str = "";
        try {
            fileReader = new FileReader( inputPath );
            bufferedReader = new BufferedReader( fileReader );
            while( (str = bufferedReader.readLine()) != null ) {
                list.add( str );
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

        String[] strline = list.get(0).split(" ");
        N = Integer.valueOf(strline[0]);
        M = Integer.valueOf(strline[1]);
        strline = list.get(1).split(" ");
        sx = Integer.valueOf(strline[0]);
        sy = Integer.valueOf(strline[1]);
        strline = list.get(2).split(" ");
        Knum = Integer.valueOf(strline[0]);
        linecnt = 4;
        c_to_c_v2.Rlight = new long[N + 1][M + 1]; //red light duration
        c_to_c_v2.Glight = new long[N + 1][M + 1]; //green light duration
        c_to_c_v2.passTime = new long[N + 1][M + 1];
        c_to_c_v2.xpath = new ArrayList<Integer>();
        c_to_c_v2.ypath = new ArrayList<Integer>();
        c_to_c_v2.t = 0;
        c_to_c_v2.K = Knum;
        c_to_c_v2.go = 1;

        
        int[] x;
        int[] y;
        x = new int[Knum + 2];
        y = new int[Knum + 2];
        x[1] = sx;
        y[1] = sy;
        for(int clientIdx = 4; clientIdx < Knum + 4; clientIdx++) {
            x[clientIdx-2] = Integer.valueOf(list.get(clientIdx-1).split(" ")[0]);
            y[clientIdx-2] = Integer.valueOf(list.get(clientIdx-1).split(" ")[1]);
        }
        c_to_c_v2.startX = sx;
        c_to_c_v2.startY = sy;

        for(int timeIdx = 3 + Knum; timeIdx < N * M + 3 + Knum; timeIdx++) {
            int i = Integer.valueOf(list.get(timeIdx).split(" ")[0]);
            int j = Integer.valueOf(list.get(timeIdx).split(" ")[1]);
            c_to_c_v2.Glight[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[2]);
            c_to_c_v2.Rlight[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[3]);
            c_to_c_v2.passTime[i][j] = Integer.valueOf(list.get(timeIdx).split(" ")[4]);

        }


        client_path cp = new client_path(0, 30, Knum + 1, 10000, 0.7f, 0.9f);

        cp.init(inputPath, linecnt - 1, sx, sy);
        int[] tour =  cp.solve();

        c_to_c_v2.calcTime(N, M, x[1], y[1], x[tour[0] + 1], y[tour[0] + 1]);
        for(int i = 0; i < tour.length - 2; i++) {
            c_to_c_v2.calcTime(N, M,x[tour[i] + 1],y[tour[i] + 1],x[tour[i + 1] + 1],y[tour[i + 1] + 1]);
        }

        //最后到达配送路口无需额外时间
        c_to_c_v2.calcTime(N, M, x[tour[tour.length - 2] + 1], y[tour[tour.length - 2] + 1], x[1], y[1]);
        
        File output = new File(outputPath + "/output_" + inputPath.substring(inputPath.lastIndexOf('_')));
        FileOutputStream fos = new FileOutputStream(output);
        OutputStreamWriter dos = new OutputStreamWriter(fos);
        dos.write(String.valueOf(c_to_c_v2.t));
        dos.write("\n");
        for(int i = 0; i < c_to_c_v2.xpath.size(); i++) {
            dos.write(String.valueOf(c_to_c_v2.xpath.get(i)));
            dos.write(" ");
            dos.write(String.valueOf(c_to_c_v2.ypath.get(i)));
            dos.write("\n");
        }
        dos.write(String.valueOf(sx));
        dos.write(" ");
        dos.write(String.valueOf(sy));
        dos.close();

    }
}
