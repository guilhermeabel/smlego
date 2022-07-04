package smlego;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ViewController {

    // BEGIN - FXML PROPERTIES

    @FXML
    private Button bpmDecBtn;

    @FXML
    private Button bpmIncBtn;

    @FXML
    private Button contrastBtn;

    @FXML
    private TextField displayBpmField;

    @FXML
    private Button fileBtn;

    @FXML
    private TextField filePathField;

    @FXML
    private Button helpBtn;

    @FXML
    private Pane mainPane;

    @FXML
    private Button metronomeBtn;

    @FXML
    private Button outputHtmlBtn;

    @FXML
    private Button playBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Pane sheetPane;

    @FXML
    private TextArea sheetTextarea;

    @FXML
    private Slider sliderBpm;

    @FXML
    private Button stopBtn;

    // END - FXML PROPERTIES

    // BEGIN - OTHER PROPERTIES

    int vectorLength = 0;
    String[] vector;
    boolean mute = false;
    private int bpm = 120;
    Metronome metronomeMetronome;
    Player playerSeq;
    private String display;
    boolean classe = false;
    boolean playingMetronome = false;
    boolean playingPattern = false;
    boolean classMetro = false; // indica se foi criada uma classe
    boolean mutePattern = false; // indica se o padrão está habilitado
    boolean muteMetronome = false; // indica que o metronomo está desabilitado
    boolean playingMutePattern = false; // habilita e dasabilita o "metronomo", deixando apenas o "padrão"
    boolean playButton = false; // botão "play" acionado antes do "metronomo"
    boolean playMetronome = false; // botão "metronomo" acionado antes do "play"
    boolean playPatternMetronome = false; // habilita e desabilita o "padrão", deixando apenas o metronomo
    boolean startSequenceMetronome = false;
    final String PLAY_METRONOME_LABEL = "TOCAR METRÔNOMO";
    final String STOP_METRONOME_LABEL = "PARAR METRÔNOMO";

    // END - OTHER PROPERTIES

    // BEGIN - FXML METHODS

    @FXML
    void initialize() throws MidiUnavailableException, InvalidMidiDataException {
        playBtn.setDisable(true);
        stopBtn.setDisable(true);
        resetBtn.setDisable(true);
        outputHtmlBtn.setDisable(true);
        displayBPM();
        createMetronome();
    }

    @FXML
    void contrastBtnAction(ActionEvent event) {
        StyleController.setMainStyle(mainPane);
    }

    @FXML
    void helpBtnAction(ActionEvent event) {
        try {
            if (Desktop.isDesktopSupported()) {
                File myFile = new File(System.getProperty("user.dir") + "/ajuda/artigo.pdf");
                Desktop.getDesktop().open(myFile);
            }
        } catch (IOException ex) {
            // no application registered for PDFs
        }
    }

    @FXML
    void selectFile(ActionEvent event) throws MidiUnavailableException {
        fileBtn.setDisable(true);
        FileChooser file = new FileChooser();
        file.getExtensionFilters().add(new ExtensionFilter("Arquivos MusicXML", "*.musicxml"));
        File f = file.showOpenDialog(null);

        if (f != null) {
            filePathField.setText(f.getAbsolutePath());
            playBtn.setDisable(false);
            resetBtn.setDisable(false);
            stopBtn.setDisable(true);
            fileBtn.setDisable(true);
            outputHtmlBtn.setDisable(false);
            config();
            classe = true;
        } else
            fileBtn.setDisable(false);

        if (playingMetronome == true) {
            metronomeMetronome.stopMetronomePlayback();
            metronomeBtn.setText(PLAY_METRONOME_LABEL);

        }
        metronomeBtn.setDisable(true);
    }

    @FXML
    void playMusic(ActionEvent event) throws InvalidMidiDataException, MidiUnavailableException {
        metronomeBtn.setDisable(false);
        setMuteMetronome(false);
        playerSeq.mute = false;
        resetBtn.setDisable(true);
        playerSeq.setBPM(getBPM());

        playerSeq.startSequence();

        if (getMuteMetronome()) {
            setMuteMetronome(playerSeq.muteMetronome(playerSeq.getSeq()));
            metronomeBtn.setText(STOP_METRONOME_LABEL);

        } else {
            setMuteMetronome(playerSeq.muteMetronome(playerSeq.getSeq()));
            metronomeBtn.setText(PLAY_METRONOME_LABEL);
        }

        playBtn.setDisable(true);
        stopBtn.setDisable(false);

    }

    @FXML
    void stopMusic(ActionEvent event) throws InvalidMidiDataException {
        playerSeq.stopPlayback();
        playBtn.setDisable(false);
        setPlayingPattern(false);
        stopBtn.setDisable(true);
        resetBtn.setDisable(false);
        metronomeBtn.setDisable(true);
    }

    @FXML
    void resetMusic(ActionEvent event) throws InvalidMidiDataException {
        setPlayButton(false);
        setPlayPatternMetronome(false);

        if (playerSeq.getSeq() != null) {
            playerSeq.stopPlayback();
        }

        setClasse(false);
        setPlayingMetronome(false);
        setMuteMetronome(true);
        resetFields();
        playBtn.setDisable(true);
        stopBtn.setDisable(true);
        resetBtn.setDisable(true);
        outputHtmlBtn.setDisable(true);
        fileBtn.setDisable(false);
        metronomeBtn.setDisable(false);
        metronomeBtn.setText(PLAY_METRONOME_LABEL);
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

        if (classe == true) {
            playerSeq.changeTempo(getBPM());
        } else {
            metronomeMetronome.changeTempoMetronome(getBPM());
        }
    }

    @FXML
    void bpmInc(ActionEvent event) {
        if (this.bpm == 240) {
            displayBPM();
        } else {
            this.bpm++;
            displayBPM();
        }
        setBpmSlider();

        if (classe == true) {
            playerSeq.changeTempo(getBPM());
        } else {
            metronomeMetronome.changeTempoMetronome(getBPM());
        }
    }

    @FXML
    void metronomeAction(ActionEvent event) throws InvalidMidiDataException, MidiUnavailableException {
        if (!getClasse()) {
            if (!getPlayingMetronome()) {
                setBPM();
                metronomeMetronome.setBpmMetronome(getBPM());
                setPlayingMetronome(true);
                metronomeBtn.setText(STOP_METRONOME_LABEL);
                metronomeMetronome.playMetronome();

            } else {
                metronomeMetronome.stopMetronomePlayback();
                metronomeBtn.setText(PLAY_METRONOME_LABEL);
                setPlayingMetronome(false);
            }
        } else {
            if (getMuteMetronome()) {
                setMuteMetronome(playerSeq.muteMetronome(playerSeq.getSeq()));
                metronomeBtn.setText(STOP_METRONOME_LABEL);

            } else {
                setMuteMetronome(playerSeq.muteMetronome(playerSeq.getSeq()));
                metronomeBtn.setText(PLAY_METRONOME_LABEL);
            }
        }
    }

    @FXML
    void bpmSlider(MouseEvent event) {
        this.bpm = (int) sliderBpm.getValue();
        displayBPM();
        if (classe == true)
            playerSeq.changeTempo(getBPM());
        else
            metronomeMetronome.changeTempoMetronome(getBPM());
    }

    @FXML
    void outputHtml(ActionEvent event) throws MidiUnavailableException {
        if (playerSeq != null) {
            config();
            openIndex();
        }
    }

    @FXML
    void displayBPM() {
        if (this.bpm == 0) {
            this.bpm = (int) sliderBpm.getValue();
            display = String.valueOf(bpm);
            displayBpmField.setText(display);
        } else {
            displayBpmField.setText(String.valueOf(bpm));
        }
    }

    // END - FXML METHODS

    // BEGIN - OTHER METHODS

    void openIndex() {
        try {
            if (Desktop.isDesktopSupported()) {
                File myFile = new File(System.getProperty("user.dir") + "/index/index.html");
                Desktop.getDesktop().open(myFile);
            }
        } catch (IOException ex) {
            // no application registered for html
        }
    }

    public void setClasse(boolean classe) {
        this.classe = classe;
    }

    public boolean getClasse() {
        return classe;
    }

    public void setPlayingMetronome(boolean isPlayingMetronome) {
        this.playingMetronome = isPlayingMetronome;
    }

    public boolean getPlayingMetronome() {
        return playingMetronome;
    }

    public void setBpmSlider() {
        sliderBpm.setValue(bpm);
    }

    public void setPlayButton(boolean playButton) {
        this.playButton = playButton;
    }

    public boolean getPlayButton() {
        return playButton;
    }

    public void setPlayPatternMetronome(boolean playPatternMetronome) {
        this.playPatternMetronome = playPatternMetronome;
    }

    public boolean getPlayPatternMetronome() {
        return playPatternMetronome;
    }

    public boolean getMuteMetronome() {
        return muteMetronome;
    }

    public void setMuteMetronome(boolean muteMetronome) {
        this.muteMetronome = muteMetronome;
    }

    public void setBPM() {
        this.bpm = (int) sliderBpm.getValue();
    }

    public int getBPM() {
        return (int) sliderBpm.getValue();
    }

    private void setVector(String[] vector) {
        this.vector = vector;
    }

    public String[] getVector() {
        return vector;
    }

    private void setVectorLength(int vectorLength) {
        this.vectorLength = vectorLength;
    }

    public int getVectorLength() {
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

    private void config() throws MidiUnavailableException {
        setBPM();
        MusicXML musicXML = new MusicXML();
        Parser parser = new Parser();
        Note note = new Note();
        Output output = new Output();
        playerSeq = new Player();
        String fileName = filePathField.getText();
        int lines = musicXML.getLines(fileName);
        String[][] array = musicXML.readFile(fileName, lines);
        setVectorLength(note.getVectorLenght(lines, array));
        setVector(parser.vectorCreate(lines, array, getVectorLength()));

        String outArray[][] = new String[2][(getVectorLength() / 2) + 1];
        outArray = parser.setSimpleArray(getVector(), getVectorLength());

        sheetTextarea.selectAll();
        sheetTextarea.replaceSelection("");

        for (int i = 0, pos0 = 0, pos1 = 0; i < 4; i++) {
            if (i > 0) {
                sheetTextarea.appendText("\n");
                sheetTextarea.appendText("   ");
            }

            for (int x = 0; pos0 < outArray[0].length; pos0++) {
                sheetTextarea.appendText(outArray[0][pos0] + " ");
                if (x == 4) {
                    x = 0;
                    pos0++;
                    break;
                }
                x++;
            }

            sheetTextarea.appendText("\n");
            for (int x = 1; pos1 < outArray[1].length; pos1++) {
                sheetTextarea.appendText(outArray[1][pos1] + " ");
                if (x == 4) {
                    x = 0;
                    pos1++;
                    break;
                }
                x++;
            }
        }

        output.createFile(lines, array, outArray);
        playerSeq.create(getVectorLength(), getVector());
    }

    private void resetFields() {
        sheetTextarea.selectAll();
        sheetTextarea.replaceSelection("");
        filePathField.setText("");
        playerSeq.closeSeq();
    }

    private void createMetronome() throws MidiUnavailableException, InvalidMidiDataException {
        metronomeMetronome = new Metronome();
        metronomeMetronome.sequenceMetronome();
    }

    // END - OTHER METHODS

}
