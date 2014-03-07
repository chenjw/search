package com.chenjw.search.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.chenjw.search.model.SearchHit;
import com.chenjw.search.service.SearchManager;
import com.chenjw.search.service.SearchService;
import com.chenjw.search.ui.DataHandler;

public class SearchManagerImpl implements SearchManager {
    private SearchService searchService;

    public void search(DataHandler dataHandler) {
        List<SearchHit> r = searchService.search(dataHandler.getSearchWord());
        dataHandler.appendResult(JSON.toJSONString(r, true) + "\n");
    }

    @Override
    public void suggest(DataHandler dataHandler) {

    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

}
