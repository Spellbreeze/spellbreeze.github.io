package application;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.SysexMessage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MidiMusicPlayer {

    private Sequencer sequencer;
    private File currMIDI;
    private InputStream is;
    private Receiver receiver;
    private boolean muted;

    public MidiMusicPlayer() throws MidiUnavailableException, IOException,
            InvalidMidiDataException {
        muted = false;
        sequencer = MidiSystem.getSequencer();
        currMIDI = new File("./src/Assets/Music MIDIs/mound.mid");

        sequencer.open();
        InputStream is = new BufferedInputStream(new FileInputStream(currMIDI));
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        sequencer.setSequence(is);
        sequencer.start();

        receiver = sequencer.getTransmitters().iterator().next().getReceiver();

        Double gain = 0.15D * 127;
        byte volumeByte = (byte) gain.intValue();
        MidiMessage volumeMessage = new SysexMessage(SysexMessage.SYSTEM_EXCLUSIVE,
                new byte[] {0x7F, 0x7F, 0x04, 0x01, 0x00, volumeByte }, 6);
        receiver.send(volumeMessage, -1);
    }

    /**
     * Closes the Music player
     */
    public void close() {
        sequencer.close();
    }

    /**
     * Toggles Mute
     * @throws InvalidMidiDataException if the midi message is invalid
     */
    public void toggleMute() throws InvalidMidiDataException {
        // gain is a value between 0 and 1 (loudest)
        Double gain = (muted) ? 0.15D * 127 : 0.0D * 127;
        byte volumeByte = (byte) gain.intValue();

        MidiMessage volumeMessage = new SysexMessage(SysexMessage.SYSTEM_EXCLUSIVE,
                new byte[] {0x7F, 0x7F, 0x04, 0x01, 0x00, volumeByte}, 6);
        receiver.send(volumeMessage, -1);

        muted ^= true;
    }
}
