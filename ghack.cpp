#include <iostream>
#include <vector>

using namespace std;

int lN = 0, lM = 0;
// matrix for green light, red light, go-through time
int** GM;
int** RM;
int** tM;
int** CityMap; // client间的距离表 还没用起来，可以在 execute 的循环里 做简化，即不用计算到那么多点的时间
int sx, sy; // 初始点
vector<vector<int>> Vvec; // 存 client的向量，Vvec[0] 是初始点
vector<vector<int>> Ovec; // Outputclient的向量
vector<bool> St(61, false);
long long GlobalT = 0; // 总共
int K = 0; 
int go = 0; // 已经经过的点 if go == K+1 表示已经遍历完了 返回出发点
const int Border = 2;
int waitlight(int t, int r, int g){ // 需要等待红灯的时间
    if(r == 0) return 0;
    if(t % (r+g) < g){
        return t %(r+g) + r;
    }else{
        return 0;
    }
}
void init(){ //输入存储等
    cin >> lN >> lM;
    cin >> sx >> sy;
    cin >> K;
    CityMap = new int*[K+1]();
    CityMap[0] = new int[K+1]();
    vector<int> svec = {sx, sy};
    Vvec.push_back(svec);
    for(int i = 1; i <= K; i++){
        int tx, ty;
        cin >> tx >> ty;
        vector<int> tv = {tx, ty};
        Vvec.push_back(tv);
        CityMap[i] = new int[K+1]();
    }
    for(int i = 0; i <= K; i++){
        for(int j = i+1; j <= K; j++){
            CityMap[i][j] = CityMap[j][i] = abs(Vvec[i][0]-Vvec[j][0]) + abs(Vvec[i][1] - Vvec[j][1]);
        }
    }
    GM = new int*[lN+1]();
    RM = new int*[lN+1]();
    tM = new int*[lN+1]();
    // cout << "light info" << endl;
    for(int i = 0; i <= lN; i++){
        GM[i] = new int[lM+1];
        RM[i] = new int[lM+1];
        tM[i] = new int[lM+1];
    }
    // light info
    for(int i = 0; i < lN*lM; i++){
        int ii, jj, g,r,t;
        cin >> ii >> jj >> g >> r >> t;
        GM[ii][jj] = g;
        RM[ii][jj] = r;
        tM[ii][jj] = t;
    }
}
int minIndex(int a, int b, int c, int d, int min){
       if(a == min) return 1;
       else if(b == min) return 2;
       else if(c == min) return 3;
       else if(d == min) return 4;
       else{
           assert(0 == 1);
       }
}
int minValue(int a, int b, int c, int d){
    return min(min(a,b),min(c,d));
}

void draw(int ** p, int x, int y){
    for(int i = 0; i < x; i++){
        for(int j = 0; j < y; j++){
            cout << p[i][j] << " ";
        }
        cout << endl;
    }
    cout << endl;
}

