/*********************
HARE AND HOUND GAME
CLASS STATE
DESIGNED BY DONG LI
VERSION 1.0
2014
**********************/	
import java.util.*;
/******** STATE ********
Use to store the different states, including the positions of hounds and hare, the value of
the certain state(sometimes), the depth parameter just use occasionally(To decide which hound 
was moved.
*/	
public class State {

	private int h1;//position of hounds and hare
	private int h2;
	private int h3;
	private int r;
	private int value;//value of the state
	private int depth;//initially designed to store depth, but deprecated
	
	public State(){}
	public State(int i,int j,int k,int r){//constructor to initial positions
		
		this.h1 = i;
		this.h2 = j;
		this.h3 = k;
		this.r =r;
	}
	public int getValue(){
		
		return value;
	}
	public void setValue(int i){
		
		value = i;
	}
	public int getDepth(){
		
		return depth;
	}
	public void setDepth(int i){
		
		depth = i;
	}
	public void increaseDepth(){//never used
		
		depth++;
	}
	public void decreaseDepth(){//never used
		
		depth--;
	}
	public int getHound1(){
		return h1;
	}
	public int getHound2(){
		return h2;
	}
	public int getHound3(){
		return h3;
	}
	public int getHare(){
		return r;
	}
	public void printState(){
		System.out.println("h1:"+h1+" h2:"+h2+" h3:"+h3+" r:"+r+" v:"+value+" depth:"+depth);
	}
	public boolean sameState(State s){//return the same state with s
		int i = s.getHound1();
		int j = s.getHound2();
		int k = s.getHound3();
		int hare = s.getHare();
		if(hare==r){
			if((i==h1||i==h2||i==h3)&&(j==h1||j==h2||j==h3)&&(k==h1||k==h2||k==h3)){
				
				return true;
			}
		}
		return false;
	}
	public boolean sameHoundState(int i,int j,int k){//return the state with same positions of hounds

		if((i==h1||i==h2||i==h3)&&(j==h1||j==h2||j==h3)&&(k==h1||k==h2||k==h3)){
				
				return true;
		}
		return false;
	}

}
