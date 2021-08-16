package smlego;


import static javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

public class Player implements MetaEventListener {

    boolean mute = false;
    boolean mutePattern = false;
    boolean toca = false;
    Controller Controller = new Controller();

    private static Sequencer sequencer;

    int bpm;
    int bpmMetronomo;
    int vectorLength;
    String[] vector;
    Sequence seq;

    private void setSeq(Sequence seq){
        this.seq = seq;
        System.out.println("Player-> setSeq seq =" + seq);
    }

    public Sequence getSeq(){
        System.out.println("Player-> getSeq seq =" + seq);
        return seq;
    }


    public void setBPM(int bpm){
        this.bpm = bpm;
        System.out.println("Player->setBPM = " + bpm);
    }

    public int getBPM(){
        return bpm;
    }

    public /*Sequence*/ void create(int vectorLenght, String[] vetor) {

        System.out.println("***CRIA A SEQUENCIA****");
        this.vectorLength = vectorLenght;
        this.vector = vetor;

        try {
            openSequencer();//PADRÃO RITMICO
            System.out.println("***ABRIU O SEQUENCER DO PADRÃO***");
            //openSequencerMetronome();// METRONOMO
            setSeq(createSequence());
            //Sequence seqMetronome = createSequenceMetronome();
            //startSequence(getSeq());
            //seqMetronome = startSequenceMetronome(seqMetronome);
            //return seq;
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public void openSequencer() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.addMetaEventListener(this);
    }
    public Sequence createSequence() {
        try {
            Sequence seq = new Sequence(Sequence.PPQ, 2);// PPQ= Pulse per quarter - 2 quer dizer pulsos por seminima
            //Sequence seqMetronome = new Sequence(Sequence.PPQ, 2);
            Track TrackPlayer = seq.createTrack();// biblioteca javax
            Track TrackMetronomo = seq.createTrack();
            //Track main_track_metronome = seq_metronome.createTrack();
            //int x = vectorLength +1;
            //int i = 0;
            System.out.println("Player-> vectorLength= " + vectorLength);

            for(int i=0; i < vectorLength ; i++){

                if(vector[i].equals("\u25CF")){
                    System.out.println("Player-> i NA CONTRUÇÃO ON= " + i );
                    addNoteEventON(TrackPlayer,i+1);
                    System.out.println("Player-> Full= "+ vector[i] );
                }
                if(vector[i].equals("\u25CB")) {
                    addNoteEventOFF(TrackPlayer, i+1);
                    System.out.println("Player-> i NA CONTRUÇÃO OFF= " + i );
                    System.out.println("Player-> Empty= "+ vector[i]);
                }
                if (i%2 == 0){
                    System.out.println("Player-> i%2 = " + (i%2));
                    addMetronomeEventON(TrackMetronomo, i+1);
                    System.out.println("Player-> ADD METRO EVENT ON");
                }
                else {
                    addMetronomeEventOFF(TrackMetronomo, i+1);
                    System.out.println("Player-> ADD METRO EVENT OFF");
                }
            }

            System.out.println("Player->createSequence Sequencia = " + seq);
            return seq;

        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void addNoteEventON(Track mainTrack, int tick) throws InvalidMidiDataException {
        long y = extractedTick(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, 9, 37, 100);
        MidiEvent event = new MidiEvent(message, y);
        System.out.println("Player-> TICK on = " + y);
        //System.out.println("SEQUENCER TICK ON = " + mainTrack.y());
        mainTrack.add(event);
    }

    private Long extractedTick(int tick) {
        return Long.valueOf(tick);
    }

    private void addNoteEventOFF(Track mainTrack, long tick) throws InvalidMidiDataException {
        long y = Long.valueOf(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, 9, 37, 100);
        MidiEvent event = new MidiEvent(message, y);
        System.out.println("Player-> TICK off= " + y);
        //System.out.println("SEQUENCER TICK OFF = " + mainTrack.ticks());
        mainTrack.add(event);
    }
    //ADICIONA NOTAS DENTRO DA TRACK
    private void addMetronomeEventON(Track mainTrackMetronome, int tick) throws InvalidMidiDataException {
        long y = extractedTick(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, 10, 100, 40);
        MidiEvent event = new MidiEvent(message, y);
        mainTrackMetronome.add(event);
    }

    //ADICONA PAUSA DENTRO DA TRACK
    private void addMetronomeEventOFF(Track mainTrackMetronome, int tick) throws InvalidMidiDataException {
        long y = extractedTick(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, 10, 100, 40);
        MidiEvent event = new MidiEvent(message, y);
        mainTrackMetronome.add(event);
        //System.out.println("ADD EVENTO Off = " + mainTrackMetronome);
    }

    public void startSequence() throws InvalidMidiDataException {

        System.out.println("Player-> startSequencer-> getFirst = " + Controller.getPlayButton());

        sequencer.setSequence(getSeq());
        System.out.println("Player-> startSequence-> getSeq() = " + getSeq());
        System.out.println("Player-> startSequence-> getBPM() =  " + getBPM());

        sequencer.setTempoInBPM(getBPM());
        //executar o pattern normalmente
        System.out.println("Player-> startSequence-> Controller.getFirst() = " + Controller.getPlayButton());
        sequencer.start();



        //if (getMutePattern() == true);
            /*
            if (getMutePattern() == true){
                // verificar se o mutePattern está habilitado para tocar o pattern só com o metronomo
                setMutePattern(mutePattern(getSeq()));
            }
            */

        //muteMetronome(getSeq());
        //System.out.println("Player-> startSequence-> mute =" + mute);
        long y = getSeq().getTickLength();
        System.out.println("Player-> startSequence-> TICK LENGTH = " + y);
        //return seq;

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");





    }

    public void meta(MetaMessage message) {
        if (message.getType() != 47) {
            return;
        }
        doLoop();
    }

    private void doLoop() {
        if (sequencer == null || !sequencer.isOpen()) {
            return;
        }
        sequencer.setTickPosition(0);
        sequencer.setLoopStartPoint(0);
        sequencer.start();
        sequencer.setTempoInBPM(getBPM());
        sequencer.setTempoFactor(1);

    }


    protected void stopPlayback() throws InvalidMidiDataException {
        sequencer.getSequence();
        sequencer.stop();
        sequencer.setTickPosition(0);
        //mute =  false;

    }

    public void setMutePattern(boolean mutePattern){
        this.mutePattern = mutePattern;
    }

    public boolean getMutePattern(){
        return mutePattern;
    }

    protected boolean mutePattern () throws InvalidMidiDataException {

        getMutePattern();

        sequencer.setTrackMute(0, mutePattern);

        return mutePattern;
    }

    protected boolean muteMetronome(Sequence seq) throws InvalidMidiDataException {


        if (mute == false) {

            mute = true;
        }
        else {

            mute = false;
        }
        if (seq == null){

        }else sequencer.setTrackMute(1, mute);


        return mute;

    }

    public void closeSeq() {
        sequencer.close();
    }

    public void changeTempo (int bpm){

        double lengthCoeff = bpm/sequencer.getTempoInBPM();
        sequencer.setLoopStartPoint((long)(sequencer.getLoopStartPoint()*lengthCoeff));
        sequencer.setLoopEndPoint((long)(sequencer.getLoopStartPoint()*lengthCoeff));
        sequencer.setTempoInBPM(bpm);
        setBPM(bpm);
    }



}
