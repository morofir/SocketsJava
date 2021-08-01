import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
//    static int excepCount=0;
    public static void main(String[] args) throws Exception {
 	
	    Scanner scn2 = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("enter x (number of clients):");
	    int x = scn2.nextInt();  // Read user input
	    int cores = Runtime.getRuntime().availableProcessors();
 	    System.out.println("number of cores: "+ cores);

 	   
 	    
        for (int i = 0; i < x; i++) {
            new Thread (new Worker("" + i)).start();
           
        }
        Thread.sleep(10000);
        
//        System.out.println( Client.excepCount);
    }
}


class Worker implements Runnable {
    String clientName;

    public Worker(String name) {
        clientName = name;
    }

    public void run() {
        System.out.println("Process started for : " + clientName);
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 1342);
            
            long sum = 0;
            
            InputStream in = null;
            ObjectInputStream objIn = null; //input from client
            OutputStream out = null;
            ObjectOutputStream objOut= null; //out from client
            in = socket.getInputStream();
            out = socket.getOutputStream();
            objIn = new ObjectInputStream(in);
      	  objOut = new ObjectOutputStream(out);
      	  
      	  
      	do
        {
            try {
           	 int[] result = (int[])objIn.readObject();  //get the int[] object from server
           	 
           	
      	        for (int i = 0; i < result.length; i++) {
      	        	sum += result[i];
      	        }
      	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      	     	dos.writeLong(sum);  //send the sum to server
      	        System.out.println("sum: "+sum);
               
               out.flush();
          
           } catch (IOException e) {
               e.printStackTrace();
               return;
           } catch (ClassNotFoundException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}

        }while (true); 
      	  

        } catch (IOException e) {
//            Client.excepCount++;
            e.printStackTrace();
        }finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}