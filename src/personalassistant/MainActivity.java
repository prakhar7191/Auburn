/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

import com.teknikindustries.yahooweather.WeatherDisplay;
import com.teknikindustries.yahooweather.WeatherDoc;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Shreyas
 */
public class MainActivity extends javax.swing.JFrame {

    
    int xMouse,yMouse;
    String defaultPlaceHolder = "< Enter something >";
    public static final String pythonFile="test.py";
    public static final String chatBotPy="brain_new.py";//"brain.py";
    public static final String getReplyPy="getReply.py";
    public static final String dbFile="database.db";
    public static final String dbTable="notes";
    
    
    public static final int defaultMode=0;
    public static final int emailMode=1;
    public static final int weatherMode=2;
    
    int currMode=defaultMode;
    
    public static final String fileDir = "C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot";
    public static final String path = "C:\\Python27";
    
    public static final String javaFileDir = "C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\personalassistant";
    public static final String javaFile = "VoiceRecog";
    
    public static final String weatherAPIKey = "V4cASGCClX7p8Xy51P5olxn3WnaAGfwC";//"UlLAKVh111vc2WanhX1IbRNKOzjB49uK";
    public static final String cityCodeAPI = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=";//UlLAKVh111vc2WanhX1IbRNKOzjB49uK&q=";
    public static final String weatherAPI = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    public static final String weatherIconURL = "https://developer.accuweather.com/sites/default/files/";
    public static final String newsAPI = "https://newsapi.org/v2/top-headlines?sources=the-times-of-india&apiKey=df8fd1b73ae8455ab8f21f0ce044c057";
    
    public static final String wikipediaURL = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";//+"narendra+modi"+"&format=json";
    
    
    public static String jsonOutput = "{\"Headline\":{\"EffectiveDate\":\"2018-01-26T19:00:00+05:30\",\"EffectiveEpochDate\":1516973400,\"Severity\":7,\"Text\":\"Cool Friday night\",\"Category\":\"cold\",\"EndDate\":\"2018-01-27T07:00:00+05:30\",\"EndEpochDate\":1517016600,\"MobileLink\":\"http://m.accuweather.com/en/in/dehradun/191339/extended-weather-forecast/191339?lang=en-us\",\"Link\":\"http://www.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?lang=en-us\"},\"DailyForecasts\":[{\"Date\":\"2018-01-26T07:00:00+05:30\",\"EpochDate\":1516930200,\"Temperature\":{\"Minimum\":{\"Value\":40.0,\"Unit\":\"F\",\"UnitType\":18},\"Maximum\":{\"Value\":65.0,\"Unit\":\"F\",\"UnitType\":18}},\"Day\":{\"Icon\":3,\"IconPhrase\":\"Partly sunny\"},\"Night\":{\"Icon\":37,\"IconPhrase\":\"Hazy moonlight\"},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?day=1&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?day=1&lang=en-us\"}]}";
    
    public static final int blankPanelIndex = 0;
    public static final int weatherPanelIndex = 1;
    public static final int wikiPanelIndex = 2;
    public static final int emailPanelIndex = 3;
    public static final int reminderPanelIndex = 4;
    public static final int notesPanelIndex = 5;
    public static final int newsPanelIndex = 6;
    public static final int numberOfPanels = 7;
    
    JPanel panels[];
    
    ProcessBuilder pb;
    Process pro;
    BufferedReader br;
    
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    String headlines[];
    String articles[];
    String imgURL[];
    int newIndex=0;
    
    boolean addRecipients=false;
    boolean isListening=false;
    FemaleVoice botVoice;
    VoiceRecog voiceRecog;
    
    ServerSocket server;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    boolean isSocketConnected;
    String socketData;
    /*
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                
            }
        });
        loader.start();
    */
    
    
    
    /**
     * Creates new form MainActivity
     */
    public MainActivity() {
        initComponents();
        botVoice = new FemaleVoice();
        voiceRecog = new VoiceRecog();
        
        
        initSocket();
        initDB();
        initPython();
        weatherPanel.setBackground( new Color(150, 150, 150, 100) );
        emailPanel.setBackground( new Color(150, 150, 150, 100) );
        reminderPanel.setBackground( new Color(150, 150, 150, 100) );
        newsPanel.setBackground( new Color(150, 150, 150, 100) );
        txtWikiOutput.setBackground( new Color(150, 150, 150, 255) );
        
        panels=new JPanel[numberOfPanels];
        panels[blankPanelIndex] = blankPanel;
        panels[weatherPanelIndex] = weatherPanel;
        panels[wikiPanelIndex] = wikiPanel;
        panels[emailPanelIndex] = emailPanel;
        panels[reminderPanelIndex] = reminderPanel;
        panels[notesPanelIndex] = notesPanel;
        panels[newsPanelIndex] = newsPanel;
        
        setParentPanelTo(blankPanelIndex);
        
        getTime();
    }
    
