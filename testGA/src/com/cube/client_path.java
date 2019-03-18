package com.cube;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class client_path {

    private int point;
    private int scale;
    private int cityNum;
    private int MAX_GEN;
    private int[][] distance;
    private int bestT;
    public int bestLength;
    private int[] bestTour;

    private int[][] oldPopulation;
    private int[][] newPopulation;
    private int[] fitness;

    private float[] instancePercent;
    private float crossPercent;
    private float metaPercent;
    private int t;

    private Random random;

    public client_path() {

    }

    public client_path(int p, int s, int num, int maxg, float pc, float pm) {
        point = p;
        scale = s;
        cityNum = num;
        MAX_GEN = maxg;
        crossPercent = pc;
        metaPercent = pm;
    }

    public void init(String filename, int preLineCnt, int sx, int sy) throws IOException {
        int[] x;
        int[] y;

        String strbuff;
        BufferedReader data = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        distance = new int[cityNum][cityNum];
        x = new int[cityNum];
        y = new int[cityNum];
        x[0] = sx;
        y[0] = sy;
        for (int i = 0; i < cityNum + preLineCnt - 1; i++) {
            strbuff = data.readLine();
            if(i < preLineCnt)
            {
                continue;
            }
            //System.out.println(strbuff);
            strbuff = strbuff.trim();
            String[] strcol = strbuff.split(" ");
            x[i - preLineCnt + 1] = Integer.valueOf(strcol[0]);
            y[i - preLineCnt + 1] = Integer.valueOf(strcol[1]);
        }

        for (int i = 0; i < cityNum - 1; i++) {
            distance[i][i] = 0;
            for (int j = i + 1; j < cityNum; j++) {
                double rij = Math
                        .sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
                                * (y[i] - y[j])));
                int tij = (int) Math.round(rij);
                if (tij < rij) {
                    distance[i][j] = tij + 1;
                    distance[j][i] = distance[i][j];
                } else {
                    distance[i][j] = tij;
                    distance[j][i] = distance[i][j];
                }
            }
        }
        distance[cityNum - 1][cityNum - 1] = 0;

        bestLength = Integer.MAX_VALUE;
        bestTour = new int[cityNum + 1];
        bestT = 0;
        t = 0;

        newPopulation = new int[scale][cityNum-1];
        oldPopulation = new int[scale][cityNum-1];
        fitness = new int[scale];
        instancePercent = new float[scale];

        random = new Random(System.currentTimeMillis());
    }

    void initGroup() {
        int i, j, k;
        for (k = 0; k < scale; k++) {
            oldPopulation[k][0] = random.nextInt(65535) % cityNum;
            while(oldPopulation[k][0]==point) {
                oldPopulation[k][0] = random.nextInt(65535) % cityNum;
            }
            for (i = 1; i < cityNum-1;) {
                oldPopulation[k][i] = random.nextInt(65535) % cityNum;
                while(oldPopulation[k][i]==point) {
                    oldPopulation[k][i] = random.nextInt(65535) % cityNum;
                }
                for (j = 0; j < i; j++) {
                    if (oldPopulation[k][i] == oldPopulation[k][j]) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }

    public int evaluate(int[] chromosome) {
        int len = 0;
        for (int i = 1; i < cityNum-1; i++) {
            len += distance[chromosome[i - 1]][chromosome[i]];
        }
        len += distance[point][chromosome[0]];
        len += distance[chromosome[cityNum - 2]][point];
        return len;
    }

    void countRate() {
        int k;
        double sumFitness = 0;

        double[] tempf = new double[scale];

        for (k = 0; k < scale; k++) {
            tempf[k] = 10.0 / fitness[k];
            sumFitness += tempf[k];
        }

        instancePercent[0] = (float) (tempf[0] / sumFitness);
        for (k = 1; k < scale; k++) {
            instancePercent[k] = (float) (tempf[k] / sumFitness + instancePercent[k - 1]);
        }
    }

    public void selectBestGh() {
        int k, i, maxid;
        int maxevaluation;

        maxid = 0;
        maxevaluation = fitness[0];
        for (k = 1; k < scale; k++) {
            if (maxevaluation > fitness[k]) {
                maxevaluation = fitness[k];
                maxid = k;
            }
        }

        if (bestLength > maxevaluation) {
            bestLength = maxevaluation;
            bestT = t;
            for (i = 0; i < cityNum-1; i++) {
                bestTour[i] = oldPopulation[maxid][i];
            }
        }

        copyGh(0, maxid);
    }

    public void copyGh(int k, int kk) {
        int i;
        for (i = 0; i < cityNum-1; i++) {
            newPopulation[k][i] = oldPopulation[kk][i];
        }
    }

    public void select() {
        int k, i, selectId;
        float ran1;
        for (k = 1; k < scale; k++) {
            ran1 = (float) (random.nextInt(65535) % 1000 / 1000.0);
            for (i = 0; i < scale; i++) {
                if (ran1 <= instancePercent[i]) {
                    break;
                }
            }
            selectId = i;
            copyGh(k, selectId);
        }
    }

    public void evolution() {
        int k;
        selectBestGh();
        select();
        float r;

        for (k = 0; k < scale; k = k + 2) {
            r = random.nextFloat();
            if (r < crossPercent) {
                OXCross1(k, k + 1);
            } else {
                r = random.nextFloat();
                if (r < metaPercent) {
                    OnCVariation(k);
                }
                r = random.nextFloat();
                if (r < metaPercent) {
                    OnCVariation(k + 1);
                }
            }

        }
    }

    public void evolution1() {
        int k;
        selectBestGh();
        select();

        float r;

        for (k = 1; k + 1 < scale / 2; k = k + 2) {
            r = random.nextFloat();
            if (r < crossPercent) {
                OXCross1(k, k + 1);
            } else {
                r = random.nextFloat();
                if (r < metaPercent) {
                    OnCVariation(k);
                }
                r = random.nextFloat();
                if (r < metaPercent) {
                    OnCVariation(k + 1);
                }
            }
        }
        if (k == scale / 2 - 1) {
            r = random.nextFloat();
            if (r < metaPercent) {
                OnCVariation(k);
            }
        }

    }

    void OXCross(int k1, int k2) {
        int i, j, k, flag;
        int ran1, ran2, temp;
        int[] Gh1 = new int[cityNum-1];
        int[] Gh2 = new int[cityNum-1];

        ran1 = random.nextInt(65535) % (cityNum-1);
        ran2 = random.nextInt(65535) % (cityNum-1);

        while (ran1 == ran2) {
            ran2 = random.nextInt(65535) % (cityNum-1);
        }

        if (ran1 > ran2) {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }

        flag = ran2 - ran1 + 1;
        for (i = 0, j = ran1; i < flag; i++, j++) {
            Gh1[i] = newPopulation[k2][j];
            Gh2[i] = newPopulation[k1][j];
        }

        for (k = 0, j = flag; j < cityNum-1;) {
            Gh1[j] = newPopulation[k1][k++];
            for (i = 0; i < flag; i++) {
                if (Gh1[i] == Gh1[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        for (k = 0, j = flag; j < cityNum-1;) {
            Gh2[j] = newPopulation[k2][k++];
            for (i = 0; i < flag; i++) {
                if (Gh2[i] == Gh2[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        for (i = 0; i < cityNum-1; i++) {
            newPopulation[k1][i] = Gh1[i];
            newPopulation[k2][i] = Gh2[i];
        }
    }

    public void OXCross1(int k1, int k2) {
        int i, j, k, flag;
        int ran1, ran2, temp;
        int[] Gh1 = new int[cityNum-1];
        int[] Gh2 = new int[cityNum-1];

        ran1 = random.nextInt(65535) % (cityNum-1);
        ran2 = random.nextInt(65535) % (cityNum-1);
        while (ran1 == ran2) {
            ran2 = random.nextInt(65535) % (cityNum-1);
        }

        if (ran1 > ran2) {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }

        for (i = 0, j = ran2; j < cityNum-1; i++, j++) {
            Gh2[i] = newPopulation[k1][j];
        }

        flag = i;

        for (k = 0, j = flag; j < cityNum-1;) {
            Gh2[j] = newPopulation[k2][k++];
            for (i = 0; i < flag; i++) {
                if (Gh2[i] == Gh2[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        flag = ran1;
        for (k = 0, j = 0; k < cityNum-1;) {
            Gh1[j] = newPopulation[k1][k++];
            for (i = 0; i < flag; i++) {
                if (newPopulation[k2][i] == Gh1[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        flag = cityNum-1 - ran1;

        for (i = 0, j = flag; j < cityNum-1; j++, i++) {
            Gh1[j] = newPopulation[k2][i];
        }

        for (i = 0; i < cityNum-1; i++) {
            newPopulation[k1][i] = Gh1[i];
            newPopulation[k2][i] = Gh2[i];
        }
    }

    public void OnCVariation(int k) {
        int ran1, ran2, temp;
        int count;

        count = random.nextInt(65535) % (cityNum-1);

        for (int i = 0; i < count; i++) {

            ran1 = random.nextInt(65535) % (cityNum-1);
            ran2 = random.nextInt(65535) % (cityNum-1);
            while (ran1 == ran2) {
                ran2 = random.nextInt(65535) % (cityNum-1);
            }
            temp = newPopulation[k][ran1];
            newPopulation[k][ran1] = newPopulation[k][ran2];
            newPopulation[k][ran2] = temp;
        }
    }

    public int[] solve() {

        int i;
        int k;
        initGroup();
        for (k = 0; k < scale; k++) {
            fitness[k] = evaluate(oldPopulation[k]);
        }
        countRate();

        for (t = 0; t < MAX_GEN; t++) {
            evolution1();
            for (k = 0; k < scale; k++) {
                for (i = 0; i < cityNum-1; i++) {
                    oldPopulation[k][i] = newPopulation[k][i];
                }
            }
            for (k = 0; k < scale; k++) {
                fitness[k] = evaluate(oldPopulation[k]);
            }
            countRate();
        }
        return bestTour;
    }
}