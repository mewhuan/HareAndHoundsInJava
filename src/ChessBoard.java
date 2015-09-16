/*********************
HARE AND HOUND GAME
CLASS CHESSBOARD
DESIGNED BY DONG LI
VERSION 1.0
2014
**********************/	
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/******** CHESSBOARD ********
This class initial the user interface, listen to the mouse event, and make AI compute.
There are three levels of AI
But only the "Hard" level use alpha-beta pruning to decide strategy, and output the 
certain statistics.
The chessboard shows as button's ID:
       0  1  2
    3  4  5  6  7
       8  9  10
*/	
public class ChessBoard implements MouseListener{
	
	String player = "hound";//to decide player's role
	String level = "Easy";//to decide level
	Boolean side = true;//like side of coins, used to swap side(change player's role)
	static BoolButton firstButton = null;//to store the first one button that player clicked, used in makeMove()
	static ArrayList<BoolButton> buttons = new ArrayList<BoolButton>();//store 11 buttons
	JLabel space1 = new JLabel("");//four corners of the chessboard
	JLabel space2 = new JLabel("");
	JLabel space3 = new JLabel("");
	JLabel space4 = new JLabel("");
	JLabel playerInfo = new JLabel("You");//to show play's role
	JLabel computerInfo = new JLabel("AI");//to show AI's role
	JComboBox levelBox = new JComboBox();//choose level
	JFrame frame = new JFrame("HARE and HOUNDS");
	JPanel bigPane = new JPanel();
	ImagePane pane = new ImagePane();//the area of main chessboard
	JPanel pane2 = new JPanel();//the area of functional buttons
	int[] repeatedColumn = null;//abandoned,use to solve ten times repeat
	int repeatTime = 0;
	int maxDepth = 1;//four required statistics
	int numOfNodes = 1;
	int numOfMaxCut = 0;
	int numOfMinCut = 0;
	ArrayList<State> states = new ArrayList<State>();//abandoned
	static int[][] neighbor = {{1,4,5},{2,5},{6,7},{0,4,8},//store hounds' valid moves, neighbor[i][*] is the valid next moves of a hound in position i
			{0,5,8},{1,2,6,9,10},{2,7,10},{},//1!
			{4,5,9},{5,10},{6,7}};
	static int[][] neighbor2 = {{3,4,1,5},{0,5,2},{1,5,6,7},//store hare's valid moves
			{},{3,0,8,5},{0,4,8,1,9,2,6,10},{5,2,10,7},
			{2,6,10},{3,4,5,9},{8,5,10},{5,9,6,7}};
	int houndToMove;//in AI, get the moved hound
	JButton swapSide = new BoolButton("Swap Side");//swap side button
	JButton restart = new JButton("Restart");//restart button
	
