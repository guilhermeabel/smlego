package smlego;

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

    private void setSeq(Sequence seq) {
        this.seq = seq;
    }

    public Sequence getSeq() {
        return seq;
    }

    public void setBPM(int bpm) {
        this.bpm = bpm;
    }

    public int getBPM() {
        return bpm;
    }

    public void create(int vectorLenght, String[] vetor) {

        this.vectorLength = vectorLenght;
        this.vector = vetor;

        try {
            openSequencer();
            setSeq(createSequence());
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
            Sequence seq = new Sequence(Sequence.PPQ, 2);
            Track TrackPlayer = seq.createTrack();
            Track TrackMetronomo = seq.createTrack();
            for (int i = 0; i < vectorLength; i++) {

                if (vector[i].equals("\u25CF")) {
                    addNoteEventON(TrackPlayer, i + 1);
                }
                if (vector[i].equals("\u25CB")) {
                    addNoteEventOFF(TrackPlayer, i + 1);
                }
                if (i % 2 == 0) {
                    addMetronomeEventON(TrackMetronomo, i + 1);
                } else {
                    addMetronomeEventOFF(TrackMetronomo, i + 1);
                }
            }

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
        mainTrack.add(event);
    }

    private Long extractedTick(int tick) {
        return Long.valueOf(tick);
    }

    private void addNoteEventOFF(Track mainTrack, long tick) throws InvalidMidiDataException {
        long y = Long.valueOf(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, 9, 37, 100);
        MidiEvent event = new MidiEvent(message, y);
        mainTrack.add(event);
    }

    private void addMetronomeEventON(Track mainTrackMetronome, int tick) throws InvalidMidiDataException {
        long y = extractedTick(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_ON, 10, 100, 40);
        MidiEvent event = new MidiEvent(message, y);
        mainTrackMetronome.add(event);
    }

    private void addMetronomeEventOFF(Track mainTrackMetronome, int tick) throws InvalidMidiDataException {
        long y = extractedTick(tick);
        ShortMessage message = new ShortMessage(ShortMessage.NOTE_OFF, 10, 100, 40);
        MidiEvent event = new MidiEvent(message, y);
        mainTrackMetronome.add(event);
    }

    public void startSequence() throws InvalidMidiDataException {
        sequencer.setSequence(getSeq());
        sequencer.setTempoInBPM(getBPM());
        sequencer.start();
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
    }

    public void setMutePattern(boolean mutePattern) {
        this.mutePattern = mutePattern;
    }

    public boolean getMutePattern() {
        return mutePattern;
    }

    protected boolean mutePattern() throws InvalidMidiDataException {
        getMutePattern();
        sequencer.setTrackMute(0, mutePattern);
        return mutePattern;
    }

    protected boolean muteMetronome(Sequence seq) throws InvalidMidiDataException {
        if (mute == false) {
            mute = true;
        } else {
            mute = false;
        }
        if (seq == null) {

        } else {
            sequencer.setTrackMute(1, mute);
        }
        return mute;
    }

    public void closeSeq() {
        sequencer.close();
    }

    public void changeTempo(int bpm) {

        double lengthCoeff = bpm / sequencer.getTempoInBPM();
        sequencer.setLoopStartPoint((long) (sequencer.getLoopStartPoint() * lengthCoeff));
        sequencer.setLoopEndPoint((long) (sequencer.getLoopStartPoint() * lengthCoeff));
        sequencer.setTempoInBPM(bpm);
        setBPM(bpm);
    }

}
