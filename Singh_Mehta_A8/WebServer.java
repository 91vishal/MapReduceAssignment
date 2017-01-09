

import java.io.IOException;

import java.util.ArrayList;

public class WebServer {
	public static void main(String[] args) throws IOException {
//		final int SERVER_PORT = 40000;

		TextSocket.Server svr = new TextSocket.Server(4002);
		ArrayList<ArrayList<FileData>> al = new ArrayList<ArrayList<FileData>>();
	    ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
	    

	    TextSocket conn;
	    System.out.println("in server");
        while (null != (conn = svr.accept())) {
        	System.out.println("Accepted");
            ServerThread tt = new ServerThread(conn, al,clients);
            clients.add(tt);
            tt.start();
        }
        System.out.println("Done in server");
    }
}

class ServerThread extends Thread {
    final TextSocket conn;
    ArrayList<ArrayList<FileData>> al;
    ArrayList<ServerThread> clients;
    int clientCount;

    public ServerThread(TextSocket conn, ArrayList<ArrayList<FileData>> al, ArrayList<ServerThread> clients) {
        this.conn = conn;
        this.al = al;
       
        this.clients = clients;
    }

    @Override 
    public void run() {
      try {
            while (true) {
                acceptData(conn.getOb());
                broadcastData();
            }
          } catch (Exception e) {
                e.printStackTrace();
    		}
    }

        private void acceptData(ArrayList<FileData> s) throws Exception {
        System.out.println("acceptData called by " + Thread.currentThread().getName());
        //add loop to read all objects
        //
        System.out.println("---------------678---> "+s);
        al.add(s);
        System.out.println("In server" +al);
        
    }

    private void broadcastData() throws Exception {
        System.out.println("broadcast called by " + Thread.currentThread().getName());
        System.out.println("al is : \n" + al);

        for (ServerThread clnt : clients) {
        	for(int i=0;i<al.size();i++){
        		clnt.conn.putOb(al.get(i));
            }
        }
    }
}
