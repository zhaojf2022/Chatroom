package com.example.nettychatdemo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import javax.swing.text.html.ListView;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTask extends Application implements Serializable, EventHandler<ActionEvent>  {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Integer count = 2;
    private final String[] buttontxt = {"发送","发送文件","视频通话","音频通话"};

    private final ImageView imageView = new ImageView();

    private final Text text = new Text("用户聊天窗口");
    private final String user_name = "李四";
    private final Button[] button = new Button[buttontxt.length];
    private TextArea textarea = new TextArea();
    private final FileChooser fileChooser = new FileChooser();

    private Alert alertss = new Alert(AlertType.INFORMATION);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Object> contentLabelList = new ArrayList<>();
    private ExecutorService forkJoin = Executors.newFixedThreadPool(10);
//	private volatile DatagramChannel datagram = DatagramChannel.open();
//	private volatile ServerSocketChannel server = ServerSocketChannel.open();

    private ImageView video_image = new ImageView();
    private ImageView audio_image = new ImageView();
    private Stage video_stage = UI.getVideoUi("客户端视频通话",video_image, this);
    private Stage audio_stage = UI.getAudioUi("客户端音频通话",audio_image, this);
    private CTasks tasks = new CTasks(this);
    private Stage stage;


    public ClientTask() throws IOException {
//		datagram.socket().setReceiveBufferSize(Integer.MAX_VALUE);
//		datagram.socket().setSendBufferSize(Integer.MAX_VALUE);
//		datagram.socket().setBroadcast(true);
//		datagram.socket().setReuseAddress(true);

        fileChooser.setInitialDirectory(new File("D:\\桌面"));
        fileChooser.setTitle("选择文件");
//		fileChooser.getExtensionFilters().addAll(
//				new ExtensionFilter("JPG","*.jpg"),
//				new ExtensionFilter("PNG","*.png"),
//				new ExtensionFilter("GIF","*.gif"),
//				new ExtensionFilter("PSD","*.psd"),
//				new ExtensionFilter("PM4","*.mp4"),
//				new ExtensionFilter("MP3","*.mp3")
//				);

        for (int a = 0; a < buttontxt.length; a++){
            button[a] = new Button(buttontxt[a]);
            button[a].setOnAction(this);
        }
        textarea = new TextArea();
        textarea.setTooltip(new Tooltip("此处为发送内容"));
        textarea.setPromptText("请输入发送内容");
        textarea.setFocusTraversable(false);

        textarea[0].setOnDragOver(x->{
            if(x.getGestureSource()!=textarea[0])
                x.acceptTransferModes(TransferMode.ANY);
        });
        textarea[0].setOnDragDropped(x->{
            Dragboard dragboard = x.getDragboard();
            List<File> file = dragboard.getFiles();
            for (File file2 : file) {
                File[] files = file2.isDirectory()?file2.listFiles(b->b.length()<1024*1024*50):new File[]{file2};
                for (File file3 : files)send_files(file3);
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            switch (((Button)event.getSource()).getText()) {
                case "发送":
                    send_byte(new Datas(DATA_TYPE.TEXT,user_name,textarea[0].getText().getBytes()));
                    break;
                case "选择文件":
                    File files = fileChooser.showOpenDialog(stage);
                    if(files==null)break;
                    forkJoin.execute(()->send_files(files));
                    break;
                case "视频通话":
                    Datas datas = Datas.builder().user_name(user_name).type(DATA_TYPE.VIDEO).build();
                    send_byte(datas);
                    break;
                case "音频通话":
                    Datas datas2 = Datas.builder().user_name(user_name).type(DATA_TYPE.AUDIO).build();
                    send_byte(datas2);
                    break;
                case "结束视频通话":
                    tasks.getService().getVideo_stop().stop();
                    Datas datas3 = Datas.builder().user_name(user_name).type(DATA_TYPE.VIDEO_END).build();
                    send_byte(datas3);
                    if(video_stage.isShowing())video_stage.close();
                    //tasks.setVideo_stop(false);
                    break;
                case "结束音频通话":

                    Thread thread2 = tasks.getService().getAudio_stop();
                    if(thread2!=null && thread2.isAlive())thread2.stop();

                    Datas datas4 = Datas.builder().user_name(user_name).type(DATA_TYPE.AUDIO_END).build();
                    send_byte(datas4);
                    if(audio_stage.isShowing())audio_stage.close();
                    break;
                case "通话录音":
                    tasks.setIs_luzhi(true);
                    System.out.println(tasks.getIs_luzhi());
                    Button buttons = ((Button)event.getSource());
                    buttons.setText("正在录制");
                    buttons.setStyle("-fx-background-color:orangered;-fx-font-color:while");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        alertss.setTitle("客户端系统提示");
        contentLabelList.add(sdf.format(new Date(System.currentTimeMillis())) + ":");
        ListView listView = new ListView<Object>(FXCollections.observableArrayList(contentLabelList));
        listView.getStylesheets().add(getClass().getResource("/javafx/listview.css").toExternalForm());
        HBox hroot = new HBox(10);
        hroot.getChildren().addAll(button[0],button[1],button[2],button[3]);
        hroot.setAlignment(Pos.CENTER_RIGHT);
        //HBox.setMargin(button[1], new Insets(0, 0, 0, 20));
        VBox vroot = new VBox(20);
        vroot.getChildren().addAll(text, listView, textarea[0], hroot);
        vroot.setAlignment(Pos.CENTER);
        vroot.setPadding(new Insets(10, 10, 10, 10));
        //VBox.setMargin(textarea[0], new Insets(20, 20, 0, 0));
        KeyCodeCombination keys = new KeyCodeCombination(KeyCode.ENTER);
        Scene scene = new Scene(vroot,500,600);
        scene.getAccelerators().put(keys,()->button[0].fire());
        scene.getStylesheets().add(getClass().getResource("/javafx/javafx.css").toExternalForm());
        primaryStage.getIcons().add(image[0]);
        //primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("客户端");
        primaryStage.setScene(scene);
        primaryStage.show();
        forkJoin.execute(tasks);
    }

    /**
     * 发送Datas
     * @param data 封装的数据
     *
     */
    //private InetSocketAddress inet = new InetSocketAddress("127.0.0.1", 8083);
    public void send_byte(Datas data){
        //System.out.println("发送");
        //SocketChannel socketChannel = tasks.getClient_socket();
        Channel channel = tasks.getTcp_channel();
        if(channel!=null && channel.isOpen() && channel.isActive()){
            System.out.println("socketChannel "+channel);
            //channel.writeAndFlush(ByteBuffer.wrap(Service.obj_to_byte(data)));
            channel.writeAndFlush(data);
        }
    }

    /**
     * 发送文件数据
     * @param files
     *
     */
    public void send_files(File files){
        Boolean is_image = files.getName().matches(".*\\.(jpg|png|gif|jpeg)$");
        try(ByteArrayOutputStream outputs = new ByteArrayOutputStream();
            FileInputStream inputs = new FileInputStream(files)) {
            Datas datas= Datas.builder().type(is_image?DATA_TYPE.IMAGE:DATA_TYPE.FILE).file_name(files.getName())
                    .data_size(files.length()).user_name(user_name).build();
            if(files.length()<1024*1024*10){
                Files.copy(files,outputs);
                datas.setDatas(outputs.toByteArray());
                send_byte(datas);
            }else{
                send_byte(datas);
                byte[] bytes = new byte[1024*1024];
                for(int data = -1;(data=inputs.read(bytes,0,bytes.length))>-1;){
                    Datas datass= Datas.builder()
                            .type(DATA_TYPE.FILE).file_name(files.getName()).data_size(files.length())
                            .isfen(1).datas(bytes).user_name(user_name).build();
                    send_byte(datass);
                }
                send_byte(Datas.builder().type(DATA_TYPE.FILE).isend(1).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 分段发送数据
     * @param files
     * @param bytes 文件数据
     * @param skip 分段大小
     */
    private void send_byte_limit(File files, byte[] bytes, int skip){
        int start = 0,end = 0;
        while(start<bytes.length){
            end = start + skip;
            if (end > bytes.length)end = bytes.length;
            byte[] inputs = Arrays.copyOfRange(bytes,start,end);
            start = end;
            Datas datas= Datas.builder()
                    .type(DATA_TYPE.FILE)
                    .file_name(files.getName())
                    .data_size(files.length())
                    .isfen(1)
                    .datas(inputs)
                    .user_name(user_name)
                    .build();
            send_byte(datas);
        }
        send_byte(Datas.builder().type(DATA_TYPE.FILE).isend(1).build());
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }