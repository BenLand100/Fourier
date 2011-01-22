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

/**
 *
 * @author benland100
 */
public class CaptureFrame extends JFrame implements ActionListener {

    public final static float FREQ_MIN = 100f;
    public final static float FREQ_MAX = 11000f;

    private SoundUtils.Recorder recorder = new SoundUtils.Recorder();
    private SoundView view = new SoundView();
    private JButton start = new JButton("Start");
    private JButton stop = new JButton("Stop");
    private JButton play = new JButton("Play");
    private JButton fourier = new JButton("Fourier");

    public static void main(String[] args) {
        CaptureFrame frame = new CaptureFrame();
        frame.setVisible(true);
    }

    public CaptureFrame() {
        super("Sound Capture");
        setLayout(new BorderLayout());
        add(view,BorderLayout.CENTER);
        JPanel south = new JPanel(new GridLayout());
        start.addActionListener(this);
        south.add(start);
        stop.addActionListener(this);
        stop.setEnabled(false);
        south.add(stop);
        play.addActionListener(this);
        play.setEnabled(false);
        south.add(play);
        fourier.addActionListener(this);
        fourier.setEnabled(false);
        south.add(fourier);
        add(south,BorderLayout.SOUTH);
        setSize(600,300);
    }

    private void start() {
        if (recorder.recording()) {
            System.out.println("Already Recording!");
            return;
        }
        fourier.setEnabled(false);
        play.setEnabled(false);
        start.setEnabled(false);
        stop.setEnabled(true);
        view.setSamples(new int[0]);
        recorder.startRecording();
    }

    private void stop() {
        if (!recorder.recording()) {
            System.out.println("Not Recording!");
            return;
        }
        recorder.stopRecording();
        play.setEnabled(true);
        fourier.setEnabled(true);
        start.setEnabled(true);
        stop.setEnabled(false);
        view.setSamples(recorder.getSamples());
        recorder = new SoundUtils.Recorder();
    }

    private void play() {
        play.setEnabled(false);
        fourier.setEnabled(false);
        start.setEnabled(false);
        stop.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                SoundUtils.playAndWait(view.getSelection());
                play.setEnabled(true);
                fourier.setEnabled(true);
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        };
        t.start();
    }

    private void fourier() {
        play.setEnabled(false);
        fourier.setEnabled(false);
        start.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                FourierFrame frame = new FourierFrame(SoundUtils.fourier(view.getSelection(), FREQ_MIN, FREQ_MAX));
                frame.setVisible(true);
                play.setEnabled(true);
                fourier.setEnabled(true);
                start.setEnabled(true);
            }
        };
        t.start();
    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == start) {
            start();
        } else if (o == stop) {
            stop();
        } else if (o == play) {
            play();
        } else if (o == fourier) {
            fourier();
        }
    }

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        CaptureFrame frame = new CaptureFrame();
//        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }

}
