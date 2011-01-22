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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author benland100
 */
public class SoundView extends JPanel implements AdjustmentListener, ActionListener {

    public void adjustmentValueChanged(AdjustmentEvent e) {
        scroll.setVisibleAmount((int)(view.getWidth()/scale+0.5));
        view.recalc();
        view.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == in) {
            scale = Math.pow(2,Math.log(scale)/Math.log(2)+0.5);
        } else {
            scale = Math.pow(2,Math.log(scale)/Math.log(2)-0.5);
        }
        scroll.setVisibleAmount((int)Math.ceil(getWidth()/scale));
        view.recalc();
        repaint();
    }

    private class SampleView extends JPanel implements MouseMotionListener, MouseListener {

        private int[] dots;
        private int sample;
        private int press;

        public SampleView() {
            super();
            recalc();
            addMouseMotionListener(this);
            addMouseListener(this);
        }

        public void recalc() {
            int w = getWidth(), h = getHeight();
            int start = scroll.getValue();
            int last = Math.min((int)(start + w/scale + 1.5), samples.length);
            dots = new int[(last - start)*2];
            for (int i = 0; i < dots.length; i+=2) {
                dots[i+0] = (int)(i/2*scale + 0.5);
                dots[i+1] = (int)(h/2d + (double)samples[i/2+start]/2147483647.5*h + 0.5);
            }
        }

        public void doLayout() {
            recalc();
        }

        public void paint(Graphics g) {
            int w = getWidth(), h = getHeight();
            g.setColor(Color.BLACK);
            g.fillRect(0,0,w,h);
            try {
                int a = (left-scroll.getValue())*2;
                int b = (right-scroll.getValue())*2;
                g.setColor(Color.DARK_GRAY);
                if (a < 0 && b > dots.length) {
                    g.fillRect(0, 0, w, h);
                } else if (a < 0) {
                    g.fillRect(0, 0, dots[b], h);
                } else if (b > dots.length) {
                    g.fillRect(dots[a], 0, w-dots[a], h);
                } else {
                    g.fillRect(dots[a], 0, dots[b]-dots[a], h);
                }
            } catch (Exception e) {}
            g.setColor(Color.GREEN);
            for (int i = 2; i < dots.length; i+=2) {
                g.drawLine(dots[i-2], dots[i-1], dots[i+0], dots[i+1]);
            }
            try {
                int i = (sample-scroll.getValue())*2;
                g.setColor(Color.GRAY);
                g.drawLine(dots[i], 0, dots[i], h);
                g.setColor(Color.RED);
                g.fillOval(dots[i+0]-2, dots[i+1]-2, 4, 4);
                g.setColor(Color.MAGENTA);
                DecimalFormat onedec = new DecimalFormat("#0.0");
                g.drawString("Sample: " + sample + " Amplitude: " + onedec.format(samples[sample]/2147483647.5*200), 5, 15);
            } catch (Exception e) {} 
        }

        private int sampleFromX(int x) {
            return (int)(scroll.getValue() + x / scale + 0.5);
        }

        public void mouseDragged(MouseEvent e) {
            sample = Math.min(samples.length-1, sampleFromX(e.getX()));
            if (press > sample) {
                left = sample;
                right = press;
            } else {
                right = sample;
                left = press;
            }
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            sample = Math.min(samples.length-1, sampleFromX(e.getX()));
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            press = Math.min(samples.length-1, sampleFromX(e.getX()));
            left = -1;
            right = -1;
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            sample = -1;
            repaint();
        }

    }

    private int[] samples;
    private int left,right;
    private double scale;
    private JScrollBar scroll;
    private SampleView view;
    private JButton in,out;

    public SoundView() {
        super(new BorderLayout());
        samples = new int[0];
        scale = 1D;
        scroll = new JScrollBar(JScrollBar.HORIZONTAL,0,0,0,0);
        scroll.addAdjustmentListener(this);
        view = new SampleView();
        add(view,BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(scroll,BorderLayout.CENTER);
        JPanel southeast = new JPanel(new GridLayout(1,2));
        in = new JButton("+");
        in.addActionListener(this);
        southeast.add(in);
        out = new JButton("-");
        out.addActionListener(this);
        southeast.add(out);
        south.add(southeast,BorderLayout.EAST);
        add(south,BorderLayout.SOUTH);
    }

    public int[] getSelection() {
        int[] res;
        if (right - left < 1) {
            res = (int[]) Arrays.copyOf(samples, samples.length);
        } else {
            res = new int[right-left+1];
            System.arraycopy(samples, left, res, 0, right - left + 1);
        }
        return res;
    }

    public void setSamples(int[] samples) {
        this.samples = samples;
        scroll.setMaximum(samples.length);
        scroll.setValue(0);
        scroll.setVisibleAmount((int)(view.getWidth()/scale+0.5));
        left = -1;
        right = -1;
        view.recalc();
        repaint();
    }

    public void setScale(double scale) {
        this.scale = scale;
        view.recalc();
        view.repaint();
    }

}
