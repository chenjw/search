/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.chenjw.search.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.swing.JComboBox;

import org.apache.commons.lang.StringUtils;

/**
 * 带历史记录选择的输入框
 * 
 * @author junwen.chenjw
 * @version $Id: HistoryComboBox.java, v 0.1 2013年10月16日 下午3:58:44 junwen.chenjw Exp $
 */
public class HistoryComboBox extends JComboBox {

    /**  */
    private static final long serialVersionUID = 7342202089111844143L;

    private Set<String>       items            = new HashSet<String>();
    private Queue<String>     itemQueue        = new LinkedList<String>();
    // 历史记录的最大保存数量
    private static final int  MAX_SIZE         = 10;
    private String            name;
    private String            value;

    public HistoryComboBox() {

    }

    public HistoryComboBox(String name) {
        this.name = name;
        this.setEditable(true);
        this.setSelectedItem(UserConfig.INSTANCE.get(name));
    }

    public String getText() {
        String text = (String) getModel().getSelectedItem();
        if (!items.contains(text)) {
            items.add(text);
            itemQueue.add(text);
            if (itemQueue.size() > MAX_SIZE) {
                items.remove(itemQueue.poll());
            }
            this.removeAllItems();
            for (String item : itemQueue) {
                this.addItem(item);
            }
            this.setSelectedItem(text);

        }
        if (!StringUtils.equals(value, text)) {
            UserConfig.INSTANCE.put(name, text);
            UserConfig.INSTANCE.save();
            value = text;
        }
        return text;
    }
}
