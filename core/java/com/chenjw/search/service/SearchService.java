package com.chenjw.search.service;

import java.util.List;

import com.chenjw.search.model.SearchHit;

public interface SearchService {
    
    public List<SearchHit> search(String word);
    
    public List<String> suggest(String word);
   
}
