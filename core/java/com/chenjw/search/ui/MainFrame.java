/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.chenjw.search.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.chenjw.search.service.SearchManager;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MainFrame extends JFrame {

    /**  */
    private static final long serialVersionUID = 1810483835021058608L;
    private JScrollPane       resultScrollPane;
    private JButton           searchButton;
    private JLabel            jLabel1;

    private JComboBox         searchWordComboBox;
    private JTextArea         resultPane;
    private JPanel            configPanel      = new JPanel();
    private SearchManager     searchManager;
    // 用于异步执行任务
    private ExecutorService   executeService   = Executors.newSingleThreadExecutor();
    private DataHandler       dataHandler;

    private void initSpring() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
            "classpath*:search-tool.xml");
        searchManager = (SearchManager) ctx.getBean("searchManager");
        System.out.println("spring inited!");
        resultPane.append("启动成功！\n");
        // 

        //
        dataHandler = new DataHandler() {

            @Override
            public void appendResult(String text) {
                resultPane.append(text);
                resultPane.setCaretPosition(resultPane.getText().length());
            }

            @Override
            public void clearResult() {
                resultPane.setText(null);
            }

            @Override
            public String getSearchWord() {     
                return (String)searchWordComboBox.getEditor().getItem();
            }

            @Override
            public void setSuggest(List<String> suggest) {
                suggest.add(0, (String)searchWordComboBox.getEditor().getItem());
                ComboBoxModel historyComboBox1Model = new DefaultComboBoxModel(
                    suggest.toArray(new String[suggest.size()]));
                searchWordComboBox.setModel(historyComboBox1Model);
                searchWordComboBox.setPopupVisible(true);
            }

        };

    }

    public MainFrame() {
        {
            this.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }

            });
        }

        GroupLayout configPanelLayout = new GroupLayout((JComponent) configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanel.setPreferredSize(new java.awt.Dimension(1035, 726));
        {
            searchButton = new JButton();
            searchButton.setText("搜索");
            searchButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    executeService.execute(new Runnable() {
                        @Override
                        public void run() {
                            searchManager.search(dataHandler);
                        }
                    });

                }

            });
            searchButton.registerKeyboardAction(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    executeService.execute(new Runnable() {
                        @Override
                        public void run() {
                            searchManager.search(dataHandler);
                        }
                    });
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
        {
            jLabel1 = new JLabel();
            jLabel1.setText("搜索关键词");
        }
        {
            resultScrollPane = new JScrollPane(resultPane);
            {
                resultPane = new JTextArea();
                resultScrollPane.setViewportView(resultPane);
            }
        }

        {
            searchWordComboBox = new JComboBox();
            searchWordComboBox.setEditable(true);
            Component editorComponent = searchWordComboBox.getEditor().getEditorComponent();
            editorComponent.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent evt) {
                    System.out.println(3333);
                    searchManager.suggest(dataHandler);
                }
            });

        }
        configPanelLayout.setVerticalGroup(configPanelLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                configPanelLayout
                    .createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, GroupLayout.Alignment.BASELINE,
                        GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchWordComboBox, GroupLayout.Alignment.BASELINE,
                        GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton, GroupLayout.Alignment.BASELINE,
                        GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(resultScrollPane, GroupLayout.PREFERRED_SIZE, 686,
                GroupLayout.PREFERRED_SIZE).addGap(0, 6, Short.MAX_VALUE));
        configPanelLayout.setHorizontalGroup(configPanelLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                configPanelLayout
                    .createParallelGroup()
                    .addGroup(
                        configPanelLayout
                            .createSequentialGroup()
                            .addComponent(resultScrollPane, GroupLayout.PREFERRED_SIZE, 979,
                                GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(
                        GroupLayout.Alignment.LEADING,
                        configPanelLayout
                            .createSequentialGroup()
                            .addPreferredGap(resultScrollPane, jLabel1,
                                LayoutStyle.ComponentPlacement.INDENT)
                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 75,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(searchWordComboBox, 0, 518, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 162,
                                GroupLayout.PREFERRED_SIZE).addGap(187))).addContainerGap());
        //添加其他组件
        pack();
        this.setSize(1010, 768);
        setVisible(true);
        this.setTitle("搜索");
        getContentPane().add(configPanel, BorderLayout.CENTER);
        initSpring();
    }
}
