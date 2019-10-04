//name:    date:
import java.util.*;
import java.io.*;
public class MazeMaster
{
   public static void main(String[] args)
   {
      Scanner sc = new Scanner(System.in);
      String filename;
      System.out.print("Enter the filename: ");
      filename = sc.next();
      System.out.println("Options: ");
      System.out.println("1: Create Random Maze in File and Solve It.");
      System.out.println("2: Solve a Maze in File.");
      System.out.print("Please make a selection: ");
      switch(sc.nextInt()){
         case 1:
            try{
               System.out.println();
               System.out.print("Please enter Dimensions (L before H, separated by a space):");;
               MazeMaker.print(filename,sc.nextInt(),sc.nextInt());
            }
            catch(FileNotFoundException e){
               System.out.println("Invalid File Name, Please Select Again.");
               main(args);
               System.exit(0);
            }
            break;
         case 2:break;
         default:
            System.out.println();
            System.out.println("Invalid Selection, Please Select Again.");
            main(args);
            System.exit(0);
      }
      char[][] retArr = buildCharArr(filename);
      Maze m = new Maze(retArr);
      m.display();
      while(true){
         retArr = buildCharArr(filename);
         m = new Maze(retArr);
         System.out.println("Options: ");
         System.out.println("1: Mark all paths.");
         System.out.println("2: Mark all paths, and display the count of all steps.");
         System.out.println("3: Show only the correct path.");
         System.out.println("4: Show only the correct path. If no path exists, display 'No path exists'.");
         System.out.println("5: Show only the correct path, and display the count of steps.");
         System.out.print("Please make a selection: ");
         m.solve(sc.nextInt());
         m.display();
      }
   } 
   //take in a filename, and return a char[][]
   public static char[][] buildCharArr(String fileName)
   {
      try{
         Scanner infile = new Scanner(new File(fileName));
         char[][] matrix = new char[infile.nextInt()][infile.nextInt()];
         infile.nextLine();
         for(int y=0;y<matrix.length;y++){
            String line = infile.nextLine();
            for(int x=0;x<matrix[0].length;x++)
               matrix[y][x] = line.charAt(x);
         }
         return matrix;
      }
      catch(FileNotFoundException e){
         System.out.println("Sorry, that file does not exist. Enter a new filename:");
         Scanner sc = new Scanner(System.in);
         return buildCharArr(sc.next());
      }
   }
}


class Maze
{
   //Constants
   private final char WALL = 'W';
   private final char PATH = '.';
   private final char START = 'S';
   private final char EXIT = 'E';
   private final char STEP = '*';
   //fields
   private char[][] maze;
   private int startRow, startCol;
   private boolean S_Exists=false, E_Exists=false;
   //constructor initializes all the fields
   public Maze(char[][] inCharArr)    
   {
      maze = inCharArr;
      for(int x=0;x<inCharArr[0].length;x++){
         for(int y=0;y<inCharArr.length;y++){
            if(maze[y][x]=='S'){
               S_Exists = true;
               startCol = x;
               startRow = y;
            }
            if(maze[y][x]=='E')
               E_Exists = true;
         }
      }
   }
   
   public void display()
   {
      if(maze==null) 
         return;
      for(int a = 0; a<maze.length; a++)
      {
         for(int b = 0; b<maze[0].length; b++)
         {
            System.out.print(maze[a][b]);
         }
         System.out.println("");
      }
      System.out.println("");
   }
   public void solve(int n)
   {
      if(!(S_Exists && E_Exists))
      {
         System.out.println("Invalid maze: Either no S or no E");
         return;
      }
      if(n==-1)
      {
         System.exit(0);
      }
      else if(n==1)
      {
         markAllPaths(startRow, startCol);
      }
      else if(n==2)
      {
         int count = markAllPathsAndCountStars(startRow, startCol);
         System.out.println("Number of steps = " + count);
      }
      else if(n==3)
      {
         displayTheCorrectPath(startRow, startCol);
      }
      else if(n==4)   //use maze3 here
      {
         if( !displayTheCorrectPath(startRow, startCol) )
            System.out.println("No path exists");   
      }     
      else if(n==5)
      {
         PossiblePath p = displayCorrectPathAndCountStars(maze,startRow, startCol, 0);
         System.out.println("Number of steps = "+p.getLength());
         maze = p.getMatrix();
         
      }
      else System.out.println("invalid submission");
      
   }
   private void markAllPaths(int r, int c)
   {
      if(r<0||c<0||r>=maze.length||c>=maze[0].length)
         return;
      if(maze[r][c] == WALL || maze[r][c] == STEP)
         return;
      if(maze[r][c] == PATH)
         maze[r][c] = STEP;
      markAllPaths(r+1,c);
      markAllPaths(r-1,c);
      markAllPaths(r,c+1);
      markAllPaths(r,c-1);
   }
        
   private int markAllPathsAndCountStars(int r, int c)
   {
      if(r<0||c<0||r>=maze.length||c>=maze[0].length)
         return 0;
      if(maze[r][c] == WALL || maze[r][c] == STEP)
         return 0;
      int n = 0;
      if(maze[r][c] == PATH){
         maze[r][c] = STEP;
         n = 1;
      }
      return n +markAllPathsAndCountStars(r+1,c)
               +markAllPathsAndCountStars(r-1,c)
               +markAllPathsAndCountStars(r,c+1)
               +markAllPathsAndCountStars(r,c-1);
   }

