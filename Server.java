import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quickconnect.json.JSONException;
import org.quickconnect.json.JSONOutputStream;
import org.quickconnect.json.JSONUtilities;

public class Server {

	public static void main(String[] args) {
		connectionStatus();
	}

	public static void connectionStatus(){
		Thread t = new Thread(new Runnable() {           
			public void run() {

				try	{
					System.out.println();
					System.out.println("Starting Legacy Books Server...");
					System.out.println();

					ServerSocket serverSock = new ServerSocket(4444);

					System.out.println("---Listening for Incoming Connections---");
					System.out.println();

					while(true)	{
						Socket sock = serverSock.accept();

						System.out.println("\tConnection Requested!");
						System.out.println("\t\tClient Request From: " + sock.getInetAddress());
						System.out.println("\tGathering Data From Database");

						ArrayList<Book> books = refresh();

						System.out.println();
						System.out.println("\tSending Books To Client At:" + sock.getInetAddress());
						System.out.println("\tData Sent:" + JSONUtilities.stringify(books));

						JSONOutputStream jsonOut = new JSONOutputStream(sock.getOutputStream());
						jsonOut.writeObject(books);

						System.out.println("\tSuccess!");
						System.out.println();
						System.out.println("---Listening for Incoming Connections---");
						System.out.println();

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		});
		t.start();
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Book> refresh()	{
		List<Book> books;

		Session session = HibernateUtilSingleton.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		books = session.createSQLQuery("select book,author,isbn,location from books order by book").list();
		transaction.commit();
		
		return new ArrayList<Book>(books);
	}
}
