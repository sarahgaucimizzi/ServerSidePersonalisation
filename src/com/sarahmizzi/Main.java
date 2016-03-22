package com.sarahmizzi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.firebase.client.*;
import org.simmetrics.StringMetric;
import org.simmetrics.StringMetrics;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (startConnection == JOptionPane.YES_OPTION) {
            server.start();
            try {
                server.bind(81);
            } catch (IOException e) {
                Log.setLevel(Level.SEVERE);
                Log.severe(e.getMessage());
            }
        } else {
            System.exit(1);
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof TcpRequest) {
                    TcpRequest tcpRequest = (TcpRequest) object;
                    System.out.println(tcpRequest.message);

                    Firebase videoFirebaseRef = new Firebase("https://sweltering-torch-8619.firebaseio.com/android/video/");
                    Query queryRef = videoFirebaseRef.child(tcpRequest.message).limitToLast(1);

                    queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot querySnapshot) {
                            Video video = querySnapshot.getChildren().iterator().next().getValue(Video.class);

                            URI uri = null;

                            switch (video.getCategory()) {
                                case "Sports":
                                    Object[] footballOptions = {"Open Skysports",
                                            "Open Twitter",
                                            "Cancel"};

                                    int footballChoice = JOptionPane.showOptionDialog(frame,
                                            "Would you like more information?",
                                            "More Information",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            footballOptions,
                                            footballOptions[2]);

                                    String videoToSearch = video.getTitle().replace(" ", "%20");

                                    if (footballChoice == JOptionPane.YES_OPTION) {
                                        try {
                                            uri = new URI("http://www.skysports.com/search?q=" + videoToSearch + "&searchtype=the+site");
                                        } catch (URISyntaxException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }

                                    } else if (footballChoice == JOptionPane.NO_OPTION) {
                                        try {
                                            uri = new URI("https://twitter.com/search?f=tweets&vertical=news&q=" + videoToSearch + "&src=typd&lang=en");
                                        } catch (URISyntaxException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }
                                    }

                                    if (uri != null) {
                                        try {
                                            java.awt.Desktop.getDesktop().browse(uri);
                                        } catch (IOException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }
                                    }

                                    break;
                                case "Thriller":
                                case "Sci-Fi/Fantasy":
                                case "Horror":
                                case "Foreign":
                                case "Family":
                                case "Drama":
                                case "Documentary":
                                case "Comedy":
                                case "Classics":
                                case "Action/Adventure":
                                case "Anime/Animation":
                                case "Movies":
                                case "Entertainment":
                                case "Trailers":
                                case "Film & Animation":
                                    String movieTitle = video.getTitle();
                                    String tempA = movieTitle.replace("Official", "");
                                    String tempB = tempA.replace("Movie", "");
                                    String tempC = tempB.replace("HD", "");
                                    String tempD = tempC.replace("-", "");
                                    String movieToSearch = tempD.replace("Trailer", "");

                                    Firebase movieFirebaseRef = new Firebase("https://sweltering-torch-8619.firebaseio.com/android/");
                                    Query movieQueryRef = movieFirebaseRef.child("movies");

                                    movieQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot movieQuerySnapshot) {
                                            ArrayList<MovieItem> movies = new ArrayList<>();
                                            float max = 0;

                                            for (DataSnapshot current : movieQuerySnapshot.getChildren()) {
                                                Movie movie = current.getValue(Movie.class);

                                                StringMetric metric = StringMetrics.mongeElkan();

                                                float result = metric.compare(movieToSearch, movie.getTitle());

                                                if(result >= max){
                                                    movies.add(0, new MovieItem(movie.getTitle(), movie.getYear(), current.getKey()));
                                                    max = result;
                                                    System.out.println(result + "\t" + movie.getTitle());
                                                }
                                            }

                                            Object[] movieOptions = {"Open IMDB",
                                                    "Open Rotten Tomatoes",
                                                    "Cancel"};

                                            int movieChoice = JOptionPane.showOptionDialog(frame,
                                                    "Would you like more information?",
                                                    "More Information",
                                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                                    JOptionPane.PLAIN_MESSAGE,
                                                    null,
                                                    movieOptions,
                                                    movieOptions[2]);

                                            URI url = null;

                                            String movieToSearch = movies.get(0).getTitle().replace(" ", "%20");

                                            if (movieChoice == JOptionPane.YES_OPTION) {
                                                try {
                                                    url = new URI("http://www.imdb.com/title/" + movies.get(0).getId());
                                                } catch (URISyntaxException e) {
                                                    Log.setLevel(Level.SEVERE);
                                                    Log.severe(e.getMessage());
                                                }

                                            } else if (movieChoice == JOptionPane.NO_OPTION) {
                                                try {
                                                    url = new URI("http://www.rottentomatoes.com/search/?search=" + movieToSearch);
                                                } catch (URISyntaxException e) {
                                                    Log.setLevel(Level.SEVERE);
                                                    Log.severe(e.getMessage());
                                                }
                                            }

                                            if (url != null) {
                                                try {
                                                    java.awt.Desktop.getDesktop().browse(url);
                                                } catch (IOException e) {
                                                    Log.setLevel(Level.SEVERE);
                                                    Log.severe(e.getMessage());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError error) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(error.getMessage());
                                        }
                                    });

                                    break;
                                case "Music":
                                    String musicTitle = video.getTitle();
                                    String temp1 = musicTitle.replace(" ", "%20");
                                    String temp2 = temp1.replace("-", "");
                                    String temp3 = temp2.replace("Official", "");
                                    String temp4 = temp3.replace("Audio", "");
                                    String musicToSearch = temp4.replace("Video", "");

                                    Object[] musicOptions = {"Open Spotify",
                                            "Open Myspace",
                                            "Cancel"};

                                    int choice = JOptionPane.showOptionDialog(frame,
                                            "Would you like more information?",
                                            "More Information",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            musicOptions,
                                            musicOptions[2]);

                                    if (choice == JOptionPane.YES_OPTION) {
                                        try {
                                            uri = new URI("https://play.spotify.com/search/" + musicToSearch);
                                        } catch (URISyntaxException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }

                                    } else if (choice == JOptionPane.NO_OPTION) {
                                        try {
                                            uri = new URI("https://myspace.com/search/?q=" + musicToSearch);
                                        } catch (URISyntaxException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }
                                    }

                                    if (uri != null) {
                                        try {
                                            java.awt.Desktop.getDesktop().browse(uri);
                                        } catch (IOException e) {
                                            Log.setLevel(Level.SEVERE);
                                            Log.severe(e.getMessage());
                                        }
                                    }

                                    break;

                                default:
                                    JOptionPane.showMessageDialog(frame, "Please try again on another video.", "More Information",
                                            JOptionPane.PLAIN_MESSAGE);
                                    break;
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError error) {
                            Log.setLevel(Level.SEVERE);
                            Log.severe(error.getMessage());
                        }
                    });

                    TcpResponse response = new TcpResponse();
                    response.message = "OK";
                    connection.sendTCP(response);
                }
            }
        });
    }
}
