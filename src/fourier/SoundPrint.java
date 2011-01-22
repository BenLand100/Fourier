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

import java.text.DecimalFormat;
import java.util.TreeSet;

/**
 *
 * @author benland100
 */
public class SoundPrint {

    public final int length;
    public final double[] freqs, amps, phases;
    private final TreeSet<FourierData> data;

    public static class FourierData implements Comparable<FourierData> {

        public final int idx;
        public final double freq;
        public final double amp;
        public final double phase;

        public FourierData(int idx, double freq, double amp, double phase) {
            this.idx = idx;
            this.freq = freq;
            this.amp = amp;
            this.phase = phase;
        }

        public int compareTo(FourierData sample) {
            int ampcomp = (int) Math.signum(amp - sample.amp);
            return ampcomp == 0 ? (int) Math.signum(freq - sample.freq) : ampcomp;
        }
    }

    public SoundPrint(TreeSet<FourierData> data) {
        this.data = new TreeSet<FourierData>(data);
        length = data.size();
        freqs = new double[length];
        amps = new double[length];
        phases = new double[length];
        for (FourierData fourier : data) {
            freqs[fourier.idx] = fourier.freq;
            amps[fourier.idx] = fourier.amp;
            phases[fourier.idx] = fourier.phase;
        }
    }

    public SoundPrint(double[] freqs, double[] amps, double[] phases) {
        this.freqs = freqs;
        this.amps = amps;
        this.phases = phases;
        length = freqs.length;
        if (length != amps.length || length != phases.length) {
            throw new IllegalArgumentException("Lengths of fourier data arrays are not equal");
        }
        data = new TreeSet<FourierData>();
        for (int i = 0; i < length; i++) {
            data.add(new FourierData(i, freqs[i], amps[i], phases[i]));
        }
    }

    public TreeSet<FourierData> threshold(double threshold) {
        TreeSet<FourierData> result = new TreeSet<FourierData>();
        for (FourierData fourier : data.descendingSet()) {
            if (fourier.amp < threshold) {
                break;
            }
            result.add(fourier);
        }
        return result;
    }

    public TreeSet<FourierData> getPeaks(double threshold) {
        TreeSet<FourierData> result = new TreeSet<FourierData>();
        double amp;
        int idx;
        for (FourierData fourier : data.descendingSet()) {
            amp = fourier.amp;
            idx = fourier.idx;
            if (amp < threshold) {
                break;
            }
            if ((idx > 0 && amp > amps[idx - 1]) && (idx < length - 1 && amp > amps[idx + 1])) {
                result.add(fourier);
            }
        }
        return result;
    }

    public void dumpPeaks(double threshold) {
        DecimalFormat onedec = new DecimalFormat("#0.0");
        DecimalFormat fourdec = new DecimalFormat("#0.0000");
        for (FourierData fourier : getPeaks(threshold)) {
            System.out.println("Freq: " + onedec.format(fourier.freq) + "\tAmplitude: " + fourdec.format(fourier.amp) + "\tPhase: " + fourdec.format(fourier.phase));
        }
    }

    public void dumpData() {
        DecimalFormat onedec = new DecimalFormat("#0.0");
        DecimalFormat fourdec = new DecimalFormat("#0.0000");
        for (FourierData fourier : data) {
            System.out.println("Freq: " + onedec.format(fourier.freq) + "\tAmplitude: " + fourdec.format(fourier.amp) + "\tPhase: " + fourdec.format(fourier.phase));
        }
    }

    public static int[] genSound(TreeSet<FourierData> data, double seconds) {
        final float framerate = 44100f;
        final float amplitude = 1610612735.625f / 16f;
        final int length = (int) (0.5 + seconds * framerate);
        final int[] samples = new int[length];
        for (FourierData fourier : data) {
            double factor = fourier.freq * 2D * Math.PI / framerate;
            double phase = fourier.phase;
            double amp = fourier.amp;
            for (int i = 0; i < length; i++) {
                samples[i] += amplitude * amp * Math.sin(factor * i + phase);
            }
        }
        return samples;
    }

    public static String dump(TreeSet<FourierData> data) {
        StringBuilder result = new StringBuilder();
        for (FourierData fourier : data) {
            result.append(fourier.freq).append('\t').append(fourier.amp).append('\t').append(fourier.phase).append('\n');
        }
        result.deleteCharAt(result.lastIndexOf("\n"));
        return result.toString();
    }

    public static TreeSet<FourierData> read(String data) {
        TreeSet<FourierData> result = new TreeSet<FourierData>();
        String[] parts = data.split("\n|\t");
        if (parts.length % 3 != 0) throw new RuntimeException("invalid data");
        for (int i = 0; i < parts.length; i+=3) {
            result.add(new FourierData(i/3,Double.parseDouble(parts[i+0]),Double.parseDouble(parts[i+1]),Double.parseDouble(parts[i+2])));
        }
        return result;
    }


}
