package com.fh.bcoin.model;

import java.io.Serializable;

public class CoinContractsModel implements Serializable{
    
    private String name;
    private String contracts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContracts() {
        return contracts;
    }

    public void setContracts(String contracts) {
        this.contracts = contracts;
    }
}
