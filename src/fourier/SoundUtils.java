/**
 *  Copyright 2010 by Benjamin J. Land (a.k.a. BenLand100)
 *
 *  This file is part of Fourier.
 *
 *  CPascal is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CPascal is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Fourier. If not, see <http://www.gnu.org/licenses/>.
 */

package fourier;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author benland100
 */
public class SoundUtils {
    
    public final static float SAMPLE_RATE = 44100f;
    public final static int SAMPLE_SIZE = 32;
    public final static int CHANNELS = 1;
    public final static boolean SIGNED = true;
    public final static boolean BIG_ENDIAN = true;
    
    public static void play(int[] samples) {
        Player play = new Player();
        play.play(samples);
    }
    
    public static void playAndWait(int[] samples) {
        Player play = new Player();
        play.play(samples);
        try { play.join(); } catch (Exception e) {}
    }
    
    public static SoundPrint fourier(int[] samples, double min_freq, double max_freq) {
        final int low = (int)(samples.length/SAMPLE_RATE*min_freq);
        final int high = (int)(samples.length/SAMPLE_RATE*max_freq);
        final int len = high - low + 1;
        final double fact = Math.PI*2D/samples.length;
        double[] dft_real = new double[len];
        double[] dft_imag = new double[len];
        double[] freqs = new double[len];
        double[] amplitudes = new double[len];
        double[] phases = new double[len];
        double amplitude, real, imag;
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (int i = low, c = 0; i <= high; i++, c++) {
            real = imag = 0;
            for (int j = 0; j < samples.length; j++) {
                real += samples[j]*Math.cos(fact*i*j);
                imag -= samples[j]*Math.sin(fact*i*j);
            }
            dft_real[c] = real;
            dft_imag[c] = imag;
            freqs[c] = (double)(i+low)/samples.length*SAMPLE_RATE;
            phases[c] = Math.atan2(imag, real);
            amplitudes[c] = amplitude = Math.sqrt(real*real+imag*imag);
            if (amplitude > max) max = amplitude;
            if (amplitude < min) min = amplitude;
        }
        double range = max-min;
        for (int i = 0; i < len; i++) {
            amplitudes[i] = (amplitudes[i]-low)/range;
        }
        return new SoundPrint(freqs,amplitudes,phases);
    }

    public static class Recorder extends Thread {

        private boolean recording = false;
        private ByteArrayOutputStream data = new ByteArrayOutputStream();
        private int[] samples = new int[0];

        public Recorder() {
            super("Recorder");
        }

        public boolean recording() {
            return recording;
        }

        public void start() {
            throw new RuntimeException("Do not attempt to start a Recorder manually");
        }

        public void startRecording() {
            if (recording) {
                throw new RuntimeException("Recorder is currently recording and cannot start again");
            } else {
                samples =  new int[0];
                recording = true;
                super.start();
            }
        }

        public void stopRecording() {
            if (recording) {
                recording = false;
                try { join(); } catch (Exception e) {}
            } else {
                throw new RuntimeException("Recorder is not currently recording and cannot stop");
            }
        }

        public int[] getSamples() {
            return samples;
        }

        public void run() {
            try {
                data.reset();
                AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIG_ENDIAN);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                byte[] buffer = new byte[(int)format.getSampleRate() * format.getFrameSize()];
                int count;
                while (recording) {
                    count = line.read(buffer, 0, buffer.length);
                    if (count > 0) {
                      data.write(buffer, 0, count);
                    }
                }
                line.flush();
                line.stop();
                line.close();
                byte[] bytes = data.toByteArray();
                samples = new int[bytes.length / 4];
                for (int i = 0; i < bytes.length; i+=4) {
                    samples[i/4] = ((bytes[i] & 0xFF) << 24) | ((bytes[i+1] & 0xFF) << 16) | ((bytes[i+2] & 0xFF) << 8) | (bytes[i+3] & 0xFF);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class Player extends Thread {

        private boolean playing = false;
        private int[] samples;

        public Player() {
            super("Player");
        }

        public boolean playing() {
            return playing;
        }

        public void start() {
            throw new RuntimeException("Do not attempt to start a Player manually");
        }

        public void play(int[] samples) {
            if (playing) {
                throw new RuntimeException("Player is already playing and cannot play again");
            } else {
                this.samples = samples;
                playing = true;
                super.start();
            }
        }

        public void run() {
            byte[] bytes = new byte[samples.length * 4];
            for (int i = 0; i < samples.length; i++) {
                bytes[i*4+0] = (byte) ((samples[i] >> 24) & 0xFF);
                bytes[i*4+1] = (byte) ((samples[i] >> 16) & 0xFF);
                bytes[i*4+2] = (byte) ((samples[i] >> 8) & 0xFF);
                bytes[i*4+3] = (byte) ((samples[i] >> 0) & 0xFF);
             }
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIG_ENDIAN);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                int remaining = bytes.length;
                while (remaining > 0) {
                    remaining -= line.write(bytes, bytes.length - remaining, remaining);
                }
                line.drain();
                line.stop();
                line.close();
                playing = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
