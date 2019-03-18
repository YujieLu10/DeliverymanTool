package com.cube;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.min;

public class c_to_c_v2 {

    public static long t = 0;
    public static long[][] Rlight;
    public static long[][] Glight;
    public static long[][] passTime;
    public static int startX;
    public static int startY;
    public static List<Integer> xpath = new ArrayList<Integer>();
    public static List<Integer> ypath = new ArrayList<Integer>();
    public static int Border = 2;
    public static int K;
    public static int go;
    public static List<Integer> rxpath;
    public static List<Integer> rypath;
    public static void calcTime(int N, int M, int si, int sj, int ei, int ej){
        int lN = N;
        int lM = M;
        xpath.add(si);
        ypath.add(sj);
        go++;
        int flag = 1; // TODO: 0 (si,sj)是起始点，1 (si,sj)不是起始点
        if(si == startX && sj == startY) flag = 1;
        int x = abs(si - ei);
        int y = abs(ej - sj);
        int siChange = (si < ei) ? +1 : -1;
        int sjChange = (sj < ej) ? +1 : -1;
        long[][] mapTime = new long[x+1+2*Border][y + 1 + 2*Border];
        long[][] routeMap = new long[x+1+2*Border][y+1+2*Border];

        int curSi = si;

        int Ui = Border;
        int Uj = Border;
        routeMap[Border][Border] = 0; // start point
        mapTime[Border][Border] = t - ((flag == 1)?0:(passTime[si][sj])); // 如果子出发点是Global出发点，则删去需要等待的时间

        // Ui所在行
        {
            int Mappi = si;
            for(int kj = 1; kj <= Border; kj++){

                int Mappj = sj - kj * sjChange; // Map中的坐标
                if(Mappj < 1 || Mappj > lM) break;
                routeMap[Ui][Uj - kj] = 3; // 都基于右边点
                long nowTime = mapTime[Ui][Uj - kj + 1] + 30 + passTime[Mappi][Mappj + sjChange];
                mapTime[Ui][Uj - kj] = nowTime + waitlight(nowTime, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
            }
            for(int kj = 1; kj <= y + Border; kj ++){

                int Mappj = sj + kj * sjChange; // Map中的坐标
                if(Mappj < 1 || Mappj > lM) break;
                routeMap[Ui][Uj + kj] = 1; // 都基于左边点
                long nowTime = mapTime[Ui][Uj + kj - 1] + 30 + passTime[Mappi][Mappj - sjChange];
                if(!((go == K + 1) && (Mappi == startX) && (Mappj == startY))){ //有可能遇到终点，终点的红绿灯等待时间不计入
                    nowTime += waitlight(nowTime, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                }
                mapTime[Ui][Uj + kj] = nowTime;
            }
        }
        //Ui以上行
        for(int ki = 1; ki <= Border; ki++){
            int Mappi = si - ki * siChange;
            if(Mappi < 1 || Mappi > lN) break;
            //Uj 列
            routeMap[Ui - ki][Uj] = 4;
            long nowTime = mapTime[Ui - ki +1][Uj] + 30 + passTime[Mappi + siChange][sj];
            mapTime[Ui - ki][Uj] = nowTime + waitlight(nowTime, Rlight[Mappi][sj], Glight[Mappi][sj]);
            // 更新Uj左边列
            for(int kj = 1; kj <= Border; kj++){
                int Mappj = sj - kj * sjChange;
                if(Mappj < 1 || Mappj > lM) break;
                // 基于右边点
                long nTimeJ = mapTime[Ui - ki][Uj - kj + 1] + 30 + passTime[Mappi][Mappj + sjChange];
                // 基于下边点

                long nTimeI = mapTime[Ui - ki + 1][Uj - kj] + 30 + passTime[Mappi + siChange][Mappj];
                nTimeJ += waitlight(nTimeJ, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                nTimeI += waitlight(nTimeI, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                if(nTimeI < nTimeJ){
                    routeMap[Ui - ki][Uj - kj] = 4;
                    mapTime[Ui - ki][Uj - kj] = nTimeI;
                }else{
                    routeMap[Ui - ki][Uj - kj] = 3;
                    mapTime[Ui-ki][Uj-kj] = nTimeJ;
                }
            }
            // 更新sj右边列
            for(int kj = 1; kj <= y + Border; kj++){
                int Mappj = sj + kj * sjChange;
                if(Mappj < 1 || Mappj > lM) break;
                // 基于左边点
                long nTimeJ = mapTime[Ui - ki][Uj + kj - 1] + 30 + passTime[Mappi][Mappj - sjChange];
                // 基于下边点
                long nTimeI = mapTime[Ui - ki + 1][Uj + kj] + 30 + passTime[Mappi + siChange][Mappj];
                nTimeJ += waitlight(nTimeJ, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                nTimeI += waitlight(nTimeI, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                if(nTimeI < nTimeJ){
                    routeMap[Ui - ki][Uj + kj] = 4;
                    mapTime[Ui-ki][Uj + kj] = nTimeI;
                }else{
                    routeMap[Ui - ki][Uj + kj] = 1;
                    mapTime[Ui-ki][Uj + kj] = nTimeJ;
                }
            }
        }
        //Ui以下行
        for(int ki = 1; ki <= x + Border; ki++){
            int Mappi = si + ki * siChange;
            if(Mappi < 1 || Mappi > lN) break;
            //Uj 列
            routeMap[Ui + ki][Uj] = 2;
            long nowTime = mapTime[Ui + ki - 1][Uj] + 30 + passTime[Mappi - siChange][sj];
            // cout << Mappi << ", " << sj << ": " << nowTime;
            if(!((go == K + 1) && (Mappi == startX) && (sj == startY))){ //有可能遇到终点，终点的红绿灯等待时间不计入
                nowTime += waitlight(nowTime, Rlight[Mappi][sj], Glight[Mappi][sj]);
            }
            mapTime[Ui + ki][Uj] = nowTime;

            // 更新Uj左边列
            for(int kj = 1; kj <= Border; kj++){
                int Mappj = sj - kj * sjChange;
                if(Mappj < 1 || Mappj > lM) break;
                // 基于右边点
                long nTimeJ = mapTime[Ui + ki][Uj - kj + 1] + 30 + passTime[Mappi][Mappj + sjChange];
                // 基于上边点
                long nTimeI = mapTime[Ui + ki - 1][Uj + kj] + 30 + passTime[Mappi - siChange][Mappj];
                nTimeJ += waitlight(nTimeJ, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                nTimeI += waitlight(nTimeI, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                if(nTimeI < nTimeJ){
                    routeMap[Ui + ki][Uj - kj] = 2;
                    mapTime[Ui + ki][Uj - kj] = nTimeI;
                }else{
                    routeMap[Ui + ki][Uj - kj] = 3;
                    mapTime[Ui + ki][Uj - kj] = nTimeJ;
                }
            }

            // 更新sj右边列
            for(int kj = 1; kj <= y + Border; kj++){
                int Mappj = sj + kj * sjChange;
                // cout << Mappj << " ? " << lM << endl;
                if(Mappj < 1 || Mappj > lM) break;
                // 基于左边点
                long nTimeJ = mapTime[Ui + ki][Uj + kj - 1] + 30 + passTime[Mappi][Mappj - sjChange];
                // 基于上边点
                long nTimeI = mapTime[Ui + ki - 1][Uj + kj] + 30 + passTime[Mappi - siChange][Mappj];
                if(!((go == K + 1) && (Mappi == startX) && (Mappj == startY))){ //有可能遇到终点，终点的红绿灯等待时间不计入
                    nTimeJ += waitlight(nTimeJ, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                    nTimeI += waitlight(nTimeI, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                }
                if(nTimeI < nTimeJ){
                    routeMap[Ui + ki][Uj + kj] = 2;
                    mapTime[Ui + ki][Uj + kj] = nTimeI;
                }else{
                    routeMap[Ui + ki][Uj + kj] = 1;
                    mapTime[Ui + ki][Uj + kj] = nTimeJ;
                }
            }
        }
        // 更新多少次
        for(int ii = 1; ii < x + 2 * Border; ii ++){
            int Mappi = si + (ii - Ui) * siChange;
            if(Mappi < 2 || Mappi > lN - 1) continue;
            for(int jj = 1; jj < y + 2 * Border; jj ++){
                int Mappj = sj + (jj - Uj) * sjChange;
                if(Mappj < 2 || Mappj > lM - 1) continue;
                long timeL = mapTime[ii][jj - 1] + 30 + passTime[Mappi][Mappj - sjChange];
                long timeT = mapTime[ii - 1][jj] + 30 + passTime[Mappi - siChange][Mappj];
                long timeR = mapTime[ii][jj + 1] + 30 + passTime[Mappi][Mappj + sjChange];
                long timeD = mapTime[ii + 1][jj] + 30 + passTime[Mappi + siChange][Mappj];
                long minTime = minValue(timeL, timeT, timeR, timeD);
                long index = minIndex(timeL, timeT, timeR, timeD,minTime);
                if(!(go == K + 1) && (Mappi == startX) && (Mappj == startY)){ //有可能遇到终点，终点的红绿灯等待时间不计入
                    minTime += waitlight(minTime, Rlight[Mappi][Mappj], Glight[Mappi][Mappj]);
                }
                if(minTime < mapTime[ii][jj]) {
                    mapTime[ii][jj] = minTime;
                    routeMap[ii][jj] = index;
                }
            }
        }
        // curRi, curRj 基于Map的坐标
        int curRi = x + Border;
        int curRj = y + Border;
        rxpath = new ArrayList<Integer>();
        rypath = new ArrayList<Integer>();
        for(int i = 1; routeMap[curRi][curRj] != 0; i++){
            if(routeMap[curRi][curRj] == 1){ // 从左来
                curRj -= 1;
            }else if(routeMap[curRi][curRj] == 2){ // 从上来
                curRi -= 1;
            }else if(routeMap[curRi][curRj] == 3){ // 从右来
                curRj += 1;
            }else if(routeMap[curRi][curRj] == 4){ // 从下来
                curRi += 1;
            }

            if(routeMap[curRi][curRj] <= 0) break;
            rxpath.add(si + (curRi-Border) * siChange);
            rypath.add(sj + (curRj-Border) * sjChange);
        }
        for(int i = rxpath.size() - 1; i >= 0; i--){
            xpath.add(rxpath.get(i));
            ypath.add(rypath.get(i));
        }
        t = mapTime[x + Border][y+Border];
        return;
    }
    public static int minIndex(long a, long b, long c, long d, long min){
       if(a == min) return 1;
       else if(b == min) return 2;
       else if(c == min) return 3;
       else if(d == min) return 4;
       else return -1;
    }
    public static long minValue(long a, long b, long c, long d){

        return min(min(a,b),min(c,d));
    }
    public static long waitlight(long t, long r, long g){ // 需要等待红灯的时间
        if(r == 0) return 0;
        if(t % (r+g) < g){
            return t %(r+g) + r;
        }else{
            return 0;
        }
    }
}
