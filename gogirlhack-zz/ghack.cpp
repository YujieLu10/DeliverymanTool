// 暴力搜索
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
int GlobalT = 0; // 总共
int K = 0; 
int go = 0; // 已经经过的点 if go == K+1 表示已经遍历完了 返回出发点
int waitlight(int t, int r, int g){ // 需要等待红灯的时间
    if(t % (r+g) <= g){
        return 0;
    }else{
        return (t%(r+g) - g);
    }
}
int nextp(int s, int e){ //下一个坐标，由于不能斜着走，每次只有一个坐标会改变
    if(s < e){
        return s + 1;
    }else{
        return s - 1;
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
int calcTime(int si, int sj, int ei, int ej, int t){
    if(si == ei && sj == ej) return - tM[si][sj];
    int ni = 30;
    int nj = 30;

    if(sj != ej){
        int nextsj = nextp(sj,ej);
        nj += waitlight(t+30,RM[si][nextsj],GM[si][nextsj]) + tM[si][nextsj];
        nj += calcTime(si,nextsj,ei,ej,t+nj);
    }else{
        nj = 0xFFFFFFF;
    }

    if(si != ei){
        int nextsi = nextp(si, ei);
        ni += waitlight(t+30,RM[nextsi][sj], GM[nextsi][sj]) + tM[nextsi][sj];
        ni += calcTime(nextsi,sj,ei,ej,t+ni);
    }else{
        ni = 0xFFFFFFF;
    }
    return std::min(ni,nj);
}
void execute(int cp){ // 跑 Vvec里第cp个点
    St[cp] = true;
    vector<int> toOvec = {Vvec[cp][0], Vvec[cp][1]}; // 将当前点放入输出队列中
    Ovec.push_back(toOvec);
    go++;
    if(cp != 0){
        GlobalT += tM[Vvec[cp][0]][Vvec[cp][1]]; // 如果不是初始点，通过当前点（等待红灯在前面的CalcTime里已经算过了
    }
    if(go == K+1){
        vector<int> endToOvec = {sx, sy};
        Ovec.push_back(endToOvec);
        GlobalT += calcTime(Vvec[cp][0],Vvec[cp][1],sx, sy, GlobalT); //返回起始点
        return ;
    }
    int minT = 0xFFFFFFF;
    int minIndex = -1;
    for(int i = 1; i < K+1; i++){
        if(!St[i]){ // 如果还没有走过Vvec[i]这个点，则尝试走
            int time = calcTime(Vvec[cp][0], Vvec[cp][1], Vvec[i][0], Vvec[i][1], GlobalT);
            if(time < minT){ //每次去离当前点最近（花费时间最少）能到的client
                minT = time;
                minIndex = i;
            }

        }
    }
    GlobalT += minT;
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