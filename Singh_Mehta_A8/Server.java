import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	public static final int SERVER_PORT = 40000;
	private ServerSocket ss;
	ArrayList<Object> al;
	ArrayList<ClientHandler> clients;

	public Server() {
		//    initComponents();
		//    setVisible(true);
		al = new ArrayList<Object>();
		clients = new ArrayList<>();
		initNet();
	}

	private void initNet() {
		Socket ds = null;
		try {
			ss = new ServerSocket(SERVER_PORT, 1);
			while (true) {

				ds = ss.accept();

				clients.add(new ClientHandler(ds));
			}
		} catch (Exception e) {

			System.out.println("shutting down server......");
		}
	}

	class ClientHandler extends Thread {

		private Socket ds;
		private ObjectOutputStream out;
		private ObjectInputStream in;

		public ClientHandler(Socket ds) throws Exception {
			this.ds = ds;
			out = new ObjectOutputStream(ds.getOutputStream());
			in = new ObjectInputStream(ds.getInputStream());
			start();
		}

		public ObjectOutputStream getOut() {
			return out;
		}

		public void run() {
			try {
				while (true) {
					acceptData(in);
					broadcastData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("Finally called. socket closed");
				if (ds != null) {
					try {
						ds.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void acceptData(ObjectInputStream in) throws Exception {
		System.out.println("acceptData called by " + Thread.currentThread().getName());
		//add loop to read all objects
		Object s = in.readObject();
		al.add(s);
		//jta.setText(al.toString());
	}

	private void broadcastData() throws Exception {
		System.out.println("broadcast called by " + Thread.currentThread().getName());
		System.out.println("al is : \n" + al);

		for (ClientHandler clnt : clients) {
			clnt.getOut().writeObject(al);
			clnt.getOut().flush();
		}
	}
}