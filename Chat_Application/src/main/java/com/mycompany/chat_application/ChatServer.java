package com.mycompany.chat_application;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This class implements a chat server that allows multiple clients to connect
 * and chat with each other. The server listens for incoming client connections
 * on a specified port and creates a new thread to handle each client
 * connection. The client threads communicate with the server using a simple
 * protocol, where the client first sends its username, followed by its
 * messages. The server broadcasts the client's messages to all other connected
 * clients.
 *
 * @author Jose Omar
 * @version 1.0
 * @since April 20, 2023
 */
public class ChatServer {

    /**
     * Variable to hold the Socket for the Server
     */
    protected ServerSocket serverSocket;

    /**
     * holds all the clients with their username and Socket as key value pair
     */
    protected HashMap<String, Socket> clients;

    /**
     * Constructs a new ChatServer instance that listens for incoming client
     * connections on the specified port.
     *
     * @param port the port number to listen on
     */
    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new HashMap<>();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.out.println("Error starting server on port " + port);
        }
    }

    /**
     * Starts the server and listens for incoming client connections. For each
     * new client connection, creates a new thread to handle the connection.
     *
     */
    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            } catch (IOException e) {
                System.out.println("\nError accepting client connection...");
            }
        }
    }

    /**
     *
     * The ClientHandler class implements the Runnable interface and is
     * responsible for handling
     *
     * client connections on the server side. For each new client connection, a
     * new thread is created
     *
     * with an instance of this class to handle the connection.
     *
     */
    protected class ClientHandler implements Runnable {

        /**
         * Client Socket
         */
        protected Socket clientSocket;

        /**
         * username of the Client
         */
        protected String username;

        /**
         * Constructs a new ClientHandler instance for the specified client
         * socket.
         *
         * @param clientSocket the socket for the client connection
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Listens for incoming messages from the client and broadcasts them to
         * all other connected clients. If the client sends a "quit" message,
         * the connection is closed and the client is removed from the hashmap
         * of clients. If there is an error handling the connection, an error
         * message is printed to the console.
         *
         */
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("\nWelcome to the chat !\n");

                // ask for username
                out.println("Enter your username :");
                username = in.readLine();
                System.out.println("\t" + username + " connected to Chat...");

                // add client to hashmap
                clients.put(username, clientSocket);
                broadcast(username + " joined the chat...");

                // listen for client messages
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equalsIgnoreCase("quit")) {
                        break;
                    }
                    broadcast(username + ": " + inputLine);
                }

                // remove client from hashmap and close socket
                clients.remove(username);
                clientSocket.close();
                System.out.println("\n" + username + " got disconnected from Chat...");
                broadcast(username + " left the chat...");

            } catch (SocketException e) {
                System.out.println("\nClient connection closed...");
            } catch (IOException e) {
                System.out.println("\nError handling client connection...");
            }
        }
    }

    /**
     *
     * Sends a message to all connected clients. This method sends a message to
     * all the clients connected to the server. It iterates over the HashMap of
     * clients and sends the message to each client using their respective
     * PrintWriter objects. If there is any error while sending the message to a
     * particular client, an error message is printed to the console.
     *
     * @param message the message to broadcast
     */
    protected void broadcast(String message) {
        for (Socket clientSocket : clients.values()) {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                System.out.println("\nError sending message to client...");
            }
        }
    }

    /**
     *
     * The main method that starts the chat server. It prompts the user to enter
     * the port number on which the server should listen for incoming client
     * connections, creates a new ChatServer instance, and starts it.
     *
     * @param args the command-line arguments passed to the application
     */
    public static void main(String[] args) {
// create a scanner to read user input from the console
        Scanner sc = new Scanner(System.in);

// prompt the user to enter the port number on which the server should listen
        System.out.print("Enter the Port Number (e.g 9999 ) where you want the Server to Run : ");
        int port = sc.nextInt();

// create a new ChatServer instance with the specified port number
        ChatServer server = new ChatServer(port);

// start the server
        server.start();
    }

}
