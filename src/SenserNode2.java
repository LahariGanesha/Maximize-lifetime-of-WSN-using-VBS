import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



public class SenserNode2 extends Frame implements ActionListener {

    // Object fields
   
    private TextField infoText;
    private Button sencedinfoButton;
    private Button  exitButton;
    private Socket requestSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private List sourceNode;
    private Label tl1,tl2,tl3;

    private String intermediate_Node6_ip ;
    
    public void SetStatus(String s)
    {
    	this.setTitle(s);
    }
    
    
    class PortListener implements Runnable {

    	ServerSocket server;
    	Socket connection;
    	BufferedReader br = null;
    	Boolean flag;
    	public String strLine;
    	int port;


    	public PortListener(int port) {
    		this.port = port;
    		strLine = "waiting for client";
    		flag = false; //initaly Active
    	}

    	public void run() {
    		if(port==2112)
    		{
    		try {
    			server = new ServerSocket(2112);

    			while (true) {
    				connection = server.accept();
    				
    				System.out.println("Connection received from " + connection.getInetAddress().getHostName());    				   				
    				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
    				   				
    				strLine = (String)in.readObject();
    				
    				System.out.println(strLine);

    				flag = !flag;
    				
    				if (flag){
    					SetStatus("Node 2 - Active");
    				} else {
       					SetStatus("Node 2 - Sleeping");
    				}

    				sencedinfoButton.setEnabled(flag);
    				in.close();
    				connection.close();   				

    			}
    		} 
    		
			catch(ClassNotFoundException classnot){
				System.err.println("Data received in unknown format");
			}
    		catch(IOException ioException){
    			ioException.printStackTrace();
    		} finally {
    		}

    		}
    	}
    }

 
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
        
        if (action.equals("Exit")) {
            dispose();
            System.out.println("Exiting.");
            System.exit(0);
        } else {
            System.out.println(action);
// add to send-info
    		try{
    			//1. creating a socket to connect to the server
    			String message;
    			requestSocket = new Socket(intermediate_Node6_ip, 3112);
    			System.out.println("Connected to localhost in port 3112");
    			//2. get Input and Output streams
    			out = new ObjectOutputStream(requestSocket.getOutputStream());
    			out.flush();
    			message = infoText.getText() + "(Path: Node-2,";
    			
    			out.writeObject(message);
    			out.flush();
     		}
    		catch(UnknownHostException unknownHost){
    			System.err.println("You are trying to connect to an unknown host!");
    		}
    		catch(IOException ioException){
    			ioException.printStackTrace();
    		}
    		finally{
    			//4: Closing connection
    			try{
//    				in.close();
    				out.close();
    				requestSocket.close();
    			}
    			catch(IOException ioException){
    				ioException.printStackTrace();
    			}
    		}
        }

    }
    
    public SenserNode2() {

        super("Senser Node 2");
        setSize(400, 200);

        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );

        intermediate_Node6_ip = "localhost";
        
        // Toolbar Panel
        Panel toolbarPanel = new Panel();
        toolbarPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        toolbarPanel.setBackground(Color.PINK);
//        toolbarPanel.setLayout(new () );
                
        tl1 = new Label("Information");
        tl2 = new Label("WSN Node Id");
        tl3 = new Label("");
        
        infoText = new TextField(30);
        sourceNode = new List();
        
        toolbarPanel.add(tl1);
        toolbarPanel.add(infoText);
    //    toolbarPanel.add(tl2,2);
     //   toolbarPanel.add(sourceNode);
        
        sencedinfoButton = new Button("Send Sensed Info To Sink");
        sencedinfoButton.addActionListener(this);
        toolbarPanel.add(sencedinfoButton);

        add(toolbarPanel, BorderLayout.CENTER);

        // Bottom Panel
        Panel bottomPanel = new Panel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        exitButton = new Button("Exit");
        exitButton.addActionListener(this);
        bottomPanel.add(exitButton);
        bottomPanel.setBackground(Color.BLUE);
        add(bottomPanel, BorderLayout.AFTER_LAST_LINE);
        
		Thread t1 = new Thread (new PortListener(2112));
		t1.setName("Togel node-2");
		t1.start();
    }

    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {

    	SenserNode2 mainFrame = new SenserNode2();
        mainFrame.setVisible(true);
    }

}

