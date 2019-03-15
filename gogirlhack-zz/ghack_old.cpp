#include <iostream>
#include <vector>

using namespace std;

int lN = 0, lM = 0;
int** GM;
int** RM;
int** tM;
int** CityMap;
int sx, sy;
vector<vector<int>> Vvec;
vector<vector<int>> Ovec;
vector<bool> St(61, false);
int GlobalT = 0;
int K;
int go = 0;
int waitlight(int t, int r, int g){
    if(t % (r+g) <= g){
        return (t%(r+g));
    }else{
        return (t%(r+g) - g);
    }
}
int nextp(int s, int e){
    if(s < e){
        return s + 1;
    }else{
        return s - 1;
    }
}
void init(){
    cin >> lN >> lM;
    cin >> sx >> sy;
    cin >> K;
    CityMap = new int*[K+1]();
    CityMap[0] = new int[K+1]();
    vector<int> svec = {sx, sy};
    Vvec.push_back(svec);
    // Ovec.push_back(svec);
    // cout << "start K client" << endl;
    for(int i = 1; i <= K; i++){
        int tx, ty;
        cin >> tx >> ty;
        vector<int> tv = {tx, ty};
        Vvec.push_back(tv);
        CityMap[i] = new int[K+1]();
    }
    // cout << "dist info" << endl;
    // dist info
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
    if(si == ei && sj == ej) return 0;
    int ni = 30;
    int nj = 30;

    if(sj != ej){
        int nextsj = nextp(sj,ej);
        // cout << "test " << si << "," << nextsj << endl;
        nj += waitlight(t+30,RM[si][nextsj],GM[si][nextsj]);
        nj += calcTime(si,nextsj,ei,ej,t+nj);
    }else{
        nj = 0xFFFFFFF;
    }

    if(si != sj){
        int nextsi = nextp(si, ei);
        // cout << "test " << nextsi << "," << sj << endl;
        ni += waitlight(t+30,RM[nextsi][sj], GM[nextsi][sj]);
        ni += calcTime(nextsi,sj,ei,ej,t+ni);
    }else{
        ni = 0xFFFFFFF;
    }
    return std::min(ni,nj);
}
int execute(int cp){
    // cout << "go " << cp << endl;
    St[cp] = true;
    vector<int> toOvec = {Vvec[cp][0], Vvec[cp][1]};
    Ovec.push_back(toOvec);
    go++;
    if(go == K+1){
        vector<int> endToOvec = {sx, sy};
        Ovec.push_back(endToOvec);
        return calcTime(Vvec[cp][0],Vvec[cp][1],sx, sy, GlobalT);
    }
    int minT = 0xFFFFFFF;
    int minIndex = -1;
    for(int i = 1; i < K+1; i++){
        if(!St[i]){
            // cout << "test " << i << endl;
            int time = calcTime(Vvec[cp][0], Vvec[cp][1], Vvec[i][0], Vvec[i][1], GlobalT);
            // cout << time << endl;
            // cout << time;
            if(time < minT){
                minT = time;
                minIndex = i;
            }
        }
    }
    GlobalT += minT;
    // cout << minIndex << endl;
    assert(minIndex >= 0 && minIndex <= K);
    return execute(minIndex);
}

int main(){
    init();
    int totalTime = execute(0);
    cout << totalTime << endl;
    for(int i = 0; i < Ovec.size(); i++){
        cout << Ovec[i][0] << " " << Ovec[i][1] << endl;
    }
    return 0;
}