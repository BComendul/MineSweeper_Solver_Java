import java.util.*;

class MineSweeper {


    Boolean isFlagged;
    Boolean isMine;
    Boolean isOpened;

    int flagsSurrounding;
    int surroundingMines;
    int minesToFind;
    int validunopenedNeighbors;
    int checking;

    int x;
    int y;
    private MineSweeper[][] minefield;
    private static int[] minesLeft=new int[1];
    private static ArrayList<MineSweeper>toFlag= new ArrayList<>();
    private static ArrayList<MineSweeper>toOpen=new ArrayList<>();


    //initializing unknown information cell
    MineSweeper(int x, int y){
        this.isOpened=false;
        this.x=x;
        this.y=y;
        this.isMine=false;
        this.flagsSurrounding=0;
        this.isFlagged=false;
    }

    //helper function to display found mine
    public static void mineFound(MineSweeper mine){
        if (!mine.isMine){
            minesLeft[0]--;
            mine.isMine=true;
        }
    }

    public void  setNumMines(MineSweeper mine, int numMines){
        mine.surroundingMines=numMines;
        mine.minesToFind=surroundingMines-this.flagsSurrounding;
        if (minesToFind==0){
            toOpen.add(mine);

        }
    }

    public void setFlagsSurrounding(MineSweeper mine, int flagsSurrounding){
        mine.flagsSurrounding=flagsSurrounding;
        mine.minesToFind=this.surroundingMines-flagsSurrounding;
        if (mine.minesToFind==0){
            toOpen.add(mine);
        }
    }

    public void setIsFlagged(MineSweeper mine, Boolean truth){
        mine.isFlagged=truth;
        mineFound(mine);
        if (mine.isFlagged==true){
            ArrayList<MineSweeper>something=validOpenedNeighbors(minefield,mine);
            //take opened neighbors and let them know there is a flag surrounding them
            for (int i=0;i<something.size();i++){
                something.get(i).setFlagsSurrounding(something.get(i),checkForFlags(minefield,something.get(i)));
            }
        }
    }
    //create mine object with information
    MineSweeper(int x, int y, int numMines){
        this.isOpened=true;
        this.x=x;
        this.y=y;
        this.surroundingMines=numMines;
        this.flagsSurrounding=0;
        this.isFlagged=false;
        this.isMine=false;
        this.minesToFind=numMines-flagsSurrounding;
        if (this.minesToFind==0){
            toOpen.add(this);
        }

    }
    //create the board object that all cells are inside of
    public MineSweeper(final String board, final int nMines) {
        System.out.println("number of mines: "+nMines);
        if(toOpen.size()!=0){
            toOpen=new ArrayList<>();}
        if (toFlag.size()!=0){
            toFlag= new ArrayList<>();}

        String [] thing=board.replaceAll(" ","").split("[\n]");
        minesLeft[0]=nMines;
        minefield= new MineSweeper[thing.length][thing[0].length()];
        //looks through board- converts to Minefield[][] adds 0's to opening list
        for (int y=0;y<thing.length;y++){
            for (int x=0;x<thing[0].length();x++){
                if (thing[y].substring(x,x+1).matches("[0-9]")){
                    minefield[y][x]=new MineSweeper(x,y,Integer.valueOf(thing[y].substring(x,x+1)));
                    if (thing[y].substring(x,x+1).matches("[0]")){
                    }
                }else if (thing[y].substring(x,x+1).equals("?")){
                    minefield[y][x]= new MineSweeper(x,y);
                }

            }
        }
    }

    //opening cell and updating- does not check for validity
    public static void setToOpen(MineSweeper[][] minefield,MineSweeper mine){
        mine.isOpened=true;
        //get surrounding Opened neighbors- subtract one from each of their unopened neighbors category
        ArrayList<MineSweeper>something=validOpenedNeighbors(minefield,mine);

        setValidUnopened(minefield,mine, checkForUnopened(minefield,mine));

        for (int i=0;i<something.size();i++){
            setValidUnopened(minefield,something.get(i),checkForUnopened(minefield,something.get(i)));
        }
    }
    //updating unopened neighbors
    public static void setValidUnopened(MineSweeper[][] minefield, MineSweeper mine, int unopened){
        mine.validunopenedNeighbors=unopened;
        //change !=0 to something better to avoid repeating
        if (mine.validunopenedNeighbors==mine.surroundingMines&&mine.surroundingMines!=0&& mine.flagsSurrounding!=mine.surroundingMines){
            toFlag.add(mine);
        }
    }

