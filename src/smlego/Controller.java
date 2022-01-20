package smlego;


import java.io.File;
import java.io.IOException;
import java.awt.Desktop;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


/**
 * @author Abel
 */
public class Controller {
    int vectorLength = 0;
    String[] vector;
    boolean mute = false;
    private int bpm;
    Metronome metronomeMetronomo;
    Player playerSeq;
    private String display;
    boolean classe = false;
    boolean playingMetronomo = false; //indica que o metronomo esta sendo ou não executado antes de ter um padrão rítmico
    boolean playingPattern = false;
    boolean classMetro = false; // indica se foi criada uma classe
    boolean mutePattern = false; // indica se o padrão está habilitado
    boolean muteMetronome = false; // indica que o metronomo está desabilitado
    boolean playingMutePattern = false; // habilita e dasabilita o "metronomo", deixando apenas o "padrão"
    boolean playButton = false;// botão "play" acionado antes do "metronomo"
    boolean playMetronomo = false;// botão "metronomo" acionado antes do "play"
    boolean playPatternMetronomo = false;// habilita e desabilita o "padrão", deixando apenas o metronomo
    boolean startSequenceMetronome = false;
    private static final String ACTION_1 = "\uD83D\uDD14";
    private static final String ACTION_2 = "❌";


    /**************************************************************************************** */