vector<vector<int>> calcTime(int si, int sj, int ei, int ej, int t, int flag = 1){
    // cout << "m " << si << ", " << sj << ", " << ei << ", " << ej << ", " << t << endl;
    int x = abs(si - ei);
    int y = abs(ej - sj);
    int siChange = (si < ei) ? +1 : -1;
    int sjChange = (sj < ej) ? +1 : -1;
    int ** mapTime = new int*[x + 1 + 2 * Border];
    int ** routeMap = new int*[x + 1 + 2 * Border];
    int curSi = si;
    vector<vector<int>> ret;
    
    for(int i = 0; i <= x + 2 * Border; i++){
        mapTime[i] = new int[y + 1 + 2 * Border]();
        routeMap[i] = new int[y + 1 + 2 * Border]();
        memset(mapTime[i],0,sizeof(int)*(y + 1 + 2 * Border));
        memset(routeMap[i],-1,sizeof(int) * (y + 1 + 2 * Border));
    }
    int Ui = Border;
    int Uj = Border;
    routeMap[Border][Border] = 0; // start point
    mapTime[Border][Border] = t - ((flag == 1)?0:(tM[si][sj])); // 如果子出发点是Global出发点，则删去需要等待的时间
    
    // Ui所在行
    {
        int Mappi = si;
        for(int kj = 1; kj <= Border; kj++){
            
            int Mappj = sj - kj * sjChange; // Map中的坐标
            if(Mappj < 1 || Mappj > lM) break;
            routeMap[Ui][Uj - kj] = 3; // 都基于右边点
            int nowTime = mapTime[Ui][Uj - kj + 1] + 30 + tM[Mappi][Mappj + sjChange];
            mapTime[Ui][Uj - kj] = nowTime + waitlight(nowTime, RM[Mappi][Mappj], GM[Mappi][Mappj]);
        }
        for(int kj = 1; kj <= y + Border; kj ++){
            
            int Mappj = sj + kj * sjChange; // Map中的坐标
            if(Mappj < 1 || Mappj > lM) break; 
            routeMap[Ui][Uj + kj] = 1; // 都基于左边点
            int nowTime = mapTime[Ui][Uj + kj - 1] + 30 + tM[Mappi][Mappj - sjChange];
            // cout << "Ui " << Mappi << "," << Mappj << ": " << nowTime << go;
            if(!((go == K + 1) && (Mappi == sx) && (Mappj == sy))){ //有可能遇到终点，终点的红绿灯等待时间不计入
                nowTime += waitlight(nowTime, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            }
            mapTime[Ui][Uj + kj] = nowTime;
        }
    }
    // draw(mapTime,x+1+2*Border,y+1+2*Border);
    // draw(routeMap,x+1+2*Border,y+1+2*Border); 

    //Ui以上行
    for(int ki = 1; ki <= Border; ki++){
        int Mappi = si - ki * siChange;
        if(Mappi < 1 || Mappi > lN) break;
        //Uj 列
        routeMap[Ui - ki][Uj] = 4;
        int nowTime = mapTime[Ui - ki +1][Uj] + 30 + tM[Mappi + siChange][sj];
        mapTime[Ui - ki][Uj] = nowTime + waitlight(nowTime, RM[Mappi][sj], GM[Mappi][sj]);
        // 更新Uj左边列
        for(int kj = 1; kj <= Border; kj++){
            int Mappj = sj - kj * sjChange;
            if(Mappj < 1 || Mappj > lM) break;
            // 基于右边点
            int nTimeJ = mapTime[Ui - ki][Uj - kj + 1] + 30 + tM[Mappi][Mappj + sjChange];
            // 基于下边点

            int nTimeI = mapTime[Ui - ki + 1][Uj - kj] + 30 + tM[Mappi + siChange][Mappj];
            nTimeJ += waitlight(nTimeJ, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            nTimeI += waitlight(nTimeI, RM[Mappi][Mappj], GM[Mappi][Mappj]);
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
            int nTimeJ = mapTime[Ui - ki][Uj + kj - 1] + 30 + tM[Mappi][Mappj - sjChange];
            // 基于下边点
            int nTimeI = mapTime[Ui - ki + 1][Uj + kj] + 30 + tM[Mappi + siChange][Mappj];
            nTimeJ += waitlight(nTimeJ, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            nTimeI += waitlight(nTimeI, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            if(nTimeI < nTimeJ){
                routeMap[Ui - ki][Uj + kj] = 4;
                mapTime[Ui-ki][Uj + kj] = nTimeI;
            }else{
                routeMap[Ui - ki][Uj + kj] = 1;
                mapTime[Ui-ki][Uj + kj] = nTimeJ; 
            } 
        }
    }
    // draw(mapTime,x+1+2*Border,y+1+2*Border);
    // draw(routeMap,x+1+2*Border,y+1+2*Border); 
    // assert(1 == 0);
    // cout << "mid update " << endl;
    //Ui以下行
    for(int ki = 1; ki <= x + Border; ki++){
        int Mappi = si + ki * siChange;
        if(Mappi < 1 || Mappi > lN) break;
        //Uj 列
        routeMap[Ui + ki][Uj] = 2;
        int nowTime = mapTime[Ui + ki - 1][Uj] + 30 + tM[Mappi - siChange][sj];
        // cout << Mappi << ", " << sj << ": " << nowTime;
        if(!((go == K + 1) && (Mappi == sx) && (sj == sy))){ //有可能遇到终点，终点的红绿灯等待时间不计入
            nowTime += waitlight(nowTime, RM[Mappi][sj], GM[Mappi][sj]);
        }
        mapTime[Ui + ki][Uj] = nowTime;

        // 更新Uj左边列
        for(int kj = 1; kj <= Border; kj++){
            int Mappj = sj - kj * sjChange;
            if(Mappj < 1 || Mappj > lM) break;
            // 基于右边点
            int nTimeJ = mapTime[Ui + ki][Uj - kj + 1] + 30 + tM[Mappi][Mappj + sjChange];
            // 基于上边点
            int nTimeI = mapTime[Ui + ki - 1][Uj + kj] + 30 + tM[Mappi - siChange][Mappj];
            nTimeJ += waitlight(nTimeJ, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            nTimeI += waitlight(nTimeI, RM[Mappi][Mappj], GM[Mappi][Mappj]);
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
            int nTimeJ = mapTime[Ui + ki][Uj + kj - 1] + 30 + tM[Mappi][Mappj - sjChange];
            // 基于上边点
            int nTimeI = mapTime[Ui + ki - 1][Uj + kj] + 30 + tM[Mappi - siChange][Mappj];
            if(!((go == K + 1) && (Mappi == sx) && (Mappj == sy))){ //有可能遇到终点，终点的红绿灯等待时间不计入
                nTimeJ += waitlight(nTimeJ, RM[Mappi][Mappj], GM[Mappi][Mappj]);
                nTimeI += waitlight(nTimeI, RM[Mappi][Mappj], GM[Mappi][Mappj]);
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
    // draw(mapTime,x+1+2*Border,y+1+2*Border);
    // draw(routeMap,x+1+2*Border,y+1+2*Border); 
    // 更新多少次
    for(int ii = 1; ii < x + 2 * Border; ii ++){
        int Mappi = si + (ii - Ui) * siChange;
        if(Mappi < 2 || Mappi > lN - 1) continue;
        for(int jj = 1; jj < y + 2 * Border; jj ++){
            int Mappj = sj + (jj - Uj) * sjChange;
            if(Mappj < 2 || Mappj > lM - 1) continue;
            int timeL = mapTime[ii][jj - 1] + 30 + tM[Mappi][Mappj - sjChange];
            int timeT = mapTime[ii - 1][jj] + 30 + tM[Mappi - siChange][Mappj];
            int timeR = mapTime[ii][jj + 1] + 30 + tM[Mappi][Mappj + sjChange];
            int timeD = mapTime[ii + 1][jj] + 30 + tM[Mappi + siChange][Mappj];
            int minTime = minValue(timeL, timeT, timeR, timeD);
            int index = minIndex(timeL, timeT, timeR, timeD,minTime);
            if(!(go == K + 1) && (Mappi == sx) && (Mappj == sy)){ //有可能遇到终点，终点的红绿灯等待时间不计入
                minTime += waitlight(minTime, RM[Mappi][Mappj], GM[Mappi][Mappj]);
            } 
            if(minTime < mapTime[ii][jj]) {
                mapTime[ii][jj] = minTime;
                routeMap[ii][jj] = index;
            }
        }
    }
    // draw(mapTime,x+1+2*Border,y+1+2*Border);
    // draw(routeMap,x+1+2*Border,y+1+2*Border); 
    // curRi, curRj 基于Map的坐标
    int curRi = x + Border;
    int curRj = y + Border;
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
        else{
            cout << "error: " << curRi <<","<< curRj << ": "<< routeMap[curRi][curRj] << endl;
            assert(1 == 0); // not ok now;
        }
        if(routeMap[curRi][curRj] <= 0) break;
        vector<int> thisNode = {si + (curRi-Border) * siChange, sj + (curRj-Border) * sjChange};
        ret.push_back(thisNode);
    }
    vector<int> EndTime = {mapTime[x + Border][y + Border]};
    ret.push_back(EndTime);
    reverse(ret.begin(),ret.end());
    for(int i = 0; i <= x + 2 * Border; i++){
        delete mapTime[i];
        delete routeMap[i];
    }
    delete[] mapTime;
    delete[] routeMap;
    // assert(1 == 0);
    return ret;
}


void execute(int cp){ // 跑 Vvec里第cp个点
    
    St[cp] = true;
    vector<int> toOvec = {Vvec[cp][0], Vvec[cp][1]}; // 将当前点放入输出队列中
    Ovec.push_back(toOvec);
    go++;
    if(go == K+1){
        vector<vector<int>> LocList = calcTime(Vvec[cp][0],Vvec[cp][1],sx, sy, GlobalT);
        GlobalT = LocList[0][0];
        Ovec.insert(Ovec.end(),LocList.begin()+1,LocList.end());
        vector<int> endVec = {sx, sy};
        Ovec.push_back(endVec);
        return ;
    }
    int minT = 0xFFFFFFF;
    int minIndex = -1;
    vector<vector<int>> minLocVec;
    for(int i = 1; i < K+1; i++){
        if(!St[i]){ // 如果还没有走过Vvec[i]这个点，则尝试走
            vector<vector<int>> LocList = calcTime(Vvec[cp][0], Vvec[cp][1], Vvec[i][0], Vvec[i][1], GlobalT, (go == 1) ? 0 : 1); 
            int time = LocList[0][0]; 
            if(time < minT){ //每次去离当前点最近（花费时间最少）能到的client
                minT = time;
                minIndex = i;
                minLocVec.assign(LocList.begin()+1,LocList.end());
            }
        }
    }
    GlobalT = minT;
    // cout << Vvec[cp][0]<< "," << Vvec[cp][1] << "->" << Vvec[minIndex][0]<< "," << Vvec[minIndex][1] << " "<< GlobalT << endl;
    // assert (1 == 0);
    Ovec.insert(Ovec.end(),minLocVec.begin(),minLocVec.end());
    assert(minIndex >= 0 && minIndex <= K);
    execute(minIndex);
    return ;
}

int main(){
    init();
    execute(0);
    cout << GlobalT << endl;
    for(int i = 0; i < Ovec.size(); i++){
        cout << Ovec[i][0] << " " << Ovec[i][1] << endl;
    }
    return 0;
}