    // solving puzzle loop
    public String solve() {
        while (minesLeft[0]>0){
            printMap(minefield);
            while (toOpen.size()>0){
                MineSweeper opening= toOpen.get(0);
                toOpen.remove(0);
                ArrayList<MineSweeper>neighbors=validUnOpenedNeighbors(minefield, opening);
                for (int i=0;i<neighbors.size();i++) {
                    //take neighboring cells and open the ones that are not flagged
                    MineSweeper neighbor = neighbors.get(i);
                    if (!neighbor.isFlagged) {
                        neighbor.setNumMines(neighbor, Game.open(neighbor.y, neighbor.x));
                        neighbor.setFlagsSurrounding(neighbor, checkForFlags(minefield, neighbor));
                        setToOpen(minefield, neighbor);
                    }
                }
            }
            while(toFlag.size()>0){

                MineSweeper mine=toFlag.get(0);
                toFlag.remove(0);

                ArrayList<MineSweeper>FlagList=validUnOpenedNeighbors(minefield,mine);
                for (int i=0;i<FlagList.size();i++){
                    if (!FlagList.get(i).isOpened){
                        setIsFlagged(FlagList.get(i),true);
                        mineFound(FlagList.get(i));

                    }
                }
            }
            if (toFlag.size()==0&&toOpen.size()==0&&minesLeft[0]!=0){
                if (!constrainedView(minefield)){
                    //returns "?" if determined unsolvable
                    return "?";
                }
            }}

        //final updates and answer
        String answer="";

        for (int y=0;y<minefield.length;y++){
            for (int x=0;x<minefield[0].length;x++){
                if (!minefield[y][x].isFlagged&& !minefield[y][x].isOpened){
                    System.out.println(x+","+y);
                    minefield[y][x].isOpened=true;
                    minefield[y][x].surroundingMines=Game.open(minefield[y][x].y,minefield[y][x].x);
                }
                if (x==minefield[0].length-1){
                    answer+=minefield[y][x].toString();
                }else{
                    answer+=minefield[y][x].toString()+" ";
                }
            }
            if (y!=minefield.length-1){
                answer+="\n";
            }
        }
        return answer;
    }
    public static int checkForFlags(MineSweeper[][] field, MineSweeper mine){
        //takes mine, searches neighbors, returns amount of flags surrounding
        ArrayList<MineSweeper>neigbors=validUnOpenedNeighbors(field, mine);
        int flags=0;
        for (int i=0;i<neigbors.size();i++){
            if (neigbors.get(i).isFlagged){
                flags++;
            }
        }
        return flags;
    }
    public static void printMap(MineSweeper[][] field){
        int limitY=field.length;
        int limitX=field[0].length;
        for (int y=0;y<limitY;y++){
            String printline="";
            for (int x=0;x<limitX;x++){
                printline+=field[y][x];
            }
            System.out.println(printline);
        }
        System.out.println("\n");
    }


    public static int checkForUnopened(MineSweeper[][] field, MineSweeper mine){
        //inputs a mine, checks valid neighbors, outputs number of unopened neighbors
        ArrayList<MineSweeper>neigbors=validUnOpenedNeighbors(field, mine);
        int unopened=0;
        for (int i=0;i<neigbors.size();i++){
            if (!neigbors.get(i).isOpened){
                unopened++;
            }
        }
        return unopened;
    }
    public static int checkForOpened(MineSweeper[][] field, MineSweeper mine){
        //inputs a mine, checks valid neighbors, outputs number of unopened neighbors
        ArrayList<MineSweeper>neigbors=validOpenedNeighbors(field, mine);
        int opened=0;
        for (int i=0;i<neigbors.size();i++){
            if (neigbors.get(i).isOpened){
                opened++;
            }
        }
        return opened;
    }