    @FXML
    private Button switchButton;

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("secondary");
    }


    @FXML
    private Button buttonStyleOne;

    @FXML
    private Button buttonStyleTwo;

    @FXML
    private Button buttonStyleThree;

    @FXML
    private AnchorPane frame;

    @FXML
    private TextField filePath;

    @FXML
    private TextField displayMetronomo;

    @FXML
    private Button helpButton;

    @FXML
    private Button close;

    @FXML
    private Button fileButton;

    @FXML
    private Button minimize;

    @FXML
    private Button metronome1;

    @FXML
    private TextArea sheetPanel;

    @FXML
    private Button stop;

    @FXML
    private Button play;

    @FXML
    private Button reset;

    @FXML
    private Button imprimscnir3d;

    @FXML
    private Button bpmInc;

    @FXML
    private Button bpmDec;

    @FXML
    public Slider sliderBPM;

    @FXML
    private Button btnOutputHtml;


    private Stage stage;

    private Scene scene;

    private Parent root;

    private double xOffset;
    private double yOffset;

    /*************************    MÉTODOS DOS AÇÕES DA INTERFACE  ********************************** */

    @FXML
    void initialize() throws MidiUnavailableException, InvalidMidiDataException {
        play.setDisable(true);
        stop.setDisable(true);
        reset.setDisable(true);
        btnOutputHtml.setDisable(true);
        displayBPM();
        createMetronome();
    }

    public void switchToPrimary(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("primary.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSecondary(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("secondary.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void bpmSlider(MouseEvent event) {
        this.bpm = (int) sliderBPM.getValue();
        displayBPM();
        //System.out.println("Controller-> BPMSLIDER");
        // //System.out.println("bpmSlider = " + sliderBPM.getValue());

        // testa se existe o padrão rítmico
        if (classe == true)playerSeq.changeTempo(getBPM());
        else metronomeMetronomo.changeTempoMetronomo(getBPM());

    }

    @FXML
    void openHelp(ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(System.getProperty("user.dir") + "/ajuda/artigo.pdf");
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }

    void openIndex() {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(System.getProperty("user.dir") + "/index/index.html");
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for html
            }
        }
    }

    @FXML
    void metronomeButton(ActionEvent event) throws InvalidMidiDataException, MidiUnavailableException {

        // verifica se já existe um padrão rítmico
        // toca e inicia o metronomo
        if (!getClasse()) {
            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //System.out.println("Controller-> metronomeButton-> classe if =" + classe);
            if (!getPlayingMetronomo()) {
                setBPM();
                metronomeMetronomo.setBpmMetronomo(getBPM());
                setPlayingMetronomo(true);
                if (metronome1.getText() == "\ud83d\udd14"){
                    metronome1.setText("\u274c");
                } else {
                    metronome1.setText("PARAR METRÔNOMO");
                }
                metronomeMetronomo.playMetronomo();

            } else {
                metronomeMetronomo.stopMetronomoPlayback();
                if (metronome1.getText() == "\u274c"){
                    metronome1.setText("\ud83d\udd14");
                } else {
                    metronome1.setText("LIGAR METRÔNOMO");
                }
                setPlayingMetronomo(false);
            }
        }

        // manipula a tecla "metronomo" quando existe um padrão rítmico
        else {

            if(getMuteMetronome()){
                //System.out.println("9090909009090909900990990099009900990909909090909009099090090990");
                // indica o se a track metronomo vai se habilitado ou desabilitado
                setMuteMetronome (playerSeq.muteMetronome(playerSeq.getSeq()));
                metronome1.setText(ACTION_2);

            }
            else {
                //System.out.println("123321123321123321123321123321123321123321123");
                setMuteMetronome (playerSeq.muteMetronome(playerSeq.getSeq()));
                metronome1.setText(ACTION_1);
            }
        }

    }

    @FXML
    void playMusic(ActionEvent event) throws InvalidMidiDataException, MidiUnavailableException {

        // habilita o botão metronomo
        metronome1.setDisable(false);

        // indica que o metronomo está desabilitado
        setMuteMetronome(false);

        //indica o mute do metronomo
        playerSeq.mute = false;

        // desabilita o botão Reset
        reset.setDisable(true);

        // indica o bpm
        playerSeq.setBPM(getBPM());

        // inicia a sequencia
        playerSeq.startSequence();

        // verifica se o metronomo está habilitado ou desabilitado
        if(getMuteMetronome()){

            // indica o se a track metronomo vai ser habilitado ou desabilitado
            setMuteMetronome (playerSeq.muteMetronome(playerSeq.getSeq()));

            // altera o texto do botão "metronomo" para "Metronomo OFF"
            metronome1.setText(ACTION_2);

        }
        else {

            // indica o se a track metronomo vai ser habilitado ou desabilitado
            setMuteMetronome (playerSeq.muteMetronome(playerSeq.getSeq()));

            // altera o texto do botão "metronomo" para "Metronomo ON"
            metronome1.setText(ACTION_1);
        }

        // desabilita o botão "play"
        play.setDisable(true);

        // desabilita o botão "stop"
        stop.setDisable(false);

    }

    @FXML
    void resetMusic(ActionEvent event) throws InvalidMidiDataException {

        setPlayButton(false);
        setPlayPatternMetronomo(false);

        if (playerSeq.getSeq() != null) {
            playerSeq.stopPlayback(/* playerSeq.getSeq() */); // Chamada do Player
            // Metronome.stopPlayback(this.seqMetronome); // Chamada do Metronomo
        }

        setClasse(false);
        setPlayingMetronomo(false);
        setMuteMetronome(true);
        resetFields();
        play.setDisable(true);
        stop.setDisable(true);
        reset.setDisable(true);
        btnOutputHtml.setDisable(true);
        fileButton.setDisable(false);
        metronome1.setDisable(false);
        metronome1.setText(ACTION_1);


    }

    @FXML
    void stopMusic(ActionEvent event) throws InvalidMidiDataException {

        // pára o playback
        playerSeq.stopPlayback(/* playerSeq.getSeq() */);

        // habilita o botão "play"
        play.setDisable(false);

        // indica se o padrão está tocando
        setPlayingPattern(false);

        // habilitao o botão "stop"
        stop.setDisable(true);

        // habilita o botão "reset"
        reset.setDisable(false);

        // desabilita o botão "metronomo"
        metronome1.setDisable(true);

    }



    @FXML
    void setStyleOne(ActionEvent event) {

    }

    @FXML
    void setStyleThree(ActionEvent event) {

    }

    @FXML
    void setStyleTwo(ActionEvent event) {

    }

    @FXML
    void print3dButton(ActionEvent event) {
        // to do
    }

    @FXML
    void closeFrame(ActionEvent event) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
        System.exit(0);
        playerSeq.closeSeq();
        metronomeMetronomo.closeSequencerMetronome();


    }

    @FXML
    void minimizeFrame(ActionEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void selectFile(ActionEvent event) throws MidiUnavailableException {
        fileButton.setDisable(true);
        FileChooser file = new FileChooser();
        file.getExtensionFilters().add(new ExtensionFilter("Arquivos MusicXML", "*.musicxml"));
        File f = file.showOpenDialog(null);

        if (f != null) {
            filePath.setText(f.getAbsolutePath());
            play.setDisable(false);
            reset.setDisable(false);
            stop.setDisable(true);
            fileButton.setDisable(true);
            btnOutputHtml.setDisable(false);
            config();
            classe = true;
        } else
            fileButton.setDisable(false);

        if (playingMetronomo == true){
            //System.out.println("&*(&*(&*(&*(&*(&*(&*(&*(&*(&*(&*(*(&*(&(");
            metronomeMetronomo.stopMetronomoPlayback();
            metronome1.setText(ACTION_1);

        }
        metronome1.setDisable(true);


    }

    @FXML
    void bpmInc(ActionEvent event) {
        if (this.bpm == 240) {
            displayBPM();
        } else {
            this.bpm++;
            displayBPM();
            //System.out.println("Controller-> BPM inc ");
        }
        setBpmSlider();

        // testa se existe o padrão rítmico
        if (classe == true){
            // altera o tempo quando o padrão está em execução
            playerSeq.changeTempo(getBPM());
        }
        else {
            // altera o tempo quando o metronomo está em execução
            metronomeMetronomo.changeTempoMetronomo(getBPM());

        }


    }

    @FXML
    void bpmDec(ActionEvent event) {
        if (this.bpm == 33) {
            displayBPM();
        } else {
            this.bpm--;
            displayBPM();
        }
        setBpmSlider();

        // testa se existe o padrão rítmico
        if (classe == true){
            // altera o tempo quando o padrão está em execução
            playerSeq.changeTempo(getBPM());
        }
        else {
            // altera o tempo quando o metronomo está em execução
            metronomeMetronomo.changeTempoMetronomo(getBPM());

        }
    }

    @FXML
    void outputHtml(ActionEvent event) throws MidiUnavailableException {
        if (playerSeq != null) {
            config();
        }
    }


    @FXML
    void displayBPM() {
        if (this.bpm == 0) {
            this.bpm = (int) sliderBPM.getValue();
            display = String.valueOf(bpm);
            displayMetronomo.setText(display);
        } else {
            // displayMetronomo.setText(String.valueOf((int)sliderBPM.getValue()));
            displayMetronomo.setText(String.valueOf(bpm));
        }
    }

    /*************************    MÉTODOS GET / SET    ************************************ */

    public void setClasse(boolean classe) {
        this.classe = classe;
    }

    public boolean getClasse (){
        return classe;
    }

    public void setPlayingMetronomo (boolean playingMetronomo){
        this.playingMetronomo = playingMetronomo;
    }

    public boolean getPlayingMetronomo() {
        return playingMetronomo;
    }

    public void setBpmSlider() {
        sliderBPM.setValue(bpm);
    }

    public void setPlayButton(boolean playButton) {
        this.playButton = playButton;
    }

    public boolean getPlayButton() {
        return playButton;
    }

    public void setPlayPatternMetronomo(boolean playPatternMetronomo) {
        this.playPatternMetronomo = playPatternMetronomo;
        //System.out.println("playPatternMetronomo = " + getPlayPatternMetronomo());
    }

    public boolean getPlayPatternMetronomo() {
        return playPatternMetronomo;
    }

    public boolean getMuteMetronome() {
        return muteMetronome;
    }

    public void setMuteMetronome(boolean muteMetronome) {
        this.muteMetronome = muteMetronome;
    }

    public void setBPM(/* int bpm */) {
        this.bpm = (int) sliderBPM.getValue();
        //System.out.println("Controller->  setBPM = " + bpm);
    }

    public int getBPM() {
        return (int) sliderBPM.getValue();
    }

    private void setVector(String[] vector) {
        //System.out.println("Controller-> set vector");
        this.vector = vector;
    }

    public String[] getVector() {
        //System.out.println("Controller-> get vector");
        return vector;
    }

    private void setVectorLength(int vectorLength) {
        //System.out.println("Controller-> set VectorLength");
        this.vectorLength = vectorLength;
    }

    public int getVectorLength() {
        //System.out.println("Controller-> set VectorLength");
        return vectorLength;
    }

    public void setPlayingMutePattern(boolean playingMutePattern) {
        this.playingMutePattern = playingMutePattern;
    }

    public boolean getPlayingMutePattern() {
        return playingMutePattern;
    }

    public void setPlayingPattern(boolean playingPattern) {
        this.playingPattern = playingPattern;
    }

    public boolean getPlayingPatern() {
        return playingPattern;
    }

    /****************************    MÉTODOS DIVERSOS      *************************************** */

    private void config() throws MidiUnavailableException {
//        //System.out.println("Controller-> INICIOU CONFIG");
//        //System.out.println("Controller-> bpm = " + (int) sliderBPM.getValue());
        setBPM(/*(int)sliderBPM.getValue()*/);
        MusicXML musicXML = new MusicXML();
        Parser parser = new Parser();
        Note note = new Note();
        Output output = new Output();
        playerSeq = new Player();
        /*
         * if (classe == false){ //System.out.println("BOOLEAN CLASSE = " + classe);
         * playerMetronomo = new Player(); classe = true;
         * //System.out.println("BOOLEAN CLASSE = " + classe); }
         */
        // var metronome = new Metronome();
        // BUSCA O CAMINHO DO ARQUIVO QUE SERA TRADUZIDO NO FORMATO .musicXML
        String fileName = filePath.getText();
        // BUSCA O NUMERO DE LINHAS DO ARQUIVO
        int lines = musicXML.getLines(fileName);
        // CRIA UM ARRAY COM O CONTEUDO DO ARQUIVO
        String[][] array = musicXML.readFile(fileName, lines);
        // BUSCA O TAMANHO DO VETOR
        // int vectorLenght = note.getVectorLenght(lines, array);
        //System.out.println("Controller-> vai entrar no set vector");
        setVectorLength(note.getVectorLenght(lines, array));
        setVector(parser.vectorCreate(lines, array, getVectorLength()));

        // int tam = parser.getVectorLenght();
        String outArray[][] = new String[2][(getVectorLength() / 2) + 1];
        outArray = parser.setSimpleArray(getVector(), getVectorLength());
        // exibe na GUI o conteúdo do vetor

        sheetPanel.selectAll();
        sheetPanel.replaceSelection("");

        for (int i = 0, pos0 = 0, pos1 = 0; i < 4; i++) {
            if (i > 0) {
                sheetPanel.appendText("\n"); // evita quebra de linha antes da primeira linha
                sheetPanel.appendText("    "); // espaço vazio da partitura do ó
            }

            for (int x = 0; pos0 < outArray[0].length; pos0++) {
                sheetPanel.appendText(outArray[0][pos0] + " ");
                if (x == 4) { // x = 4 define o measure
                    x = 0;
                    pos0++;
                    break;
                }
                x++;
            }

            sheetPanel.appendText("\n");
            for (int x = 1; pos1 < outArray[1].length; pos1++) {
                sheetPanel.appendText(outArray[1][pos1] + " ");
                if (x == 4) { // x = 4 define o measure
                    x = 0;
                    pos1++;
                    break;
                }
                x++;
            }
        }
        // output.getAtributos(lines, array);
        //System.out.println("Controller-> CRIA ARQUIVO");
        output.createFile(lines, array, outArray);
        // //System.out.println("Controller-> SEQUENCIA DO METRONOMO =" +
        // seqMetronome);
        // CRIAR A SEQUENCIA DO METRONOMO
//        //System.out.println("Controller-> TAMANHO VETOR= " + getVectorLength());
//        //System.out.println("Controller-> VETOR =" + vector[0]);

        // seqMetronome = metronome.createSequence(getVectorLength(), getVector());
        // //System.out.println("Controller-> SEQUENCIA DO METRONOMO = " +
        // seqMetronome);
        //System.out.println("Controller->config() bpm= " + getBPM());
        playerSeq.create(getVectorLength(), getVector());
        openIndex();
    }

    private void resetFields() {
        sheetPanel.selectAll();
        sheetPanel.replaceSelection("");
        filePath.setText("");
        playerSeq.closeSeq();
    }

    private void createMetronome() throws MidiUnavailableException, InvalidMidiDataException {
        metronomeMetronomo = new Metronome();
        metronomeMetronomo.sequenceMetronomo();
    }
}
