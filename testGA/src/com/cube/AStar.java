package com.cube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//A*算法
public class AStar {
    public static int[][] map;//地图(1可通过 0不可通过)
    private List<Node> openList;//开启列表
    private List<Node> closeList;//关闭列表
    private final int COST_STRAIGHT = 10;//垂直方向或水平方向移动的路径评分
    private final int COST_DIAGONAL = 14;//斜方向移动的路径评分
    public static int row;//行
    public static int column;//列
    public static int[][] Rlight; //red light duration
    public static int[][] Glight; //green light duration
    public static int[][] passTime;

    public AStar(int row,int column){
        //this.map=map;
        //this.row=row;
        //this.column=column;
        openList=new ArrayList<Node>();
        closeList=new ArrayList<Node>();
    }

    //查找坐标（-1：错误，0：没找到，1：找到了）
    public int search(int x1,int y1,int x2,int y2){
        if(x1<0||x1>=row||x2<0||x2>=row||y1<0||y1>=column||y2<0||y2>=column){
            return -1;
        }
        if(map[x1][y1]==0||map[x2][y2]==0){
            return -1;
        }
        Node sNode=new Node(x1,y1,null);
        Node eNode=new Node(x2,y2,null);
        openList.add(sNode);
        List<Node> resultList=search(sNode, eNode);
        if(resultList.size()==0){
            return 0;
        }
        for(Node node:resultList){
            map[node.getX()][node.getY()]=-1;
        }
        return 1;
    }

    //查找核心算法
    private List<Node> search(Node sNode,Node eNode){
        List<Node> resultList=new ArrayList<Node>();
        boolean isFind=false;
        Node node=null;
        while(openList.size()>0){
            //取出开启列表中最低F值，即第一个存储的值的F为最低的
            node=openList.get(0);
            //判断是否找到目标点
            if(node.getX()==eNode.getX()&&node.getY()==eNode.getY()){
                isFind=true;
                break;
            }
            //上
            if((node.getY()-1)>=0){
                checkPath(node.getX(),node.getY()-1,node, eNode, COST_STRAIGHT);
            }
            //下
            if((node.getY()+1)<column){
                checkPath(node.getX(),node.getY()+1,node, eNode, COST_STRAIGHT);
            }
            //左
            if((node.getX()-1)>=0){
                checkPath(node.getX()-1,node.getY(),node, eNode, COST_STRAIGHT);
            }
            //右
            if((node.getX()+1)<row){
                checkPath(node.getX()+1,node.getY(),node, eNode, COST_STRAIGHT);
            }
            //左上
            if((node.getX()-1)>=0&&(node.getY()-1)>=0){
                checkPath(node.getX()-1,node.getY()-1,node, eNode, COST_DIAGONAL);
            }
            //左下
            if((node.getX()-1)>=0&&(node.getY()+1)<column){
                checkPath(node.getX()-1,node.getY()+1,node, eNode, COST_DIAGONAL);
            }
            //右上
            if((node.getX()+1)<row&&(node.getY()-1)>=0){
                checkPath(node.getX()+1,node.getY()-1,node, eNode, COST_DIAGONAL);
            }
            //右下
            if((node.getX()+1)<row&&(node.getY()+1)<column){
                checkPath(node.getX()+1,node.getY()+1,node, eNode, COST_DIAGONAL);
            }
            //从开启列表中删除
            //添加到关闭列表中
            closeList.add(openList.remove(0));
            //开启列表中排序，把F值最低的放到最底端
            Collections.sort(openList, new NodeFComparator());
        }
        if(isFind){
            getPath(resultList, node);
        }
        return resultList;
    }

    //查询此路是否能走通
    private boolean checkPath(int x,int y,Node parentNode,Node eNode,int cost){
        Node node=new Node(x, y, parentNode);
        //查找地图中是否能通过
        if(map[x][y]==0){
            closeList.add(node);
            return false;
        }
        //查找关闭列表中是否存在
        if(isListContains(closeList, x, y)!=-1){
            return false;
        }
        //查找开启列表中是否存在
        int index=-1;
        if((index=isListContains(openList, x, y))!=-1){
            //G值是否更小，即是否更新G，F值
            if((parentNode.getG()+cost)<openList.get(index).getG()){
                node.setParentNode(parentNode);
                countG(node, eNode, cost);
                countF(node);
                openList.set(index, node);
            }
        }else{
            //添加到开启列表中
            node.setParentNode(parentNode);
            count(node, eNode, cost);
            openList.add(node);
        }
        return true;
    }

    //集合中是否包含某个元素(-1：没有找到，否则返回所在的索引)
    private int isListContains(List<Node> list,int x,int y){
        for(int i=0;i<list.size();i++){
            Node node=list.get(i);
            if(node.getX()==x&&node.getY()==y){
                return i;
            }
        }
        return -1;
    }

    //从终点往返回到起点
    private void getPath(List<Node> resultList,Node node){
        if(node.getParentNode()!=null){
            getPath(resultList, node.getParentNode());
        }
        resultList.add(node);
    }

    //计算G,H,F值
    private void count(Node node,Node eNode,int cost){
        countG(node, eNode, cost);
        countH(node, eNode);
        countF(eNode);
    }
    //计算G值
    private void countG(Node node,Node eNode,int cost){
        if(node.getParentNode()==null){
            node.setG(cost);
        }else{
            node.setG(node.getParentNode().getG()+cost);
        }
    }
    //计算H值
    private void countH(Node node,Node eNode){
        node.setF(Math.abs(node.getX()-eNode.getX())+Math.abs(node.getY()-eNode.getY()));
    }
    //计算F值
    private void countF(Node node){
        node.setF(node.getG()+node.getF());
    }

}
//节点类
class Node {
    private int x;//X坐标
    private int y;//Y坐标
    private Node parentNode;//父类节点
    private int g;//当前点到起点的移动耗费
    private int h;//当前点到终点的移动耗费，即曼哈顿距离|x1-x2|+|y1-y2|(忽略障碍物)
    private int f;//f=g+h

    public Node(int x,int y,Node parentNode){
        this.x=x;
        this.y=y;
        this.parentNode=parentNode;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public Node getParentNode() {
        return parentNode;
    }
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
    public int getG() {
        return g;
    }
    public void setG(int g) {
        this.g = g;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public int getF() {
        return f;
    }
    public void setF(int f) {
        this.f = f;
    }
}
//节点比较类
class NodeFComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getF()-o2.getF();
    }

}