    public static ArrayList<MineSweeper> validUnOpenedNeighbors(MineSweeper[][]field , MineSweeper Mine){
        //inputs a board and location
        //outputs a list of all valid neighbors
        ArrayList<MineSweeper> validNeighbors=new ArrayList<>();
        int limitX=field[0].length;
        int limitY=field.length;

        int x=Mine.x;
        int y=Mine.y;


        int newX;
        int newY;

        if (x+1>=0&&x+1<limitX){
            newX=x+1;
            newY=y;

            if (!field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
            if (y-1>=0 &&y-1<limitY){
                newX=x+1;
                newY=y-1;

                if (!field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
            if (y+1>=0 && y+1<limitY){
                newX=x+1;
                newY=y+1;

                if (!field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
        }
        if (x-1>=0&&x-1<limitX){
            newX=x-1;
            newY=y;

            if (!field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }

            if (y-1>=0 &&y-1<limitY){
                newX=x-1;
                newY=y-1;

                if (!field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }

            if (y+1>=0 && y+1<limitY){
                newX=x-1;
                newY=y+1;

                if (!field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
        }

        if (y-1>=0&&y-1<limitY) {
            newX = x;
            newY = y - 1;

            if (!field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
        }
        if (y+1>=0&&y+1<limitY){

            newY=y+1;
            newX=x;

            if (!field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
        }


        return validNeighbors;
    }

    public static ArrayList<MineSweeper> validOpenedNeighbors(MineSweeper[][]field , MineSweeper Mine){
        //inputs a board and location
        //outputs a list of all valid neighbors
        ArrayList<MineSweeper> validNeighbors=new ArrayList<>();
        int limitX=field[0].length;
        int limitY=field.length;

        int x=Mine.x;
        int y=Mine.y;

        int newX;
        int newY;

        if (x+1>=0&&x+1<limitX){
            newX=x+1;
            newY=y;

            if (field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
            if (y-1>=0 &&y-1<limitY){
                newX=x+1;
                newY=y-1;

                if (field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
            if (y+1>=0 && y+1<limitY){
                newX=x+1;
                newY=y+1;

                if (field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
        }
        if (x-1>=0&&x-1<limitX){
            newX=x-1;
            newY=y;

            if (field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }

            if (y-1>=0 &&y-1<limitY){
                newX=x-1;
                newY=y-1;

                if (field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }

            if (y+1>=0 && y+1<limitY){
                newX=x-1;
                newY=y+1;

                if (field[newY][newX].isOpened){
                    validNeighbors.add(field[newY][newX]);
                }
            }
        }

        if (y-1>=0&&y-1<limitY) {
            newX = x;
            newY = y - 1;

            if (field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
        }
        if (y+1>=0&&y+1<limitY){

            newY=y+1;
            newX=x;

            if (field[newY][newX].isOpened){
                validNeighbors.add(field[newY][newX]);
            }
        }


        return validNeighbors;
    }

    public String toString(){
        if (isOpened){
            if (isMine){
                return"B";
            }
            return String.valueOf(surroundingMines);
        }else {
            if (isFlagged){
                return "x";
            }else{
                return "?";
            }
        }
    }
    //Accounts for ALL information currently available in puzzle in order to solve
    public boolean constrainedView(MineSweeper[][]minefield){
        //takes a list of the opened-with mines to find
        //creates a list of neighbors- constrained unopened cells
        ArrayList<MineSweeper>finalCheck=new ArrayList<>();
        ArrayList<MineSweeper>OpenedFinal= new ArrayList<>();
        ArrayList<MineSweeper>UnOpenedFinal= new ArrayList<>();
        int total=0;
        //Iterates all possibilities for solution and compares to find similarities within solutions
        //total is the total bombs indicated from opened cells that still need at least one bomb
        //total is what we will compare to in order to find solution
        for (int y=0;y<minefield.length;y++){
            for (int x=0;x<minefield[0].length;x++){
                if (minefield[y][x].minesToFind>0&&minefield[y][x].isOpened){
                    System.out.println("OPENED");
                    OpenedFinal.add(minefield[y][x]);
                    System.out.println(minefield[y][x]);
                    ArrayList<MineSweeper>mineSweeperArrayList= validUnOpenedNeighbors(minefield,minefield[y][x]);
                    for (int i=0;i<mineSweeperArrayList.size();i++){
                        if (!finalCheck.contains(mineSweeperArrayList.get(i))&&!mineSweeperArrayList.get(i).isFlagged){
                            finalCheck.add(mineSweeperArrayList.get(i));
                        }}

                    total=total+minefield[y][x].minesToFind;
                }
                if (!minefield[y][x].isFlagged&&!minefield[y][x].isOpened){
                    //total cells left to flag/open
                    UnOpenedFinal.add(minefield[y][x]);
                }

            }}
        //list of opened mines with bombs needed
        //list of unopened+ unflagged mines in entire grid
        //list of unopened+unflagged mines adjacent to opened with bombs needed

        //test for all mines== unopened unflagged
        if (minesLeft[0]==UnOpenedFinal.size()){
            for (int i=0;i<UnOpenedFinal.size();i++){
                setIsFlagged(UnOpenedFinal.get(i),true);
            }
            System.out.println("mines equal size");
            return true;
        }
        boolean totalPuzzle=false;
        if (UnOpenedFinal.size()==finalCheck.size()){
            //solution has to include all mines should only check solutions with minesleft[0]
            totalPuzzle=true;
        }

        //for each unopened cell- assign an amount of opened cells surrounding it
        // for a correct solution total from above must be equal to sum of All ((cells surrounding bombs))
        //AND correct solution must satisfy all unopened adjacent cells

        for (MineSweeper mine:finalCheck){
            mine.checking=checkForOpened(minefield,mine);
        }

        System.out.println("minesLeft");
        System.out.println(minesLeft[0]);

        int maximum=Math.min(finalCheck.size(),minesLeft[0]);

        ArrayList<Integer> something= new ArrayList<>();
        int count=0;
        boolean noanswer=true;
        boolean noanswer1=true;

        if (noanswer){
            for (int x=0;x<finalCheck.size();x++){
                int subTotal=finalCheck.get(x).checking;
                if (total==subTotal) {
                    Boolean solution=true;
                    //matches total- need to check opened cells for match
                    for (int i=0;i<OpenedFinal.size();i++){
                        //takes each- gets list of unopened neighbors
                        //checks to see if answer cells are in list
                        //counts number of cells in the answer and compares to bombs needed
                        ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(i));
                        int tally=0;
                        if (checkingList.contains(finalCheck.get(x))){
                            tally++;
                        }
                        if (tally!=OpenedFinal.get(i).minesToFind){
                            //not a solution
                            solution=false;
                        }
                    }
                    if (solution){
                        if (!totalPuzzle||minesLeft[0]==1){
                            System.out.println("one");
                            noanswer1=false;
                            count++;
                            something.add(x);
                            if (count > 1&&minesLeft[0]==1&&count>10) {

                                return false;
                            }}
                    }}}}
        if (noanswer&&maximum>=2){
            for (int x=0;x<finalCheck.size();x++){
                for (int i=x+1;i<finalCheck.size();i++){
                    int subTotal=finalCheck.get(x).checking+finalCheck.get(i).checking;
                    if (total==subTotal){
                        Boolean solution=true;
                        //matches total- need to check opened cells for match
                        for (int z=0;z<OpenedFinal.size();z++){
                            //takes each- gets list of unopened neighbors
                            //checks to see if answer cells are in list
                            //counts number of cells in the answer and compares to bombs needed
                            System.out.println(OpenedFinal.get(z).x+","+OpenedFinal.get(z).y);
                            System.out.println("OpenedFinal");
                            ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(z));
                            int tally=0;
                            if (checkingList.contains(finalCheck.get(i))){
                                tally++;
                            }
                            if (checkingList.contains(finalCheck.get(x))){
                                tally++;
                            }
                            if (tally!=OpenedFinal.get(z).minesToFind){
                                //not a solution
                                solution=false;
                            }
                        }
                        if (solution){
                            if (!totalPuzzle||minesLeft[0]==2){
                                System.out.println("two");
                                noanswer=false;
                                count++;
                                something.add(x);
                                something.add(i);
                                if (count>1&&minesLeft[0]==2&&count>10) {
                                    //two possible solutions
                                    return false;
                                }}
                        }}}}}
        if (noanswer1&&maximum>=3){
            for (int x=0;x<finalCheck.size();x++){
                for (int i=x+1;i<finalCheck.size();i++){
                    for (int q=i+1;q<finalCheck.size();q++){
                        int subTotal=finalCheck.get(x).checking+finalCheck.get(i).checking+finalCheck.get(q).checking;
                        if (total==subTotal){
                            Boolean solution=true;
                            //matches total- need to check opened cells for match
                            for (int z=0;z<OpenedFinal.size();z++){
                                //takes each- gets list of unopened neighbors
                                //checks to see if answer cells are in list
                                //counts number of cells in the answer and compares to bombs needed
                                ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(z));
                                int tally=0;
                                if (checkingList.contains(finalCheck.get(i))){
                                    tally++;
                                }
                                if (checkingList.contains(finalCheck.get(q))){
                                    tally++;
                                }
                                if (checkingList.contains(finalCheck.get(x))){
                                    tally++;
                                }
                                if (tally!=OpenedFinal.get(z).minesToFind){
                                    //not a solution
                                    solution=false;
                                }
                            }
                            if (solution){
                                if (!totalPuzzle||minesLeft[0]==3){
                                    System.out.println("three");
                                    System.out.println(finalCheck.get(i).x+","+finalCheck.get(i).y);
                                    System.out.println(finalCheck.get(q).x+","+finalCheck.get(q).y);
                                    System.out.println(finalCheck.get(x).x+","+finalCheck.get(x).y);
                                    noanswer1=false;
                                    something.add(x);
                                    something.add(i);
                                    something.add(q);
                                    count++;
                                    if (count>1&& minesLeft[0]==3&&something.size()>100){
                                        //two possible solutions
                                        System.out.println("returning false");
                                        return false;
                                    }
                                }}}
                    }
                }}}
        if (noanswer&&maximum>=4){
            for (int x=0;x<finalCheck.size();x++){
                for (int i=x+1;i<finalCheck.size();i++){
                    for (int q=i+1;q<finalCheck.size();q++){
                        for (int z=q+1;z<finalCheck.size();z++){
                            int subTotal=finalCheck.get(x).checking+finalCheck.get(i).checking+finalCheck.get(q).checking+finalCheck.get(z).checking;
                            if (total==subTotal){
                                Boolean solution=true;
                                //matches total- need to check opened cells for match
                                for (int a=0;a<OpenedFinal.size();a++){
                                    //takes each- gets list of unopened neighbors
                                    //checks to see if answer cells are in list
                                    //counts number of cells in the answer and compares to bombs needed
                                    ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(a));
                                    int tally=0;
                                    if (checkingList.contains(finalCheck.get(i))){
                                        tally++;
                                    }
                                    if (checkingList.contains(finalCheck.get(q))){
                                        tally++;
                                    }
                                    if (checkingList.contains(finalCheck.get(x))){
                                        tally++;
                                    }
                                    if (checkingList.contains(finalCheck.get(z))){
                                        tally++;
                                    }
                                    if (tally!=OpenedFinal.get(a).minesToFind){
                                        //not a solution
                                        solution=false;
                                    }
                                }
                                if (solution){
                                    if (!totalPuzzle||minesLeft[0]==4){
                                        System.out.println("four");
                                        noanswer=false;
                                        something.add(x);
                                        something.add(i);
                                        something.add(q);
                                        something.add(z);
                                        count++;
                                        if (count>1&& minesLeft[0]==4&&count>10){
                                            //two possible solutions
                                            return false;
                                        }
                                    }}
                            }}}
                }}}
        if (noanswer1&&maximum>=5){
            for (int x=0;x<finalCheck.size();x++){
                for (int i=x+1;i<finalCheck.size();i++){
                    for (int q=i+1;q<finalCheck.size();q++){
                        for (int z=q+1;z<finalCheck.size();z++){
                            for (int p=z+1;p<finalCheck.size();p++){
                                int subTotal=finalCheck.get(x).checking+finalCheck.get(i).checking+finalCheck.get(q).checking+finalCheck.get(z).checking+finalCheck.get(p).checking;
                                if (total==subTotal){
                                    Boolean solution=true;
                                    //matches total- need to check opened cells for match
                                    for (int a=0;a<OpenedFinal.size();a++){
                                        //takes each- gets list of unopened neighbors
                                        //checks to see if answer cells are in list
                                        //counts number of cells in the answer and compares to bombs needed
                                        ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(a));
                                        int tally=0;
                                        if (checkingList.contains(finalCheck.get(i))){
                                            tally++;
                                        }
                                        if (checkingList.contains(finalCheck.get(p))){
                                            tally++;
                                        }
                                        if (checkingList.contains(finalCheck.get(q))){
                                            tally++;
                                        }
                                        if (checkingList.contains(finalCheck.get(x))){
                                            tally++;
                                        }
                                        if (checkingList.contains(finalCheck.get(z))){
                                            tally++;
                                        }
                                        if (tally!=OpenedFinal.get(a).minesToFind){
                                            //not a solution
                                            solution=false;
                                        }
                                    }
                                    if (solution){
                                        if (!totalPuzzle||minesLeft[0]==5){
                                            System.out.println("five");
                                            noanswer1=false;
                                            something.add(x);
                                            something.add(i);
                                            something.add(q);
                                            something.add(z);
                                            something.add(p);
                                            count++;
                                            if (count>1&&minesLeft[0]==5&&count>10){
                                                //two possible solutions
                                                return false;
                                            }
                                        }}
                                }}}}
                }}}
        if (noanswer1&&maximum>=6){
            for (int x=0;x<finalCheck.size();x++){
                for (int i=x+1;i<finalCheck.size();i++){
                    for (int q=i+1;q<finalCheck.size();q++){
                        for (int z=q+1;z<finalCheck.size();z++){
                            for (int p=z+1;p<finalCheck.size();p++){
                                for (int r=p+1;r<finalCheck.size();r++){
                                    int subTotal=finalCheck.get(x).checking+finalCheck.get(i).checking+finalCheck.get(q).checking+finalCheck.get(z).checking+finalCheck.get(p).checking+finalCheck.get(r).checking;
                                    if (total==subTotal){
                                        Boolean solution=true;
                                        //matches total- need to check opened cells for match
                                        for (int a=0;a<OpenedFinal.size();a++){
                                            //takes each- gets list of unopened neighbors
                                            //checks to see if answer cells are in list
                                            //counts number of cells in the answer and compares to bombs needed
                                            ArrayList<MineSweeper>checkingList=validUnOpenedNeighbors(minefield,OpenedFinal.get(a));
                                            int tally=0;
                                            if (checkingList.contains(finalCheck.get(i))){
                                                tally++;
                                            }
                                            if (checkingList.contains(finalCheck.get(p))){
                                                tally++;
                                            }
                                            if (checkingList.contains(finalCheck.get(r))){
                                                tally++;
                                            }
                                            if (checkingList.contains(finalCheck.get(q))){
                                                tally++;
                                            }
                                            if (checkingList.contains(finalCheck.get(x))){
                                                tally++;
                                            }
                                            if (checkingList.contains(finalCheck.get(z))){
                                                tally++;
                                            }
                                            if (tally!=OpenedFinal.get(a).minesToFind){
                                                //not a solution
                                                solution=false;
                                            }
                                        }
                                        if (solution){
                                            if (!totalPuzzle||minesLeft[0]==6){
                                                System.out.println("six");

                                                noanswer1=false;
                                                something.add(x);
                                                something.add(i);
                                                something.add(q);
                                                something.add(z);
                                                something.add(p);
                                                something.add(r);
                                                count++;
                                                if (count>1&&minesLeft[0]==6&& count>10){
                                                    //two possible solutions
                                                    return false;
                                                }
                                            }}
                                    }}}}}
                }}}

        if (something.size()>0&& count==1){
            System.out.println(finalCheck.size());
            for (int i=0;i<finalCheck.size();i++){
                System.out.println(finalCheck.get(i).x+","+finalCheck.get(i).y);
            }
            System.out.println(something.size());
            System.out.println("Answer Found");
            System.out.println(total);
            for (int i=0;i<something.size();i++){
                System.out.println(finalCheck.get(something.get(i)).x+","+finalCheck.get(something.get(i)).y);
                System.out.println(finalCheck.get(something.get(i)).checking);
                setIsFlagged(finalCheck.get(something.get(i)),true);
                printMap(minefield);

            }
            return true;
        }else if (something.size()>0&& count>=2){
            System.out.println("Inside this");
            for (int i=0;i<finalCheck.size();i++){
                int current=i;
                int counting=0;
                for (int q=0;q<something.size();q++){
                    System.out.println(finalCheck.get(something.get(q)).x+","+finalCheck.get(something.get(q)).y);
                    if (something.get(q)==current){
                        counting++;
                    }
                }
                if (counting==count){
                    //item has occured in every solution
                    System.out.println("common index");
                    setIsFlagged(finalCheck.get(current),true);
                    return true;
                }else if (counting==0){
                    System.out.println("index not in common");
                    System.out.println(finalCheck.get(i).x+","+finalCheck.get(i).y);
                    //index doesn't occur in any of the solutions
                    finalCheck.get(i).setNumMines(finalCheck.get(i),Game.open(finalCheck.get(i).y,finalCheck.get(i).x));
                    finalCheck.get(i).setFlagsSurrounding(finalCheck.get(i),checkForFlags(minefield,finalCheck.get(i)));
                    setToOpen(minefield,finalCheck.get(i));



                    printMap(minefield);
                    return true;
                }

            }
        }
        return false;

    }


    public static void main (String [] args){
         //String field= "? ? ? ? ? ?\n? ? ? ? ? ?\n? ? ? 0 ? ?\n? ? ? ? ? ?\n? ? ? ? ? ?\n0 0 0 ? ? ?";
         String field= "? ? 0 ? ? ? 0 0 ? ? ? 0 0 0 0 ? ? ? 0\n? ? 0 ? ? ? 0 0 ? ? ? 0 0 0 0 ? ? ? ?\n? ? 0 ? ? ? ? ? ? ? ? 0 0 0 0 ? ? ? ?\n0 ? ? ? ? ? ? ? ? ? ? 0 0 0 0 0 ? ? ?\n0 ? ? ? ? ? ? ? ? ? 0 0 0 0 0 0 0 0 0\n0 ? ? ? 0 0 0 ? ? ? 0 0 0 0 0 0 0 0 0\n0 0 0 0 0 0 0 ? ? ? ? ? ? ? 0 0 0 0 0\n0 0 0 0 0 0 0 0 0 0 ? ? ? ? 0 0 0 0 0\n0 0 ? ? ? 0 ? ? ? 0 ? ? ? ? 0 0 0 0 0\n0 0 ? ? ? ? ? ? ? 0 0 0 0 0 0 ? ? ? 0\n0 0 ? ? ? ? ? ? ? ? ? 0 0 0 0 ? ? ? 0\n0 0 0 0 ? ? ? ? ? ? ? 0 0 0 0 ? ? ? 0\n0 0 0 0 0 ? ? ? ? ? ? 0 0 0 0 0 ? ? ?\n0 0 ? ? ? ? ? ? 0 0 0 0 0 0 0 0 ? ? ?\n0 0 ? ? ? ? ? ? ? 0 0 0 0 0 0 0 ? ? ?\n0 0 ? ? ? ? ? ? ? ? 0 0 0 0 0 0 0 ? ?\n0 0 0 0 0 0 ? ? ? ? 0 0 0 ? ? ? 0 ? ?\n0 0 0 ? ? ? ? ? ? ? 0 0 0 ? ? ? ? ? ?\n0 0 0 ? ? ? ? ? 0 0 0 ? ? ? ? ? ? ? ?\n0 0 0 ? ? ? ? ? 0 0 0 ? ? ? 0 ? ? ? ?\n0 0 0 0 ? ? ? ? ? ? ? ? ? ? 0 ? ? ? ?\n0 0 0 0 ? ? ? ? ? ? ? ? ? ? 0 ? ? ? ?\n0 0 0 0 ? ? ? ? ? ? ? ? ? ? 0 ? ? ? ?";
        //String field ="1 1 1 1\n? ? ? ?";
        MineSweeper soemthing= new MineSweeper(field, 43);
    soemthing.solve();

    }

}