   private boolean displayTheCorrectPath(int r, int c)
   {
      if(r<0||c<0||r>=maze.length||c>=maze[0].length)
         return false;
      if(maze[r][c] == WALL || maze[r][c] == STEP)
         return false;
      if(maze[r][c] == EXIT)
         return true;
      if(maze[r][c] == PATH)
         maze[r][c] = STEP;
      boolean a =   displayTheCorrectPath(r+1,c)
                  ||displayTheCorrectPath(r,c+1)
                  ||displayTheCorrectPath(r-1,c)
                  ||displayTheCorrectPath(r,c-1);
      if(!a && maze[r][c] == STEP)
         maze[r][c] = PATH;
      return a;
   }
   
   private PossiblePath displayCorrectPathAndCountStars(char[][] m, int r, int c, int count)
   {
  		char[][] mn = cloneArray(m);
		int deadEnd = mn.length*mn[0].length+1 ;   
		if(r<0||c<0||r>=mn.length||c>=mn[0].length)
         return new PossiblePath(deadEnd,mn);
      if(mn[r][c] == WALL || mn[r][c] == STEP)
         return new PossiblePath(deadEnd,mn);
      if(mn[r][c] == EXIT)
         return new PossiblePath(count,mn);
      if(mn[r][c] == PATH)
         mn[r][c] = STEP;
		PossiblePath[] a = new PossiblePath[4];
		a[0] = displayCorrectPathAndCountStars(mn,r+1,c,count+1);
		a[1] = displayCorrectPathAndCountStars(mn,r,c+1,count+1);
		a[2] = displayCorrectPathAndCountStars(mn,r-1,c,count+1);
		a[3] = displayCorrectPathAndCountStars(mn,r,c-1,count+1);
		return findMin(a);
   }
   
   //This is for testing purposes. Do not change or remove this method.
   public char[][] getMaze()
   {
      return maze;
   }
	
	//For purposes of finding shortest path
	private PossiblePath findMin(PossiblePath[] array){
		PossiblePath p = array[0];
		for(int x=0;x<array.length;x++)
			if(array[x].compareTo(p)>0)
				p = array[x];
		return p;
	}
	private char[][] cloneArray(char[][] array){
		char[][] newArray = new char[array.length][array[0].length];
		for(int y=0;y<array.length;y++)
			for(int x=0;x<array[0].length;x++)
				newArray[y][x] = array[y][x];
		return newArray;
	}
}
class MazeMaker {
   public static void print(String filename, int l, int h) throws FileNotFoundException{
      PrintStream ps = new PrintStream(new FileOutputStream(new File(filename)));
      ps.print(h + " " + l + "\n");
      String[][] createdArray = newArray(l,h);
      for(int y=0;y<h;y++){
         for(int x=0;x<l;x++){
            ps.print(createdArray[y][x]);
            System.out.print(createdArray[y][x]);
         }
         ps.print("\n");
         System.out.println();
      }
   }
   private static String[][] newArray(int l, int h){
      //Instantiate Array
      String[][] arr;
      if(h<4||l<3){
         System.out.println("Array Sizes Too Small: h should be >4, l should be >3.");
         System.exit(0);
      }
      arr = new String[h][l];
      
      //Initialize Array Contents
      for(int x=0;x<l;x++)
         arr[0][x] = "W";
      arr[1][0] = "S";
      for(int x=1;x<l;x++)
         arr[1][x] = "W";
      for(int y=2;y<h-2;y++)
         for(int x=0;x<l;x++)
            arr[y][x] = "W";
      for(int x=0;x<l-1;x++)
         arr[h-2][x] = "W";
      arr[h-2][l-1] = "E";
      for(int x=0;x<l;x++)
         arr[h-1][x] = "W";   
      
      //Recursively create paths
      recur(arr,1,1,false,0);
      arr[h-2][l-2] = ".";
      
      return arr;
   }
   private static void recur(String[][] arr, int row, int col, boolean turn, int dir){
      if((row<1)||(col<1)||(row>arr.length-2)||(col>arr[0].length-2))
         return;
      if(arr[row][col] != "W")
         return;
      arr[row][col] = ".";
      if(turn){
         int n = (int)(Math.random()*100);
         if(n<0)
            return;
         else if(n<0)
            changeDir(arr,row,col,!turn);
         else
            branch(arr,row,col,!turn);
      }
      else{
         continueCurrentDir(arr,row,col,!turn,dir);
      }
   }
   private static void continueCurrentDir(String[][] arr, int row, int col, boolean turn, int dir){
      recur(arr,
            translateDir(dir,row,col)[0],
            translateDir(dir,row,col)[1],
            turn,dir);
   }
   private static void branch(String[][] arr, int row, int col, boolean turn){
      for(int x=0;x<4;x++)
         if((int)(Math.random()*100)<60)
            recur(arr,
               translateDir(x,row,col)[0],
               translateDir(x,row,col)[1],
               turn,x);
   }
   private static void changeDir(String[][] arr, int row, int col, boolean turn){
      int nd = (int)(Math.random()*4);
      recur(arr,
         translateDir(nd,row,col)[0],
         translateDir(nd,row,col)[1],
         turn,nd);
   }
   private static int[] translateDir(int dir, int currow, int curcol){
      int[] ns = new int[2];
      switch(dir){
         case 0:ns[0]=currow;ns[1]=curcol+1;break;
         case 1:ns[0]=currow-1;ns[1]=curcol;break;
         case 2:ns[0]=currow;ns[1]=curcol-1;break;
         case 3:ns[0]=currow+1;ns[1]=curcol;break;
      }
      return ns;
   }
}
class PossiblePath implements Comparable<PossiblePath>{
	private int length;
	private char[][] matrix;
	public PossiblePath(int l, char[][] m){
		length = l;
		matrix = m;
	}
	public int getLength(){return length;}
	public char[][] getMatrix(){return matrix;}
	public void setLength(int l){length = l;}
	public void setMatrix(char[][] m){matrix = m;}
	public int compareTo(PossiblePath p){return p.getLength()-getLength();}
}