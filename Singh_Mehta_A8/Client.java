import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client{
	
public static final int SERVER_PORT = 40000;
public static final String SERVER_IP = "localhost";
private Socket s1;
private ObjectOutputStream out;
private ObjectInputStream in;
private ArrayList<Object> al;

public Client() {
//    initComponents();
//    setVisible(true);
    initNet();
}

private void initNet() {
    try {
        s1 = new Socket(SERVER_IP, SERVER_PORT);
        out = new ObjectOutputStream(s1.getOutputStream());            
        in = new ObjectInputStream(s1.getInputStream());
        System.out.println("connected to server");

        new ReadData();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

class ReadData extends Thread {

    public ReadData() {
        start();
    }

    public void run() {
        System.out.println("client thread started");
        try {
            while (true) {
            	al.add(in.readObject());
                //al = (ArrayList<FileData>) in.readObject();
                System.out.println("client read completed, al is "+al);
                out.writeObject(al);
                                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}
