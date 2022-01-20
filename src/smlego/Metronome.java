package smlego;


//import static javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;
//import javax.swing.event.SwingPropertyChangeSupport;

public class Metronome implements MetaEventListener {

    //private static Sequencer sequencer;
    //int bpm;
    //int vectorLength;
    //String[] vector;
    //Sequence seqMetronome;
    boolean play;
    Sequence seqMetronome;
    private int bpmMetronomo;
    private static Sequencer sequencerMetronome;

    /****************************************************************************************/

    public void sequenceMetronomo() {
        ////System.out.println("metronomeButton->getBPM = " + bpmMetronomo);

        if(play == false){

            try {
                openSequencer();//METRNÔNOMO
                //System.out.println("***ABRIU O SEQUENCER DO METRONOMO na classe Metronome***");
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

            Sequence seqMetronomo = new Sequence(Sequence.PPQ, 2);// PPQ= Pulse per quarter - 2 quer dizer pulsos por seminima
            Track TrackMetronomo = seqMetronomo.createTrack();

            for(int i=0; i < 2 ; i++){

                if(i == 0){
                    //System.out.println("Metronome-> i NA CONTRUÇÃO MetronomoON= " + i );
                    addMetronomeEventON(TrackMetronomo,i+1);

                }else {
                    addMetronomeEventOFF(TrackMetronomo, i+1);
                    //System.out.println("Metronome-> i NA CONTRUÇÃO Metronomo OFF= " + i );
                }
            }
            //System.out.println("Metronome->createSequence Metronomo = " + seqMetronomo);
            return seqMetronomo;
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private void setSeqMetronome (Sequence seqMetronome){
        this.seqMetronome = seqMetronome;
        //System.out.println("Metronome-> setSeqMetronome = " + seqMetronome);
    }

    private Long extractedTick(int tick) {
        return Long.valueOf(tick);
    }

    public void closeSequencerMetronome() {
        sequencerMetronome.close();
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
        ////System.out.println("ADD EVENTO Off = " + mainTrackMetronome);
    }

    public void playMetronomo() throws InvalidMidiDataException {

        sequencerMetronome.setSequence(getSeqMetronome());
        //System.out.println("Metronome->Sequence metronome->getSeq() = " + getSeqMetronome());
        sequencerMetronome.setTempoInBPM(getBpmMetronomo());
        //System.out.println("Metronome->sequenceMetro-> bpmMetronomo = " + getBpmMetronomo());
        sequencerMetronome.start();
        //sequencerMetronome.setLoopCount(LOOP_CONTINUOUSLY);
        long y = getSeqMetronome().getTickLength();
        //System.out.println("Metronome-> TICK LENGTH = " + y);
        //return seq;

    }

    public Sequence getSeqMetronome(){
        //System.out.println("Metronome-> getSeqMetronome =" + seqMetronome);
        return seqMetronome;
    }

    public void setBpmMetronomo(int bpmMetronomo){
        this.bpmMetronomo = bpmMetronomo;
        //System.out.println("Metronome->setBPM = " + bpmMetronomo);
    }

    public int getBpmMetronomo(){
        return bpmMetronomo;
    }

    protected void stopMetronomoPlayback() {
        sequencerMetronome.getSequence();
        sequencerMetronome.stop();
        sequencerMetronome.setTickPosition(0);

    }

    public void closeSeqMetronome() {
        sequencerMetronome.close();
    }

    public void changeTempoMetronomo (int bpm){
        //System.out.println("/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*//*/*/*/*/*/");
        double lengthCoeff = bpm/sequencerMetronome.getTempoInBPM();
        sequencerMetronome.setLoopStartPoint((long)(sequencerMetronome.getLoopStartPoint()*lengthCoeff));
        sequencerMetronome.setLoopEndPoint((long)(sequencerMetronome.getLoopStartPoint()*lengthCoeff));
        sequencerMetronome.setTempoInBPM(bpm);
        setBpmMetronomo(bpm);
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
        sequencerMetronome.setTempoInBPM(getBpmMetronomo());
        sequencerMetronome.setTempoFactor(1);

    }

}
