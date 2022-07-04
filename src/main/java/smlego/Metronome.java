package smlego;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

public class Metronome implements MetaEventListener {
    boolean play;
    Sequence seqMetronome;
    private int bpmMetronome;
    private static Sequencer sequencerMetronome;

    public void sequenceMetronome() {
        if (play == false) {

            try {
                openSequencer();
                setSeqMetronome(createSequenceMetronome());
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void openSequencer() throws MidiUnavailableException {
        sequencerMetronome = MidiSystem.getSequencer();
        sequencerMetronome.open();
        sequencerMetronome.addMetaEventListener(this);
    }

    private Sequence createSequenceMetronome() {
        try {
            Sequence seqMetronome = new Sequence(Sequence.PPQ, 2);// PPQ= Pulse per quarter - 2 quer dizer pulsos por
                                                                  // seminima
            Track TrackMetronome = seqMetronome.createTrack();

            for (int i = 0; i < 2; i++) {

                if (i == 0) {
                    addMetronomeEventON(TrackMetronome, i + 1);

                } else {
                    addMetronomeEventOFF(TrackMetronome, i + 1);
                }
            }
            return seqMetronome;
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void setSeqMetronome(Sequence seqMetronome) {
        this.seqMetronome = seqMetronome;
    }

    private Long extractedTick(int tick) {
        return Long.valueOf(tick);
    }

    public void closeSequencerMetronome() {
        sequencerMetronome.close();
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

    public void playMetronome() throws InvalidMidiDataException {
        sequencerMetronome.setSequence(getSeqMetronome());
        sequencerMetronome.setTempoInBPM(getBpmMetronome());
        sequencerMetronome.start();
    }

    public Sequence getSeqMetronome() {
        return seqMetronome;
    }

    public void setBpmMetronome(int bpmMetronome) {
        this.bpmMetronome = bpmMetronome;
    }

    public int getBpmMetronome() {
        return bpmMetronome;
    }

    protected void stopMetronomePlayback() {
        sequencerMetronome.getSequence();
        sequencerMetronome.stop();
        sequencerMetronome.setTickPosition(0);
    }

    public void closeSeqMetronome() {
        sequencerMetronome.close();
    }

    public void changeTempoMetronome(int bpm) {
        double lengthCoeff = bpm / sequencerMetronome.getTempoInBPM();
        sequencerMetronome.setLoopStartPoint((long) (sequencerMetronome.getLoopStartPoint() * lengthCoeff));
        sequencerMetronome.setLoopEndPoint((long) (sequencerMetronome.getLoopStartPoint() * lengthCoeff));
        sequencerMetronome.setTempoInBPM(bpm);
        setBpmMetronome(bpm);
    }

    public void meta(MetaMessage message) {
        if (message.getType() != 47) {
            return;
        }
        doLoop();
    }

    private void doLoop() {
        if (sequencerMetronome == null || !sequencerMetronome.isOpen()) {
            return;
        }
        sequencerMetronome.setTickPosition(0);
        sequencerMetronome.setLoopStartPoint(0);
        sequencerMetronome.start();
        sequencerMetronome.setTempoInBPM(getBpmMetronome());
        sequencerMetronome.setTempoFactor(1);
    }

}
