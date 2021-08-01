import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


/*Created by Mor Ofir
 * just to be clear if the numbers are consecutive, and they begin with 1, then:
https://en.wikipedia.org/wiki/1_%2B_2_%2B_3_%2B_4_%2B_%E2%8B%AF 
would be enough to sum up the all array with the formula: n(n+1)/2 .
*/


public class Server {
    static int hitCount = 0;
    static final int PORT = 1342;
//	private static ArrayList<ClientHandler> clients = new ArrayList<>();
	private static ExecutorService pool = Executors.newWorkStealingPool((Runtime.getRuntime().availableProcessors())); //pool as much as my processors
	public static int[] mainArray;
	
	static void mainMethod(int x, int y, int z, ObjectOutputStream outStream,int id) throws IOException {
	    List<int[]> intArrays =new ArrayList<>(); 

		
		int chunk = z/x; //chunks = lastNumber/numComputers
		for(int i=0;i<mainArray.length;i+=chunk){
		    int[] newArr = Arrays.copyOfRange(mainArray, i, Math.min(mainArray.length,i+chunk));
		    
		    intArrays.add(newArr);/// list = [1...19], [19....39] , [] ,[] ,...
		}   
		
		if(id<=x)
			System.out.println("Sending chunk to the client "+ id);
			
	    
	    try {
	    int[] anIntArray = intArrays.get(id-1); //0 for start
	   
	       
	    //iterate the retrieved array an print the individual elements
	    for (int aNumber : anIntArray ) { 
	        System.out.print(+aNumber+" ");
	    }
	   
	        
	        System.out.println(); 
		    outStream.writeObject(intArrays.get(id-1));
		    //server write to specific client every time(id is changing in main)
	    
	    }catch(Exception e) {
	    	 System.out.println("ERROR: only "+x+" clients!");
	    }		
	  }
	

    public static void main(String[] args) throws Exception {
    	int processorsNumber = Runtime.getRuntime().availableProcessors(); //number of available processors
		System.out.println("processors: "+ processorsNumber);

		
		int id = 1;
//		ServerSocket listener = new ServerSocket(PORT);
		
		
		Scanner scn = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("enter Z (last number of the series):");
	    int z = scn.nextInt();  // Read user input
	    
	    Scanner scn2 = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("enter x (number of clients):");
	    int x = scn2.nextInt();  // Read user input
	    long sum = 0;
	    

	    
		mainArray =  IntStream.range(1, z+1).toArray();// 1 to z

		InputStream in = null;
		ObjectInputStream objIn = null; // input from client
		OutputStream out = null;
	

        System.out.println("[SERVER] ServerSocket awaiting connections...");
        
        ServerSocket listener = new ServerSocket(1342, 10000);
        while (true) {
            Socket client = listener.accept();
            ObjectOutputStream outStream = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(client.getInputStream());
			
			
			/*can change values (int x, int y, int z)
			 * x-number of clients
			 * y- number of processors
			 * z- last number is the series Σ(n) (start with 1)*/
			mainMethod(x,processorsNumber,z,outStream,id); 
            
	        id++; //every thread client id changes
            new Thread(new ServerSlave(client,id)).start();
//            System.out.println("Size is :" + hitCount);

	        DataInputStream dis = new DataInputStream(client.getInputStream()); //getting the sum of the chunks array
	        long sumFromClient = dis.readLong();
	        

	        sum += sumFromClient; //adding to final sum
	        System.out.println("Sum From Client "+ (id-1) +": "+sumFromClient);
	        
	        if(id<x)	
		        System.out.println("Array sum:(so far) "+(sum-z));
			else
				try {
					if(id>x) {
						System.out.println("\nFinal Array Sum: "+sum);
						System.out.println("connection terminated...");
						 break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }

    }
}

class ServerSlave implements Runnable {
    Socket client;
    int id;

    public ServerSlave(Socket socket,int id) {
        client = socket;
        this.id = id;

    }


	public void run() {

    	   InputStream in = null;
    	   OutputStream out = null;
           ObjectInputStream objIn = null; //input from client (chunks array from server to client)
           ObjectOutputStream objOut= null; //out from client (sum of chunks array)
           
           try {
               in = client.getInputStream();
               out = client.getOutputStream();
               objIn = new ObjectInputStream(in);
    	       objOut = new ObjectOutputStream(out);
           } catch (IOException e) {
               return;
           }
           DataInputStream dis = null;
    	try {
    		dis = new DataInputStream(client.getInputStream());
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} //getting the sum of the chunks array
           try {
    		long sumFromClient = dis.readLong(); //get the sum from client
//    		sum += sumFromClient;
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
}