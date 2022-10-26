import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class MineSweapPart extends JFrame
{
  private static final long serialVersionUID = 1L;
  private static final int WINDOW_HEIGHT = 760;
  private static final int WINDOW_WIDTH = 760;
  private static final int TOTAL_MINES = 16;
  
  
  private static int guessedMinesLeft = TOTAL_MINES;
  private static int actualMinesLeft = TOTAL_MINES;

  private static final String INITIAL_CELL_TEXT = "";
  private static final String UNEXPOSED_FLAGGED_CELL_TEXT = "@";
    private static final String EXPOSED_MINE_TEXT = "M";
  
  // visual indication of an exposed MyJButton
  private static final Color EXPOSED_CELL_BACKGROUND_COLOR = Color.gray;
  // colors used when displaying the getStateStr() String
  private static final Color EXPOSED_CELL_FOREGROUND_COLOR_MAP[] = {Color.lightGray, Color.blue, Color.green, Color.cyan, Color.yellow, 
                                           Color.orange, Color.pink, Color.magenta, Color.red, Color.red};

  
  // holds the "number of mines in perimeter" value for each MyJButton 
  private static final int MINEGRID_ROWS = 16;
  private static final int MINEGRID_COLS = 16;
  private int[][] mineGrid = new int[MINEGRID_ROWS][MINEGRID_COLS];

  private static final int NO_MINES_IN_PERIMETER_MINEGRID_VALUE = 0;
  private static final int ALL_MINES_IN_PERIMETER_MINEGRID_VALUE = 8;
  private static final int IS_A_MINE_IN_MINEGRID_VALUE = 9;
  
  private boolean running = true;
  private MyJButton[][] buttons = new MyJButton[16][16]; 
  private int slotsLeft = 256;
  
  public MineSweapPart()
  {
    this.setTitle("MineSweap                                                         " + 
                  MineSweapPart.guessedMinesLeft +" Mines left");
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    this.setResizable(false);
    this.setLayout(new GridLayout(MINEGRID_ROWS, MINEGRID_COLS, 0, 0));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    // set the grid of MyJbuttons
    this.createContents();
    
    // place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
    this.setMines();
    
    this.setVisible(true);
  }

  public void createContents()
  {
    for (int mgr = 0; mgr < MINEGRID_ROWS; ++mgr)
    {  
      for (int mgc = 0; mgc < MINEGRID_COLS; ++mgc)
      {  
        // set sGrid[mgr][mgc] entry to 0 - no mines in it's perimeter
        this.mineGrid[mgr][mgc] = NO_MINES_IN_PERIMETER_MINEGRID_VALUE; 
        
        // create a MyJButton that will be at location (mgr, mgc) in the GridLayout
        MyJButton but = new MyJButton(INITIAL_CELL_TEXT, mgr, mgc); 
        
        		
        // register the event handler with this MyJbutton
        but.addActionListener(new MyListener());
        
        this.buttons[mgr][mgc] = but;
        
        // add the MyJButton to the GridLayout collection
        this.add(but);
      }  
    }
  }


//begin nested private class
 private class MyListener implements ActionListener
 {
   public void actionPerformed(ActionEvent event)
   {
     if ( running )
     {
       // used to determine if ctrl or alt key was pressed at the time of mouse action
       int mod = event.getModifiers();
       MyJButton mjb = (MyJButton)event.getSource();
       
       // is the MyJbutton that the mouse action occurred in flagged
       boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);
       
       // is the MyJbutton that the mouse action occurred in already exposed
       boolean exposed = mjb.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
      
       // flag a cell : ctrl + left click
       if ( !flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0 )
       {
         mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);
         --MineSweapPart.guessedMinesLeft;
         
         // if the MyJbutton that the mouse action occurred in is a mine
         if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
         {
           --MineSweapPart.actualMinesLeft;
           if (MineSweapPart.actualMinesLeft == 0) {
        	   setTitle("YOU WON!!!!");
        	running =false;
        	   return;
           }
         }
         setTitle("MineSweap                                                         " + 
                  MineSweapPart.guessedMinesLeft +" Mines left");
       }
      
       // unflag a cell : alt + left click
       else if ( flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0 )
       {
         mjb.setText(INITIAL_CELL_TEXT);
         ++MineSweapPart.guessedMinesLeft;
         
         // if the MyJbutton that the mouse action occurred in is a mine
         if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
         {
        ++MineSweapPart.actualMinesLeft;
         }
         setTitle("MineSweap                                                         " + 
                   MineSweapPart.guessedMinesLeft +" Mines left");
       }
    
       // expose a cell : left click
       else if ( !flagged && !exposed )
       {
         exposeCell(mjb);
       }  
       if (slotsLeft == TOTAL_MINES) {
    	   setTitle("YOU WON!!!!");
    	   running = false;
       }
     }
   }
    
    public void exposeCell(MyJButton mjb)
    {
      if ( !running )
        return;
      
		if (buttons[mjb.ROW][mjb.COL].getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR)){
			return;
		}
		
      mjb.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
      mjb.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
      mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
      slotsLeft--;
      
      // if the MyJButton that was just exposed is a mine
      if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE )
      {  
    	  for (int i= 0; i<16; i++) {
    		  for (int j =0; j<16; j++) {
    			  if (mineGrid[i][j] != IS_A_MINE_IN_MINEGRID_VALUE) {
    				  exposeCell(buttons[i][j]);
    			  }
    			  else {
        		      buttons[i][j].setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
        		      buttons[i][j].setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[i][j]]);
        		      buttons[i][j].setText(getGridValueStr(i,j));
    			  }
    		  }
    	  }
    	  setTitle("You lost :(");
    	  running= false;
        return;
      }