	public ChessBoard(){
		
		for(int i=0;i<11;i++){//store 11 buttons into array list
			
			BoolButton b = new BoolButton(i);
			buttons.add(b);
			b.addMouseListener(this);
		}
		swapSide.setPreferredSize(new Dimension(100,50));//button's size
		restart.setPreferredSize(new Dimension(100,50));
		playerInfo.setIcon(new ImageIcon("src/resource/4.png"));//show roles
		computerInfo.setIcon(new ImageIcon("src/resource/3.png"));
		levelBox.addItem("Easy");//levels
		levelBox.addItem("Medium");
		levelBox.addItem("Hard");
		levelBox.addActionListener(new ActionListener(){//choose level
			public void actionPerformed(ActionEvent e){
				
				String s = (String)levelBox.getSelectedItem();
				level = s;
			}
		});
		pane2.setLayout(new FlowLayout());
		pane2.add(playerInfo);//add label and buttons into the area
		pane2.add(levelBox);
		pane2.add(swapSide);
		pane2.add(restart);
		pane2.add(computerInfo);
		restart.addActionListener(new ActionListener(){//restart button event
			public void actionPerformed(ActionEvent e){
				int choice = JOptionPane.showConfirmDialog(null,"Want to restart?","Notice",JOptionPane.YES_NO_OPTION);
				if(choice==JOptionPane.YES_OPTION){
					restart();
				}
			}
		});
		swapSide.addActionListener(new ActionListener(){//swap side button event
			public void actionPerformed(ActionEvent e){
				int choice = JOptionPane.showConfirmDialog(null,"Want to swap side?","Notice",JOptionPane.YES_NO_OPTION);
				if(choice==JOptionPane.YES_OPTION){
					side = !side;
					if(side){
						player = "hound";
						playerInfo.setIcon(new ImageIcon("src/resource/4.png"));
						computerInfo.setIcon(new ImageIcon("src/resource/3.png"));
					}
					else{
						player = "hare";
						computerInfo.setIcon(new ImageIcon("src/resource/4.png"));
						playerInfo.setIcon(new ImageIcon("src/resource/3.png"));
					}
					restart();
				}
			}
		});
		pane.setBackground("src/resource/2.png");
		pane.setPreferredSize(new Dimension(500,280));
		pane.setBorder(BorderFactory.createLineBorder(Color.blue));//set borders
		pane2.setPreferredSize(new Dimension(500,70));
		pane2.setBorder(BorderFactory.createLineBorder(Color.red));
		frame.setLocation(200,200);
		frame.setVisible(true);
		frame.setSize(600,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.setContentPane(bigPane);
		bigPane.setLayout(new BorderLayout());
		bigPane.add(pane,"North");
		bigPane.add(pane2,"South");
		buttons.get(7).setRole("hare");//assign roles to buttons, the default player is hound
		buttons.get(0).setRole("hound");
		buttons.get(3).setRole("hound");
		buttons.get(8).setRole("hound");
		buttons.get(1).setRole("nothing");
		buttons.get(2).setRole("nothing");
		buttons.get(4).setRole("nothing");
		buttons.get(5).setRole("nothing");
		buttons.get(6).setRole("nothing");
		buttons.get(9).setRole("nothing");
		buttons.get(10).setRole("nothing");
		if(player.equals("hare")){
			
			if(level.equals("Hard")){//if level is hard, call alpha-beta pruning to make a move
				int nextMove = hard(currentState());
				buttons.get(houndToMove).setRole("nothing");
				buttons.get(nextMove).setRole("hound");
				disableHounds();
			}
			else{//level is not hard, just move like this
				buttons.get(0).setRole("nothing");
				buttons.get(5).setRole("hound");
			}
		}
		pane.setLayout(new GridLayout(3,5,50,50));
		pane.add(space1);//grid layout and add items in this order, to form the chessboard
		pane.add(buttons.get(0));
		pane.add(buttons.get(1));
		pane.add(buttons.get(2));
		pane.add(space2);
		pane.add(buttons.get(3));
		pane.add(buttons.get(4));
		pane.add(buttons.get(5));
		pane.add(buttons.get(6));
		pane.add(buttons.get(7));
		pane.add(space3);
		pane.add(buttons.get(8));
		pane.add(buttons.get(9));
		pane.add(buttons.get(10));
		pane.add(space4);
		frame.pack();
	}

	
	public void makeMove(){//select to different buttons, and then to decide different conditions
		if(player.equals("hound")){//player is hound, player cannot move hare
			disableHare();
		}
		else if(player.equals("hare")){//player is hare, player cannot move hound
			disableHounds();
		}
		BoolButton b1 = null;
		BoolButton b2 = null;
		for(int i=0;i<buttons.size();i++){

			if(firstButton!=null&&!firstButton.getSelected()){//clear the first button

				firstButton = null;
			}
			BoolButton b = buttons.get(i);
			if(b.getSelected()){//change the selected hound's background(red circle)
				if(b.getRole().equals("hound")){
		
					unselectHounds();
					b.setIcon(new ImageIcon("src/resource/5.png"));
				}
				b1 = b1==null?b:b1;
				b2 = b1==b?null:b;//Both b1 and b2 are null, just assign the value to b1; Else b1 is not null, assign the value to b2
				if(firstButton==null){
					firstButton = b1;//store the first selected button
				}
			}
			if(b2!=null)
				break;
		}
		if(b1!=null&&b2!=null){//two different buttons have been selected
			
			BoolButton firstB = firstButton;
			BoolButton secondB;
			if(b1==firstB){
				secondB = b2;
			}
			else 
				secondB = b1;//get the first and second selected buttons, store in firstB and secondB
			String firstRole = firstB.getRole();
			String secondRole = secondB.getRole();//get their role: hound?hare?nothing?
			if(player.equals("hound")){//when player is hound
				
				if(firstRole.equals("hound")&&secondRole.equals("nothing")){//player choose a hound and then choose a blank
					
					int firstID = firstB.getID();
					for(int i=0;i<neighbor[firstID].length;i++){//traversal possible next positions
						
						BoolButton b = buttons.get(neighbor[firstID][i]);
						if(secondB.getID()==b.getID()){//when the second clicked button is a valid position
							
							firstB.setRole("nothing");
							secondB.setRole("hound");//change role to move
							firstButton = null;//clear the first button variable
							repeatTen(firstB,secondB);//if hound move vertically 10 times in a row, it fail
							State s = currentState();//return current state: positions of hounds and hare
							if(level.equals("Hard")){//if level is hard
								int nextMove = hard(s);//get next move from hard method
								if(nextMove==11){//hard will not return 11 unless something unexpected happens(never)
									medium(s.getHound1(),s.getHound2(),s.getHound3());//if happens, call another method to rescue
								}
								else{
									buttons.get(findHare()).setRole("nothing");
									buttons.get(nextMove).setRole("hare");//make a move
									disableHare();//player is hound, cannot control hare
									checkHoundWin();//check whether hound win
									checkHareWin();//check whether hare win
								}	
							}
							else if(level.equals("Medium")){//if level is medium
								medium(s.getHound1(),s.getHound2(),s.getHound3());//call medium method
								disableHare();//player is hound, cannot control hare
								checkHareWin();
								checkHoundWin();
							}
							else if(level.equals("Easy")){//if level is easy
								easy();//call easy method
								disableHare();
								checkHareWin();
								checkHoundWin();
							}
							break;
						}
						else{
							firstB.setIcon(new ImageIcon("src/resource/4.png"));//change its background as unselected
						}
					}
					firstButton = null;//clear first button variable
					resetAll();//set all buttons as unselected
				}
				else if (secondRole.equals("hound")){//player secondly click a hound
					
					unselectHounds();
					resetAll();
					firstButton = secondB;//the second one as first button
					secondB.setSelected(true);//set as selected
					secondB.setIcon(new ImageIcon("src/resource/5.png"));
				}
				else{
					firstButton.setSelected(false);
					firstButton = null;
					resetAll();
					unselectHounds();//reset all the state of buttons
				}
			}
			else if(player.equals("hare")){//when player is hare
				
				if(firstRole.equals("hare")&&secondRole.equals("nothing")){//as same as the operations above
					
					int firstID = firstB.getID();
					for(int i=0;i<neighbor2[firstID].length;i++){
						
						BoolButton b = buttons.get(neighbor2[firstID][i]);
						if(secondB.getID()==b.getID()){
							
							firstB.setRole("nothing");
							secondB.setRole("hare");
							firstButton = null;
							State s = currentState();
							if(level.equals("Hard")){
								int nextMove = hard(s);
								if(nextMove==11){
									medium(s.getHound1(),s.getHound2(),s.getHound3());
								}
								else{
									buttons.get(houndToMove).setRole("nothing");
									buttons.get(nextMove).setRole("hound");
									disableHounds();
									checkHoundWin();
									checkHareWin();
									repeatTen(buttons.get(houndToMove),buttons.get(nextMove));
								}
							}
							else if(level.equals("Medium")){
								medium(s.getHound1(),s.getHound2(),s.getHound3());
								disableHounds();
								checkHareWin();
								checkHoundWin();
							}
							else if(level.equals("Easy")){
								easy();
								disableHounds();
								checkHareWin();
								checkHoundWin();
							}
							break;
						}
						else{
							firstB.setIcon(new ImageIcon("src/resource/2.png"));
						}
					}
					firstButton = null;
					resetAll();
				}
				else if(!(firstRole.equals("hare"))&&secondRole.equals("hare")){
					
					unselectHare();
					resetAll();
					firstButton = secondB;
					secondB.setSelected(true);
					secondB.setIcon(new ImageIcon("src/resource/1.png")); 
				}
				else{
					firstButton.setSelected(false);
					firstButton = null;
					resetAll();
					unselectHare();
				}
			}
		}
	}
	public int hard(State s){//hard level method, the alpha-beta pruning algorithm
	//in my algorithm, State s is the root, but I compute the values of all the root's 
	//children to find the maximum one. In this way, I can get the next state with the
	//matching value
		maxDepth = 1;
		numOfNodes = 1;
		numOfMaxCut = 0;
		numOfMinCut = 0;//initial the required statistics
		int depth = 10;//set recursion depth
		int v = -100;
		int r = s.getHare();
		int h1 = s.getHound1();
		int h2 = s.getHound2();
		int h3 = s.getHound3();//get positions of hounds and hare
		ArrayList<State> nexts = new ArrayList<State>();//use to store all the possible next moves
		ArrayList<State> results = new ArrayList<State>();//use to store all the best next moves
		if(player.equals("hound")){//when player is hound

			for(int i=0;i<neighbor2[r].length;i++){
				if(neighbor2[r][i]!=h1&&neighbor2[r][i]!=h2&&neighbor2[r][i]!=h3){
					State state = new State(h1,h2,h3,neighbor2[r][i]);
					nexts.add(state);//store all possible moves
				}
			}
			if(nexts!=null&&nexts.size()!=0){//use minValue to compute, store the value, update the maximum value
				numOfNodes += nexts.size();
				for(int i=0;i<nexts.size();i++){
					State sts = nexts.get(i);
					int mins = minValue(sts,-100,100,depth);
					if(v<mins){
						v = mins;
					}
					sts.setValue(mins);
				}
			}
			if(nexts!=null&&nexts.size()!=0){//find the minimum value and store the certain state
				for(int i=0;i<nexts.size();i++){
					State sts = nexts.get(i);
					if(v==sts.getValue()){
						results.add(sts);
					}
				}		
			}
			System.out.println("Level of the generated tree: "+maxDepth);//output statistics
			System.out.println("Number of nodes generated: "+numOfNodes);
			System.out.println("Number of MAX pruning occurred: "+numOfMaxCut);
			System.out.println("Number of MIN pruning occurred: "+numOfMinCut);
			if(results.size()!=0&&results!=null){
				if(results.size()==2){//special case, since other move with the same value also can win but not win it immediately
					if((results.get(0).getHare()==5||results.get(0).getHare()==7)&&
							(results.get(1).getHare()==5||results.get(1).getHare()==7)){
						
						return 5;
					}
				}
				int index = (int)(Math.random()*results.size());
				return (Integer)results.get(index).getHare();//randomly return a state from the best states
			}
		}
		else if(player.equals("hare")){//when player is hare, as same as above

			for(int i=0;i<neighbor[h1].length;i++){
				if(neighbor[h1][i]!=h2&&neighbor[h1][i]!=h3&&neighbor[h1][i]!=r){
					State state = new State(neighbor[h1][i],h2,h3,r);
					state.setDepth(1);//we can know which dog moved from the depth value, not means depth any more
					nexts.add(state);
				}
			}
			for(int i=0;i<neighbor[h2].length;i++){
				if(neighbor[h2][i]!=h1&&neighbor[h2][i]!=h3&&neighbor[h2][i]!=r){
					State state = new State(h1,neighbor[h2][i],h3,r);
					state.setDepth(2);
					nexts.add(state);
				}
			}
			for(int i=0;i<neighbor[h3].length;i++){
				if(neighbor[h3][i]!=h2&&neighbor[h3][i]!=h1&&neighbor[h3][i]!=r){
					State state = new State(h1,h2,neighbor[h3][i],r);
					state.setDepth(3);
					nexts.add(state);
				}
			}//store all possible next moves
			if(nexts!=null&&nexts.size()!=0){//compute values
				numOfNodes += nexts.size();
				for(int i=0;i<nexts.size();i++){
					State sts = nexts.get(i);
					int mins = minValue(sts,-100,100,depth);
					if(v<mins){
						v = mins;
					}
					sts.setValue(mins);
				}
			}
			if(nexts!=null&&nexts.size()!=0){//find best values(with state)
				for(int i=0;i<nexts.size();i++){
					State sts = nexts.get(i);
					if(v==sts.getValue()){
						results.add(sts);
					}
				}		
			}
			System.out.println("Level of the generated tree: "+maxDepth);
			System.out.println("Number of nodes generated: "+numOfNodes);
			System.out.println("Number of MAX pruning occurred: "+numOfMaxCut);
			System.out.println("Number of MIN pruning occurred: "+numOfMinCut);
			if(results.size()!=0&&results!=null){//special case
				int size = 0;
				int sum = h1+h2+h3;
				int sum2 = 0;
				for(int i=0;i<results.size();i++){
					size++;
					State sts = new State(2,6,10,7);//choose this to win immediately
					State result = results.get(i);
					if(result.sameState(sts)){
						int h11 = result.getHound1();
						int h22 = result.getHound2();
						int h33 = result.getHound3();
						if(h11!=h1&&h11!=h2&&h11!=h3){//find where a hound moved to
							
							sum2 = h22+h33;
							houndToMove = sum - sum2;//since the other 2 hounds didn't move, we can get the moved hound in this way
							return h11;
						}
						if(h22!=h1&&h22!=h2&&h22!=h3){
							
							sum2 = h11+h33;
							houndToMove = sum - sum2;
							return h22;
						}
						if(h33!=h1&&h33!=h2&&h33!=h3){
							
							sum2 = h22+h11;
							houndToMove = sum - sum2;
							return h33;
						}
					}
				}
				int index = (int)(Math.random()*results.size());
				State state = results.get(index);
				if(state.getDepth()==1){//use depth variable to get which hound moved
					houndToMove = h1;
					return (Integer)results.get(index).getHound1();
				}
				else if(state.getDepth()==2){
					houndToMove = h2;
					return (Integer)results.get(index).getHound2();
				}
				else if(state.getDepth()==3){
					houndToMove = h3;
					return (Integer)results.get(index).getHound3();
				}
			}
			return 11;
		}
		return 11;
	}
	public int maxValue(State s,int a,int b,int depth){//maxValue method, very much like the pseudo code
		
		maxDepth = max(maxDepth,(11-depth));
		int v = -100;
		int h1 = s.getHound1();
		int h2 = s.getHound2();
		int h3 = s.getHound3();
		int r = s.getHare();
		if(player.equals("hound")){
			if(hareWin(s)){//set different utility for different situations
				
				v = 5;
				return v;
			}
			else if(houndWin(s)){
			
				v = -1;
				return v;
			}
			else if(depth<=0){
				
				return evaluate(s);//return the evaluate value if reach the depth
			}
			for(int i=0;i<neighbor2[r].length;i++){
				
				if(neighbor2[r].length!=0&&neighbor2[r][i]!=h1&&neighbor2[r][i]!=h2&&neighbor2[r][i]!=h3){
					State state = new State(h1,h2,h3,neighbor2[r][i]);
					numOfNodes++;
					v = max(v,minValue(state,a,b,depth-1));
					if(v>=b){//prune
						numOfMaxCut++;
						return v;
					}
					a = max(a,v);
				}
				else{
					continue;
				}
			}
		}
		else if(player.equals("hare")){
			if(hareWin(s)){
				
				v = -1;
				return v;
			}
			else if(houndWin(s)){
				
				v = 5;
				return v;
			}
			else if(depth<=0){
				
				return 4 - evaluate(s);
			}
			for(int i=0;i<neighbor[h1].length;i++){

				if(neighbor[h1][i]!=h2&&neighbor[h1][i]!=h3&&neighbor[h1][i]!=r){

					State state = new State(neighbor[h1][i],h2,h3,r);
					numOfNodes++;
					v = max(v,minValue(state,a,b,depth-1));
					if(v>=b){//prune
						numOfMaxCut++;
						return v;
					}
					a = max(a,v);
				}
				else{
					continue;
				}
			}
			for(int i=0;i<neighbor[h2].length;i++){

				if(neighbor[h2][i]!=h1&&neighbor[h2][i]!=h3&&neighbor[h2][i]!=r){

					State state = new State(h1,neighbor[h2][i],h3,r);
					numOfNodes++;
					v = max(v,minValue(state,a,b,depth-1));
					if(v>=b){//prune
						numOfMaxCut++;
						return v;
					}
					a = max(a,v);
				}
				else{
					continue;
				}
			}
			for(int i=0;i<neighbor[h3].length;i++){

				if(neighbor[h3][i]!=h2&&neighbor[h3][i]!=h1&&neighbor[h3][i]!=r){

					State state = new State(h1,h2,neighbor[h3][i],r);
					numOfNodes++;
					v = max(v,minValue(state,a,b,depth-1));
					if(v>=b){//prune
						numOfMaxCut++;
						return v;
					}
					a = max(a,v);
				}
				else{
					continue;
				}
			}	
		}
		
		return v;
	}
	public int minValue(State s,int a,int b,int depth){//minValue method, very much like the pseudo code
	
		maxDepth = max(maxDepth,(11-depth));
		int h1 = s.getHound1();
		int h2 = s.getHound2();
		int h3 = s.getHound3();
		int r = s.getHare();
		int v = 100;
		if(player.equals("hound")){
			if(hareWin(s)){
				v = 5;
				return v;
			}
			else if(houndWin(s)){
				
				v = -1;
				return v;
			}
			else if(depth<=0){

				return evaluate(s);
			}
			for(int i=0;i<neighbor[h1].length;i++){
	
				if(neighbor[h1][i]!=h2&&neighbor[h1][i]!=h3&&neighbor[h1][i]!=r){
	
					State state = new State(neighbor[h1][i],h2,h3,r);
					numOfNodes++;
					v = min(v,maxValue(state,a,b,depth-1));
					if(v<=a){
						numOfMinCut++;
						return v;
					}
					b = min(b,v);
				}
				else{
					continue;
				}
			}
			for(int i=0;i<neighbor[h2].length;i++){
	
				if(neighbor[h2][i]!=h1&&neighbor[h2][i]!=h3&&neighbor[h2][i]!=r){
	
					State state = new State(h1,neighbor[h2][i],h3,r);
					numOfNodes++;
					v = min(v,maxValue(state,a,b,depth-1));
					if(v<=a){
						numOfMinCut++;
						return v;
					}
					b = min(b,v);
				}
				else{
					continue;
				}
			}
			for(int i=0;i<neighbor[h3].length;i++){
	
				if(neighbor[h3][i]!=h2&&neighbor[h3][i]!=h1&&neighbor[h3][i]!=r){
	
					State state = new State(h1,h2,neighbor[h3][i],r);
					numOfNodes++;
					v = min(v,maxValue(state,a,b,depth-1));
					if(v<=a){
						numOfMinCut++;
						return v;
					}
					b = min(b,v);
				}
				else{
					continue;
				}
			}
		}
		else if(player.equals("hare")){
			if(hareWin(s)){
				v = -1;
				return v;
			}
			else if(houndWin(s)){
				
				v = 5;
				return v;
			}
			else if(depth<=0){
	
				return 4 - evaluate(s);
			}
			for(int i=0;i<neighbor2[r].length;i++){
				
				if(neighbor2[r].length!=0&&neighbor2[r][i]!=h1&&neighbor2[r][i]!=h2&&neighbor2[r][i]!=h3){
					State state = new State(h1,h2,h3,neighbor2[r][i]);
					numOfNodes++;
					v = min(v,maxValue(state,a,b,depth-1));
					if(v<=a){
						numOfMinCut++;
						return v;
					}
					b = min(b,v);
				}
				else{
					continue;
				}
			}
		}
		return v;
	}
	public boolean canBeNextHare(int i){//never used,decide whether i can be the next one 
		
		int r = findHare();
		for(int j=0;j<neighbor2[r].length;j++){
			
			if(i==neighbor2[r][j])
				return true;
		}
		return false;
	}
	public void easy(){//easy method: get all the possible next one move, use evaluate 
		//function to get their value, and randomly return a best one
		State s = currentState();
		int h1 = s.getHound1();
		int h2 = s.getHound2();
		int h3 = s.getHound3();
		int r = findHare();
		int value = -1;
		ArrayList<State> nexts = new ArrayList<State>();
		ArrayList<State> results = new ArrayList<State>();
		if(player.equals("hound")){
			
			for(int m=0;m<neighbor2[r].length;m++){
				if(neighbor2[r][m]!=h1&&neighbor2[r][m]!=h2&&neighbor2[r][m]!=h3){
					State state = new State(h1,h2,h3,neighbor2[r][m]);
					int v = evaluate(state);
					value = max(v,value);
					state.setValue(v);
					nexts.add(state);
				}
			}
			for(int m=0;m<nexts.size();m++){
				State state = nexts.get(m);
				if(state.getValue()==value){
					results.add(state);
				}
			}
			if(results.size()!=0&&results!=null){

				int index = (int)(Math.random()*results.size());
				buttons.get(r).setRole("nothing");
				buttons.get(results.get(index).getHare()).setRole("hare");
			}
		}
		else if(player.equals("hare")){

			for(int m=0;m<neighbor[h1].length;m++){
				if(neighbor[h1][m]!=h2&&neighbor[h1][m]!=h3&&neighbor[h1][m]!=r){
					State state = new State(neighbor[h1][m],h2,h3,r);
					state.setDepth(1);
					nexts.add(state);
				}
			}
			for(int m=0;m<neighbor[h2].length;m++){
				if(neighbor[h2][m]!=h1&&neighbor[h2][m]!=h3&&neighbor[h2][m]!=r){
					State state = new State(h1,neighbor[h2][m],h3,r);
					state.setDepth(2);
					nexts.add(state);
				}
			}
			for(int m=0;m<neighbor[h3].length;m++){
				if(neighbor[h3][m]!=h2&&neighbor[h3][m]!=h1&&neighbor[h3][m]!=r){
					State state = new State(h1,h2,neighbor[h3][m],r);
					state.setDepth(3);
					nexts.add(state);
				}
			}
			for(int m=0;m<nexts.size();m++){
				State state = nexts.get(m);
				int v = 4 - evaluate(state);//It's good to hard, means it's bad to hound
				state.setValue(v);
				value = max(v,value);
			}
			for(int m=0;m<nexts.size();m++){
				State state = nexts.get(m);
				if(value==state.getValue()){
					results.add(state);
				}
			}
			if(results.size()!=0&&results!=null){

				int index = (int)(Math.random()*results.size());
				int whichHound = results.get(index).getDepth();//same way, use depth to get which hound moved
				if(whichHound==1){
					buttons.get(h1).setRole("nothing");
					buttons.get(results.get(index).getHound1()).setRole("hound");
					repeatTen(buttons.get(h1),buttons.get(results.get(index).getHound1()));
				}
				if(whichHound==2){
					buttons.get(h2).setRole("nothing");
					buttons.get(results.get(index).getHound2()).setRole("hound");
					repeatTen(buttons.get(h2),buttons.get(results.get(index).getHound2()));

				}
				if(whichHound==3){
					buttons.get(h3).setRole("nothing");
					buttons.get(results.get(index).getHound3()).setRole("hound");
					repeatTen(buttons.get(h3),buttons.get(results.get(index).getHound3()));

				}
			}
		}
	}
	public void medium(int h1, int h2, int h3){//medium method
		//for hare(AI), I store the next move of hare(neighbor2) in a better way, so it 
		//always try to move left first, then try to stay in the column, and if no where 
		//to go, it will back.
		if(player.equals("hound")){
			BoolButton b1 = buttons.get(h1);
			BoolButton b2 = buttons.get(h2);
			BoolButton b3 = buttons.get(h3);
			boolean trapped = true;
			if(b1.getRole().equals("hound")&&b2.getRole().equals("hound")&&b3.getRole().equals("hound")){
				
				if(findHare()<11){
					for(int n=0;n<neighbor2[findHare()].length;n++){
						
						BoolButton b = buttons.get(neighbor2[findHare()][n]);
						if(b.getID()!=h1&&b.getID()!=h2&&b.getID()!=h3){
							
							trapped = false;
							buttons.get(findHare()).setRole("nothing");
							b.setRole("hare");
							break;
						}
					}
					checkHoundWin();
					checkHareWin();
				}
			}
		}
		//for hound(AI), they will try the moves first if they still stand in a line after
		//a move, stand in a line means the three hounds are "connected", and hare cannot
		//move to their left. If every possible next move cannot make them in a line,
		//just choose a random one.
		else if(player.equals("hare")){
			Boolean moved = false;
			State sts = currentState();
			int r = findHare();
			int sum = sts.getHound1()+sts.getHound2()+sts.getHound3();//same way, showed in hard method
			int sum2 = 0;
			int[][] standInLine = {{2,6,10},{2,5,10},{2,5,9},{1,5,10},{2,5,8},{0,5,10},{1,5,9},{0,5,9},{1,5,8},{0,5,8},{0,4,8}};
			ArrayList<State> nexts = new ArrayList<State>();
			for(int m=0;m<neighbor[h1].length;m++){
				if(neighbor[h1][m]!=h2&&neighbor[h1][m]!=h3&&neighbor[h1][m]!=r){
					State state = new State(neighbor[h1][m],h2,h3,r);
					state.setDepth(1);//store a value(depth) in order to know which hound moved
					nexts.add(state);
				}
			}
			for(int m=0;m<neighbor[h2].length;m++){
				if(neighbor[h2][m]!=h1&&neighbor[h2][m]!=h3&&neighbor[h2][m]!=r){
					State state = new State(h1,neighbor[h2][m],h3,r);
					state.setDepth(2);
					nexts.add(state);
				}
			}
			for(int m=0;m<neighbor[h3].length;m++){
				if(neighbor[h3][m]!=h2&&neighbor[h3][m]!=h1&&neighbor[h3][m]!=r){
					State state = new State(h1,h2,neighbor[h3][m],r);
					state.setDepth(3);
					nexts.add(state);
				}
			}
			for(int i=0;i<standInLine.length;i++){//make a move, if they still stand in a line after that move
				
				for(int j=0;j<nexts.size();j++){
					State state = nexts.get(j);
					if(state.sameHoundState(standInLine[i][0],standInLine[i][1],standInLine[i][2])){
						int h11 = state.getHound1();
						int h22 = state.getHound2();
						int h33 = state.getHound3();
						if(h11!=h1&&h11!=h2&&h11!=h3){
							moved = true;
							sum2 = h22+h33;
							buttons.get(sum - sum2).setRole("nothing");
							buttons.get(h11).setRole("hound");
							repeatTen(buttons.get(sum - sum2),buttons.get(h11));
							break;
						}
						if(h22!=h1&&h22!=h2&&h22!=h3){
							moved = true;
							sum2 = h11+h33;
							buttons.get(sum - sum2).setRole("nothing");
							buttons.get(h22).setRole("hound");
							repeatTen(buttons.get(sum - sum2),buttons.get(h22));
							break;
						}
						if(h33!=h1&&h33!=h2&&h33!=h3){
							moved = true;
							sum2 = h22+h11;
							buttons.get(sum - sum2).setRole("nothing");
							buttons.get(h33).setRole("hound");
							repeatTen(buttons.get(sum - sum2),buttons.get(h33));
							break;
						}
					}
				}
				if(moved==true){
					break;
				}
			}
			if(moved==false){//if they cannot stand in a line after any possible move, just pick one randomly
				int index = (int)(Math.random()*nexts.size());
				int whichHound = nexts.get(index).getDepth();
				if(whichHound==1){
					buttons.get(h1).setRole("nothing");
					buttons.get(nexts.get(index).getHound1()).setRole("hound");
					repeatTen(buttons.get(h1),buttons.get(nexts.get(index).getHound1()));
				}
				if(whichHound==2){
					buttons.get(h2).setRole("nothing");
					buttons.get(nexts.get(index).getHound2()).setRole("hound");
					repeatTen(buttons.get(h2),buttons.get(nexts.get(index).getHound2()));
				}
				if(whichHound==3){
					buttons.get(h3).setRole("nothing");
					buttons.get(nexts.get(index).getHound3()).setRole("hound");
					repeatTen(buttons.get(h3),buttons.get(nexts.get(index).getHound3()));
				}
			}
		}
	}
	public int evaluate(State s){//evaluate function, in terms of hare. One hound in 
		//the same column:+1, one hound in the right of hare:+2
		int i = s.getHound1();
		int j = s.getHound2();
		int k = s.getHound3();
		int r = s.getHare();
		int[] a1 = {2,6,10,7};
		int[] a2 = {1,2,5,6,7,9,10};
		int[] a3 = {0,1,2,4,5,6,7,8,9,10};
		int value = 0;
		if(r==2||r==6||r==10){//when hare in the 4th column
			
			for(int m=0;m<a1.length;m++){//when in the same column
				
				if(i==a1[m])
					value++;
				else if(j==a1[m])
					value++;
				else if(k==a1[m])
					value++;
			}
			if(i==7||j==7||k==7){//when in the right
				value++;
			}
			return value;
		}
		if(r==1||r==5||r==9){//when hare in the 3rd column
			
			for(int m=0;m<a2.length;m++){//when in the same column
				
				if(i==a2[m])
					value++;
				else if(j==a2[m])
					value++;
				else if(k==a2[m])
					value++;
			}
			for(int m=0;m<a1.length;m++){//when in the right
				if(i==a1[m])
					value++;
				else if(j==a1[m])
					value++;
				else if(k==a1[m])
					value++;
			}
			return value;
		}
		if(r==0||r==4||r==8){//when hare in the 2nd column
			
			for(int m=0;m<a3.length;m++){//when in the same column
				
				if(i==a3[m])
					value++;
				else if(j==a3[m])
					value++;
				else if(k==a3[m])
					value++;
			}
			for(int m=0;m<a2.length;m++){//when in the right
				
				if(i==a2[m])
					value++;
				else if(j==a2[m])
					value++;
				else if(k==a2[m])
					value++;
			}
			return value;
		}
		return value;
	}
	public void actionPerformed(ActionEvent e){//button click event
		
		if(e.getSource() instanceof BoolButton){//if click a BoolButton
			
			makeMove();
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {//mouse release event
		if(e.getSource() instanceof BoolButton)
			
			makeMove();
	}
	public void resetAll(){//set all buttons as unselected
		
		for(int i=0;i<buttons.size();i++){
			
			buttons.get(i).reset();
		}
	}
	public void unselectHounds(){//set all hounds as unselected and change to the relative background
		
		for(int i=0;i<buttons.size();i++){
			
			BoolButton b = buttons.get(i);
			if(b.getRole().equals("hound")){
				
				b.setIcon(new ImageIcon("src/resource/4.png"));
			}
		}
	}
	public void unselectHare(){//set hare as unselected and change to the relative background
	
		int i = findHare();
		buttons.get(i).setIcon(new ImageIcon("src/resource/3.png"));
	}
	public int findHare(){//return the position of hare
		
		for(int i=0;i<buttons.size();i++){
			
			if(buttons.get(i).getRole().equals("hare"))
				return buttons.get(i).getID();
		}
		return 11;
	}
	public void houndWin(){//never used
		
		System.out.println("Hounds WIN!");
	}
	public boolean checkHareWin(){//check whether hare win the game, the hare will win
		//when no hound is in the left side of the hare. Then show the window
		int i = findHare();
		int m = 0;
		if(i==0||i==4||i==8){
			Object[] options = {"restart"};
			int response = JOptionPane.showOptionDialog(null,"Hare Win!", "HARE WIN",JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if(response==0){
				restart();
			}
			return true;
		}
		else if(i==1||i==5||i==9){
			
			for(int j=0;j<buttons.size();j++){
				
				if(buttons.get(j).getRole().equals("hound")){
					
					int k = buttons.get(j).getID();
					if(k==3||k==0||k==4||k==8)
						m++;	
				}
			}
			if(m==0){
				Object[] options = {"restart"};
				int response = JOptionPane.showOptionDialog(null,"Hare Win!", "HARE WIN",JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(response==0){
					restart();
				}
				return true;
			}
		}
		return false;
	}
	public boolean hareWin(State s){//check if the state is hare win state
		int i = s.getHound1();
		int j = s.getHound2();
		int k = s.getHound3();
		int r = s.getHare();
		if(r==0||r==4||r==8){

			return true;
		}
		else if(r==1||r==5||r==9){
			
			if(i!=0&&i!=3&&i!=4&&i!=8&&j!=0&&j!=3&&j!=4&&j!=8&&k!=0&&k!=3&&k!=4&&k!=8)
				return true;
		}
		return false;
	}
	public boolean houndWin(State s){//check if the state is hound win state
		
		int i = s.getHound1();
		int j = s.getHound2();
		int k = s.getHound3();
		int r = s.getHare();
		if(r==1){
			
			if((i==0||i==2||i==5)&&(j==0||j==2||j==5)&&(k==0||k==2||k==5))
				return true;
		}
		else if(r==9){
			
			if((i==8||i==10||i==5)&&(j==10||j==8||j==5)&&(k==10||k==8||k==5))
				return true;
		}
		else if(r==7){
	
			if((i==10||i==2||i==6)&&(j==10||j==2||j==6)&&(k==10||k==2||k==6))
				return true;
		}
		return false;
	}
	public boolean checkHoundWin(){//check whether hound win, and show the window
		//hound will win only under the 3 conditions below
		int i = findHare();
		int m = 0;
		if(i==1){
			
			for(int j=0;j<buttons.size();j++){
				
				if(buttons.get(j).getRole().equals("hound")){
				
					int k = buttons.get(j).getID();
					if(k==0||k==5||k==2){
						m++;
					}
				}
			}
		}
		else if(i==9){
			
			for(int j=0;j<buttons.size();j++){
				
				if(buttons.get(j).getRole().equals("hound")){
				
					int k = buttons.get(j).getID();
					if(k==10||k==5||k==8){
						m++;
					}
				}
			}
		}
		else if(i==7){
	
			for(int j=0;j<buttons.size();j++){
				
				if(buttons.get(j).getRole().equals("hound")){
				
					int k = buttons.get(j).getID();
					if(k==10||k==6||k==2){
						m++;
					}
				}
			}
		}
		if(m==3){

			Object[] options = {"restart"};
			int response = JOptionPane.showOptionDialog(null,"Hound Win!", "HOUND WIN",JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if(response==0){
				restart();
			}
			return true;
		}
		return false;
	}
	public State getStateFromStates(State s){//abandoned
		
		if(inTheStates(s)){
			
			for(int i=0;i<states.size();i++){
				
				State sts = states.get(i);
				if(sts.sameState(s)){
					
					return sts;
				}
			}
		}
		return null;
	}
	public boolean inTheStates(State state){//abandoned
		
		int h1 = state.getHound1();
		int h2 = state.getHound1();
		int h3 = state.getHound1();
		int r = state.getHare();
		int d = state.getDepth();
		if(states==null||states.size()==0){
			
			return false;
		}
		else{
			
			for(int i=0;i<states.size();i++){
				State s = states.get(i);
				if(r==s.getHare()&&d==s.getDepth()){
					
					if((h1==s.getHound1()||h1==s.getHound2()||h1==s.getHound3())&&
							(h2==s.getHound1()||h2==s.getHound2()||h2==s.getHound3())&&
							(h3==s.getHound1()||h3==s.getHound2()||h3==s.getHound3())){
						return true;
					}
						
				}
			}
		}
		return false;
	}
	public int getAction(int i){//abandoned

		int r = findHare();
		int h1 = 11;
		int h2 = 11;
		int h3 = 11;
		ArrayList resultMoves = new ArrayList();
		for(int m=0;m<buttons.size();m++){
			if(buttons.get(m).getRole().equals("hound")){
				if(h1==11)
					h1 = buttons.get(m).getID();
				else if (h2 == 11)
					h2 = buttons.get(m).getID();
				else if(h3 == 11)
					h3 = buttons.get(m).getID();
			}
		}
		for(int m=0;m<neighbor2[r].length;m++){
			if(neighbor2[r][m]!=h1&&neighbor2[r][m]!=h2&&neighbor2[r][m]!=h3){
				State s = new State(h1,h2,h3,neighbor2[r][m]);
				if(inTheStates(s)){
					
					for(int j=0;j<states.size();j++){
						
						State state = states.get(j);
						if(state.sameState(s)){
							if(i==state.getValue()&&state.getDepth()==9){

								resultMoves.add(state.getHare());
							}
						}
					}
				}
			}
		}
		if(resultMoves.size()!=0&&resultMoves!=null){
			int index = (int)(Math.random()*resultMoves.size());
			return (Integer)resultMoves.get(index);
		}
		return 11;
	}
	
	public int max(int a,int b){//return a bigger one
		
		if(a>=b)
			return a;
		return b;
	}
	public int min(int a,int b){//return a smaller one
		
		if(a<b)
			return a;
		return b;
	}
	public State currentState(){//return the current state
		
		int h1 = 11;
		int h2 = 11;
		int h3 = 11;
		int r = findHare();
		for(int j=0;j<buttons.size();j++){
			
			if(buttons.get(j).getRole().equals("hound")){
				
				if(h1==11)
					h1 = buttons.get(j).getID();
				else if (h2 == 11)
					h2 = buttons.get(j).getID();
				else if(h3 == 11)
					h3 = buttons.get(j).getID();
			}
		}
		State s = new State(h1,h2,h3,r);
		return s;
	}
	public void disableHounds(){//able all buttons and then disable the hound button, and set their background
		
		State s = currentState();
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).setEnabled(true);
		}
		buttons.get(s.getHound1()).setEnabled(false);
		buttons.get(s.getHound2()).setEnabled(false);
		buttons.get(s.getHound3()).setEnabled(false);
		buttons.get(s.getHound1()).setDisabledIcon(new ImageIcon("src/resource/4.png"));
		buttons.get(s.getHound2()).setDisabledIcon(new ImageIcon("src/resource/4.png"));
		buttons.get(s.getHound3()).setDisabledIcon(new ImageIcon("src/resource/4.png"));
		
	}
	public void disableHare(){//able all buttons and then disable the hare button, and set their background
		
		State s = currentState();
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).setEnabled(true);
		}
		buttons.get(s.getHare()).setEnabled(false);
		buttons.get(s.getHare()).setDisabledIcon(new ImageIcon("src/resource/3.png"));
	}
	public void ableAll(){//able all the buttons
		
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).setEnabled(true);
		}
	}
	public void disableAll(){//disable all the buttons
		
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).setEnabled(false);
		}
	}
	public void restart(){//restart the game, actually re-initial the following item
		buttons.get(7).setRole("hare");
		buttons.get(0).setRole("hound");
		buttons.get(3).setRole("hound");
		buttons.get(8).setRole("hound");
		buttons.get(1).setRole("nothing");
		buttons.get(2).setRole("nothing");
		buttons.get(4).setRole("nothing");
		buttons.get(5).setRole("nothing");
		buttons.get(6).setRole("nothing");
		buttons.get(9).setRole("nothing");
		buttons.get(10).setRole("nothing");
		repeatedColumn = null;
		repeatTime = 0;
		if(player.equals("hare")){
			
			if(level.equals("Hard")){
				int nextMove = hard(currentState());
				buttons.get(houndToMove).setRole("nothing");
				buttons.get(nextMove).setRole("hound");
				disableHounds();
			}
			else{
				buttons.get(0).setRole("nothing");
				buttons.get(5).setRole("hound");
			}
		}
		resetAll();
		ableAll();
		if(player.equals("hound")){
			disableHare();
		}
		else if(player.equals("hare")){
			disableHounds();
		}
	}
	public Boolean inSameColumn(int i,int j){//decide whether two position in the same column
		int[] a1 = {0,4,8};
		int[] a2 = {1,5,9};
		int[] a3 = {2,6,10};
		for(int k=0;k<a1.length;k++){
			if(i==a1[k]){
				for(int m=0;m<a1.length;m++){
					if(j==a1[m]){
						repeatedColumn = a1;
						return true;
					}
				}
			}
		}
		for(int k=0;k<a2.length;k++){
			if(i==a2[k]){
				for(int m=0;m<a2.length;m++){
					if(j==a2[m]){
						repeatedColumn = a2;
						return true;
					}
				}
			}
		}
		for(int k=0;k<a3.length;k++){
			if(i==a3[k]){
				for(int m=0;m<a3.length;m++){
					if(j==a3[m]){
						repeatedColumn = a3;
						return true;
					}
				}
			}
		}
		repeatedColumn = null;
		return false;
	}
	public void repeatTenInAColumn(BoolButton i,BoolButton j){//abandoned, decide whether repeat happens in the same column
		
		if(repeatedColumn!=null&&repeatedColumn.length>0){
			int[] previous = repeatedColumn;
			inSameColumn(i.getID(),j.getID());
			if(repeatedColumn!=null&&previous[0]==repeatedColumn[0]){
				repeatTime++;
//				System.out.println("TIME:"+repeatTime);
				if(repeatTime>=9){
					Object[] options = {"restart"};
					int response = JOptionPane.showOptionDialog(null,"Hare Win(10 times more)!", "HARE WIN",JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if(response==0){
						restart();
					}
				}
			}
			else if(previous!=repeatedColumn){
				repeatTime = 0;
			}
		}
		else if(repeatedColumn==null){
			inSameColumn(i.getID(),j.getID());
		}
	}
	public void repeatTen(BoolButton i,BoolButton j){//decide whether hound moves vertically 10 times in a row
		
		if(inSameColumn(i.getID(),j.getID())){
			repeatTime++;
			if(repeatTime>=9){
				Object[] options = {"restart"};
				int response = JOptionPane.showOptionDialog(null,"Hare Win(10 times more)!", "HARE WIN",JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(response==0){
					restart();
				}
			}
		}
		else{
			repeatTime = 0;
		}
	}
	public static void main(String[] args){
	
		new ChessBoard();
	}
	
	
}
