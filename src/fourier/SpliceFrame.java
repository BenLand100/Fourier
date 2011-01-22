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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 *
 * @author benland100
 */
public class SpliceFrame extends JFrame {

    private static final String[] MIX_STYLES = new String[] { "Fade In and Out", "Fade In Only", "Fade Out Only", "No Fade: Only Combine" };
    private static final String[] COLUMN_NAMES = new String[] { "Frequency", "Phase", "Amplitude", "Active" };

    private JSplitPane split;

    private JScrollPane list_scroll;
    private JList list;
    private JButton capture, remove, play;

    private JScrollPane data_scroll;
    private JTable data;
    private JButton add_data, rem_data;
    private JSlider freq, amp, phase;
    private JSlider volume, init_overlap, duration, final_overlap;
    private JComboBox mix_style;

    public SpliceFrame() {
        super("Sound Splicer");
        
        setLayout(new BorderLayout());
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Available Sound Prints",JLabel.CENTER),BorderLayout.NORTH);
        list = new JList();
        list_scroll = new JScrollPane(list);
        left.add(list_scroll,BorderLayout.CENTER);
        JPanel south_east = new JPanel(new GridLayout());
        capture = new JButton("Capture");
        south_east.add(capture);
        remove = new JButton("Remove");
        south_east.add(remove);
        play = new JButton("Play");
        south_east.add(play);
        left.add(south_east,BorderLayout.SOUTH);
        split.setLeftComponent(left);
        JPanel right = new JPanel(new BorderLayout());
        JPanel center_right = new JPanel(new BorderLayout());
        data = new JTable(new Object[0][0],COLUMN_NAMES);
        data_scroll = new JScrollPane(data);
        data.setFillsViewportHeight(true);
        center_right.add(data_scroll,BorderLayout.CENTER);
        JPanel south_center_right = new JPanel(new GridLayout());
        add_data = new JButton("Add New Data");
        south_center_right.add(add_data);
        rem_data = new JButton("Remove Selected Data");
        south_center_right.add(rem_data);
        center_right.add(south_center_right,BorderLayout.SOUTH);
        JPanel east_center_right = new JPanel(new BorderLayout());
        JPanel north_east_center_right = new JPanel(new GridLayout());
        JPanel south_east_center_right = new JPanel(new GridLayout());
        north_east_center_right.add(new JLabel("F",JLabel.CENTER));
        freq = new JSlider(JSlider.VERTICAL);
        south_east_center_right.add(freq);
        north_east_center_right.add(new JLabel("P",JLabel.CENTER));
        phase = new JSlider(JSlider.VERTICAL);
        south_east_center_right.add(phase);
        north_east_center_right.add(new JLabel("A",JLabel.CENTER));
        amp = new JSlider(JSlider.VERTICAL);
        south_east_center_right.add(amp);
        east_center_right.add(north_east_center_right,BorderLayout.NORTH);
        east_center_right.add(south_east_center_right,BorderLayout.CENTER);
        center_right.add(east_center_right,BorderLayout.EAST);
        right.add(center_right);
        JPanel south_right = new JPanel(new BorderLayout());
        JPanel left_south_right = new JPanel(new GridLayout(5,1));
        JPanel right_south_right = new JPanel(new GridLayout(5,1));
        left_south_right.add(new JLabel("Volume"));
        volume = new JSlider(JSlider.HORIZONTAL);
        right_south_right.add(volume);
        left_south_right.add(new JLabel("Durration"));
        duration = new JSlider(JSlider.HORIZONTAL);
        right_south_right.add(duration);
        left_south_right.add(new JLabel("In Length"));
        init_overlap = new JSlider();
        right_south_right.add(init_overlap);
        left_south_right.add(new JLabel("Out Length"));
        final_overlap = new JSlider();
        right_south_right.add(final_overlap);
        left_south_right.add(new JLabel("Mix Style"));
        mix_style = new JComboBox(MIX_STYLES);
        right_south_right.add(mix_style);
        south_right.add(left_south_right, BorderLayout.WEST);
        south_right.add(right_south_right, BorderLayout.CENTER);
        right.add(south_right,BorderLayout.SOUTH);
        split.setRightComponent(right);
        split.setDividerLocation(0.2);
        add(split);
        setSize(750,400);
    }
    
    public static void main(String[] args) {
        SpliceFrame frame = new SpliceFrame();
        frame.setVisible(true);
    }


}
