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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author benland100
 */
public class FourierView extends JPanel implements AdjustmentListener, ActionListener {

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


    private class FreqView extends JPanel implements MouseMotionListener, MouseListener {

        private int[] dots;
        private int idx;

        public FreqView() {
            super();
            recalc();
            addMouseMotionListener(this);
            addMouseListener(this);
        }

        public void recalc() {
            int w = getWidth(), h = getHeight();
            int start = scroll.getValue();
            int last = Math.min((int)(start + w/scale + 1.5), freqs.length);
            dots = new int[(last - start)*2];
            for (int i = 0; i < dots.length; i+=2) {
                dots[i+0] = (int)(i/2*scale + 0.5);
                dots[i+1] =  h - (int)((h-30)*amps[start+i/2]);
            }
        }

        public void doLayout() {
            recalc();
        }

        public void paint(Graphics g) {
            int w = getWidth(), h = getHeight();
            g.setColor(Color.BLACK);
            g.fillRect(0,0,w,h);
            g.setColor(Color.GREEN);
            int rect_w = Math.max((int)Math.ceil(scale),1);
            for (int i = 0; i < dots.length; i+=2) {
                g.fillRect(dots[i+0], dots[i+1], rect_w,h);
            }
            try {
                int i = (idx-scroll.getValue())*2;
                g.setColor(Color.GRAY);
                g.fillRect(dots[i+0], 0, rect_w,h);
                g.setColor(Color.RED);
                g.fillRect(dots[i+0], dots[i+1], rect_w,h);
                g.setColor(Color.MAGENTA);
                DecimalFormat onedec = new DecimalFormat("#0.0");
                DecimalFormat fourdec = new DecimalFormat("#0.0000");
                g.drawString("Freq: " + onedec.format(freqs[idx]) + " Amplitude: " + onedec.format(amps[idx]*100) + " Phase: " + fourdec.format(phases[idx]), 5, 15);
            } catch (Exception e) {}
        }

        private int idxFromX(int x) {
            return (int)(scroll.getValue() + x / scale + 0.5);
        }

        public void mouseDragged(MouseEvent e) {
            idx = Math.min(freqs.length-1, idxFromX(e.getX()));
        }

        public void mouseMoved(MouseEvent e) {
            idx = Math.min(freqs.length-1, idxFromX(e.getX()));
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            idx = -1;
            repaint();
        }

    }

    private double[] amps,freqs,phases;
    private double scale;
    private JScrollBar scroll;
    private FreqView view;
    private JButton in,out;

    public FourierView() {
        super(new BorderLayout());
        amps = new double[0];
        freqs = new double[0];
        phases = new double[0];
        scroll = new JScrollBar(JScrollBar.HORIZONTAL,0,0,0,0);
        scroll.addAdjustmentListener(this);
        view = new FreqView();
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

    public void setFourierData(double[] freqs, double[] amps, double[] phases) {
        this.freqs = freqs;
        this.amps = amps;
        this.phases = phases;
        scale = 1D;
        scroll.setValue(0);
        scroll.setMaximum(freqs.length);
        scroll.setVisibleAmount((int)(view.getWidth()/scale+0.5));
        view.recalc();
        repaint();
    }

}