//       if the MyJButton that was just exposed has no mines in its perimeter
      if ( mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE )
      {
    	  for (int x = -1; x <2; x++) {
				for(int y = -1; y<2; y++) {
					if( mjb.ROW +x >= 0 && mjb.COL +y>= 0 && mjb.COL +y <16 && mjb.ROW +x <16) {   
						exposeCell(buttons[mjb.ROW+x][mjb.COL+y]);
					}
				}
    	  }
      }
    }
  }
  // end nested private class

  public static void main(String[] args)
  {
    new MineSweapPart();
  }

  
  //************************************************************************************************

  // place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
  private void setMines()
  {
	Random rand = new Random();
    int minesLeft = TOTAL_MINES;
    while (minesLeft>0) {
		for (int i = 0; i<16; i++) {
			for (int j= 0; j<16; j++) {
				if (this.mineGrid[i][j] != IS_A_MINE_IN_MINEGRID_VALUE) {
					if (rand.nextInt(16) == 1 && minesLeft> 0) { 
						this.mineGrid[i][j] = IS_A_MINE_IN_MINEGRID_VALUE;
						--minesLeft;
					}
				}
			}
		}
    }
    for (int i = 0; i<16; i++) {
		for (int j= 0; j<16; j++) {
			if (this.mineGrid[i][j] != IS_A_MINE_IN_MINEGRID_VALUE) {
				int minesFound = 0;
				for (int x = -1; x <2; x++) {
					for(int y = -1; y<2; y++) {
						if( i+x >= 0 && j+y>= 0 && j+y <16 && i+x <16) {
							if(this.mineGrid[i+x][j+y] == IS_A_MINE_IN_MINEGRID_VALUE) {
								minesFound++;
							}
						}
					}
				}
				this.mineGrid[i][j] = minesFound;
			}
		}
    }
			
  }
  
  private String getGridValueStr(int row, int col)
  {
    // no mines in this MyJbutton's perimeter
    if ( this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE )
      return INITIAL_CELL_TEXT;
    
    // 1 to 8 mines in this MyJButton's perimeter
    else if ( this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_MINEGRID_VALUE && 
              this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_MINEGRID_VALUE )
      return "" + this.mineGrid[row][col];
    
    // this MyJButton in a mine
    else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
      return MineSweapPart.EXPOSED_MINE_TEXT;
  }
  
}
