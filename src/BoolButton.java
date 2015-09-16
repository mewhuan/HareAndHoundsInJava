/*********************
HARE AND HOUND GAME
CLASS BOOLBUTTON
DESIGNED BY DONG LI
VERSION 1.0
2014
**********************/	
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
/******** BoolButton ********
Inherit JButton class.
This is a JButton, with ID, role, and like a coin, it change its side if you click it many
times in a row.
*/	
public class BoolButton extends JButton implements ActionListener{
	
	private boolean selected;
	private String role = "nothing";
	private int ID; 
	
	public BoolButton(){
		
		this(null);
	}
	public BoolButton(String s){
		
		super(s);
		this.addActionListener(this);
	}
	public BoolButton(int i){
		
		ID = i;
		this.addActionListener(this);

	}
	public String getRole(){
		
		return role;
	}
	public void setRole(String s){//set role, and change the certain button's background
		
		this.role = s;
		if(s.equals("hound")){
			
			this.setIcon(new ImageIcon("src/resource/4.png"));
		}
		else if(s.equals("hare")){
			
			this.setIcon(new ImageIcon("src/resource/3.png"));
		}
		else 
			this.setIcon(null);
	}
	public void cleanRole(){//forget to use, but the function used many times
		
		this.role = "nothing";
		this.setIcon(null);
	}
	public void setSelected(boolean b){
		
		this.selected = b;
	}
	public boolean getSelected(){
		
		return selected;
	}
	public void reset(){
		
		this.selected = false;
	}
	public void setID(int i){
		
		this.ID = i;
	}
	public int getID()
	{
		return ID;
	}
	public void actionPerformed(ActionEvent e){//change button's side(like a coin), and change its background decided by role 
		
		selected = !selected;
		if(role.equals("hound")){
			if(selected){
				
				this.setIcon(new ImageIcon("src/resource/5.png"));
			}
			else
				this.setIcon(new ImageIcon("src/resource/4.png"));
		}
		if(role.equals("hare")){
			
			if(selected){
				
				this.setIcon(new ImageIcon("src/resource/1.png"));
			}
			else 
				this.setIcon(new ImageIcon("src/resource/3.png"));
		}
	}
}
