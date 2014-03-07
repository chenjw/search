package com.chenjw.search.ui;

import java.util.List;

public interface DataHandler {

    public String getSearchWord();


    public void setSuggest(List<String> suggest);

    public void appendResult(String text);

    public void clearResult();
}