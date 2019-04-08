/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erastothenes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giacomo Pellicci
 */
public class Erastothenes {
    
    static long startTime = System.currentTimeMillis();
    static long computeTime;
    static final int NUMTASKS=50;
    static final int POOLSIZE=2;
    static final int INITVALUE=2; //1st known prime number
    private static AtomicInteger c = new AtomicInteger(NUMTASKS);
    static final int n = 1000000000;
    static boolean[] set = new boolean[n+1];
    private ExecutorService myExecutor;
    private static IncClassBusy myTask;
    
    public Erastothenes(ExecutorService exc) {
        myExecutor = exc;     
        for(int i=0; i<=n; i++)
            set[i]=true;    //all prime at the beginning
        
        // all the tasks are created
        myTask = new IncClassBusy(INITVALUE);  
    }
    
    private static boolean isPrime(int num){
            if(num <= 1)
                return false;
            else if(num<=3)
                return true;
            else if(num%2==0 || num%3==0)
                return false;
            
            int i=5;
            while(i*i <= num){
                if((num%i == 0) || (num%(i+2) == 0))
                    return false;
                i = i+6;
            }
            return true;
        }
    
    public static void main(String[] arg){
        Erastothenes myInst=
            new Erastothenes(Executors.newFixedThreadPool(POOLSIZE));
        
        myInst.myExecutor.execute(myTask);
        
        
        //System.out.println(isPrime(119297));
    }



    class IncClassBusy implements Runnable{
        
        private final int myPrime;            //both represent the value and the index.

        private IncClassBusy(int prime){   
            myPrime = prime;
        }
        
        

        @Override
        public void run() {
            //start message
            /*
            int id = NUMTASKS-c.getAndDecrement();
            String msg="";
            msg+="Task #"+Integer.toString(id)+": STARTED! MyPrime is: " + myPrime;
            System.out.println(msg);
            */
            
            
            int j = -1;
            for(int i=myPrime+1; (i<myPrime*2)&&(i<n); i++){
                if(set[i]=false || isPrime(i)){
                    j = i;          //next Prime discovered -> run a task which deletes it's multiple
                    break;
                }  
                else{
                    set[i] = false;
                }
            }
            
            //System.out.println("Task#"+id+" my j: "+j);
            if(j!=-1){
                if(j*j < n){
                    IncClassBusy task = new IncClassBusy(j);
                    //System.out.println("\t Task#"+id+"  myPrime:"+myPrime+" NEXT is:"+j);
                    myExecutor.execute(task);   
                }
                else{
                    set[j] = true;              //useless
                    //exec here in the last prime number relevant to be iterated. (this executes over myPrime, not over j which does not require any iteration
                    //as its square is larger than n
                    if(set[myPrime]==true)
                    for(int k=myPrime*myPrime; k<=n; k+=myPrime){
                        if(!(myPrime!=2 && k%2==0))
                            set[k] = false;
                    }
                    //System.out.println("\t \t Task #"+Integer.toString(id)+" called awaitTermination");
                    myExecutor.shutdown();
                    try {
                        myExecutor.awaitTermination(1, TimeUnit.SECONDS);
                    } catch (InterruptedException ex) {
                        System.out.println("awaitT   EXCEPTION!!!");
                    }
                    computeTime = System.currentTimeMillis() - startTime;
                    startTime = System.currentTimeMillis();
                    /*
                    int print=0;
                    for(int i=2; i<=n; i++){
                        if(set[i]==true){
                            print++;
                            System.out.print(i+"\t");
                            if(print%8==0)
                                System.out.print("\n");
                        }
                        if(i==n)
                            System.out.print("\n");
                            
                    }
                    
                    System.out.println("Moreover...");
                    startTime = System.currentTimeMillis();
                    int c=0;
                    for(int i=2; i<=n; i++)
                        if(set[i]==true && isPrime(i)==false){
                            System.out.println("Mistakes were made.  ->  "+i);
                            c++;
                        }
                    
                    System.out.println("\tYou have done "+c+" errors!!!");
                    
                    System.out.println("\n\tChecked in : " + ( System.currentTimeMillis() - startTime )/1000 +"s, " + ( System.currentTimeMillis() - startTime )%1000 +" ms." );

                    */
                    System.out.println("\nComputed in : " + ( computeTime )/1000 +"s, " + ( computeTime )%1000 +" ms." );
                    
                    
                    
                    
                    /*
                    String sTime = Long.toString(computeTime);

                    try {
                        PrintWriter output = new PrintWriter(new FileOutputStream(
                                new File("src/erastothenes/filename.txt"), 
                                true));
                        output.append(sTime+'\n');
                        output.close();
                    } catch (FileNotFoundException ex) {
                        System.out.println('k');
                    }
                    */
                    
                    return;
                }
                
                
            }
            else{
            }
            
            //System.out.println("\t \t Task #"+Integer.toString(id)+": OUT OF THE LOOP");
            //SIEVE OF ERASTOTHENES ITSELF
            
            if(set[myPrime]==true)
                for(int k=myPrime*myPrime; k<=n; k+=myPrime){
                    if(!(myPrime!=2 && k%2==0))      //if it's not 2 and its a multiple of 2 do not update set[k], the task(2) will do it
                                                       //tested with 3 too but it's too hard to execute and do not reduce times
                        set[k] = false;
                }
            
            
            
            
            //end message
            /*
            msg="";
            msg+="Task #"+Integer.toString(id)+": '"+myPrime+"' DONE!";
            System.out.println(msg);
            */
            
            return;
        }
    }
}
