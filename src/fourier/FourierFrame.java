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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author benland100
 */
public class FourierFrame extends JFrame implements ActionListener,ChangeListener {

    private JButton play,dump;
    private JRadioButton peak, limit;
    private JSlider threshold;
    private SoundView sview;
    private FourierView fview;
    private final SoundPrint print;
    private final double[] freqs, amps, phases;
    private int[] samples;

    public FourierFrame(SoundPrint print) {
        super("Fourier View");
        freqs = print.freqs;
        amps = print.amps;
        phases = print.phases;
        this.print = print;
        samples = print.genSound(print.getPeaks(0.2), 1d);
        init();
    }

    public FourierFrame(double[] freqs, double[] amps, double[] phases) {
        super("Fourier View");
        this.freqs = freqs;
        this.amps = amps;
        this.phases = phases;
        print = new SoundPrint(freqs,amps,phases);
        samples = print.genSound(print.getPeaks(0.2), 1d);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel center = new JPanel(new GridLayout(2,1));
        fview = new FourierView();
        fview.setFourierData(freqs, amps, phases);
        center.add(fview);
        sview = new SoundView();
        sview.setSamples(samples);
        center.add(sview);
        add(center,BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout());
        play = new JButton("Play Generated");
        play.addActionListener(this);
        south.add(play);
        dump = new JButton("Dump Data");
        dump.addActionListener(this);
        south.add(dump);
        peak = new JRadioButton("Peaks Only");
        peak.addActionListener(this);
        peak.setSelected(true);
        south.add(peak);
        limit = new JRadioButton("Simple Limit");
        limit.addActionListener(this);
        limit.setSelected(false);
        south.add(limit);
        add(south,BorderLayout.SOUTH);

        threshold = new JSlider(JSlider.VERTICAL,0,1000,200);
        threshold.addChangeListener(this);
        add(threshold,BorderLayout.EAST);

        setSize(600,600);
    }

    private void play() {
        play.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                SoundUtils.playAndWait(samples);
                play.setEnabled(true);
            }
        };
        t.start();
    }

    private void dump() {
        System.out.println("==============================================================");
        System.out.println(SoundPrint.dump(print.getPeaks(threshold.getValue()/1000D)));
        System.out.println("==============================================================");
    }

    private void update() {
        setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                if (peak.isSelected()) {
                    samples = print.genSound(print.getPeaks(threshold.getValue()/1000D), 1d);
                } else {
                    samples = print.genSound(print.threshold(threshold.getValue()/1000D), 1d);
                }
                sview.setSamples(samples);
                setEnabled(true);
            }
        };
        t.start();
    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == peak) {
            peak.setSelected(true);
            limit.setSelected(false);
            update();
        } else if (o == limit) {
            limit.setSelected(true);
            peak.setSelected(false);
            update();
        } else if (o == play) {
            play();
        } else if (o == dump) {
            dump();
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (threshold.getValueIsAdjusting()) return;
        update();
    }

}
