
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.io.LineIterator;

// Author: Nat Tuck
public class TextSocket implements Iterable<String> {
    final Socket sock;
    final BufferedReader rdr=null;
    final BufferedWriter wtr=null;;
     ObjectInputStream objectInputStream;
     ObjectOutputStream objectOutputStream;
   
    public TextSocket(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    public TextSocket(Socket ss) { 
        sock = ss;
        System.out.println(ss.getPort()+" "+ss+" "+ss.getInetAddress());
        try {
           // rdr = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
            //wtr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
            InputStream stream=sock.getInputStream();
            System.out.println("done completing first "+stream);
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(new BufferedInputStream(stream));
            System.out.println("done completing "+objectInputStream);
            
            
        }
        catch (Exception ee) {
        	ee.printStackTrace();
           System.out.println(ee);
        }
    }

    public void closeToo() throws IOException {
        sock.close();
    }

    public String getln() throws IOException {
        String line = rdr.readLine();
        if (line == null) {
            return null;
        }
        else {
            return line.trim();
        }
    }

     public ArrayList<FileData> getOb() {
    	 
    	 // have to verify
    	 Object obj=null;
    	 ArrayList<FileData> ob=null;;
    	 try{
    	 while ((obj = objectInputStream.readObject()) != null) {
    		 ob = (ArrayList<FileData>) obj;
    		 System.out.println(" is this object "+ob);
    	 }
    	 }catch(Exception e){
    		 System.out.println(e.getMessage());
    	 }
    	
        if (ob == null) {
            return null;
        }
        else {
        	 
            return ob;
        }
    	 
    }

    public Iterator<String> iterator() {
        return new LineIterator(rdr); 
    }

    public void putln(String line) throws IOException {
        line += "\r\n";

        wtr.write(line, 0, line.length());
        wtr.flush();
    }

     public void putOb(ArrayList<FileData> al) throws IOException {
        //int size = al.size();
    	 
    	 System.out.println("In putOB");
    
        objectOutputStream.writeObject(al);
        objectOutputStream.flush();
       
        objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));
        objectOutputStream.flush();
        
       
        
    }

    public static class Server {
        final ServerSocket server;

        public Server(int port) throws IOException {
            server = new ServerSocket(port);
        }

        public TextSocket accept() throws IOException {
            Socket sock = server.accept();
            if (sock == null) {
                return null;
            }
            else {
                return new TextSocket(sock);
            }
        }
    }
}
