/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.context;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by conforti on 17/09/15.
 */
public class FakeProMPropertiesPanel extends ProMHeaderPanel {
    private static final long serialVersionUID = 1L;
    private boolean first = true;
    private final JPanel properties = new ProMScrollablePanel();

    public FakeProMPropertiesPanel(String title) {
        super(title);
        this.properties.setOpaque(false);
        this.properties.setLayout(new BoxLayout(this.properties, 1));
        ProMScrollPane scrollPane = new ProMScrollPane(this.properties);
        scrollPane.setVerticalScrollBarPolicy(20);
        scrollPane.setHorizontalScrollBarPolicy(31);
        this.add(scrollPane);
    }

    public JCheckBox addCheckBox(String name) {
        return this.addCheckBox(name, false);
    }

    public JCheckBox addCheckBox(String name, boolean value) {
        JCheckBox checkBox = SlickerFactory.instance().createCheckBox(null, value);
        return this.addProperty(name, checkBox);
    }

    public <E> ProMComboBox<E> addComboBox(String name, E[] values) {
        return (ProMComboBox)this.addProperty(name, new ProMComboBox(values));
    }

    public <E> ProMComboBox<E> addComboBox(String name, Iterable<E> values) {
        return (ProMComboBox)this.addProperty(name, new ProMComboBox(values));
    }

    public <T extends JComponent> T addProperty(String name, T component) {
        if(!this.first) {
            this.properties.add(Box.createVerticalStrut(3));
        } else {
            this.first = false;
        }

        this.properties.add(this.packInfo(name, component));
        return component;
    }

    public ProMTextField addTextField(String name) {
        return this.addTextField(name, "");
    }

    public ProMTextField addTextField(String name, String value) {
        ProMTextField component = new ProMTextField();
        component.setText(value);
        return this.addProperty(name, component);
    }

    private Component findComponent(Component component) {
        if(component instanceof AbstractButton) {
            return component;
        } else if(component instanceof JTextComponent) {
            return component;
        } else {
            if(component instanceof Container) {
                Component[] arr$ = ((Container)component).getComponents();
                int len$ = arr$.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Component child = arr$[i$];
                    Component result = this.findComponent(child);
                    if(result != null) {
                        return result;
                    }
                }
            }

            return null;
        }
    }

    private void installHighlighter(Component component, final RoundedPanel target) {
        component.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
            }

            public void mouseEntered(MouseEvent arg0) {
                target.setBackground(new Color(60, 60, 60, 240));
                target.repaint();
            }

            public void mouseExited(MouseEvent arg0) {
                target.setBackground(new Color(60, 60, 60, 160));
                target.repaint();
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
        if(component instanceof Container) {
            Component[] arr$ = ((Container)component).getComponents();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Component child = arr$[i$];
                this.installHighlighter(child, target);
            }
        }

    }

    protected RoundedPanel packInfo(String name, JComponent component) {
        final RoundedPanel packed = new RoundedPanel(10, 0, 0);
        packed.setBackground(new Color(60, 60, 60, 160));
        final Component actualComponent = this.findComponent(component);
        packed.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                if(actualComponent != null) {
                    if(actualComponent instanceof AbstractButton) {
                        AbstractButton text = (AbstractButton)actualComponent;
                        text.doClick();
                    }

                    if(actualComponent instanceof JTextComponent) {
                        JTextComponent text1 = (JTextComponent)actualComponent;
                        if(text1.isEnabled() && text1.isEditable()) {
                            text1.selectAll();
                        }

                        text1.grabFocus();
                    }
                }

            }

            public void mouseEntered(MouseEvent arg0) {
                packed.setBackground(new Color(60, 60, 60, 240));
                packed.repaint();
            }

            public void mouseExited(MouseEvent arg0) {
                packed.setBackground(new Color(60, 60, 60, 160));
                packed.repaint();
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
        this.installHighlighter(component, packed);
        packed.setLayout(new BoxLayout(packed, 0));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setOpaque(false);
        nameLabel.setForeground(WidgetColors.TEXT_COLOR);
        nameLabel.setFont(nameLabel.getFont().deriveFont(12.0F));
        nameLabel.setMinimumSize(new Dimension(150, 20));
        packed.add(Box.createHorizontalStrut(5));
        packed.add(nameLabel);
        packed.add(Box.createHorizontalGlue());
        packed.add(component);
        packed.add(Box.createHorizontalStrut(5));
        packed.revalidate();
        return packed;
    }
}
