package com.sarahmizzi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.firebase.client.*;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {

        Logger Log = Logger.getLogger(Main.class.getName());
        JFrame frame = new JFrame();
        Server server = new Server();

        Kryo kryo = server.getKryo();
        kryo.register(TcpRequest.class);
        kryo.register(TcpResponse.class);

        int startConnection = JOptionPane.showConfirmDialog(frame, "Would you like to start connection?", "Connection",
                JOptionPane.YES_NO_OPTION);

        if(startConnection == JOptionPane.YES_OPTION){
            server.start();
            try {
                server.bind(81);
            }
            catch(IOException e){
                Log.setLevel(Level.SEVERE);
                Log.severe(e.getMessage());
            }
        }
        else{
            System.exit(1);
        }

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof TcpRequest) {
                    TcpRequest tcpRequest = (TcpRequest) object;
                    System.out.println(tcpRequest.methodName);

                    String[] request = tcpRequest.methodName.split(";", -1);
                    Firebase videoFirebaseRef = new Firebase("https://sweltering-torch-8619.firebaseio.com/android/video/");
                    Query queryRef = videoFirebaseRef.child(request[0]).limitToLast(1);

                    queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot querySnapshot) {
                            Video video = querySnapshot.getChildren().iterator().next().getValue(Video.class);

                            switch(video.getCategory()){
                                case "FOOTBALL":
                                    break;
                                case "MOVIES":
                                    String movieTitle = video.getTitle();
                                    String movieToSearch = movieTitle.replace("Trailer", "");

                                    
                                    break;
                                case "MUSIC":
                                    String musicTitle = video.getTitle();
                                    String temp3 = musicTitle.replace("Official", "");
                                    String musicToSearch = temp3.replace("Video", "");


                                    break;
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError error) {
                            Log.setLevel(Level.SEVERE);
                            Log.severe(error.getMessage());
                        }
                    });

                    JOptionPane.showMessageDialog(frame, tcpRequest.methodName);



                    URI uri = null;
                    try {
                        uri = new URI("https://twitter.com/search?f=tweets&vertical=news&q=liverpool&src=typd&lang=en");
                    }
                    catch(URISyntaxException e){
                        Log.setLevel(Level.SEVERE);
                        Log.severe(e.getMessage());
                    }

                    if(uri != null){
                        try {
                            java.awt.Desktop.getDesktop().browse(uri);
                        }
                        catch(IOException e){
                            Log.setLevel(Level.SEVERE);
                            Log.severe(e.getMessage());
                        }
                    }

                    TcpResponse response = new TcpResponse();
                    response.message = "OK";
                    connection.sendTCP(response);
                }
            }
        });
    }
}