    public void initSocket(){
        System.out.println("init Socket");
        Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(5077);
                    socket = server.accept();
                    System.out.println("connection established");
                    isSocketConnected = true;
                    dos=new DataOutputStream(socket.getOutputStream());
                    dis=new DataInputStream(socket.getInputStream());
                    startSocketListener();
                } catch (Exception e) {
                    
                }
            }
        });
        socketThread.start();
    }

    public void startSocketListener() {
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isSocketConnected) {
                        String data = dis.readUTF();
                        System.out.println("date receicved "+data);
                        sendSocketResponse(data);
                        Thread.sleep(10);
                    }
                }
                catch (Exception e) {
                    
                }
            }
        });
        listener.start();
    }
    
    public void sendSocketResponse(String data) {
        try {
            String output = getChatBotResponse(data);
            System.out.println("data to be sent : "+output);
            dos.writeUTF(output);
            dos.flush();
        }
        catch (Exception e) {
            
        }
    }
    
    public void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:"+dbFile);
            System.out.println("Connection Established");
            displayTable();
        }
        catch (Exception e) {
            
        }
    }
    
    public void displayTable() {
        try {
            String sql="select * from "+dbTable+";";
            ps=conn.prepareStatement(sql);
            ps.clearParameters();
            rs=ps.executeQuery();
            while (rs.next()) {
                int id=rs.getInt(1);
                String name=rs.getString("name");
                String data=rs.getString("data");
                System.out.println(id+"\t\t"+name+"\t\t"+data);
            }
        }
        catch (Exception e) {
            
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
    }
    
    public void getTime() {
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");
        String date = s.format(d);
        s = new SimpleDateFormat("hh:mm:ss a");
        String time = s.format(d);
        System.out.println(date+" "+time);
    }
    
    public void getWeatherAPI(String location) {
        
        Thread loadQuery = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = cityCodeAPI+weatherAPIKey+"&q="+location;
                System.out.println(url);
                String cityCode=getCityCode(url);
                url = weatherAPI+cityCode+"?apikey="+weatherAPIKey;
                System.out.println(url);

                getWeatherData(url);
                setParentPanelTo(weatherPanelIndex);
            }
        });
        loadQuery.start();
        
    }
    
    public void getWikiAPI(String search) {
        //String str=search;
        showResponse("Loading data");
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                String str=search.replaceAll(" ","+");
                String url = wikipediaURL+str+"&format=json";
                System.out.println(url);
                String info = loadWikiData(url);
                setParentPanelTo(wikiPanelIndex);
                txtWikiOutput.setText(info);
            }
        });
        loader.start();
    }
    
    public String loadWikiData(String url) {
        try {
            //getResponseString(url);
            String resp = getResponseString(url);
            JSONParser parser = new JSONParser();
            String info = (String)(((JSONArray)(((JSONArray)parser.parse(resp)).get(2))).get(0));
            return info;
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    
    public void loadNewsData() {
        showResponse("Loading news data");
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setParentPanelTo(newsPanelIndex);
                    String url = newsAPI;
                    System.out.println(url);
                    String resp = getResponseString(url);
                    JSONParser parser = new JSONParser();
                    JSONObject data = (JSONObject)parser.parse(resp);
                    int num = (int)((long)(data.get("totalResults")));
                    headlines=new String[num];
                    articles=new String[num];
                    imgURL=new String[num];
                    JSONArray art = (JSONArray)data.get("articles");
                    for (int i=0;i<num;i++) {
                        data = (JSONObject)art.get(i);
                        headlines[i]=(String)data.get("title");
                        articles[i]=(String)data.get("description");
                        imgURL[i]=(String)data.get("urlToImage");
                    }
                    newIndex=0;
                    txtNewsHead.setText(headlines[newIndex]);
                    txtNewsText.setText(articles[newIndex]);
                    showResponse(txtNewsHead.getText());
                }
                catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        });
        loader.start();
        
        
    }
    
    public void setParentPanelTo(int panelIndex) {
        parentPanel.removeAll();
        parentPanel.add(panels[panelIndex]);
        parentPanel.repaint();
        parentPanel.revalidate();
    }
    
    private String getResponseString(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            String resp=response.toString();
            System.out.println(resp);
            return resp;
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    public String getCityCode(String url) {
        try {
            String resp = getResponseString(url);
            JSONParser parser = new JSONParser();
            JSONObject myResponse = (JSONObject)((JSONArray)parser.parse(resp)).get(0);
            return (String)myResponse.get("Key");
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    public void getWeatherData(String url) {
        try {
            String resp = getResponseString(url); // jsonOutput
            System.out.println(resp);
            JSONParser parser = new JSONParser();
            String data = (String)((JSONObject)(((JSONObject)parser.parse(resp)).get("Headline"))).get("Text");
            showResponse(data);
            
            
            JSONObject dailyForecasts = ((JSONObject)(((JSONArray)(((JSONObject)parser.parse(resp)).get("DailyForecasts"))).get(0)));
            double maxTemp = (double)((JSONObject)((JSONObject)dailyForecasts.get("Temperature")).get("Maximum")).get("Value");
            double minTemp = (double)((JSONObject)((JSONObject)dailyForecasts.get("Temperature")).get("Minimum")).get("Value");
            
            String day = (String)((JSONObject)dailyForecasts.get("Day")).get("IconPhrase");
            String night = (String)((JSONObject)dailyForecasts.get("Night")).get("IconPhrase");
            
            long ic = (long)((JSONObject)dailyForecasts.get("Day")).get("Icon");
            String iconDay = "";
            String iconNight = "";
            if (ic<10)
                iconDay = "0"+ic;
            else
                iconDay = ic+"";
            ic = (long)((JSONObject)dailyForecasts.get("Night")).get("Icon");
            if (ic<10)
                iconNight = "0"+ic;
            else
                iconNight = ic+"";
            
            setWeatherIcon(dayIcon,iconDay);
            setWeatherIcon(nightIcon,iconNight);
            
            maxTemp = 5.0/9*(maxTemp-32);
            minTemp = 5.0/9*(minTemp-32);
            
            txtWeatherInfo.setText(data);
            txtMaxTemp.setText(String.format("%.1f",maxTemp)+" °C");
            txtMinTemp.setText(String.format("%.1f",minTemp)+" °C");
            txtDayPhrase.setText(day);
            txtNightPhrase.setText(night);
            
            System.out.println(data);
            System.out.println(maxTemp+" "+minTemp);
            System.out.println(day+"\n"+night);
            System.out.println(iconDay+"\n"+iconNight);
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            //return null;
        }
    }
    
    public void setWeatherIcon(JLabel label,String index) {
        
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedImage brImg = ImageIO.read(new URL(weatherIconURL+index+"-s.png"));
                    int w=150,h=90;
                    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = resizedImg.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(brImg, 0, 0, w, h, null);
                    g2.dispose();
                    label.setIcon(new ImageIcon(resizedImg)); 
                }
                catch (Exception e) {

                }
            }
        });
        loader.start();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentPanel = new javax.swing.JPanel();
        blankPanel = new javax.swing.JPanel();
        weatherPanel = new javax.swing.JPanel();
        dayIcon = new javax.swing.JLabel();
        nightIcon = new javax.swing.JLabel();
        txtWeatherInfo = new javax.swing.JLabel();
        txtMinTemp = new javax.swing.JLabel();
        txtMaxTemp = new javax.swing.JLabel();
        txtDayPhrase = new javax.swing.JLabel();
        txtNightPhrase = new javax.swing.JLabel();
        wikiPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtWikiOutput = new javax.swing.JTextArea();
        emailPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtEmailTo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtEmailText = new javax.swing.JTextArea();
        btnSendEmail = new javax.swing.JButton();
        btnEmailClose = new javax.swing.JLabel();
        reminderPanel = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        datePicker = new com.toedter.calendar.JDateChooser();
        btnRemSet = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnRemClose = new javax.swing.JLabel();
        hourPicker = new javax.swing.JSpinner();
        minPicker = new javax.swing.JSpinner();
        txtRemMsg = new javax.swing.JTextField();
        notesPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtNoteText = new javax.swing.JTextArea();
        txtNoteFileName = new javax.swing.JTextField();
        btnNoteSave = new javax.swing.JButton();
        btnNoteCancel = new javax.swing.JButton();
        btnNoteOpen = new javax.swing.JButton();
        btnNoteShow = new javax.swing.JButton();
        newsPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtNewsText = new javax.swing.JTextArea();
        btnNewsNext = new javax.swing.JLabel();
        btnNewsPrev = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtNewsHead = new javax.swing.JTextArea();
        btnNewsClose = new javax.swing.JLabel();
        btnSpeak = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtOutput = new javax.swing.JTextField();
        txtInput = new javax.swing.JTextField();
        btnClose = new javax.swing.JLabel();
        titleBar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        parentPanel.setOpaque(false);
        parentPanel.setLayout(new java.awt.CardLayout());

        blankPanel.setOpaque(false);

        javax.swing.GroupLayout blankPanelLayout = new javax.swing.GroupLayout(blankPanel);
        blankPanel.setLayout(blankPanelLayout);
        blankPanelLayout.setHorizontalGroup(
            blankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
        );
        blankPanelLayout.setVerticalGroup(
            blankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );

        parentPanel.add(blankPanel, "card3");

        weatherPanel.setBackground(new java.awt.Color(51, 51, 51));

        dayIcon.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        dayIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nothing_to_show.png"))); // NOI18N

        nightIcon.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        nightIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nothing_to_show.png"))); // NOI18N

        txtWeatherInfo.setFont(new java.awt.Font("Tahoma", 0, 37)); // NOI18N
        txtWeatherInfo.setForeground(new java.awt.Color(255, 255, 255));
        txtWeatherInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtWeatherInfo.setText("Clear Sky");

        txtMinTemp.setFont(new java.awt.Font("Agency FB", 0, 70)); // NOI18N
        txtMinTemp.setForeground(new java.awt.Color(255, 255, 255));
        txtMinTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtMinTemp.setText("10 °C");

        txtMaxTemp.setFont(new java.awt.Font("Agency FB", 0, 70)); // NOI18N
        txtMaxTemp.setForeground(new java.awt.Color(255, 255, 255));
        txtMaxTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtMaxTemp.setText("25 °C");

        txtDayPhrase.setFont(new java.awt.Font("Tahoma", 0, 25)); // NOI18N
        txtDayPhrase.setForeground(new java.awt.Color(255, 255, 255));
        txtDayPhrase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDayPhrase.setText("Sunny day");

        txtNightPhrase.setFont(new java.awt.Font("Tahoma", 0, 25)); // NOI18N
        txtNightPhrase.setForeground(new java.awt.Color(255, 255, 255));
        txtNightPhrase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNightPhrase.setText("Cool night");

        javax.swing.GroupLayout weatherPanelLayout = new javax.swing.GroupLayout(weatherPanel);
        weatherPanel.setLayout(weatherPanelLayout);
        weatherPanelLayout.setHorizontalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(weatherPanelLayout.createSequentialGroup()
                        .addComponent(txtWeatherInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(weatherPanelLayout.createSequentialGroup()
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dayIcon)
                            .addComponent(nightIcon))
                        .addGap(21, 21, 21)
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(weatherPanelLayout.createSequentialGroup()
                                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(weatherPanelLayout.createSequentialGroup()
                                        .addGap(218, 218, 218)
                                        .addComponent(txtNightPhrase, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(weatherPanelLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(txtMaxTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDayPhrase, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(40, Short.MAX_VALUE))
                            .addGroup(weatherPanelLayout.createSequentialGroup()
                                .addComponent(txtMinTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        weatherPanelLayout.setVerticalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMaxTemp)
                    .addComponent(dayIcon)
                    .addComponent(txtDayPhrase))
                .addGap(18, 18, 18)
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMinTemp)
                        .addComponent(txtNightPhrase))
                    .addComponent(nightIcon))
                .addGap(18, 18, 18)
                .addComponent(txtWeatherInfo)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        parentPanel.add(weatherPanel, "card2");

        wikiPanel.setOpaque(false);

        jScrollPane1.setOpaque(false);

        txtWikiOutput.setColumns(20);
        txtWikiOutput.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        txtWikiOutput.setForeground(new java.awt.Color(51, 51, 255));
        txtWikiOutput.setLineWrap(true);
        txtWikiOutput.setRows(5);
        txtWikiOutput.setOpaque(false);
        jScrollPane1.setViewportView(txtWikiOutput);

        javax.swing.GroupLayout wikiPanelLayout = new javax.swing.GroupLayout(wikiPanel);
        wikiPanel.setLayout(wikiPanelLayout);
        wikiPanelLayout.setHorizontalGroup(
            wikiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wikiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                .addContainerGap())
        );
        wikiPanelLayout.setVerticalGroup(
            wikiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wikiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addContainerGap())
        );

        parentPanel.add(wikiPanel, "card4");

        emailPanel.setBackground(new java.awt.Color(51, 51, 51));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gmail icon.png"))); // NOI18N

        txtEmailTo.setBackground(new java.awt.Color(230, 230, 230));
        txtEmailTo.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        txtEmailText.setBackground(new java.awt.Color(230, 230, 230));
        txtEmailText.setColumns(20);
        txtEmailText.setFont(new java.awt.Font("Monospaced", 0, 20)); // NOI18N
        txtEmailText.setRows(5);
        jScrollPane2.setViewportView(txtEmailText);

        btnSendEmail.setText("Send");
        btnSendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendEmailActionPerformed(evt);
            }
        });

        btnEmailClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnEmailClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEmailCloseMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout emailPanelLayout = new javax.swing.GroupLayout(emailPanel);
        emailPanel.setLayout(emailPanelLayout);
        emailPanelLayout.setHorizontalGroup(
            emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(emailPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtEmailTo, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSendEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEmailClose)
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
        emailPanelLayout.setVerticalGroup(
            emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailPanelLayout.createSequentialGroup()
                .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnEmailClose, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmailTo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSendEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );

        parentPanel.add(emailPanel, "card5");

        reminderPanel.setBackground(new java.awt.Color(51, 51, 51));

        datePicker.setDateFormatString("d MMM , yyyy");
        datePicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        btnRemSet.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnRemSet.setText("Set");
        btnRemSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemSetActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reminder.png"))); // NOI18N

        btnRemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnRemClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRemCloseMouseClicked(evt);
            }
        });

        hourPicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        minPicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        txtRemMsg.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtRemMsg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRemMsg.setText("Reminder");

        javax.swing.GroupLayout reminderPanelLayout = new javax.swing.GroupLayout(reminderPanel);
        reminderPanel.setLayout(reminderPanelLayout);
        reminderPanelLayout.setHorizontalGroup(
            reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reminderPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(btnRemSet, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, reminderPanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(reminderPanelLayout.createSequentialGroup()
                                .addComponent(hourPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(minPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(datePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemMsg))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reminderPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemClose)
                .addContainerGap())
        );
        reminderPanelLayout.setVerticalGroup(
            reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reminderPanelLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(txtRemMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(btnRemSet, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reminderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRemClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(27, 27, 27))
        );

        parentPanel.add(reminderPanel, "card6");

        txtNoteText.setColumns(20);
        txtNoteText.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        txtNoteText.setRows(5);
        jScrollPane3.setViewportView(txtNoteText);

        txtNoteFileName.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnNoteSave.setText("Save");
        btnNoteSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoteSaveActionPerformed(evt);
            }
        });

        btnNoteCancel.setText("Cancel");
        btnNoteCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoteCancelActionPerformed(evt);
            }
        });

        btnNoteOpen.setText("Open");
        btnNoteOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoteOpenActionPerformed(evt);
            }
        });

        btnNoteShow.setText("Show");
        btnNoteShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoteShowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout notesPanelLayout = new javax.swing.GroupLayout(notesPanel);
        notesPanel.setLayout(notesPanelLayout);
        notesPanelLayout.setHorizontalGroup(
            notesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(notesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(notesPanelLayout.createSequentialGroup()
                        .addComponent(txtNoteFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNoteShow)
                        .addGap(18, 18, 18)
                        .addComponent(btnNoteOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNoteCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNoteSave, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        notesPanelLayout.setVerticalGroup(
            notesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, notesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(notesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNoteFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnNoteCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNoteSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNoteShow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNoteOpen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        parentPanel.add(notesPanel, "card7");

        newsPanel.setBackground(new java.awt.Color(51, 51, 51));

        txtNewsText.setEditable(false);
        txtNewsText.setColumns(20);
        txtNewsText.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        txtNewsText.setLineWrap(true);
        txtNewsText.setRows(5);
        jScrollPane4.setViewportView(txtNewsText);

        btnNewsNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/next.png"))); // NOI18N
        btnNewsNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNewsNextMouseClicked(evt);
            }
        });

        btnNewsPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/prev.png"))); // NOI18N
        btnNewsPrev.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNewsPrevMouseClicked(evt);
            }
        });

        txtNewsHead.setEditable(false);
        txtNewsHead.setColumns(20);
        txtNewsHead.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        txtNewsHead.setLineWrap(true);
        txtNewsHead.setRows(5);
        jScrollPane5.setViewportView(txtNewsHead);

        btnNewsClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnNewsClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNewsCloseMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout newsPanelLayout = new javax.swing.GroupLayout(newsPanel);
        newsPanel.setLayout(newsPanelLayout);
        newsPanelLayout.setHorizontalGroup(
            newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(newsPanelLayout.createSequentialGroup()
                        .addComponent(btnNewsPrev)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNewsNext)
                            .addComponent(btnNewsClose, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        newsPanelLayout.setVerticalGroup(
            newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4))
                    .addGroup(newsPanelLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(newsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNewsNext)
                            .addComponent(btnNewsPrev))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addComponent(btnNewsClose)))
                .addContainerGap())
        );

        parentPanel.add(newsPanel, "card8");

        getContentPane().add(parentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 730, 310));

        btnSpeak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N
        btnSpeak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSpeakMouseClicked(evt);
            }
        });
        getContentPane().add(btnSpeak, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 540, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N
        jButton1.setOpaque(false);
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 50));

        txtOutput.setEditable(false);
        txtOutput.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtOutput.setForeground(new java.awt.Color(255, 255, 255));
        txtOutput.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtOutput.setText("< Thinking >");
        txtOutput.setCaretColor(new java.awt.Color(255, 255, 255));
        txtOutput.setOpaque(false);
        txtOutput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOutputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOutputFocusLost(evt);
            }
        });
        txtOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtOutputKeyTyped(evt);
            }
        });
        getContentPane().add(txtOutput, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 640, 40));

        txtInput.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtInput.setForeground(new java.awt.Color(255, 255, 255));
        txtInput.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInput.setText("< Enter something >");
        txtInput.setCaretColor(new java.awt.Color(255, 255, 255));
        txtInput.setOpaque(false);
        txtInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtInputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtInputFocusLost(evt);
            }
        });
        txtInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtInputKeyTyped(evt);
            }
        });
        getContentPane().add(txtInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 540, 40));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close-icon.png"))); // NOI18N
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCloseMouseClicked(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, -1));

        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                titleBarMouseDragged(evt);
            }
        });
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                titleBarMousePressed(evt);
            }
        });
        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 600));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/liveWallpaper.gif"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        setSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void titleBarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleBarMouseDragged
        this.setLocation(evt.getXOnScreen()-xMouse,evt.getYOnScreen()-yMouse);
    }//GEN-LAST:event_titleBarMouseDragged

    private void titleBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleBarMousePressed
        xMouse=evt.getX();
        yMouse=evt.getY();
    }//GEN-LAST:event_titleBarMousePressed

    private void btnCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCloseMouseClicked
        closeApp();
    }//GEN-LAST:event_btnCloseMouseClicked

    private void txtInputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInputFocusGained
        String txt=txtInput.getText();
        if (txt.equals(defaultPlaceHolder)) {
            txtInput.setText("");
        }
    }//GEN-LAST:event_txtInputFocusGained

    private void txtInputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInputFocusLost
        String txt=txtInput.getText();
        if (txt.equals("")) {
            txtInput.setText(defaultPlaceHolder);
        }
    }//GEN-LAST:event_txtInputFocusLost

    private void txtInputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputKeyTyped
        if (evt.getKeyChar()=='\n') {
            String txt=txtInput.getText();
            txtInput.setText("");
            executeCommand(txt);
            //exec(txt);
            //execURL(txt);
            //searchQuery();
            //getWeather();
        }
    }//GEN-LAST:event_txtInputKeyTyped

    private void txtOutputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOutputFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputFocusGained

    private void txtOutputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOutputFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputFocusLost

    private void txtOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputKeyTyped

    private void btnSendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendEmailActionPerformed
        String sendTo = txtEmailTo.getText();
        if (sendTo.equals("")) {
            addRecipients=true;
            showResponse("Add Recipients");
        }
        else {
            String to[]=sendTo.split(" ");
            if (Email.sendMail(txtEmailText.getText(),to)) {
                showResponse("Your email has been sent");
                currMode=defaultMode;
                txtEmailText.setText("");
                setParentPanelTo(blankPanelIndex);
            }
            else {
                showResponse("Sorry email was not sent");
            }
        }
    }//GEN-LAST:event_btnSendEmailActionPerformed

    private void btnEmailCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEmailCloseMouseClicked
        // TODO add your handling code here:
        currMode=defaultMode;
        txtEmailText.setText("");
        txtEmailTo.setText("");
        setParentPanelTo(blankPanelIndex);
        txtOutput.setText("");
    }//GEN-LAST:event_btnEmailCloseMouseClicked

    private void btnRemCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemCloseMouseClicked
        setParentPanelTo(blankPanelIndex);
        txtOutput.setText("");
    }//GEN-LAST:event_btnRemCloseMouseClicked

    private void btnRemSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemSetActionPerformed
        Date d = datePicker.getDate();
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");
        String date = s.format(d);
        System.out.println(date);
        
        String hrs = hourPicker.getValue().toString();
        String min = minPicker.getValue().toString();
        
        System.out.println(hrs+" "+min);
        
        if (hrs.length()<2)
            hrs="0"+hrs;
        hrs=hrs+":";
        if (min.length()<2)
            min="0"+min;
        
        String targetTime = hrs+min;
        
        s = new SimpleDateFormat("hh:mm:ss");
        String time = s.format(d);
        time=time.substring(0,time.lastIndexOf(":"));
        
        System.out.println(targetTime);
        System.out.println(time);
        
        
        Popup window = new Popup();
        window.targetDate=date;
        window.targetTime=targetTime;
        window.message=txtRemMsg.getText();
        window.startTimer();
    }//GEN-LAST:event_btnRemSetActionPerformed

    private void btnNoteCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteCancelActionPerformed
        txtNoteFileName.setText("");
        txtNoteText.setText("");
        showResponse("Note not saved");
        setParentPanelTo(blankPanelIndex);
    }//GEN-LAST:event_btnNoteCancelActionPerformed

    private void btnNoteSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteSaveActionPerformed
        String data = txtNoteText.getText();
        String file = txtNoteFileName.getText();
        showResponse("Saving note");
        int id = maxEntry()+1;
        
        if (file.equals("")) {
            file="untitled-"+id;
        }
        
        try {
            String sql="insert into "+dbTable+" values(?,?,?)";
            ps=conn.prepareStatement(sql);
            ps.setInt(1,id);
            ps.setString(2,file);
            ps.setString(3,data);
            ps.executeUpdate();
            displayTable();
        }
        catch (Exception e) {
            System.out.println("Failed");
            System.out.println(e);
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
        
    }//GEN-LAST:event_btnNoteSaveActionPerformed

    private void btnNoteOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteOpenActionPerformed
        String data=openDBFile();
        txtNoteText.setText(data);
        showResponse("Opening note");
    }//GEN-LAST:event_btnNoteOpenActionPerformed

    private void btnNoteShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteShowActionPerformed
        txtNoteText.setText("");
        showResponse("Showing list of all notes");
        try {
            String sql="select * from "+dbTable+";";
            ps=conn.prepareStatement(sql);
            ps.clearParameters();
            rs=ps.executeQuery();
            while (rs.next()) {
                String name=rs.getString("name");
                System.out.println(name);
                txtNoteText.append(name+"\n");
            }
        }
        catch (Exception e) {
            
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
    }//GEN-LAST:event_btnNoteShowActionPerformed

    private void btnNewsNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewsNextMouseClicked
        newIndex = (newIndex+1)%articles.length;
        txtNewsHead.setText(headlines[newIndex]);
        txtNewsText.setText(articles[newIndex]);
        showResponse(txtNewsHead.getText());
    }//GEN-LAST:event_btnNewsNextMouseClicked

    private void btnNewsPrevMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewsPrevMouseClicked
        newIndex = (newIndex-1+articles.length)%articles.length;
        txtNewsHead.setText(headlines[newIndex]);
        txtNewsText.setText(articles[newIndex]);
    }//GEN-LAST:event_btnNewsPrevMouseClicked

    private void btnNewsCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewsCloseMouseClicked
        setParentPanelTo(blankPanelIndex);
        txtOutput.setText("");
    }//GEN-LAST:event_btnNewsCloseMouseClicked

    private void btnSpeakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSpeakMouseClicked
        if (!isListening) {
            isListening=true;
            Thread listening = new Thread(new Runnable() {
                @Override
                public void run() {
                    micCommand();
                    //voiceRecog.interpret();
                    isListening=false;
                }
            });
            listening.start();
        }
    }//GEN-LAST:event_btnSpeakMouseClicked

    public void micCommand() {
        Thread compileRun = new Thread(new Runnable(){
            @Override
            public void run() {
                //inFile=filePath.substring(filePath.lastIndexOf('\\')+1);
                //outFile=inFile.substring(0,inFile.lastIndexOf('.'));
                //fileDir=filePath.substring(0,filePath.lastIndexOf('\\'));
                //String ext=filePath.substring(filePath.lastIndexOf('.'));
                //pb = new ProcessBuilder("cmd", "/C", "g++ " + "\"" + filepath2 + "\\" + name + "\"" + " -o \"" + name2+"\"");
                try {
                    //ProcessBuilder pb = new ProcessBuilder("cmd", "/C", outFile);
                    ProcessBuilder pb;
                    pb = new ProcessBuilder("cmd","/c","java "+javaFile);
                        //fileDir="C:\\Program Files\\Java\\jdk1.8.0_111\\bin";
                    pb.directory(new File(javaFileDir));
                    Process p = pb.start();
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    //PrintWriter pw = new PrintWriter(p.getOutputStream());
                    //String inp=txtInput.getText()+"\n";
                    
                    //byte buffer[] = inp.getBytes();//new byte[100];
                    //buffer=new String("5\n").getBytes();
                    //OutputStream os =(p.getOutputStream());
                    //os.write(buffer,0,buffer.length);
                    //os.flush();
                    
                    String str;
                    //textDebug.setText("");
                    long t1=System.currentTimeMillis();
                    while ((str=br.readLine())!=null) {
                        System.out.println(str);
                        //textDebug.append(str+"\n");
                        //if (terminate) {
                           // break;
                        //}
                        try {
                            ;//Thread.sleep(10);
                        } catch (Exception e) {
                            ;
                        }
                    }
                    long t2=System.currentTimeMillis();
                    //System.out.println("show");
                    //textDebug.append("\nRun completed !\n");
                    //textDebug.append("Elapsed Time : "+((t2-t1)/1000.0)+" sec\n");
                    //terminate=false;
                    //System.out.println("");
                    //textDebug.setText("");
                    p.waitFor();
                    int x = p.exitValue();
                    if (x == 0) {
                        ;//JOptionPane.showMessageDialog(null,"Run Completed !");
                        System.out.println("java mic code run complete");
                    }
                    else
                    {
                        System.out.println("Displaying something");
                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String out;
                        while ((out = r.readLine()) != null)
                        {
                            String msg=out + System.getProperty("line.separator");
                            System.out.println(msg);
                            System.out.println("Compiler : "+out);
                            //textDebug.append(out+"\n");
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Error ");
                }
            }
        });
        compileRun.start();
    }
    
    public String openDBFile() {
        String sql="select * from "+dbTable+" where name=?";
        try {
            ps=conn.prepareStatement(sql);
            ps.setString(1,txtNoteFileName.getText());
            rs=ps.executeQuery();
            if (rs.next()) {
                int tmp=rs.getInt("id");
                String data = rs.getString("data");
                rs.close();
                ps.close();
                return data;
            }
            else {
                showResponse("Sorry I could not find your file");
                System.out.println("file not found");
            }
        }
        catch (Exception e) {
            showResponse("Sorry I could not find your file");
            System.out.println("file Not found");
            System.out.println(e);
            e.printStackTrace();
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
        return "";
    }
    
    public int countEntry() {
        try {
            String sql="select count(*) from "+dbTable+";";
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            int n=0;
            if (rs.next()) {
                n=rs.getInt(1);
            }
            System.out.println("size : "+n);
            return n;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
        return 0;
    }
    
    public int maxEntry() {
        try {
            String sql="select max(id) from "+dbTable+";";
            ps=conn.prepareStatement(sql);
            rs=ps.executeQuery();
            int n=0;
            if (rs.next()) {
                n=rs.getInt(1);
            }
            System.out.println("size : "+n);
            return n;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                rs.close();
                ps.close();
            }
            catch (Exception e) {
                
            }
        }
        return 0;
    }
    
    private void closeApp() {
        runPython("shutdown");
        System.exit(0);
    }
    
    private void executeCommand(String command) {
        System.out.println("=>"+command);
        //String tmpCom=command;
        
        if (currMode==defaultMode) {
            setParentPanelTo(blankPanelIndex);
            txtOutput.setText("");
            command=command.toLowerCase();
            if (command.contains("open")) {
                try {
                    showResponse("Opening Application");
                    String app = command.substring(command.indexOf("open")+5);
                    ProcessBuilder pb;
                    pb = new ProcessBuilder (app);
                    Process p = pb.start();
                }
                catch (Exception e) {
                    System.out.println("err");
                }
            }
            else if (command.contains("email")) {
                showResponse("Opening Application");
                currMode=emailMode;
                setParentPanelTo(emailPanelIndex);
            }
            else if (command.contains("weather")) {
                //currMode=weatherMode;
                showResponse("Loading weather data");
                getWeatherAPI("allahabad");
            }
            else if (command.contains("search")) {
                if (command.contains("search for")) {
                    command=command.substring(command.indexOf("search for")+"search for".length());
                }
                else {
                    command=command.substring(command.indexOf("search")+"search".length());
                }
                execURL(command);
            }
            else if(command.contains("reminder")||command.contains("alarm")) {
                showResponse("I will remind you of that");
                setParentPanelTo(reminderPanelIndex);
                
            }
            else if (command.contains("note")) {
                showResponse("Making note for you");
                setParentPanelTo(notesPanelIndex);
            }
            else if (command.contains("news")||command.contains("headlines")) {
                loadNewsData();
            }
            else {
                runPython(command);
            }
        }
        else if (currMode==emailMode) {
            // send an email
            if (command.equals("cancel email")) {
                currMode=defaultMode;
                txtEmailText.setText("");
                txtEmailTo.setText("");
                setParentPanelTo(blankPanelIndex);
            }
            else if (addRecipients) {
                txtEmailTo.setText(txtEmailTo.getText()+command+" ");
            }
            else {
                txtEmailText.append(command+"\n");
            }
            
        }
        
    }
    
    public void showResponse(String data) {
        if (data.equals("")) {
            return;
        }
        txtOutput.setText(data);
        Thread speak = new Thread(new Runnable() {
            @Override
            public void run() {
                botVoice.sayWords(data);
            }
        });
        speak.start();
        
    }
    
    public void initPython() {
        try {
            pb = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+chatBotPy);
            pb.directory(new File(path));
            pro = pb.start();
            br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            
            Thread output = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String out="";
                        while ((out=br.readLine())!=null) {
                            System.out.println(chatBotPy+" : "+out);
                        }
                        pro.waitFor();
                        int x = pro.exitValue();
                        if (x == 0) {
                            System.out.println(chatBotPy+" : done successful");
                        }
                        else {
                            System.out.println(chatBotPy+" : done with error");
                            
                            BufferedReader r = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
                            String errMsg;
                            while ((errMsg = r.readLine()) != null)
                            {
                                String msg=errMsg + System.getProperty("line.separator");
                                System.out.println(msg);
                                System.out.println("Compiler : "+out);
                                //textDebug.append(errMsg+"\n");
                            }
                            //long t2=System.currentTimeMillis();
                            //textDebug.append("Compilation Time : "+((t2-t1)/1000.0)+" sec\n");
                            
                            
                        }
                    }
                    catch (Exception e) {
                        
                    }
                    
                }
            });
            output.start();
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public void runPython(String msg) {
        txtOutput.setText(" < Thinking > ");
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                long t1=System.currentTimeMillis();
                try {
                    String inp=msg+"\n";
                    byte buffer[] = inp.getBytes();
                    OutputStream os =(pro.getOutputStream());
                    os.write(buffer,0,buffer.length);
                    os.flush();

                    Thread output = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ProcessBuilder pblocal;
                                pblocal = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+getReplyPy);
                                pblocal.directory(new File(path));
                                Process plocal = pblocal.start();
                                BufferedReader brlocal = new BufferedReader(new InputStreamReader(plocal.getInputStream()));
                                String out="";
                                while ((out=brlocal.readLine())!=null) {
                                    System.out.println(getReplyPy+" "+out);
                                    if (out.equals("failed to load results")) {
                                        txtOutput.setText("Loading better results from wikipedia");
                                        System.out.println("loading results from wiki");
                                        getWikiAPI(msg);
                                    }
                                    else {
                                        showResponse(out);
                                    }
                                }
                                plocal.waitFor();
                            }
                            catch (Exception e) {
                                System.out.println(e);
                                e.printStackTrace();
                            }

                        }
                    });
                    output.start();
                }
                catch (Exception e) {
                    System.out.println("Error in running");
                }
            }
        });
        loader.start();
    }
    
    public String getChatBotResponse(String msg) {
        //txtOutput.setText(" < Thinking > ");
        //long t1=System.currentTimeMillis();
        try {
            System.out.println("Data received : "+msg);
            String inp=msg+"\n";
            byte buffer[] = inp.getBytes();
            OutputStream os =(pro.getOutputStream());
            os.write(buffer,0,buffer.length);
            os.flush();

            try {
                ProcessBuilder pblocal;
                pblocal = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+getReplyPy);
                pblocal.directory(new File(path));
                Process plocal = pblocal.start();
                BufferedReader brlocal = new BufferedReader(new InputStreamReader(plocal.getInputStream()));
                String out="";
                String data="";
                while ((out=brlocal.readLine())!=null) {
                    System.out.println(getReplyPy+" "+out);
                    data=data+out+"\n";
                }
                plocal.waitFor();
                socketData=data;
            }
            catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
            return socketData;
        }
        catch (Exception e) {
            System.out.println("Error in running");
            return null;
        }
    }
    

    public void exec(String command) {
        Runtime runtime = Runtime.getRuntime(); 
        try {
            runtime.exec(command);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void execURL(String command) {
        command=command.replaceAll(" ","+");
        String query="www.google.com/search?q=";
        query=query+command;
        Runtime runtime = Runtime.getRuntime(); 
        String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", query};
        try {
            runtime.exec(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainActivity().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    private javax.swing.JLabel btnClose;
    private javax.swing.JLabel btnEmailClose;
    private javax.swing.JLabel btnNewsClose;
    private javax.swing.JLabel btnNewsNext;
    private javax.swing.JLabel btnNewsPrev;
    private javax.swing.JButton btnNoteCancel;
    private javax.swing.JButton btnNoteOpen;
    private javax.swing.JButton btnNoteSave;
    private javax.swing.JButton btnNoteShow;
    private javax.swing.JLabel btnRemClose;
    private javax.swing.JButton btnRemSet;
    private javax.swing.JButton btnSendEmail;
    private javax.swing.JLabel btnSpeak;
    private com.toedter.calendar.JDateChooser datePicker;
    private javax.swing.JLabel dayIcon;
    private javax.swing.JPanel emailPanel;
    private javax.swing.JSpinner hourPicker;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSpinner minPicker;
    private javax.swing.JPanel newsPanel;
    private javax.swing.JLabel nightIcon;
    private javax.swing.JPanel notesPanel;
    private javax.swing.JPanel parentPanel;
    private javax.swing.JPanel reminderPanel;
    private javax.swing.JLabel titleBar;
    private javax.swing.JLabel txtDayPhrase;
    private javax.swing.JTextArea txtEmailText;
    private javax.swing.JTextField txtEmailTo;
    private javax.swing.JTextField txtInput;
    private javax.swing.JLabel txtMaxTemp;
    private javax.swing.JLabel txtMinTemp;
    private javax.swing.JTextArea txtNewsHead;
    private javax.swing.JTextArea txtNewsText;
    private javax.swing.JLabel txtNightPhrase;
    private javax.swing.JTextField txtNoteFileName;
    private javax.swing.JTextArea txtNoteText;
    private javax.swing.JTextField txtOutput;
    private javax.swing.JTextField txtRemMsg;
    private javax.swing.JLabel txtWeatherInfo;
    private javax.swing.JTextArea txtWikiOutput;
    private javax.swing.JPanel weatherPanel;
    private javax.swing.JPanel wikiPanel;
    // End of variables declaration//GEN-END:variables
}

class GoogleResults {

    private ResponseData responseData;
    public ResponseData getResponseData() { return responseData; }
    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
    public String toString() { return "ResponseData[" + responseData + "]"; }

    static class ResponseData {
        private List<Result> results;
        public List<Result> getResults() { return results; }
        public void setResults(List<Result> results) { this.results = results; }
        public String toString() { return "Results[" + results + "]"; }
    }

    static class Result {
        private String url;
        private String title;
        public String getUrl() { return url; }
        public String getTitle() { return title; }
        public void setUrl(String url) { this.url = url; }
        public void setTitle(String title) { this.title = title; }
        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
    }
}