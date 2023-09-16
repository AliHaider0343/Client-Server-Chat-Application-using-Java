package com.mycompany.chat_application;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 *
 * The ChatClient class represents a simple chat client that connects to a chat
 * server and allows the user to send and receive messages. The class creates
 * two threads - one for sending messages to the server and one for receiving
 * messages from the server. The SendHandler thread takes input from the console
 * and sends it to the server. The ReceiveHandler thread receives messages from
 * the server and prints them to the console. To use the chat client, run the
 * ChatClient class and enter the port number to connect to the server. Once
 * connected, enter a username to identify yourself to other users in the chat.
 * You can now send messages to the chat by typing them in the console and
 * hitting enter. To quit the chat, type "quit" and hit enter.
 *
 * @author Jose Omar
 * @version 1.0
 * @since April 20, 2023
 */
public class ChatClient {

    /**
     * Socket to Connect
     */
    protected Socket socket;

    /**
     * Any username for identification
     */
    protected String username;

    /**
     * Constructs a new ChatClient object and connects to the chat server at the
     * specified host and port. Creates two threads for sending and receiving
     * messages.
     *
     * @param serverName The hostname of the chat server.
     * @param port The port number to connect to on the chat server.
     */
    public ChatClient(String serverName, int port) {
        try {
            socket = new Socket(serverName, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // create threads for sending and receiving messages
            Thread sendThread = new Thread(new SendHandler(out, stdIn));
            Thread receiveThread = new Thread(new ReceiveHandler(in));
            sendThread.start();
            receiveThread.start();

        } catch (IOException e) {
            System.out.println("\nError connecting to server on the given socket. Please input a valid socket.");

        }
    }

    /**
     * A private inner class that implements the Runnable interface. This class
     * handles sending messages from the client to the server.
     *
     */
    protected class SendHandler implements Runnable {

        /**
         * to Write the output Stream
         */
        protected PrintWriter out;

        /**
         * to Read the Input Stream
         */
        protected BufferedReader stdIn;

        /**
         *
         * Constructs a new SendHandler object with the given PrintWriter and
         * BufferedReader objects.
         *
         * @param out The PrintWriter object that will be used to send messages
         * to the server.
         * @param stdIn The BufferedReader object that will be used to read
         * input from the user.
         */
        public SendHandler(PrintWriter out, BufferedReader stdIn) {
            this.out = out;
            this.stdIn = stdIn;
        }

        /**
         *
         * The run method of this class is called when the thread is started. It
         * reads input from the user and sends it to the server. If the user
         * types "quit", the thread exits.
         *
         */
        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = stdIn.readLine()) != null) {
                    out.println(inputLine);
                    if (inputLine.equalsIgnoreCase("quit")) {
                        break;
                    }
                }
                socket.close();
            } catch (IOException e) {
                System.out.println("\nError sending message to server...");
            }
        }
    }

    /**
     *
     * The ReceiveHandler class is a thread that receives messages from the
     * server and prints them to the console. It implements the Runnable
     * interface and
     *
     * overrides the run() method.
     */
    protected class ReceiveHandler implements Runnable {

        /**
         * BufferedReader to read messages from the server
         */
        protected BufferedReader in;

        /**
         *
         * Constructs a new ReceiveHandler object with a BufferedReader to read
         * messages from the server.
         *
         * @param in the BufferedReader to read messages from the server
         */
        public ReceiveHandler(BufferedReader in) {
            this.in = in;
        }

        /**
         *
         * Overrides the run() method from the Runnable interface. Receives
         * messages from the server and prints them to the console until the
         * connection is closed or an error occurs.
         *
         */
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (SocketException e) {
                System.out.println("\nChat Connection closed...");
            } catch (IOException e) {
                System.out.println("\nError receiving message from server..");
            }
        }
    }

    /**
     *
     * The main class for the chat client. Prompts the user for the port number
     * to connect to the server and creates a new ChatClient object to connect
     * to the chat server.
     *
     * @param args The command line arguments passed to the program.
     *
     */
    public static void main(String[] args) {
// prompt the user to enter the port number to connect to the server
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the Port Number (e.g 9999 ) to Connect with Server : ");
        int port = sc.nextInt();

// create a new ChatClient object and connect to the chat server
        ChatClient client = new ChatClient("localhost", port);
    }
}
